/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal sealed class StreamSerializer<StreamT : Any>(private val streamKind: StreamKind) : KSerializer<StreamT> {
    companion object {
        private const val STREAM_SERIALIZER_NAME_PREFIX = "StreamSerializer"

        private const val STREAM_ID_SERIAL_NAME = "streamId"
        private const val STREAM_ID_SERIALIZER_NAME = "StreamIdSerializer"

        private const val STATE_FLOW_INITIAL_VALUE_SERIAL_NAME = "stateFlowInitialValue"
    }

    protected abstract val context: KrpcStreamContext
    protected abstract val elementType: KSerializer<Any?>

    protected open fun ClassSerialDescriptorBuilder.descriptorExtension() { }

    protected open fun decodeStateFlowInitialValue(decoder: Decoder): Any? { return null }

    protected open fun encodeStateFlowInitialValue(encoder: Encoder, flow: StreamT) { }

    override val descriptor: SerialDescriptor by lazy {
        buildClassSerialDescriptor("$STREAM_SERIALIZER_NAME_PREFIX.${streamKind.name}") {
            element(STREAM_ID_SERIAL_NAME, PrimitiveSerialDescriptor(STREAM_ID_SERIALIZER_NAME, PrimitiveKind.STRING))
            descriptorExtension()
        }
    }

    override fun deserialize(decoder: Decoder): StreamT {
        val streamId = decoder.decodeString()

        val stateFlowValue = decodeStateFlowInitialValue(decoder)

        return context.prepareIncomingStream(streamId, streamKind, stateFlowValue, elementType) as StreamT
    }

    override fun serialize(encoder: Encoder, value: StreamT) {
        val id = context.registerOutgoingStream(value, streamKind, elementType)

        encoder.encodeString(id)

        encodeStateFlowInitialValue(encoder, value)
    }

    class Flow(
        override val context: KrpcStreamContext,
        override val elementType: KSerializer<Any?>,
    ) : StreamSerializer<kotlinx.coroutines.flow.Flow<Any?>>(StreamKind.Flow)

    class SharedFlow(
        override val context: KrpcStreamContext,
        override val elementType: KSerializer<Any?>,
    ) : StreamSerializer<kotlinx.coroutines.flow.SharedFlow<Any?>>(StreamKind.SharedFlow)

    class StateFlow(
        override val context: KrpcStreamContext,
        override val elementType: KSerializer<Any?>,
    ) : StreamSerializer<kotlinx.coroutines.flow.StateFlow<Any?>>(StreamKind.StateFlow) {
        override fun ClassSerialDescriptorBuilder.descriptorExtension() {
            element(STATE_FLOW_INITIAL_VALUE_SERIAL_NAME, elementType.descriptor, isOptional = true)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun decodeStateFlowInitialValue(decoder: Decoder): Any? {
            return decoder.decodeNullableSerializableValue(elementType)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun encodeStateFlowInitialValue(encoder: Encoder, flow: kotlinx.coroutines.flow.StateFlow<Any?>) {
            encoder.encodeNullableSerializableValue(elementType, flow.value)
        }
    }
}

internal enum class StreamKind {
    Flow, SharedFlow, StateFlow;
}
