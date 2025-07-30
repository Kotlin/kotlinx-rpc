/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

public object WireSize

public expect fun WireSize.int32(value: Int): Int
public expect fun WireSize.int64(value: Long): Int
public expect fun WireSize.uInt32(value: UInt): Int
public expect fun WireSize.uInt64(value: ULong): Int
public expect fun WireSize.sInt32(value: Int): Int
public expect fun WireSize.sInt64(value: Long): Int

public fun WireSize.bool(value: Boolean): Int = int32(if (value) 1 else 0)
public fun WireSize.enum(value: Int): Int = int32(value)
public fun WireSize.packedInt32(value: List<Int>): Int = value.sumOf { int32(it) }
public fun WireSize.packedInt64(value: List<Long>): Int = value.sumOf { int64(it) }
public fun WireSize.packedUInt32(value: List<UInt>): Int = value.sumOf { uInt32(it) }
public fun WireSize.packedUInt64(value: List<ULong>): Int = value.sumOf { uInt64(it) }
public fun WireSize.packedSInt32(value: List<Int>): Int = value.sumOf { sInt32(it) }
public fun WireSize.packedSInt64(value: List<Long>): Int = value.sumOf { sInt64(it) }
public fun WireSize.packedEnum(value: List<Int>): Int = value.sumOf { enum(it) }
