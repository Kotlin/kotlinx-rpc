/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.utils.BitSet
import kotlin.test.*

class BitSetTest {

    @Test
    fun testConstructor() {
        // Test with size 0
        val bitSet0 = BitSet(0)
        assertEquals(0, bitSet0.size)
        assertEquals(0, bitSet0.cardinality())

        // Test with small size
        val bitSet10 = BitSet(10)
        assertEquals(10, bitSet10.size)
        assertEquals(0, bitSet10.cardinality())

        // Test with size that spans multiple words
        val bitSet100 = BitSet(100)
        assertEquals(100, bitSet100.size)
        assertEquals(0, bitSet100.cardinality())

        // Test with size at word boundary
        val bitSet64 = BitSet(64)
        assertEquals(64, bitSet64.size)
        assertEquals(0, bitSet64.cardinality())

        // Test with size just over word boundary
        val bitSet65 = BitSet(65)
        assertEquals(65, bitSet65.size)
        assertEquals(0, bitSet65.cardinality())
    }

    @Test
    fun testSetAndGet() {
        val bitSet = BitSet(100)

        // Initially all bits should be unset
        for (i in 0 until 100) {
            assertFalse(bitSet[i], "Bit $i should be initially unset")
        }

        // Set some bits
        bitSet.set(0)
        bitSet.set(1)
        bitSet.set(63)
        bitSet.set(64)
        bitSet.set(99)

        // Verify the bits are set
        assertTrue(bitSet[0], "Bit 0 should be set")
        assertTrue(bitSet[1], "Bit 1 should be set")
        assertTrue(bitSet[63], "Bit 63 should be set")
        assertTrue(bitSet[64], "Bit 64 should be set")
        assertTrue(bitSet[99], "Bit 99 should be set")

        // Verify other bits are still unset
        assertFalse(bitSet[2], "Bit 2 should be unset")
        assertFalse(bitSet[62], "Bit 62 should be unset")
        assertFalse(bitSet[65], "Bit 65 should be unset")
        assertFalse(bitSet[98], "Bit 98 should be unset")
    }

    @Test
    fun testClear() {
        val bitSet = BitSet(100)

        // Set all bits
        for (i in 0 until 100) {
            bitSet.set(i)
        }

        // Verify all bits are set
        for (i in 0 until 100) {
            assertTrue(bitSet[i], "Bit $i should be set")
        }

        // Clear some bits
        bitSet.clear(0)
        bitSet.clear(1)
        bitSet.clear(63)
        bitSet.clear(64)
        bitSet.clear(99)

        // Verify the bits are cleared
        assertFalse(bitSet[0], "Bit 0 should be cleared")
        assertFalse(bitSet[1], "Bit 1 should be cleared")
        assertFalse(bitSet[63], "Bit 63 should be cleared")
        assertFalse(bitSet[64], "Bit 64 should be cleared")
        assertFalse(bitSet[99], "Bit 99 should be cleared")

        // Verify other bits are still set
        assertTrue(bitSet[2], "Bit 2 should still be set")
        assertTrue(bitSet[62], "Bit 62 should still be set")
        assertTrue(bitSet[65], "Bit 65 should still be set")
        assertTrue(bitSet[98], "Bit 98 should still be set")
    }

    @Test
    fun testClearAll() {
        val bitSet = BitSet(100)

        // Set all bits
        for (i in 0 until 100) {
            bitSet.set(i)
        }

        // Verify all bits are set
        for (i in 0 until 100) {
            assertTrue(bitSet[i], "Bit $i should be set")
        }

        // Clear all bits
        bitSet.clearAll()

        // Verify all bits are cleared
        for (i in 0 until 100) {
            assertFalse(bitSet[i], "Bit $i should be cleared after clearAll")
        }
    }

    @Test
    fun testCardinality() {
        val bitSet = BitSet(100)
        assertEquals(0, bitSet.cardinality(), "Initial cardinality should be 0")

        // Set some bits
        bitSet.set(0)
        assertEquals(1, bitSet.cardinality(), "Cardinality should be 1 after setting 1 bit")

        bitSet.set(63)
        assertEquals(2, bitSet.cardinality(), "Cardinality should be 2 after setting 2 bits")

        bitSet.set(64)
        assertEquals(3, bitSet.cardinality(), "Cardinality should be 3 after setting 3 bits")

        bitSet.set(99)
        assertEquals(4, bitSet.cardinality(), "Cardinality should be 4 after setting 4 bits")

        // Clear a bit
        bitSet.clear(0)
        assertEquals(3, bitSet.cardinality(), "Cardinality should be 3 after clearing 1 bit")

        // Set a bit that's already set
        bitSet.set(63)
        assertEquals(3, bitSet.cardinality(), "Cardinality should still be 3 after setting an already set bit")

        // Clear all bits
        bitSet.clearAll()
        assertEquals(0, bitSet.cardinality(), "Cardinality should be 0 after clearAll")
    }

    @Test
    fun testAllSet() {
        // Test with empty BitSet
        val emptyBitSet = BitSet(0)
        assertTrue(emptyBitSet.allSet(), "Empty BitSet should return true for allSet")

        // Test with small BitSet
        val smallBitSet = BitSet(5)
        assertFalse(smallBitSet.allSet(), "New BitSet should return false for allSet")

        smallBitSet.set(0)
        smallBitSet.set(1)
        smallBitSet.set(2)
        smallBitSet.set(3)
        smallBitSet.set(4)
        assertTrue(smallBitSet.allSet(), "BitSet with all bits set should return true for allSet")

        smallBitSet.clear(2)
        assertFalse(smallBitSet.allSet(), "BitSet with one bit cleared should return false for allSet")

        // Test with BitSet that spans multiple words
        val largeBitSet = BitSet(100)
        assertFalse(largeBitSet.allSet(), "New large BitSet should return false for allSet")

        for (i in 0 until 100) {
            largeBitSet.set(i)
        }
        assertTrue(largeBitSet.allSet(), "Large BitSet with all bits set should return true for allSet")

        largeBitSet.clear(63)
        assertFalse(largeBitSet.allSet(), "Large BitSet with one bit cleared should return false for allSet")

        // Test with BitSet at word boundary
        val wordBoundaryBitSet = BitSet(64)
        assertFalse(wordBoundaryBitSet.allSet(), "New word boundary BitSet should return false for allSet")

        for (i in 0 until 64) {
            wordBoundaryBitSet.set(i)
        }
        assertTrue(wordBoundaryBitSet.allSet(), "Word boundary BitSet with all bits set should return true for allSet")
    }

    @Test
    fun testEdgeCases() {
        val bitSet = BitSet(100)

        // Test setting and getting at boundaries
        bitSet.set(0)
        assertTrue(bitSet[0], "Should be able to set and get bit 0")

        bitSet.set(99)
        assertTrue(bitSet[99], "Should be able to set and get bit at size-1")

        // Test clearing at boundaries
        bitSet.clear(0)
        assertFalse(bitSet[0], "Should be able to clear bit 0")

        bitSet.clear(99)
        assertFalse(bitSet[99], "Should be able to clear bit at size-1")

        // Test out of bounds access
        assertFailsWith<IllegalArgumentException> {
            bitSet.set(100)
        }

        assertFailsWith<IllegalArgumentException> {
            bitSet.clear(100)
        }

        assertFailsWith<IllegalArgumentException> {
            bitSet[100]
        }

        assertFailsWith<IllegalArgumentException> {
            bitSet.set(-1)
        }

        assertFailsWith<IllegalArgumentException> {
            bitSet.clear(-1)
        }

        assertFailsWith<IllegalArgumentException> {
            bitSet[-1]
        }
    }

    @Test
    fun testWordBoundaries() {
        // Test BitSet with size at word boundaries
        for (size in listOf(63, 64, 65, 127, 128, 129)) {
            val bitSet = BitSet(size)

            // Set all bits
            for (i in 0 until size) {
                bitSet.set(i)
            }

            // Verify all bits are set
            for (i in 0 until size) {
                assertTrue(bitSet[i], "Bit $i should be set in BitSet of size $size")
            }

            // Verify cardinality
            assertEquals(size, bitSet.cardinality(), "Cardinality should equal size for fully set BitSet")

            // Verify allSet
            assertTrue(bitSet.allSet(), "allSet should return true for fully set BitSet")

            // Clear all bits
            bitSet.clearAll()

            // Verify all bits are cleared
            for (i in 0 until size) {
                assertFalse(bitSet[i], "Bit $i should be cleared in BitSet of size $size after clearAll")
            }

            // Verify cardinality
            assertEquals(0, bitSet.cardinality(), "Cardinality should be 0 after clearAll")

            // Verify allSet
            assertFalse(bitSet.allSet(), "allSet should return false after clearAll")
        }
    }

    @Test
    fun testLargeCardinality() {
        // Test with a large BitSet to verify cardinality calculation
        val size = 1000
        val bitSet = BitSet(size)

        // Set every other bit
        for (i in 0 until size step 2) {
            bitSet.set(i)
        }

        // Verify cardinality
        assertEquals(size / 2, bitSet.cardinality(), "Cardinality should be half the size when every other bit is set")

        // Set all bits
        for (i in 0 until size) {
            bitSet.set(i)
        }

        // Verify cardinality
        assertEquals(size, bitSet.cardinality(), "Cardinality should equal size when all bits are set")
    }
}