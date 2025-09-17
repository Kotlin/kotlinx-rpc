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
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.HandlerRegistry
import libkgrpc.gpr_timespec
import libkgrpc.grpc_call_details
import libkgrpc.grpc_call_details_destroy
import libkgrpc.grpc_metadata_array
import libkgrpc.grpc_metadata_array_destroy
import libkgrpc.grpc_status_code
import libkgrpc.kgrpc_batch_call_allocation
import libkgrpc.kgrpc_registered_call_allocation
import kotlin.experimental.ExperimentalNativeApi

/**
 * A [CallbackTag] that is passed to a completion queue and invoked when an incoming gRPC call for the
 * registered [method] must be handled.
 *
 * The [toRawCallAllocation] method provides a [kgrpc_registered_call_allocation] used by the grpc runtime
 * to pass the call context to this callback.
 *
 * An object of this type won't be garbage collected until the callback is executed.
 */
internal class RegisteredServerCallTag<Request, Response>(
    val cq: CompletionQueue,
    val method: ServerMethodDefinition<Request, Response>,
) : CallbackTag {
    val arena = Arena()
    val rawCall = arena.alloc<CPointerVar<grpc_call>>()
    val rawDeadline = arena.alloc<gpr_timespec>()
    val rawRequestMetadata = arena.alloc<grpc_metadata_array>()

    // the run() method disposes all resources
    private fun dispose() {
        grpc_metadata_array_destroy(rawRequestMetadata.ptr)
        arena.clear()
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
            val trailers = GrpcMetadata()
            // start the actual call.
            val listener = method.getServerCallHandler().startCall(call, trailers)
            call.setListener(listener)
        } finally {
            // at this point, all return values have been transformed into kotlin ones,
            // so we can safely clear all resources.
            dispose()
        }
    }

    fun toRawCallAllocation(): CValue<kgrpc_registered_call_allocation> {
        return cValue {
            tag = toCbTag()
            call = rawCall.ptr
            initial_metadata = rawRequestMetadata.ptr
            deadline = rawDeadline.ptr
            cq = this@RegisteredServerCallTag.cq.raw
            // we are currently not optimizing the initial client payload
            // for unary and server streaming (payload_handling is always GRPC_SRM_PAYLOAD_NONE)
            optional_payload = null
        }
    }
}

/**
 * A [CallbackTag] that is passed to a completion queue and invoked when an incoming gRPC call that was
 * not registered must be handled.
 * The gRPC runtime will provide information about the method being called in the [rawDetails] field.
 *
 * The [toRawCallAllocation] method provides a [kgrpc_registered_call_allocation] used by the grpc runtime
 * to pass the call context to this callback.
 *
 * An object of this type won't be garbage collected until the callback is executed.
 */
internal class LookupServerCallTag(
    val cq: CompletionQueue,
    val registry: HandlerRegistry,
) : CallbackTag {
    val arena = Arena()
    val rawCall = arena.alloc<CPointerVar<grpc_call>>()
    val rawDeadline = arena.alloc<gpr_timespec>()
    val rawRequestMetadata = arena.alloc<grpc_metadata_array>()
    val rawDetails = arena.alloc<grpc_call_details>()

    // the run() method disposes all resources
    private fun dispose() {
        grpc_metadata_array_destroy(rawRequestMetadata.ptr)
        grpc_call_details_destroy(rawDetails.ptr)
        arena.clear()
    }

    override fun run(ok: Boolean) {
        try {
            if (!ok) {
                return
            }

            var method = rawDetails.method.toByteArray().decodeToString()

            // gRPC preserves the '/' character in the method name,
            // while the method definition omits it and starts without '/'
            method = method.removePrefix("/")

            val definition = registry.lookupMethod(method)
            if (definition == null) {
                // the method isn't registered; closing with UNIMPLEMENTED
                val call = NativeServerCall<Any, Any>(rawCall.value!!, cq)
                call.cancel(grpc_status_code.GRPC_STATUS_UNIMPLEMENTED, "Method not found: $method")
            } else {
                @Suppress("UNCHECKED_CAST")
                run {
                    val callHandler = definition.getServerCallHandler() as ServerCallHandler<Any, Any>
                    val call = NativeServerCall(
                        rawCall.value!!,
                        cq,
                        definition.getMethodDescriptor() as MethodDescriptor<Any, Any>
                    )
                    // TODO: Turn metadata into a kotlin GrpcTrailers.
                    val metadata = GrpcMetadata()
                    val listener = callHandler.startCall(call, metadata)
                    call.setListener(listener)
                }
            }

        } finally {
            dispose()
        }
    }

    fun toRawCallAllocation(): CValue<kgrpc_batch_call_allocation> {
        return cValue {
            tag = toCbTag()
            call = rawCall.ptr
            initial_metadata = rawRequestMetadata.ptr
            details = rawDetails.ptr
            cq = this@LookupServerCallTag.cq.raw
        }
    }
}

