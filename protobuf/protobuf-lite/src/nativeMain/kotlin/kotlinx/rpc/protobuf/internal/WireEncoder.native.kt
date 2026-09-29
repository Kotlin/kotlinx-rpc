/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.usePinned
import kotlinx.io.Sink
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.unsafe.UnsafeByteStringApi
import kotlinx.io.bytestring.unsafe.UnsafeByteStringOperations
import kotlinx.rpc.grpc.marshaller.internal.NativeRegionMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageWriterOperations
import kotlinx.rpc.protobuf.ProtobufEncodingException
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_delete
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_flush
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_new
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_t
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_bool
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_bool_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_bytes
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_double
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_double_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_enum
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_fixed32
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_fixed32_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_fixed64
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_fixed64_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_float
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_float_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_int32
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_int32_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_int64
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_int64_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_raw_bytes
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sfixed32
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sfixed32_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sfixed64
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sfixed64_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sint32
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sint32_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sint64
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_sint64_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_string
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_uint32
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_uint32_no_tag
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_uint64
import kotlinx.rpc.protobuf.internal.cinterop.pw_encoder_write_uint64_no_tag
import platform.posix.memcpy
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import kotlin.native.ref.createCleaner

private interface EncoderOutput {
    fun write(buf: CPointer<ByteVar>?, size: Int): Boolean
}

// The StableRef points to the output, so the buffered encoder's cleaner can collect
// its handle. The direct encoder closes the handle before the storage scope ends.
private class EncoderHandle(output: EncoderOutput) {
    private val context = StableRef.create(output)
    private var open = true

    val raw: CPointer<pw_encoder_t> = pw_encoder_new(context.asCPointer(), staticCFunction { ctx, buf, size ->
        if (ctx == null || size < 0) return@staticCFunction false
        if (size > 0 && buf == null) return@staticCFunction false
        ctx.asStableRef<EncoderOutput>().get().write(buf?.reinterpret(), size)
    }) ?: run {
        context.dispose()
        error("Failed to create proto wire encoder")
    }

    fun requireOpen(): CPointer<pw_encoder_t> {
        check(open) { "The direct encoder has left its storage scope" }
        return raw
    }

    fun close() {
        if (!open) return
        open = false
        pw_encoder_delete(raw)
        context.dispose()
    }
}

private class BufferOutput(private val sink: Sink) : EncoderOutput {
    override fun write(buf: CPointer<ByteVar>?, size: Int): Boolean {
        if (size > 0) sink.writeFully(checkNotNull(buf), 0L, size)
        return true
    }
}

private class DirectOutput(private val base: CPointer<ByteVar>?, private val capacity: Int) : EncoderOutput {
    var byteCount: Int = 0
        private set

    override fun write(buf: CPointer<ByteVar>?, size: Int): Boolean {
        // Subtraction avoids overflowing when the shim reports a large chunk.
        if (size > capacity - byteCount) return false
        if (size > 0) memcpy(checkNotNull(base) + byteCount, checkNotNull(buf), size.convert())
        byteCount += size
        return true
    }
}

internal class WireEncoderNative private constructor(
    private val handle: EncoderHandle,
    bufferBacked: Boolean = false,
) : WireEncoder {
    constructor(sink: Sink) : this(EncoderHandle(BufferOutput(sink)), bufferBacked = true)

    @OptIn(ExperimentalNativeApi::class)
    @Suppress("unused")
    private val cleaner = if (bufferBacked) createCleaner(handle) { it.close() } else null

    internal val raw: CPointer<pw_encoder_t>
        get() = handle.requireOpen()

    override fun flush() {
        pw_encoder_flush(raw)
    }

    override fun writeTag(tag: KTag) {
        pw_encoder_write_tag(raw, tag.fieldNr, tag.wireType.ordinal)
    }

    override fun writeBool(fieldNr: Int, value: Boolean) = checked {
        pw_encoder_write_bool(raw, fieldNr, value)
    }

    override fun writeInt32(fieldNr: Int, value: Int) = checked {
        pw_encoder_write_int32(raw, fieldNr, value)
    }

    override fun writeInt64(fieldNr: Int, value: Long) = checked {
        pw_encoder_write_int64(raw, fieldNr, value)
    }

    override fun writeUInt32(fieldNr: Int, value: UInt) = checked {
        pw_encoder_write_uint32(raw, fieldNr, value)
    }

    override fun writeUInt64(fieldNr: Int, value: ULong) = checked {
        pw_encoder_write_uint64(raw, fieldNr, value)
    }

    override fun writeSInt32(fieldNr: Int, value: Int) = checked {
        pw_encoder_write_sint32(raw, fieldNr, value)
    }

    override fun writeSInt64(fieldNr: Int, value: Long) = checked {
        pw_encoder_write_sint64(raw, fieldNr, value)
    }

    override fun writeFixed32(fieldNr: Int, value: UInt) = checked {
        pw_encoder_write_fixed32(raw, fieldNr, value)
    }

    override fun writeFixed64(fieldNr: Int, value: ULong) = checked {
        pw_encoder_write_fixed64(raw, fieldNr, value)
    }

    override fun writeSFixed32(fieldNr: Int, value: Int) = checked {
        pw_encoder_write_sfixed32(raw, fieldNr, value)
    }

    override fun writeSFixed64(fieldNr: Int, value: Long) = checked {
        pw_encoder_write_sfixed64(raw, fieldNr, value)
    }

    override fun writeFloat(fieldNr: Int, value: Float) = checked {
        pw_encoder_write_float(raw, fieldNr, value)
    }

    override fun writeDouble(fieldNr: Int, value: Double) = checked {
        pw_encoder_write_double(raw, fieldNr, value)
    }

    override fun writeEnum(fieldNr: Int, value: Int) = checked {
        pw_encoder_write_enum(raw, fieldNr, value)
    }

    override fun writeString(fieldNr: Int, value: String) = checked {
        memScoped {
            if (value.isEmpty()) {
                return@checked pw_encoder_write_string(raw, fieldNr, null, 0)
            }
            val cStr = value.cstr
            val len = cStr.size - 1 // minus 1 as it also counts the null terminator
            return@checked pw_encoder_write_string(raw, fieldNr, cStr.ptr, len)
        }
    }

    override fun writeBytes(fieldNr: Int, value: ByteArray) = checked {
        if (value.isEmpty()) {
            return@checked pw_encoder_write_bytes(raw, fieldNr, null, 0)
        }
        return@checked value.usePinned {
            pw_encoder_write_bytes(raw, fieldNr, it.addressOf(0), value.size)
        }
    }

    @OptIn(UnsafeByteStringApi::class)
    override fun writeBytes(fieldNr: Int, value: ByteString) {
        UnsafeByteStringOperations.withByteArrayUnsafe(value) {
            writeBytes(fieldNr, it)
        }
    }

    override fun writePackedBool(fieldNr: Int, value: List<Boolean>, fieldSize: Int) =
        writePackedInternal(fieldNr, value, fieldSize, ::pw_encoder_write_bool_no_tag)

    override fun writePackedInt32(fieldNr: Int, value: List<Int>, fieldSize: Int) =
        writePackedInternal(fieldNr, value, fieldSize, ::pw_encoder_write_int32_no_tag)

    override fun writePackedInt64(fieldNr: Int, value: List<Long>, fieldSize: Int) =
        writePackedInternal(fieldNr, value, fieldSize, ::pw_encoder_write_int64_no_tag)

    override fun writePackedUInt32(fieldNr: Int, value: List<UInt>, fieldSize: Int) =
        writePackedInternal(fieldNr, value, fieldSize, ::pw_encoder_write_uint32_no_tag)

    override fun writePackedUInt64(fieldNr: Int, value: List<ULong>, fieldSize: Int) =
        writePackedInternal(fieldNr, value, fieldSize, ::pw_encoder_write_uint64_no_tag)

    override fun writePackedSInt32(fieldNr: Int, value: List<Int>, fieldSize: Int) =
        writePackedInternal(fieldNr, value, fieldSize, ::pw_encoder_write_sint32_no_tag)

    override fun writePackedSInt64(fieldNr: Int, value: List<Long>, fieldSize: Int) =
        writePackedInternal(fieldNr, value, fieldSize, ::pw_encoder_write_sint64_no_tag)

    override fun writePackedFixed32(fieldNr: Int, value: List<UInt>) =
        writePackedInternal(fieldNr, value, value.size * UInt.SIZE_BYTES, ::pw_encoder_write_fixed32_no_tag)

    override fun writePackedFixed64(fieldNr: Int, value: List<ULong>) =
        writePackedInternal(fieldNr, value, value.size * ULong.SIZE_BYTES, ::pw_encoder_write_fixed64_no_tag)

    override fun writePackedSFixed32(fieldNr: Int, value: List<Int>) =
        writePackedInternal(fieldNr, value, value.size * Int.SIZE_BYTES, ::pw_encoder_write_sfixed32_no_tag)

    override fun writePackedSFixed64(fieldNr: Int, value: List<Long>) =
        writePackedInternal(fieldNr, value, value.size * Long.SIZE_BYTES, ::pw_encoder_write_sfixed64_no_tag)

    override fun writePackedFloat(fieldNr: Int, value: List<Float>) =
        writePackedInternal(fieldNr, value, value.size * Float.SIZE_BYTES, ::pw_encoder_write_float_no_tag)

    override fun writePackedDouble(fieldNr: Int, value: List<Double>) =
        writePackedInternal(fieldNr, value, value.size * Double.SIZE_BYTES, ::pw_encoder_write_double_no_tag)

    override fun <T : InternalMessage> writeMessage(
        fieldNr: Int,
        value: T,
        encode: T.(WireEncoder) -> Unit,
    ) {
        pw_encoder_write_tag(raw, fieldNr, WireType.LENGTH_DELIMITED.ordinal)
        pw_encoder_write_uint32_no_tag(raw, value._size.toUInt())
        value.encode(this)
    }

    override fun <T : InternalMessage> writeGroupMessage(fieldNr: Int, value: T, encode: T.(WireEncoder) -> Unit) {
        pw_encoder_write_tag(raw, fieldNr, WireType.START_GROUP.ordinal)
        value.encode(this)
        pw_encoder_write_tag(raw, fieldNr, WireType.END_GROUP.ordinal)
    }

    override fun writeRawBytes(bytes: ByteArray, offset: Int, length: Int) {
        raw
        require(offset >= 0 && offset + length <= bytes.size) { "Invalid offset or length" }
        if (length == 0) return
        bytes.usePinned { pinned ->
            pw_encoder_write_raw_bytes(raw, pinned.addressOf(offset), length)
        }
    }

    override fun writeRawBytes(buffer: kotlinx.io.Buffer) {
        raw
        super.writeRawBytes(buffer)
    }

    companion object {
        fun encodeDirect(writer: NativeRegionMessageWriter, block: (WireEncoder) -> Unit) {
            try {
                val expected = writer.remaining
                if (expected == 0) {
                    check(encodeInto(null, 0, block) == 0) { "Protobuf encoding exceeded the declared size" }
                    return
                }
                val count = UnsafeGrpcMessageWriterOperations.writeToTail(writer, expected) { pointer, start, end ->
                    encodeInto(pointer + start, end - start, block)
                }
                check(count == expected) { "Protobuf encoded $count bytes instead of $expected" }
            } catch (cause: Throwable) {
                writer.discard()
                throw cause
            }
        }

        private fun encodeInto(base: CPointer<ByteVar>?, capacity: Int, block: (WireEncoder) -> Unit): Int {
            val output = DirectOutput(base, capacity)
            val handle = EncoderHandle(output)
            try {
                block(WireEncoderNative(handle))
                check(pw_encoder_flush(handle.requireOpen())) {
                    "Failed to encode protobuf message into native storage"
                }
                return output.byteCount
            } finally {
                // CodedOutputStream may still hold patch bytes until Trim or destruction.
                handle.close()
            }
        }
    }
}

@PublishedApi
internal fun encodeDirect(writer: NativeRegionMessageWriter, block: (WireEncoder) -> Unit) {
    WireEncoderNative.encodeDirect(writer, block)
}

@OptIn(ExperimentalNativeApi::class)
private val ensureLittleEndian: Unit = require(Platform.isLittleEndian) {
    "kotlinx-rpc protobuf native implementation requires a little-endian platform"
}

public actual fun WireEncoder(sink: Sink): WireEncoder {
    ensureLittleEndian
    return WireEncoderNative(sink)
}

// the current implementation is slow, as it iterates through the list, to write each element individually,
// which can be speed up in case of fixed sized types, that are not compressed. KRPC-183
private inline fun <T> WireEncoderNative.writePackedInternal(
    fieldNr: Int,
    value: List<T>,
    fieldSize: Int,
    crossinline writer: (CValuesRef<pw_encoder_t>?, T) -> Boolean,
) = checked {
    pw_encoder_write_tag(raw, fieldNr, WireType.LENGTH_DELIMITED.ordinal)
    // write the field size of the packed field
    pw_encoder_write_uint32_no_tag(raw, fieldSize.toUInt())
    for (v in value) {
        if (!writer(raw, v)) {
            return@checked false
        }
    }
    return@checked true
}

/**
 * Checks the block's return value and throws an [ProtobufEncodingException] if its `false`.
 */
private inline fun checked(crossinline block: () -> Boolean) {
    if (!block()) {
        throw ProtobufEncodingException("Failed to encode protobuf message.")
    }
}

public actual inline fun checkForPlatformEncodeException(block: () -> Unit) {
    block() // nothing to check for on native
}
