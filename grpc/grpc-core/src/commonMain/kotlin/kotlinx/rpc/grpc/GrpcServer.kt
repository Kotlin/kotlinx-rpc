/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcServer
import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.descriptor.flowInvokator
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.descriptor.unaryInvokator
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.EmptyMessageCodecResolver
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.codec.ThrowingMessageCodecResolver
import kotlinx.rpc.grpc.codec.plus
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.grpc.internal.MethodType
import kotlinx.rpc.grpc.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.internal.bidiStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.clientStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.serverStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.type
import kotlinx.rpc.grpc.internal.unaryServerMethodDefinition
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlin.time.Duration

private typealias RequestServer = Any
private typealias ResponseServer = Any

/**
 * GrpcServer is an implementation of both [RpcServer] and [Server] interfaces,
 * providing the ability to host gRPC services.
 *
 * @property port Specifies the port used by the server to listen for incoming connections.
 * @param parentContext
 * @param serverBuilder exposes platform-specific Server builder.
 */
public class GrpcServer internal constructor(
    override val port: Int,
    private val serverBuilder: ServerBuilder<*>,
    private val interceptors: List<ServerInterceptor>,
    messageCodecResolver: MessageCodecResolver = EmptyMessageCodecResolver,
    parentContext: CoroutineContext = EmptyCoroutineContext,
) : RpcServer, Server {
    private val internalContext = SupervisorJob(parentContext[Job])
    private val internalScope = CoroutineScope(parentContext + internalContext)

    private val messageCodecResolver = messageCodecResolver + ThrowingMessageCodecResolver

    private var isBuilt = false
    private lateinit var internalServer: Server

    private val registry: MutableHandlerRegistry by lazy {
        MutableHandlerRegistry().apply { this@GrpcServer.serverBuilder.fallbackHandlerRegistry(this) }
    }

    private val localRegistry = RpcInternalConcurrentHashMap<KClass<*>, ServerServiceDefinition>()

    override fun <@Grpc Service : Any> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: () -> Service,
    ) {
        val service = serviceFactory()

        val definition: ServerServiceDefinition = getDefinition(service, serviceKClass)

        if (isBuilt) {
            registry.addService(definition)
        } else {
            this@GrpcServer.serverBuilder.addService(definition)
        }
    }

    override fun <Service : Any> deregisterService(serviceKClass: KClass<Service>) {
        localRegistry.remove(serviceKClass)?.let {
            registry.removeService(it)
        }
    }

    private fun <@Grpc Service : Any> getDefinition(
        service: Service,
        serviceKClass: KClass<Service>,
    ): ServerServiceDefinition {
        @Suppress("UNCHECKED_CAST")
        val descriptor = serviceDescriptorOf(serviceKClass) as? GrpcServiceDescriptor<Service>
            ?: error("Service $serviceKClass is not a gRPC service")

        val delegate = descriptor.delegate(messageCodecResolver)

        val methods = descriptor.callables.map {
            @Suppress("UNCHECKED_CAST")
            val methodDescriptor = delegate.getMethodDescriptor(it.name)
                    as? MethodDescriptor<RequestServer, ResponseServer>
                ?: error("Expected a gRPC method descriptor")

            // TODO: support per service and per method interceptors (KRPC-222)
            it.toDefinitionOn(methodDescriptor, service, interceptors)
        }

        return serverServiceDefinition(delegate.serviceDescriptor, methods)
    }

    private fun <@Grpc Service : Any> RpcCallable<Service>.toDefinitionOn(
        descriptor: MethodDescriptor<RequestServer, ResponseServer>,
        service: Service,
        interceptors: List<ServerInterceptor>,
    ): ServerMethodDefinition<RequestServer, ResponseServer> {
        return when (descriptor.type) {
            MethodType.UNARY -> {
                internalScope.unaryServerMethodDefinition(descriptor, returnType.kType, interceptors) { request ->
                    unaryInvokator.call(service, arrayOf(request)) as ResponseServer
                }
            }

            MethodType.CLIENT_STREAMING -> {
                internalScope.clientStreamingServerMethodDefinition(
                    descriptor,
                    returnType.kType,
                    interceptors
                ) { requests ->
                    unaryInvokator.call(service, arrayOf(requests)) as ResponseServer
                }
            }

            MethodType.SERVER_STREAMING -> {
                internalScope.serverStreamingServerMethodDefinition(
                    descriptor, returnType.kType, interceptors
                ) { request ->
                    @Suppress("UNCHECKED_CAST")
                    flowInvokator.call(service, arrayOf(request)) as Flow<ResponseServer>
                }
            }

            MethodType.BIDI_STREAMING -> {
                internalScope.bidiStreamingServerMethodDefinition(
                    descriptor,
                    returnType.kType,
                    interceptors
                ) { requests ->
                    @Suppress("UNCHECKED_CAST")
                    flowInvokator.call(service, arrayOf(requests)) as Flow<ResponseServer>
                }
            }

            MethodType.UNKNOWN -> {
                error("Unsupported method type ${descriptor.type} for ${descriptor.getFullMethodName()}")
            }
        }
    }

    private val buildLock = atomic(false)

    internal fun build() {
        if (buildLock.compareAndSet(expect = false, update = true)) {
            internalServer = Server(this@GrpcServer.serverBuilder)
            isBuilt = true
        }
    }

    override val isShutdown: Boolean
        get() = internalServer.isShutdown

    override val isTerminated: Boolean
        get() = internalServer.isTerminated

    override fun start(): GrpcServer {
        internalServer.start()
        return this
    }

    override fun shutdown(): GrpcServer {
        internalContext.cancel("Shutting down server")
        internalServer.shutdown()
        return this
    }

    override fun shutdownNow(): GrpcServer {
        internalContext.cancel("Shutting down server now")
        internalServer.shutdownNow()
        return this
    }

    override suspend fun awaitTermination(duration: Duration): GrpcServer {
        internalContext.join()
        internalServer.awaitTermination(duration)
        return this
    }
}

/**
 * Creates and configures a gRPC server instance.
 *
 * This function initializes a gRPC server with the provided port and a configuration block
 * ([GrpcServerConfiguration]).
 *
 * To start the server, call the [GrpcServer.start] method.
 * To clean up resources, call the [GrpcServer.shutdown] or [GrpcServer.shutdownNow] methods.
 *
 * ```kt
 * GrpcServer(port) {
 *     credentials = tls(myCertChain, myPrivateKey)
 *     services {
 *         registerService<MyService> { MyServiceImpl() }
 *         registerService<MyOtherService> { MyOtherServiceImpl() }
 *     }
 * }
 * ```
 *
 * @param port The port number where the gRPC server will listen for incoming connections.
 *             This must be a valid and available port on the host system.
 * @param parentContext The parent coroutine context used for managing server-related operations.
 *                      Defaults to an empty coroutine context if not specified.
 * @param configure A configuration lambda receiver,
 *                  allowing customization of server behavior such as credentials, interceptors,
 *                  codecs, and service registration logic.
 * @return A fully configured `GrpcServer` instance, which must be started explicitly to handle requests.
 */
public fun GrpcServer(
    port: Int,
    parentContext: CoroutineContext = EmptyCoroutineContext,
    configure: GrpcServerConfiguration.() -> Unit = {},
): GrpcServer {
    val config = GrpcServerConfiguration().apply(configure)
    val serverBuilder = ServerBuilder(port, config.credentials).apply {
        config.fallbackHandlerRegistry?.let { fallbackHandlerRegistry(it) }
    }
    return GrpcServer(port, serverBuilder, config.interceptors, config.messageCodecResolver, parentContext)
        .apply(config.serviceBuilder)
        .apply { build() }
}

/**
 * A configuration class for setting up a gRPC server.
 *
 * This class provides an API to configure various server parameters, such as message codecs,
 * security credentials, server-side interceptors, and service registration.
 */
public class GrpcServerConfiguration internal constructor() {

    internal val interceptors: MutableList<ServerInterceptor> = mutableListOf()
    internal var serviceBuilder: RpcServer.() -> Unit = { }


    /**
     * Sets the credentials to be used by the gRPC server for secure communication.
     *
     * By default, the server does not have any credentials configured and the communication is plaintext.
     * To set up transport-layer security provide a [TlsServerCredentials] by constructing it with the
     * [tls] function.
     *
     * @see TlsServerCredentials
     * @see tls
     */
    public var credentials: ServerCredentials? = null

    /**
     * Sets a custom [MessageCodecResolver] to be used by the gRPC server for resolving the appropriate
     * codec for message serialization and deserialization.
     *
     * When not explicitly set, a default [EmptyMessageCodecResolver] is used, which may not perform
     * any specific resolution.
     * Provide a custom [MessageCodecResolver] to resolve codecs based on the message's `KType`.
     */
    public var messageCodecResolver: MessageCodecResolver = EmptyMessageCodecResolver


    /**
     * Sets a custom [HandlerRegistry] to be used by the gRPC server for resolving service implementations
     * that were not registered before via the [services] configuration block.
     *
     * If not set, unknown services not registered will cause a `UNIMPLEMENTED` status
     * to be returned to the client.
     */
    public var fallbackHandlerRegistry: HandlerRegistry? = null

    /**
     * Registers one or more server-side interceptors for the gRPC server.
     *
     * Interceptors allow observing and modifying incoming gRPC calls before they reach the service
     * implementation logic.
     * They are commonly used to implement cross-cutting concerns like
     * authentication, logging, metrics, or custom request/response transformations.
     *
     * @param interceptors One or more instances of [ServerInterceptor] to be applied to incoming calls.
     * @see ServerInterceptor
     */
    public fun intercept(vararg interceptors: ServerInterceptor) {
        this.interceptors.addAll(interceptors)
    }

    /**
     * Configures the gRPC server to register services.
     *
     * This method allows defining a block of logic to configure an [RpcServer] instance,
     * where multiple services can be registered:
     * ```kt
     * GrpcServer(port) {
     *     services {
     *         registerService<MyService> { MyServiceImpl() }
     *         registerService<MyOtherService> { MyOtherServiceImpl() }
     *     }
     * }
     * ```
     *
     * @param block A lambda with [RpcServer] as its receiver, allowing service registration.
     */
    public fun services(block: RpcServer.() -> Unit) {
        serviceBuilder = block
    }

    /**
     * Configures and creates TLS (Transport Layer Security) credentials for the gRPC server.
     *
     * This method allows specifying the server's certificate chain, private key, and additional
     * configurations needed for setting up a secure communication channel over TLS.
     *
     * @param certificateChain A string representing the PEM-encoded certificate chain for the server.
     * @param privateKey A string representing the PKCS#8 formatted private key corresponding to the certificate.
     * @param configure A lambda to further customize the [TlsServerCredentialsBuilder], enabling configurations
     *                  like setting trusted root certificates or enabling client authentication.
     * @return An instance of [ServerCredentials] representing the configured TLS credentials that must be passed
     * to [credentials].
     *
     * @see credentials
     */
    public fun tls(
        certificateChain: String,
        privateKey: String,
        configure: TlsServerCredentialsBuilder.() -> Unit,
    ): ServerCredentials =
        TlsServerCredentials(certificateChain, privateKey, configure)
}