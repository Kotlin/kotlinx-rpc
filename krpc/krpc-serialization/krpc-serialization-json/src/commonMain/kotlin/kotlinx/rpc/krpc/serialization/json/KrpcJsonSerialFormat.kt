/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.serialization.json

import kotlinx.rpc.krpc.serialization.KrpcSerialFormat
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatBuilder
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

internal object KrpcJsonSerialFormat : KrpcSerialFormat<Json, JsonBuilder> {
    override fun withBuilder(from: Json?, builderConsumer: JsonBuilder.() -> Unit): Json {
        return Json(from ?: Json.Default) { builderConsumer() }
    }

    override fun JsonBuilder.applySerializersModule(serializersModule: SerializersModule) {
        this.serializersModule += serializersModule
    }
}

/**
 * Extension function that allows to configure JSON kotlinx.rpc serial format
 * Usage:
 * ```kotlin
 * // this: KrpcConfig
 * serialization {
 *     json {
 *         // custom params
 *     }
 * }
 * ```
 */
public fun KrpcSerialFormatConfiguration.json(from: Json = Json.Default, builderConsumer: JsonBuilder.() -> Unit = {}) {
    register(KrpcSerialFormatBuilder.String(KrpcJsonSerialFormat, from, builderConsumer))
}
