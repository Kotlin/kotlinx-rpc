/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

/**
 * ZigZag encoding and decoding between the `sint32` and `sint64` values and their unsigned varint bits.
 */
internal object ZigZag {
    fun encode(value: SInt32): UInt32 = UInt32((value.value shl 1) xor (value.value shr 31))

    fun encode(value: SInt64): UInt64 = UInt64((value.value shl 1) xor (value.value shr 63))

    fun decode(value: UInt32): SInt32 = SInt32((value.value ushr 1) xor -(value.value and 1))

    fun decode(value: UInt64): SInt64 = SInt64((value.value ushr 1) xor -(value.value and 1L))
}
