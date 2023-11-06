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

fun RPCSerialFormatConfiguration.json(from: Json = Json.Default, builderConsumer: JsonBuilder.() -> Unit = {}) {
    register(RPCSerialFormatInitializer(RPCJsonSerialFormat, from, builderConsumer))
}
