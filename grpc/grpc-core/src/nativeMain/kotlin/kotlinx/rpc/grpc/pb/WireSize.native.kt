/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.pb

import kotlinx.cinterop.ExperimentalForeignApi
import libprotowire.*

internal actual fun WireSize.int32(value: Int) = pw_size_int32(value).toInt()
internal actual fun WireSize.int64(value: Long) = pw_size_int64(value).toInt()
internal actual fun WireSize.uInt32(value: UInt) = pw_size_uint32(value).toInt()
internal actual fun WireSize.uInt64(value: ULong) = pw_size_uint64(value).toInt()
internal actual fun WireSize.sInt32(value: Int) = pw_size_sint32(value).toInt()
internal actual fun WireSize.sInt64(value: Long) = pw_size_sint64(value).toInt()

