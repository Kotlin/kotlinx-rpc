/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import com.google.protobuf.CodedInputStream
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.io.Source
import kotlinx.io.asInputStream
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.unsafe.UnsafeByteStringApi
import kotlinx.io.bytestring.unsafe.UnsafeByteStringOperations
import kotlinx.rpc.protobuf.ProtobufDecodingException

internal class WireDecoderJvm(source: Source) : WireDecoder {
    override var recursionDepth: Int = 0
    override var recursionLimit: Int = kotlinx.rpc.protobuf.ProtoConfig.DEFAULT_RECURSION_LIMIT

    // there is no way to omit coping here
    internal val codedInputStream: CodedInputStream = CodedInputStream.newInstance(source.asInputStream())

    override fun readTag(): KTag? {
        if (codedInputStream.isAtEnd) return null

        val posBefore = codedInputStream.totalBytesRead
        val raw64 = try {
            codedInputStream.readRawVarint64()
        } catch (e: InvalidProtocolBufferException) {
            // readRawVarint64() throws for varints exceeding 10 bytes.
            // Convert to ProtobufDecodingException so callers only need to handle one type.
            throw ProtobufDecodingException(e.message ?: "Malformed varint", e)
        }
        val bytesUsed = codedInputStream.totalBytesRead - posBefore

        // A valid tag must fit in 32 bits (29-bit field number + 3-bit wire type).
        if (raw64 < 0 || raw64 > UInt.MAX_VALUE.toLong()) {
            throw ProtobufDecodingException("Tag field number out of range")
        }

        // Reject overlong varint encodings: the varint used more bytes than the
        // minimum required for its value. Each varint byte carries 7 payload bits,
        // so the minimum number of bytes is ceil(bitsNeeded / 7), with 0 needing 1 byte.
        val minBytes = when {
            raw64 == 0L -> 1
            raw64 < (1L shl 7) -> 1
            raw64 < (1L shl 14) -> 2
            raw64 < (1L shl 21) -> 3
            raw64 < (1L shl 28) -> 4
            else -> 5
        }
        if (bytesUsed > minBytes) {
            throw ProtobufDecodingException("Overlong varint encoding for tag")
        }

        val tag = raw64.toUInt()
        if (tag == 0u) {
            throw ProtobufDecodingException.invalidTag(tag)
        }

        return KTag.from(tag)
    }

    override fun readBool(): Boolean {
        return codedInputStream.readBool()
    }

    override fun readInt32(): Int {
        return codedInputStream.readInt32()
    }

    override fun readInt64(): Long {
        return codedInputStream.readInt64()
    }

    override fun readUInt32(): UInt {
        // todo check java unsigned types
        return codedInputStream.readUInt32().toUInt()
    }

    override fun readUInt64(): ULong {
        // todo check java unsigned types
        return codedInputStream.readUInt64().toULong()
    }

    override fun readSInt32(): Int {
        return codedInputStream.readSInt32()
    }

    override fun readSInt64(): Long {
        return codedInputStream.readSInt64()
    }

    override fun readFixed32(): UInt {
        // todo check java unsigned types
        return codedInputStream.readFixed32().toUInt()
    }

    override fun readFixed64(): ULong {
        // todo check java unsigned types
        return codedInputStream.readFixed64().toULong()
    }

    override fun readSFixed32(): Int {
        return codedInputStream.readSFixed32()
    }

    override fun readSFixed64(): Long {
        return codedInputStream.readSFixed64()
    }

    override fun readFloat(): Float {
        return codedInputStream.readFloat()
    }

    override fun readDouble(): Double {
        return codedInputStream.readDouble()
    }

    override fun readEnum(): Int {
        return codedInputStream.readEnum()
    }

    override fun readString(): String {
        return codedInputStream.readStringRequireUtf8()
    }

    @OptIn(UnsafeByteStringApi::class)
    override fun readBytes(): ByteString {
        return UnsafeByteStringOperations.wrapUnsafe(codedInputStream.readByteArray())
    }

    override fun readPackedBool() = readPackedInternal(this::readBool)
    override fun readPackedInt32() = readPackedInternal(this::readInt32)
    override fun readPackedInt64() = readPackedInternal(this::readInt64)
    override fun readPackedUInt32() = readPackedInternal(this::readUInt32)
    override fun readPackedUInt64() = readPackedInternal(this::readUInt64)
    override fun readPackedSInt32() = readPackedInternal(this::readSInt32)
    override fun readPackedSInt64() = readPackedInternal(this::readSInt64)
    override fun readPackedEnum() = readPackedInternal(this::readEnum)
    override fun readPackedFixed32(): List<UInt> = readPackedInternal(::readFixed32)
    override fun readPackedFixed64(): List<ULong> = readPackedInternal(::readFixed64)
    override fun readPackedSFixed32(): List<Int> = readPackedInternal(::readSFixed32)
    override fun readPackedSFixed64(): List<Long> = readPackedInternal(::readSFixed64)
    override fun readPackedFloat(): List<Float> = readPackedInternal(::readFloat)
    override fun readPackedDouble(): List<Double> = readPackedInternal(::readDouble)

    override fun close() {}

    private fun <T : Any> readPackedInternal(read: () -> T) = readPackedVarInternal(
        size = { -1 },
        readFn = read
    )
}


public actual inline fun checkForPlatformDecodeException(block: () -> Unit) {
    try {
        return block()
    } catch (e: InvalidProtocolBufferException) {
        throw ProtobufDecodingException(e.message ?: "Failed to decode protobuf message.", e)
    }
}

public actual fun WireDecoder(source: Source): WireDecoder {
    return WireDecoderJvm(source)
}
