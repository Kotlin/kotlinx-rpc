@file:OptIn(ExperimentalSerializationApi::class)

package org.jetbrains.krpc.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoBufBuilder

internal object RPCProtobufSerialFormat : RPCSerialFormat<ProtoBuf, ProtoBufBuilder> {
    override fun withBuilder(from: ProtoBuf?, builderConsumer: ProtoBufBuilder.() -> Unit): ProtoBuf {
        return ProtoBuf(from ?: ProtoBuf.Default) { builderConsumer() }
    }

    override fun ProtoBufBuilder.applySerializersModule(serializersModule: SerializersModule) {
        this.serializersModule += serializersModule
    }
}

fun RPCSerialFormatConfiguration.protobuf(
    from: ProtoBuf = ProtoBuf.Default,
    builderConsumer: ProtoBufBuilder.() -> Unit = {},
) {
    registerBinary(RPCSerialFormatInitializer(RPCProtobufSerialFormat, from, builderConsumer))
}
