# Overview

This module implements gRPC protocol support. 
It provides a Kotlin Multiplatform client/server gRPC implementation that targets JVM (via gRPC-Java) and Native (via C interop with gRPC Core library). 
WASM, JS, and MinGW are not supported yet.

## Module Dependency Graph

```
grpc-ktor-server --> grpc-server --\
                                    --> grpc-core --> grpc-marshaller --> :core (RPC abstractions)
                     grpc-client --/
                     
grpc-marshaller-kotlinx-serialization --> grpc-marshaller
```

**Test server dependency**: Integration tests in `grpc-core` automatically build and start the `:tests:grpc-test-server` as a background process (port 50051). 
The test task waits for `[GRPC-TEST-SERVER] Server started` before proceeding.

**`@Grpc`** annotation -- marks an interface as a gRPC service (analogous to `@Rpc` for kRPC).

See [protoc-gen codegen reference](../protoc-gen/codegen.md) for details on generated code.
