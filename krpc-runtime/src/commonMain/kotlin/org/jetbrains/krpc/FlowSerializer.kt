package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class FlowSerializer(
    private val context: RPCCallContext,
    private val elementType: KSerializer<Any>
) : KSerializer<Flow<*>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "FlowIdentifier",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Flow<*> {
        val flowId = decoder.decodeString()
        return context.prepareFlow(flowId, elementType)
    }

    override fun serialize(encoder: Encoder, value: Flow<*>) {
        val id = context.registerFlow(value, elementType)
        encoder.encodeString(id)
    }
}
