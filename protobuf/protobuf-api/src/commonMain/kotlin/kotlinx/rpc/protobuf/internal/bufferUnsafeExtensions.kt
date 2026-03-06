/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.io.InternalIoApi
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations

/**
 * Consumes this [Buffer] and invokes [handler] for every contiguous byte range
 * in the underlying segments without copying.
 *
 * The handler receives the backing [ByteArray] together with the valid range
 * `[startIndex, endIndexExclusive)` containing readable bytes.
 *
 * The buffer is fully consumed after this call. The passed array must be
 * treated as read-only and must not be stored beyond the scope of [handler],
 * as the underlying storage may be reused by the buffer implementation.
 *
 * This function performs zero-copy iteration over the buffer's internal
 * segments.
 */
@OptIn(InternalIoApi::class, UnsafeIoApi::class)
internal inline fun Buffer.readFully(handler: (bytes: ByteArray, startIdx: Int, endIndexExclusive: Int) -> Unit) {
    while (!this.exhausted()) {
        UnsafeBufferOperations.readFromHead(this) { array, start, endIndexExclusive ->
            handler(array, start, endIndexExclusive)
            endIndexExclusive - start
        }
    }
}
