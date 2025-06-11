/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.internal.utils.thread.RpcInternalThreadLocal
import kotlinx.serialization.KSerializer

internal class ClientStreamContext(private val connectionId: Long?) {
    val streams = RpcInternalConcurrentHashMap<String, List<StreamCall>>()

    private val currentCallId = RpcInternalThreadLocal<String>()
    private val currentServiceId = RpcInternalThreadLocal<Long>()

    fun <T> scoped(callId: String, serviceId: Long, body: () -> T): T {
        try {
            currentCallId.set(callId)
            currentServiceId.set(serviceId)
            return body()
        } finally {
            currentCallId.remove()
            currentServiceId.remove()
        }
    }

    private val streamIdCounter = atomic(0L)

    fun registerClientStream(value: Flow<*>, elementKind: KSerializer<*>): String {
        val callId = currentCallId.get() ?: error("No call id")
        val serviceId = currentServiceId.get() ?: error("No service id")
        val streamId = "$STREAM_ID_PREFIX${streamIdCounter.getAndIncrement()}"

        @Suppress("UNCHECKED_CAST")
        val stream = StreamCall(
            callId = callId,
            streamId = streamId,
            stream = value,
            elementSerializer = elementKind as KSerializer<Any?>,
            connectionId = connectionId,
            serviceId = serviceId
        )

        @Suppress("UNCHECKED_CAST")
        streams.merge(callId, listOf(stream)) { old, new -> old + new }

        return streamId
    }

    private companion object {
        private const val STREAM_ID_PREFIX = "stream:"
    }
}
