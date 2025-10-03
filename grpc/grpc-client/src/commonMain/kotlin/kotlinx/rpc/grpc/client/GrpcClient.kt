/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.client.internal.GrpcDefaultCallOptions
import kotlinx.rpc.grpc.client.internal.bidirectionalStreamingRpc
import kotlinx.rpc.grpc.client.internal.clientStreamingRpc
import kotlinx.rpc.grpc.client.internal.serverStreamingRpc
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.codec.EmptyMessageCodecResolver
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.codec.ThrowingMessageCodecResolver
import kotlinx.rpc.grpc.codec.plus
import kotlinx.rpc.grpc.descriptor.GrpcServiceDelegate
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.grpc.internal.MethodType
import kotlinx.rpc.grpc.internal.methodType
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

        return when (methodDescriptor.methodType) {
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

            else -> error("Wrong method type ${methodDescriptor.methodType}")
        }
    }

    override fun <T> callServerStreaming(call: RpcCall): Flow<T> = withGrpcCall(call) { methodDescriptor, request ->
        val callOptions = GrpcDefaultCallOptions
        val trailers = GrpcMetadata()

        when (methodDescriptor.methodType) {
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

            else -> error("Wrong method type ${methodDescriptor.methodType}")
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
 * Creates and configures a gRPC client instance.
 *
 * This function initializes a new gRPC client with the specified target server
 * and allows optional customization of the client's configuration through a configuration block.
 *
 * @param hostname The gRPC server hostname to connect to.
 * @param port The gRPC server port to connect to.
 * @param configure An optional configuration block to customize the [GrpcClientConfiguration].
 * This can include setting up interceptors, specifying credentials, customizing message codec
 * resolution, and overriding default authority.
 *
 * @return A new instance of [GrpcClient] configured with the specified target and options.
 *
 * @see [GrpcClientConfiguration]
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
 * Creates and configures a gRPC client instance.
 *
 * This function initializes a new gRPC client with the specified target server
 * and allows optional customization of the client's configuration through a configuration block.
 *
 * @param target The gRPC server endpoint to connect to, typically specified in
 * the format `hostname:port`.
 * @param configure An optional configuration block to customize the [GrpcClientConfiguration].
 * This can include setting up interceptors, specifying credentials, customizing message codec
 * resolution, and overriding default authority.
 *
 * @return A new instance of [GrpcClient] configured with the specified target and options.
 *
 * @see [GrpcClientConfiguration]
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


/**
 * Configuration class for a gRPC client, providing customization options
 * for client behavior, including interceptors, credentials, codec resolution,
 * and authority overrides.
 */
public class GrpcClientConfiguration internal constructor() {
    internal val interceptors: MutableList<ClientInterceptor> = mutableListOf()

    /**
     * Configurable resolver used to determine the appropriate codec for a given Kotlin type
     * during message serialization and deserialization in gRPC calls.
     *
     * Custom implementations of [MessageCodecResolver] can be provided to handle specific serialization
     * for arbitrary types.
     * For custom types prefer using the [kotlinx.rpc.grpc.codec.WithCodec] annotation.
     *
     * @see MessageCodecResolver
     * @see kotlinx.rpc.grpc.codec.SourcedMessageCodec
     * @see kotlinx.rpc.grpc.codec.WithCodec
     */
    public var messageCodecResolver: MessageCodecResolver = EmptyMessageCodecResolver


    /**
     * Configures the client credentials used for secure gRPC requests made by the client.
     *
     * By default, the client uses default TLS credentials.
     * To use custom TLS credentials, use the [tls] constructor function which returns a
     * [TlsClientCredentials] instance.
     *
     * To use plaintext communication, use the [plaintext] constructor function.
     * Should only be used for testing or for APIs where the use of such API or
     * the data exchanged is not sensitive.
     *
     * ```
     * GrpcClient("localhost", 50051) {
     *     credentials = plaintext() // for testing purposes only!
     * }
     * ```
     */
    public var credentials: ClientCredentials? = null

    /**
     * Overrides the authority used with TLS and HTTP virtual hosting.
     * It does not change what the host is actually connected to.
     * Is commonly in the form `host:port`.
     */
    public var overrideAuthority: String? = null


    /**
     * Adds one or more client-side interceptors to the current gRPC client configuration.
     * Interceptors enable extended customization of gRPC calls
     * by observing or altering the behaviors of requests and responses.
     *
     * The order of interceptors added via this method is significant.
     * Interceptors are executed in the order they are added,
     * while one interceptor has to invoke the next interceptor to proceed with the call.
     *
     * @param interceptors Interceptors to be added to the current configuration.
     * Each provided instance of [ClientInterceptor] may perform operations such as modifying headers,
     * observing call metadata, logging, or transforming data flows.
     *
     * @see ClientInterceptor
     * @see ClientCallScope
     */
    public fun intercept(vararg interceptors: ClientInterceptor) {
        this.interceptors.addAll(interceptors)
    }

    /**
     * Provides insecure client credentials for the gRPC client configuration.
     *
     * Typically, this would be used for local development, testing, or other
     * environments where security is not a concern.
     *
     * @return An insecure [ClientCredentials] instance that must be passed to [credentials].
     */
    public fun plaintext(): ClientCredentials = createInsecureClientCredentials()

    /**
     * Configures and creates secure client credentials for the gRPC client.
     *
     * This method takes a configuration block in which TLS-related parameters,
     * such as trust managers and key managers, can be defined. The resulting
     * credentials are used to establish secure communication between the gRPC client
     * and server, ensuring encrypted transmission of data and mutual authentication
     * if configured.
     *
     * Alternatively, you can use the [TlsClientCredentials] constructor.
     *
     * @param configure A configuration block that allows setting up the TLS parameters
     * using the [TlsClientCredentialsBuilder].
     * @return A secure [ClientCredentials] instance that must be passed to [credentials].
     *
     * @see credentials
     */
    public fun tls(configure: TlsClientCredentialsBuilder.() -> Unit): ClientCredentials =
        TlsClientCredentials(configure)

}