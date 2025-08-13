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
import libgrpcpp_c.grpc_channel_create
import libgrpcpp_c.grpc_channel_credentials_release
import libgrpcpp_c.grpc_channel_credentials_t
import libgrpcpp_c.grpc_channel_destroy
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


internal class NativeManagedChannel(
    private val target: String,
    // we must store them, otherwise the credentials are getting released
    private val credentials: GrpcCredentials,
    dispatcher: CoroutineDispatcher,
) : ManagedChannel, ManagedChannelPlatform() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(job + dispatcher)

    // the channel's completion queue, handling all request operations
    private val cq = CompletionQueue()

    internal val raw: CPointer<grpc_channel> = grpc_channel_create(target, credentials.raw, null)
        ?: error("Failed to create channel")
    private val rawCleaner = createCleaner(raw) {
        grpc_channel_destroy(it)
    }

    override val platformApi: ManagedChannelPlatform = this

    override val isShutdown: Boolean
        get() = TODO("Not yet implemented")
    override val isTerminated: Boolean
        get() = TODO("Not yet implemented")

    override suspend fun awaitTermination(duration: Duration): Boolean {
        cq.shutdown()
        job.join()
        return true
    }

    override fun shutdown(): ManagedChannel {
        coroutineScope.launch {
            cq.shutdown()
        }
        TODO("Return managed channel")
    }

    override fun shutdownNow(): ManagedChannel {
        // cancel all ongoing requests
        job.children.forEach { it.cancel("Channel (force) shutdown") }
        // after all requests got canceled, we shut down the completion queue
        shutdown()
        TODO("Return managed channel")
    }


    override fun <RequestT, ResponseT> newCall(
        methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
        callOptions: GrpcCallOptions,
    ): ClientCall<RequestT, ResponseT> {
        TODO("Not yet implemented")
    }

    override fun authority(): String {
        TODO("Not yet implemented")
    }

}


internal class GrpcCredentials(
    internal val raw: CPointer<grpc_channel_credentials_t>,
) {
    val rawCleaner = createCleaner(raw) {
        grpc_channel_credentials_release(it)
    }
}
