<div align="center">
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/kotlin/kotlinx-rpc/main/.github/images/logo_dark.png">
        <img alt="kotlinx.rpc logo" src="https://raw.githubusercontent.com/kotlin/kotlinx-rpc/main/.github/images/logo_light.png">
    </picture>
</div>

[![Kotlin Experimental](https://kotl.in/badges/experimental.svg)](https://kotlinlang.org/docs/components-stability.html)
[![Official JetBrains project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.0--2.0.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[//]: # ([![TeamCity build]&#40;https://img.shields.io/teamcity/build/s/Build_kRPC_All.svg?server=http%3A%2F%2Fkrpc.teamcity.com&#41;]&#40;https://teamcity.jetbrains.com/viewType.html?buildTypeId=Build_kRPC_All&guest=1&#41;)

`kotlinx.rpc` is a Kotlin library for adding asynchronous Remote Procedure Call (RPC) services to your applications. 
Build your RPC with already known language constructs and nothing more!

## Quick start

First, create your `RPC` service and define some methods:
```kotlin
import kotlinx.rpc.RPC

interface AwesomeService : RPC {
    suspend fun getNews(city: String): Flow<String>
}
```
In your server code define how to respond by simply implementing the service:
```kotlin
class AwesomeServiceImpl(override val coroutineContext: CoroutineContext) : AwesomeService {
    override suspend fun getNews(city: String): Flow<String> {
        return flow { 
            emit("Today is 23 degrees!")
            emit("Harry Potter is in $city!")
            emit("New dogs cafe has opened doors to all fluffy customers!")
        }
    }
}
```
Then, choose how do you want your service to communicate. For example, you can use integration with [Ktor](https://ktor.io/):

```kotlin
fun main() {
    embeddedServer(Netty, 8080) {
        install(RPC)
        routing {
            rpc("/awesome") {
                rpcConfig {
                    serialization {
                        json()
                    }
                }

                registerService<AwesomeService> { ctx -> AwesomeServiceImpl(ctx) }
            }
        }
    }.start(wait = true)
}
```
To connect to the server use the following [Ktor Client](https://ktor.io/docs/create-client.html) setup:
```kotlin
val rpcClient = HttpClient { installRPC() }.rpc {
    url("ws://localhost:8080/awesome")

    rpcConfig {
        serialization {
            json()
        }
    }
}

streamScoped {
    rpcClient.withService<AwesomeService>().getNews("KotlinBurg").collect { article ->
        println(article)
    }
}
```

Check out our [getting started guide](https://kotlin.github.io/kotlinx-rpc) for a thorough overview of all components and features.

## Configure the project

### Plugin dependencies

`kotlinx.rpc` has the following plugin dependencies:
- The `org.jetbrains.kotlinx.rpc.plugin` will set up BOM and code generation for targets in the project.
- The `org.jetbrains.kotlinx.rpc.platform` will only set up BOM. It is useful when you want to split your app into modules, 
and some of them will contain service declarations, thus using code generation, while others will only consume them.

Example of plugins setup in a project's `build.gradle.kts`:
```kotlin
plugins {
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.0.10"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.3.0"
}
```

For Kotlin versions prior to 2.0, 
KSP plugin is required 
(Corresponding configurations will be set up automatically by `org.jetbrains.kotlinx.rpc.plugin` plugin):

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.3.0"
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
    // client API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client")
    // server API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server") 
    // serialization module. also, protobuf and cbor are available
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json") 

    // transport implementation for Ktor
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server")

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
Just plug-in `kotlinx.rpc`, provide it with means to transfer encoded data (or use out-of-the-box integrations) and it will run.
With enough time it might even work with [avian carriers](https://en.wikipedia.org/wiki/IP_over_Avian_Carriers).

`kotlinx.rpc` provides its own transfer protocol called kRPC, which takes responsibility for tracking serializing and handling other complex request operations.
When using kRPC you only need to provide a transport or choose from the officially supported ones:
- Ktor transport

Besides that, one can even provide their own protocol or integration with one to use with services and `kotlinx.rpc` API with it.
Though possible, it is much more complicated way to use the library and generally not needed. 
`kotlinx.rpc` aims to provide most common protocols integrations as well as the in-house one called kRPC.  
Integrations in progress:
- Integration with [gRPC](https://grpc.io/)  (in prototype)

## Kotlin compatibility
We support all stable Kotlin versions starting from 1.7.0:
- 1.7.0, 1.7.10, 1.7.20, 1.7.21, 1.7.22
- 1.8.0, 1.8.10, 1.8.20, 1.8.21, 1.8.22
- 1.9.0, 1.9.10, 1.9.20, 1.9.21, 1.9.22, 1.9.23, 1.9.24, 1.9.25
- 2.0.0, 2.0.10

To simplify project configuration, our Gradle plugin sets a proper library version automatically using BOM, 
based on the project's Kotlin version:
```kotlin
plugins {
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.3.0"
}

dependencies {
    // version 0.3.0 is set by the Gradle plugin
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-core") 
}
```

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
