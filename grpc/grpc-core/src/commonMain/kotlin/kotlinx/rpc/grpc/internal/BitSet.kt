/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

/**
 * A fixed-sized vector of bits, allowing one to set/clear/read bits from it by a bit index.
 */
public class BitSet(public val size: Int) {
    private val data: LongArray = LongArray((size + 63) ushr 6)

    /** Sets the bit at [index] to 1. */
    public fun set(index: Int) {
        require(index >= 0 && index < size) { "Index $index out of bounds for length $size" }
        val word = index ushr 6
        data[word] = data[word] or (1L shl (index and 63))
    }

    /** Clears the bit at [index] (sets to 0). */
    public fun clear(index: Int) {
        require(index >= 0 && index < size) { "Index $index out of bounds for length $size" }
        val word = index ushr 6
        data[word] = data[word] and (1L shl (index and 63)).inv()
    }

    /** Returns true if the bit at [index] is set. */
    public operator fun get(index: Int): Boolean {
        require(index >= 0 && index < size) { "Index $index out of bounds for length $size" }
        val word = index ushr 6
        return (data[word] ushr (index and 63) and 1L) != 0L
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
}