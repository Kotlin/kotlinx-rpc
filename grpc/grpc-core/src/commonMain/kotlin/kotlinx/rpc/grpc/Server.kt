/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlin.time.Duration

/**
 * Platform-specific gRPC server builder.
 */
public expect abstract class ServerBuilder<T : ServerBuilder<T>> {
    /**
     * Adds a service implementation to the handler registry.
     *
     * @return `this`
     */
    public abstract fun addService(service: ServerServiceDefinition): T

    /**
     * Sets a fallback handler registry that will be looked up in if a method is not found in the
     * primary registry.
     * The primary registry (configured via [addService]) is faster but immutable.
     * The fallback registry is more flexible and allows implementations to mutate over
     * time and load services on-demand.
     *
     * @return `this`
     */
    public abstract fun fallbackHandlerRegistry(registry: HandlerRegistry?): T
}

internal expect fun ServerBuilder(port: Int): ServerBuilder<*>

/**
 * Server for listening for and dispatching incoming calls.
 * It is not expected to be implemented by application code or interceptors.
 */
public interface Server {
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
    public fun start(): Server

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
    public fun shutdown(): Server

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
    public fun shutdownNow(): Server

    /**
     * Waits for the server to become terminated, giving up if the timeout is reached.
     *
     * Calling this method before [start] or [shutdown] is permitted and doesn't
     * change its behavior.
     *
     * @return `this`
     */
    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE): Server
}

internal expect fun Server(builder: ServerBuilder<*>): Server
