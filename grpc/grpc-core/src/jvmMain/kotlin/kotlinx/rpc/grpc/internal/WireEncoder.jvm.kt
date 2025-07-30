/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import com.google.protobuf.ByteString
import com.google.protobuf.CodedOutputStream
import kotlinx.io.Sink
import kotlinx.io.asOutputStream

private class WireEncoderJvm(sink: Sink) : WireEncoder {
    private val codedOutputStream = CodedOutputStream.newInstance(sink.asOutputStream())

    override fun flush() {
        codedOutputStream.flush()
    }

    override fun writeBool(fieldNr: Int, value: Boolean): Boolean {
        codedOutputStream.writeBool(fieldNr, value)
        return true
    }

    override fun writeInt32(fieldNr: Int, value: Int): Boolean {
        codedOutputStream.writeInt32(fieldNr, value)
        return true
    }

    override fun writeInt64(fieldNr: Int, value: Long): Boolean {
        codedOutputStream.writeInt64(fieldNr, value)
        return true
    }

    override fun writeUInt32(fieldNr: Int, value: UInt): Boolean {
        // todo check java unsigned types
        codedOutputStream.writeUInt32(fieldNr, value.toInt())
        return true
    }

    override fun writeUInt64(fieldNr: Int, value: ULong): Boolean {
        // todo check java unsigned types
        codedOutputStream.writeUInt64(fieldNr, value.toLong())
        return true
    }

    override fun writeSInt32(fieldNr: Int, value: Int): Boolean {
        codedOutputStream.writeSInt32(fieldNr, value)
        return true
    }

    override fun writeSInt64(fieldNr: Int, value: Long): Boolean {
        codedOutputStream.writeSInt64(fieldNr, value)
        return true
    }

    override fun writeFixed32(fieldNr: Int, value: UInt): Boolean {
        // todo check java unsigned types
        codedOutputStream.writeFixed32(fieldNr, value.toInt())
        return true
    }

    override fun writeFixed64(fieldNr: Int, value: ULong): Boolean {
        // todo check java unsigned types
        codedOutputStream.writeFixed64(fieldNr, value.toLong())
        return true
    }

    override fun writeSFixed32(fieldNr: Int, value: Int): Boolean {
        codedOutputStream.writeSFixed32(fieldNr, value)
        return true
    }

    override fun writeSFixed64(fieldNr: Int, value: Long): Boolean {
        codedOutputStream.writeSFixed64(fieldNr, value)
        return true
    }

    override fun writeFloat(fieldNr: Int, value: Float): Boolean {
        codedOutputStream.writeFloat(fieldNr, value)
        return true
    }

    override fun writeDouble(fieldNr: Int, value: Double): Boolean {
        codedOutputStream.writeDouble(fieldNr, value)
        return true
    }

    override fun writeEnum(fieldNr: Int, value: Int): Boolean {
        codedOutputStream.writeEnum(fieldNr, value)
        return true
    }

    override fun writeBytes(fieldNr: Int, value: ByteArray): Boolean {
        codedOutputStream.writeByteArray(fieldNr, value)
        return true
    }

    override fun writeString(fieldNr: Int, value: String): Boolean {
        codedOutputStream.writeString(fieldNr, value)
        return true
    }

    override fun writePackedBool(
        fieldNr: Int,
        value: List<Boolean>,
        fieldSize: Int,
    ): Boolean {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeBoolNoTag)
        return true
    }

    override fun writePackedInt32(
        fieldNr: Int,
        value: List<Int>,
        fieldSize: Int,
    ): Boolean {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeInt32NoTag)
        return true
    }

    override fun writePackedInt64(
        fieldNr: Int,
        value: List<Long>,
        fieldSize: Int,
    ): Boolean {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeInt64NoTag)
        return true
    }

    override fun writePackedUInt32(
        fieldNr: Int,
        value: List<UInt>,
        fieldSize: Int,
    ): Boolean {
        writePackedInternal(fieldNr, value, fieldSize) { codedOutputStream, v ->
            codedOutputStream.writeUInt32NoTag(v.toInt())
        }
        return true
    }

    override fun writePackedUInt64(
        fieldNr: Int,
        value: List<ULong>,
        fieldSize: Int,
    ): Boolean {
        writePackedInternal(fieldNr, value, fieldSize) { codedOutputStream, v ->
            codedOutputStream.writeUInt64NoTag(v.toLong())
        }
        return true
    }

    override fun writePackedSInt32(
        fieldNr: Int,
        value: List<Int>,
        fieldSize: Int,
    ): Boolean {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeSInt32NoTag)
        return true
    }

    override fun writePackedSInt64(
        fieldNr: Int,
        value: List<Long>,
        fieldSize: Int,
    ): Boolean {
        writePackedInternal(fieldNr, value, fieldSize, CodedOutputStream::writeSInt64NoTag)
        return true
    }

    override fun writePackedFixed32(fieldNr: Int, value: List<UInt>): Boolean {
        writePackedInternal(fieldNr, value, value.size * UInt.SIZE_BYTES) { codedOutputStream, v ->
            codedOutputStream.writeFixed32NoTag(v.toInt())
        }
        return true
    }

    override fun writePackedFixed64(fieldNr: Int, value: List<ULong>): Boolean {
        writePackedInternal(fieldNr, value, value.size * ULong.SIZE_BYTES) { codedOutputStream, v ->
            codedOutputStream.writeFixed64NoTag(v.toLong())
        }
        return true
    }

    override fun writePackedSFixed32(fieldNr: Int, value: List<Int>): Boolean {
        writePackedInternal(fieldNr, value, value.size * Int.SIZE_BYTES, CodedOutputStream::writeSFixed32NoTag)
        return true
    }

    override fun writePackedSFixed64(fieldNr: Int, value: List<Long>): Boolean {
        writePackedInternal(fieldNr, value, value.size * Long.SIZE_BYTES, CodedOutputStream::writeSFixed64NoTag)
        return true
    }

    override fun writePackedFloat(fieldNr: Int, value: List<Float>): Boolean {
        writePackedInternal(fieldNr, value, value.size * Float.SIZE_BYTES, CodedOutputStream::writeFloatNoTag)
        return true
    }

    override fun writePackedDouble(fieldNr: Int, value: List<Double>): Boolean {
        writePackedInternal(fieldNr, value, value.size * Double.SIZE_BYTES, CodedOutputStream::writeDoubleNoTag)
        return true
    }

    private inline fun <T> writePackedInternal(
        fieldNr: Int,
        value: List<T>,
        fieldSize: Int,
        crossinline writer: (CodedOutputStream, T) -> Unit,
    ): Boolean {
        codedOutputStream.writeTag(fieldNr, WireType.LENGTH_DELIMITED.ordinal)
        // write the field size of the packed field
        codedOutputStream.writeInt32NoTag(fieldSize)
        for (v in value) {
            writer(codedOutputStream, v)
        }
        return true
    }
}

internal actual fun WireEncoder(sink: Sink): WireEncoder {
    return WireEncoderJvm(sink)
}
