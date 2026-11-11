/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.unsafe.UnsafeByteStringApi
import kotlinx.io.bytestring.unsafe.UnsafeByteStringOperations
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufDecodingException

/**
 * Decodes protobuf fields in wire format.
 *
 * Call [readTag] for each field, then the `read` function for the field type. [readTag] returns `null` at the
 * end of the message. A decoder exists only inside [decodeWire] or a marshaller decode.
 */
@InternalRpcApi
public class WireDecoder internal constructor(
    private val input: WireInput,
    /** The maximum nesting depth of messages and groups. */
    public val recursionLimit: Int,
) {
    private var depth = 0

    /** Returns the next tag, or `null` at the end of the message. */
    public fun readTag(): KTag? {
        check(!input.isClosed) { "The WireDecoder is used after its decoding scope ended." }
        if (input.isAtLimit) {
            return null
        }
        val raw = input.readTagVarint()
        if (raw == 0) {
            throw ProtobufDecodingException.invalidTag(0u)
        }
        return KTag.from(raw.toUInt())
    }

    public fun readBool(): Boolean = input.readVarint64() != 0L

    public fun readInt32(): Int = input.readVarint32()

    public fun readInt64(): Long = input.readVarint64()

    public fun readUInt32(): UInt = input.readVarint32().toUInt()

    public fun readUInt64(): ULong = input.readVarint64().toULong()

    public fun readSInt32(): Int = ZigZag.decode(UInt32(input.readVarint32())).value

    public fun readSInt64(): Long = ZigZag.decode(UInt64(input.readVarint64())).value

    public fun readFixed32(): UInt = input.readFixed32().toUInt()

    public fun readFixed64(): ULong = input.readFixed64().toULong()

    public fun readSFixed32(): Int = input.readFixed32()

    public fun readSFixed64(): Long = input.readFixed64()

    public fun readFloat(): Float = Float.fromBits(input.readFixed32())

    public fun readDouble(): Double = Double.fromBits(input.readFixed64())

    public fun readEnum(): Int = readInt32()

    public fun readString(): String = input.readUtf8(readLength())

    @OptIn(UnsafeByteStringApi::class)
    public fun readBytes(): ByteString {
        val length = readLength()
        return if (length == 0) {
            ByteString()
        } else {
            UnsafeByteStringOperations.wrapUnsafe(input.readBytes(length))
        }
    }

    public fun readPackedBool(): List<Boolean> = readPackedVarints { readBool() }

    public fun readPackedInt32(): List<Int> = readPackedVarints { readInt32() }

    public fun readPackedInt64(): List<Long> = readPackedVarints { readInt64() }

    public fun readPackedUInt32(): List<UInt> = readPackedVarints { readUInt32() }

    public fun readPackedUInt64(): List<ULong> = readPackedVarints { readUInt64() }

    public fun readPackedSInt32(): List<Int> = readPackedVarints { readSInt32() }

    public fun readPackedSInt64(): List<Long> = readPackedVarints { readSInt64() }

    public fun readPackedEnum(): List<Int> = readPackedVarints { readEnum() }

    public fun readPackedFixed32(): List<UInt> = readPackedFixed(4) { readFixed32() }

    public fun readPackedSFixed32(): List<Int> = readPackedFixed(4) { readSFixed32() }

    public fun readPackedFloat(): List<Float> = readPackedFixed(4) { readFloat() }

    public fun readPackedFixed64(): List<ULong> = readPackedFixed(8) { readFixed64() }

    public fun readPackedSFixed64(): List<Long> = readPackedFixed(8) { readSFixed64() }

    public fun readPackedDouble(): List<Double> = readPackedFixed(8) { readDouble() }

    public fun <T : InternalMessage> readMessage(msg: T, decoder: (T, WireDecoder) -> Unit) {
        enterNested()
        val length = readLength()
        val oldLimit = input.pushLimit(length)
        decoder(msg, this)
        if (!input.isAtLimit) {
            throw ProtobufDecodingException.truncatedMessage()
        }
        input.popLimit(oldLimit)
        depth--
    }

    public fun <T : InternalMessage> readGroup(msg: T, decoder: (T, WireDecoder) -> Unit) {
        enterNested()
        decoder(msg, this)
        depth--
    }

    public fun skipUnknownField(tag: KTag) {
        readOrSkipUnknownField(tag, null)
    }

    public fun readUnknownField(tag: KTag, unknownFieldEncoder: WireEncoder) {
        readOrSkipUnknownField(tag, unknownFieldEncoder)
    }

    private fun readLength(): Int {
        val length = input.readVarint32()
        if (length < 0) {
            throw ProtobufDecodingException.negativeSize()
        }
        if (length > input.bytesUntilLimit) {
            throw ProtobufDecodingException.truncatedMessage()
        }
        return length
    }

    private fun enterNested() {
        if (depth >= recursionLimit) {
            throw ProtobufDecodingException.recursionLimitExceeded(recursionLimit)
        }
        depth++
    }

    private inline fun <T> readPackedVarints(read: () -> T): List<T> {
        val length = readLength()
        if (length == 0) {
            return emptyList()
        }
        val oldLimit = input.pushLimit(length)
        val result = ArrayList<T>()
        while (!input.isAtLimit) {
            result.add(read())
        }
        input.popLimit(oldLimit)
        return result
    }

    private inline fun <T> readPackedFixed(elementSize: Int, read: () -> T): List<T> {
        val length = readLength()
        if (length % elementSize != 0) {
            throw ProtobufDecodingException("Packed field length $length isn't a multiple of $elementSize.")
        }
        val count = length / elementSize
        val result = ArrayList<T>(count)
        repeat(count) {
            result.add(read())
        }
        return result
    }

    private fun readOrSkipUnknownField(tag: KTag, out: WireEncoder?) {
        when (tag.wireType) {
            WireType.VARINT -> {
                val value = input.readVarint64()
                out?.writeInt64(tag.fieldNr, value)
            }

            WireType.FIXED32 -> {
                val value = input.readFixed32()
                out?.writeFixed32(tag.fieldNr, value.toUInt())
            }

            WireType.FIXED64 -> {
                val value = input.readFixed64()
                out?.writeFixed64(tag.fieldNr, value.toULong())
            }

            WireType.LENGTH_DELIMITED -> {
                val length = readLength()
                if (out == null) {
                    input.skip(length)
                } else {
                    out.writeBytes(tag.fieldNr, input.readBytes(length))
                }
            }

            WireType.START_GROUP -> {
                readOrSkipUnknownGroup(tag, out)
            }

            WireType.END_GROUP -> {
                throw ProtobufDecodingException("Mismatched END_GROUP tag: ${tag.fieldNr}")
            }
        }
    }

    private fun readOrSkipUnknownGroup(tag: KTag, out: WireEncoder?) {
        enterNested()
        out?.writeTag(tag)
        while (true) {
            val t = readTag() ?: throw ProtobufDecodingException.truncatedMessage()
            if (t.wireType == WireType.END_GROUP) {
                if (t.fieldNr != tag.fieldNr) {
                    throw ProtobufDecodingException("Mismatched END_GROUP: got ${t.fieldNr}, want ${tag.fieldNr}")
                }
                out?.writeTag(t)
                depth--
                return
            }
            readOrSkipUnknownField(t, out)
        }
    }
}
