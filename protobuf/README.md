# Overview

A Kotlin Multiplatform Protobuf runtime. It doesn't depend on the rest of the library or on external code generation.
The wire encoding and decoding are common Kotlin code.

The code that the plugins in `protoc-gen/` generate uses this runtime. [protoc-gen/codegen.md](../protoc-gen/codegen.md) shows the generated code.

## Modules

```
protobuf-lite  -- Wire encoders and decoders, base message class, descriptors, extensions, config
    |
protobuf-wkt   -- Google well-known types (Any, Duration, Timestamp, Struct, Descriptor)
    |
protobuf       -- Aggregator: lite, wkt, and grpc-marshaller
```

## Well-known types

The files in `protobuf-wkt/src/commonMain/generated-code/` are generated. Never edit them by hand.
After a change in `protoc-gen/`, run `:protobuf:protobuf-wkt:bufGenerateCommonMain`.
