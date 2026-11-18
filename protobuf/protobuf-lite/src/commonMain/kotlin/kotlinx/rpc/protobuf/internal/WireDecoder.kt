/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.unsafe.UnsafeByteStringApi
import kotlinx.io.bytestring.unsafe.UnsafeByteStringOperations
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufDecodingException

/**
 * Decodes protobuf fields in wire format.
 *
 * Call [readTag] for each field, then the `read` function for the field type. [readTag] returns an end marker at the
 * end of the message. A decoder exists only inside [decodeWire] or a marshaller decode.
 *
 * The `readPacked*` functions append their elements to the destination list. An empty payload appends nothing, so
 * several occurrences of one field concatenate.
 */
@InternalRpcApi
public class WireDecoder internal constructor(
    private val input: WireInput,
    /** The maximum nesting depth of messages and groups. */
    public val recursionLimit: Int,
) {
    private var depth = 0

    /** Returns the next tag, or a tag with [WireTag.isEnd] at the end of the message. */
    public fun readTag(): WireTag {
        if (input.isClosed) {
            throw ProtobufDecodingException("The WireDecoder is used after its decoding scope ended.")
        }
        if (input.isAtLimit) {
            return WireTag.END
        }
        val raw = input.readTagVarint()
        val tag = WireTag(raw)
        if (tag.fieldNr == 0) {
            throw ProtobufDecodingException.invalidTag(raw.toUInt())
        }
        val type = raw and WireTag.TYPE_MASK
        if (type > WireType.FIXED32) {
            throw ProtobufDecodingException("Invalid wire type: $type")
        }
        return tag
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

    public fun readPackedBool(destination: MutableList<Boolean>) {
        readPackedVarints(destination) { readBool() }
    }

    public fun readPackedInt32(destination: MutableList<Int>) {
        readPackedVarints(destination) { readInt32() }
    }

    public fun readPackedInt64(destination: MutableList<Long>) {
        readPackedVarints(destination) { readInt64() }
    }

    public fun readPackedUInt32(destination: MutableList<UInt>) {
        readPackedVarints(destination) { readUInt32() }
    }

    public fun readPackedUInt64(destination: MutableList<ULong>) {
        readPackedVarints(destination) { readUInt64() }
    }

    public fun readPackedSInt32(destination: MutableList<Int>) {
        readPackedVarints(destination) { readSInt32() }
    }

    public fun readPackedSInt64(destination: MutableList<Long>) {
        readPackedVarints(destination) { readSInt64() }
    }

    public fun <T> readPackedEnum(destination: MutableList<T>, fromNumber: (Int) -> T) {
        readPackedVarints(destination) { fromNumber(readEnum()) }
    }

    public fun readPackedFixed32(destination: MutableList<UInt>) {
        readPackedFixed(destination, 4) { readFixed32() }
    }

    public fun readPackedSFixed32(destination: MutableList<Int>) {
        readPackedFixed(destination, 4) { readSFixed32() }
    }

    public fun readPackedFloat(destination: MutableList<Float>) {
        readPackedFixed(destination, 4) { readFloat() }
    }

    public fun readPackedFixed64(destination: MutableList<ULong>) {
        readPackedFixed(destination, 8) { readFixed64() }
    }

    public fun readPackedSFixed64(destination: MutableList<Long>) {
        readPackedFixed(destination, 8) { readSFixed64() }
    }

    public fun readPackedDouble(destination: MutableList<Double>) {
        readPackedFixed(destination, 8) { readDouble() }
    }

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

    public fun skipUnknownField(tag: WireTag) {
        readOrSkipUnknownField(tag, null)
    }

    /**
     * Appends the original bytes of [tag] and its value to [unknownFields].
     */
    public fun readUnknownField(tag: WireTag, unknownFields: Buffer) {
        readOrSkipUnknownField(tag, unknownFields)
    }

    private fun readLength(): Int {
        return checkedLength(input.readVarint32())
    }

    private fun checkedLength(length: Int): Int {
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

    private inline fun <T> readPackedVarints(destination: MutableList<T>, read: () -> T) {
        val length = readLength()
        if (length == 0) {
            return
        }
        val oldLimit = input.pushLimit(length)
        while (!input.isAtLimit) {
            destination.add(read())
        }
        input.popLimit(oldLimit)
    }

    private inline fun <T> readPackedFixed(destination: MutableList<T>, elementSize: Int, read: () -> T) {
        val length = readLength()
        if (length % elementSize != 0) {
            throw ProtobufDecodingException("Packed field length $length isn't a multiple of $elementSize.")
        }
        val count = length / elementSize
        repeat(count) {
            destination.add(read())
        }
    }

    private fun readOrSkipUnknownField(tag: WireTag, out: Buffer?) {
        writeUnknownTag(tag, out)
        when (tag.wireType) {
            WireType.VARINT -> {
                if (out == null) {
                    input.readVarint64()
                } else {
                    input.readVarint64Into(out)
                }
            }

            WireType.FIXED32 -> {
                copyOrSkip(out, 4)
            }

            WireType.FIXED64 -> {
                copyOrSkip(out, 8)
            }

            WireType.LENGTH_DELIMITED -> {
                val length = checkedLength(readUnknownVarint(out).toInt())
                copyOrSkip(out, length)
            }

            WireType.START_GROUP -> {
                readOrSkipUnknownGroup(tag, out)
            }

            WireType.END_GROUP -> {
                throw ProtobufDecodingException("Mismatched END_GROUP tag: ${tag.fieldNr}")
            }
        }
    }

    private fun readUnknownVarint(out: Buffer?): Long {
        if (out == null) {
            return input.readVarint64()
        }
        return Varint.decode { input.readByte().also { out.writeByte(it) } }
    }

    private fun copyOrSkip(out: Buffer?, count: Int) {
        if (out == null) {
            input.skip(count)
        } else {
            input.readBytesInto(out, count)
        }
    }

    private fun writeUnknownTag(tag: WireTag, out: Buffer?) {
        if (out != null) {
            // readTag accepts only minimal tag encodings, so these are the original tag bytes.
            Varint.encode(UInt32(tag.raw)) { out.writeByte(it) }
        }
    }

    private fun readOrSkipUnknownGroup(tag: WireTag, out: Buffer?) {
        enterNested()
        while (true) {
            val t = readTag()
            if (t.isEnd) {
                throw ProtobufDecodingException.truncatedMessage()
            }
            if (t.wireType == WireType.END_GROUP) {
                if (t.fieldNr != tag.fieldNr) {
                    throw ProtobufDecodingException("Mismatched END_GROUP: got ${t.fieldNr}, want ${tag.fieldNr}")
                }
                writeUnknownTag(t, out)
                depth--
                return
            }
            readOrSkipUnknownField(t, out)
        }
    }
}
