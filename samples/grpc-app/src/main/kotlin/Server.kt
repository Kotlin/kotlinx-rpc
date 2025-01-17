/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.registerService

fun main(): Unit = runBlocking {
    val grpcServer = GrpcServer(8080) {
        registerService<ImageRecognizer> { ImageRecognizerImpl() }
    }

    grpcServer.start()
    grpcServer.awaitTermination()
}
