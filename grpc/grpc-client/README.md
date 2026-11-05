# gRPC client

The iOS source set imports `GrpcSwiftBridge` from the
[kotlinx-rpc-grpc-swift](https://github.com/Kotlin/kotlinx-rpc-grpc-swift) Swift package.
The build resolves and compiles the published package revision from the version catalog.
The `native-deps/kotlinx-rpc-grpc-swift` git submodule holds the sources of that revision.

## Local kotlinx-rpc-grpc-swift development

Set the package directory in the root `local.properties` file:

```properties
kotlinx.rpc.grpc.swift.packagePath=/absolute/path/to/kotlinx-rpc-grpc-swift
```

A relative path is resolved from the kotlinx-rpc checkout root. The property overrides the
published revision. Point it at a separate checkout or at `native-deps/kotlinx-rpc-grpc-swift`.
Remove the property before you regenerate the shared SwiftPM lock files.
