/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.internal.popLimit
import kotlinx.rpc.grpc.internal.pushLimit
import kotlinx.rpc.internal.utils.InternalRpcApi

// TODO: Evaluate if this buffer size is suitable for all targets (KRPC-186)
// maximum buffer size to allocate as contiguous memory in bytes
internal const val MAX_PACKED_BULK_SIZE: Int = 1_000_000

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
@InternalRpcApi
public interface WireDecoder : AutoCloseable {
    public fun hadError(): Boolean

    /**
     * When the read tag is null, it indicates EOF and the parser may stop at this point.
     */
    public fun readTag(): KTag?
    public fun readBool(): Boolean
    public fun readInt32(): Int
    public fun readInt64(): Long
    public fun readUInt32(): UInt
    public fun readUInt64(): ULong
    public fun readSInt32(): Int
    public fun readSInt64(): Long
    public fun readFixed32(): UInt
    public fun readFixed64(): ULong
    public fun readSFixed32(): Int
    public fun readSFixed64(): Long
    public fun readFloat(): Float
    public fun readDouble(): Double

    public fun readEnum(): Int
    public fun readString(): String
    public fun readBytes(): ByteArray
    public fun readPackedBool(): List<Boolean>
    public fun readPackedInt32(): List<Int>
    public fun readPackedInt64(): List<Long>
    public fun readPackedSInt32(): List<Int>
    public fun readPackedSInt64(): List<Long>
    public fun readPackedUInt32(): List<UInt>
    public fun readPackedUInt64(): List<ULong>
    public fun readPackedFixed32(): List<UInt>
    public fun readPackedFixed64(): List<ULong>
    public fun readPackedSFixed32(): List<Int>
    public fun readPackedSFixed64(): List<Long>
    public fun readPackedFloat(): List<Float>
    public fun readPackedDouble(): List<Double>
    public fun readPackedEnum(): List<Int>

    // TODO: Throw error instead of just returning
    public fun <T : InternalMessage> readMessage(msg: T, decoder: (T, WireDecoder) -> Unit) {
        val len = readInt32()
        if (hadError()) return
        if (len <= 0) return
        val limit = pushLimit(len)
        decoder(msg, this)
        popLimit(limit)
    }

    public fun skipValue(writeType: WireType) {
        when (writeType) {
            WireType.VARINT -> readInt64()
            WireType.FIXED32 -> readFixed32()
            WireType.FIXED64 -> readFixed64()
            WireType.LENGTH_DELIMITED -> readBytes()
            WireType.START_GROUP -> error("Unexpected START_GROUP wire type")
            WireType.END_GROUP -> {} // nothing to do
        }
    }
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