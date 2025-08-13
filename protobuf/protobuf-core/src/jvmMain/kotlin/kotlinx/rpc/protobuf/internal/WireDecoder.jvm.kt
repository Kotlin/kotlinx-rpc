/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import com.google.protobuf.CodedInputStream
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream

internal class WireDecoderJvm(source: InputStream) : WireDecoder {
    // there is no way to omit coping here
    internal val codedInputStream: CodedInputStream = CodedInputStream.newInstance(source)

    override fun readTag(): KTag? {
        val tag = codedInputStream.readTag().toUInt()
        if (tag == 0u) {
            return null
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

    override fun readBytes(): ByteArray {
        return codedInputStream.readByteArray()
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

public actual fun WireDecoder(source: InputStream): WireDecoder {
    return WireDecoderJvm(source)
}
