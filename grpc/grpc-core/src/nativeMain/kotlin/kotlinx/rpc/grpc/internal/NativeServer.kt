/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_server
import cnames.structs.grpc_server_credentials
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.grpc.HandlerRegistry
import kotlinx.rpc.grpc.Server
import kotlinx.rpc.grpc.ServerServiceDefinition
import libkgrpc.grpc_insecure_server_credentials_create
import libkgrpc.grpc_server_add_http2_port
import libkgrpc.grpc_server_cancel_all_calls
import libkgrpc.grpc_server_create
import libkgrpc.grpc_server_credentials_release
import libkgrpc.grpc_server_destroy
import libkgrpc.grpc_server_register_completion_queue
import libkgrpc.grpc_server_register_method
import libkgrpc.grpc_server_register_method_payload_handling
import libkgrpc.grpc_server_shutdown_and_notify
import libkgrpc.grpc_server_start
import libkgrpc.kgrpc_batch_call_allocation
import libkgrpc.kgrpc_registered_call_allocation
import libkgrpc.kgrpc_server_set_batch_method_allocator
import libkgrpc.kgrpc_server_set_register_method_allocator
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlin.time.Duration

/**
 * Wrapper for [grpc_server_credentials].
 */
internal sealed class GrpcServerCredentials(
    internal val raw: CPointer<grpc_server_credentials>,
) {
    val rawCleaner = createCleaner(raw) {
        grpc_server_credentials_release(it)
    }
}

internal class GrpcInsecureServerCredentials :
    GrpcServerCredentials(grpc_insecure_server_credentials_create() ?: error("Failed to create server credentials"))


internal class NativeServer(
    override val port: Int,
    // we must reference them, otherwise the credentials are getting garbage collected
    @Suppress("Redundant")
    private val credentials: GrpcServerCredentials,
    services: List<ServerServiceDefinition>,
    val fallbackRegistry: HandlerRegistry,
) : Server {

    // a reference to make sure the grpc_init() was called. (it is released after shutdown)
    @Suppress("unused")
    private val rt = GrpcRuntime.acquire()

    private val cq = CompletionQueue()

    val raw: CPointer<grpc_server> = grpc_server_create(null, null)!!

    // holds all stable references to MethodAllocationCtx objects.
    // the stable references must eventually be disposed.
    private val callAllocationCtxs = mutableSetOf<StableRef<CallAllocationCtx>>()

    init {
        grpc_server_register_completion_queue(raw, cq.raw, null)
        grpc_server_add_http2_port(raw, "0.0.0.0:$port", credentials.raw)
        registerServices(services)
        setLookupCallAllocatorCallback()
    }

    private var started = false
    private var isShutdownInternal = atomic(false)
    override val isShutdown: Boolean
        get() = isShutdownInternal.value

    private val isTerminatedInternal = CompletableDeferred<Unit>()
    override val isTerminated: Boolean
        get() = isTerminatedInternal.isCompleted

    override fun start(): Server {
        check(!started) { internalError("Server already started") }
        started = true
        grpc_server_start(raw)
        return this
    }

    private fun dispose() {
        // disposed with completion of shutdown
        grpc_server_destroy(raw)
        callAllocationCtxs.forEach { it.dispose() }
        // release the grpc runtime, so grpc is shutdown if no other grpc servers are running.
        rt.close()
    }

    override fun shutdown(): Server {
        if (!isShutdownInternal.compareAndSet(expect = false, update = true)) {
            // shutdown only once
            return this
        }

        grpc_server_shutdown_and_notify(raw, cq.raw, CallbackTag.anonymous {
            cq.shutdown().onComplete {
                dispose()
                isTerminatedInternal.complete(Unit)
            }
        })
        return this
    }

    override fun shutdownNow(): Server {
        shutdown()
        grpc_server_cancel_all_calls(raw)
        return this
    }

    override suspend fun awaitTermination(duration: Duration): Server {
        withTimeoutOrNull(duration) {
            isTerminatedInternal.await()
        }
        return this
    }

    /**
     * Registers a list of server service definitions with the server.
     */
    private fun registerServices(services: List<ServerServiceDefinition>) {
        check(!started) { internalError("Server already started") }

        services.flatMap { it.getMethods() }.forEach {
            val desc = it.getMethodDescriptor()
            // to construct a valid HTTP/2 path, we must prepend the name with a slash.
            // the user does not do this to align it with the java implementation.
            val name = "/" + desc.getFullMethodName()
            val tag = grpc_server_register_method(
                server = raw,
                method = name,
                host = null,
                // we currently don't optimize unary calls by reading the message on connection
                payload_handling = grpc_server_register_method_payload_handling.GRPC_SRM_PAYLOAD_NONE,
                flags = 0u
            ) ?: error("Failed to register method: $name")

            val ctx = StableRef.create(
                RegisteredCallAllocationCtx(
                    server = this,
                    method = it,
                    cq = cq,
                )
            )
            callAllocationCtxs.add(ctx)

            // register the allocation callback for the method.
            // it is invoked by the grpc runtime to allocate a new call context for incoming requests.
            kgrpc_server_set_register_method_allocator(
                server = raw,
                cq = cq.raw,
                method_tag = tag,
                allocator_ctx = ctx.asCPointer(),
                allocator = staticCFunction(::registeredCallAllocationCallback)
            )
        }
    }

    /**
     * Configures the server with a callback to handle the allocation of method calls for incoming requests,
     * which were not registered before the server started (via [registerServices]).
     */
    private fun setLookupCallAllocatorCallback() {
        val ctx = StableRef.create(
            CallAllocationCtx(
                server = this,
                cq = cq,
            )
        )
        callAllocationCtxs.add(ctx)

        kgrpc_server_set_batch_method_allocator(
            server = raw,
            cq = cq.raw,
            allocator_ctx = ctx.asCPointer(),
            allocator = staticCFunction(::lookupCallAllocationCallback)
        )
    }

}

/**
 * Allocates and returns a registered call allocation for a given call context.
 */
@CName("kgrpc_method_allocation_callback")
private fun registeredCallAllocationCallback(ctx: COpaquePointer?): CValue<kgrpc_registered_call_allocation> {
    val ctx = ctx!!.asStableRef<RegisteredCallAllocationCtx>().get()
    val request = RegisteredServerCallTag(ctx.cq, ctx.method)
    return request.toRawCallAllocation()
}

/**
 * A static callback that is invoked by the grpc runtime to allocate a new [kgrpc_registered_call_allocation].
 * As the [LookupServerCallTag] is a [CallbackTag] it won't be garbage collected until the callback is executed.
 *
 * @param ctx A pointer to a [CallAllocationCtx] object
 */
@CName("kgrpc_method_allocation_callback")
private fun lookupCallAllocationCallback(ctx: COpaquePointer?): CValue<kgrpc_batch_call_allocation> {
    val ctx = ctx!!.asStableRef<CallAllocationCtx>().get()
    val request = LookupServerCallTag(ctx.cq, ctx.server.fallbackRegistry)
    return request.toRawCallAllocation()
}

/**
 * A context to pass dynamic information to the [lookupCallAllocationCallback] and
 * [registeredCallAllocationCallback] callbacks.
 */
private open class CallAllocationCtx(
    val server: NativeServer,
    val cq: CompletionQueue,
)

/**
 * A context to pass dynamic information to the [registeredCallAllocationCallback] callback.
 */
private class RegisteredCallAllocationCtx(
    server: NativeServer,
    cq: CompletionQueue,
    val method: ServerMethodDefinition<*, *>,
) : CallAllocationCtx(server, cq)


