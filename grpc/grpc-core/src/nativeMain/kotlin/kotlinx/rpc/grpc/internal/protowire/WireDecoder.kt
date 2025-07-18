/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal.protowire

import kotlinx.cinterop.*
import libprotowire.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

@ExperimentalForeignApi
@OptIn(ExperimentalNativeApi::class)
internal class WireDecoder(buffer: UByteArray) {
    internal val pinnedBuffer = buffer.pin()
    internal val raw = pw_decoder_new(pinnedBuffer.addressOf(0), buffer.size.toUInt())
        ?: error("Failed to create proto wire decoder")

    init {
        // free the encoder once garbage collector collects its pointer
        createCleaner(raw) {
            pw_decoder_delete(it)
        }
        createCleaner(pinnedBuffer) {
            // it is not sure if this is needed (https://kotlinlang.slack.com/archives/C3SGXARS6/p1752851274402679)
            // or if this is already done by the GC.
            it.unpin()
        }
    }

    fun readTag(): KTag? {
        val tag = pw_decoder_read_tag(raw)
        return KTag.from(tag)
    }

    fun readBool(): Boolean? = memScoped {
        val value = alloc<BooleanVar>()
        if (pw_decoder_read_bool(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readInt32(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_int32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readInt64(): Long? = memScoped {
        val value = alloc<LongVar>()
        if (pw_decoder_read_int64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readUInt32(): UInt? = memScoped {
        val value = alloc<UIntVar>()
        if (pw_decoder_read_uint32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readUInt64(): ULong? = memScoped {
        val value = alloc<ULongVar>()
        if (pw_decoder_read_uint64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readSInt32(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_sint32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readSInt64(): Long? = memScoped {
        val value = alloc<LongVar>()
        if (pw_decoder_read_sint64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readFixed32(): UInt? = memScoped {
        val value = alloc<UIntVar>()
        if (pw_decoder_read_fixed32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readFixed64(): ULong? = memScoped {
        val value = alloc<ULongVar>()
        if (pw_decoder_read_fixed64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readSFixed32(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_sfixed32(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readSFixed64(): Long? = memScoped {
        val value = alloc<LongVar>()
        if (pw_decoder_read_sfixed64(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readEnum(): Int? = memScoped {
        val value = alloc<IntVar>()
        if (pw_decoder_read_enum(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    fun readString(): String? = memScoped {
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
