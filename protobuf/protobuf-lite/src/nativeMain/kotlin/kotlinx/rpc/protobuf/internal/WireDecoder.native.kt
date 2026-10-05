/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.unsafe.UnsafeByteStringApi
import kotlinx.io.bytestring.unsafe.UnsafeByteStringOperations
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageReaderOperations
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.cinterop.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.math.min
import kotlin.native.Platform
import kotlin.native.ref.createCleaner

@OptIn(ExperimentalForeignApi::class)
internal interface DecoderInput : AutoCloseable {
    fun next(data: CPointer<CPointerVar<ByteVar>>, size: CPointer<IntVar>): Boolean
    fun backUp(count: Int)
    fun skip(count: Int): Boolean
    fun byteCount(): Long
}

private interface DecoderTarget {
    val input: DecoderInput
    val availableSize: Long
    fun closeNative(raw: CPointer<pw_decoder_t>)
    fun registerCleaner(raw: CPointer<pw_decoder_t>): Any?

    fun close(raw: CPointer<pw_decoder_t>) {
        try {
            // The CodedInputStream destructor may call backUp on the input.
            closeNative(raw)
        } finally {
            input.close()
        }
    }
}

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
private class BufferSourceTarget(private val source: Buffer) : DecoderTarget {
    override val input: DecoderInput = ZeroCopyInputSource(source)
    override val availableSize: Long get() = source.size
    override fun closeNative(raw: CPointer<pw_decoder_t>) {
        pw_decoder_close(raw)
    }
    override fun registerCleaner(raw: CPointer<pw_decoder_t>): Any = createCleaner(raw) { pw_decoder_delete(it) }
}

@OptIn(ExperimentalForeignApi::class)
private class NativeBufferTarget(base: CPointer<ByteVar>?, size: Int) : DecoderTarget {
    private val bufferInput = NativeBufferZeroCopyInput(base, size)
    override val input: DecoderInput = bufferInput
    override val availableSize: Long = size.toLong()
    val position: Int get() = bufferInput.position
    override fun closeNative(raw: CPointer<pw_decoder_t>) {
        pw_decoder_delete(raw)
    }
    override fun registerCleaner(raw: CPointer<pw_decoder_t>): Any? = null
}

@OptIn(ExperimentalForeignApi::class)
internal class WireDecoderNative private constructor(
    private val target: DecoderTarget,
) : WireDecoder {
    constructor(source: Buffer) : this(BufferSourceTarget(source))

    override var recursionDepth: Int = 0
    override var recursionLimit: Int = kotlinx.rpc.protobuf.ProtoConfig.DEFAULT_RECURSION_LIMIT

    // Keeps the callback target alive while the C++ decoder can access it.
    private val zeroCopyInput = StableRef.create(target.input)
    private var open = true

    // Bridge the CodedInputStream to either the Buffer or the scoped native buffer.
    private val rawPointer: CPointer<pw_decoder_t> = run {
        val zeroCopyCInput = cValue<pw_zero_copy_input> {
            ctx = zeroCopyInput.asCPointer()
            next = staticCFunction { ctx, data, size ->
                ctx!!.asStableRef<DecoderInput>().get().next(data!!.reinterpret(), size!!.reinterpret())
            }
            backUp = staticCFunction { ctx, count ->
                ctx!!.asStableRef<DecoderInput>().get().backUp(count)
            }
            skip = staticCFunction { ctx, count ->
                ctx!!.asStableRef<DecoderInput>().get().skip(count)
            }
            byteCount = staticCFunction { ctx ->
                ctx!!.asStableRef<DecoderInput>().get().byteCount()
            }
        }
        pw_decoder_new(zeroCopyCInput)
            ?: error("Failed to create proto wire decoder")
    }

    // Retaining the cleaner ties the Buffer decoder's native allocation to its lifetime.
    @Suppress("UnusedPrivateProperty")
    private val rawCleaner = target.registerCleaner(rawPointer)

    internal val raw: CPointer<pw_decoder_t>
        get() {
            check(open) { "The direct decoder has left its storage scope" }
            return rawPointer
        }

    override fun close() {
        if (!open) return
        open = false
        try {
            target.close(rawPointer)
        } finally {
            zeroCopyInput.dispose()
        }
    }

    override fun readTag(): KTag? = memScoped {
        val tag = alloc<UIntVar>()
        when (pw_decoder_read_validated_tag(raw, tag.ptr)) {
            0 -> null // end of stream or sub-message boundary
            1 -> KTag.from(tag.value)
            else -> throw ProtobufDecodingException.invalidTag(tag.value)
        }
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
            if (!pw_string_is_valid_utf8(str.value)) {
                throw ProtobufDecodingException.invalidUtf8()
            }
            return pw_string_c_str(str.value)?.toKString()
                ?: throw ProtobufDecodingException.genericParsingError()
        } finally {
            pw_string_delete(str.value)
        }
    }

    // TODO: Should readBytes return a buffer, to prevent allocation of large contiguous memory blocks ? KRPC-182
    @OptIn(UnsafeByteStringApi::class)
    override fun readBytes(): ByteString {
        val length = readInt32()
        if (length < 0) throw ProtobufDecodingException.negativeSize()
        // check if the remaining buffer size is less than the set length,
        // we can early abort, without allocating unnecessary memory
        if (target.availableSize < length) throw ProtobufDecodingException.truncatedMessage()
        if (length == 0) return ByteString() // actually an empty array (no error)
        val bytes = ByteArray(length)
        bytes.usePinned {
            pw_decoder_read_raw_bytes(raw, it.addressOf(0), length).checkError()
        }
        // return the ByteArray as ByteString
        return UnsafeByteStringOperations.wrapUnsafe(bytes)
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
        size = { target.availableSize },
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
        if (target.availableSize < byteLen) throw ProtobufDecodingException.truncatedMessage()
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

    companion object {
        fun <R> decodeDirect(reader: NativeBufferMessageReader, block: (WireDecoder) -> R): R {
            var result: Result<R>? = null
            if (reader.remaining == 0) {
                // The reader exposes no pointer for an empty message, but still enforces its lifetime.
                UnsafeGrpcMessageReaderOperations.readFromHead(reader) { _, _, _ -> 0 }
                val target = NativeBufferTarget(null, 0)
                WireDecoderNative(target).use { result = runCatching { block(it) } }
            } else {
                UnsafeGrpcMessageReaderOperations.readFromHead(reader) { base, start, end ->
                    val size = end - start
                    val target = NativeBufferTarget(base + start, size)
                    WireDecoderNative(target).use { decoder ->
                        result = runCatching { block(decoder) }
                    }
                    target.position
                }
            }
            return checkNotNull(result).getOrThrow()
        }
    }
}

@PublishedApi
internal fun <R> directDecode(reader: NativeBufferMessageReader, block: (WireDecoder) -> R): R =
    WireDecoderNative.decodeDirect(reader, block)

@OptIn(ExperimentalNativeApi::class)
private val ensureLittleEndian: Unit = require(Platform.isLittleEndian) {
    "kotlinx-rpc protobuf native implementation requires a little-endian platform"
}

/**
 * This constructor takes a [Source] (which must be a [Buffer]) because
 * the implementation ([WireDecoderNative]) depends on [Buffer]'s internal structure.
 */
public actual fun WireDecoder(source: Source): WireDecoder {
    ensureLittleEndian
    return WireDecoderNative(source as Buffer)
}

public actual inline fun checkForPlatformDecodeException(block: () -> Unit) {
    block()
}
