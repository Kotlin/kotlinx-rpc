/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.io.Buffer

/**
 * A platform-specific decoder for wire format data.
 *
 * This decoder is used by first calling [readTag], than looking up the field based on the field number in the returned,
 * tag and then calling the actual `read*()` method to read the value to the corresponding field.
 *
 * [hadError] indicates an error during decoding. While calling `read*()` is safe, the returned values
 * are meaningless if [hadError] returns `true`.
 *
 * NOTE: If the [hadError] after a call to `read*()` returns `false`, it doesn't mean that the
 * value is correctly decoded. E.g., the following test will pass:
 * ```kt
 * val fieldNr = 1
 * val buffer = Buffer()
 *
 * val encoder = WireEncoder(buffer)
 * assertTrue(encoder.writeInt32(fieldNr, 12312))
 * encoder.flush()
 *
 * WireDecoder(buffer).use { decoder ->
 *     decoder.readTag()
 *     decoder.readBool()
 *     assertFalse(decoder.hasError())
 * }
 * ```
 */
internal interface WireDecoder : AutoCloseable {
    fun hadError(): Boolean
    fun readTag(): KTag?
    fun readBool(): Boolean
    fun readInt32(): Int
    fun readInt64(): Long
    fun readUInt32(): UInt
    fun readUInt64(): ULong
    fun readSInt32(): Int
    fun readSInt64(): Long
    fun readFixed32(): UInt
    fun readFixed64(): ULong
    fun readSFixed32(): Int
    fun readSFixed64(): Long
    fun readFloat(): Float
    fun readDouble(): Double

    fun readEnum(): Int
    fun readString(): String
    fun readBytes(): ByteArray
    fun readPackedBool(): List<Boolean>
    fun readPackedInt32(): List<Int>
    fun readPackedInt64(): List<Long>
    fun readPackedSInt32(): List<Int>
    fun readPackedSInt64(): List<Long>
    fun readPackedUInt32(): List<UInt>
    fun readPackedUInt64(): List<ULong>
    fun readPackedFixed32(): List<UInt>
    fun readPackedFixed64(): List<ULong>
    fun readPackedSFixed32(): List<Int>
    fun readPackedSFixed64(): List<Long>
    fun readPackedFloat(): List<Float>
    fun readPackedDouble(): List<Double>
    fun readPackedEnum(): List<Int>
}

/**
 * Creates a platform-specific [WireDecoder].
 *
 * This constructor takes a [Buffer] instead of a [kotlinx.io.Source] because
 * the native implementation (`WireDecoderNative`) depends on [Buffer]'s internal structure.
 *
 * NOTE: Do not use the [source] buffer while the [WireDecoder] is still open.
 *
 * @param source The buffer containing the encoded wire-format data.
 */
internal expect fun WireDecoder(source: Buffer): WireDecoder