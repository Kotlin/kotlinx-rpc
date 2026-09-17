/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.ServerInterceptors
import io.grpc.netty.NettyServerBuilder
import java.net.InetSocketAddress

public fun main() {
    val registry = CallScenarioRegistry()
    val server = NettyServerBuilder.forAddress(InetSocketAddress("127.0.0.1", 50051))
        .addService(EchoServiceImpl())
        .addService(GreeterServiceImpl())
        .addService(
            ServerInterceptors.intercept(
                InteropTestService(),
                InteropMetadataInterceptor(registry),
            )
        )
        .addService(GrpcClientControlService(registry))
        .build()
    try {
        server.start()
        println("[GRPC-TEST-SERVER] Server started on 127.0.0.1:${server.port}; control protocol v1")
        server.awaitTermination()
    } finally {
        server.shutdown()
        server.awaitTermination()
    }
}
