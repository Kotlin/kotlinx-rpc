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
        registry.configuration(callId)
        val service = InteropTestService(registry)
        val server = NettyServerBuilder.forAddress(InetSocketAddress(HOST, 0))
            .addService(
                ServerInterceptors.intercept(
                    service,
                    InteropMetadataInterceptor(registry),
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

    private class DisposableEndpoint(
        private val proxy: ResettingTcpProxy,
        private val server: Server,
        private val service: InteropTestService,
    ) {
        fun close() {
            try {
                proxy.close()
            } finally {
                try {
                    server.shutdownNow()
                    check(server.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                        "disposable gRPC endpoint did not terminate"
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
