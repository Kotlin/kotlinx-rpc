/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc

import cnames.structs.grpc_channel
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.*
import kotlinx.rpc.grpc.internal.*
import libgrpcpp_c.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlin.time.Duration

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
public actual abstract class ManagedChannelPlatform : GrpcChannel()

/**
 * Builder class for [ManagedChannel].
 */
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>> {
    public actual fun usePlaintext(): T {
        TODO("Not yet implemented")
    }
}

internal actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    TODO("Not yet implemented")
//    return NativeManagedChannel(
//        target = "localhost:50051",
//        credentials = GrpcCredentials(
//            grpc_insecure_credentials_create()
//                ?: error("Failed to create credentials")
//        ),
//
//    )
}

internal actual fun ManagedChannelBuilder(hostname: String, port: Int): ManagedChannelBuilder<*> {
    error("Native target is not supported in gRPC")
}

internal actual fun ManagedChannelBuilder(target: String): ManagedChannelBuilder<*> {
    error("Native target is not supported in gRPC")
}


private const val GRPC_PROPAGATE_DEFAULTS = 0x0000FFFFu

internal class NativeManagedChannel(
    target: String,
    // we must store them, otherwise the credentials are getting released
    credentials: GrpcCredentials,
) : ManagedChannel, ManagedChannelPlatform() {

    // a reference to make sure the grpc_init() was called. (it is released after shutdown)
    @Suppress("unused")
    private val rt = GrpcRuntime.acquire()

    // job bundling all the call jobs created by this channel.
    // this allows easy cancellation of ongoing calls.
    private val callJobSupervisor = SupervisorJob()

    // the channel's completion queue, handling all request operations
    private val cq = CompletionQueue()

    internal val raw: CPointer<grpc_channel> = grpc_channel_create(target, credentials.raw, null)
        ?: error("Failed to create channel")

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_channel_destroy(it)
    }

    override val platformApi: ManagedChannelPlatform = this

    private var isShutdownInternal: Boolean = false
    override val isShutdown: Boolean = isShutdownInternal
    private var isTerminatedInternal = CompletableDeferred<Unit>()
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
        isShutdownInternal = true
        if (isTerminatedInternal.isCompleted) {
            return
        }
        if (force) {
            // TODO: replace jobs by custom pendingCallClass.
            callJobSupervisor.cancelChildren(CancellationException("Channel is shutting down"))
        }

        // wait for the completion queue to shut down.
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
        check(!isShutdown) { "Channel is shutdown" }

        val callJob = Job(callJobSupervisor)

        val methodNameSlice = methodDescriptor.getFullMethodName().toGrpcSlice()
        val rawCall = grpc_channel_create_call(
            channel = raw, parent_call = null, propagation_mask = GRPC_PROPAGATE_DEFAULTS, completion_queue = cq.raw,
            method = methodNameSlice, host = null, deadline = gpr_inf_future(GPR_CLOCK_REALTIME), reserved = null
        ) ?: error("Failed to create call")

        return NativeClientCall(
            cq, rawCall, methodDescriptor, callJob
        )
    }

    override fun authority(): String {
        TODO("Not yet implemented")
    }

}


internal sealed class GrpcCredentials(
    internal val raw: CPointer<grpc_channel_credentials_t>,
) {
    val rawCleaner = createCleaner(raw) {
        grpc_channel_credentials_release(it)
    }
}

internal class GrpcInsecureCredentials() :
    GrpcCredentials(grpc_insecure_credentials_create() ?: error("Failed to create credentials"))


