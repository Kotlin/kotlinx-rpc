/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSerializationApi::class)

package kotlinx.rpc.krpc.serialization.protobuf

import kotlinx.rpc.krpc.serialization.RPCSerialFormat
import kotlinx.rpc.krpc.serialization.RPCSerialFormatBuilder
import kotlinx.rpc.krpc.serialization.RPCSerialFormatConfiguration
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

/**
 * Extension function that allows to configure ProtoBuf kotlinx.rpc serial format
 * Usage:
 * ```kotlin
 * // this: RPCConfig
 * serialization {
 *     protobuf {
 *         // custom params
 *     }
 * }
 * ```
 */
public fun RPCSerialFormatConfiguration.protobuf(
    from: ProtoBuf = ProtoBuf.Default,
    builderConsumer: ProtoBufBuilder.() -> Unit = {},
) {
    register(RPCSerialFormatBuilder.Binary(RPCProtobufSerialFormat, from, builderConsumer))
}
