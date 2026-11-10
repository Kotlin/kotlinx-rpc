/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlin.jvm.JvmInline

/** An `int32` or `enum` value. A negative value is sign-extended to 64 bits, so it encodes as 10 bytes. */
@JvmInline
internal value class Int32(val value: Int)

/** An `int64` value. */
@JvmInline
internal value class Int64(val value: Long)

/** The bits of an unsigned 32-bit varint: a `uint32` value, a tag, or a length. */
@JvmInline
internal value class UInt32(val value: Int)

/** The bits of a `uint64` value. */
@JvmInline
internal value class UInt64(val value: Long)

/** An `sint32` value. It encodes as the ZigZag value. */
@JvmInline
internal value class SInt32(val value: Int)

/** An `sint64` value. It encodes as the ZigZag value. */
@JvmInline
internal value class SInt64(val value: Long)
