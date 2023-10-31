@file:OptIn(ExperimentalSerializationApi::class)

package org.jetbrains.krpc.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.cbor.CborBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

internal object RPCCborSerialFormat : RPCSerialFormat<Cbor, CborBuilder> {
    override fun withBuilder(from: Cbor?, builderConsumer: CborBuilder.() -> Unit): Cbor {
        return Cbor(from ?: Cbor.Default) { builderConsumer() }
    }

    override fun CborBuilder.applySerializersModule(serializersModule: SerializersModule) {
        this.serializersModule += serializersModule
    }
}

fun RPCSerialFormatConfiguration.cbor(from: Cbor = Cbor.Default, builderConsumer: CborBuilder.() -> Unit = {}) {
    registerBinary(RPCSerialFormatInitializer(RPCCborSerialFormat, from, builderConsumer))
}
