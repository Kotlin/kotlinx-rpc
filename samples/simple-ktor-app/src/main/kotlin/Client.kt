/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.withService
import kotlinx.rpc.serialization.json
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig

fun main() = runBlocking {
    val ktorClient = HttpClient {
        install(WebSockets)
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
        recognizer.currentlyProcessedImage.collect {
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

    streamScoped {
        val categories = recognizer.recognizeAll(imageFlow)
        categories.collect { println("Recognized category: $it") }
    }

    recognizer.cancel()
    ktorClient.close()
    stateJob.cancel()
}
