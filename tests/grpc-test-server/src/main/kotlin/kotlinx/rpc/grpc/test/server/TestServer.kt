/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.GreeterService
import kotlinx.rpc.registerService

public suspend fun main() {
    val server = GrpcServer(50051) {
        services {
            registerService<EchoService> { EchoServiceImpl()}
            registerService<GreeterService> { GreeterServiceImpl() }
        }
    }
    try {
        server.start()
        println("[GRPC-TEST-SERVER] Server started")
        server.awaitTermination()
    } finally {
        server.shutdown()
        server.awaitTermination()
    }
}