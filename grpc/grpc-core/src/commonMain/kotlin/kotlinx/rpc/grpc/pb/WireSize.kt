/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

internal object WireSize

internal expect fun WireSize.int32(value: Int): UInt
internal expect fun WireSize.int64(value: Long): UInt
internal expect fun WireSize.uInt32(value: UInt): UInt
internal expect fun WireSize.uInt64(value: ULong): UInt
internal expect fun WireSize.sInt32(value: Int): UInt
internal expect fun WireSize.sInt64(value: Long): UInt

internal fun WireSize.bool(value: Boolean) = int32(if (value) 1 else 0)
internal fun WireSize.enum(value: Int) = int32(value)
internal fun WireSize.packedInt32(value: List<Int>) = value.sumOf { int32(it) }
internal fun WireSize.packedInt64(value: List<Long>) = value.sumOf { int64(it) }
internal fun WireSize.packedUInt32(value: List<UInt>) = value.sumOf { uInt32(it) }
internal fun WireSize.packedUInt64(value: List<ULong>) = value.sumOf { uInt64(it) }
internal fun WireSize.packedSInt32(value: List<Int>) = value.sumOf { sInt32(it) }
internal fun WireSize.packedSInt64(value: List<Long>) = value.sumOf { sInt64(it) }
internal fun WireSize.packedEnum(value: List<Int>) = value.sumOf { enum(it) }
