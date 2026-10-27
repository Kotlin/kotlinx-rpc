/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.ServerInterceptors
import io.grpc.netty.NettyServerBuilder
import java.net.InetSocketAddress

/** Starts the server on the port from the first argument. */
public fun main(args: Array<String>) {
    val port = args.singleOrNull()?.toIntOrNull()
        ?: error("Usage: grpc-test-server <port>")
    val registry = CallScenarioRegistry()
    val interopService = InteropTestService(registry)
    val scenarioInterceptor = ScenarioInterceptor(registry)
    val disposableEndpoints = DisposableEndpointManager(registry)
    val server = NettyServerBuilder.forAddress(InetSocketAddress("127.0.0.1", port))
        .addService(EchoServiceImpl())
        .addService(GreeterServiceImpl())
        .addService(ServerInterceptors.intercept(interopService, scenarioInterceptor))
        .addService(ServerInterceptors.intercept(InvalidResponseCountTestService(registry), scenarioInterceptor))
        .addService(GrpcClientControlService(registry, disposableEndpoints))
        .build()
    try {
        server.start()
        println("[GRPC-TEST-SERVER] Server started on 127.0.0.1:${server.port}")
        server.awaitTermination()
    } finally {
        server.shutdown()
        server.awaitTermination()
        disposableEndpoints.close()
        interopService.close()
    }
}
