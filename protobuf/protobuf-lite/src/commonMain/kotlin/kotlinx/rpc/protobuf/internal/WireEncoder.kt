/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.unsafe.UnsafeByteStringApi
import kotlinx.io.bytestring.unsafe.UnsafeByteStringOperations
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Encodes protobuf fields in wire format.
 *
 * Writes go to a buffer, so [flush] must be called at the end when the encoder writes to a [Buffer].
 */
@InternalRpcApi
public class WireEncoder internal constructor(private val output: WireOutput) {
    /** Makes an encoder that appends to [buffer] at each [flush]. */
    public constructor(buffer: Buffer) : this(WireOutput(BufferOutputSegments(buffer, UNKNOWN_SIZE)))

    public fun flush() {
        output.flush()
    }

    public fun writeTag(tag: KTag) {
        output.writeVarint(UInt32(tag.toRawKTag().toInt()))
    }

    public fun writeBool(fieldNr: Int, value: Boolean) {
        writeTag(fieldNr, WireType.VARINT)
        output.writeByte(value.asWireByte)
    }

    public fun writeInt32(fieldNr: Int, value: Int) {
        writeTag(fieldNr, WireType.VARINT)
        output.writeVarint(Int32(value))
    }

    public fun writeInt64(fieldNr: Int, value: Long) {
        writeTag(fieldNr, WireType.VARINT)
        output.writeVarint(Int64(value))
    }

    public fun writeUInt32(fieldNr: Int, value: UInt) {
        writeTag(fieldNr, WireType.VARINT)
        output.writeVarint(UInt32(value.toInt()))
    }

    public fun writeUInt64(fieldNr: Int, value: ULong) {
        writeTag(fieldNr, WireType.VARINT)
        output.writeVarint(UInt64(value.toLong()))
    }

    public fun writeSInt32(fieldNr: Int, value: Int) {
        writeTag(fieldNr, WireType.VARINT)
        output.writeVarint(SInt32(value))
    }

    public fun writeSInt64(fieldNr: Int, value: Long) {
        writeTag(fieldNr, WireType.VARINT)
        output.writeVarint(SInt64(value))
    }

    public fun writeFixed32(fieldNr: Int, value: UInt) {
        writeTag(fieldNr, WireType.FIXED32)
        output.writeFixed32(value.toInt())
    }

    public fun writeFixed64(fieldNr: Int, value: ULong) {
        writeTag(fieldNr, WireType.FIXED64)
        output.writeFixed64(value.toLong())
    }

    public fun writeSFixed32(fieldNr: Int, value: Int) {
        writeTag(fieldNr, WireType.FIXED32)
        output.writeFixed32(value)
    }

    public fun writeSFixed64(fieldNr: Int, value: Long) {
        writeTag(fieldNr, WireType.FIXED64)
        output.writeFixed64(value)
    }

    public fun writeFloat(fieldNr: Int, value: Float) {
        writeTag(fieldNr, WireType.FIXED32)
        output.writeFixed32(value.toRawBits())
    }

    public fun writeDouble(fieldNr: Int, value: Double) {
        writeTag(fieldNr, WireType.FIXED64)
        output.writeFixed64(value.toRawBits())
    }

    public fun writeEnum(fieldNr: Int, value: Int) {
        writeInt32(fieldNr, value)
    }

    public fun writeBytes(fieldNr: Int, value: ByteArray) {
        writeTag(fieldNr, WireType.LENGTH_DELIMITED)
        output.writeVarint(UInt32(value.size))
        output.writeBytes(value, 0, value.size)
    }

    @OptIn(UnsafeByteStringApi::class)
    public fun writeBytes(fieldNr: Int, value: ByteString) {
        writeTag(fieldNr, WireType.LENGTH_DELIMITED)
        output.writeVarint(UInt32(value.size))
        UnsafeByteStringOperations.withByteArrayUnsafe(value) { bytes ->
            output.writeBytes(bytes, 0, bytes.size)
        }
    }

    public fun writeString(fieldNr: Int, value: String) {
        writeTag(fieldNr, WireType.LENGTH_DELIMITED)
        val length = Utf8.encodedLength(value)
        output.writeVarint(UInt32(length))
        output.writeUtf8(value, length)
    }

    public fun writePackedBool(fieldNr: Int, value: List<Boolean>, fieldSize: Int) {
        writePackedHeader(fieldNr, fieldSize)
        value.forEach { output.writeByte(it.asWireByte) }
    }

    public fun writePackedInt32(fieldNr: Int, value: List<Int>, fieldSize: Int) {
        writePackedHeader(fieldNr, fieldSize)
        value.forEach { output.writeVarint(Int32(it)) }
    }

    public fun writePackedInt64(fieldNr: Int, value: List<Long>, fieldSize: Int) {
        writePackedHeader(fieldNr, fieldSize)
        value.forEach { output.writeVarint(Int64(it)) }
    }

    public fun writePackedUInt32(fieldNr: Int, value: List<UInt>, fieldSize: Int) {
        writePackedHeader(fieldNr, fieldSize)
        value.forEach { output.writeVarint(UInt32(it.toInt())) }
    }

    public fun writePackedUInt64(fieldNr: Int, value: List<ULong>, fieldSize: Int) {
        writePackedHeader(fieldNr, fieldSize)
        value.forEach { output.writeVarint(UInt64(it.toLong())) }
    }

    public fun writePackedSInt32(fieldNr: Int, value: List<Int>, fieldSize: Int) {
        writePackedHeader(fieldNr, fieldSize)
        value.forEach { output.writeVarint(SInt32(it)) }
    }

    public fun writePackedSInt64(fieldNr: Int, value: List<Long>, fieldSize: Int) {
        writePackedHeader(fieldNr, fieldSize)
        value.forEach { output.writeVarint(SInt64(it)) }
    }

    public fun writePackedFixed32(fieldNr: Int, value: List<UInt>) {
        writePackedHeader(fieldNr, value.size * 4)
        value.forEach { output.writeFixed32(it.toInt()) }
    }

    public fun writePackedFixed64(fieldNr: Int, value: List<ULong>) {
        writePackedHeader(fieldNr, value.size * 8)
        value.forEach { output.writeFixed64(it.toLong()) }
    }

    public fun writePackedSFixed32(fieldNr: Int, value: List<Int>) {
        writePackedHeader(fieldNr, value.size * 4)
        value.forEach { output.writeFixed32(it) }
    }

    public fun writePackedSFixed64(fieldNr: Int, value: List<Long>) {
        writePackedHeader(fieldNr, value.size * 8)
        value.forEach { output.writeFixed64(it) }
    }

    public fun writePackedFloat(fieldNr: Int, value: List<Float>) {
        writePackedHeader(fieldNr, value.size * 4)
        value.forEach { output.writeFixed32(it.toRawBits()) }
    }

    public fun writePackedDouble(fieldNr: Int, value: List<Double>) {
        writePackedHeader(fieldNr, value.size * 8)
        value.forEach { output.writeFixed64(it.toRawBits()) }
    }

    public fun writePackedEnum(fieldNr: Int, value: List<Int>, fieldSize: Int) {
        writePackedInt32(fieldNr, value, fieldSize)
    }

    public fun <T : InternalMessage> writeMessage(
        fieldNr: Int,
        value: T,
        encode: T.(WireEncoder) -> Unit,
    ) {
        writeTag(fieldNr, WireType.LENGTH_DELIMITED)
        output.writeVarint(UInt32(value._size))
        value.encode(this)
    }

    public fun <T : InternalMessage> writeGroupMessage(
        fieldNr: Int,
        value: T,
        encode: T.(WireEncoder) -> Unit,
    ) {
        writeTag(fieldNr, WireType.START_GROUP)
        value.encode(this)
        writeTag(fieldNr, WireType.END_GROUP)
    }

    /** Writes all bytes of [buffer]. Doesn't change [buffer]. */
    public fun writeRawBytes(buffer: Buffer) {
        output.writeBuffer(buffer)
    }

    private val Boolean.asWireByte: Byte
        get() = if (this) {
            1
        } else {
            0
        }

    private fun writeTag(fieldNr: Int, wireType: WireType) {
        output.writeVarint(UInt32((fieldNr shl 3) or wireType.ordinal))
    }

    private fun writePackedHeader(fieldNr: Int, fieldSize: Int) {
        writeTag(fieldNr, WireType.LENGTH_DELIMITED)
        output.writeVarint(UInt32(fieldSize))
    }
}
