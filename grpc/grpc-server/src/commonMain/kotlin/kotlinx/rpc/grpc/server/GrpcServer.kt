/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.marshaller.GrpcEmptyMarshallerResolver
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlinx.rpc.grpc.server.internal.GrpcServerImpl
import kotlinx.rpc.grpc.server.internal.ServerBuilder
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

/**
 * Server for listening for and dispatching incoming calls.
 * It is not expected to be implemented by application code or interceptors.
 */
public interface GrpcServer : RpcServer {
    /**
     * Returns the port number the server is listening on.
     * This can return -1 if there is no actual port or the result otherwise doesn't make sense.
     * The result is undefined after the server is terminated.
     * If there are multiple possible ports, this will return one arbitrarily.
     * Implementations are encouraged to return the same port on each call.
     *
     * @throws [IllegalStateException] – if the server has not yet been started.
     */
    public val port: Int

    /**
     * Returns whether the server is shutdown.
     * Shutdown servers reject any new calls but may still have some calls being processed.
     */
    public val isShutdown: Boolean

    /**
     * Returns whether the server is terminated.
     * Terminated servers have no running calls and relevant resources released (like TCP connections).
     */
    public val isTerminated: Boolean

    /**
     * Bind and start the server.
     * After this call returns, clients may begin connecting to the listening socket(s).
     * @return `this`
     * @throws IllegalStateException if already started or shut down
     * @throws IOException if unable to bind
     */
    // TODO, What is IOException in KMP? KRPC-163
    public fun start(): GrpcServer

    /**
     * Initiates an orderly shutdown in which preexisting calls continue but new calls are rejected.
     * After this call returns, this server has released the listening socket(s) and may be reused by
     * another server.
     *
     * Note that this method will not wait for preexisting calls to finish before returning.
     * [awaitTermination] needs to be called to wait for existing calls to finish.
     *
     * Calling this method before [start] will shut down and terminate the server like
     * normal, but prevents starting the server in the future.
     *
     * @return `this`
     */
    public fun shutdown(): GrpcServer

    /**
     * Initiates a forceful shutdown in which preexisting and new calls are rejected. Although
     * forceful, the shutdown process is still not instantaneous; [isTerminated] will likely
     * return `false` immediately after this method returns. After this call returns, this
     * server has released the listening socket(s) and may be reused by another server.
     *
     * Calling this method before [start] will shut down and terminate the server like
     * normal, but prevents starting the server in the future.
     *
     * @return `this`
     */
    public fun shutdownNow(): GrpcServer

    /**
     * Waits for the server to become terminated, giving up if the timeout is reached.
     *
     * Calling this method before [start] or [shutdown] is permitted and doesn't
     * change its behavior.
     *
     * @return `this`
     */
    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE): GrpcServer
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
 *                  marshallers, and service registration logic.
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

    return GrpcServerImpl(
        port = port,
        serverBuilder = serverBuilder,
        interceptors = config.interceptors,
        messageMarshallerResolver = config.messageMarshallerResolver,
        marshallerConfig = config.marshallerConfig,
        parentContext = parentContext
    )
        .apply(config.serviceBuilder)
        .apply { build() }
}

/**
 * A configuration class for setting up a gRPC server.
 *
 * This class provides an API to configure various server parameters, such as message marshallers,
 * security credentials, server-side interceptors, and service registration.
 */
public class GrpcServerConfiguration internal constructor() {
    internal val interceptors: MutableList<GrpcServerInterceptor> = mutableListOf()
    internal var serviceBuilder: RpcServer.() -> Unit = { }

    /**
     * Sets the credentials to be used by the gRPC server for secure communication.
     *
     * By default, the server does not have any credentials configured and the communication is plaintext.
     * To set up transport-layer security provide a [GrpcTlsServerCredentials] by constructing it with the
     * [tls] function.
     *
     * @see GrpcTlsServerCredentials
     * @see tls
     */
    public var credentials: GrpcServerCredentials? = null

    /**
     * Sets a custom [GrpcMarshallerResolver] to be used by the gRPC server for resolving the appropriate
     * marshaller for message serialization and deserialization.
     *
     * When not explicitly set, a default [GrpcEmptyMarshallerResolver] is used, which may not perform
     * any specific resolution.
     * Provide a custom [GrpcMarshallerResolver] to resolve marshallers based on the message's `KType`.
     */
    public var messageMarshallerResolver: GrpcMarshallerResolver = GrpcEmptyMarshallerResolver

    public var marshallerConfig: GrpcMarshallerConfig? = null

    /**
     * Sets a custom [GrpcHandlerRegistry] to be used by the gRPC server for resolving service implementations
     * that were not registered before via the [services] configuration block.
     *
     * If not set, unknown services not registered will cause a `UNIMPLEMENTED` status
     * to be returned to the client.
     */
    public var fallbackHandlerRegistry: GrpcHandlerRegistry? = null

    /**
     * Registers one or more server-side interceptors for the gRPC server.
     *
     * Interceptors allow observing and modifying incoming gRPC calls before they reach the service
     * implementation logic.
     * They are commonly used to implement cross-cutting concerns like
     * authentication, logging, metrics, or custom request/response transformations.
     *
     * @param interceptors One or more instances of [GrpcServerInterceptor] to be applied to incoming calls.
     * @see GrpcServerInterceptor
     */
    public fun intercept(vararg interceptors: GrpcServerInterceptor) {
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
     * @param configure A lambda to further customize the [GrpcTlsServerCredentialsBuilder], enabling configurations
     *                  like setting trusted root certificates or enabling client authentication.
     * @return An instance of [GrpcServerCredentials] representing the configured TLS credentials that must be passed
     * to [credentials].
     *
     * @see credentials
     */
    public fun tls(
        certificateChain: String,
        privateKey: String,
        configure: GrpcTlsServerCredentialsBuilder.() -> Unit,
    ): GrpcServerCredentials = GrpcTlsServerCredentials(certificateChain, privateKey, configure)
}
