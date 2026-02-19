/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.codec.codec
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.GeneratedProtoMessage
import kotlinx.rpc.protobuf.internal.protoDescriptorOf
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Checks if this [Any] message contains a message of type [T].
 *
 * This method examines the type URL [Any.typeUrl] and compares it against
 * the fully qualified protobuf message type name of [T]. The check is performed
 * by verifying that the type URL ends with `"/<fully.qualified.message.name>"`.
 *
 * Example:
 * ```kotlin
 * val any = Any.pack(Timestamp { seconds = 123 })
 * if (any.contains<Timestamp>()) {
 *     val timestamp = any.unpack<Timestamp>()
 * }
 * ```
 *
 * @param T the protobuf message type to check for
 * @return `true` if this [Any] contains a message of type [T], `false` otherwise
 * @see pack
 * @see unpack
 */
public inline fun <@GeneratedProtoMessage reified T: kotlin.Any> Any.contains(): Boolean = this.contains(T::class)

/**
 * Checks if this [Any] message contains a message of the specified [messageClass] type.
 *
 * This method examines the type URL [Any.typeUrl] and compares it against
 * the fully qualified protobuf message type name of [T]. The check is performed
 * by verifying that the type URL ends with `"/<fully.qualified.message.name>"`.
 *
 * Example:
 * ```kotlin
 * val any = Any.pack(Duration { seconds = 60 })
 * if (any.contains(Duration::class)) {
 *     val duration = any.unpack<Duration>()
 * }
 * ```
 *
 * @param T the protobuf message type to check for
 * @param messageClass the [KClass] of the message type to check
 * @return `true` if this [Any] contains a message of type [messageClass], `false` otherwise
 * @see pack
 * @see unpack
 */
public fun <@GeneratedProtoMessage T: kotlin.Any> Any.contains(messageClass: KClass<T>): Boolean {
    val fullName = protoDescriptorOf(messageClass).fullName
    return typeUrl.endsWith("/$fullName")
}

/**
 * Checks if this [Any] message contains a message of the specified [messageType] type.
 *
 * This method examines the type URL [Any.typeUrl] and compares it against
 * the fully qualified protobuf message type name of [T]. The check is performed
 * by verifying that the type URL ends with `"/<fully.qualified.message.name>"`.
 *
 * Example:
 * ```kotlin
 * val any = Any.pack(Duration { seconds = 60 })
 * if (any.contains<Duration>(typeOf<Duration>)) {
 *     val duration = any.unpack<Duration>()
 * }
 * ```
 *
 * @param T the protobuf message type to check for
 * @param messageType the message [KType] to check
 * @return `true` if this [Any] contains a message of type [messageType], `false` otherwise
 * @see pack
 * @see unpack
 */
public fun <@GeneratedProtoMessage T: kotlin.Any> Any.contains(messageType: KType): Boolean {
    val fullName = protoDescriptorOf<T>(messageType).fullName
    return typeUrl.endsWith("/$fullName")
}

/**
 * Packs a protobuf message into an [Any] message.
 *
 * This method serializes the given [value] and wraps it in an [Any] message with a type URL ([Any.typeUrl])
 * that identifies the message type. The type URL is constructed as `"<urlPrefix>/<fully.qualified.message.name>"`.
 *
 * The resulting [Any] message can be transmitted over the wire and later unpacked using [unpack]
 * by a receiver that knows the message type.
 *
 * Example:
 * ```kotlin
 * val timestamp = Timestamp { seconds = 1234567890; nanos = 123456789 }
 * val any = Any.pack(timestamp)
 * // any.typeUrl = "type.googleapis.com/google.protobuf.Timestamp"
 * ```
 *
 * Example with custom URL prefix:
 * ```kotlin
 * val message = MyMessage { field = "value" }
 * val any = Any.pack(message, urlPrefix = "example.com/protos")
 * // any.typeUrl = "example.com/protos/my.package.MyMessage"
 * ```
 *
 * @param T the protobuf message type to pack
 * @param value the message instance to pack
 * @param urlPrefix the URL prefix for the type URL (default: `"type.googleapis.com"`)
 * @return an [Any] message containing the serialized [value]
 * @see contains
 * @see unpack
 */
public inline fun <@GeneratedProtoMessage reified T : kotlin.Any> Any.Companion.pack(
    value: T,
    urlPrefix: String = "type.googleapis.com",
): Any {
    return pack(value, T::class, urlPrefix)
}

/**
 * This is a helper function of the `pack(T, String)` variant to allow specifying the message class explicitly,
 * which makes it possible to use the internal [protoDescriptorOf] function.
 */
@InternalRpcApi
public fun <@GeneratedProtoMessage T : kotlin.Any> Any.Companion.pack(
    value: T,
    valueClass: KClass<T>,
    urlPrefix: String = "type.googleapis.com",
): Any {
    val descriptor = protoDescriptorOf(valueClass)
    val typeUrl = "$urlPrefix/${descriptor.fullName}"
    val encoded = codec(valueClass)
        .encode(value)

    return Any {
        this.typeUrl = typeUrl
        this.value = encoded.readByteArray()
    }
}

/**
 * Unpacks this [Any] message into a message of type [T].
 *
 * This method deserializes the content of this [Any] message and returns it as an instance
 * of type [T]. Before unpacking, it verifies that the type URL matches the expected message
 * type. If the type URL does not match, an [IllegalArgumentException] is thrown.
 *
 * Example:
 * ```kotlin
 * val any: Any = receiveFromNetwork()
 * if (any.contains<Timestamp>()) {
 *     val timestamp = any.unpack<Timestamp>()
 *     processTimestamp(timestamp)
 * }
 * ```
 *
 * @param T the protobuf message type to unpack to
 * @return the unpacked message of type [T]
 * @throws IllegalArgumentException if this [Any] does not contain a message of type [T]
 * @see pack
 * @see contains
 */
public inline fun <@GeneratedProtoMessage reified T : kotlin.Any> Any.unpack(): T {
    return unpack(T::class)
}

/**
 * Unpacks this [Any] message into a message of class [kClass].
 *
 * This method deserializes the content of this [Any] message and returns it as an instance
 * of class [kClass]. Before unpacking, it verifies that the type URL matches the expected message
 * type. If the type URL does not match, an [IllegalArgumentException] is thrown.
 *
 * Example:
 * ```kotlin
 * val any: Any = receiveFromNetwork()
 * if (any.contains<Timestamp>()) {
 *     val timestamp = any.unpack(Timestamp::class)
 *     processTimestamp(timestamp)
 * }
 * ```
 *
 * @param kClass the protobuf message class to unpack to
 * @return the unpacked message of type [T]
 * @throws IllegalArgumentException if this [Any] does not contain a message of class [kClass]
 * @see pack
 * @see contains
 */
public fun <@GeneratedProtoMessage T : kotlin.Any> Any.unpack(kClass: KClass<T>): T {
    require(contains(kClass)) { "Cannot unpack Any message of type $typeUrl to ${kClass.qualifiedName}" }
    val source = Buffer().apply { write(value) }
    return codec(kClass).decode(source)
}

/**
 * Unpacks this [Any] message into a message of type [kType].
 *
 * This method deserializes the content of this [Any] message and returns it as an instance
 * of type [kType]. Before unpacking, it verifies that the type URL matches the expected message
 * type. If the type URL does not match, an [IllegalArgumentException] is thrown.
 *
 * Example:
 * ```kotlin
 * val any: Any = receiveFromNetwork()
 * if (any.contains<Timestamp>()) {
 *     val timestamp = any.unpack<Timestamp>(typeOf<Timestamp>())
 *     processTimestamp(timestamp)
 * }
 * ```
 *
 * @param kType the protobuf message type to unpack to
 * @return the unpacked message of type [T]
 * @throws IllegalArgumentException if this [Any] does not contain a message of type [kType]
 * @see pack
 * @see contains
 */
public fun <@GeneratedProtoMessage T : kotlin.Any> Any.unpack(kType: KType): T {
    require(contains<T>(kType)) { "Cannot unpack Any message of type $typeUrl to ${kType.classifier}" }
    val source = Buffer().apply { write(value) }
    return codec<T>(kType).decode(source)
}


/**
 * Unpacks this [Any] message into a message of type [T], or returns `null` if the type does not match.
 *
 * This method deserializes the content of this [Any] message and returns it as an instance
 * of type [T]. Before unpacking, it verifies that the type URL matches the expected message
 * type. If the type URL does not match, `null` is returned instead of throwing an exception.
 *
 * Example:
 * ```kotlin
 * val any: Any = receiveFromNetwork()
 * val timestamp = any.unpackOrNull<Timestamp>()
 * if (timestamp != null) {
 *     processTimestamp(timestamp)
 * }
 * ```
 *
 * @param T the protobuf message type to unpack to
 * @return the unpacked message of type [T], or `null` if this [Any] does not contain a message of type [T]
 * @see pack
 * @see contains
 * @see unpack
 */
public inline fun <@GeneratedProtoMessage reified T : kotlin.Any> Any.unpackOrNull(): T? {
    return unpackOrNull(T::class)
}

/**
 * Unpacks this [Any] message into a message of type [kType], or returns `null` if the type does not match.
 *
 * This method deserializes the content of this [Any] message and returns it as an instance
 * of type [kType]. Before unpacking, it verifies that the type URL matches the expected message
 * type. If the type URL does not match, `null` is returned instead of throwing an exception.
 *
 * Example:
 * ```kotlin
 * val any: Any = receiveFromNetwork()
 * val timestamp = any.unpackOrNull<Timestamp>(typeOf<Timestamp>())
 * if (timestamp != null) {
 *     processTimestamp(timestamp)
 * }
 * ```
 *
 * @param kType the protobuf message type to unpack to
 * @return the unpacked message of type [T], or `null` if this [Any] does not contain a message of type [kType]
 * @see pack
 * @see contains
 * @see unpack
 */
public fun <@GeneratedProtoMessage T : kotlin.Any> Any.unpackOrNull(kType: KType): T? {
    return if (contains<T>(kType)) unpack(kType) else null
}

/**
 * Unpacks this [Any] message into a message of class [kClass], or returns `null` if the type does not match.
 *
 * This method deserializes the content of this [Any] message and returns it as an instance
 * of class [kClass]. Before unpacking, it verifies that the type URL matches the expected message
 * type. If the type URL does not match, `null` is returned instead of throwing an exception.
 *
 * Example:
 * ```kotlin
 * val any: Any = receiveFromNetwork()
 * val timestamp = any.unpackOrNull(Timestamp::class)
 * if (timestamp != null) {
 *     processTimestamp(timestamp)
 * }
 * ```
 *
 * @param kClass the protobuf message class to unpack to
 * @return the unpacked message of type [T], or `null` if this [Any] does not contain a message of class [kClass]
 * @see pack
 * @see contains
 * @see unpack
 */
public fun <@GeneratedProtoMessage T : kotlin.Any> Any.unpackOrNull(kClass: KClass<T>): T? {
    return if (contains(kClass)) unpack(kClass) else null
}