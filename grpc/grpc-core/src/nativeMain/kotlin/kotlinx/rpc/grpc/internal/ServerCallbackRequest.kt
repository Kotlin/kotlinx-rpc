/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.rpc.grpc.GrpcTrailers
import libkgrpc.gpr_timespec
import libkgrpc.grpc_call_details
import libkgrpc.grpc_call_details_destroy
import libkgrpc.grpc_metadata_array
import libkgrpc.grpc_metadata_array_destroy
import libkgrpc.kgrpc_batch_call_allocation
import libkgrpc.kgrpc_registered_call_allocation
import kotlin.experimental.ExperimentalNativeApi

internal class ServerCallbackRequest<Request, Response>(
    val server: NativeServer,
    val method: ServerMethodDefinition<Request, Response>,
    val cq: CompletionQueue,
) : CallbackTag {
    val arena = Arena()
    val rawCall = arena.alloc<CPointerVar<grpc_call>>()
    val rawDeadline = arena.alloc<gpr_timespec>()
    val rawRequestMetadata = arena.alloc<grpc_metadata_array>()

    // the run() method disposes the callback request.
    // so this is a self-disposing mechanism.
    fun dispose() {
        grpc_metadata_array_destroy(rawRequestMetadata.ptr)
        arena.clear()
    }

    fun toRaw(): CValue<kgrpc_registered_call_allocation> {
        return cValue {
            tag = toCbTag()
            call = rawCall.ptr
            initial_metadata = rawRequestMetadata.ptr
            deadline = rawDeadline.ptr
            cq = this@ServerCallbackRequest.cq.raw
            // we are currently not optimizing the initial client payload
            // for unary and server streaming (payload_handling is always GRPC_SRM_PAYLOAD_NONE)
            optional_payload = null
        }
    }

    override fun run(ok: Boolean) {
        try {
            if (!ok) {
                // the call has been shutdown.\
                return
            }

            // create a NativeServerCall to control the underlying core call.
            // ownership of the core call is transferred to the NativeServerCall.
            val call = NativeServerCall(rawCall.value!!, cq, method.getMethodDescriptor())
            // TODO: Turn metadata into a kotlin GrpcTrailers.
            val trailers = GrpcTrailers()
            // start the actual call.
            val listener = method.getServerCallHandler().startCall(call, trailers)
            call.setListener(listener)
        } finally {
            // at this point, all return values have been transformed into kotlin ones,
            // so we can safely clear all resources.
            dispose()
        }
    }
}

internal class BatchedCallbackRequest(
    val server: NativeServer,
    val cq: CompletionQueue,
) : CallbackTag {
    val arena = Arena()
    val rawCall = arena.alloc<CPointerVar<grpc_call>>()
    val rawDeadline = arena.alloc<gpr_timespec>()
    val rawRequestMetadata = arena.alloc<grpc_metadata_array>()
    val rawDetails = arena.alloc<grpc_call_details>()

    // the run() method disposes the callback request.
    // so this is a self-disposing mechanism.
    fun dispose() {
        grpc_metadata_array_destroy(rawRequestMetadata.ptr)
        grpc_call_details_destroy(rawDetails.ptr)
        arena.clear()
    }

    fun toRaw(): CValue<kgrpc_batch_call_allocation> {
        return cValue {
            tag = toCbTag()
            call = rawCall.ptr
            initial_metadata = rawRequestMetadata.ptr
            details = rawDetails.ptr
            cq = this@BatchedCallbackRequest.cq.raw
        }
    }

    override fun run(ok: Boolean) {
        val host = rawDetails.host.toByteArray().decodeToString()
        val method = rawDetails.method.toByteArray().decodeToString()
        println("Got unknown callback request trigger.")
        println("Host: $host")
        println("Method: $method")
        dispose()
    }
}

