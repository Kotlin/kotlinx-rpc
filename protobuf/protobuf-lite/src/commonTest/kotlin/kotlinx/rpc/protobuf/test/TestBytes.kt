/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.internal.UInt32
import kotlinx.rpc.protobuf.internal.UInt64
import kotlinx.rpc.protobuf.internal.Utf8
import kotlinx.rpc.protobuf.internal.Varint

/** Makes a [ByteArray] from byte values from 0 to 255. */
internal fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { values[it].toByte() }

/** The bytes that [Varint.encode] gives for [value]. */
internal fun varintBytes(value: UInt32): ByteArray {
    val result = ByteArray(Varint.MAX_SIZE_32)
    var size = 0
    Varint.encode(value) { result[size++] = it }
    return result.copyOf(size)
}

/** The bytes that [Varint.encode] gives for [value]. */
internal fun varintBytes(value: UInt64): ByteArray {
    val result = ByteArray(Varint.MAX_SIZE_64)
    var size = 0
    Varint.encode(value) { result[size++] = it }
    return result.copyOf(size)
}

/** The bytes that [Utf8.encode] gives for [value]. */
internal fun utf8Bytes(value: String): ByteArray {
    // A UTF-16 char gives at most 3 bytes in UTF-8.
    val result = ByteArray(value.length * 3)
    var size = 0
    Utf8.encode(value) { result[size++] = it }
    return result.copyOf(size)
}
