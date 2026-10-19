/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.Server
import io.grpc.ServerInterceptors
import io.grpc.netty.NettyServerBuilder
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Owns the disposable endpoints. Each one is a separate gRPC data endpoint that simulates a lost connection for one
 * scenario.
 *
 * @param registry The registry that the main test server also uses.
 */
internal class DisposableEndpointManager(
    private val registry: CallScenarioRegistry,
) : AutoCloseable {
    private val endpoints = ConcurrentHashMap<String, DisposableEndpoint>()

    /** Starts an endpoint for [callId] and returns its ephemeral port. */
    fun start(callId: String): Int {
        registry.checkScenarioExists(callId)
        val service = InteropTestService(registry)
        val server = NettyServerBuilder.forAddress(InetSocketAddress(HOST, 0))
            .addService(
                ServerInterceptors.intercept(
                    service,
                    ScenarioInterceptor(registry),
                )
            )
            .build()
        var proxy: ResettingTcpProxy? = null

        try {
            server.start()
            proxy = ResettingTcpProxy(InetSocketAddress(HOST, server.port))
            val endpoint = DisposableEndpoint(proxy, server, service)
            check(endpoints.putIfAbsent(callId, endpoint) == null) {
                "scenario '$callId' already has a disposable endpoint"
            }
            return proxy.port
        } catch (error: Throwable) {
            proxy?.close()
            server.shutdownNow()
            service.close()
            throw error
        }
    }

    /** Stops the endpoint for [callId] and resets its open network connections. */
    fun stop(callId: String) {
        val endpoint = endpoints.remove(callId)
            ?: error("scenario '$callId' has no disposable endpoint")
        endpoint.close()
    }

    /** If the endpoint for [callId] exists, it will stop it and reset its open network connections. */
    fun stopIfPresent(callId: String) {
        endpoints.remove(callId)?.close()
    }

    /** Stops all managed endpoints. */
    override fun close() {
        endpoints.keys.toList().forEach(::stopIfPresent)
    }

    /**
     * One running disposable endpoint: a separate gRPC server that clients connect to only through [proxy].
     *
     * Data-plane calls use the shared registry, so their scenarios behave as on the main server.
     *
     * @param proxy Forwards client connections to [server] and resets them when the endpoint stops.
     * @param server The gRPC server for [service] on an ephemeral loopback port.
     * @param service The `TestService` of this endpoint. Its response executor must stop with the endpoint.
     */
    private class DisposableEndpoint(
        private val proxy: ResettingTcpProxy,
        private val server: Server,
        private val service: InteropTestService,
    ) {
        /**
         * Resets all client connections first, then stops the server and the response executor of the service.
         *
         * The proxy closes first, so the client sees a TCP reset and doesn't get the HTTP/2 shutdown frames of the
         * server. If the proxy fails to close, the function still stops the server and the service.
         */
        fun close() {
            try {
                proxy.close()
            } finally {
                try {
                    server.shutdownNow()
                    check(server.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                        "disposable gRPC endpoint didn't stop"
                    }
                } finally {
                    service.close()
                }
            }
        }
    }

    private companion object {
        const val HOST: String = "127.0.0.1"
        const val STOP_TIMEOUT_SECONDS: Long = 5
    }
}
