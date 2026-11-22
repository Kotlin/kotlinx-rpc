# Overview

The `grpc` modules have the gRPC protocol: Kotlin Multiplatform clients and a server.

| Target | Client | Server |
|---|---|---|
| JVM | gRPC Java | gRPC Java |
| Android | gRPC Java | None |
| iOS, macOS, tvOS, watchOS | grpc-swift-2 on Network.framework, through the [Swift bridge](https://github.com/Kotlin/kotlinx-rpc-grpc-swift) | None |
| JS, WASM, Linux, MinGW | None | None |

Generated services, protobuf messages, interceptors, and unary and streaming calls have one common API on all client targets.

```
grpc-ktor-server --> grpc-server --\
                                    --> grpc-core --> grpc-marshaller --> :core
                     grpc-client --/

grpc-marshaller-kotlinx-serialization --> grpc-marshaller

grpc-client (appleMain) --> kotlinx-rpc-grpc-swift (SwiftPM) --> grpc-swift-2, grpc-swift-nio-transport
```

Generated code: [gRPC codegen](codegen.md) and [protoc-gen codegen](../protoc-gen/codegen.md).

## Tests

The tests of `grpc-core` and `grpc-client` use [grpc-test-server](../tests/grpc-test-server/README.md).
Gradle builds the server, starts it on a free port, and waits for the `[GRPC-TEST-SERVER] Server started` line
before the tests run.

## iOS integration

- The iOS client requires iOS 18 or newer, and Xcode with Swift tools 6.3 or newer.
- The Kotlin Gradle plugin finds the transitive SwiftPM dependencies, but the application must also configure the
  Xcode linkage. Use the setup action of the Project Environment Preflight Checks in the Kotlin Multiplatform IDE
  plugin, or run `integrateLinkagePackage` for the Gradle module that makes the framework.
- If the build phase of the Kotlin framework is missing, also run `integrateEmbedAndSign`.
- More: the [user setup guide](https://kotlin.github.io/kotlinx-rpc/grpc-configuration.html#grpc-ios-xcode)
  and the [bridge README](https://github.com/Kotlin/kotlinx-rpc-grpc-swift).
