/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, InternalRpcApi::class,
    InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.client.internal

import cnames.structs.grpc_channel
import cnames.structs.grpc_channel_credentials
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientCredentials
import kotlinx.rpc.grpc.client.createRaw
import kotlinx.rpc.grpc.client.rawDeadline
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.internal.CompletionQueue
import kotlinx.rpc.grpc.internal.GRPC_ARG_ABSOLUTE_MAX_METADATA_SIZE
import kotlinx.rpc.grpc.internal.GRPC_ARG_CLIENT_IDLE_TIMEOUT_MS
import kotlinx.rpc.grpc.internal.GRPC_ARG_KEEPALIVE_PERMIT_WITHOUT_CALLS
import kotlinx.rpc.grpc.internal.GRPC_ARG_KEEPALIVE_TIMEOUT_MS
import kotlinx.rpc.grpc.internal.GRPC_ARG_KEEPALIVE_TIME_MS
import kotlinx.rpc.grpc.internal.GRPC_ARG_MAX_METADATA_SIZE
import kotlinx.rpc.grpc.internal.GRPC_ARG_MAX_RECEIVE_MESSAGE_LENGTH
import kotlinx.rpc.grpc.internal.GrpcArg
import kotlinx.rpc.grpc.internal.GrpcRuntime
import kotlinx.rpc.grpc.internal.ResourceGuard
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.internal.toChannelArgMilliseconds
import kotlinx.rpc.grpc.internal.toGrpcSlice
import kotlinx.rpc.grpc.internal.toRaw
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.grpc.internal.cinterop.GRPC_PROPAGATE_DEFAULTS
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_create
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_create_call
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_credentials_release
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_destroy
import kotlinx.rpc.grpc.internal.cinterop.grpc_slice_unref
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlin.time.Duration

/**
 * Native implementation of [ManagedChannel].
 *
 * @param target The target address to connect to.
 * @property overrideAuthority Optional authority override for TLS and HTTP virtual hosting.
 * @property keepAlive Optional keep-alive configuration for the connection.
 * @param clientCredentials The credentials to use for the connection.
 */
internal class NativeManagedChannel(
    target: String,
    val overrideAuthority: String?,
    val keepAlive: GrpcClientConfiguration.KeepAlive?,
    val userAgent: String?,
    val maxInboundMessageSize: Int?,
    val maxInboundMetadataSize: Int?,
    val idleTimeout: Duration?,
    // this is not a composite channel credentials
    clientCredentials: GrpcClientCredentials,
) : ManagedChannel, ManagedChannelPlatform() {

    // a reference to make sure the grpc_init() was called. (it is released after shutdown)
    @Suppress("unused")
    private val rt = GrpcRuntime.acquire()

    // job bundling all the call jobs created by this channel.
    // this allows easy cancellation of ongoing calls.
    private val callJobSupervisor = SupervisorJob()

    // the channel's completion queue, handling all request operations
    private val cq = CompletionQueue()

    private val rawChannelCredentials: CPointer<grpc_channel_credentials> = clientCredentials.createRaw()

    internal val raw: CPointer<grpc_channel> = memScoped {
        val args = mutableListOf<GrpcArg>()

        overrideAuthority?.let {
            // the C Core API doesn't have a way to override the authority (used for TLS SNI) as it
            // is available in the Java gRPC implementation.
            // instead, it can be done by setting the "grpc.ssl_target_name_override" argument.
            args.add(GrpcArg.Str(
                    key = "grpc.ssl_target_name_override",
                    value = it
            ))
        }

        userAgent?.let {
            // GRPC_ARG_PRIMARY_USER_AGENT_STRING — prepended to the C-core's own User-Agent token.
            args.add(GrpcArg.Str(
                    key = "grpc.primary_user_agent",
                    value = it
            ))
        }

        maxInboundMessageSize?.let {
            args.add(GrpcArg.Integer(
                key = GRPC_ARG_MAX_RECEIVE_MESSAGE_LENGTH,
                value = it,
            ))
        }

        maxInboundMetadataSize?.let {
            args.add(GrpcArg.Integer(
                key = GRPC_ARG_MAX_METADATA_SIZE,
                value = it,
            ))
            args.add(GrpcArg.Integer(
                key = GRPC_ARG_ABSOLUTE_MAX_METADATA_SIZE,
                value = it,
            ))
        }

        idleTimeout?.let {
            args.add(GrpcArg.Integer(
                key = GRPC_ARG_CLIENT_IDLE_TIMEOUT_MS,
                value = it.toChannelArgMilliseconds(),
            ))
        }

        keepAlive?.let {
            args.add(GrpcArg.Integer(
                key = GRPC_ARG_KEEPALIVE_TIME_MS,
                value = it.time.toChannelArgMilliseconds(),
            ))
            args.add(GrpcArg.Integer(
                key = GRPC_ARG_KEEPALIVE_TIMEOUT_MS,
                value = it.timeout.toChannelArgMilliseconds(),
            ))
            args.add(GrpcArg.Integer(
                key = GRPC_ARG_KEEPALIVE_PERMIT_WITHOUT_CALLS,
                value = if (it.withoutCalls) 1 else 0
            ))
        }

        var rawArgs = if (args.isNotEmpty()) args.toRaw(this) else null

        grpc_channel_create(target, rawChannelCredentials, rawArgs?.ptr)
            ?: error("Failed to create channel")
    }

    // Guards to prevent double-free between explicit shutdown cleanup and GC cleaners.
    private val channelGuard = ResourceGuard()
    private val credentialsGuard = ResourceGuard()

    @Suppress("unused")
    private val rawCleaner = createCleaner(Pair(raw, channelGuard)) { (ptr, guard) ->
        if (guard.released.compareAndSet(expect = false, update = true)) {
            grpc_channel_destroy(ptr)
        }
    }
    @Suppress("unused")
    internal val rawCredentialsCleaner = createCleaner(Pair(rawChannelCredentials, credentialsGuard)) { (ptr, guard) ->
        if (guard.released.compareAndSet(expect = false, update = true)) {
            grpc_channel_credentials_release(ptr)
        }
    }

    override val platformApi: ManagedChannelPlatform = this

    private var isShutdownInternal = atomic(false)
    override val isShutdown: Boolean
        get() = isShutdownInternal.value
    private val isTerminatedInternal = CompletableDeferred<Unit>()
    override val isTerminated: Boolean
        get() = isTerminatedInternal.isCompleted

    override suspend fun awaitTermination(duration: Duration): Boolean {
        withTimeoutOrNull(duration) {
            isTerminatedInternal.await()
        } ?: return false
        return true
    }

    override fun shutdown(): ManagedChannel {
        shutdownInternal(false)
        return this
    }

    override fun shutdownNow(): ManagedChannel {
        shutdownInternal(true)
        return this
    }

    private fun shutdownInternal(force: Boolean) {
        isShutdownInternal.value = true
        if (isTerminatedInternal.isCompleted) {
            return
        }
        if (force) {
            // cancel all jobs, such that the shutdown is completing faster (not immediate).
            // TODO KRPC-552: replace jobs by custom pendingCallClass.
            callJobSupervisor.cancelChildren(CancellationException("Channel is shutting down"))
        }

        // wait for the completion queue to shut down.
        // the completion queue will be shut down after all requests are completed.
        // therefore, we don't have to wait for the callJobs to be completed.
        cq.shutdown(force).onComplete {
            if (isTerminatedInternal.complete(Unit)) {
                // Destroy the channel and release credentials BEFORE closing the runtime reference.
                // GrpcRuntime.close() may call grpc_shutdown() when this is the last ref,
                // and the grpc C API requires channels to be destroyed before grpc_shutdown().
                // The guards prevent double-free if the GC cleaners also fire.
                if (channelGuard.released.compareAndSet(expect = false, update = true)) {
                    grpc_channel_destroy(raw)
                }
                if (credentialsGuard.released.compareAndSet(expect = false, update = true)) {
                    grpc_channel_credentials_release(rawChannelCredentials)
                }
                rt.close()
            }
        }
    }

    override fun <RequestT, ResponseT> newCall(
        methodDescriptor: GrpcMethodDescriptor<RequestT, ResponseT>,
        callOptions: GrpcCallOptions,
        coroutineContext: CoroutineContext
    ): ClientCall<RequestT, ResponseT> {
        check(!isShutdown) { internalError("Channel is shutdown") }

        val methodFullName = methodDescriptor.getFullMethodName()
        // to construct a valid HTTP/2 path, we must prepend the name with a slash.
        // the user does not do this to align it with the java implementation.
        val methodNameSlice = "/$methodFullName".toGrpcSlice()

        val rawCall = grpc_channel_create_call(
            channel = raw,
            parent_call = null,
            propagation_mask = GRPC_PROPAGATE_DEFAULTS,
            completion_queue = cq.raw,
            method = methodNameSlice,
            host = null,
            deadline = callOptions.rawDeadline(),
            reserved = null
        ) ?: error("Failed to create call")

        grpc_slice_unref(methodNameSlice)

        return NativeClientCall(
            cq = cq,
            raw = rawCall,
            methodDescriptor =methodDescriptor,
            callOptions = callOptions,
            callJob = Job(callJobSupervisor),
            coroutineContext = coroutineContext,
        )
    }

}
