/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

fun main() = runBlocking {
    val ktorClient = HttpClient {
        installKrpc()
    }

    val client = ktorClient.rpc {
        url {
            host = "localhost"
            port = 8080
            encodedPath = "image-recognizer"
        }

        rpcConfig {
            serialization {
                json()
            }
        }
    }

    val recognizer: ImageRecognizer = client.withService<ImageRecognizer>()

    val stateJob = launch {
        recognizer.currentlyProcessedImage().collect {
            println("New state, current image: $it")
        }
    }

    val image = Image(byteArrayOf(0, 1, 2, 3))
    val category = recognizer.recognize(image)
    println("Recognized category: $category")

    val imageFlow = flow {
        repeat(10) {
            emit(image)
        }
    }

    val categories = recognizer.recognizeAll(imageFlow)
    categories.collect { println("Recognized category: $it") }

    recognizer.cancel()
    ktorClient.close()
    stateJob.cancel()
}
