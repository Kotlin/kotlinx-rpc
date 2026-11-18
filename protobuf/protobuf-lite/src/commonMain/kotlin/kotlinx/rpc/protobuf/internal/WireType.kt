/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

/** Protobuf wire type values stored in the low bits of a tag. */
@InternalRpcApi
public object WireType {
    public const val VARINT: Int = 0

    public const val FIXED64: Int = 1

    public const val LENGTH_DELIMITED: Int = 2

    public const val START_GROUP: Int = 3

    public const val END_GROUP: Int = 4

    public const val FIXED32: Int = 5
}
