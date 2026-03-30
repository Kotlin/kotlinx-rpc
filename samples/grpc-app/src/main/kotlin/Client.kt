/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.runBlocking
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.withService

fun main(): Unit = runBlocking {
    val grpcClient = GrpcClient("localhost", 8080) {
        credentials = plaintext()
    }

    val recognizer = grpcClient.withService<ImageRecognizer>()

    val image = Image {
        data = ByteString(0, 1, 2, 3)
    }
    val result = recognizer.recognize(image)
    println("Recognized category: ${result.category}")
}
