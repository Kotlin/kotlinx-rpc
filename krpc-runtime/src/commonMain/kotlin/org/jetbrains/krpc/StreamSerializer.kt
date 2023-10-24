package org.jetbrains.krpc

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

sealed class StreamSerializer<StreamT : Any>(private val streamKind: StreamKind) : KSerializer<StreamT> {
    protected abstract val context: RPCStreamContext
    protected abstract val elementType: KSerializer<Any?>

    protected open fun ClassSerialDescriptorBuilder.descriptorExtension() { }

    protected open fun decodeStateFlowInitialValue(decoder: Decoder): Any? { return null }

    protected open fun encodeStateFlowInitialValue(encoder: Encoder, flow: StreamT) { }

    override val descriptor: SerialDescriptor by lazy {
        buildClassSerialDescriptor("StreamSerializer.${streamKind.name}") {
            element("streamId", PrimitiveSerialDescriptor("StreamIdSerializer", PrimitiveKind.STRING))
            descriptorExtension()
        }
    }

    override fun deserialize(decoder: Decoder): StreamT {
        val flowId = decoder.decodeString()

        val stateFlowValue = decodeStateFlowInitialValue(decoder)

        return context.prepareStream(flowId, streamKind, stateFlowValue, elementType) as StreamT
    }

    override fun serialize(encoder: Encoder, value: StreamT) {
        val id = context.registerStream(value, streamKind, elementType)

        encoder.encodeString(id)

        encodeStateFlowInitialValue(encoder, value)
    }

    class Flow(
        override val context: RPCStreamContext,
        override val elementType: KSerializer<Any?>,
    ) : StreamSerializer<kotlinx.coroutines.flow.Flow<Any?>>(StreamKind.Flow)

    class SharedFlow(
        override val context: RPCStreamContext,
        override val elementType: KSerializer<Any?>,
    ) : StreamSerializer<kotlinx.coroutines.flow.SharedFlow<Any?>>(StreamKind.SharedFlow)

    class StateFlow(
        override val context: RPCStreamContext,
        override val elementType: KSerializer<Any?>,
    ) : StreamSerializer<kotlinx.coroutines.flow.StateFlow<Any?>>(StreamKind.StateFlow) {
        override fun ClassSerialDescriptorBuilder.descriptorExtension() {
            element("stateFlowInitialValue", elementType.descriptor, isOptional = true)
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

enum class StreamKind {
    Flow, SharedFlow, StateFlow;
}
