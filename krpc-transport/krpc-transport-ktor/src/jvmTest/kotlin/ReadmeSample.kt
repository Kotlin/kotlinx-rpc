@file:Suppress("ExtractKtorModule")

package org.jetbrains.krpc

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.transport.ktor.client.rpc
import org.jetbrains.krpc.transport.ktor.server.rpc
import kotlin.coroutines.CoroutineContext

@Serializable
class Image(val data: ByteArray)

enum class Category {
    CAT, DOG
}

interface ImageRecognizer : RPC {
    suspend fun recognize(image: Image): Category

    suspend fun recognizeAll(images: Flow<Image>): Flow<Category>
}

class ImageRecognizerService : ImageRecognizer {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun recognize(image: Image): Category {
        val byte = image.data[0].toInt()
        return if (byte == 0) Category.CAT else Category.DOG
    }

    override suspend fun recognizeAll(images: Flow<Image>): Flow<Category> {
        return images.map { recognize(it) }
    }
}

fun server(): NettyApplicationEngine {
    return embeddedServer(Netty, port = 8080) {
        install(WebSockets)

        routing {
            rpc<ImageRecognizer>("image-recognizer", ImageRecognizerService())
        }
    }.start(wait = false)
}

fun runClient() = runBlocking {
    val client = HttpClient {
        install(io.ktor.client.plugins.websocket.WebSockets)
    }

    val recognizer: ImageRecognizer = client.rpc<ImageRecognizer> {
        url {
            host = "localhost"
            port = 8080
            encodedPath = "image-recognizer"
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
}

fun main() {
    val server = server()
    runClient()
    server.stop()
}