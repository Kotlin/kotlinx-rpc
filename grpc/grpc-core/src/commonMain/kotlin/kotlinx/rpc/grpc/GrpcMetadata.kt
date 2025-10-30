/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.codec.MessageCodec

/**
 * Provides access to read and write metadata values to be exchanged during a gRPC call.
 *
 * Metadata is an ordered map with case-insensitive keys that are ASCII strings. Each key can be
 * associated with multiple values. Values can be either strings (for standard keys) or binary data
 * (for keys ending with "-bin" suffix).
 *
 * ## Key Requirements
 *
 * Keys must contain only the following ASCII characters:
 * - Digits: `0-9`
 * - Lowercase letters: `a-z` (uppercase letters are normalized to lowercase)
 * - Special characters: `-`, `_`, `.`
 *
 * Keys must not contain spaces or other special characters. Invalid keys will cause an
 * [IllegalArgumentException] to be thrown. Binary keys must additionally end with the `-bin` suffix.
 *
 * ## Value Requirements
 *
 * ASCII string values must contain only:
 * - ASCII visible characters (0x21-0x7E)
 * - Space (0x20), but not at the beginning or end of the string
 *
 * Non-ASCII characters in values are replaced with `?` during encoding.
 *
 * ## Thread Safety
 *
 * This class is not thread-safe. Modifications made by one thread may not be visible to another
 * thread concurrently reading the metadata.
 *
 * ## Example usage
 * ```kotlin
 * val metadata = GrpcMetadata().apply {
 *    append("custom-header", "value1")
 *    append("custom-header", "value2")
 *    appendBinary("custom-header-bin", byteArrayOf(1, 2, 3))
 *}
 *
 * val firstValue = metadata["custom-header"] // returns "value2" (last added)
 * val allValues = metadata.getAll("custom-header") // returns ["value1", "value2"]
 * ```
 */
@Suppress("RedundantConstructorKeyword")
public expect class GrpcMetadata constructor()

/**
 * A typed key for metadata entries that uses a [MessageCodec] to encode and decode values.
 *
 * Typed keys provide type-safe access to metadata values with automatic serialization and
 * deserialization using the provided codec. The key name follows the same requirements as
 * string-based keys (case-insensitive, ASCII characters only).
 *
 * For non-binary methods ([get], [getAll], [append], [remove], [removeAll]), the codec must
 * encode values as ASCII strings and assume an ASCII string from the passed stream.
 * For binary methods ([getBinary], [getAllBinary], [appendBinary], [removeBinary], [removeAllBinary]),
 * the codec encodes values as raw bytes.
 *
 * @param T the type of values associated with this key
 * @param name the key name (case-insensitive). Must contain only digits (0-9), lowercase
 *   letters (a-z), and special characters (`-`, `_`, `.`). Binary keys must end with `-bin`.
 * @param codec the codec used to encode and decode values of type [T]
 */
public expect class GrpcMetadataKey<T> public constructor(name: String, codec: MessageCodec<T>) {}

/**
 * Returns the last metadata entry added with the given [key], or `null` if there are no entries.
 *
 * @param key the name of the metadata entry (case-insensitive). Must contain only digits (0-9),
 *   lowercase letters (a-z), and special characters (`-`, `_`, `.`). Must not end with `-bin`.
 * @return the last value associated with the key, or `null` if no values exist
 * @throws IllegalArgumentException if the key ends with `-bin` or contains invalid characters
 */
public expect operator fun GrpcMetadata.get(key: String): String?

/**
 * Returns the last metadata entry added with the given typed [key], or `null` if there are no entries.
 *
 * The value is decoded using the codec associated with the key.
 * The codec must encode values as ASCII strings (not raw bytes).
 *
 * @param T the type of value associated with the key
 * @param key the typed metadata key. The key name must not end with `-bin`.
 * @return the last value associated with the key, decoded using the key's codec, or `null` if no values exist
 * @throws IllegalArgumentException if the key name ends with `-bin` or contains invalid characters
 */
public expect operator fun <T> GrpcMetadata.get(key: GrpcMetadataKey<T>): T?

/**
 * Returns the last binary metadata entry added with the given [key], or `null` if there are no entries.
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification.
 *
 * @param key the name of the metadata entry (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 *   Must end with `-bin`.
 * @return the last binary value associated with the key, or `null` if no values exist
 * @throws IllegalArgumentException if the key does not end with `-bin` or contains invalid characters
 */
public expect fun GrpcMetadata.getBinary(key: String): ByteArray?

/**
 * Returns the last binary metadata entry added with the given typed [key], or `null` if there are no entries.
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification. The value is
 * decoded using the codec associated with the key, which encodes values as raw bytes.
 *
 * @param T the type of value associated with the key
 * @param key the typed metadata key. The key name must end with `-bin`.
 * @return the last binary value associated with the key, decoded using the key's codec, or `null` if no values exist
 * @throws IllegalArgumentException if the key name does not end with `-bin` or contains invalid characters
 */
public expect fun <T> GrpcMetadata.getBinary(key: GrpcMetadataKey<T>): T?

/**
 * Returns all metadata entries with the given [key], in the order they were added.
 *
 * @param key the name of the metadata entry (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 *   Must not end with `-bin`.
 * @return a list of all values associated with the key, or an empty list if no values exist
 * @throws IllegalArgumentException if the key ends with `-bin` or contains invalid characters
 */
public expect fun GrpcMetadata.getAll(key: String): List<String>

/**
 * Returns all metadata entries with the given typed [key], in the order they were added.
 *
 * Each value is decoded using the codec associated with the key. The codec must encode values
 * as ASCII strings (not raw bytes).
 *
 * @param T the type of values associated with the key
 * @param key the typed metadata key. The key name must not end with `-bin`.
 * @return a list of all values associated with the key, decoded using the key's codec, or an empty list if no values exist
 * @throws IllegalArgumentException if the key name ends with `-bin` or contains invalid characters
 */
public expect fun <T> GrpcMetadata.getAll(key: GrpcMetadataKey<T>): List<T>

/**
 * Returns all binary metadata entries with the given [key], in the order they were added.
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification.
 *
 * @param key the name of the metadata entry (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 *   Must end with `-bin`.
 * @return a list of all binary values associated with the key, or an empty list if no values exist
 * @throws IllegalArgumentException if the key does not end with `-bin` or contains invalid characters
 */
public expect fun GrpcMetadata.getAllBinary(key: String): List<ByteArray>

/**
 * Returns all binary metadata entries with the given typed [key], in the order they were added.
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification. Each value is
 * decoded using the codec associated with the key, which encodes values as raw bytes.
 *
 * @param T the type of values associated with the key
 * @param key the typed metadata key. The key name must end with `-bin`.
 * @return a list of all binary values associated with the key, decoded using the key's codec, or an empty list if no values exist
 * @throws IllegalArgumentException if the key name does not end with `-bin` or contains invalid characters
 */
public expect fun <T> GrpcMetadata.getAllBinary(key: GrpcMetadataKey<T>): List<T>

/**
 * Returns an immutable set of all keys present in this metadata.
 *
 * The returned set is a snapshot of the keys at the time of the call and will not reflect
 * subsequent modifications to the metadata.
 *
 * @return an immutable set of all keys
 */
public expect fun GrpcMetadata.keys(): Set<String>

/**
 * Returns `true` if this metadata contains one or more values for the specified [key].
 *
 * @param key the key whose presence is to be tested (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 * @return `true` if this metadata contains the key, `false` otherwise
 */
public expect operator fun GrpcMetadata.contains(key: String): Boolean

/**
 * Appends a metadata entry with the given [key] and [value].
 *
 * If the key already has values, the new value is added to the end of the list.
 * Duplicate values for the same key are permitted.
 *
 * @param key the name of the metadata entry (case-insensitive). Must contain only digits (0-9),
 *   lowercase letters (a-z), and special characters (`-`, `_`, `.`). Must not end with `-bin`.
 * @param value the ASCII string value to add. Non-ASCII characters will be replaced with `?`.
 * @throws IllegalArgumentException if the key contains invalid characters or ends with `-bin`
 */
public expect fun GrpcMetadata.append(key: String, value: String)

/**
 * Appends a metadata entry with the given typed [key] and [value].
 *
 * The value is encoded using the codec associated with the key. The codec must encode values
 * as ASCII strings (not raw bytes). If the key already has values, the new value is added to
 * the end of the list. Duplicate values for the same key are permitted.
 *
 * @param T the type of value associated with the key
 * @param key the typed metadata key. The key name must not end with `-bin`.
 * @param value the value to add, which will be encoded using the key's codec
 * @throws IllegalArgumentException if the key name contains invalid characters or ends with `-bin`
 */
public expect fun <T> GrpcMetadata.append(key: GrpcMetadataKey<T>, value: T)

/**
 * Appends a binary metadata entry with the given [key] and [value].
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification.
 * If the key already has values, the new value is added to the end of the list.
 * Duplicate values for the same key are permitted.
 *
 * @param key the name of the binary metadata entry (case-insensitive). Must contain only digits (0-9),
 *   lowercase letters (a-z), and special characters (`-`, `_`, `.`). Must end with `-bin`.
 * @param value the binary value to add
 * @throws IllegalArgumentException if the key contains invalid characters or does not end with `-bin`
 */
public expect fun GrpcMetadata.appendBinary(key: String, value: ByteArray)

/**
 * Appends a binary metadata entry with the given typed [key] and [value].
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification. The value is
 * encoded using the codec associated with the key, which encodes values as raw bytes. If the
 * key already has values, the new value is added to the end of the list. Duplicate values for
 * the same key are permitted.
 *
 * @param T the type of value associated with the key
 * @param key the typed metadata key. The key name must end with `-bin`.
 * @param value the value to add, which will be encoded using the key's codec
 * @throws IllegalArgumentException if the key name contains invalid characters or does not end with `-bin`
 */
public expect fun <T> GrpcMetadata.appendBinary(key: GrpcMetadataKey<T>, value: T)

/**
 * Removes the first occurrence of the specified [value] for the given [key].
 *
 * @param key the name of the metadata entry (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 *   Must not end with `-bin`.
 * @param value the value to remove
 * @return `true` if the value was found and removed, `false` if the value was not present
 * @throws IllegalArgumentException if the key ends with `-bin` or contains invalid characters
 */
public expect fun GrpcMetadata.remove(key: String, value: String): Boolean

/**
 * Removes the first occurrence of the specified [value] for the given typed [key].
 *
 * The value is compared using the decoded form (after decoding with the key's codec).
 * The codec must encode values as ASCII strings (not raw bytes).
 *
 * @param T the type of value associated with the key
 * @param key the typed metadata key. The key name must not end with `-bin`.
 * @param value the value to remove
 * @return `true` if the value was found and removed, `false` if the value was not present
 * @throws IllegalArgumentException if the key name ends with `-bin` or contains invalid characters
 */
public expect fun <T> GrpcMetadata.remove(key: GrpcMetadataKey<T>, value: T): Boolean

/**
 * Removes the first occurrence of the specified binary [value] for the given [key].
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification.
 * The value is compared by reference, not by content.
 *
 * @param key the name of the binary metadata entry (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 *   Must end with `-bin`.
 * @param value the binary value to remove
 * @return `true` if the value was found and removed, `false` if the value was not present
 * @throws IllegalArgumentException if the key does not end with `-bin`
 */
public expect fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean

/**
 * Removes the first occurrence of the specified binary [value] for the given typed [key].
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification. The value is
 * compared using the decoded form (after decoding with the key's codec), which encodes values
 * as raw bytes.
 *
 * @param T the type of value associated with the key
 * @param key the typed metadata key. The key name must end with `-bin`.
 * @param value the binary value to remove
 * @return `true` if the value was found and removed, `false` if the value was not present
 * @throws IllegalArgumentException if the key name does not end with `-bin` or contains invalid characters
 */
public expect fun <T> GrpcMetadata.removeBinary(key: GrpcMetadataKey<T>, value: T): Boolean

/**
 * Removes all values for the given [key] and returns them.
 *
 * @param key the name of the metadata entries to remove (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 *   Must not end with `-bin`.
 * @return a list of all values that were removed, or an empty list if there were no values
 * @throws IllegalArgumentException if the key ends with `-bin`
 */
public expect fun GrpcMetadata.removeAll(key: String): List<String>

/**
 * Removes all values for the given typed [key] and returns them.
 *
 * Each value is decoded using the codec associated with the key. The codec must encode values
 * as ASCII strings (not raw bytes).
 *
 * @param T the type of values associated with the key
 * @param key the typed metadata key. The key name must not end with `-bin`.
 * @return a list of all values that were removed, decoded using the key's codec, or an empty list if there were no values
 * @throws IllegalArgumentException if the key name ends with `-bin` or contains invalid characters
 */
public expect fun <T> GrpcMetadata.removeAll(key: GrpcMetadataKey<T>): List<T>

/**
 * Removes all binary values for the given [key] and returns them.
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification.
 *
 * @param key the name of the binary metadata entries to remove (case-insensitive).
 *   Must contain only digits (0-9), lowercase letters (a-z), and special characters (`-`, `_`, `.`).
 *   Must end with `-bin`.
 * @return a list of all binary values that were removed, or an empty list if there were no values
 * @throws IllegalArgumentException if the key does not end with `-bin`
 */
public expect fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray>
public expect fun <T> GrpcMetadata.removeAllBinary(key: GrpcMetadataKey<T>): List<T>

/**
 * Merges all entries from [other] metadata into this metadata.
 *
 * This is a purely additive operation. All entries from [other] are appended to this metadata,
 * preserving the order and allowing duplicate values for the same key.
 *
 * @param other the metadata to merge into this metadata
 */
public expect fun GrpcMetadata.merge(other: GrpcMetadata)

/**
 * Merges all entries from [other] metadata into this metadata using the `+=` operator.
 *
 * This is an alias for [merge] that allows idiomatic Kotlin usage.
 *
 * @param other the metadata to merge into this metadata
 * @see merge
 */
public operator fun GrpcMetadata.plusAssign(other: GrpcMetadata): Unit = merge(other)

/**
 * Creates a copy of this metadata containing all entries.
 *
 * @return a new [GrpcMetadata] instance with the same entries as this metadata
 */
public fun GrpcMetadata.copy(): GrpcMetadata = GrpcMetadata().also { it.merge(this) }

/**
 * Creates a new metadata instance containing all entries from this metadata and [other].
 *
 * This operation does not modify either the current or [other] metadata.
 *
 * @param other the metadata to merge with this metadata
 * @return a new [GrpcMetadata] instance containing entries from both metadata objects
 */
public operator fun GrpcMetadata.plus(other: GrpcMetadata): GrpcMetadata = copy().apply { merge(other) }
