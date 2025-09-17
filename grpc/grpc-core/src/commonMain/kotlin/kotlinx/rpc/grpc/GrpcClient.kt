/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.grpc.codec.EmptyMessageCodecResolver
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.codec.ThrowingMessageCodecResolver
import kotlinx.rpc.grpc.codec.plus
import kotlinx.rpc.grpc.descriptor.GrpcServiceDelegate
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.grpc.internal.GrpcDefaultCallOptions
import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.grpc.internal.MethodType
import kotlinx.rpc.grpc.internal.bidirectionalStreamingRpc
import kotlinx.rpc.grpc.internal.clientStreamingRpc
import kotlinx.rpc.grpc.internal.serverStreamingRpc
import kotlinx.rpc.grpc.internal.type
import kotlinx.rpc.grpc.internal.unaryRpc
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlin.time.Duration

private typealias RequestClient = Any

/**
 * GrpcClient manages gRPC communication by providing implementation for making asynchronous RPC calls.
 *
 * @field channel The [ManagedChannel] used to communicate with remote gRPC services.
 */
public class GrpcClient internal constructor(
    internal val channel: ManagedChannel,
    messageCodecResolver: MessageCodecResolver = EmptyMessageCodecResolver,
    internal val interceptors: List<ClientInterceptor>,
) : RpcClient {
    private val delegates = RpcInternalConcurrentHashMap<String, GrpcServiceDelegate>()
    private val messageCodecResolver = messageCodecResolver + ThrowingMessageCodecResolver

    public fun shutdown() {
        delegates.clear()
        channel.shutdown()
    }

    public fun shutdownNow() {
        delegates.clear()
        channel.shutdownNow()
    }

    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE) {
        channel.awaitTermination(duration)
    }

    override suspend fun <T> call(call: RpcCall): T = withGrpcCall(call) { methodDescriptor, request ->
        val callOptions = GrpcDefaultCallOptions
        val trailers = GrpcMetadata()

        return when (methodDescriptor.type) {
            MethodType.UNARY -> unaryRpc(
                descriptor = methodDescriptor,
                request = request,
                callOptions = callOptions,
                trailers = trailers,
            )

            MethodType.CLIENT_STREAMING -> @Suppress("UNCHECKED_CAST") clientStreamingRpc(
                descriptor = methodDescriptor,
                requests = request as Flow<RequestClient>,
                callOptions = callOptions,
                trailers = trailers,
            )

            else -> error("Wrong method type ${methodDescriptor.type}")
        }
    }

    override fun <T> callServerStreaming(call: RpcCall): Flow<T> = withGrpcCall(call) { methodDescriptor, request ->
        val callOptions = GrpcDefaultCallOptions
        val trailers = GrpcMetadata()

        when (methodDescriptor.type) {
            MethodType.SERVER_STREAMING -> serverStreamingRpc(
                descriptor = methodDescriptor,
                request = request,
                callOptions = callOptions,
                trailers = trailers,
            )

            MethodType.BIDI_STREAMING -> @Suppress("UNCHECKED_CAST") bidirectionalStreamingRpc(
                descriptor = methodDescriptor,
                requests = request as Flow<RequestClient>,
                callOptions = callOptions,
                trailers = trailers,
            )

            else -> error("Wrong method type ${methodDescriptor.type}")
        }
    }

    private inline fun <T, R> withGrpcCall(call: RpcCall, body: (MethodDescriptor<RequestClient, T>, Any) -> R): R {
        require(call.arguments.size <= 1) {
            "Call parameter size must be 0 or 1, but ${call.arguments.size}"
        }

        val delegate = delegates.computeIfAbsent(call.descriptor.fqName) {
            val grpc = call.descriptor as? GrpcServiceDescriptor<*>
                ?: error("Expected a gRPC service")

            grpc.delegate(messageCodecResolver)
        }

        @Suppress("UNCHECKED_CAST")
        val methodDescriptor = delegate.getMethodDescriptor(call.callableName)
                as? MethodDescriptor<RequestClient, T>
            ?: error("Expected a gRPC method descriptor")

        val request = call.arguments.getOrNull(0) ?: Unit

        return body(methodDescriptor, request)
    }
}

/**
 * Constructor function for the [GrpcClient] class.
 */
public fun GrpcClient(
    hostname: String,
    port: Int,
    configure: GrpcClientConfiguration.() -> Unit = {},
): GrpcClient {
    val config = GrpcClientConfiguration().apply(configure)
    return GrpcClient(ManagedChannelBuilder(hostname, port, config.credentials), config)
}

/**
 * Constructor function for the [GrpcClient] class.
 */
public fun GrpcClient(
    target: String,
    configure: GrpcClientConfiguration.() -> Unit = {},
): GrpcClient {
    val config = GrpcClientConfiguration().apply(configure)
    return GrpcClient(ManagedChannelBuilder(target, config.credentials), config)
}

private fun GrpcClient(
    builder: ManagedChannelBuilder<*>,
    config: GrpcClientConfiguration,
): GrpcClient {
    val channel = builder.apply {
        config.overrideAuthority?.let { overrideAuthority(it) }
    }.buildChannel()
    return GrpcClient(channel, config.messageCodecResolver, config.interceptors)
}

public class GrpcClientConfiguration internal constructor() {
    internal var messageCodecResolver: MessageCodecResolver = EmptyMessageCodecResolver
    internal var credentials: ClientCredentials? = null
    internal var overrideAuthority: String? = null
    internal val interceptors: MutableList<ClientInterceptor> = mutableListOf()

    public fun usePlaintext() {
        credentials = createInsecureClientCredentials()
    }

    public fun useCredentials(credentials: ClientCredentials) {
        this@GrpcClientConfiguration.credentials = credentials
    }

    public fun overrideAuthority(authority: String) {
        overrideAuthority = authority
    }

    public fun useMessageCodecResolver(messageCodecResolver: MessageCodecResolver) {
        this.messageCodecResolver = messageCodecResolver
    }

    public fun intercept(vararg interceptors: ClientInterceptor) {
        this.interceptors.addAll(interceptors)
    }

}