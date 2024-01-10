/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

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

/**
 * Extension function that allows to configure ProtoBuf kRPC serial format
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
fun RPCSerialFormatConfiguration.protobuf(
    from: ProtoBuf = ProtoBuf.Default,
    builderConsumer: ProtoBufBuilder.() -> Unit = {},
) {
    register(RPCSerialFormatBuilder.Binary(RPCProtobufSerialFormat, from, builderConsumer))
}
