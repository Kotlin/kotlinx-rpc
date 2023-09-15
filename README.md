# kRPC - the Kotlin RPC Framework

https://youtu.be/YDGrHOw8WGg

kRPC is a Kotlin RPC (Remote Procedure Call) framework that leverages the power of suspend functions
to simplify and streamline the development of distributed applications.
With kRPC, you can easily create remote services, call remote methods,
and handle asynchronous communication between different parts of your application,
all while writing concise and clean Kotlin code.

## Features

- **Multiplatform**: kRPC is written in common Kotlin and allows to be used on all platforms
  supported by Kotlin. Current transport is using Ktor and runs on all platforms supported by Ktor.

- **Suspend**: kRPC makes extensive use of Kotlin's suspend functions,
  allowing you to write asynchronous code in a sequential, easy-to-read manner

- **Exception Transparent**: kRPC automatically bypass exceptions thrown by remote services

- **Cancellation and Interruption Transparent**: kRPC bypass cancellation and interruption
  of remote services both from the client and the server side

- **Streaming Support**: kRPC supports streaming using `Flow` of both requests and responses.
  Nested streams are also supported.

- **Serialization support**: All serializable classes are transferred automatically.

- **Pluggable transport**: kRPC transport is pluggable, allowing you to use any implementation.
  Currently, it uses WebSockets provided by Ktor.

## Getting Started

Make an interface for your service:

```kotlin
@Serializable
class Image(val data: ByteArray)

enum class Category {
    CAT, DOG
}

interface ImageRecognizer : RPC {
    suspend fun recognize(image: Image): Category

    suspend fun recognizeAll(images: Flow<Image>): Flow<Category>
}
```

Implement the service:

```kotlin
class ImageRecognizerService : ImageRecognizer {
    override suspend fun recognize(image: Image): Category {
        val byte = image.data[0].toInt()
        return if (byte == 0) Category.CAT else Category.DOG
    }

    override suspend fun recognizeAll(images: Flow<Image>): Flow<Category> {
        return images.map { recognize(it) }
    }
}
```

Start the server:

```kotlin
embeddedServer(Netty, port = 8080) {
    install(WebSockets)
    routing {
        route("image-recognizer") {
            rpc<ImageRecognizer>(ImageRecognizerService())
        }
    }
}.start(wait = true)
```

Connect to the server:

```kotlin
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
```
