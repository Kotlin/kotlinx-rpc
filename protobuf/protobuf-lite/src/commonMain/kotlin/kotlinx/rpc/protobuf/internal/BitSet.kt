/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.countOneBits

/**
 * A fixed-sized vector of bits, allowing one to set/clear/read bits from it by a bit index.
 */
@InternalRpcApi
public class BitSet(public val size: Int) {
    private val data: LongArray = LongArray((size + 63) ushr 6)

    /** Sets the bit at [index] to 1. */
    public operator fun set(index: Int, value: Boolean) {
        if (!value) return clear(index)
        require(index in 0 until size) { "Index $index out‑of‑bounds for length $size" }
        val word = index ushr 6
        val mask = 1L shl (index and 63)
        data[word] = data[word] or mask
    }

    /** Clears the bit at [index] (sets to 0). */
    public fun clear(index: Int) {
        require(index in 0..<size) { "Index $index out of bounds for length $size" }
        val word = index ushr 6
        data[word] = data[word] and (1L shl (index and 63)).inv()
    }

    /** Returns true if the bit at [index] is set. */
    public operator fun get(index: Int): Boolean {
        require(index in 0..<size) { "Index $index out of bounds for length $size" }
        val word = index ushr 6
        return (data[word] ushr (index and 63) and 1L) != 0L
    }

    /**
     * Clears every bit in the inclusive range [from]..[to].
     * Used by generated oneof setters to reset all sibling presence bits at once.
     */
    public fun clearRange(from: Int, to: Int) {
        require(from in 0..to && to < size) { "Range $from..$to out of bounds for length $size" }
        var i = from
        while (i <= to) {
            val word = i ushr 6
            val startBit = i and 63
            val endBit = if ((to ushr 6) == word) (to and 63) else 63
            data[word] = data[word] and rangeMask(startBit, endBit).inv()
            i = (word + 1) shl 6
        }
    }

    /**
     * Clears every bit in the inclusive range [from]..[to] and then sets the bit at [index].
     * [index] must lie within the range. This is a single mask operation when the range fits in one word.
     */
    public fun setExclusive(index: Int, from: Int, to: Int) {
        require(index in from..to) { "Index $index is not within range $from..$to" }
        val word = index ushr 6
        if ((from ushr 6) == word && (to ushr 6) == word) {
            require(to < size) { "Range $from..$to out of bounds for length $size" }
            val mask = rangeMask(from and 63, to and 63)
            data[word] = (data[word] and mask.inv()) or (1L shl (index and 63))
        } else {
            clearRange(from, to)
            set(index, true)
        }
    }

    /** Returns true if any bit in the inclusive range [from]..[to] is set. */
    public fun anySet(from: Int, to: Int): Boolean {
        require(from in 0..to && to < size) { "Range $from..$to out of bounds for length $size" }
        var i = from
        while (i <= to) {
            val word = i ushr 6
            val startBit = i and 63
            val endBit = if ((to ushr 6) == word) (to and 63) else 63
            if (data[word] and rangeMask(startBit, endBit) != 0L) return true
            i = (word + 1) shl 6
        }
        return false
    }

    /** Mask with bits [startBit]..[endBit] (inclusive, both within 0..63) set. */
    private fun rangeMask(startBit: Int, endBit: Int): Long {
        val count = endBit - startBit + 1
        return if (count == 64) -1L else ((1L shl count) - 1) shl startBit
    }

    /** Clears all bits. */
    public fun clearAll() {
        data.fill(0L)
    }

    /** Returns the number of bits set to 1. */
    public fun cardinality(): Int {
        var sum = 0
        for (w in data) {
            sum += w.countOneBits()
        }
        return sum
    }

    /** Returns true if all bits are set. */
    public fun allSet(): Boolean {
        val fullWords = size ushr 6
        // check full 64-bit words
        for (i in 0 until fullWords) {
            if (data[i] != -1L) return false
        }
        // check leftover bits
        val rem = size and 63
        if (rem != 0) {
            val mask = (-1L ushr (64 - rem))
            if (data[fullWords] != mask) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BitSet

        if (size != other.size) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + data.contentHashCode()
        return result
    }
}
