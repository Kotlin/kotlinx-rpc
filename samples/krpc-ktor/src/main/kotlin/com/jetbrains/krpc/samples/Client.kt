package com.jetbrains.krpc.samples

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.transport.ktor.client.rpc


fun main() = runBlocking {
    val client = HttpClient {
        install(WebSockets)
    }

    val recognizer: ImageRecognizer = client.rpc<ImageRecognizer> {
        url {
            host = "localhost"
            port = 8080
            encodedPath = "image-recognizer"
        }
    }

    val stateJob = launch(Dispatchers.Unconfined) {
        recognizer.currentlyProcessedImage.collect {
            println("New state, current image: ${it?.data}")
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
    client.close()
    stateJob.cancel()
}
