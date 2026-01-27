/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.protobuf.input.stream.InputStream
import kotlin.reflect.KClass

@ExperimentalRpcApi
@Target(AnnotationTarget.CLASS)
public expect annotation class WithCodec(val codec: KClass<out MessageCodec<*>>)

public inline fun <reified T: Any> codec(config: CodecConfig? = null): MessageCodec<T> {
    return codec(T::class, config)
}

public fun <T: Any> codec(kClass: KClass<T>, config: CodecConfig? = null): MessageCodec<T> {
    val codecObj = resolveCodec(kClass) ?: error("No codec object found for ${kClass.qualifiedName}. " +
            "Make sure that the kotlinx.rpc compiler plugin is applied.")
    @Suppress("UNCHECKED_CAST")
    val codec = codecObj as? MessageCodec<T> ?: error("Internal kotlinx.rpc error: " +
            "Codec for ${kClass.qualifiedName} is not a MessageCodec but ${codecObj::class.qualifiedName}")

    if (config == null) {
        // if no default config is specified, we just return the plain codec
        return codec
    }

    // otherwise, we wrap the codec to pass the default config on every encode/decode
    val defaultConfig = config
    return object : MessageCodec<T> {
        override fun encode(
            value: T,
            config: CodecConfig?
        ): InputStream = codec.encode(value, config ?: defaultConfig)

        override fun decode(
            stream: InputStream,
            config: CodecConfig?
        ): T = codec.decode(stream, config ?: defaultConfig)
    }
}

internal expect fun <T: Any> resolveCodec(kClass: KClass<T>): Any?
