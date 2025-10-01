/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.internal.utils.thread.RpcInternalThreadLocal
import kotlinx.rpc.krpc.internal.KrpcCallMessage
import kotlinx.rpc.krpc.internal.decodeMessageData
import kotlinx.rpc.krpc.internal.deserialize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat

internal class ServerStreamContext {
    private val currentCallId = RpcInternalThreadLocal<String>()

    fun <T> scoped(callId: String, body: () -> T): T {
        try {
            currentCallId.set(callId)
            return body()
        } finally {
            currentCallId.remove()
        }
    }

    private val streams = RpcInternalConcurrentHashMap<String, RpcInternalConcurrentHashMap<String, StreamCall>>()

    suspend fun send(message: KrpcCallMessage.StreamMessage, serialFormat: SerialFormat) {
        val call = streams[message.callId]?.get(message.streamId) ?: return
        val data = scoped(message.streamId) {
            decodeMessageData(serialFormat, call.elementSerializer, message)
        }
        call.channel.send(data)
    }

    suspend fun cancelStream(message: KrpcCallMessage.StreamCancel) {
        streams[message.callId]?.get(message.streamId)?.channel?.send(StreamCancel(message.cause.deserialize()))
    }

    suspend fun closeStream(message: KrpcCallMessage.StreamFinished) {
        streams[message.callId]?.get(message.streamId)?.channel?.send(StreamEnd)
    }

    fun removeCall(callId: String, cause: Throwable?) {
        streams.remove(callId)?.values?.forEach {
            it.channel.close(cause)
            it.channel.cancel(cause?.let { e -> e as? CancellationException ?: CancellationException(null, e) })
        }
    }

    fun prepareClientStream(streamId: String, elementKind: KSerializer<Any?>): Flow<Any?> {
        val callId = currentCallId.get() ?: error("No call id")

        val channel = Channel<Any?>()

        @Suppress("UNCHECKED_CAST")
        val map = streams.computeIfAbsent(callId) { RpcInternalConcurrentHashMap() }
        map[streamId] = StreamCall(callId, streamId, channel, elementKind)

        fun onClose() {
            channel.cancel()
            map.remove(streamId)
        }

        val collected = AtomicBoolean(false)

        val flow = flow {
            check(collected.value.compareAndSet(expect = false, update = true)) {
                "Request streams can only be collected once"
            }

            for (message in channel) {
                when (message) {
                    is StreamCancel -> {
                        onClose()
                        throw message.cause ?: streamCanceled()
                    }

                    is StreamEnd -> {
                        onClose()

                        return@flow
                    }

                    else -> {
                        emit(message)
                    }
                }
            }
        }

        return flow
    }

    private fun streamCanceled(): Throwable = NoSuchElementException("Stream canceled")
}

private class AtomicBoolean(initialValue: Boolean) {
    val value = atomic(initialValue)
}

private data class StreamCancel(val cause: Throwable? = null)
private data object StreamEnd
