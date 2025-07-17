/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal.protowire

import kotlinx.cinterop.*
import libprotowire.pw_decoder_delete
import libprotowire.pw_decoder_delete_opaque_string
import libprotowire.pw_decoder_new
import libprotowire.pw_decoder_read_bool
import libprotowire.pw_decoder_read_string
import libprotowire.pw_decoder_read_tag
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
    }

    fun readTag(): KTag? {
        val tag = pw_decoder_read_tag(raw)
        return KTag.from(tag)
    }

    fun readBool(): Boolean? = memScoped {
        val bool = alloc<BooleanVar>()
        if (pw_decoder_read_bool(raw, bool.ptr)) {
            return bool.value
        }
        return null
    }

    fun readString(): String? = memScoped {
        // allocate an opaque pointer that holds a reference to the allocated std::string.
        // by keeping std::string object alive, we avoid the need to copy the C++ string to an allocated C string.
        val opaqueStr = alloc<COpaquePointerVar>()
        val cCharPtr = alloc<CPointerVar<ByteVar>>()
        val ok = pw_decoder_read_string(raw, opaqueStr.ptr, cCharPtr.ptr)
        var result: String? = null
        if (ok) result = cCharPtr.value?.toKString()
        // after copying the string to a Kotlin string, we must delete the allocated C++ string (includes the C string)
        pw_decoder_delete_opaque_string(opaqueStr.value)
        return result;

    }
}
