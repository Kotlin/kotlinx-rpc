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

    public fun packedInt32(value: List<Int>): Int = value.sumOf { int32(it) }

    public fun packedInt64(value: List<Long>): Int = value.sumOf { int64(it) }

    public fun packedUInt32(value: List<UInt>): Int = value.sumOf { uInt32(it) }

    public fun packedUInt64(value: List<ULong>): Int = value.sumOf { uInt64(it) }

    public fun packedSInt32(value: List<Int>): Int = value.sumOf { sInt32(it) }

    public fun packedSInt64(value: List<Long>): Int = value.sumOf { sInt64(it) }

    public fun packedEnum(value: List<Int>): Int = value.sumOf { enum(it) }

    public fun packedFloat(value: List<Float>): Int = value.size * 4

    public fun packedDouble(value: List<Double>): Int = value.size * 8

    public fun packedFixed32(value: List<UInt>): Int = value.size * 4

    public fun packedFixed64(value: List<ULong>): Int = value.size * 8

    public fun packedSFixed32(value: List<Int>): Int = value.size * 4

    public fun packedSFixed64(value: List<Long>): Int = value.size * 8

    public fun packedBool(value: List<Boolean>): Int = value.size
}
