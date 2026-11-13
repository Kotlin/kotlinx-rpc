/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

/** Protobuf wire type values stored in the low bits of a tag. */
@InternalRpcApi
public object WireType {
    /** A varint. */
    public const val VARINT: Int = 0

    /** A 64-bit value. */
    public const val FIXED64: Int = 1

    /** A value with a byte length prefix. */
    public const val LENGTH_DELIMITED: Int = 2

    /** The start of a group. */
    public const val START_GROUP: Int = 3

    /** The end of a group. */
    public const val END_GROUP: Int = 4

    /** A 32-bit value. */
    public const val FIXED32: Int = 5
}
