/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.client.internal

import io.grpc.ClientCall
import io.grpc.Grpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientCredentials
import kotlinx.rpc.grpc.client.toJvm
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

@InternalRpcApi
public fun <RequestT, ResponseT> ManagedChannel.createCall(
    methodDescriptor: GrpcMethodDescriptor<RequestT, ResponseT>,
    callOptions: GrpcCallOptions,
    coroutineContext: CoroutineContext,
): ClientCall<RequestT, ResponseT> {
    check(this is JvmManagedChannel) {
        internalError("Wrong channel type, expected JvmManagedChannel")
    }
    return channel.newCall(methodDescriptor, callOptions.toJvm(coroutineContext))
}

/**
 * Builder class for [ManagedChannel].
 */
@InternalRpcApi
public actual typealias ManagedChannelBuilder<T> = io.grpc.ManagedChannelBuilder<T>

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    hostname: String,
    port: Int,
    credentials: GrpcClientCredentials?,
): ManagedChannelBuilder<*> {
    if (credentials != null) {
        return Grpc.newChannelBuilderForAddress(hostname, port, credentials.toJvm())
    }
    return io.grpc.ManagedChannelBuilder.forAddress(hostname, port)
}

@InternalRpcApi
public fun io.grpc.ManagedChannel.toKotlin(): ManagedChannel {
    return JvmManagedChannel(this)
}

private class JvmManagedChannel(val channel: io.grpc.ManagedChannel) : ManagedChannel {
    override val isShutdown: Boolean
        get() = channel.isShutdown

    override val isTerminated: Boolean
        get() = channel.isTerminated

    override suspend fun awaitTermination(duration: Duration): Boolean {
        val dispatchStart = System.nanoTime()
        diagnostic("await-dispatch", "timeout_ns=${duration.inWholeNanoseconds}")
        return withContext(Dispatchers.IO) {
            diagnostic("await-enter", "dispatch_ns=${System.nanoTime() - dispatchStart} timeout_ns=${duration.inWholeNanoseconds}")
            val start = System.nanoTime()
            try {
                val completed = channel.awaitTermination(duration.inWholeNanoseconds, TimeUnit.NANOSECONDS)
                diagnostic("await-exit", "elapsed_ns=${System.nanoTime() - start} completed=$completed")
                completed
            } catch (error: Throwable) {
                diagnostic("await-exception", "elapsed_ns=${System.nanoTime() - start} exception=$error")
                throw error
            }
        }
    }

    private fun diagnostic(event: String, detail: String = "") {
        println(
            "CI_CHANNEL_JVM utc=${Instant.now()} epoch_ms=${System.currentTimeMillis()} " +
                "mono_ns=${System.nanoTime()} channel=${hashCode()} event=$event " +
                "shutdown=${channel.isShutdown} terminated=${channel.isTerminated} $detail"
        )
    }

    override fun shutdown(): ManagedChannel {
        channel.shutdown()
        return this
    }

    override fun shutdownNow(): ManagedChannel {
        val start = System.nanoTime()
        diagnostic("shutdown-enter")
        try {
            channel.shutdownNow()
            diagnostic("shutdown-exit", "elapsed_ns=${System.nanoTime() - start}")
        } catch (error: Throwable) {
            diagnostic("shutdown-exception", "elapsed_ns=${System.nanoTime() - start} exception=$error")
            throw error
        }
        return this
    }
}

internal actual fun ManagedChannelBuilder<*>.buildChannel(config: GrpcClientConfiguration): ManagedChannel {
    config.keepAlive?.let {
        keepAliveTime(it.time.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        keepAliveTimeout(it.timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        keepAliveWithoutCalls(it.withoutCalls)
    }

    config.overrideAuthority?.let { overrideAuthority(it) }
    config.userAgent?.let { userAgent(it) }
    maxInboundMessageSize(config.maxInboundMessageSize)
    maxInboundMetadataSize(config.maxInboundMetadataSize)
    idleTimeout(
        config.idleTimeout.inWholeMilliseconds,
        TimeUnit.MILLISECONDS,
    )

    return build().toKotlin()
}
