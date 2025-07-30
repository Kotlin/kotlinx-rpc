/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import com.google.protobuf.CodedOutputStream.computeInt32SizeNoTag
import com.google.protobuf.CodedOutputStream.computeInt64SizeNoTag
import com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag
import com.google.protobuf.CodedOutputStream.computeUInt64SizeNoTag
import com.google.protobuf.CodedOutputStream.computeSInt32SizeNoTag
import com.google.protobuf.CodedOutputStream.computeSInt64SizeNoTag

internal actual fun WireSize.int32(value: Int): UInt {
    return computeInt32SizeNoTag(value).toUInt()
}

internal actual fun WireSize.int64(value: Long): UInt {
    return computeInt64SizeNoTag(value).toUInt()
}

internal actual fun WireSize.uInt32(value: UInt): UInt {
    // todo check java unsigned types
    return computeUInt32SizeNoTag(value.toInt()).toUInt()
}

internal actual fun WireSize.uInt64(value: ULong): UInt {
    // todo check java unsigned types
    return computeUInt64SizeNoTag(value.toLong()).toUInt()
}

internal actual fun WireSize.sInt32(value: Int): UInt {
    return computeSInt32SizeNoTag(value).toUInt()
}

internal actual fun WireSize.sInt64(value: Long): UInt {
    return computeSInt64SizeNoTag(value).toUInt()
}
