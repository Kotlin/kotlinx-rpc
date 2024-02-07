<div align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/ktorio/krpc/main/.github/images/logo.svg">
    <img alt="kRPC logo" src="https://raw.githubusercontent.com/ktorio/krpc/main/.github/images/logo.svg">
  </picture>
</div>

[![Kotlin Experimental](https://kotl.in/badges/experimental.svg)](https://kotlinlang.org/docs/components-stability.html)
[![Official JetBrains project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.0--1.9.22-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[//]: # ([![TeamCity build]&#40;https://img.shields.io/teamcity/build/s/Build_kRPC_All.svg?server=http%3A%2F%2Fkrpc.teamcity.com&#41;]&#40;https://teamcity.jetbrains.com/viewType.html?buildTypeId=Build_kRPC_All&guest=1&#41;)

kRPC is a Kotlin framework for adding asynchronous Remote Procedure Call (RPC) services to your applications. 
Build your RPC with already known language constructs and nothing more!

## Quick start

First, create your `RPC` service and define some methods:
```kotlin
import org.jetbrains.krpc.RPC

interface AwesomeService : RPC {
    suspend fun getNews(city: String): Flow<String>
}
```
In your server code define how to respond by simply implementing the service:
```kotlin
class AwesomeServiceImpl : AwesomeService {
    override suspend fun getNews(city: String): Flow<String> {
        return flow { 
            emit("Today is 23 degrees!")
            emit("Harry Potter in $city!")
            emit("New dogs cafe has opened doors to all fluffy customers!")
        }
    }
}
```
Then, choose how do you want your service to communicate. For example, you can use integration with [Ktor](https://ktor.io/):
```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {    
        install(WebSockets)
        routing {
            rpc("/awesome") {
                rpcConfig {
                    serialization {
                        json()
                    }
                }

                registerService<AwesomeService>(AwesomeServiceImpl())
            }
        }
    }.start(wait = true)
}
```
To connect to the server use the following [Ktor Client](https://ktor.io/docs/create-client.html) setup:
```kotlin
val rpcClient = HttpClient { install(WebSockets) }.rpc {
    url("ws://localhost:8080/awesome")

    rpcConfig {
        serialization {
            json()
        }
    }
}

rpcClient.withService<AwesomeService>().sayHello("Hello from client!")
```

Check out our [getting started guide](GETTING_STARTED.md) for a thorough overview of all components and features.

## Configure the project

### Plugin dependencies

kRPC has the following plugin dependencies:
- The `org.jetbrains.krpc.plugin` will set up BOM and code generation for targets in the project.
- The `org.jetbrains.krpc.platform` will only set up BOM. It is useful when you want to split your app into modules, 
and some of them will contain service declarations, thus using code generation, while others will only consume them.
- The `com.google.devtools.ksp` is required by kRPC. Corresponding configurations will be set up automatically by `org.jetbrains.krpc.plugin` plugin.

To use the kRPC Gradle plugins, you need to add the following repositories in the `settings.gradle.kts` file:
```kotlin
pluginManagement {
    repositories {
        maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven")
        gradlePluginPortal()
    }
}
```
Example of plugins setup in a project's `build.gradle.kts`:
```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
    id("org.jetbrains.krpc.plugin") version "5.3-beta"
}
```
### Runtime dependencies
To use kRPC runtime dependencies, you need to add our Space repository to the list of project repositories: 
```kotlin
repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/krpc/maven")
    mavenCentral() // for other dependencies, e.g. Ktor
}
```
And now you can add dependencies to your project:
```kotlin
dependencies {
    implementation("org.jetbrains.krpc:krpc-runtime-client") // client API
    implementation("org.jetbrains.krpc:krpc-runtime-server") // server API
    implementation("org.jetbrains.krpc:krpc-runtime-serialization-json") // serialization module, can be other than JSON 
    
    // transport implementation for Ktor
    implementation("org.jetbrains.krpc:krpc-transport-ktor-client")
    implementation("org.jetbrains.krpc:krpc-transport-ktor-server")

    // Ktor API
    implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
}
```
You can see example projects in the [samples](samples) folder.

## Transport
kRPC is designed to be transport agnostic.
That means that kRPC aims to provide the best RPC experience regardless of how the resulting messages are transferred. 
That allows for easy integration into existing solutions, such as Ktor, without the need to rewrite code.
Just plug-in kRPC, provide it with means to transfer encoded data (or use out-of-the-box integrations) and it will run.
With enough time it might even work with [avian carriers](https://en.wikipedia.org/wiki/IP_over_Avian_Carriers).

kRPC provides its own transfer protocol, which takes responsibility for tracking serializing and handling other complex request operations.
For that protocol you only need to provide a transport or choose from the officially supported ones:
- Ktor transport

Besides that, one can even provide their own protocol or integration with one to use with services and kRPC API with it.
Though possible, it is much more complicated way to use kRPC and generally not needed. 
kRPC aims to provide most common protocols integrations as well as the in-house one.  
Integrations in progress:
- Integration with [gRPC](https://grpc.io/)  (in prototype)

## Kotlin compatibility
kRPC heavily relies on Kotlin compiler plugin to be able to generate client service implementations and other needed code.
That results in the need to be bound to Kotlin compiler version and the versions of library,
so kRPC version may look like this: `1.9.10-1.0.0`, where the prefix (`1.9.10`) is the Kotlin version and the suffix (`1.0.0`) is the kRPC feature (or core) version.
To mitigate inconveniences related to the need to update Kotlin version then one wants to have newer version of kRPC,
kRPC provides all feature releases for all stable releases of the 3 last major Kotlin versions. Currently, they are:
- 1.7.0, 1.7.10, 1.7.20, 1.7.21, 1.7.22
- 1.8.0, 1.8.10, 1.8.20, 1.8.21, 1.8.22
- 1.9.0, 1.9.10, 1.9.20, 1.9.21, 1.9.22

That generates resulting versions `1.7.0-1.0.0`, `1.7.10-1.0.0`, etc.
To simplify project configuration, our Gradle plugin sets proper library version automatically using BOM, based on the project's Kotlin version:
```kotlin
plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.krpc.plugin") version "1.0.0"
}

dependencies {
    implemntation("org.jetbrains.krpc:krpc-runtime") // version is 1.9.22-1.0.0 is set by Gradle plugin
}
```

## JetBrains Product

kRPC is an official [JetBrains](https://jetbrains.com) product and is primarily developed by the team at JetBrains, with
contributions from the community.

[//]: # (## Documentation)

[//]: # (TODO: add docs site and most useful links)

## Reporting Security Vulnerabilities

If you find a security vulnerability in kRPC, we kindly request that you reach out to the JetBrains security team via
our [responsible disclosure process](https://www.jetbrains.com/legal/terms/responsible-disclosure.html).

## Contributing

Please see [the contribution guide](CONTRIBUTING.md) and the [Code of conduct](CODE_OF_CONDUCT.md) before contributing.
