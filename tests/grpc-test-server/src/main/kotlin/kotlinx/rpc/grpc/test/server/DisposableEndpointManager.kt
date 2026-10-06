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
 * Owns isolated gRPC data endpoints used to simulate connection loss for configured scenarios.
 *
 * @param registry Registry shared with the main test server.
 */
internal class DisposableEndpointManager(
    private val registry: CallScenarioRegistry,
) : AutoCloseable {
    private val endpoints = ConcurrentHashMap<String, DisposableEndpoint>()

    /** Starts an endpoint for [callId] and returns its ephemeral port. */
    fun start(callId: String): Int {
        registry.configuration(callId) // fails with NOT_FOUND for an unknown scenario
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

    /** Stops the endpoint for [callId], resetting its active network connections. */
    fun stop(callId: String) {
        val endpoint = endpoints.remove(callId)
            ?: error("scenario '$callId' has no disposable endpoint")
        endpoint.close()
    }

    /** Stops the endpoint for [callId] when one exists. */
    fun stopIfPresent(callId: String) {
        endpoints.remove(callId)?.close()
    }

    /** Stops all managed endpoints. */
    override fun close() {
        endpoints.keys.toList().forEach(::stopIfPresent)
    }

    /**
     * One running disposable endpoint: a dedicated gRPC server that clients reach only through [proxy].
     *
     * Clients connect to the proxy's port, never to the server's own port. Data-plane calls still use the shared
     * registry, so their scenarios behave as on the main server.
     *
     * @param proxy Forwards client connections to [server] and resets them when the endpoint stops.
     * @param server The gRPC server serving [service] on an ephemeral loopback port.
     * @param service The endpoint's own `TestService`, whose response executor must be shut down with it.
     */
    private class DisposableEndpoint(
        private val proxy: ResettingTcpProxy,
        private val server: Server,
        private val service: InteropTestService,
    ) {
        /**
         * Resets all client connections first, then stops the server and the service's response executor.
         *
         * The order matters: closing the proxy first means the client sees its connections reset at the TCP level
         * and never receives the server's HTTP/2 shutdown frames. The server and service are stopped even if
         * closing the proxy fails.
         */
        fun close() {
            try {
                proxy.close()
            } finally {
                service.use {
                    server.shutdownNow()
                    check(server.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                        "disposable gRPC endpoint did not terminate"
                    }
                }
            }
        }
    }

    private companion object {
        const val HOST: String = "127.0.0.1"
        const val STOP_TIMEOUT_SECONDS: Long = 5
    }
}
