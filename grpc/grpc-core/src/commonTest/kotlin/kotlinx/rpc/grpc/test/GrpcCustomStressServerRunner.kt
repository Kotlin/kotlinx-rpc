/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.registerService
import kotlin.test.Test

/**
 * Standalone server runner for the custom echo stress setup.
 *
 * Run this on the platform you want to measure server performance for (e.g., JVM or Native).
 * The server listens on a fixed port and serves [CustomEchoService].
 */
class GrpcCustomStressServerRunner {

    @Test
    fun runServer() = runBlocking {
        val port = 18080
        val server = GrpcServer(
            port = port,
            builder = {
                registerService<CustomEchoService> { CustomEchoServiceImpl() }
            }
        )

        println("[GrpcCustomStressServerRunner] Starting server on port $port ...")
        try {
            server.start()
            println("[GrpcCustomStressServerRunner] Server started. Press any key to stop.")
//            readln()
//            server.shutdown()
            server.awaitTermination()
        } finally {
            println("[GrpcCustomStressServerRunner] Shutting down server ...")
            server.shutdown()
            server.awaitTermination()
        }


        println("[GrpcCustomStressServerRunner] Server stopped")
        delay(10000L)

    }
}
