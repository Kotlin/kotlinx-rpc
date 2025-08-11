/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc

import cnames.structs.grpc_channel
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.internal.ClientCall
import kotlinx.rpc.grpc.internal.GrpcCallOptions
import kotlinx.rpc.grpc.internal.GrpcChannel
import kotlinx.rpc.grpc.internal.MethodDescriptor
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
    return NativeManagedChannel(
        target = "localhost:50051",
        credentials = GrpcCredentials(
            grpc_insecure_credentials_create()
                ?: error("Failed to create credentials")
        )
    )
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
) : ManagedChannel, ManagedChannelPlatform() {

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
        TODO("Not yet implemented")
    }

    override fun shutdown(): ManagedChannel {
        TODO("Not yet implemented")
    }

    override fun shutdownNow(): ManagedChannel {
        TODO("Not yet implemented")
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
