/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.protobuf.ProtobufDecodingException

/**
 * Reads protobuf wire primitives from [segments].
 *
 * [size] is the total number of bytes of the input. Positions are absolute, from 0 to [size].
 * Reads past [limit] throw [ProtobufDecodingException.truncatedMessage].
 */
internal class WireInput(private val segments: InputSegments, val size: Int) : AutoCloseable {
    private val window = ByteWindow()
    private var pos = 0                // read index in the attached segment
    private var segmentLength = 0      // length of the attached segment
    private var bytesBeforeSegment = 0 // bytes in the segments before the attached one
    private var readEnd = 0            // the fast paths read up to here, capped by the limit
    private var closed = false
    private var chars = CharArray(0)

    var limit: Int = size
        private set

    val position: Int get() = bytesBeforeSegment + pos
    val bytesUntilLimit: Int get() = limit - position
    val isAtLimit: Boolean get() = bytesBeforeSegment + pos == limit
    val isClosed: Boolean get() = closed

    /** Limits the input to the next [length] bytes and returns the old limit. */
    fun pushLimit(length: Int): Int {
        if (length < 0) throw ProtobufDecodingException.negativeSize()
        if (length > bytesUntilLimit) throw ProtobufDecodingException.truncatedMessage()
        val oldLimit = limit
        limit = position + length
        updateReadEnd()
        return oldLimit
    }

    /** Restores the [oldLimit] that [pushLimit] returned. */
    fun popLimit(oldLimit: Int) {
        limit = oldLimit
        updateReadEnd()
    }

    fun readByte(): Byte {
        if (pos == readEnd) {
            refill()
        }
        return window.getByte(pos++)
    }

    fun readVarint64(): Long {
        if (readEnd - pos >= Varint.MAX_SIZE_64) {
            var p = pos
            val value = Varint.decode { window.getByte(p++) }
            pos = p
            return value
        }
        return Varint.decode { readByte() }
    }

    /**
     * Reads a varint and keeps its low 32 bits.
     *
     * A negative `int32` has 10 bytes on the wire, so this reads up to 10 bytes. The same truncation applies to
     * lengths: a length of `2^32 + 5` becomes 5, as in protobuf-java.
     */
    fun readVarint32(): Int {
        return readVarint64().toInt()
    }

    fun readTagVarint(): Int {
        val start = position
        val value = readVarint64()
        if (value ushr 32 != 0L) {
            throw ProtobufDecodingException("Tag field number out of range")
        }
        if (position - start != Varint.sizeOf(UInt64(value))) {
            throw ProtobufDecodingException("Overlong varint encoding for tag")
        }
        return value.toInt()
    }

    fun readFixed32(): Int {
        if (readEnd - pos >= 4) {
            val b0 = window.getByte(pos).toInt() and 0xFF
            val b1 = window.getByte(pos + 1).toInt() and 0xFF
            val b2 = window.getByte(pos + 2).toInt() and 0xFF
            val b3 = window.getByte(pos + 3).toInt()
            pos += 4
            return b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)
        }
        return (readByte().toInt() and 0xFF) or
            ((readByte().toInt() and 0xFF) shl 8) or
            ((readByte().toInt() and 0xFF) shl 16) or
            (readByte().toInt() shl 24)
    }

    fun readFixed64(): Long {
        if (readEnd - pos >= 8) {
            var result = 0L
            for (i in 0 until 8) {
                val byte = window.getByte(pos + i).toLong() and 0xFF
                result = result or (byte shl (8 * i))
            }
            pos += 8
            return result
        }
        var result = 0L
        for (i in 0 until 8) {
            result = result or ((readByte().toLong() and 0xFF) shl (8 * i))
        }
        return result
    }

    /** Reads [count] bytes into a new array. The array has no more bytes than the input has. */
    fun readBytes(count: Int): ByteArray {
        checkCount(count)
        val result = ByteArray(count)
        var offset = 0
        while (offset < count) {
            if (pos == readEnd) {
                refill()
            }
            val chunk = minOf(readEnd - pos, count - offset)
            window.copyTo(result, offset, pos, chunk)
            pos += chunk
            offset += chunk
        }
        return result
    }

    fun readUtf8(count: Int): String {
        if (count == 0) {
            return ""
        }
        checkCount(count)
        if (count > chars.size) {
            chars = CharArray(count)
        }
        if (readEnd - pos >= count) {
            val result = Utf8.readUtf8(window, pos, count, chars)
            pos += count
            return result
        }
        val charCount = Utf8.decode(count, chars) { readByte() }
        return chars.concatToString(0, charCount)
    }

    fun skip(count: Int) {
        checkCount(count)
        var left = count
        while (left > 0) {
            if (pos == readEnd) {
                refill()
            }
            val chunk = minOf(readEnd - pos, left)
            pos += chunk
            left -= chunk
        }
    }

    /** Detaches the segment. The input is invalid after this call. Calling it again does nothing. */
    override fun close() {
        if (closed) {
            return
        }
        closed = true
        window.detach()
        pos = 0
        readEnd = 0
        segmentLength = 0
    }

    private fun refill() {
        check(!closed) { "The WireDecoder is used after its decoding scope ended." }
        if (position >= limit) throw ProtobufDecodingException.truncatedMessage()
        // Here pos == segmentLength, because position < limit.
        window.detach()
        bytesBeforeSegment += segmentLength
        pos = 0
        segmentLength = segments.next(window)
        if (segmentLength < 0) {
            segmentLength = 0
            readEnd = 0
            throw ProtobufDecodingException.truncatedMessage()
        }
        updateReadEnd()
    }

    private fun updateReadEnd() {
        readEnd = minOf(segmentLength, limit - bytesBeforeSegment)
    }

    private fun checkCount(count: Int) {
        if (count < 0) throw ProtobufDecodingException.negativeSize()
        if (count > bytesUntilLimit) throw ProtobufDecodingException.truncatedMessage()
    }
}
