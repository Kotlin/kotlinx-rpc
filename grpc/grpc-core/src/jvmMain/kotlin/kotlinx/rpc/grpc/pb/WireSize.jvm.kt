/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import com.google.protobuf.CodedOutputStream.*
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual fun WireSize.int32(value: Int): Int {
    return computeInt32SizeNoTag(value)
}

@InternalRpcApi
public actual fun WireSize.int64(value: Long): Int {
    return computeInt64SizeNoTag(value)
}

@InternalRpcApi
public actual fun WireSize.uInt32(value: UInt): Int {
    // todo check java unsigned types
    return computeUInt32SizeNoTag(value.toInt())
}

@InternalRpcApi
public actual fun WireSize.uInt64(value: ULong): Int {
    // todo check java unsigned types
    return computeUInt64SizeNoTag(value.toLong())
}

@InternalRpcApi
public actual fun WireSize.sInt32(value: Int): Int {
    return computeSInt32SizeNoTag(value)
}

@InternalRpcApi
public actual fun WireSize.sInt64(value: Long): Int {
    return computeSInt64SizeNoTag(value)
}
