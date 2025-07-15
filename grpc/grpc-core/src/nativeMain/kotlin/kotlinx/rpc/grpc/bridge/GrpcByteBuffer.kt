/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.bridge

import kotlinx.cinterop.*
import libgrpcpp_c.*

@OptIn(ExperimentalForeignApi::class)
internal class GrpcByteBuffer internal constructor(
    internal val cByteBuffer: CPointer<grpc_byte_buffer>
): AutoCloseable {

    constructor(slice: GrpcSlice): this(memScoped {
        grpc_raw_byte_buffer_create(slice.cSlice, 1u) ?: error("Failed to create byte buffer")
    })

    fun intoSlice(): GrpcSlice {
        memScoped {
            val resp_slice = alloc<grpc_slice>()
            grpc_byte_buffer_dump_to_single_slice(cByteBuffer, resp_slice.ptr)
            return GrpcSlice(resp_slice.readValue())
        }
    }

    override fun close() {
        grpc_byte_buffer_destroy(cByteBuffer)
    }

}