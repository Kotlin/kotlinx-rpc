/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.withService

fun main(): Unit = runBlocking {
    val grpcClient = GrpcClient("localhost", 8080) {
        usePlaintext()
    }

    val recognizer = grpcClient.withService<ImageRecognizer>()

    val image = Image {
        data = byteArrayOf(0, 1, 2, 3)
    }
    val result = recognizer.recognize(image)
    println("Recognized category: ${result.category}")

    grpcClient.cancel()
}
