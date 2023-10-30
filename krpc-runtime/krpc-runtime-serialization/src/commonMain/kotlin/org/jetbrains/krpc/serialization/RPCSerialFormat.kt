package org.jetbrains.krpc.serialization

import kotlinx.serialization.SerialFormat
import kotlinx.serialization.modules.SerializersModule

interface RPCSerialFormat<Format : SerialFormat, FormatBuilder : Any> {
    fun withBuilder(from: Format? = null, builderConsumer: FormatBuilder.() -> Unit): Format

    fun FormatBuilder.applySerializersModule(serializersModule: SerializersModule)
}

class RPCSerialFormatInitializer<Format: SerialFormat, FormatBuilder : Any>(
    rpcSerialFormat: RPCSerialFormat<Format, FormatBuilder>,
    from: Format? = null,
    builder: FormatBuilder.() -> Unit,
) {
    private val getter: (SerializersModule) -> SerialFormat = { serializersModule ->
        with(rpcSerialFormat) {
            withBuilder(from) {
                builder()
                applySerializersModule(serializersModule)
            }
        }
    }

    fun applySerializersModuleAndGet(serializersModule: SerializersModule): SerialFormat {
        return getter(serializersModule)
    }
}
