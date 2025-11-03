/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.client.internal

import cnames.structs.grpc_channel
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.grpc.client.ClientCredentials
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
import libkgrpc.grpc_channel_destroy
import libkgrpc.grpc_slice_unref
import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlin.time.Duration


/**
 * Native implementation of [ManagedChannel].
 *
 * @param target The target address to connect to.
 * @param credentials The credentials to use for the connection.
 */
internal class NativeManagedChannel(
    target: String,
    val authority: String?,
    // we must store them, otherwise the credentials are getting released
    credentials: ClientCredentials,
) : ManagedChannel, ManagedChannelPlatform() {

    // a reference to make sure the grpc_init() was called. (it is released after shutdown)
    @Suppress("unused")
    private val rt = GrpcRuntime.acquire()

    // job bundling all the call jobs created by this channel.
    // this allows easy cancellation of ongoing calls.
    private val callJobSupervisor = SupervisorJob()

    // the channel's completion queue, handling all request operations
    private val cq = CompletionQueue()

    internal val raw: CPointer<grpc_channel> = memScoped {
        val args = authority?.let {
            // the C Core API doesn't have a way to override the authority (used for TLS SNI) as it
            // is available in the Java gRPC implementation.
            // instead, it can be done by setting the "grpc.ssl_target_name_override" argument.
            val authorityOverride = alloc<grpc_arg> {
                type = grpc_arg_type.GRPC_ARG_STRING
                key = "grpc.ssl_target_name_override".cstr.ptr
                value.string = authority.cstr.ptr
            }

            alloc<grpc_channel_args> {
                num_args = 1u
                args = authorityOverride.ptr
            }
        }
        grpc_channel_create(target, credentials.raw, args?.ptr)
            ?: error("Failed to create channel")
    }

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_channel_destroy(it)
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
