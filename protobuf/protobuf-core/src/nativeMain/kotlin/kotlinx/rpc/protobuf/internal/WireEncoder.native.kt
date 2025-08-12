/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.*
import kotlinx.io.Sink
import libprotowire.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner


@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
internal class WireEncoderNative(private val sink: Sink) : WireEncoder {
    /**
     * The context object provides a stable reference to the kotlin context.
     * This is required, as functions must be static and cannot capture environment references.
     * With this context, the write callback (called by the pw_encoder_t) is able
     * to write the data to the [sink].
     */
    private inner class Ctx {
        fun write(buf: CPointer<ByteVar>, size: Int): Boolean {
            sink.writeFully(buf, 0L, size.toLong())
            return true
        }
    }

    // create context as a stable reference that can be passed to static function callback
    private val context = StableRef.create(this.Ctx())

    // construct encoder with a callback that calls write() on this.context
    internal val raw: CPointer<pw_encoder_t> = run {
        pw_encoder_new(context.asCPointer(), staticCFunction { ctx, buf, size ->
            if (buf == null || ctx == null) {
                return@staticCFunction false
            }
            ctx.asStableRef<Ctx>().get().write(buf.reinterpret(), size)
        }) ?: error("Failed to create proto wire encoder")
    }

    @Suppress("unused")
    private val contextCleaner = createCleaner(context) {
        it.dispose()
    }

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        pw_encoder_delete(it)
    }

    override fun flush() {
        pw_encoder_flush(raw)
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
        pw_encoder_write_int32_no_tag(raw, value._size)
        value.encode(this)
    }
}

public actual fun WireEncoder(sink: Sink): WireEncoder = WireEncoderNative(sink)


// the current implementation is slow, as it iterates through the list, to write each element individually,
// which can be speed up in case of fixed sized types, that are not compressed. KRPC-183
@OptIn(ExperimentalForeignApi::class)
private inline fun <T> WireEncoderNative.writePackedInternal(
    fieldNr: Int,
    value: List<T>,
    fieldSize: Int,
    crossinline writer: (CValuesRef<pw_encoder_t>?, T) -> Boolean,
) = checked {
    pw_encoder_write_tag(raw, fieldNr, WireType.LENGTH_DELIMITED.ordinal)
    // write the field size of the packed field
    pw_encoder_write_int32_no_tag(raw, fieldSize)
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