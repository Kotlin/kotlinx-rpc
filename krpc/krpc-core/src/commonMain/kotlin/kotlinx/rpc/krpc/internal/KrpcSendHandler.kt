/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlin.coroutines.resume

private class Lock {
    val lock = atomic(false)

    inline fun <T> withLock(block: () -> T): T {
        while (true) {
            if (!lock.compareAndSet(expect = false, update = true)) {
                continue
            }

            return try {
                block()
            } finally {
                lock.compareAndSet(expect = true, update = false)
            }
        }
    }
}

internal class KrpcSendHandler(
    private val sendChannel: SendChannel<KrpcTransportMessage>,
) {
    private val closed = atomic(false)

    internal var window = -1
        private set

    /**
     * Could've been just `atomic`, but when we send the message, we need to:
     * - Check size
     * - Send Message
     * - Update window size if sent
     *
     * As a one atomic operation.
     *
     * We are an execution resume guarantee as [kotlinx.coroutines.channels.Channel.trySend] doesn't wait
     */
    private val windowLock = Lock()
    private val continuationsLock = Lock()

    // internal for tests
    @Suppress("PropertyName")
    internal val __continuations = mutableListOf<CancellableContinuation<Unit>>()

    fun updateWindowSize(update: Int) {
        if (closed.value) {
            return
        }

        windowLock.withLock {
            window = if (window == -1) {
                update
            } else {
                window + update
            }


            continuationsLock.withLock {
                __continuations.forEach { it.resume(Unit) }
                __continuations.clear()
            }
        }
    }

    suspend fun sendMessage(message: KrpcTransportMessage) {
        if (closed.value) {
            throw ClosedSendChannelException("KrpcSendHandler closed")
        }

        while (true) {
            val currentWindow = window
            if (currentWindow == -1) {
                sendChannel.trySend(message).onClosed {
                    throw it ?: ClosedSendChannelException("KrpcSendHandler closed")
                }

                break
            } else {
                if (currentWindow == 0) {
                    suspendCancellableCoroutine { cont ->
                        windowLock.withLock {
                            if (window > 0) {
                                cont.resume(Unit)
                                return@withLock
                            }

                            continuationsLock.withLock {
                                __continuations.add(cont)
                            }
                        }
                    }

                    continue
                }

                val sent = windowLock.withLock {
                    val currentWindow = window

                    if (currentWindow == 0) {
                        return@withLock false
                    }

                    if (closed.value) {
                        throw ClosedSendChannelException("KrpcSendHandler closed")
                    }

                    val result = sendChannel.trySend(message)
                        .onSuccess {
                            // don't resume other continuations, as we decrement
                            // no if check as we have lock
                            window = currentWindow - 1
                        }
                        .onClosed {
                            throw it ?: ClosedSendChannelException("KrpcSendHandler closed")
                        }

                    result.isSuccess
                }

                if (sent) {
                    break
                }
            }
        }
    }

    fun close(e: Throwable? = null) {
        if (closed.compareAndSet(expect = false, update = true)) {
            continuationsLock.withLock {
                __continuations.forEach { it.cancel(e) }
            }
        }
    }
}
