/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal.bridge

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import libgrpcpp_c.grpc_slice
import libgrpcpp_c.grpc_slice_from_copied_buffer
import libgrpcpp_c.grpc_slice_unref
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
internal class GrpcSlice internal constructor(internal val cSlice: CValue<grpc_slice>) {

    constructor(buffer: ByteArray) : this(
        buffer.usePinned { pinned ->
            grpc_slice_from_copied_buffer(pinned.addressOf(0), buffer.size.toULong())
        }
    )

    init {
        createCleaner(cSlice) {
            grpc_slice_unref(it)
        }
    }
}
