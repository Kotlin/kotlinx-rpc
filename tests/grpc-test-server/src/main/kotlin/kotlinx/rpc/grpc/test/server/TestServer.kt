/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.netty.NettyServerBuilder

public fun main() {
    val registry = CallScenarioRegistry()
    val server = NettyServerBuilder.forPort(50051)
        .addService(EchoServiceImpl())
        .addService(GreeterServiceImpl())
        .addService(GrpcClientControlService(registry))
        .build()
    try {
        server.start()
        println("[GRPC-TEST-SERVER] Server started")
        server.awaitTermination()
    } finally {
        server.shutdown()
        server.awaitTermination()
    }
}
