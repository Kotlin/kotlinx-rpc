# kRPC - the Kotlin RPC Framework

https://youtu.be/YDGrHOw8WGg

kRPC is a Kotlin RPC (Remote Procedure Call) framework that leverages the power of suspend functions
to simplify and streamline the development of distributed applications.
With kRPC, you can easily create remote services, call remote methods,
and handle asynchronous communication between different parts of your application,
all while writing concise and clean Kotlin code.

## Contents
- [Features](#features)
- [Known Limitations](#known-limitations)
- [Getting Started](#getting-started)
  - [Using Ktor RPC Transport](#using-ktor-rpc-transport)
  - [Implementing Transport](#implementing-transport)
  - [Using Implemented RPC Transport](#using-implemented-rpc-transport)

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

## Known Limitations

* RPC interface can't be in the top-level package (will be fixed)
* Json is a single supported serialization format (will be fixed)
* vararg are not supported
* Flow<*> inside transferred serializable `data class` should be marked with `@Contextual`

## Getting Started

To get started with kRPC you need to have KSP and kotlinx.serialization plugins installed. To do so, add the following to your `build.gradle.kts`:

```kotlin
plugins {
    kotlin("plugin.serialization") version "1.9.10"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}
```

Add repository for kRPC:
```kotlin
repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven")
    mavenCentral()
}

```

Then, apply the KSP and kRPC plugins in the dependency section:

```kotlin
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

dependencies {
    ksp("org.jetbrains.krpc:krpc-ksp-plugin:1.9.10-beta-1")
    PLUGIN_CLASSPATH_CONFIGURATION_NAME("org.jetbrains.krpc:krpc-compiler-plugin:1.9.10-beta-1")
}
```

### Using Ktor RPC Transport
Add dependencies for client and server transport and Ktor dependencies:
```kotlin
dependencies {
    implementation("org.jetbrains.krpc:krpc-transport-ktor-client:1.9.10-beta-1")
    implementation("org.jetbrains.krpc:krpc-transport-ktor-server:1.9.10-beta-1")
  
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
}

```

Create an interface for your service:

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

Implement the service for the backend:

```kotlin
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

```

Create the server `Server.kt`:

```kotlin
package com.jetbrains.krpc.samples

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.jetbrains.krpc.transport.ktor.server.rpc

fun main() {
  embeddedServer(Netty, port = 8080) {
    install(WebSockets)
    routing {
      rpc<ImageRecognizer>("image-recognizer", ImageRecognizerService())
    }
  }.start(wait = true)
}
```

Create the client `Client.kt`:

```kotlin
package com.jetbrains.krpc.samples

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow
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
}
```

## Implementing Transport

To implement a transport, you need to add dependency:
```kotlin
dependencies {
    implementation("org.jetbrains.krpc:krpc-runtime:1.9.10-beta-1")
    testImplementation("org.jetbrains.krpc:krpc-runtime-test:1.9.10-beta-1")
}
```

And implement the interface `RPCTransport`:

```kotlin
package org.jetbrains.krpc

interface RPCTransport : CoroutineScope {
    val incoming: SharedFlow<RPCMessage>

    suspend fun send(message: RPCMessage)
}
```

There is a testing suite `KRPCTransportTestBase` checking if the transport working correctly. Consider implementing it on your transport.

### Using Implemented RPC Transport

To use implemented transport on the client, you need to add dependency:
```kotlin
dependencies {
    implementation("org.jetbrains.krpc:krpc-transport-client:1.9.10-beta-1")
}
```

Next, you can create your transport and service:
```kotlin
val transport = MyTransport(...)
val serice: MyService = rpcServiceOf<MyService>(transport)
```

To use implemented transport on the server, you need to add dependency:
```kotlin
dependencies {
    implementation("org.jetbrains.krpc:krpc-transport-server:1.9.10-beta-1")
}
```

Next, you can create your transport and service:
```kotlin
val transport = MyTransport(...)
val service = MyServiceImpl()
val rpcServer: RPCServer<MyService> = rpcServerOf<MyService>(backend, transport)
server.run()
```