package org.jetbrains.krpc

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.krpc.internal.InternalKRPCApi

fun RPCTransport.broadcast(waitForService: Boolean = true): RPCTransport = multiclient(waitForService)

@OptIn(InternalKRPCApi::class, InternalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun RPCTransport.multiclient(waitForService: Boolean = true): RPCTransport {
    val logger = KotlinLogging.logger("RPCTransport.multiclient")
    val mutex = Mutex()
    val receiver = this

    val subscribers = mutableSetOf<suspend (RPCMessage) -> Boolean>()
    val waiting = mutableSetOf<RPCMessage>()

    receiver.launch {
        val toRemove = mutableSetOf<suspend (RPCMessage) -> Boolean>()
        receiver.subscribe {
            var done = false
            mutex.withLock {
                for (subscriber in subscribers) {
                    val result = runCatching {
                        subscriber(it)
                    }

                    if (result.isFailure) {
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
                        waiting.add(it)
                        logger.warn { "No service registered for ${it.serviceType} at the moment. Waiting for new services." }
                        return@subscribe false
                    }

                    val cause = IllegalStateException("No service found for ${it.serviceType} call ${it.callId}")
                    val message = RPCMessage.CallException(it.callId, it.serviceType, serializeException(cause))
                    send(message)
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
