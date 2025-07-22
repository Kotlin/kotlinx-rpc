/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import libprotowire.*
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
internal class WireDecoderNative(private val source: Buffer): WireDecoder {

    // wraps the source in a class that allows to pass data from the source buffer to the C++ encoder
    // without copying it to an intermediate byte array.
    private val zeroCopyInput = StableRef.create(ZeroCopyInputSource(source))

    // construct the pw_decoder_t by passing a pw_zero_copy_input_t that provides a bridge between
    // the CodedInputStream and the given source buffer. it passes functions that call the respective
    // ZeroCopyInputSource methods.
    internal val raw = run {
        if (source.exhausted()) {
            error("Failed to create WireDecoder: provided buffer is empty")
        }

        // construct the pw_zero_copy_input_t that functions as a bridge to the ZeroCopyInputSource
        val zeroCopyCInput = cValue<pw_zero_copy_input> {
            ctx = zeroCopyInput.asCPointer()
            next = staticCFunction { ctx, data, size ->
                ctx!!.asStableRef<ZeroCopyInputSource>().get().next(data!!.reinterpret(), size!!.reinterpret())
            }
            backUp = staticCFunction { ctx, count ->
                ctx!!.asStableRef<ZeroCopyInputSource>().get().backUp(count)
            }
            skip = staticCFunction { ctx, count ->
                ctx!!.asStableRef<ZeroCopyInputSource>().get().skip(count)
            }
            byteCount = staticCFunction { ctx ->
                ctx!!.asStableRef<ZeroCopyInputSource>().get().byteCount()
            }
        }
        pw_decoder_new(zeroCopyCInput)
            ?: error("Failed to create proto wire decoder")
    }


    override fun close() {
        // delete the underlying decoder.
        // this will also fix the position in the source buffer
        // (done by deconstructor of CodedInputStream)
        pw_decoder_delete(raw)
        // close zero inputs on close
        zeroCopyInput.get().close()
        zeroCopyInput.dispose()
    }

    override fun readTag(): KTag? {
        val tag = pw_decoder_read_tag(raw)
        return KTag.from(tag)
    }

    override fun readBool(): Boolean? = memScoped {
        val value = alloc<BooleanVar>()
        if (pw_decoder_read_bool(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readInt32(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_int32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readInt64(): Long? = memScoped {
        val value = alloc<LongVar>()
        if (pw_decoder_read_int64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readUInt32(): UInt? = memScoped {
        val value = alloc<UIntVar>()
        if (pw_decoder_read_uint32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readUInt64(): ULong? = memScoped {
        val value = alloc<ULongVar>()
        if (pw_decoder_read_uint64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readSInt32(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_sint32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readSInt64(): Long? = memScoped {
        val value = alloc<LongVar>()
        if (pw_decoder_read_sint64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readFixed32(): UInt? = memScoped {
        val value = alloc<UIntVar>()
        if (pw_decoder_read_fixed32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readFixed64(): ULong? = memScoped {
        val value = alloc<ULongVar>()
        if (pw_decoder_read_fixed64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readSFixed32(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_sfixed32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readSFixed64(): Long? = memScoped {
        val value = alloc<LongVar>()
        if (pw_decoder_read_sfixed64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readEnum(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_enum(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readString(): String? = memScoped {
        val str = alloc<CPointerVar<pw_string_t>>()
        val ok = pw_decoder_read_string(raw, str.ptr)
        try {
            if (!ok) return null
            return pw_string_c_str(str.value)?.toKString()
        } finally {
            pw_string_delete(str.value)
        }
    }
}

internal actual fun WireDecoder(source: Buffer): WireDecoder = WireDecoderNative(source)