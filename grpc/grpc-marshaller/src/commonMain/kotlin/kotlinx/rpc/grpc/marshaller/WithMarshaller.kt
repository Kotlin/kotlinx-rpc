/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.rpc.annotations.CheckedTypeAnnotation
import kotlinx.rpc.grpc.marshaller.internal.ConfiguredMessageMarshallerDelegate
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Specifies a custom [MessageMarshaller] for encoding and decoding a message type in gRPC communication.
 *
 * If a gRPC service uses a message type annotated with this annotation,
 * the generated gRPC stubs will use the specified marshaller to encode and decode instances of that type.
 *
 * The [MessageMarshaller] for a type annotated with [WithMarshaller] can be retrieved using the [marshaller] function.
 *
 * Example:
 * ```
 * @WithMarshaller(ChatEntryMarshaller::class)
 * class ChatEntry(...)
 * object ChatEntryMarshaller : MessageMarshaller<ChatEntry> { ... }
 *
 * val marshaller = marshallerOf<ChatEntry>()
 * ```
 *
 * *Note:* The referenced [MessageMarshaller] must be an object.
 *
 * @property marshaller The [MessageMarshaller] object responsible for encoding and decoding instances of the annotated type.
 *
 * @see MessageMarshaller
 * @see marshaller
 */
@ExperimentalRpcApi
@Target(AnnotationTarget.CLASS)
public expect annotation class WithMarshaller(val marshaller: KClass<out MessageMarshaller<*>>)

/**
 * An annotation that marks type parameter that must have a [WithMarshaller] annotation.
 *
 * Example
 * ```
 * fun marshallerOf<@HasWithMarshaller T: Any>() { ... }
 *
 * @WithMarshaller(ChatEntryMarshaller::class)
 * class ChatEntry(...)
 *
 * marshallerOf<ChatEntry>() // OK
 * marshallerOf<Int>() // Error
 * ```
 *
 * @see CheckedTypeAnnotation
 * @see WithMarshaller
 * @see marshallerOf
 */
@InternalRpcApi
@CheckedTypeAnnotation(WithMarshaller::class)
@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.CLASS)
public annotation class HasWithMarshaller

/**
 * Retrieves the [MessageMarshaller] for the specified type [T].
 *
 * This function resolves the marshaller associated with type [T] through the [WithMarshaller] annotation.
 *
 * If a [config] is provided, the returned marshaller will use it as the default [MarshallerConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [MarshallerConfig] fits the marshaller's requirements, depends on the marshaller implementation.
 *
 * Example:
 * ```kotlin
 * val chatMarshaller = marshallerOf<ChatEntry>()
 * val encoded = chatMarshaller.encode(chatEntry)
 * val decoded = chatMarshaller.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the marshaller. Must be annotated with [WithMarshaller].
 * @param config Optional default [MarshallerConfig] to use for encoding and decoding operations.
 * @return A [MessageMarshaller] instance for type [T].
 *
 * @see WithMarshaller
 * @see MessageMarshaller
 * @see MarshallerConfig
 */
public inline fun <@HasWithMarshaller reified T: Any> marshallerOf(config: MarshallerConfig? = null): MessageMarshaller<T> {
    return marshallerOf(T::class, config)
}

/**
 * Retrieves the [MessageMarshaller] for the specified [messageType].
 *
 * This function resolves the marshaller associated with the [messageType] through the [WithMarshaller] annotation.
 * The given [messageType] must match the type argument [T].
 *
 * If a [config] is provided, the returned marshaller will use it as the default [MarshallerConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [MarshallerConfig] fits the marshaller's requirements, depends on the marshaller implementation.
 *
 * Example:
 * ```kotlin
 * val chatMarshaller = marshallerOf<ChatEntry>(typeOf<ChatEntry>())
 * val encoded = chatMarshaller.encode(chatEntry)
 * val decoded = chatMarshaller.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the marshaller. Must be annotated with [WithMarshaller].
 * @param messageType The message type [KType] of [T] for which to retrieve the marshaller.
 *        Must be annotated with [WithMarshaller].
 * @param config Optional default [MarshallerConfig] to use for encoding and decoding operations.
 * @return A [MessageMarshaller] instance for type [T].
 *
 * @see WithMarshaller
 * @see MessageMarshaller
 * @see MarshallerConfig
 */
public fun <@HasWithMarshaller T: Any> marshallerOf(messageType: KType, config: MarshallerConfig? = null): MessageMarshaller<T> {
    val classifier = messageType.classifier ?: error("Expected denotable type, found $messageType")
    val classifierClass = classifier as? KClass<*> ?: error("Expected class type, found $messageType")

    @Suppress("UNCHECKED_CAST")
    return marshallerOf(classifierClass as KClass<T>, config)
}

/**
 * Retrieves the [MessageMarshaller] for the specified [messageClass].
 *
 * This function resolves the marshaller associated with the [messageClass] through the [WithMarshaller] annotation.
 *
 * If a [config] is provided, the returned marshaller will use it as the default [MarshallerConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [MarshallerConfig] fits the marshaller's requirements, depends on the marshaller implementation.
 *
 * Example:
 * ```kotlin
 * val chatMarshaller = marshallerOf(ChatEntry::class)
 * val encoded = chatMarshaller.encode(chatEntry)
 * val decoded = chatMarshaller.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the marshaller.
 * @param messageClass The message type class [T] for which to retrieve the marshaller. Must be annotated with [WithMarshaller].
 * @param config Optional default [MarshallerConfig] to use for encoding and decoding operations.
 * @return A [MessageMarshaller] instance for the [messageClass].
 *
 * @see WithMarshaller
 * @see MessageMarshaller
 * @see MarshallerConfig
 */
public fun <@HasWithMarshaller T: Any> marshallerOf(messageClass: KClass<T>, config: MarshallerConfig? = null): MessageMarshaller<T> {
    val marshallerObj = resolveMarshaller(messageClass) ?: error("No marshaller object found for ${messageClass.qualifiedName}. " +
            "Make sure that the kotlinx.rpc compiler plugin is applied.")
    @Suppress("UNCHECKED_CAST")
    val marshaller = marshallerObj as? MessageMarshaller<T> ?: error("Internal kotlinx.rpc error: " +
            "Marshaller for ${messageClass.qualifiedName} is not a MessageMarshaller but ${marshallerObj::class.simpleName}")

    if (config == null) {
        // if no default config is specified, we just return the plain marshaller
        return marshaller
    }

    // otherwise, we wrap the marshaller to pass the default config on every encode/decode
    val defaultConfig = config
    return ConfiguredMessageMarshallerDelegate(defaultConfig, marshaller)
}

internal expect fun <T: Any> resolveMarshaller(kClass: KClass<T>): Any?
