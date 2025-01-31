<div align="center">
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/kotlin/kotlinx-rpc/main/.github/images/logo_dark.png">
        <img alt="kotlinx.rpc logo" src="https://raw.githubusercontent.com/kotlin/kotlinx-rpc/main/.github/images/logo_light.png">
    </picture>
</div>

[![Kotlin Experimental](https://kotl.in/badges/experimental.svg)](https://kotlinlang.org/docs/components-stability.html)
[![Official JetBrains project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0.0--2.0.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[//]: # ([![TeamCity build]&#40;https://img.shields.io/teamcity/build/s/Build_kRPC_All.svg?server=http%3A%2F%2Fkrpc.teamcity.com&#41;]&#40;https://teamcity.jetbrains.com/viewType.html?buildTypeId=Build_kRPC_All&guest=1&#41;)

`kotlinx.rpc` is a Kotlin library for adding asynchronous Remote Procedure Call (RPC) services to your applications. 
Build your RPC with already known language constructs and nothing more!

## Quick start

First, create your RPC service and define some methods:
```kotlin
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc

@Rpc
interface AwesomeService : RemoteService {
    suspend fun getNews(city: String): Flow<String>
    
    suspend fun daysUntilStableRelese(): Int
}
```
In your server code define how to respond by simply implementing the service:
```kotlin
class AwesomeServiceImpl(
    val parameters: AwesomeParameters,
    override val coroutineContext: CoroutineContext,
) : AwesomeService {
    override suspend fun getNews(city: String): Flow<String> {
        return flow { 
            emit("Today is 23 degrees!")
            emit("Harry Potter is in $city!")
            emit("New dogs cafe has opened doors to all fluffy customers!")
        }
    }
    
    override suspend fun daysUntilStableRelese(): Int {
        return if (parameters.stable) 0 else {
            parameters.daysUntilStable ?: error("Who says it will be stable?")
        }
    }
}
```
Then, choose how do you want your service to communicate. For example, you can use integration with [Ktor](https://ktor.io/):

```kotlin
data class AwesomeParameters(val stable: Boolean, val daysUntilStable: Int?)

fun main() {
    embeddedServer(Netty, 8080) {
        install(Krpc)
        routing {
            rpc("/awesome") {
                rpcConfig {
                    serialization {
                        json()
                    }
                }

                registerService<AwesomeService> { ctx -> 
                    AwesomeServiceImpl(AwesomeParameters(false, null), ctx) 
                }
            }
        }
    }.start(wait = true)
}
```
To connect to the server use the following [Ktor Client](https://ktor.io/docs/create-client.html) setup:
```kotlin
val rpcClient = HttpClient { installKrpc() }.rpc {
    url("ws://localhost:8080/awesome")

    rpcConfig {
        serialization {
            json()
        }
    }
}

val service = rpcClient.withService<AwesomeService>()

service.daysUntilStableRelese()

streamScoped {
    service.getNews("KotlinBurg").collect { article ->
        println(article)
    }
}
```

Check out our [getting started guide](https://kotlin.github.io/kotlinx-rpc) for a thorough overview of all components and features.

## Configure the project

### Plugin dependencies

`kotlinx.rpc` provides Gradle plugin `org.jetbrains.kotlinx.rpc.plugin` 
that will set up code generation in a project.

Example of a setup in a project's `build.gradle.kts`:
```kotlin
plugins {
    kotlin("multiplatform") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.5.0"
}
```

### Runtime dependencies
To use `kotlinx.rpc` runtime dependencies, add Maven Central to the list of your repositories: 
```kotlin
repositories {
    mavenCentral()
}
```
And now you can add dependencies to your project:
```kotlin
dependencies {
    // Client API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.5.0")
    // Server API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:0.5.0") 
    // Serialization module. Also, protobuf and cbor are provided
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.5.0") 

    // Transport implementation for Ktor
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:0.5.0")

    // Ktor API
    implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
}
```
You can see example projects in the [samples](samples) folder.

## Transport
`kotlinx.rpc` is designed to be transport agnostic.
That means that the library aims to provide the best RPC experience regardless of how the resulting messages are transferred. 
That allows for easy integration into existing solutions, such as Ktor, without the need to rewrite code.
Add `kotlinx.rpc`, provide it with means to transfer encoded data (or use out-of-the-box integrations) and it will run.
With enough time it might even work with [avian carriers](https://en.wikipedia.org/wiki/IP_over_Avian_Carriers).

`kotlinx.rpc` provides its own transfer protocol called kRPC, which takes responsibility for tracking serializing and handling other complex request operations.
When using kRPC you only need to provide a transport or choose from the officially supported ones:
- Ktor transport

Besides that, one can even provide their own protocol or integration with one to use with services and `kotlinx.rpc` API with it.
Though possible, it is much more complicated way to use the library and generally not needed. 
`kotlinx.rpc` aims to provide support for the most common protocol integrations, as well as the in-house protocol called kRPC.

## gRPC integration
The library provides experimental support for the [gRPC](https://grpc.io) protocol.
The artifacts are published separately in our [Space repository](https://public.jetbrains.space/p/krpc/packages/maven/grpc).
Current latest version: 

![Dynamic XML Badge](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fmaven.pkg.jetbrains.space%2Fpublic%2Fp%2Fkrpc%2Fgrpc%2Forg%2Fjetbrains%2Fkotlinx%2Fkotlinx-rpc-core%2Fmaven-metadata.xml&query=%2F%2Fmetadata%2Fversioning%2Flatest&label=Latest%20dev%20version&color=forest-green&cacheSeconds=60)

For more information on gRPC usage, 
see the [official documentation](https://kotlin.github.io/kotlinx-rpc/grpc-configuration.html).
For a working example, see the [sample gRPC project](/samples/grpc-app).

## Kotlin compatibility
We support all stable Kotlin versions starting from 2.0.0:
- 2.0.0, 2.0.10, 2.0.20, 2.0.21, 2.1.0

For a full compatibility checklist, 
see [Versions](https://kotlin.github.io/kotlinx-rpc/versions.html).

## JetBrains Product

`kotlinx.rpc` is an official [JetBrains](https://jetbrains.com) product and is primarily developed by the team at JetBrains, with
contributions from the community.

[//]: # (## Documentation)

[//]: # (TODO: add docs site and most useful links)

## Support 

Community support is available on the [Kotlin Slack kotlinx-rpc channel](https://kotlinlang.slack.com/archives/C072YJ3Q91V)

## Reporting Security Vulnerabilities

If you find a security vulnerability in `kotlinx.rpc`, we kindly request that you reach out to the JetBrains security team via
our [responsible disclosure process](https://www.jetbrains.com/legal/terms/responsible-disclosure.html).

## Contributing

Please see [the contribution guide](CONTRIBUTING.md) and the [Code of conduct](CODE_OF_CONDUCT.md) before contributing.
