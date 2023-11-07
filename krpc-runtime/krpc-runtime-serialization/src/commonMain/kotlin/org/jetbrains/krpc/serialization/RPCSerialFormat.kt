package org.jetbrains.krpc.serialization

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.modules.SerializersModule

/**
 * [RPCSerialFormat] interface defines an object which helps kRPC to work with various serialization formats
 *
 * Each serialization that can be used in kRPC should define an object of [RPCSerialFormat]
 */
interface RPCSerialFormat<Format : SerialFormat, FormatBuilder : Any> {
    /**
     * Generalization of kotlinx.serialization approach to configure serial formats
     *
     * @param from base instance of [Format] that should be extended via [builderConsumer].
     *             If not provided default one should be used
     * @param builderConsumer function that configures serial format
     */
    fun withBuilder(from: Format? = null, builderConsumer: FormatBuilder.() -> Unit): Format

    /**
     * Special extension for [SerialFormat] API, that allows to extend it with additional [SerializersModule].
     * Required for kRPC to work properly with special argument types like Flows
     */
    fun FormatBuilder.applySerializersModule(serializersModule: SerializersModule)
}

/**
 * Special wrapper class that is used to register serialization format in [RPCSerialFormatConfiguration]
 * Comes in two instances: [RPCSerialFormatBuilder.Binary] and [RPCSerialFormatBuilder.String]
 *
 * @param rpcSerialFormat - instance of [RPCSerialFormat]
 * @param from - optional default format instance
 * @param builder - builder function for format configuration
 */
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
