# Getting Started

`kotlinx.rpc` is a multiplatform Kotlin library that provides its users with tools
to perform Remote Procedure Calls (RPC) using Kotlin language constructs with as easy setup as possible.
The library is transport agnostic by design,
so most transfer mechanisms can be used under the hood: WebSockets, HTTP Requests, gRPC, etc.

In this document, we will go through all core concepts and abstractions that the library uses 
and see how you can apply them to your application.

> If you are not familiar with RPC, we strongly recommend reading 
> [Wikipedia's article on RPC](https://en.wikipedia.org/wiki/Remote_procedure_call) 
> before proceeding with this document.

## What this library is and what it is not

It is important to understand that this library
is a set of tools and APIs gathered in one place
with a goal of providing a great user experience
when working with RPC systems in Kotlin multiplatform projects.
`kotlinx.rpc` provides its own in-house new RPC protocol,
but it **IS NOT** a library solely focused on this protocol.

The combination of Kotlin Multiplatform technology and RPC concept
opens an opportunity to provide exceptional user experience while
creating client-server applications, and we will create technologies
that will embrace the concept in this library.
This will include our own in-house RPC protocol
with the focus on KMP projects,
as well as integrations with other technologies,
including but not limited to [gRPC](https://grpc.io).


## Project configuration

First, you will need to configure your project with [Gradle](https://docs.gradle.org/current/userguide/userguide.html).
In this section, we will describe Gradle project configuration concepts 
that are most valuable to know while working with `kotlinx.rpc`. 

### Runtime dependencies

`kotlinx.rpc` provides you with runtime [dependencies](https://docs.gradle.org/current/userguide/declaring_dependencies.html) 
(also called artifacts).
To use these dependencies, in your project's `build.gradle.kts` file you can define the following:
```kotlin
dependencies {
    // example kotlinx.rpc artifacts
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-client:1.9.23-0.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-server:1.9.23-0.1.0")
}
```
That will add you the APIs needed to work with both client and server using `kotlinx.rpc`.
However, this example will work for 
[simple Kotlin/JVM projects](https://kotlinlang.org/docs/get-started-with-jvm-gradle-project.html#explore-the-build-script) only. 
To use it with Kotlin Multiplatform projects (KMP), you need to define a slightly different script:
```kotlin
kotlin {
    // source sets can be any other supported, ios and jvm are chosen just for an example
    sourceSets {
        iosMain {
            // let's say that we have code for the client in iosMain sources set
            implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-client:1.9.23-0.1.0")
        }
        
        jvmMain {
            // let's say that we have code for the server in jvmMain sources set
            implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-server:1.9.23-0.1.0")
        }
    }
}
```
Here we define dependencies for `iosMain` and `jvmMain` source sets, 
but it can be [any other source set](https://kotlinlang.org/docs/multiplatform-discover-project.html)
that you may want to have. 
Also, you may want to split your application into 
[several modules](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html), 
so that you have a server in one module and a client in another.

For full examples of Kotlin/JVM and KMP projects, 
see [the simple single-module Ktor App example](/samples/simple-ktor-app) 
and the [Ktor web app example](/samples/ktor-web-app) respectively.

In the latter you will find usage of [Gradle version catalogs](https://docs.gradle.org/current/userguide/platforms.html), 
so the dependency declaration may look like this:
```toml
# gradle/libs.versions.toml
[versions]
kotlinx-rpc = "1.9.23-0.1.0"

[libraries]
kotlinx-rpc-client = { module = "org.jetbrains.kotlinx:kotlinx-rpc-runtime-client", version.ref = "kotlinx-rpc" }
```
```kotlin
// build.gradle.kts
dependecies {
    implementation(libs.kotlinx.rpc.client)
}
```

[//]: # (TODO: Link to docs website with features and their artifacts)

Lastly, to be able to add dependencies for `kotlinx.rpc` artifacts,
you will need to define a [repository](https://docs.gradle.org/current/userguide/declaring_repositories.html)
from which they will be consumed in your `build.gradle.kts`:
```kotlin
repositories {
    // for kotlinx.rpc dependencies
    maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven") // todo change repo link
    // for other dependencies, like Ktor
    mavenCentral() 
}
```

### Gradle plugins

`kotlinx.rpc` also provides Gradle [plugins](https://docs.gradle.org/current/userguide/plugins.html).
These are needed to simplify your project configuration, 
as they do some repetitive work by themselves.

`kotlinx.rpc` provides two Gradle plugins:

- `org.jetbrains.kotlinx.rpc.platform`
- `org.jetbrains.kotlinx.rpc.plugin`

To add a plugin to your project, you need to define the following in your `build.gradle.kts`:
```kotlin
plugins {
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.1.0"
}
```

> Note that we use version `0.1.0` here, instead of `1.9.23-0.1.0` 
that we used in [runtime dependencies](#runtime-dependencies). 
We will describe why in the [versioning](#library-versioning) section. 

To be able to consume Gradle plugins, you need to specify a 
[repository for plugins](https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_management) 
to be consumed from in your `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        // kotlinx.rpc plugins will be downloaded from here
        maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven") // todo update repo link
        // other plugins, like Kotlin, will be downloaded from here
        gradlePluginPortal()
    }
}
```

Now we will explain the difference between two Gradle plugins we provide.

`org.jetbrains.kotlinx.rpc.platform` is a simple plugin
that is particularly useful for versioning project dependencies.
It adds [BOM](https://docs.gradle.org/current/userguide/platforms.html#sub:bom_import) 
dependency to your project, that specifies proper versions for `kotlinx.rpc` dependencies.
So instead of first example in [runtime dependencies](#runtime-dependencies) section
you can write this:

```kotlin
plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlinx.rpc.platform") version "0.1.0"
}

dependencies {
    // versions are set automatically to 1.9.23-0.1.0 
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-client")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-server")
}
```

Using this plugin, an example with version catalogs can be rewritten like this:
```toml
# gradle/libs.versions.toml
[versions]
kotlinx-rpc-core = "0.1.0"

[libraries]
kotlinx-rpc-client = { module = "org.jetbrains.kotlinx:kotlinx-rpc-runtime-client" }

[plugins]
kotlinx-rpc-platform = { id = "org.jetbrains.kotlinx.rpc.platform", version.ref = "kotlinx-rpc-core" }
```
```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.kotlinx.rpc.platform)
}

dependecies {
    implementation(libs.kotlinx.rpc.client)
}
```

`org.jetbrains.kotlinx.rpc.plugin` is the second plugin we provide.
It has the same BOM functionality as `org.jetbrains.kotlinx.rpc.platform`, and it also sets up code generation configurations.
For `kotlinx.rpc` library to work correctly with user defined [services](#services) 
we need to provide two additional configurations: Kotlin compiler plugin and
[KSP](https://kotlinlang.org/docs/ksp-overview.html) plugin. 
For you, this means that you only need to add `kotlinx.rpc` and KSP Gradle plugins,
and all the needed configuration will be automatically set up by the plugin:

```kotlin
plugins {
    kotlin("jvm") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.17"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.1.0"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-client")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-server")
}
```

Note that it is only needed for multi-project setups 
where you define your [RPC services](#services) in one set of subprojects and use in the other.
So in such a setup, you can add the plugin
only to modules with service definitions 
to save time on building your project.

### Other

`kotlinx.rpc` requires you to add 
[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) 
Gradle plugin to your project. 
```kotlin
plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}
```
More on the serialization in the [dedicated](#serialization-dsl) section.

## Library versioning

As you may have noticed by this time, 
we have a rather unfamiliar versioning scheme.
We will break it down for you in this section.

`kotlinx.rpc` version for all [runtime dependencies](#runtime-dependencies)
consists of two parts: Kotlin version prefix and core (feature) version suffix.
```
1.9.23-0.1.0
```
Here `1.9.23` is the version of Kotlin of your project. 
It can be found out by looking at Kotlin Gradle Plugin:
```kotlin
plugins {
    kotlin("jvm") version "1.9.23"
}
```
As `kotlinx.rpc` uses Kotlin compiler plugin and KSP plugins, 
we rely on internal functionality that may change over time with any new Kotlin version.
To prevent the library from breaking with an incompatible Kotlin version, 
we use version prefix.
But this may result in a situation when an update to a newer version of the library
would require updating the project's Kotlin version, which is not always possible and/or easy.
To address that issue, 
we provide core version updates for all stable versions of
**the last three** major Kotlin releases. 
So if the last stable Kotlin version is `1.9.23`, as at the time of writing this guide, 
the following versions of Kotlin are supported:

- 1.7.0, 1.7.10, 1.7.20, 1.7.21, 1.7.22
- 1.8.0, 1.8.10, 1.8.20, 1.8.21, 1.8.22
- 1.9.0, 1.9.10, 1.9.20, 1.9.21, 1.9.22, 1.9.23

So for each of the listed Kotlin versions, 
you can use `<kotlin_version>-<core_version>` template 
to get the needed library version. 
(So, for core version `0.1.0`, there are `1.7.0-0.1.0`, 
`1.7.0-0.1.0`, ... , `1.9.23-0.1.0` versions present).
To simplify project configuration, both our [Gradle plugins](#gradle-plugins)
are able to set proper runtime dependencies versions automatically based
on the project's Kotlin version and the Gradle plugin version 
which is used as a core library version.

To summarize, we can look at the example:
```kotlin
plugins {
    kotlin("jvm") version "1.9.23" // project's Kotlin version 
    id("org.jetbrains.kotlinx.rpc.platform") version "0.1.0" // kotlinx.rpc core version
}

dependencies {
    // for kotlinx.rpc runtime dependencies, Gradle plugin sets version 1.9.23-0.1.0
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-client") 
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-runtime-server")
}
```

## Common concepts

In this section, we will go through the main parts of the API
that `kotlinx.rpc` provides in runtime without a relation to a particular RPC protocol.
Core concepts and terminology will be explained here and demonstrated with examples.

### Services

Services are the centerpieces of the library.
A service is an interface annotated with the `RPC` annotation, 
and contains a set of methods and fields 
that can be executed or accessed remotely.
A simple service can be declared as follows:

```kotlin
interface MyService : RPC {
    suspend fun hello(name: String): String
}
```

Here we declare the method `hello`, that accepts one argument and returns a string.
The method is marked with a `suspend` modifier, which means that we use 
[coroutines](https://kotlinlang.org/docs/coroutines-guide.html) 
to send RPC requests.
Note that for now, only `suspend` methods are supported in service interfaces.

> Depending on the type of the protocol you use, services may support different features and declarations. 

To be able to use a service both in client and server code, 
the interface should be placed in the common module
— kudos to Kotlin Multiplatform.

Now we can implement the service, so server knows how to process incoming requests.

```kotlin
class MyServiceImpl(override val coroutineContext: CoroutineContext) : MyService {
    override suspend fun hello(name: String): String {
        return "Hello, $name! I'm server!"
    }
}
```

The server will use that implementation to answer the client requests.

### RPC Clients

For each declared service, `kotlinx.rpc` will generate an actual client implementation 
that can be used to send requests to a server.
You must not use generated code directly.
Instead, you should use the APIs that will provide you with the instance of your interface.
This generated instance is commonly called a stub in RPC.

> Note here we talk about generated stubs (service implementations on the client side)
> that must not be called directly.
> There might be a case when the generated code is not a stub, but a service declaration itself 
> (for example, the services generated from 
> [.proto files](https://grpc.io/docs/what-is-grpc/core-concepts/#service-definition)).
> In this case, you can use the generated code.

To be able to obtain an instance of your service, you need to have an `RPCClient`.
You can call `withService` method on your client:

```kotlin
val rpcClient: RPCClient

val myService: MyService = rpcClient.withService<MyService>()
```

Now you have your client instance that is able to send RPC requests to your server.
`RPCClient` can have multiple services that communicate through it.
Conceptually, `RPCClient` is an abstraction of a client, 
that is able to convent service requests 
(represented by `RPCCall` and `RPCField` classes) 
into actual network requests.

You can provide your own implementations of the `RPCClient`.
But `kotlinx.rpc` already provides one out-of-the-box solution that uses
in-house RPC protocol (called kRPC), and we are working on supporting more protocols 
(with priority on gRPC).

### RPC Servers

`RPCServer` interface represents an RPC server,
that accepts RPC messages and may contain multiple services to route to.
`RPCServer` uses data from incoming RPC messages
and routes it to the designated service and sends service's response back to the corresponding client.

You can provide your own `RPCServer` implementation
or use the one provided out of the box.
Note that client and server must use the same RPC protocol to communicate.

Use `registerService` function to add your own factory for implemented RPC services.
This factory function should accept `CoroutineContext` argument and pass it to the service,
which should use it to override `coroutineContext` property of parent interface.
This ensures proper application lifetime for your services.

Example usage:

```kotlin
val server: RPCServer

server.registerService<MyService> { ctx: CoroutineContext -> MyServiceImpl(ctx) } 
```

The `registerService` function requires the explicit type of the declared RPC service.
That means that the code will not work if you provide it with the type of the service implementation:
```kotlin
// Wrong! Should be `MyService` as type argument
server.registerService<MyServiceImpl> { ctx: CoroutineContext -> MyServiceImpl(ctx) } 
```

## kRPC - In-house RPC protocol

In this section, we will focus on APIs and features that are distinctly present in the kRPC protocol. 

### Features

#### Send and receive Flows

You can send and receive [Kotlin Flows](https://kotlinlang.org/docs/flow.html) in RPC methods.
That includes `Flow`, `SharedFlow`, and `StateFlow`, but **NOT** their mutable versions.
Flows can be nested and can be included in other classes, like this:

```kotlin
@Serializable
data class StreamResult {
    @Contextual 
    val innerFlow: StateFlow<Int>
}

interface MyService : RPC {
    suspend fun sendStream(stream: Flow<Flow<Int>>): Flow<StreamResult>
}
```

Note that flows that are declared in classes (like in `StreamResult`) require
[Contextual](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#contextual-serialization)
annotation.

To use flows in your code - you need to use special `streamScoped` function 
that will provide your flows with their lifetime:
```kotlin
interface MyService {
    suspend fun sendFlow(flow: Flow<Int>)
}

val myService = rpcClient.withService<MyService>()

streamScoped {
    val flow = flow { 
        repeat(10) { i ->
            emit(i)
        }
    }
    
    myService.sendFlow(flow)
}
```
In that case all your flows, including incoming and outgoing, 
will work until the `streamScoped` function completes. 
After that, all streams that are still live will be closed.

You can have multiple RPC call and flows inside the `streamScoped` function, even from different services.

On server side, you can use `invokeOnStreamScopeCompletion` handler inside your methods
to execute code after `streamScoped` on client side has closed.
It might be useful to clean resources, for example:
```kotlin
override suspend fun hotFlow(): StateFlow<Int> {
    val state = MutableStateFlow(-1)

    incomingHotFlowJob = launch {
        repeat(Int.MAX_VALUE) { value ->
            state.value = value

            hotFlowMirror.first { it == value }
        }
    }

    invokeOnStreamScopeCompletion {
        incomingHotFlowJob.cancel()
    }

    return state
}
```
Note that this API is experimental and may be removed in future releases.

#### Service fields

Our protocol provides you with an ability to declare service fields:
```kotlin
interface MyService : RPC {
    val plainFlow: Flow<Int>
    val sharedFlow: SharedFlow<Int>
    val stateFlow: StateFlow<Int>
}

// ### Server code ###

class MyServiceImpl(override val coroutineContext: CoroutineContext) : MyService {
    override val plainFlow: Flow<Int> = flow {
        emit(1)
    }
    
    override val sharedFlow: SharedFlow<Int> = MutableSharedFlow(replay = 1)
    override val stateFlow: StateFlow<Int> = MutableStateFlow(value = 1)
}
```

Field declarations are only supported for these three types: `Flow`, `SharedFlow` and `StateFlow`.

You don't need to use `streamScoped` function to work with streams in fields.

To learn more about the limitations of such declarations, 
see [Field declarations in services](#field-declarations-in-services).

[//]: # (TODO #### Exception propagation)

[//]: # (TODO #### Requests cancellation)

[//]: # (TODO #### Connection lifetime)


### KRPCClient — RPCClient for kRPC Protocol

`KRPCClient` is an abstract class that implements `RPCClient` and kRPC protocol logic. 
The only thing required to be implemented is the transporting of the raw data.
Abstract transport is represented by `RPCTransport` interface.

To implement your own `RPCTransport` 
you need to be able to transfer strings and/or raw bytes (Kotlin's `ByteArray`). 
And also the library will provide you with integrations with different 
libraries that are used to work with the network. 
More on that in the [transports](#transport) section.

Let's see an example usage of kRPC with a custom transport:

```kotlin
class MySimpleRPCTransport : RPCTransport {
    val outChannel = Channel<RPCTransportMessage>()
    val inChannel = Channel<RPCTransportMessage>()

    override val coroutineContext: CoroutineContext = Job()

    override suspend fun send(message: RPCTransportMessage) {
        outChannel.send(message)
    }

    override suspend fun receive(): RPCTransportMessage {
        return inChannel.receive()
    }
}

class MySimpleRPCClient : KRPCClient(rpcClientConfig(), MySimpleRPCTransport())

val client = MySimpleRPCClient()
val service: MyService = client.withService<MyService>()
```

### KRPCServer — RPCServer for kRPC Protocol

`KRPCServer` abstract class implements `RPCServer` 
and all the logic for processing RPC messages 
and again leaves `RPCTransport` methods for the specific implementations
(see [transports](#transport) section).

Example usage with custom transport:

```kotlin
// same MySimpleRPCTransport as in the client example above
class MySimpleRPCServer : KRPCServer(rpcServerConfig(), MySimpleRPCTransport())

val server = MySimpleRPCServer()
server.registerService<MyService> { ctx -> MyServiceImpl(ctx) }
```

Note that here we pass explicit `MyService` type parameter to the `registerService` method.
You must explicitly specify the type of the service interface here, 
otherwise the server service will not be found.

### Transport

Transport layer exists to abstract from the RPC requests logic and focus on delivering and receiving 
encoded RPC messages in kRPC Protocol. 
This layer is represented by `RPCTransport` interface. 
It supports two message formats — string and binary, 
and depending on which [serialization](#serialization-dsl) format you choose, 
one or the other will be used.

### Configuration

`RPCConfig` is a class used to configure `KRPCClient` and `KRPCServer` 
(not to be confused with `RPCClient` and `RPCServer`). 
It has two children: `RPCConfig.Client` and `RPCConfig.Server`. 
`Client` and `Server`  may have shared properties as well as distinct ones.
To create instances of these configurations, DSL builders are provided
(`RPCConfigBuilder.Client` class with `rpcClientConfig` function
and `RPCConfigBuilder.Server` class with `rpcServerConfig` function respectively):

```kotlin
val config: RPCConfig.Client = rpcClientConfig { // same for RPCConfig.Server with rpcServerConfig
    waitForServices = true // default parameter
}
```

The following configuration options are available:

#### `serialization` DSL

This parameter defines how serialization should work in RPC services
and is present in both client and server configurations.

The serialization process is used to encode and decode data in RPC requests,
so that the data can be transferred through the network.

Currently only `StringFormat` and `BinaryFormat` from 
[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) are supported, 
and by default you can choose from Json, Protobuf or Cbor formats:
```kotlin
rpcClientConfig {
    serialization {
        json { /* this: JsonBuilder from kotlinx.serialization */ }
        cbor { /* this: CborBuilder from kotlinx.serialization */ }
        protobuf { /* this: ProtobufBuilder from kotlinx.serialization */ }
    }
}
```

Only last defined format will be used to serialize requests.
If no format is specified, the error will be thrown.
You can define a [custom format]().

[//]: # (TODO add link to custom format guide)

#### `sharedFlowParameters` DSL

```kotlin
rpcClientConfig {
    sharedFlowParameters {
        replay = 1 // default parameter 
        extraBufferCapacity = 10 // default parameter  
        onBufferOverflow = BufferOverflow.SUSPEND // default parameter
    }
}
```

This parameter is needed to decode `SharedFlow` parameters received from a peer. 
`MutableSharedFlow`, the default function to create a `SharedFlow` instance, has the following signature:

```kotlin
fun <T> MutableSharedFlow(  
    replay: Int = 0,  
    extraBufferCapacity: Int = 0,  
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND  
): MutableSharedFlow<T> { /* ... */ }
```

It creates a `SharedFlowImpl` class that contains these parameters as properties, 
but this class in internal in `kotlinx.coroutines` and neither `SharedFlow`,
nor `MutableShatedFlow` interfaces define these properties, 
which makes it impossible (at least for now) to send these properties from one endpoint to another. 
But instances of these flows when deserialized should be created somehow, 
so to overcome this - configuration parameter is created.
Configuration builder allows defining these parameters 
and produces a builder function that is then placed into the `RPCConfig`.

#### `waitForServices` DSL

`waitForServices` parameter is available for both client and server. 
It specifies the behavior for an endpoint in situations 
when the message for a service is received, 
but the service is not present in `RPCClient` or `RPCServer`. 
If set to `true`, the message will be stored in memory, 
otherwise, the error will be sent to a peer endpoint,
saying that the message was not handled. 
Default value is `true`.

### Field declarations in services

Fields are supported in the in-house RPC protocol, 
but the support comes with its limitations.
There always will be a considerable time gap between the 
initial access to a field and the moment information about this field arrives from a server. 
This makes it hard to provide good uniform API for all possible field types,
so now will limit supported types to `Flow`, `SharedFlow` and `StateFlow` 
(excluding mutable versions of them). 
To work with these fields, you may use additional provided APIs:

Firstly, we define two possible states of a flow field: _uninitialized_ and _initialized_.
Before the first information about this flow has arrived from a server,
the flow is in _uninitialized_ state. 
In this state, if you access any of its **fields** 
(`replayCache` for `SharedFlow` and `StateFlow`, and `value` for `StateFlow`)
you will get a `UninitializedRPCFieldException`.
If you call a suspend `collect` method on them, 
execution will suspend until the state is _initialized_ 
and then the actual `collect` method will be executed.
The same ability to suspend execution until the state is _initialized_
can be achieved by using `awaitFieldInitialization` function:
```kotlin
interface MyService : RPC {
    val flow: StateFlow<Int>
}

// ### Somewhere in client code ###
val myService: MyService = rpcClient.withService<MyService>()

val value = myService.flow.value // throws UninitializedRPCFieldException
val value = myService.awaitFieldInitialization { flow }.value // OK
// or 
val value = myService.awaitFieldInitialization().flow.value // OK
// or
val firstFive = myService.flow.take(5).toList() // OK
```

Secondly, we provide you with an instrument to make initialization faster.
By default, all fields are lazy. 
By adding `@RPCEagerField` annotation, you can change this behavior,
so that fields will be initialized when the service in created 
(when `withService` method is called):

```kotlin
interface MyService : RPC {
    val lazyFlow: Flow<Int> // initialized on first access

    @RPCEagerField
    val eagerFlow: Flow<Int> // initialized on service creation
}
```

### Ktor transport

The `kotlinx.rpc` library provides integration with the [Ktor framework](https://ktor.io) with the in-house RPC protocol. 
This includes both server and client APIs.
Under the hood, the library uses [WebSocket](https://ktor.io/docs/websocket.html) plugin
to create a `RPCTransport` and send and receive messages through it.

#### Client

`kotlinx.rpc` provides a way to plug-in into existing Ktor clients with your RPC services. 
To do that, the following DSL can be used:

```kotlin
val ktorClient = HttpClient {
    install(RPC) { // this: RPCConfigBuilder.Client
        waitForServices = true
    }
}

val rpcClient: RPCClient = ktorClient.rpc("ws://localhost:4242/services") { // this: HttpRequestBuilder
    rpcConfig { // this: RPCConfigBuilder.Client
        waitForServices = false
    }
}
val myService: MyService = rpcClient.withService<MyService>()
```

Note that in this example, only the latter defined `RPCConfig` will be used.

#### Server

`kotlinx.rpc` provides a way to plug-in into existing server routing with your RPC services. 
To do that, the following DSL can be used:

```kotlin
fun Application.module() {
    install(RPC) { // this: RPCConfigBuilder.Server
        waitForServices = true
    }

    routing {
        rpc("/services") { // this RPCRoute
            rpcConfig { // this: RPCConfigBuilder.Server
                waitForServices = false
            }
            
            registerService<MyService> { ctx -> MyServiceImpl(ctx) }
            registerService<MyOtherService> { ctx - MyOtherServiceImpl(ctx) }
            // etc
        }
    }
}
```

#### Ktor application example

An example code for a Ktor web application may look like this:

```kotlin
// ### COMMON CODE ###
@Serializable
data class ProcessedImage(
    val url: String,
    val numberOfCats: Int,
    val numberOfDogs: Int
)

interface ImageService : RPC {
    suspend fun processImage(url: Srting): ProcessedImage
}

// ### CLIENT CODE ###

val client = HttpClient {
    install(WebSockets)

    install(kotlinx.rpc.transport.ktor.client.RPC) {
        serialization {
            json()
        }
    }
}

val service = client.rpc("/image-recognizer").withService<ImageService>()

service.processImage(url = "https://catsanddogs.com/cats/1")

// ### SERVER CODE ###

class ImageServiceImpl(override val coroutineContext: CoroutineContext) : ImageService {
    // user defined classes
    private val downloader = Downloader()
    private val recognizer = AnimalRecognizer()

    override suspend fun processImage(url: Srting): ProcessedImage {
        val image = downloader.loadImage(url)
        return ProcessedImage(url, recognizer.getNumberOfCatsOnImage(image), recognizer.getNumberOfDogsOnImage(image))
    }
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets)

        install(kotlinx.rpc.transport.ktor.server.RPC) {
            serialization {
                json()
            }
        }

        routing {
            rpc("/image-recognizer") {
                registerService<ImageService> { ctx -> ImageServiceImpl(ctx) }
            }
        }
    }.start(wait = true)
}
```

For more details and complete examples, see the [code samples](samples). 

### Other transports

Generally, there are no specific guidelines on how RPC should be set up for different transports, 
but structures and APIs used to develop integration with Ktor should outline the common approach.
You can provide your own transport and even your own fully implemented protocols, 
while the library will take care of code generation.
