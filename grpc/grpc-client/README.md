# gRPC client

The iOS source set imports `GrpcSwiftBridge` from the
[kotlinx-rpc-grpc-swift](https://github.com/Kotlin/kotlinx-rpc-grpc-swift) Swift package.
The version catalog pins the package revision.

## Local kotlinx-rpc-grpc-swift development

Set the package directory in the root `local.properties` file:

```properties
kotlinx.rpc.grpc.swift.packagePath=/absolute/path/to/kotlinx-rpc-grpc-swift
```

A relative path is resolved from the kotlinx-rpc checkout root. Remove the property to use the pinned remote revision.
Remove it before regenerating the shared SwiftPM lock files.
