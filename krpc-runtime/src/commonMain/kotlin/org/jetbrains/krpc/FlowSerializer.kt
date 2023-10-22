package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class FlowSerializer(
    private val context: RPCFlowContext,
    private val elementType: KSerializer<Any?>,
) : KSerializer<Flow<*>> {
    private val stateFlowInitialValueSerializer by lazy {
        RPCStateFlowInitialValue.Serializer(elementType)
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FlowSerializer") {
        element("flowId", PrimitiveSerialDescriptor("FlowIdSerializer", PrimitiveKind.STRING))
        element("flowKind", FlowKind.serializer.descriptor)
        element("stateFlowInitialValue", stateFlowInitialValueSerializer.descriptor, isOptional = true)
    }

    override fun deserialize(decoder: Decoder): Flow<*> {
        val flowId = decoder.decodeString()
        val kindIndex = decoder.decodeEnum(FlowKind.serializer.descriptor)
        if (kindIndex !in FlowKind.entries.indices) {
            throw SerializationException(
                "$kindIndex is not among valid FlowKind enum values, values size is ${FlowKind.entries.size}"
            )
        }
        val flowKind = FlowKind.entries[kindIndex]
        @OptIn(ExperimentalSerializationApi::class)
        val stateValue = decoder.decodeNullableSerializableValue(stateFlowInitialValueSerializer)?.value

        return context.prepareFlow(flowId, flowKind, stateValue, elementType)
    }

    override fun serialize(encoder: Encoder, value: Flow<*>) {
        val id = context.registerFlow(value, elementType)
        val (kind, stateValue) = value.kindAndOptionalStateValue()

        encoder.encodeString(id)
        encoder.encodeEnum(FlowKind.serializer.descriptor, kind.ordinal)

        @OptIn(ExperimentalSerializationApi::class)
        encoder.encodeNullableSerializableValue(stateFlowInitialValueSerializer, stateValue)
    }
}

@Serializable
enum class FlowKind {
    Plain, Shared, State;

    companion object {
        val serializer by lazy { serializer() }
    }
}

private data class RPCStateFlowInitialValue(val value: Any?) {
    class Serializer(private val elementType: KSerializer<Any?>) : KSerializer<RPCStateFlowInitialValue> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StateFlowInitialValueSerializer") {
            element("value", elementType.descriptor)
        }

        override fun deserialize(decoder: Decoder): RPCStateFlowInitialValue {
            return RPCStateFlowInitialValue(decoder.decodeSerializableValue(elementType))
        }

        override fun serialize(encoder: Encoder, value: RPCStateFlowInitialValue) {
            encoder.encodeSerializableValue(elementType, value.value)
        }
    }
}

private fun Flow<*>.kindAndOptionalStateValue(): Pair<FlowKind, RPCStateFlowInitialValue?> = when (this) {
    is StateFlow<*> -> FlowKind.State to RPCStateFlowInitialValue(value)
    is SharedFlow<*> -> FlowKind.Shared to null
    else -> FlowKind.Plain to null
}
