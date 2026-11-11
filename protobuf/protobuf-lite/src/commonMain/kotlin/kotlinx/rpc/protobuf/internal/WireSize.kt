/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UnusedReceiverParameter")

package kotlinx.rpc.protobuf.internal

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public object WireSize

@InternalRpcApi
public fun WireSize.tag(fieldNumber: Int, wireType: WireType): Int =
    Varint.sizeOf(UInt32((fieldNumber shl 3) or wireType.ordinal))

@InternalRpcApi
public fun WireSize.int32(value: Int): Int = Varint.sizeOf(Int32(value))

@InternalRpcApi
public fun WireSize.int64(value: Long): Int = Varint.sizeOf(Int64(value))

@InternalRpcApi
public fun WireSize.uInt32(value: UInt): Int = Varint.sizeOf(UInt32(value.toInt()))

@InternalRpcApi
public fun WireSize.uInt64(value: ULong): Int = Varint.sizeOf(UInt64(value.toLong()))

@InternalRpcApi
public fun WireSize.sInt32(value: Int): Int = Varint.sizeOf(SInt32(value))

@InternalRpcApi
public fun WireSize.sInt64(value: Long): Int = Varint.sizeOf(SInt64(value))

@InternalRpcApi
@Suppress("detekt.UnusedParameter")
public fun WireSize.float(value: Float): Int = 4

@InternalRpcApi
@Suppress("detekt.UnusedParameter")
public fun WireSize.double(value: Double): Int = 8

@InternalRpcApi
@Suppress("detekt.UnusedParameter")
public fun WireSize.fixed32(value: UInt): Int = 4

@InternalRpcApi
@Suppress("detekt.UnusedParameter")
public fun WireSize.fixed64(value: ULong): Int = 8

@InternalRpcApi
@Suppress("detekt.UnusedParameter")
public fun WireSize.sFixed32(value: Int): Int = 4

@InternalRpcApi
@Suppress("detekt.UnusedParameter")
public fun WireSize.sFixed64(value: Long): Int = 8

@InternalRpcApi
@Suppress("detekt.UnusedParameter")
public fun WireSize.bool(value: Boolean): Int = 1

@InternalRpcApi
public fun WireSize.enum(value: Int): Int = int32(value)

@InternalRpcApi
public fun WireSize.bytes(value: ByteArray): Int = value.size

@InternalRpcApi
public fun WireSize.bytes(value: ByteString): Int = value.size

@InternalRpcApi
public fun WireSize.string(value: String): Int = Utf8.encodedLength(value)

@InternalRpcApi
public fun WireSize.packedInt32(value: List<Int>): Int = value.sumOf { int32(it) }

@InternalRpcApi
public fun WireSize.packedInt64(value: List<Long>): Int = value.sumOf { int64(it) }

@InternalRpcApi
public fun WireSize.packedUInt32(value: List<UInt>): Int = value.sumOf { uInt32(it) }

@InternalRpcApi
public fun WireSize.packedUInt64(value: List<ULong>): Int = value.sumOf { uInt64(it) }

@InternalRpcApi
public fun WireSize.packedSInt32(value: List<Int>): Int = value.sumOf { sInt32(it) }

@InternalRpcApi
public fun WireSize.packedSInt64(value: List<Long>): Int = value.sumOf { sInt64(it) }

@InternalRpcApi
public fun WireSize.packedEnum(value: List<Int>): Int = value.sumOf { enum(it) }

@InternalRpcApi
public fun WireSize.packedFloat(value: List<Float>): Int = value.size * 4

@InternalRpcApi
public fun WireSize.packedDouble(value: List<Double>): Int = value.size * 8

@InternalRpcApi
public fun WireSize.packedFixed32(value: List<UInt>): Int = value.size * 4

@InternalRpcApi
public fun WireSize.packedFixed64(value: List<ULong>): Int = value.size * 8

@InternalRpcApi
public fun WireSize.packedSFixed32(value: List<Int>): Int = value.size * 4

@InternalRpcApi
public fun WireSize.packedSFixed64(value: List<Long>): Int = value.size * 8

@InternalRpcApi
public fun WireSize.packedBool(value: List<Boolean>): Int = value.size
