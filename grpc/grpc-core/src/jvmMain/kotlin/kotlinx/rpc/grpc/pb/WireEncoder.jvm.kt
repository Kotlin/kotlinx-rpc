/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import com.google.protobuf.CodedOutputStream
import kotlinx.io.IOException
import kotlinx.io.Sink
import kotlinx.io.asOutputStream
import kotlinx.rpc.grpc.ProtobufEncodingException

private class WireEncoderJvm(sink: Sink) : WireEncoder {
    private val codedOutputStream = CodedOutputStream.newInstance(sink.asOutputStream())

    override fun flush() {
        codedOutputStream.flush()
    }

    override fun writeBool(fieldNr: Int, value: Boolean) = checked {
        codedOutputStream.writeBool(fieldNr, value)
    }

    override fun writeInt32(fieldNr: Int, value: Int) = checked {
        codedOutputStream.writeInt32(fieldNr, value)
    }

    override fun writeInt64(fieldNr: Int, value: Long) = checked {
        codedOutputStream.writeInt64(fieldNr, value)
    }

    override fun writeUInt32(fieldNr: Int, value: UInt) = checked {
        // todo check java unsigned types
        codedOutputStream.writeUInt32(fieldNr, value.toInt())
    }

    override fun writeUInt64(fieldNr: Int, value: ULong) = checked {
        // todo check java unsigned types
        codedOutputStream.writeUInt64(fieldNr, value.toLong())
    }

    override fun writeSInt32(fieldNr: Int, value: Int) = checked {
        codedOutputStream.writeSInt32(fieldNr, value)
    }

    override fun writeSInt64(fieldNr: Int, value: Long) = checked {
        codedOutputStream.writeSInt64(fieldNr, value)
    }

    override fun writeFixed32(fieldNr: Int, value: UInt) = checked {
        // todo check java unsigned types
        codedOutputStream.writeFixed32(fieldNr, value.toInt())
    }

    override fun writeFixed64(fieldNr: Int, value: ULong) = checked {
        // todo check java unsigned types
        codedOutputStream.writeFixed64(fieldNr, value.toLong())
    }

    override fun writeSFixed32(fieldNr: Int, value: Int) = checked {
        codedOutputStream.writeSFixed32(fieldNr, value)
    }

    override fun writeSFixed64(fieldNr: Int, value: Long) = checked {
        codedOutputStream.writeSFixed64(fieldNr, value)
    }

    override fun writeFloat(fieldNr: Int, value: Float) = checked {
        codedOutputStream.writeFloat(fieldNr, value)
    }

    override fun writeDouble(fieldNr: Int, value: Double) = checked {
        codedOutputStream.writeDouble(fieldNr, value)
    }

    override fun writeEnum(fieldNr: Int, value: Int) = checked {
        codedOutputStream.writeEnum(fieldNr, value)
    }

    override fun writeBytes(fieldNr: Int, value: ByteArray) = checked {
        codedOutputStream.writeByteArray(fieldNr, value)
    }

    override fun writeString(fieldNr: Int, value: String) = checked {
        codedOutputStream.writeString(fieldNr, value)
    }

    override fun writePackedBool(
        fieldNr: Int,
        value: List<Boolean>,
        fieldSize: Int,
    ) = checked {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeBoolNoTag)
    }

    override fun writePackedInt32(
        fieldNr: Int,
        value: List<Int>,
        fieldSize: Int,
    ) = checked {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeInt32NoTag)
    }

    override fun writePackedInt64(
        fieldNr: Int,
        value: List<Long>,
        fieldSize: Int,
    ) = checked {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeInt64NoTag)
    }

    override fun writePackedUInt32(
        fieldNr: Int,
        value: List<UInt>,
        fieldSize: Int,
    ) = checked {
        writePackedInternal(fieldNr, value, fieldSize) { codedOutputStream, v ->
            codedOutputStream.writeUInt32NoTag(v.toInt())
        }
    }

    override fun writePackedUInt64(
        fieldNr: Int,
        value: List<ULong>,
        fieldSize: Int,
    ) = checked {
        writePackedInternal(fieldNr, value, fieldSize) { codedOutputStream, v ->
            codedOutputStream.writeUInt64NoTag(v.toLong())
        }
    }

    override fun writePackedSInt32(
        fieldNr: Int,
        value: List<Int>,
        fieldSize: Int,
    ) = checked {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeSInt32NoTag)
    }

    override fun writePackedSInt64(
        fieldNr: Int,
        value: List<Long>,
        fieldSize: Int,
    ) = checked {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeSInt64NoTag)
    }

    override fun writePackedFixed32(fieldNr: Int, value: List<UInt>) = checked {
        writePackedInternal(fieldNr, value, value.size * UInt.SIZE_BYTES) { codedOutputStream, v ->
            codedOutputStream.writeFixed32NoTag(v.toInt())
        }
    }

    override fun writePackedFixed64(fieldNr: Int, value: List<ULong>) = checked {
        writePackedInternal(fieldNr, value, value.size * ULong.SIZE_BYTES) { codedOutputStream, v ->
            codedOutputStream.writeFixed64NoTag(v.toLong())
        }
    }

    override fun writePackedSFixed32(fieldNr: Int, value: List<Int>) = checked {
        writePackedInternal(fieldNr, value, value.size * Int.SIZE_BYTES, CodedOutputStream::writeSFixed32NoTag)
    }

    override fun writePackedSFixed64(fieldNr: Int, value: List<Long>) = checked {
        writePackedInternal(fieldNr, value, value.size * Long.SIZE_BYTES, CodedOutputStream::writeSFixed64NoTag)
    }

    override fun writePackedFloat(fieldNr: Int, value: List<Float>) = checked {
        writePackedInternal(fieldNr, value, value.size * Float.SIZE_BYTES, CodedOutputStream::writeFloatNoTag)
    }

    override fun writePackedDouble(fieldNr: Int, value: List<Double>) = checked {
        writePackedInternal(fieldNr, value, value.size * Double.SIZE_BYTES, CodedOutputStream::writeDoubleNoTag)
    }

    override fun <T : InternalMessage> writeMessage(
        fieldNr: Int,
        value: T,
        encode: T.(WireEncoder) -> Unit,
    ) {
        codedOutputStream.writeTag(fieldNr, WireType.LENGTH_DELIMITED.ordinal)
        codedOutputStream.writeInt32NoTag(value._size)
        value.encode(this)
    }

    private inline fun <T> writePackedInternal(
        fieldNr: Int,
        value: List<T>,
        fieldSize: Int,
        crossinline writer: (CodedOutputStream, T) -> Unit,
    ) = checked {
        codedOutputStream.writeTag(fieldNr, WireType.LENGTH_DELIMITED.ordinal)
        // write the field size of the packed field
        codedOutputStream.writeInt32NoTag(fieldSize)
        for (v in value) {
            writer(codedOutputStream, v)
        }
    }
}

internal actual fun WireEncoder(sink: Sink): WireEncoder {
    return WireEncoderJvm(sink)
}

/**
 * Wraps a [kotlinx.io.IOException] in our own [kotlinx.rpc.grpc.ProtobufEncodingException].
 */
private inline fun checked(crossinline block: () -> Unit) {
    try {
        return block()
    } catch (e: IOException) {
        throw ProtobufEncodingException("Failed to encode protobuf message.", e)
    }
}
