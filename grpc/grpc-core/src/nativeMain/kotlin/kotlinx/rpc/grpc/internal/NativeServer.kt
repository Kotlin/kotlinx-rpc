/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_server
import cnames.structs.grpc_server_credentials
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
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
internal class GrpcServerCredentials(
    internal val raw: CPointer<grpc_server_credentials>,
) {
    val rawCleaner = createCleaner(raw) {
        grpc_server_credentials_release(it)
    }

    companion object {
        fun createInsecure(): GrpcServerCredentials = GrpcServerCredentials(
            grpc_insecure_server_credentials_create() ?: error("Failed to create server credentials")
        )
    }
}

internal typealias MethodTag = COpaquePointer

internal data class RegisteredMethod(
    val methodDescriptor: ServerMethodDefinition<*, *>,
    val tag: MethodTag,
)

internal class NativeServer(
    override val port: Int,
    @Suppress("Redundant")
    private val credentials: GrpcServerCredentials,
) : Server {

    // a reference to make sure the grpc_init() was called. (it is released after shutdown)
    @Suppress("unused")
    private val rt = GrpcRuntime.acquire()

    private val cq = CompletionQueue()

    val raw: CPointer<grpc_server> = grpc_server_create(null, null)!!

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_server_destroy(it)
    }

    // holds all stable references to MethodAllocationCtx objects.
    // the stable references must eventually be disposed.
    private val methodAllocationCtxs = mutableSetOf<StableRef<MethodAllocationCtx>>()

    @Suppress("unused")
    private val methodAllocationCtxCleaner = createCleaner(methodAllocationCtxs) { refs ->
        refs.forEach { it.dispose() }
    }

    init {
        grpc_server_register_completion_queue(raw, cq.raw, null)
        grpc_server_add_http2_port(raw, "localhost:$port", credentials.raw)
        addUnknownService()
    }

    private var started = false
    private var isShutdownInternal = false
    override val isShutdown: Boolean
        get() = isShutdownInternal

    private val isTerminatedInternal = CompletableDeferred<Unit>()
    override val isTerminated: Boolean
        get() = isTerminatedInternal.isCompleted

    override fun start(): Server {
        check(!started) { internalError("Server already started") }
        started = true
        grpc_server_start(raw)
        return this
    }

    fun addService(service: ServerServiceDefinition) {
        check(!started) { internalError("Server already started") }

        service.getMethods().forEach {
            val desc = it.getMethodDescriptor()
            // to construct a valid HTTP/2 path, we must prepend the name with a slash.
            // the user does not do this to align it with the java implementation.
            val name = "/" + desc.getFullMethodName()
            // TODO: don't hardcode localhost
            val tag = grpc_server_register_method(
                server = raw,
                method = name,
                host = "localhost:$port",
                payload_handling = grpc_server_register_method_payload_handling.GRPC_SRM_PAYLOAD_NONE,
                flags = 0u
            ) ?: error("Failed to register method: $name")

            val ctx = StableRef.create(
                RegisteredMethodAllocationCtx(
                    server = this,
                    method = it,
                    cq = cq,
                )
            )
            methodAllocationCtxs.add(ctx)
            
            kgrpc_server_set_register_method_allocator(
                server = raw,
                cq = cq.raw,
                method_tag = tag,
                allocator_ctx = ctx.asCPointer(),
                allocator = staticCFunction(::methodAllocationCallback)
            )
        }
    }

    private fun addUnknownService() {
        val ctx = StableRef.create(
            MethodAllocationCtx(
                server = this,
                cq = cq,
            )
        )
        methodAllocationCtxs.add(ctx)

        kgrpc_server_set_batch_method_allocator(
            server = raw,
            cq = cq.raw,
            allocator_ctx = ctx.asCPointer(),
            allocator = staticCFunction(::batchMethodAllocationCallback)
        )
    }


    override fun shutdown(): Server {
        if (isShutdownInternal) {
            return this
        }
        isShutdownInternal = true

        grpc_server_shutdown_and_notify(raw, cq.raw, CallbackTag.anonymous {
            cq.shutdown().onComplete {
                methodAllocationCtxs.forEach { it.dispose() }
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
}

@CName("kgrpc_method_allocation_callback")
private fun methodAllocationCallback(ctx: COpaquePointer?): CValue<kgrpc_registered_call_allocation> {
    val ctx = ctx!!.asStableRef<RegisteredMethodAllocationCtx>().get()
    val request = ServerCallbackRequest(ctx.server, ctx.method, ctx.cq)
    return request.toRaw()
}

@CName("kgrpc_method_allocation_callback")
private fun batchMethodAllocationCallback(ctx: COpaquePointer?): CValue<kgrpc_batch_call_allocation> {
    val ctx = ctx!!.asStableRef<MethodAllocationCtx>().get()
    val request = BatchedCallbackRequest(ctx.server, ctx.cq)
    return request.toRaw()
}

private open class MethodAllocationCtx(
    val server: NativeServer,
    val cq: CompletionQueue,
)

private class RegisteredMethodAllocationCtx(
    server: NativeServer,
    cq: CompletionQueue,
    val method: ServerMethodDefinition<*, *>,
) : MethodAllocationCtx(server, cq)


