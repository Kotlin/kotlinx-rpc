/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlinx.cinterop.plus
import kotlinx.io.Sink
import platform.posix.memcpy

/**
 * The byte sink of a [WireEncoderNative].
 *
 * The C++ `CopyingOutputStream` calls [write] through a C callback.
 * An exception thrown from it cannot unwind through the C++ frames and terminates the process.
 */
internal interface EncoderOutput {
    /**
     * Writes [size] bytes from [buf], with [size] greater than zero; returns `false` to fail the encoding.
     */
    fun write(buf: CPointer<ByteVar>, size: Int): Boolean
}

/**
 * An output that appends to a [Sink].
 */
internal class BufferOutput(private val sink: Sink) : EncoderOutput {
    override fun write(buf: CPointer<ByteVar>, size: Int): Boolean {
        sink.writeFully(buf, 0L, size)
        return true
    }
}

/**
 * An output into a borrowed native buffer of [capacity] bytes. Valid only within its writer's buffer scope.
 */
@OptIn(UnsafeNumber::class)
internal class NativeBufferOutput(private val base: CPointer<ByteVar>?, private val capacity: Int) : EncoderOutput {
    var byteCount: Int = 0
        private set
    private var overflowed = false

    override fun write(buf: CPointer<ByteVar>, size: Int): Boolean {
        if (size > capacity - byteCount) {
            overflowed = true
            return false
        }
        memcpy(checkNotNull(base) + byteCount, buf, size.convert())
        byteCount += size
        return true
    }

    fun checkWithinCapacity(cause: Throwable? = null) {
        if (overflowed) {
            throw IllegalStateException("Protobuf encoding exceeded the declared size of $capacity bytes", cause)
        }
    }
}

