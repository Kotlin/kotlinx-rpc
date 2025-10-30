/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.InternalIoApi
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.internal.utils.InternalRpcApi
import libkgrpc.grpc_byte_buffer
import libkgrpc.grpc_byte_buffer_reader
import libkgrpc.grpc_byte_buffer_reader_destroy
import libkgrpc.grpc_byte_buffer_reader_init
import libkgrpc.grpc_byte_buffer_reader_next
import libkgrpc.grpc_metadata
import libkgrpc.grpc_metadata_array
import libkgrpc.grpc_raw_byte_buffer_create
import libkgrpc.grpc_slice
import libkgrpc.grpc_slice_from_copied_buffer
import libkgrpc.grpc_slice_from_copied_string
import libkgrpc.grpc_slice_malloc
import libkgrpc.grpc_slice_unref
import libkgrpc.grpc_status_code
import platform.posix.memcpy

@InternalRpcApi
public fun internalError(message: String): Nothing {
    error("Unexpected internal error: $message. Please, report the issue here: https://github.com/Kotlin/kotlinx-rpc/issues/new?template=bug_report.md")
}


@InternalRpcApi
@OptIn(ExperimentalForeignApi::class, InternalIoApi::class, UnsafeIoApi::class)
public fun Sink.writeFully(buffer: CPointer<ByteVar>, offset: Long, length: Long) {
    var consumed = 0L
    while (consumed < length) {
        UnsafeBufferOperations.writeToTail(this.buffer, 1) { array, start, endExclusive ->
            val size = minOf(length - consumed, (endExclusive - start).toLong())

            array.usePinned {
                memcpy(it.addressOf(start), buffer + offset + consumed, size.convert())
            }

            consumed += size
            size.toInt()
        }
    }
}

@InternalRpcApi
public fun grpc_slice.toByteArray(): ByteArray = memScoped {
    val out = ByteArray(len().toInt())
    if (out.isEmpty()) return out

    out.usePinned {
        memcpy(it.addressOf(0), startPtr(), len().convert())
    }
    return out
}


@InternalRpcApi
public fun CPointer<grpc_byte_buffer>.toKotlin(): Buffer = memScoped {
    val reader = alloc<grpc_byte_buffer_reader>()
    check(grpc_byte_buffer_reader_init(reader.ptr, this@toKotlin) == 1)
    { internalError("Failed to initialized byte buffer.") }

    val out = Buffer()
    val slice = alloc<grpc_slice>()
    while (grpc_byte_buffer_reader_next(reader.ptr, slice.ptr) != 0) {
        val dataPtr = slice.startPtr()
        val len = slice.len()

        out.writeFully(dataPtr, 0, len.convert())
        grpc_slice_unref(slice.readValue())
    }

    grpc_byte_buffer_reader_destroy(reader.ptr)
    return out
}

@InternalRpcApi
public fun Source.toGrpcByteBuffer(): CPointer<grpc_byte_buffer> {
    if (this is Buffer) return toGrpcByteBuffer()

    val tmp = ByteArray(8192)
    val slices = ArrayList<CValue<grpc_slice>>(4)

    while (true) {
        val n = readAtMostTo(tmp, 0, tmp.size)
        if (n <= 0) break
        tmp.usePinned {
            slices += grpc_slice_from_copied_buffer(it.addressOf(0), n.toULong())
        }
    }

    return slices.toGrpcByteBuffer()
}

@InternalRpcApi
@OptIn(UnsafeIoApi::class)
public fun Buffer.toGrpcByteBuffer(): CPointer<grpc_byte_buffer> {
    val slices = ArrayList<CValue<grpc_slice>>(4)

    while (size > 0L) {
        UnsafeBufferOperations.readFromHead(this) { arr, start, end ->
            val len = end - start
            arr.usePinned { p -> slices += grpc_slice_from_copied_buffer(p.addressOf(start), len.toULong()) }
            len
        }
    }

    return slices.toGrpcByteBuffer()
}

private fun ArrayList<CValue<grpc_slice>>.toGrpcByteBuffer(): CPointer<grpc_byte_buffer> = memScoped {
    val count = if (isEmpty()) 1 else size
    val sliceArr = allocArray<grpc_slice>(count)
    val base = sliceArr.reinterpret<ByteVar>()
    val stride = sizeOf<grpc_slice>()

    if (isEmpty()) {
        val dst = base /* + 0*stride */
        val empty = grpc_slice_malloc(0u)
        empty.useContents { memcpy(dst, ptr, stride.convert()) }
    } else {
        for (i in 0 until count) {
            val dst = base + i * stride     // <-- important: advance by i*size
            this@toGrpcByteBuffer[i].useContents { memcpy(dst, ptr, stride.convert()) }
        }
    }


    val buf = grpc_raw_byte_buffer_create(sliceArr, count.toULong())!!
    // unref each slice, as the buffer takes ownership
    this@toGrpcByteBuffer.forEach { grpc_slice_unref(it) }

    return buf
}

@InternalRpcApi
public fun grpc_slice.startPtr(): CPointer<ByteVar> {
    return if (this.refcount != null) {
        this.data.refcounted.bytes!!.reinterpret()
    } else {
        this.data.inlined.bytes.reinterpret()
    }
}

@InternalRpcApi
public fun grpc_slice.len(): ULong {
    return if (this.refcount != null) {
        this.data.refcounted.length
    } else {
        this.data.inlined.length.convert()
    }
}

@InternalRpcApi
public fun String.toGrpcSlice(): CValue<grpc_slice> {
    return grpc_slice_from_copied_string(this)
}

@InternalRpcApi
@Suppress("REDUNDANT_ELSE_IN_WHEN") // compilation fails without else case, but gives warning with
public fun grpc_status_code.toKotlin(): StatusCode = when (this) {
    grpc_status_code.GRPC_STATUS_OK -> StatusCode.OK
    grpc_status_code.GRPC_STATUS_CANCELLED -> StatusCode.CANCELLED
    grpc_status_code.GRPC_STATUS_UNKNOWN -> StatusCode.UNKNOWN
    grpc_status_code.GRPC_STATUS_INVALID_ARGUMENT -> StatusCode.INVALID_ARGUMENT
    grpc_status_code.GRPC_STATUS_DEADLINE_EXCEEDED -> StatusCode.DEADLINE_EXCEEDED
    grpc_status_code.GRPC_STATUS_NOT_FOUND -> StatusCode.NOT_FOUND
    grpc_status_code.GRPC_STATUS_ALREADY_EXISTS -> StatusCode.ALREADY_EXISTS
    grpc_status_code.GRPC_STATUS_PERMISSION_DENIED -> StatusCode.PERMISSION_DENIED
    grpc_status_code.GRPC_STATUS_RESOURCE_EXHAUSTED -> StatusCode.RESOURCE_EXHAUSTED
    grpc_status_code.GRPC_STATUS_FAILED_PRECONDITION -> StatusCode.FAILED_PRECONDITION
    grpc_status_code.GRPC_STATUS_ABORTED -> StatusCode.ABORTED
    grpc_status_code.GRPC_STATUS_OUT_OF_RANGE -> StatusCode.OUT_OF_RANGE
    grpc_status_code.GRPC_STATUS_UNIMPLEMENTED -> StatusCode.UNIMPLEMENTED
    grpc_status_code.GRPC_STATUS_INTERNAL -> StatusCode.INTERNAL
    grpc_status_code.GRPC_STATUS_UNAVAILABLE -> StatusCode.UNAVAILABLE
    grpc_status_code.GRPC_STATUS_DATA_LOSS -> StatusCode.DATA_LOSS
    grpc_status_code.GRPC_STATUS_UNAUTHENTICATED -> StatusCode.UNAUTHENTICATED
    grpc_status_code.GRPC_STATUS__DO_NOT_USE -> error("Invalid status code: $this")
    else -> error("Invalid status code: $this")
}

@InternalRpcApi
public fun StatusCode.toRaw(): grpc_status_code = when (this) {
    StatusCode.OK -> grpc_status_code.GRPC_STATUS_OK
    StatusCode.CANCELLED -> grpc_status_code.GRPC_STATUS_CANCELLED
    StatusCode.UNKNOWN -> grpc_status_code.GRPC_STATUS_UNKNOWN
    StatusCode.INVALID_ARGUMENT -> grpc_status_code.GRPC_STATUS_INVALID_ARGUMENT
    StatusCode.DEADLINE_EXCEEDED -> grpc_status_code.GRPC_STATUS_DEADLINE_EXCEEDED
    StatusCode.NOT_FOUND -> grpc_status_code.GRPC_STATUS_NOT_FOUND
    StatusCode.ALREADY_EXISTS -> grpc_status_code.GRPC_STATUS_ALREADY_EXISTS
    StatusCode.PERMISSION_DENIED -> grpc_status_code.GRPC_STATUS_PERMISSION_DENIED
    StatusCode.RESOURCE_EXHAUSTED -> grpc_status_code.GRPC_STATUS_RESOURCE_EXHAUSTED
    StatusCode.FAILED_PRECONDITION -> grpc_status_code.GRPC_STATUS_FAILED_PRECONDITION
    StatusCode.ABORTED -> grpc_status_code.GRPC_STATUS_ABORTED
    StatusCode.OUT_OF_RANGE -> grpc_status_code.GRPC_STATUS_OUT_OF_RANGE
    StatusCode.UNIMPLEMENTED -> grpc_status_code.GRPC_STATUS_UNIMPLEMENTED
    StatusCode.INTERNAL -> grpc_status_code.GRPC_STATUS_INTERNAL
    StatusCode.UNAVAILABLE -> grpc_status_code.GRPC_STATUS_UNAVAILABLE
    StatusCode.DATA_LOSS -> grpc_status_code.GRPC_STATUS_DATA_LOSS
    StatusCode.UNAUTHENTICATED -> grpc_status_code.GRPC_STATUS_UNAUTHENTICATED
}

@InternalRpcApi
public fun grpc_metadata.destroy() {
    grpc_slice_unref(key.readValue())
    grpc_slice_unref(value.readValue())
}

@InternalRpcApi
public fun grpc_metadata_array.destroyEntries() {
    for (i in 0 until count.convert()) {
        metadata?.get(i)?.destroy()
    }
}