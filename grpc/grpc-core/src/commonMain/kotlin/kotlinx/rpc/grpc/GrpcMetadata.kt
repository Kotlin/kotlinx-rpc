/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

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
 * Returns the last metadata entry added with the given [key], or `null` if there are no entries.
 *
 * @param key the name of the metadata entry (case-insensitive). Must contain only digits (0-9),
 *   lowercase letters (a-z), and special characters (`-`, `_`, `.`). Must not end with `-bin`.
 * @return the last value associated with the key, or `null` if no values exist
 * @throws IllegalArgumentException if the key ends with `-bin` or contains invalid characters
 */
public expect operator fun GrpcMetadata.get(key: String): String?

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
 * Removes the first occurrence of the specified binary [value] for the given [key].
 *
 * Binary keys must end with the "-bin" suffix according to gRPC specification.
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
