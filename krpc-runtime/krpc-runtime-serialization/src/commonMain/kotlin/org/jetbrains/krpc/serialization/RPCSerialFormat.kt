package org.jetbrains.krpc.serialization

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.modules.SerializersModule

interface RPCSerialFormat<Format : SerialFormat, FormatBuilder : Any> {
    fun withBuilder(from: Format? = null, builderConsumer: FormatBuilder.() -> Unit): Format

    fun FormatBuilder.applySerializersModule(serializersModule: SerializersModule)
}

sealed class RPCSerialFormatBuilder<Format : SerialFormat, FormatBuilder : Any>(
    rpcSerialFormat: RPCSerialFormat<Format, FormatBuilder>,
    from: Format? = null,
    builder: FormatBuilder.() -> Unit,
) {
    private val builder: (SerializersModule?) -> SerialFormat = { serializersModule ->
        with(rpcSerialFormat) {
            withBuilder(from) {
                builder()
                serializersModule?.let { applySerializersModule(it) }
            }
        }
    }

    fun build(): SerialFormat = builder(null)

    fun applySerializersModuleAndBuild(serializersModule: SerializersModule): SerialFormat {
        return builder(serializersModule)
    }

    class Binary<Format : BinaryFormat, FormatBuilder : Any>(
        rpcSerialFormat: RPCSerialFormat<Format, FormatBuilder>,
        from: Format? = null,
        builder: FormatBuilder.() -> Unit,
    ): RPCSerialFormatBuilder<Format, FormatBuilder>(rpcSerialFormat, from, builder)

    class String<Format : SerialFormat, FormatBuilder : Any>(
        rpcSerialFormat: RPCSerialFormat<Format, FormatBuilder>,
        from: Format? = null,
        builder: FormatBuilder.() -> Unit,
    ): RPCSerialFormatBuilder<Format, FormatBuilder>(rpcSerialFormat, from, builder)
}
