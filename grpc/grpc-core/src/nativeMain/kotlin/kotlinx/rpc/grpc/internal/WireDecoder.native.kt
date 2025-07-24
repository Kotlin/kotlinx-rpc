/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.io.Buffer
import libprotowire.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.math.min

private const val MAX_PACKED_BULK_SIZE: Int = 1_000_000

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
internal class WireDecoderNative(private val source: Buffer): WireDecoder {

    // wraps the source in a class that allows to pass data from the source buffer to the C++ encoder
    // without copying it to an intermediate byte array.
    private val zeroCopyInput = StableRef.create(ZeroCopyInputSource(source))

    // construct the pw_decoder_t by passing a pw_zero_copy_input_t that provides a bridge between
    // the CodedInputStream and the given source buffer. it passes functions that call the respective
    // ZeroCopyInputSource methods.
    internal val raw = run {
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

    override fun readFloat(): Float? = memScoped {
        val value = alloc<FloatVar>()
        if (pw_decoder_read_float(raw, value.ptr)) {
            return value.value
        }
        return null
    }

    override fun readDouble(): Double? = memScoped {
        val value = alloc<DoubleVar>()
        if (pw_decoder_read_double(raw, value.ptr)) {
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

    // TODO: Is it possible to avoid copying the c_str, by directly allocating a K/N String (as in readBytes)?
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

    // TODO: Should readBytes return a buffer, to prevent allocation of large contiguous memory blocks ? KRPC-182
    override fun readBytes(): ByteArray? {
        val length = readInt32() ?: return null
        if (length < 0) return null
        // check if the remaining buffer size is less than the set length,
        // we can early abort, without allocating unnecessary memory
        if (source.size < length) return null
        if (length == 0) return ByteArray(0)
        val bytes = ByteArray(length)
        var ok = true
        bytes.usePinned {
            ok = pw_decoder_read_raw_bytes(raw, it.addressOf(0), length)
        }
        if (!ok) return null
        return bytes
    }

    /*
     * Based on the length of the packed repeated field, one of two list strategies is chosen.
     * If the length is less or equal a specific threshold (MAX_PACKED_BULK_SIZE),
     * a single array list is filled with the buffer-packed value (two copies).
     * Otherwise, a kotlinx.collections.immutable.PersistentList is used to split allocation in several chunks.
     * To build the persistent list, a buffer array is allocated that is used for fast copy from C++ to Kotlin.
     *
     * Note that this implementation assumes a little endian memory order.
     */
    override fun readPackedFixed32(): List<UInt>? {
        var byteLen = readInt32() ?: return null
        if (byteLen < 0) return null
        if (source.size < byteLen) return null
        if (byteLen % UInt.SIZE_BYTES != 0 ) return null
        val count = byteLen / UInt.SIZE_BYTES
        if (byteLen == 0) return emptyList()

        if (count <= MAX_PACKED_BULK_SIZE) {
            // this implementation assumes that the program is running on little endian machines.
            val arr = UIntArray(count)
            arr.usePinned {
                pw_decoder_read_raw_bytes(raw, it.addressOf(0), byteLen)
            }
            return ArrayList(arr)
        } else {
            val bufByteLen = MAX_PACKED_BULK_SIZE
            val bufLen = bufByteLen / UInt.SIZE_BYTES
            val buffer = UIntArray(bufLen)
            var list = persistentListOf<UInt>()
            buffer.usePinned {
                while (byteLen > 0) {
                    val written = min(bufByteLen, byteLen)
                    pw_decoder_read_raw_bytes(raw, it.addressOf(0), written)
                    list = if (written == bufByteLen) {
                        list.addAll(buffer)
                    } else {
                        list.addAll(buffer.copyOfRange(0, written / UInt.SIZE_BYTES))
                    }
                    byteLen -= written
                }
            }
            return list
        }
    }
}

internal actual fun WireDecoder(source: Buffer): WireDecoder = WireDecoderNative(source)