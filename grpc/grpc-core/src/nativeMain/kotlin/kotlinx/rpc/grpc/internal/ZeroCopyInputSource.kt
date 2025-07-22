/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.EOFException
import kotlinx.io.InternalIoApi
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations


/**
 * Handles (almost) zero-copy input operations on a [Buffer], allowing efficient transfer of data
 * without creating intermediate copies. This class provides mechanisms to iterate over
 * buffer data in a zero-copy manner and supports backing up, skipping, and advancing data.
 *
 * This class is intended for internal use only and specifically designed to be used
 * as a bridge between [Buffer] and the C++ protobuf `ZeroCopyInputStream`.
 * This implementation assumes that the data of a buffer segment is backed by a [ByteArray].
 *
 * The inner [Buffer] MUST NOT be accessed while using the [ZeroCopyInputSource] as it highly
 * depends on tracked state of [Buffer] internals. Each read or write to the underlying buffer
 * will result in undefined behavior.
 * Additionally, [ZeroCopyInputSource] is not thread-safe, so concurrent access might also
 * result in undefined behavior.
 *
 * Unlike [Buffer.readByte], the [ZeroCopyInputSource.next] does directly consume the data
 * in the [Buffer]. This has two reasons:
 * 1. The underlying [ByteArray] must stay valid after the `next()` call
 * 2. The [ZeroCopyInputSource.backUp] method might preserve bytes that where already read
 *    during the last [ZeroCopyInputSource.next] call. This method is required by the
 *    `ZeroCopyInputStream` interface of protobuf.
 * Because of this, the inner [Buffer] is in an invalid read position during the use of
 * [ZeroCopyInputSource]. After closing the [ZeroCopyInputSource] the inner [Buffer]
 * is valid again. However, the buffer might be further advanced than expected,
 * depending on whether the user called [backUp] for the unused bytes.
 *
 * The that memory received by a call to [ZeroCopyInputSource.next] is only valid until the next
 * invocation of any method of [ZeroCopyInputSource].
 *
 * @param inner The underlying [Buffer] to read data from. If must not be accessed while using
 *  [ZeroCopyInputSource].
 */
@OptIn(ExperimentalForeignApi::class, InternalIoApi::class, UnsafeIoApi::class)
internal class ZeroCopyInputSource(private val inner: Buffer) : AutoCloseable {

    // number of bytes read since construction
    private var byteCount = 0L
    // the array segment that was read by the latest call to next()
    // while it was already read by the ZeroCopyInputSource user, it is not yet
    // released by the buffer. this is done by releaseLatestReadSegement
    // which releases the segment in the inner buffer.
    private var latestReadSegementArray: Pinned<ByteArray>? = null

    /**
     * Get access to a segment of continuous bytes in the underlying [Buffer].
     * The returned memory gets invalid with a call to `next(), backUp(), skip()` or `close()`.
     * If the method returns `false`, if the inner buffer is exhausted. The `outData` and
     * `outSize` remain unset in this case.
     *
     * @return false if the buffer is exhausted, otherwise true
     */
    fun next(outData: CPointer<CPointerVar<ByteVar>>, outSize: CPointer<IntVar>): Boolean {
        if (latestReadSegementArray != null) {
            // if there is some unreleased segment array, we must release it first.
            // this will advance the head of the buffer to the correct position.
            releaseLatestReadSegment()
        }
        if (inner.exhausted()) {
            return false
        }
        // perform access to the underlying array of the buffer's current segment
        UnsafeBufferOperations.readFromHead(inner.buffer) { arr, start, end ->
            check(latestReadSegementArray == null) { "currArr must be null at this point"}
            // fix the array so it does not move in memory, which is important as we pass its
            // memory address as a result to the caller.
            latestReadSegementArray = arr.pin()

            val segmentSize = end - start
            outData.pointed.value = latestReadSegementArray!!.addressOf(start)
            outSize.pointed.value = segmentSize;

            byteCount += segmentSize;

            // we are not yet advancing the inner buffer head.
            // this ensures that the segment array is not released by the buffer and remains valid
            0
        }
        return true;
    }

    /**
     * Allows to replay [count] many bytes of the previously read segment.
     * This is useful when writing procedures that are only supposed to read up
     * to a certain point in the input, then return. If [next] returns a
     * buffer that goes beyond what you wanted to read, you can use [backUp]
     * to return to the point where you intended to finish.
     * ```kt
     * next(...)  // access the current buffer segment
     * backUp(10) // back up the last 10 bytes of the previous accessed segment
     * next(...)  // read the 10 last bytes of the previous accessed segment again
     * ```
     * This is only possible if [next] was the last method called.
     *
     */
    fun backUp(count: Int) {
        check(latestReadSegementArray != null) { "next() must be immediately before backUp()" }
        releaseLatestReadSegment(count)
        byteCount -= count;
    }

    /**
     * Skip [count] bytes of the buffer.
     * @return `false` iff the buffer is exhausted before skipping completed, `true` otherwise
     */
    fun skip(count: Int): Boolean {
        if (latestReadSegementArray != null) {
            releaseLatestReadSegment(count)
        }
        try {
            byteCount += count
            inner.skip(count.toLong())
            return true
        } catch (_: EOFException) {
            return false
        }
    }

    /**
     * The number of bytes read since the object got created.
     * If [backUp] is called, it will decrement the number of read bytes by the given amount.
     */
    fun byteCount(): Long {
        return byteCount
    }

    /**
     * Releases the latest read segment that was not yet released.
     * It won't close the underlying [Buffer]. After closing this, the underlying [Buffer] is
     * valid and can be used again.
     *
     * This [ZeroCopyInputSource] must not be used after closing it.
     */
    override fun close() {
        if (latestReadSegementArray != null) {
            releaseLatestReadSegment()
        }
    }

    /**
     * Releases the segment that was previously read using [next] but not yet released by the buffer.
     * It also unpins it, so it can be collected by the GC. This must only be called if [next] was previously called.
     *
     * The [backUpCount] defines how many bytes of the segment should stay valid (not released). This is used by the
     * [backUp] to allow users to replay reading of the latest read segment.
     */
    private fun releaseLatestReadSegment(backUpCount: Int = 0) {
        check(latestReadSegementArray != null) { "currArr must be not null" }
        // the return value of the readFromHead defines the number of bytes that are getting released in the underlying
        // buffer.
        UnsafeBufferOperations.readFromHead(inner.buffer) { arr, start, end ->
            check(latestReadSegementArray?.get() == arr) {
                "array to advance must be the SAME as the currArr, was there some access to the underlying buffer?" }
            // release the whole segmentSize - the backup count.
            // prevent the value from being negative.
            val read = maxOf(end - start - backUpCount, 0)
            read
        }
        // remove tracking of the released segment
        latestReadSegementArray?.unpin()
        latestReadSegementArray = null;
    }
}