/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

private const val DEFAULT_CAPACITY = 8

private fun checkElementIndex(index: Int, size: Int) {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException("Index: $index, Size: $size")
    }
}

private fun checkPositionIndex(index: Int, size: Int) {
    if (index < 0 || index > size) {
        throw IndexOutOfBoundsException("Index: $index, Size: $size")
    }
}

/** Returns the [transform] results of all elements, for example the numbers of a packed enum list. */
@InternalRpcApi
public inline fun <E> List<E>.mapToIntArrayList(transform: (E) -> Int): IntArrayList {
    val result = IntArrayList(size)
    for (element in this) {
        result.addInt(transform(element))
    }
    return result
}

/**
 * The storage of a repeated `int32`, `sint32`, or `sfixed32` field: a [MutableList] over an [IntArray].
 *
 * The `addX`, `getX`, `sumOfX`, and `forEachX` members of the primitive array lists don't box. The `List`
 * members box, as every generic boundary does. So generated code and the codec use only the unboxed members.
 * [equals] and [hashCode] follow the `List` contract, because the field type that users see is `List`.
 */
@InternalRpcApi
public class IntArrayList(initialCapacity: Int = DEFAULT_CAPACITY) : AbstractMutableList<Int>(), RandomAccess {
    private var array: IntArray = IntArray(initialCapacity)
    private var count: Int = 0

    override val size: Int get() = count

    public fun ensureCapacity(minCapacity: Int) {
        if (minCapacity > array.size) {
            array = array.copyOf(maxOf(minCapacity, array.size * 2))
        }
    }

    public fun addInt(element: Int) {
        ensureCapacity(count + 1)
        array[count] = element
        count++
        modCount++
    }

    public fun getInt(index: Int): Int {
        checkElementIndex(index, count)
        return array[index]
    }

    public inline fun sumOfInt(selector: (Int) -> Int): Int {
        var total = 0
        for (index in 0 until size) {
            total += selector(getInt(index))
        }
        return total
    }

    public inline fun forEachInt(action: (Int) -> Unit) {
        for (index in 0 until size) {
            action(getInt(index))
        }
    }

    override fun get(index: Int): Int = getInt(index)

    override fun set(index: Int, element: Int): Int {
        val old = getInt(index)
        array[index] = element
        return old
    }

    override fun add(index: Int, element: Int) {
        checkPositionIndex(index, count)
        ensureCapacity(count + 1)
        array.copyInto(array, index + 1, index, count)
        array[index] = element
        count++
        modCount++
    }

    override fun removeAt(index: Int): Int {
        val old = getInt(index)
        array.copyInto(array, index, index + 1, count)
        count--
        modCount++
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is List<*> || other.size != count) {
            return false
        }
        if (other is IntArrayList) {
            for (index in 0 until count) {
                if (array[index] != other.array[index]) {
                    return false
                }
            }
            return true
        }
        for (index in 0 until count) {
            if (other[index] != array[index]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        for (index in 0 until count) {
            result = 31 * result + array[index]
        }
        return result
    }

    public companion object {
        public fun copyOf(values: List<Int>): IntArrayList {
            if (values is IntArrayList) {
                val copy = IntArrayList(values.count)
                values.array.copyInto(copy.array, 0, 0, values.count)
                copy.count = values.count
                return copy
            }
            val copy = IntArrayList(values.size)
            for (value in values) {
                copy.addInt(value)
            }
            return copy
        }
    }
}

/** The storage of a repeated `int64`, `sint64`, or `sfixed64` field, see [IntArrayList]. */
@InternalRpcApi
public class LongArrayList(initialCapacity: Int = DEFAULT_CAPACITY) : AbstractMutableList<Long>(), RandomAccess {
    private var array: LongArray = LongArray(initialCapacity)
    private var count: Int = 0

    override val size: Int get() = count

    public fun ensureCapacity(minCapacity: Int) {
        if (minCapacity > array.size) {
            array = array.copyOf(maxOf(minCapacity, array.size * 2))
        }
    }

    public fun addLong(element: Long) {
        ensureCapacity(count + 1)
        array[count] = element
        count++
        modCount++
    }

    public fun getLong(index: Int): Long {
        checkElementIndex(index, count)
        return array[index]
    }

    public inline fun sumOfLong(selector: (Long) -> Int): Int {
        var total = 0
        for (index in 0 until size) {
            total += selector(getLong(index))
        }
        return total
    }

    public inline fun forEachLong(action: (Long) -> Unit) {
        for (index in 0 until size) {
            action(getLong(index))
        }
    }

    override fun get(index: Int): Long = getLong(index)

    override fun set(index: Int, element: Long): Long {
        val old = getLong(index)
        array[index] = element
        return old
    }

    override fun add(index: Int, element: Long) {
        checkPositionIndex(index, count)
        ensureCapacity(count + 1)
        array.copyInto(array, index + 1, index, count)
        array[index] = element
        count++
        modCount++
    }

    override fun removeAt(index: Int): Long {
        val old = getLong(index)
        array.copyInto(array, index, index + 1, count)
        count--
        modCount++
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is List<*> || other.size != count) {
            return false
        }
        if (other is LongArrayList) {
            for (index in 0 until count) {
                if (array[index] != other.array[index]) {
                    return false
                }
            }
            return true
        }
        for (index in 0 until count) {
            if (other[index] != array[index]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        for (index in 0 until count) {
            result = 31 * result + array[index].hashCode()
        }
        return result
    }

    public companion object {
        public fun copyOf(values: List<Long>): LongArrayList {
            if (values is LongArrayList) {
                val copy = LongArrayList(values.count)
                values.array.copyInto(copy.array, 0, 0, values.count)
                copy.count = values.count
                return copy
            }
            val copy = LongArrayList(values.size)
            for (value in values) {
                copy.addLong(value)
            }
            return copy
        }
    }
}

/** The storage of a repeated `uint32` or `fixed32` field, see [IntArrayList]. Stores the bits in an [IntArrayList]. */
@InternalRpcApi
public class UIntArrayList private constructor(
    private val ints: IntArrayList,
) : AbstractMutableList<UInt>(), RandomAccess {
    public constructor(initialCapacity: Int = DEFAULT_CAPACITY) : this(IntArrayList(initialCapacity))

    override val size: Int get() = ints.size

    public fun ensureCapacity(minCapacity: Int) {
        ints.ensureCapacity(minCapacity)
    }

    public fun addUInt(element: UInt) {
        ints.addInt(element.toInt())
        modCount++
    }

    public fun getUInt(index: Int): UInt = ints.getInt(index).toUInt()

    public inline fun sumOfUInt(selector: (UInt) -> Int): Int {
        var total = 0
        for (index in 0 until size) {
            total += selector(getUInt(index))
        }
        return total
    }

    public inline fun forEachUInt(action: (UInt) -> Unit) {
        for (index in 0 until size) {
            action(getUInt(index))
        }
    }

    override fun get(index: Int): UInt = getUInt(index)

    override fun set(index: Int, element: UInt): UInt = ints.set(index, element.toInt()).toUInt()

    override fun add(index: Int, element: UInt) {
        ints.add(index, element.toInt())
        modCount++
    }

    override fun removeAt(index: Int): UInt {
        val old = ints.removeAt(index).toUInt()
        modCount++
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is List<*> || other.size != size) {
            return false
        }
        if (other is UIntArrayList) {
            return ints == other.ints
        }
        for (index in 0 until size) {
            if (other[index] != getUInt(index)) {
                return false
            }
        }
        return true
    }

    // A UInt has the hash code of its bits, so the hash code of the bits list is the same.
    override fun hashCode(): Int = ints.hashCode()

    public companion object {
        public fun copyOf(values: List<UInt>): UIntArrayList {
            if (values is UIntArrayList) {
                return UIntArrayList(IntArrayList.copyOf(values.ints))
            }
            val copy = UIntArrayList(values.size)
            for (value in values) {
                copy.addUInt(value)
            }
            return copy
        }
    }
}

/** The storage of a repeated `uint64` or `fixed64` field, see [IntArrayList]. Stores the bits in a [LongArrayList]. */
@InternalRpcApi
public class ULongArrayList private constructor(
    private val longs: LongArrayList,
) : AbstractMutableList<ULong>(), RandomAccess {
    public constructor(initialCapacity: Int = DEFAULT_CAPACITY) : this(LongArrayList(initialCapacity))

    override val size: Int get() = longs.size

    public fun ensureCapacity(minCapacity: Int) {
        longs.ensureCapacity(minCapacity)
    }

    public fun addULong(element: ULong) {
        longs.addLong(element.toLong())
        modCount++
    }

    public fun getULong(index: Int): ULong = longs.getLong(index).toULong()

    public inline fun sumOfULong(selector: (ULong) -> Int): Int {
        var total = 0
        for (index in 0 until size) {
            total += selector(getULong(index))
        }
        return total
    }

    public inline fun forEachULong(action: (ULong) -> Unit) {
        for (index in 0 until size) {
            action(getULong(index))
        }
    }

    override fun get(index: Int): ULong = getULong(index)

    override fun set(index: Int, element: ULong): ULong = longs.set(index, element.toLong()).toULong()

    override fun add(index: Int, element: ULong) {
        longs.add(index, element.toLong())
        modCount++
    }

    override fun removeAt(index: Int): ULong {
        val old = longs.removeAt(index).toULong()
        modCount++
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is List<*> || other.size != size) {
            return false
        }
        if (other is ULongArrayList) {
            return longs == other.longs
        }
        for (index in 0 until size) {
            if (other[index] != getULong(index)) {
                return false
            }
        }
        return true
    }

    // A ULong has the hash code of its bits, so the hash code of the bits list is the same.
    override fun hashCode(): Int = longs.hashCode()

    public companion object {
        public fun copyOf(values: List<ULong>): ULongArrayList {
            if (values is ULongArrayList) {
                return ULongArrayList(LongArrayList.copyOf(values.longs))
            }
            val copy = ULongArrayList(values.size)
            for (value in values) {
                copy.addULong(value)
            }
            return copy
        }
    }
}

/**
 * The storage of a repeated `float` field, see [IntArrayList].
 *
 * [equals] compares the bits, as `Float.equals` does: `NaN` equals `NaN`, and `0.0f` doesn't equal `-0.0f`.
 */
@InternalRpcApi
public class FloatArrayList(initialCapacity: Int = DEFAULT_CAPACITY) : AbstractMutableList<Float>(), RandomAccess {
    private var array: FloatArray = FloatArray(initialCapacity)
    private var count: Int = 0

    override val size: Int get() = count

    public fun ensureCapacity(minCapacity: Int) {
        if (minCapacity > array.size) {
            array = array.copyOf(maxOf(minCapacity, array.size * 2))
        }
    }

    public fun addFloat(element: Float) {
        ensureCapacity(count + 1)
        array[count] = element
        count++
        modCount++
    }

    public fun getFloat(index: Int): Float {
        checkElementIndex(index, count)
        return array[index]
    }

    public inline fun sumOfFloat(selector: (Float) -> Int): Int {
        var total = 0
        for (index in 0 until size) {
            total += selector(getFloat(index))
        }
        return total
    }

    public inline fun forEachFloat(action: (Float) -> Unit) {
        for (index in 0 until size) {
            action(getFloat(index))
        }
    }

    override fun get(index: Int): Float = getFloat(index)

    override fun set(index: Int, element: Float): Float {
        val old = getFloat(index)
        array[index] = element
        return old
    }

    override fun add(index: Int, element: Float) {
        checkPositionIndex(index, count)
        ensureCapacity(count + 1)
        array.copyInto(array, index + 1, index, count)
        array[index] = element
        count++
        modCount++
    }

    override fun removeAt(index: Int): Float {
        val old = getFloat(index)
        array.copyInto(array, index, index + 1, count)
        count--
        modCount++
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is List<*> || other.size != count) {
            return false
        }
        if (other is FloatArrayList) {
            for (index in 0 until count) {
                if (array[index].toBits() != other.array[index].toBits()) {
                    return false
                }
            }
            return true
        }
        for (index in 0 until count) {
            if (other[index] != array[index]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        for (index in 0 until count) {
            result = 31 * result + array[index].hashCode()
        }
        return result
    }

    public companion object {
        public fun copyOf(values: List<Float>): FloatArrayList {
            if (values is FloatArrayList) {
                val copy = FloatArrayList(values.count)
                values.array.copyInto(copy.array, 0, 0, values.count)
                copy.count = values.count
                return copy
            }
            val copy = FloatArrayList(values.size)
            for (value in values) {
                copy.addFloat(value)
            }
            return copy
        }
    }
}

/**
 * The storage of a repeated `double` field, see [IntArrayList].
 *
 * [equals] compares the bits, as `Double.equals` does: `NaN` equals `NaN`, and `0.0` doesn't equal `-0.0`.
 */
@InternalRpcApi
public class DoubleArrayList(initialCapacity: Int = DEFAULT_CAPACITY) : AbstractMutableList<Double>(), RandomAccess {
    private var array: DoubleArray = DoubleArray(initialCapacity)
    private var count: Int = 0

    override val size: Int get() = count

    public fun ensureCapacity(minCapacity: Int) {
        if (minCapacity > array.size) {
            array = array.copyOf(maxOf(minCapacity, array.size * 2))
        }
    }

    public fun addDouble(element: Double) {
        ensureCapacity(count + 1)
        array[count] = element
        count++
        modCount++
    }

    public fun getDouble(index: Int): Double {
        checkElementIndex(index, count)
        return array[index]
    }

    public inline fun sumOfDouble(selector: (Double) -> Int): Int {
        var total = 0
        for (index in 0 until size) {
            total += selector(getDouble(index))
        }
        return total
    }

    public inline fun forEachDouble(action: (Double) -> Unit) {
        for (index in 0 until size) {
            action(getDouble(index))
        }
    }

    override fun get(index: Int): Double = getDouble(index)

    override fun set(index: Int, element: Double): Double {
        val old = getDouble(index)
        array[index] = element
        return old
    }

    override fun add(index: Int, element: Double) {
        checkPositionIndex(index, count)
        ensureCapacity(count + 1)
        array.copyInto(array, index + 1, index, count)
        array[index] = element
        count++
        modCount++
    }

    override fun removeAt(index: Int): Double {
        val old = getDouble(index)
        array.copyInto(array, index, index + 1, count)
        count--
        modCount++
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is List<*> || other.size != count) {
            return false
        }
        if (other is DoubleArrayList) {
            for (index in 0 until count) {
                if (array[index].toBits() != other.array[index].toBits()) {
                    return false
                }
            }
            return true
        }
        for (index in 0 until count) {
            if (other[index] != array[index]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        for (index in 0 until count) {
            result = 31 * result + array[index].hashCode()
        }
        return result
    }

    public companion object {
        public fun copyOf(values: List<Double>): DoubleArrayList {
            if (values is DoubleArrayList) {
                val copy = DoubleArrayList(values.count)
                values.array.copyInto(copy.array, 0, 0, values.count)
                copy.count = values.count
                return copy
            }
            val copy = DoubleArrayList(values.size)
            for (value in values) {
                copy.addDouble(value)
            }
            return copy
        }
    }
}

/** The storage of a repeated `bool` field, see [IntArrayList]. */
@InternalRpcApi
public class BooleanArrayList(initialCapacity: Int = DEFAULT_CAPACITY) : AbstractMutableList<Boolean>(), RandomAccess {
    private var array: BooleanArray = BooleanArray(initialCapacity)
    private var count: Int = 0

    override val size: Int get() = count

    public fun ensureCapacity(minCapacity: Int) {
        if (minCapacity > array.size) {
            array = array.copyOf(maxOf(minCapacity, array.size * 2))
        }
    }

    public fun addBoolean(element: Boolean) {
        ensureCapacity(count + 1)
        array[count] = element
        count++
        modCount++
    }

    public fun getBoolean(index: Int): Boolean {
        checkElementIndex(index, count)
        return array[index]
    }

    public inline fun sumOfBoolean(selector: (Boolean) -> Int): Int {
        var total = 0
        for (index in 0 until size) {
            total += selector(getBoolean(index))
        }
        return total
    }

    public inline fun forEachBoolean(action: (Boolean) -> Unit) {
        for (index in 0 until size) {
            action(getBoolean(index))
        }
    }

    override fun get(index: Int): Boolean = getBoolean(index)

    override fun set(index: Int, element: Boolean): Boolean {
        val old = getBoolean(index)
        array[index] = element
        return old
    }

    override fun add(index: Int, element: Boolean) {
        checkPositionIndex(index, count)
        ensureCapacity(count + 1)
        array.copyInto(array, index + 1, index, count)
        array[index] = element
        count++
        modCount++
    }

    override fun removeAt(index: Int): Boolean {
        val old = getBoolean(index)
        array.copyInto(array, index, index + 1, count)
        count--
        modCount++
        return old
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is List<*> || other.size != count) {
            return false
        }
        if (other is BooleanArrayList) {
            for (index in 0 until count) {
                if (array[index] != other.array[index]) {
                    return false
                }
            }
            return true
        }
        for (index in 0 until count) {
            if (other[index] != array[index]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        for (index in 0 until count) {
            result = 31 * result + array[index].hashCode()
        }
        return result
    }

    public companion object {
        public fun copyOf(values: List<Boolean>): BooleanArrayList {
            if (values is BooleanArrayList) {
                val copy = BooleanArrayList(values.count)
                values.array.copyInto(copy.array, 0, 0, values.count)
                copy.count = values.count
                return copy
            }
            val copy = BooleanArrayList(values.size)
            for (value in values) {
                copy.addBoolean(value)
            }
            return copy
        }
    }
}
