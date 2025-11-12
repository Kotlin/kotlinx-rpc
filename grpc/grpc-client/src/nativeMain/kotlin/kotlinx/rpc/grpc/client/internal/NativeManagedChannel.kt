/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.client.internal

import cnames.structs.grpc_call_credentials
import cnames.structs.grpc_channel
import cnames.structs.grpc_channel_credentials
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.rawDeadline
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.grpc.internal.CompletionQueue
import kotlinx.rpc.grpc.internal.GrpcRuntime
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.internal.toGrpcSlice
import libkgrpc.GRPC_PROPAGATE_DEFAULTS
import libkgrpc.grpc_arg
import libkgrpc.grpc_arg_type
import libkgrpc.grpc_channel_args
import libkgrpc.grpc_channel_create
import libkgrpc.grpc_channel_create_call
import libkgrpc.grpc_channel_credentials_release
import libkgrpc.grpc_channel_destroy
import libkgrpc.grpc_composite_channel_credentials_create
import libkgrpc.grpc_slice_unref
import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlin.time.Duration


/**
 * Native implementation of [ManagedChannel].
 *
 * @param target The target address to connect to.
 * @param rawChannelCredentials The credentials to use for the connection.
 */
internal class NativeManagedChannel(
    target: String,
    val authority: String?,
    val keepAlive: GrpcClientConfiguration.KeepAlive?,
    // this is not a composite channel credentials
    val rawChannelCredentials: CPointer<grpc_channel_credentials>,
    val rawCallCredentials: CPointer<grpc_call_credentials>?,
) : ManagedChannel, ManagedChannelPlatform() {

    // a reference to make sure the grpc_init() was called. (it is released after shutdown)
    @Suppress("unused")
    private val rt = GrpcRuntime.acquire()

    // job bundling all the call jobs created by this channel.
    // this allows easy cancellation of ongoing calls.
    private val callJobSupervisor = SupervisorJob()

    // the channel's completion queue, handling all request operations
    private val cq = CompletionQueue()

    private val rawCompositeCredentials = rawCallCredentials?.let {
        grpc_composite_channel_credentials_create(rawChannelCredentials, it, null)
    }

    internal val raw: CPointer<grpc_channel> = memScoped {
        val args = mutableListOf<GrpcArg>()

        authority?.let {
            // the C Core API doesn't have a way to override the authority (used for TLS SNI) as it
            // is available in the Java gRPC implementation.
            // instead, it can be done by setting the "grpc.ssl_target_name_override" argument.
            args.add(GrpcArg.Str(
                    key = "grpc.ssl_target_name_override",
                    value = it
            ))
        }

        keepAlive?.let {
            args.add(GrpcArg.Integer(
                    key = "grpc.keepalive_time_ms",
                    value = it.time.inWholeMilliseconds.convert()
            ))
            args.add(GrpcArg.Integer(
                key = "grpc.keepalive_timeout_ms",
                value = it.timeout.inWholeMilliseconds.convert()
            ))
            args.add(GrpcArg.Integer(
                key = "grpc.keepalive_permit_without_calls",
                value = if (it.withoutCalls) 1 else 0
            ))
        }

        var rawArgs = if (args.isNotEmpty()) args.toRaw(this) else null

        // if we have composite credentials, which bundles call credentials and channel credentials,
        // we use it. Otherwise, we use the channel credentials alone.
        var credentials = rawCompositeCredentials ?: rawChannelCredentials
        grpc_channel_create(target, credentials, rawArgs?.ptr)
            ?: error("Failed to create channel")
    }

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_channel_destroy(it)
    }
    @Suppress("unused")
    internal val rawCredentialsCleaner = createCleaner(rawChannelCredentials) {
        grpc_channel_credentials_release(it)
    }

    @Suppress("unused")
    internal val rawCompositeCredentialsCleaner = createCleaner(rawCompositeCredentials) {
        if (it != null) {
            grpc_channel_credentials_release(it)
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
            // TODO: replace jobs by custom pendingCallClass.
            callJobSupervisor.cancelChildren(CancellationException("Channel is shutting down"))
        }

        // wait for the completion queue to shut down.
        // the completion queue will be shut down after all requests are completed.
        // therefore, we don't have to wait for the callJobs to be completed.
        cq.shutdown(force).onComplete {
            if (isTerminatedInternal.complete(Unit)) {
                // release the grpc runtime, so it might call grpc_shutdown()
                rt.close()
            }
        }
    }

    override fun <RequestT, ResponseT> newCall(
        methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
        callOptions: GrpcCallOptions,
    ): ClientCall<RequestT, ResponseT> {
        check(!isShutdown) { internalError("Channel is shutdown") }

        val callJob = Job(callJobSupervisor)

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
            cq, rawCall, methodDescriptor, callOptions, callJob
        )
    }

}

internal sealed class GrpcArg(val key: String) {
    internal class Str(key: String, val value: String) : GrpcArg(key)
    internal class Integer(key: String, val value: Int) : GrpcArg(key)

    internal val rawType: grpc_arg_type
        get() = when (this) {
            is Str -> grpc_arg_type.GRPC_ARG_STRING
            is Integer -> grpc_arg_type.GRPC_ARG_INTEGER
        }
}

private fun List<GrpcArg>.toRaw(memScope: MemScope): grpc_channel_args {
    with(memScope) {
        val arr = allocArray<grpc_arg>(size) {
            val arg = get(it)
            type = arg.rawType
            key = arg.key.cstr.ptr
            when (arg) {
                is GrpcArg.Str -> value.string = arg.value.cstr.ptr
                is GrpcArg.Integer -> value.integer = arg.value.convert()
            }
        }

        return alloc<grpc_channel_args> {
            num_args = size.convert()
            args = arr
        }
    }
}