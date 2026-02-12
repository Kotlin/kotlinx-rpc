/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec.kotlinx.serialization

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KType

@ExperimentalRpcApi
public class KotlinxSerializationCodecResolver(private val serialFormat: SerialFormat) : MessageCodecResolver {
    override fun resolveOrNull(kType: KType): MessageCodec<*>? {
        val serializer = serialFormat.serializersModule.serializerOrNull(kType) ?: return null

        return KotlinxSerializationCodec(serializer, serialFormat)
    }
}

@ExperimentalRpcApi
public fun SerialFormat.asCodecResolver(): MessageCodecResolver =
    KotlinxSerializationCodecResolver(this)

private class KotlinxSerializationCodec<T>(
    private val serializer: KSerializer<T>,
    private val serialFormat: SerialFormat,
) : MessageCodec<T> {
    override fun encode(value: T, config: CodecConfig?): Source {
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

    override fun decode(source: Source, config: CodecConfig?): T {
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
