/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.kotlinx.serialization

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
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
    override fun encode(value: T, config: GrpcMarshallerConfig?): Source {
        return when (serialFormat) {
            is StringFormat -> {
                val stringValue = serialFormat.encodeToString(serializer, value)
                Buffer().apply {
                    writeString(stringValue)
                }
            }

            is BinaryFormat -> {
                val bytesValue = serialFormat.encodeToByteArray(serializer, value)
                Buffer().apply {
                    write(bytesValue)
                }
            }

            else -> {
                error("Only ${StringFormat::class.simpleName} and ${BinaryFormat::class.simpleName} are supported")
            }
        }
    }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): T {
        return when (serialFormat) {
            is StringFormat -> {
                serialFormat.decodeFromString(serializer, source.readString())
            }

            is BinaryFormat -> {
                serialFormat.decodeFromByteArray(serializer, source.readByteArray())
            }

            else -> {
                error("Only ${StringFormat::class.simpleName} and ${BinaryFormat::class.simpleName} are supported")
            }
        }
    }
}
