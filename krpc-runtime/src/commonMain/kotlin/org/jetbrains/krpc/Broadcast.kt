/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.serializeException

fun RPCTransport.broadcast(waitForService: Boolean = true): RPCTransport = multiclient(waitForService)

@OptIn(InternalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun RPCTransport.multiclient(waitForService: Boolean = true): RPCTransport {
    val logger = CommonLogger.initialized().logger("RPCTransport.multiclient")
    val mutex = Mutex()
    val receiver = this

    val subscribers = mutableSetOf<suspend (RPCMessage) -> Boolean>()
    val waiting = mutableSetOf<RPCMessage>()

    receiver.launch {
        val toRemove = mutableSetOf<suspend (RPCMessage) -> Boolean>()
        receiver.subscribe { message ->
            var done = false
            mutex.withLock {
                for (subscriber in subscribers) {
                    val result = runCatching {
                        subscriber(message)
                    }

                    if (result.isFailure) {
                        logger.error(result.exceptionOrNull()) { "Service failed" }
                        toRemove.add(subscriber)
                        continue
                    }

                    if (result.getOrNull() == true) {
                        done = true
                        break
                    }
                }

                if (!done) {
                    if (waitForService) {
                        waiting.add(message)
                        logger.warn {
                            "No registered service of ${message.serviceType} service type " +
                                    "was able to process message at the moment. Waiting for new services."
                        }
                        return@subscribe false
                    }

                    val cause = IllegalStateException(
                        "Failed to process call ${message.callId} for service ${message.serviceType}, " +
                                if (toRemove.isEmpty()) "no services found" else "${toRemove.size} attempts failed"
                    )

                    val callException = RPCMessage.CallException(
                        callId = message.callId,
                        serviceType = message.serviceType,
                        cause = serializeException(cause)
                    )
                    send(callException)
                }

                subscribers.removeAll(toRemove)
                toRemove.clear()
            }

            return@subscribe done
        }
    }

    return object : RPCTransport(receiver.coroutineContext) {

        init {
            GlobalScope.launch {
                val job = receiver.coroutineContext.job
                job.join()
                val cause = if (!job.isActive) job.getCancellationException() else null
                mutex.withLock {
                    if (cause == null) return@withLock
                    if (waiting.isEmpty()) return@withLock

                    logger.warn { "Cancelling service with unprocessed messages. Cause: $cause" }
                    for (call in waiting) {
                        val message = RPCMessage.CallException(call.callId, call.serviceType, serializeException(cause))
                        runCatching {
                            send(message)
                        }
                    }
                }
            }
        }

        override suspend fun send(message: RPCMessage) {
            receiver.send(message)
        }

        override suspend fun subscribe(block: suspend (RPCMessage) -> Boolean) {
            mutex.withLock {
                subscribers.add(block)
                processWaiters(block)
            }
        }

        private suspend fun processWaiters(block: suspend (RPCMessage) -> Boolean) {
            if (waiting.isEmpty()) return

            val iterator = waiting.iterator()
            while (iterator.hasNext()) {
                val message = iterator.next()
                if (block(message)) {
                    iterator.remove()
                }
            }
        }
    }
}
