/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec

import kotlinx.rpc.annotations.CheckedTypeAnnotation
import kotlinx.rpc.grpc.codec.internal.ConfiguredMessageCodecDelegate
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Specifies a custom [MessageCodec] for encoding and decoding a message type in gRPC communication.
 *
 * If a gRPC service uses a message type annotated with this annotation,
 * the generated gRPC stubs will use the specified codec to encode and decode instances of that type.
 *
 * The [MessageCodec] for a type annotated with [WithCodec] can be retrieved using the [codec] function.
 *
 * Example:
 * ```
 * @WithCodec(ChatEntryCodec::class)
 * class ChatEntry(...)
 * object ChatEntryCodec : MessageCodec<ChatEntry> { ... }
 *
 * val codec = codec<ChatEntry>()
 * ```
 *
 * *Note:* The referenced [MessageCodec] must be an object.
 *
 * @property codec The [MessageCodec] object responsible for encoding and decoding instances of the annotated type.
 *
 * @see MessageCodec
 * @see codec
 */
@ExperimentalRpcApi
@Target(AnnotationTarget.CLASS)
public expect annotation class WithCodec(val codec: KClass<out MessageCodec<*>>)

/**
 * An annotation that marks type parameter that must have a [WithCodec] annotation.
 *
 * Example
 * ```
 * fun codec<@HasWithCodec T: Any>() { ... }
 *
 * @WithCodec(ChatEntryCodec::class)
 * class ChatEntry(...)
 *
 * codec<ChatEntry>() // OK
 * codec<Int>() // Error
 * ```
 *
 * @see CheckedTypeAnnotation
 * @see WithCodec
 * @see codec
 */
@InternalRpcApi
@CheckedTypeAnnotation(WithCodec::class)
@Target(AnnotationTarget.TYPE_PARAMETER)
public annotation class HasWithCodec

/**
 * Retrieves the [MessageCodec] for the specified type [T].
 *
 * This function resolves the codec associated with type [T] through the [WithCodec] annotation.
 *
 * If a [config] is provided, the returned codec will use it as the default [CodecConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [CodecConfig] fits the codec's requirements, depends on the codec implementation.
 *
 * Example:
 * ```kotlin
 * val chatCodec = codec<ChatEntry>()
 * val encoded = chatCodec.encode(chatEntry)
 * val decoded = chatCodec.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the codec. Must be annotated with [WithCodec].
 * @param config Optional default [CodecConfig] to use for encoding and decoding operations.
 * @return A [MessageCodec] instance for type [T].
 *
 * @see WithCodec
 * @see MessageCodec
 * @see CodecConfig
 */
public inline fun <@HasWithCodec reified T: Any> codec(config: CodecConfig? = null): MessageCodec<T> {
    return codec(T::class, config)
}

/**
 * Retrieves the [MessageCodec] for the specified [messageType].
 *
 * This function resolves the codec associated with the [messageType] through the [WithCodec] annotation.
 * The given [messageType] must match the type argument [T].
 *
 * If a [config] is provided, the returned codec will use it as the default [CodecConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [CodecConfig] fits the codec's requirements, depends on the codec implementation.
 *
 * Example:
 * ```kotlin
 * val chatCodec = codec<ChatEntry>(typeOf<ChatEntry>())
 * val encoded = chatCodec.encode(chatEntry)
 * val decoded = chatCodec.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the codec. Must be annotated with [WithCodec].
 * @param messageType The message type [KType] of [T] for which to retrieve the codec.
 *        Must be annotated with [WithCodec].
 * @param config Optional default [CodecConfig] to use for encoding and decoding operations.
 * @return A [MessageCodec] instance for type [T].
 *
 * @see WithCodec
 * @see MessageCodec
 * @see CodecConfig
 */
@Suppress("UNCHECKED_CAST")
public fun <@HasWithCodec T: Any> codec(messageType: KType, config: CodecConfig? = null): MessageCodec<T> {
    val classifier = messageType.classifier ?: error("Expected denotable type, found $messageType")
    val classifierClass = classifier as? KClass<*> ?: error("Expected class type, found $messageType")

    return codec(classifierClass as KClass<T>, config)
}

/**
 * Retrieves the [MessageCodec] for the specified [messageClass].
 *
 * This function resolves the codec associated with the [messageClass] through the [WithCodec] annotation.
 *
 * If a [config] is provided, the returned codec will use it as the default [CodecConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [CodecConfig] fits the codec's requirements, depends on the codec implementation.
 *
 * Example:
 * ```kotlin
 * val chatCodec = codec(ChatEntry::class)
 * val encoded = chatCodec.encode(chatEntry)
 * val decoded = chatCodec.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the codec.
 * @param messageClass The message type class [T] for which to retrieve the codec. Must be annotated with [WithCodec].
 * @param config Optional default [CodecConfig] to use for encoding and decoding operations.
 * @return A [MessageCodec] instance for the [messageClass].
 *
 * @see WithCodec
 * @see MessageCodec
 * @see CodecConfig
 */
public fun <@HasWithCodec T: Any> codec(messageClass: KClass<T>, config: CodecConfig? = null): MessageCodec<T> {
    val codecObj = resolveCodec(messageClass) ?: error("No codec object found for ${messageClass.qualifiedName}. " +
            "Make sure that the kotlinx.rpc compiler plugin is applied.")
    @Suppress("UNCHECKED_CAST")
    val codec = codecObj as? MessageCodec<T> ?: error("Internal kotlinx.rpc error: " +
            "Codec for ${messageClass.qualifiedName} is not a MessageCodec but ${codecObj::class.simpleName}")

    if (config == null) {
        // if no default config is specified, we just return the plain codec
        return codec
    }

    // otherwise, we wrap the codec to pass the default config on every encode/decode
    val defaultConfig = config
    return ConfiguredMessageCodecDelegate(defaultConfig, codec)
}

internal expect fun <T: Any> resolveCodec(kClass: KClass<T>): Any?
