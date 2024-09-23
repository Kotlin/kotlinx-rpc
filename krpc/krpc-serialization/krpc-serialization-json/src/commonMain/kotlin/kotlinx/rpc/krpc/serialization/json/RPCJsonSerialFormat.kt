/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.serialization.json

import kotlinx.rpc.krpc.serialization.RPCSerialFormat
import kotlinx.rpc.krpc.serialization.RPCSerialFormatBuilder
import kotlinx.rpc.krpc.serialization.RPCSerialFormatConfiguration
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

internal object RPCJsonSerialFormat : RPCSerialFormat<Json, JsonBuilder> {
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
 * // this: RPCConfig
 * serialization {
 *     json {
 *         // custom params
 *     }
 * }
 * ```
 */
public fun RPCSerialFormatConfiguration.json(from: Json = Json.Default, builderConsumer: JsonBuilder.() -> Unit = {}) {
    register(RPCSerialFormatBuilder.String(RPCJsonSerialFormat, from, builderConsumer))
}
