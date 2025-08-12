/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.*
import kotlinx.io.InternalIoApi
import kotlinx.io.Sink
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import platform.posix.memcpy


@OptIn(ExperimentalForeignApi::class, InternalIoApi::class, UnsafeIoApi::class)
internal fun Sink.writeFully(buffer: CPointer<ByteVar>, offset: Long, length: Long) {
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
