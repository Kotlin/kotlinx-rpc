package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

sealed class FlowSerializer<FlowT : Flow<*>>(private val flowKind: FlowKind) : KSerializer<FlowT> {
    protected abstract val context: RPCFlowContext
    protected abstract val elementType: KSerializer<Any?>

    protected open fun ClassSerialDescriptorBuilder.descriptorExtension() { }

    protected open fun decodeStateFlowInitialValue(decoder: Decoder): Any? { return null }

    protected open fun encodeStateFlowInitialValue(encoder: Encoder, flow: FlowT) { }

    override val descriptor: SerialDescriptor by lazy {
        buildClassSerialDescriptor("FlowSerializer.${flowKind.name}") {
            element("flowId", PrimitiveSerialDescriptor("FlowIdSerializer", PrimitiveKind.STRING))
            descriptorExtension()
        }
    }

    override fun deserialize(decoder: Decoder): FlowT {
        val flowId = decoder.decodeString()

        val stateValue = decodeStateFlowInitialValue(decoder)

        @Suppress("UNCHECKED_CAST")
        return context.prepareFlow(flowId, flowKind, stateValue, elementType) as FlowT
    }

    override fun serialize(encoder: Encoder, value: FlowT) {
        val id = context.registerFlow(value, elementType)

        encoder.encodeString(id)

        encodeStateFlowInitialValue(encoder, value)
    }

    class Plain(
        override val context: RPCFlowContext,
        override val elementType: KSerializer<Any?>,
    ) : FlowSerializer<Flow<Any?>>(FlowKind.Plain)

    class Shared(
        override val context: RPCFlowContext,
        override val elementType: KSerializer<Any?>,
    ) : FlowSerializer<SharedFlow<Any?>>(FlowKind.Shared)

    class State(
        override val context: RPCFlowContext,
        override val elementType: KSerializer<Any?>,
    ) : FlowSerializer<StateFlow<Any?>>(FlowKind.State) {
        override fun ClassSerialDescriptorBuilder.descriptorExtension() {
            element("stateFlowInitialValue", elementType.descriptor, isOptional = true)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun decodeStateFlowInitialValue(decoder: Decoder): Any? {
            return decoder.decodeNullableSerializableValue(elementType)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun encodeStateFlowInitialValue(encoder: Encoder, flow: StateFlow<Any?>) {
            encoder.encodeNullableSerializableValue(elementType, flow.value)
        }
    }
}

enum class FlowKind {
    Plain, Shared, State;
}
