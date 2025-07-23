/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.*
import kotlinx.io.Sink
import libprotowire.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner


// TODO: Evaluate if we should implement a ZeroCopyOutputSink (similar to the ZeroCopyInputSource)
//      to reduce the number of copies during encoding.
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
internal class WireEncoderNative(private val sink: Sink): WireEncoder {
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
    internal val raw = run {
        pw_encoder_new(context.asCPointer(), staticCFunction { ctx, buf, size ->
            if (buf == null || ctx == null) {
                return@staticCFunction false
            }
            ctx.asStableRef<Ctx>().get().write(buf.reinterpret(), size)
        }) ?: error("Failed to create proto wire encoder")
    }

    private val contextCleaner = createCleaner(context) {
        it.dispose()
    }
    private val rawCleaner = createCleaner(raw) {
        pw_encoder_delete(it)
    }

    override fun writeBool(field: Int, value: Boolean): Boolean {
        return pw_encoder_write_bool(raw, field, value)
    }

    override fun writeInt32(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_int32(raw, fieldNr, value)
    }

    override fun writeInt64(fieldNr: Int, value: Long): Boolean {
        return pw_encoder_write_int64(raw, fieldNr, value)
    }

    override fun writeUInt32(fieldNr: Int, value: UInt): Boolean {
        return pw_encoder_write_uint32(raw, fieldNr, value)
    }

    override fun writeUInt64(fieldNr: Int, value: ULong): Boolean {
        return pw_encoder_write_uint64(raw, fieldNr, value)
    }

    override fun writeSInt32(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_sint32(raw, fieldNr, value)
    }

    override fun writeSInt64(fieldNr: Int, value: Long): Boolean {
        return pw_encoder_write_sint64(raw, fieldNr, value)
    }

    override fun writeFixed32(fieldNr: Int, value: UInt): Boolean {
        return pw_encoder_write_fixed32(raw, fieldNr, value)
    }

    override fun writeFixed64(fieldNr: Int, value: ULong): Boolean {
        return pw_encoder_write_fixed64(raw, fieldNr, value)
    }

    override fun writeSFixed32(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_sfixed32(raw, fieldNr, value)
    }

    override fun writeSFixed64(fieldNr: Int, value: Long): Boolean {
        return pw_encoder_write_sfixed64(raw, fieldNr, value)
    }

    override fun writeEnum(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_enum(raw, fieldNr, value)
    }

    override fun writeString(fieldNr: Int, value: String): Boolean {
        if (value.isEmpty()) {
            return pw_encoder_write_string(raw, fieldNr, null, 0)
        }
        return value.usePinned {
            pw_encoder_write_string(raw, fieldNr, it.addressOf(0).reinterpret(), value.length)
        }
    }

    override fun flush() {
        pw_encoder_flush(raw)
    }
}

internal actual fun WireEncoder(sink: Sink): WireEncoder = WireEncoderNative(sink)