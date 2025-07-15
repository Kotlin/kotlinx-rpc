/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.bridge

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import libgrpcpp_c.grpc_slice
import libgrpcpp_c.grpc_slice_from_copied_buffer
import libgrpcpp_c.grpc_slice_unref


@OptIn(ExperimentalForeignApi::class)
internal class GrpcSlice internal constructor(internal val cSlice: CValue<grpc_slice>) : AutoCloseable {

    constructor(buffer: ByteArray) : this(
        buffer.usePinned { pinned ->
            grpc_slice_from_copied_buffer(pinned.addressOf(0), buffer.size.toULong())
        }
    )

    override fun close() {
        grpc_slice_unref(cSlice)
    }
}