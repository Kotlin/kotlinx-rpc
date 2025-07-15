/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.bridge

import kotlinx.cinterop.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import libgrpcpp_c.*


@OptIn(ExperimentalForeignApi::class)
internal class GrpcClient(target: String): AutoCloseable {
    private var clientPtr: CPointer<grpc_client_t> = grpc_client_create_insecure(target) ?: error("Failed to create client")

    fun callUnaryBlocking(method: String, req: GrpcSlice): GrpcSlice {
        memScoped {
            val result = alloc<grpc_slice>()
            grpc_client_call_unary_blocking(clientPtr, method, req.cSlice, result.ptr)
            return GrpcSlice(result.readValue())
        }
    }

    suspend fun callUnary(method: String, req: GrpcByteBuffer): GrpcByteBuffer = suspendCancellableCoroutine { continuation ->
            val context = grpc_context_create()
            val method = grpc_method_create(method)

            val req_raw_buf = nativeHeap.alloc<CPointerVar<grpc_byte_buffer>>()
            req_raw_buf.value = req.cByteBuffer

            val resp_raw_buf: CPointerVar<grpc_byte_buffer> = nativeHeap.alloc()

            val continueCb = { st: grpc_status_code_t ->
                // cleanup allocations owned by this method (this runs always)
                grpc_method_delete(method)
                grpc_context_delete(context)
                nativeHeap.free(req_raw_buf)

                if (st != GRPC_C_STATUS_OK) {
                    continuation.resumeWithException(RuntimeException("Call failed with code: $st"))
                } else {
                    val result = resp_raw_buf.value
                    if (result == null) {
                        continuation.resumeWithException(RuntimeException("No response received"))
                    } else {
                        continuation.resume(GrpcByteBuffer(result))
                    }
                }

                nativeHeap.free(resp_raw_buf)
            }
            val cbCtxStable = StableRef.create(continueCb)

            grpc_client_call_unary_callback(clientPtr, method, context, req_raw_buf.ptr, resp_raw_buf.ptr, cbCtxStable.asCPointer(), staticCFunction { st, ctx ->
                val cbCtxStable = ctx!!.asStableRef<(grpc_status_code_t) -> Unit>()
                cbCtxStable.get()(st)
                cbCtxStable.dispose()
            })
    }

    override fun close() {
        grpc_client_delete(clientPtr)
    }

}