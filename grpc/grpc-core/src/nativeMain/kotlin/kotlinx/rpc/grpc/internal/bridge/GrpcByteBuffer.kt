/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal.bridge

import kotlinx.cinterop.*
import libgrpcpp_c.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
internal class GrpcByteBuffer internal constructor(
    internal val cByteBuffer: CPointer<grpc_byte_buffer>
) {

    constructor(slice: GrpcSlice) : this(memScoped {
        grpc_raw_byte_buffer_create(slice.cSlice, 1u) ?: error("Failed to create byte buffer")
    })

    init {
        createCleaner(cByteBuffer) {
            grpc_byte_buffer_destroy(it)
        }
    }

    fun intoSlice(): GrpcSlice {
        memScoped {
            val respSlice = alloc<grpc_slice>()
            grpc_byte_buffer_dump_to_single_slice(cByteBuffer, respSlice.ptr)
            return GrpcSlice(respSlice.readValue())
        }
    }

}
