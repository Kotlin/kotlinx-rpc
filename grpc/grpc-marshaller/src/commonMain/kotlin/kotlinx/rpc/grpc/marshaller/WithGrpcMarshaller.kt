/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.rpc.annotations.CheckedTypeAnnotation
import kotlinx.rpc.grpc.marshaller.internal.ConfiguredGrpcMarshallerDelegate
import kotlinx.rpc.grpc.marshaller.internal.resolveMarshaller
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Specifies a custom [GrpcMarshaller] for encoding and decoding a message type in gRPC communication.
 *
 * If a gRPC service uses a message type annotated with this annotation,
 * the generated gRPC stubs will use the specified marshaller to encode and decode instances of that type.
 *
 * The [GrpcMarshaller] for a type annotated with [WithGrpcMarshaller] can be retrieved using the [marshaller] function.
 *
 * Example:
 * ```
 * @WithGrpcMarshaller(ChatEntryMarshaller::class)
 * class ChatEntry(...)
 * object ChatEntryMarshaller : GrpcMarshaller<ChatEntry> { ... }
 *
 * val marshaller = grpcMarshallerOf<ChatEntry>()
 * ```
 *
 * *Note:* The referenced [GrpcMarshaller] must be an object.
 *
 * @property marshaller The [GrpcMarshaller] object responsible for encoding and decoding
 *   instances of the annotated type.
 *
 * @see GrpcMarshaller
 * @see marshaller
 */
@ExperimentalRpcApi
@Target(AnnotationTarget.CLASS)
public expect annotation class WithGrpcMarshaller(val marshaller: KClass<out GrpcMarshaller<*>>)

/**
 * An annotation that marks type parameter that must have a [WithGrpcMarshaller] annotation.
 *
 * Example
 * ```
 * fun grpcMarshallerOf<@HasWithGrpcMarshaller T: Any>() { ... }
 *
 * @WithGrpcMarshaller(ChatEntryMarshaller::class)
 * class ChatEntry(...)
 *
 * grpcMarshallerOf<ChatEntry>() // OK
 * grpcMarshallerOf<Int>() // Error
 * ```
 *
 * @see CheckedTypeAnnotation
 * @see WithGrpcMarshaller
 * @see grpcMarshallerOf
 */
@CheckedTypeAnnotation(WithGrpcMarshaller::class)
@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.CLASS)
public annotation class HasWithGrpcMarshaller

/**
 * Retrieves the [GrpcMarshaller] for the specified type [T].
 *
 * This function resolves the marshaller associated with type [T] through the [WithGrpcMarshaller] annotation.
 *
 * If a [config] is provided, the returned marshaller will use it as the default [GrpcMarshallerConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [GrpcMarshallerConfig] fits the marshaller's requirements, depends on the marshaller implementation.
 *
 * Example:
 * ```kotlin
 * val chatMarshaller = grpcMarshallerOf<ChatEntry>()
 * val encoded = chatMarshaller.encode(chatEntry)
 * val decoded = chatMarshaller.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the marshaller. Must be annotated with [WithGrpcMarshaller].
 * @param config Optional default [GrpcMarshallerConfig] to use for encoding and decoding operations.
 * @return A [GrpcMarshaller] instance for type [T].
 *
 * @see WithGrpcMarshaller
 * @see GrpcMarshaller
 * @see GrpcMarshallerConfig
 */
public inline fun <@HasWithGrpcMarshaller reified T : Any> grpcMarshallerOf(
    config: GrpcMarshallerConfig? = null,
): GrpcMarshaller<T> {
    return grpcMarshallerOf(T::class, config)
}

/**
 * Retrieves the [GrpcMarshaller] for the specified [messageType].
 *
 * This function resolves the marshaller associated with the [messageType] through the [WithGrpcMarshaller] annotation.
 * The given [messageType] must match the type argument [T].
 *
 * If a [config] is provided, the returned marshaller will use it as the default [GrpcMarshallerConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [GrpcMarshallerConfig] fits the marshaller's requirements, depends on the marshaller implementation.
 *
 * Example:
 * ```kotlin
 * val chatMarshaller = grpcMarshallerOf<ChatEntry>(typeOf<ChatEntry>())
 * val encoded = chatMarshaller.encode(chatEntry)
 * val decoded = chatMarshaller.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the marshaller. Must be annotated with [WithGrpcMarshaller].
 * @param messageType The message type [KType] of [T] for which to retrieve the marshaller.
 *        Must be annotated with [WithGrpcMarshaller].
 * @param config Optional default [GrpcMarshallerConfig] to use for encoding and decoding operations.
 * @return A [GrpcMarshaller] instance for type [T].
 *
 * @see WithGrpcMarshaller
 * @see GrpcMarshaller
 * @see GrpcMarshallerConfig
 */
public fun <@HasWithGrpcMarshaller T : Any> grpcMarshallerOf(
    messageType: KType,
    config: GrpcMarshallerConfig? = null
): GrpcMarshaller<T> {
    val classifier = messageType.classifier ?: error("Expected denotable type, found $messageType")
    val classifierClass = classifier as? KClass<*> ?: error("Expected class type, found $messageType")

    @Suppress("UNCHECKED_CAST")
    return grpcMarshallerOf(classifierClass as KClass<T>, config)
}

/**
 * Retrieves the [GrpcMarshaller] for the specified [messageClass].
 *
 * This function resolves the marshaller associated with the [messageClass] through the [WithGrpcMarshaller] annotation.
 *
 * If a [config] is provided, the returned marshaller will use it as the default [GrpcMarshallerConfig] for all
 * encode/decode operations unless explicitly overridden.
 * If the provided [GrpcMarshallerConfig] fits the marshaller's requirements, depends on the marshaller implementation.
 *
 * Example:
 * ```kotlin
 * val chatMarshaller = grpcMarshallerOf(ChatEntry::class)
 * val encoded = chatMarshaller.encode(chatEntry)
 * val decoded = chatMarshaller.decode(buffer)
 * ```
 *
 * @param T The message type for which to retrieve the marshaller.
 * @param messageClass The message type class [T] for which to retrieve the marshaller.
 *   Must be annotated with [WithGrpcMarshaller].
 * @param config Optional default [GrpcMarshallerConfig] to use for encoding and decoding operations.
 * @return A [GrpcMarshaller] instance for the [messageClass].
 *
 * @see WithGrpcMarshaller
 * @see GrpcMarshaller
 * @see GrpcMarshallerConfig
 */
public fun <@HasWithGrpcMarshaller T : Any> grpcMarshallerOf(
    messageClass: KClass<T>,
    config: GrpcMarshallerConfig? = null
): GrpcMarshaller<T> {
    val marshallerObj = resolveMarshaller(messageClass) ?: error(
        "No marshaller object found for ${messageClass.qualifiedName}. " +
            "Make sure that the kotlinx.rpc compiler plugin is applied."
    )

    @Suppress("UNCHECKED_CAST")
    val marshaller = marshallerObj as? GrpcMarshaller<T> ?: error(
        "Internal kotlinx.rpc error: " +
            "Marshaller for ${messageClass.qualifiedName} is not a GrpcMarshaller " +
            "but ${marshallerObj::class.simpleName}"
    )

    if (config == null) {
        // if no default config is specified, we just return the plain marshaller
        return marshaller
    }

    // otherwise, we wrap the marshaller to pass the default config on every encode/decode
    val defaultConfig = config
    return ConfiguredGrpcMarshallerDelegate(defaultConfig, marshaller)
}
