/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server.internal

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.krpc.internal.KrpcCallMessage
import kotlinx.rpc.krpc.internal.decodeMessageData
import kotlinx.rpc.krpc.internal.deserialize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlin.native.concurrent.ThreadLocal

internal class ServerStreamContext {
    @ThreadLocal
    private var currentCallId: String? = null

    fun <T> scoped(callId: String, body: () -> T): T {
        try {
            currentCallId = callId
            return body()
        } finally {
            currentCallId = null
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
        }
    }

    fun prepareClientStream(streamId: String, elementKind: KSerializer<Any?>): Flow<Any?> {
        val callId = currentCallId ?: error("No call id")

        val channel = Channel<Any?>()

        @Suppress("UNCHECKED_CAST")
        val map = streams.computeIfAbsent(callId) { RpcInternalConcurrentHashMap() }
        map[streamId] = StreamCall(callId, streamId, channel, elementKind)

        fun onClose() {
            channel.cancel()
            map.remove(streamId)
        }

        val flow = flow {
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

private data class StreamCancel(val cause: Throwable? = null)
private data object StreamEnd
