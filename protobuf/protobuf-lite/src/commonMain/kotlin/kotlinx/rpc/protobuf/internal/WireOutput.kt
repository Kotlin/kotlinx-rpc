/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.io.UnsafeIoApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.io.unsafe.withData

/** Writes protobuf wire primitives to [segments]. */
internal class WireOutput(private val segments: OutputSegments) : AutoCloseable {
    private val window = ByteWindow()
    private var pos = 0        // write index in the attached segment
    private var end = 0        // capacity of the attached segment
    private var committed = 0  // bytes in the segments before the attached one
    private var closed = false

    /** The number of bytes written so far. */
    val written: Int get() = committed + pos

    fun writeByte(value: Byte) {
        if (pos == end) {
            nextSegment()
        }
        window.setByte(pos++, value)
    }

    fun writeVarint(value: UInt32) {
        if (end - pos >= Varint.MAX_SIZE_32) {
            var p = pos
            Varint.encode(value) { window.setByte(p++, it) }
            pos = p
        } else {
            Varint.encode(value) { writeByte(it) }
        }
    }

    fun writeVarint(value: UInt64) {
        if (end - pos >= Varint.MAX_SIZE_64) {
            var p = pos
            Varint.encode(value) { window.setByte(p++, it) }
            pos = p
        } else {
            Varint.encode(value) { writeByte(it) }
        }
    }

    fun writeVarint(value: Int32) {
        writeVarint(Varint.bits(value))
    }

    fun writeVarint(value: Int64) {
        writeVarint(Varint.bits(value))
    }

    fun writeVarint(value: SInt32) {
        writeVarint(Varint.bits(value))
    }

    fun writeVarint(value: SInt64) {
        writeVarint(Varint.bits(value))
    }

    fun writeFixed32(value: Int) {
        if (end - pos >= 4) {
            window.setByte(pos, (value and 0xFF).toByte())
            window.setByte(pos + 1, ((value ushr 8) and 0xFF).toByte())
            window.setByte(pos + 2, ((value ushr 16) and 0xFF).toByte())
            window.setByte(pos + 3, ((value ushr 24) and 0xFF).toByte())
            pos += 4
        } else {
            writeByte((value and 0xFF).toByte())
            writeByte(((value ushr 8) and 0xFF).toByte())
            writeByte(((value ushr 16) and 0xFF).toByte())
            writeByte(((value ushr 24) and 0xFF).toByte())
        }
    }

    fun writeFixed64(value: Long) {
        if (end - pos >= 8) {
            for (i in 0 until 8) {
                window.setByte(pos + i, ((value ushr (8 * i)) and 0xFF).toByte())
            }
            pos += 8
        } else {
            for (i in 0 until 8) {
                writeByte(((value ushr (8 * i)) and 0xFF).toByte())
            }
        }
    }

    fun writeBytes(source: ByteArray, offset: Int, length: Int) {
        checkOpen()
        var written = 0
        while (written < length) {
            if (pos == end) {
                nextSegment()
            }
            val chunk = minOf(end - pos, length - written)
            window.copyFrom(source, offset + written, pos, chunk)
            pos += chunk
            written += chunk
        }
    }

    /** Appends all bytes of [source] to the segments. Doesn't change [source]. */
    fun writeBuffer(source: Buffer) {
        checkOpen()
        UnsafeBufferOperations.forEachSegment(source) { context, segment ->
            context.withData(segment) { bytes, from, to -> writeBytes(bytes, from, to - from) }
        }
    }

    /** Writes [value], which has [length] bytes in UTF-8. The caller has the length from [Utf8.encodedLength]. */
    fun writeUtf8(value: String, length: Int) {
        if (end - pos >= length) {
            var p = pos
            Utf8.encode(value) { window.setByte(p++, it) }
            pos = p
        } else {
            Utf8.encode(value) { writeByte(it) }
        }
    }

    /** Gives the written bytes to the destination and detaches the segment. */
    fun flush() {
        checkOpen()
        window.detach()
        segments.commit(pos)
        committed += pos
        pos = 0
        end = 0
    }

    /** Detaches the segment without a commit. The output is invalid after this call. Calling it again does nothing. */
    override fun close() {
        if (closed) {
            return
        }
        closed = true
        window.detach()
        pos = 0
        end = 0
    }

    private fun nextSegment() {
        checkOpen()
        window.detach()
        segments.commit(pos)
        committed += pos
        pos = 0
        end = segments.next(window)
    }

    private fun checkOpen() {
        check(!closed) { "The WireEncoder is used after its encoding scope ended." }
    }
}
