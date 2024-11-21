/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSerializationApi::class)

package kotlinx.rpc.krpc.serialization.cbor

import kotlinx.rpc.krpc.serialization.KrpcSerialFormat
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatBuilder
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.cbor.CborBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

internal object KrpcCborSerialFormat : KrpcSerialFormat<Cbor, CborBuilder> {
    override fun withBuilder(from: Cbor?, builderConsumer: CborBuilder.() -> Unit): Cbor {
        return Cbor(from ?: Cbor.Default) { builderConsumer() }
    }

    override fun CborBuilder.applySerializersModule(serializersModule: SerializersModule) {
        this.serializersModule += serializersModule
    }
}

/**
 * Extension function that allows to configure CBOR kotlinx.rpc serial format
 * Usage:
 * ```kotlin
 * // this: KrpcConfig
 * serialization {
 *     cbor {
 *         // custom params
 *     }
 * }
 * ```
 */
public fun KrpcSerialFormatConfiguration.cbor(from: Cbor = Cbor.Default, builderConsumer: CborBuilder.() -> Unit = {}) {
    register(KrpcSerialFormatBuilder.Binary(KrpcCborSerialFormat, from, builderConsumer))
}
