/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.InternalRpcApi

/** Computes protobuf field sizes in bytes. */
@InternalRpcApi
public object WireSize {
    public fun tag(tag: WireTag): Int = Varint.sizeOf(UInt32(tag.raw))

    public fun tag(fieldNumber: Int, wireType: Int): Int =
        tag(WireTag(fieldNumber, wireType))

    public fun int32(value: Int): Int = Varint.sizeOf(Int32(value))

    public fun int64(value: Long): Int = Varint.sizeOf(Int64(value))

    public fun uInt32(value: UInt): Int = Varint.sizeOf(UInt32(value.toInt()))

    public fun uInt64(value: ULong): Int = Varint.sizeOf(UInt64(value.toLong()))

    public fun sInt32(value: Int): Int = Varint.sizeOf(SInt32(value))

    public fun sInt64(value: Long): Int = Varint.sizeOf(SInt64(value))

    @Suppress("detekt.UnusedParameter")
    public fun float(value: Float): Int = 4

    @Suppress("detekt.UnusedParameter")
    public fun double(value: Double): Int = 8

    @Suppress("detekt.UnusedParameter")
    public fun fixed32(value: UInt): Int = 4

    @Suppress("detekt.UnusedParameter")
    public fun fixed64(value: ULong): Int = 8

    @Suppress("detekt.UnusedParameter")
    public fun sFixed32(value: Int): Int = 4

    @Suppress("detekt.UnusedParameter")
    public fun sFixed64(value: Long): Int = 8

    @Suppress("detekt.UnusedParameter")
    public fun bool(value: Boolean): Int = 1

    public fun enum(value: Int): Int = int32(value)

    public fun bytes(value: ByteArray): Int = value.size

    public fun bytes(value: ByteString): Int = value.size

    public fun string(value: String): Int = Utf8.encodedLength(value)

    /** The payload sizes of packed fields, over the primitive array list of each field type, see [IntArrayList]. */
    public fun packedInt32(value: IntArrayList): Int = value.sumOfInt { int32(it) }

    public fun packedInt64(value: LongArrayList): Int = value.sumOfLong { int64(it) }

    public fun packedUInt32(value: UIntArrayList): Int = value.sumOfUInt { uInt32(it) }

    public fun packedUInt64(value: ULongArrayList): Int = value.sumOfULong { uInt64(it) }

    public fun packedSInt32(value: IntArrayList): Int = value.sumOfInt { sInt32(it) }

    public fun packedSInt64(value: LongArrayList): Int = value.sumOfLong { sInt64(it) }

    /** The payload size of a packed enum field over the enum numbers. */
    public fun packedEnum(value: IntArrayList): Int = value.sumOfInt { enum(it) }

    public fun packedFloat(value: FloatArrayList): Int = value.size * 4

    public fun packedDouble(value: DoubleArrayList): Int = value.size * 8

    public fun packedFixed32(value: UIntArrayList): Int = value.size * 4

    public fun packedFixed64(value: ULongArrayList): Int = value.size * 8

    public fun packedSFixed32(value: IntArrayList): Int = value.size * 4

    public fun packedSFixed64(value: LongArrayList): Int = value.size * 8

    public fun packedBool(value: BooleanArrayList): Int = value.size
}
