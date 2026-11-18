/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.io.UnsafeIoApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.protobuf.internal.ByteWindow
import kotlinx.rpc.protobuf.internal.InputSegments
import kotlinx.rpc.protobuf.internal.OutputSegments
import kotlinx.rpc.protobuf.internal.destinationFull

/** Gives [bytes] to the codec in segments of [chunkSize] bytes. */
internal class SplitInputSegments(
    private val bytes: ByteArray,
    private val chunkSize: Int,
) : InputSegments {
    private var position = 0

    override fun next(window: ByteWindow): Int {
        if (position == bytes.size) {
            return -1
        }
        window.attach(bytes, position)
        val count = minOf(chunkSize, bytes.size - position)
        position += count
        return count
    }
}

/** Takes the committed bytes in segments of [chunkSize] bytes, with [capacity] bytes in total. */
internal class SplitOutputSegments(
    private val capacity: Int,
    private val chunkSize: Int,
) : OutputSegments {
    private val storage = ByteArray(capacity)
    private var committed = 0

    override fun next(window: ByteWindow): Int {
        if (committed == capacity) {
            throw destinationFull(capacity)
        }
        window.attach(storage, committed)
        return minOf(chunkSize, capacity - committed)
    }

    override fun commit(count: Int) {
        committed += count
    }

    /** The committed bytes. */
    fun bytes(): ByteArray = storage.copyOf(committed)
}

/** A [Buffer] with one kotlinx-io segment for each chunk of [chunkSize] bytes. */
internal fun bufferOf(bytes: ByteArray, chunkSize: Int): Buffer {
    val buffer = Buffer()
    var position = 0
    while (position < bytes.size) {
        val count = minOf(chunkSize, bytes.size - position)
        val chunk = ByteArray(count)
        bytes.copyInto(chunk, 0, position, position + count)
        UnsafeBufferOperations.moveToTail(buffer, chunk)
        position += count
    }
    return buffer
}
