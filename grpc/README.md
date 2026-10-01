# Overview

This module implements gRPC protocol support. 
It provides Kotlin Multiplatform gRPC clients and servers. JVM and Android use gRPC Java.
iOS clients use grpc-swift-2 with Apple's Network.framework through the
[Swift bridge](grpc-swift/README.md). Other supported Native clients, including macOS and Linux,
and Native servers use C interop with gRPC Core.
WASM, JS, and MinGW are not supported yet.

## Module Dependency Graph

```
grpc-ktor-server --> grpc-server --\
                                    --> grpc-core --> grpc-marshaller --> :core (RPC abstractions)
                     grpc-client --/
                     
grpc-marshaller-kotlinx-serialization --> grpc-marshaller

grpc-client (iosMain) --> grpc-swift --> grpc-swift-2 + grpc-swift-nio-transport
```

The client backend is selected automatically. Generated Kotlin services, protobuf messages,
interceptors, and unary and streaming calls use the common API on every supported client target.
The Swift bridge does not provide a server backend.

## iOS integration

The iOS client requires iOS 18 or newer and Xcode with Swift tools 6.3 or newer. The Kotlin Gradle
plugin discovers its transitive SwiftPM dependencies, but the consuming app must also configure
Xcode linkage. Use the Kotlin Multiplatform IDE plugin's Project Environment Preflight Checks setup
action, or run `integrateLinkagePackage` for the app's framework-producing Gradle module.
If the Kotlin framework build phase is missing, also run `integrateEmbedAndSign`.
See the [user setup guide](https://kotlin.github.io/kotlinx-rpc/grpc-configuration.html#grpc-ios-xcode)
and the [bridge README](grpc-swift/README.md) for details.

**Test server dependency**: Integration tests in `grpc-core` automatically build and start the `:tests:grpc-test-server` as a background process (port 50051). 
The test task waits for `[GRPC-TEST-SERVER] Server started` before proceeding.

**`@Grpc`** annotation -- marks an interface as a gRPC service (analogous to `@Rpc` for kRPC).

See [protoc-gen codegen reference](../protoc-gen/codegen.md) for details on generated code.
