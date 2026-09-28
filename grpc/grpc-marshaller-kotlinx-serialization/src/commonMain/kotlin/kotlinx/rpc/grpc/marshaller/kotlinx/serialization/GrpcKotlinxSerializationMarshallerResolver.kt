/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.kotlinx.serialization

import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KType

@ExperimentalRpcApi
public class GrpcKotlinxSerializationMarshallerResolver(
    private val serialFormat: SerialFormat,
) : GrpcMarshallerResolver {
    override fun resolveOrNull(kType: KType): GrpcMarshaller<*>? {
        val serializer = serialFormat.serializersModule.serializerOrNull(kType) ?: return null

        return KotlinxSerializationMarshaller(serializer, serialFormat)
    }
}

@ExperimentalRpcApi
public fun SerialFormat.asMarshallerResolver(): GrpcMarshallerResolver =
    GrpcKotlinxSerializationMarshallerResolver(this)

private class KotlinxSerializationMarshaller<T>(
    private val serializer: KSerializer<T>,
    private val serialFormat: SerialFormat,
) : GrpcMarshaller<T> {
    override fun prepare(value: T, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        GrpcEncodedMessage.of(
            when (serialFormat) {
                is StringFormat -> serialFormat.encodeToString(serializer, value).encodeToByteArray()
                is BinaryFormat -> serialFormat.encodeToByteArray(serializer, value)
                else -> unsupportedFormat()
            },
        )

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): T {
        val bytes = reader.readByteArray()
        return when (serialFormat) {
            is StringFormat -> serialFormat.decodeFromString(serializer, bytes.decodeToString())
            is BinaryFormat -> serialFormat.decodeFromByteArray(serializer, bytes)
            else -> unsupportedFormat()
        }
    }

    private fun unsupportedFormat(): Nothing =
        error("Only ${StringFormat::class.simpleName} and ${BinaryFormat::class.simpleName} are supported")
}
