/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.protobuf.internal.BooleanArrayList
import kotlinx.rpc.protobuf.internal.DoubleArrayList
import kotlinx.rpc.protobuf.internal.FloatArrayList
import kotlinx.rpc.protobuf.internal.IntArrayList
import kotlinx.rpc.protobuf.internal.LongArrayList
import kotlinx.rpc.protobuf.internal.UIntArrayList
import kotlinx.rpc.protobuf.internal.ULongArrayList
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.decodeWire
import kotlinx.rpc.protobuf.internal.encodeWire
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class PrimitiveArrayListsTest {
    @Test
    fun intListMatchesPlainList() {
        val values = listOf(0, 1, -1, Int.MIN_VALUE, Int.MAX_VALUE)
        val list = IntArrayList()
        values.forEach { list.addInt(it) }

        assertEquals(values, list)
        assertEquals(list, values)
        assertEquals(values.hashCode(), list.hashCode())
        assertEquals(values.toString(), list.toString())
        assertEquals(5, list.size)
    }

    @Test
    fun uintListRendersUnsignedValues() {
        val list = UIntArrayList()
        list.addUInt(0xFFFFFFFFu)

        assertEquals(listOf(0xFFFFFFFFu), list)
        assertEquals("[4294967295]", list.toString())
        assertEquals(listOf<UInt>(0xFFFFFFFFu).hashCode(), list.hashCode())
    }

    @Test
    fun uLongListStoresBitsOfLongList() {
        val list = ULongArrayList()
        list.addULong(ULong.MAX_VALUE)
        list.addULong(0uL)

        assertEquals(listOf(ULong.MAX_VALUE, 0uL), list)
        assertEquals("[18446744073709551615, 0]", list.toString())
        assertEquals(listOf<ULong>(ULong.MAX_VALUE, 0uL).hashCode(), list.hashCode())
    }

    @Test
    fun longFloatDoubleAndBooleanListsMatchPlainLists() {
        val longValues = listOf(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE)
        val longs = LongArrayList.copyOf(longValues)
        assertEquals(longValues, longs)
        assertEquals(longValues.hashCode(), longs.hashCode())

        val floatValues = listOf(0.0f, -1.5f, Float.MAX_VALUE)
        val floats = FloatArrayList.copyOf(floatValues)
        assertEquals(floatValues, floats)
        assertEquals(floatValues.hashCode(), floats.hashCode())

        val doubleValues = listOf(0.0, -1.5, Double.MAX_VALUE)
        val doubles = DoubleArrayList.copyOf(doubleValues)
        assertEquals(doubleValues, doubles)
        assertEquals(doubleValues.hashCode(), doubles.hashCode())

        val booleanValues = listOf(true, false)
        val booleans = BooleanArrayList.copyOf(booleanValues)
        assertEquals(booleanValues, booleans)
        assertEquals(booleanValues.hashCode(), booleans.hashCode())
    }

    @Test
    fun floatEqualityComparesBitsLikePlainLists() {
        val nan = FloatArrayList.copyOf(listOf(Float.NaN))
        assertEquals(nan, FloatArrayList.copyOf(listOf(Float.NaN)))
        assertEquals(nan, listOf(Float.NaN))
        assertEquals(listOf(Float.NaN).hashCode(), nan.hashCode())

        val zero = FloatArrayList.copyOf(listOf(0.0f))
        val negativeZero = FloatArrayList.copyOf(listOf(-0.0f))
        assertNotEquals(zero, negativeZero)
        assertEquals(listOf(0.0f) == listOf(-0.0f), zero == negativeZero)
    }

    @Test
    fun doubleEqualityComparesBitsLikePlainLists() {
        val nan = DoubleArrayList.copyOf(listOf(Double.NaN))
        assertEquals(nan, DoubleArrayList.copyOf(listOf(Double.NaN)))
        assertEquals(nan, listOf(Double.NaN))
        assertEquals(listOf(Double.NaN).hashCode(), nan.hashCode())

        val zero = DoubleArrayList.copyOf(listOf(0.0))
        val negativeZero = DoubleArrayList.copyOf(listOf(-0.0))
        assertNotEquals(zero, negativeZero)
        assertEquals(listOf(0.0) == listOf(-0.0), zero == negativeZero)
    }

    @Test
    fun copyOfMakesAnIndependentCopy() {
        val source = LongArrayList.copyOf(listOf(1L, 2L))
        val copy = LongArrayList.copyOf(source)
        source.addLong(3)

        assertNotSame(source, copy)
        assertEquals(listOf(1L, 2L), copy)
        assertEquals(3, source.size)

        val plain = mutableListOf(1u, 2u)
        val uints = UIntArrayList.copyOf(plain)
        plain.add(3u)
        assertEquals(listOf(1u, 2u), uints)
        assertEquals(listOf(1u, 2u), UIntArrayList.copyOf(uints))
    }

    @Test
    fun mutationsThroughTheListInterface() {
        val list = IntArrayList()
        list.add(1)
        list.add(3)
        list.add(1, 2)
        list.add(list.size, 4)
        assertEquals(listOf(1, 2, 3, 4), list)

        list[0] = 6
        assertEquals(6, list.getInt(0))

        assertEquals(2, list.removeAt(1))
        assertEquals(listOf(6, 3, 4), list)

        assertFailsWith<IndexOutOfBoundsException> { list.add(4, 0) }
        assertFailsWith<IndexOutOfBoundsException> { list.add(-1, 0) }
    }

    @Test
    fun unsignedMutationsThroughTheListInterface() {
        val list = ULongArrayList()
        list.add(1uL)
        list.add(ULong.MAX_VALUE)
        list.add(1, 2uL)
        assertEquals(listOf(1uL, 2uL, ULong.MAX_VALUE), list)

        assertEquals(2uL, list.set(1, 5uL))
        assertEquals(ULong.MAX_VALUE, list.removeAt(2))
        assertEquals(listOf(1uL, 5uL), list)
    }

    @Test
    fun sumOfAndForEachVisitEachElement() {
        val list = UIntArrayList.copyOf((1u..100u).toList())

        assertEquals(5050, list.sumOfUInt { it.toInt() })

        val visited = mutableListOf<UInt>()
        list.forEachUInt { visited.add(it) }
        assertEquals(list, visited)
    }

    @Test
    fun growsBeyondTheInitialCapacity() {
        val list = DoubleArrayList(initialCapacity = 0)
        repeat(1_000) { list.addDouble(it.toDouble()) }

        assertEquals(1_000, list.size)
        assertEquals(999.0, list.getDouble(999))
    }

    @Test
    fun getRejectsOutOfBoundsIndexes() {
        val list = IntArrayList()
        list.addInt(1)

        assertFailsWith<IndexOutOfBoundsException> { list.getInt(-1) }
        assertFailsWith<IndexOutOfBoundsException> { list.getInt(1) }
    }

    @Test
    fun packedVarintFieldsRoundTrip() {
        val ints = listOf(0, 1, -1, 300, Int.MIN_VALUE, Int.MAX_VALUE)
        val longs = listOf(0L, 1L, -1L, 300L, Long.MIN_VALUE, Long.MAX_VALUE)
        checkPacked(
            ints, { IntArrayList.copyOf(it) }, { IntArrayList() },
            { writePackedInt32(1, it, WireSize.packedInt32(it)) },
            { readPackedInt32(it) },
        )
        checkPacked(
            ints, { IntArrayList.copyOf(it) }, { IntArrayList() },
            { writePackedSInt32(1, it, WireSize.packedSInt32(it)) },
            { readPackedSInt32(it) },
        )
        checkPacked(
            longs, { LongArrayList.copyOf(it) }, { LongArrayList() },
            { writePackedInt64(1, it, WireSize.packedInt64(it)) },
            { readPackedInt64(it) },
        )
        checkPacked(
            longs, { LongArrayList.copyOf(it) }, { LongArrayList() },
            { writePackedSInt64(1, it, WireSize.packedSInt64(it)) },
            { readPackedSInt64(it) },
        )
        checkPacked(
            ints.map { it.toUInt() }, { UIntArrayList.copyOf(it) }, { UIntArrayList() },
            { writePackedUInt32(1, it, WireSize.packedUInt32(it)) },
            { readPackedUInt32(it) },
        )
        checkPacked(
            longs.map { it.toULong() }, { ULongArrayList.copyOf(it) }, { ULongArrayList() },
            { writePackedUInt64(1, it, WireSize.packedUInt64(it)) },
            { readPackedUInt64(it) },
        )
        checkPacked(
            listOf(true, false, true), { BooleanArrayList.copyOf(it) }, { BooleanArrayList() },
            { writePackedBool(1, it) },
            { readPackedBool(it) },
        )
    }

    @Test
    fun packedFixedFieldsRoundTrip() {
        val ints = listOf(0, 1, -1, Int.MIN_VALUE, Int.MAX_VALUE)
        val longs = listOf(0L, 1L, -1L, Long.MIN_VALUE, Long.MAX_VALUE)
        checkPacked(
            ints.map { it.toUInt() }, { UIntArrayList.copyOf(it) }, { UIntArrayList() },
            { writePackedFixed32(1, it) },
            { readPackedFixed32(it) },
        )
        checkPacked(
            ints, { IntArrayList.copyOf(it) }, { IntArrayList() },
            { writePackedSFixed32(1, it) },
            { readPackedSFixed32(it) },
        )
        checkPacked(
            longs.map { it.toULong() }, { ULongArrayList.copyOf(it) }, { ULongArrayList() },
            { writePackedFixed64(1, it) },
            { readPackedFixed64(it) },
        )
        checkPacked(
            longs, { LongArrayList.copyOf(it) }, { LongArrayList() },
            { writePackedSFixed64(1, it) },
            { readPackedSFixed64(it) },
        )
        checkPacked(
            listOf(0.0f, -0.0f, Float.NaN, Float.MAX_VALUE), { FloatArrayList.copyOf(it) }, { FloatArrayList() },
            { writePackedFloat(1, it) },
            { readPackedFloat(it) },
        )
        checkPacked(
            listOf(0.0, -0.0, Double.NaN, Double.MAX_VALUE), { DoubleArrayList.copyOf(it) }, { DoubleArrayList() },
            { writePackedDouble(1, it) },
            { readPackedDouble(it) },
        )
    }

    /** Checks that two occurrences of a packed field decode into one destination. */
    private fun <T, L : MutableList<T>> checkPacked(
        values: List<T>,
        copyOf: (List<T>) -> L,
        empty: () -> L,
        write: WireEncoder.(L) -> Unit,
        read: WireDecoder.(L) -> Unit,
    ) {
        val bytes = encode { it.write(copyOf(values)) }
        val buffer = Buffer()
        buffer.write(bytes)
        buffer.write(bytes)
        val destination = empty()
        decodeWire(buffer) { decoder ->
            repeat(2) {
                decoder.readTag()
                decoder.read(destination)
            }
            assertTrue(decoder.readTag().isEnd)
        }
        assertEquals(values + values, destination)
    }

    private fun encode(write: (WireEncoder) -> Unit): ByteArray {
        val buffer = Buffer()
        encodeWire(buffer) { write(it) }
        return buffer.readByteArray()
    }
}
