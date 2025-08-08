/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.cinterop.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.io.Buffer
import kotlinx.rpc.grpc.internal.ZeroCopyInputSource
import kotlinx.rpc.grpc.internal.readPackedVarInternal
import libprotowire.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.math.min
import kotlin.native.ref.createCleaner

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
internal class WireDecoderNative(private val source: Buffer) : WireDecoder {

    // wraps the source in a class that allows to pass data from the source buffer to the C++ encoder
    // without copying it to an intermediate byte array.
    private val zeroCopyInput = StableRef.create(ZeroCopyInputSource(source))

    // construct the pw_decoder_t by passing a pw_zero_copy_input_t that provides a bridge between
    // the CodedInputStream and the given source buffer. it passes functions that call the respective
    // ZeroCopyInputSource methods.
    internal val raw: CPointer<pw_decoder_t> = run {
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

    val rawCleaner = createCleaner(raw) {
        pw_decoder_delete(it)
    }


    override fun close() {
        // this will fix the position in the source buffer
        // (done by deconstructor of CodedInputStream)
        pw_decoder_close(raw)

        zeroCopyInput.get().close()
        zeroCopyInput.dispose()
    }

    override fun readTag(): KTag? {
        val tag = pw_decoder_read_tag(raw)
        if (tag == 0u) {
            if (!pw_decoder_consumed_entire_msg(raw)) {
                throw ProtobufDecodingException.invalidTag()
            }
            return null
        }
        return KTag.from(tag)
    }

    override fun readBool(): Boolean = memScoped {
        val value = alloc<BooleanVar>()
        pw_decoder_read_bool(raw, value.ptr).checkError()
        return value.value
    }

    override fun readInt32(): Int = memScoped {
        val value = alloc<IntVar>()
        pw_decoder_read_int32(raw, value.ptr).checkError()
        return value.value
    }

    override fun readInt64(): Long = memScoped {
        val value = alloc<LongVar>()
        pw_decoder_read_int64(raw, value.ptr).checkError()
        return value.value
    }

    override fun readUInt32(): UInt = memScoped {
        val value = alloc<UIntVar>()
        pw_decoder_read_uint32(raw, value.ptr).checkError()
        return value.value
    }

    override fun readUInt64(): ULong = memScoped {
        val value = alloc<ULongVar>()
        pw_decoder_read_uint64(raw, value.ptr).checkError()
        return value.value
    }

    override fun readSInt32(): Int = memScoped {
        val value = alloc<IntVar>()
        pw_decoder_read_sint32(raw, value.ptr).checkError()
        return value.value
    }

    override fun readSInt64(): Long = memScoped {
        val value = alloc<LongVar>()
        pw_decoder_read_sint64(raw, value.ptr).checkError()
        return value.value
    }

    override fun readFixed32(): UInt = memScoped {
        val value = alloc<UIntVar>()
        pw_decoder_read_fixed32(raw, value.ptr).checkError()
        return value.value
    }

    override fun readFixed64(): ULong = memScoped {
        val value = alloc<ULongVar>()
        pw_decoder_read_fixed64(raw, value.ptr).checkError()
        return value.value
    }

    override fun readSFixed32(): Int = memScoped {
        val value = alloc<IntVar>()
        pw_decoder_read_sfixed32(raw, value.ptr).checkError()
        return value.value
    }

    override fun readSFixed64(): Long = memScoped {
        val value = alloc<LongVar>()
        pw_decoder_read_sfixed64(raw, value.ptr).checkError()
        return value.value
    }

    override fun readFloat(): Float = memScoped {
        val value = alloc<FloatVar>()
        pw_decoder_read_float(raw, value.ptr).checkError()
        return value.value
    }

    override fun readDouble(): Double = memScoped {
        val value = alloc<DoubleVar>()
        pw_decoder_read_double(raw, value.ptr).checkError()
        return value.value
    }

    override fun readEnum(): Int = memScoped {
        val value = alloc<IntVar>()
        pw_decoder_read_enum(raw, value.ptr).checkError()
        return value.value
    }

    // TODO: Is it possible to avoid copying the c_str, by directly allocating a K/N String (as in readBytes)? KRPC-187
    override fun readString(): String = memScoped {
        val str = alloc<CPointerVar<pw_string_t>>()
        pw_decoder_read_string(raw, str.ptr).checkError()
        try {
            return pw_string_c_str(str.value)?.toKString()
                ?: throw ProtobufDecodingException.genericParsingError()
        } finally {
            pw_string_delete(str.value)
        }
    }

    // TODO: Should readBytes return a buffer, to prevent allocation of large contiguous memory blocks ? KRPC-182
    override fun readBytes(): ByteArray {
        val length = readInt32()
        if (length < 0) throw ProtobufDecodingException.negativeSize()
        // check if the remaining buffer size is less than the set length,
        // we can early abort, without allocating unnecessary memory
        if (source.size < length) throw ProtobufDecodingException.truncatedMessage()
        if (length == 0) return ByteArray(0) // actually an empty array (no error)
        val bytes = ByteArray(length)
        bytes.usePinned {
            pw_decoder_read_raw_bytes(raw, it.addressOf(0), length).checkError()
        }
        return bytes
    }

    override fun readPackedBool() = readPackedVarInternal(this::readBool)
    override fun readPackedInt32() = readPackedVarInternal(this::readInt32)
    override fun readPackedInt64() = readPackedVarInternal(this::readInt64)
    override fun readPackedUInt32() = readPackedVarInternal(this::readUInt32)
    override fun readPackedUInt64() = readPackedVarInternal(this::readUInt64)
    override fun readPackedSInt32() = readPackedVarInternal(this::readSInt32)
    override fun readPackedSInt64() = readPackedVarInternal(this::readSInt64)
    override fun readPackedEnum() = readPackedVarInternal(this::readEnum)

    override fun readPackedFixed32() = readPackedFixedInternal(
        UInt.SIZE_BYTES,
        ::UIntArray,
        Pinned<UIntArray>::addressOf,
        UIntArray::asList,
    )

    override fun readPackedFixed64() = readPackedFixedInternal(
        ULong.SIZE_BYTES,
        ::ULongArray,
        Pinned<ULongArray>::addressOf,
        ULongArray::asList,
    )

    override fun readPackedSFixed32() = readPackedFixedInternal(
        Int.SIZE_BYTES,
        ::IntArray,
        Pinned<IntArray>::addressOf,
        IntArray::asList,
    )

    override fun readPackedSFixed64() = readPackedFixedInternal(
        Long.SIZE_BYTES,
        ::LongArray,
        Pinned<LongArray>::addressOf,
        LongArray::asList,
    )

    override fun readPackedFloat() = readPackedFixedInternal(
        Float.SIZE_BYTES,
        ::FloatArray,
        Pinned<FloatArray>::addressOf,
        FloatArray::asList,
    )

    override fun readPackedDouble() = readPackedFixedInternal(
        Double.SIZE_BYTES,
        ::DoubleArray,
        Pinned<DoubleArray>::addressOf,
        DoubleArray::asList,
    )

    private fun <T : Any> readPackedVarInternal(read: () -> T) = readPackedVarInternal(
        size = { source.size },
        readFn = read
    )

    /*
     * Based on the length of the packed repeated field, one of two list strategies is chosen.
     * If the length is less or equal a specific threshold (MAX_PACKED_BULK_SIZE),
     * a single array list is filled with the buffer-packed value (two copies).
     * Otherwise, a kotlinx.collections.immutable.PersistentList is used to split allocation in several chunks.
     * To build the persistent list, a buffer array is allocated that is used for fast copy from C++ to Kotlin.
     *
     * Note that this implementation assumes a little endian memory order.
     */
    private inline fun <T : Any, R : Any> readPackedFixedInternal(
        sizeBytes: Int,
        crossinline createArray: (Int) -> R,
        crossinline getAddress: Pinned<R>.(Int) -> COpaquePointer,
        crossinline asList: (R) -> List<T>,
    ): List<T> {
        // fetch the size of the packed repeated field
        var byteLen = readInt32()
        if (byteLen < 0) throw ProtobufDecodingException.negativeSize()
        if (source.size < byteLen) throw ProtobufDecodingException.truncatedMessage()
        if (byteLen % sizeBytes != 0) throw ProtobufDecodingException.truncatedMessage()
        if (byteLen == 0) return emptyList()  // actually an empty list (no error)

        // allocate the buffer array (has at most MAX_PACKED_BULK_SIZE bytes)
        val bufByteLen = minOf(byteLen, MAX_PACKED_BULK_SIZE)
        val bufElemCount = bufByteLen / sizeBytes
        val buffer = createArray(bufElemCount)

        buffer.usePinned {
            val bufAddr = it.getAddress(0)

            if (byteLen == bufByteLen) {
                // the whole packed field fits into the buffer -> copy into buffer and returns it as a list.
                pw_decoder_read_raw_bytes(raw, bufAddr, byteLen).checkError()
                return asList(buffer)
            } else {
                // the packed field is too large for the buffer, so we load it into a persistent list
                var chunkedList = persistentListOf<T>()

                while (byteLen > 0) {
                    // copy data into the buffer.
                    val copySize = min(bufByteLen, byteLen)
                    pw_decoder_read_raw_bytes(raw, bufAddr, copySize).checkError()

                    // add buffer to the chunked list
                    chunkedList = if (copySize == bufByteLen) {
                        chunkedList.addAll(asList(buffer))
                    } else {
                        chunkedList.addAll(asList(buffer).subList(0, copySize / sizeBytes))
                    }

                    byteLen -= copySize
                }

                return chunkedList
            }
        }
    }

    private fun Boolean.checkError() {
        if (!this) throw ProtobufDecodingException.genericParsingError()
    }
}

public actual fun WireDecoder(source: Buffer): WireDecoder = WireDecoderNative(source)

public actual fun checkForPlatformDecodeException(block: () -> Unit) {
    block()
}