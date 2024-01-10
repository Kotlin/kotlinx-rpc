/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.serialization

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
 * Extension function that allows to configure JSON kRPC serial format
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
fun RPCSerialFormatConfiguration.json(from: Json = Json.Default, builderConsumer: JsonBuilder.() -> Unit = {}) {
    register(RPCSerialFormatBuilder.String(RPCJsonSerialFormat, from, builderConsumer))
}
