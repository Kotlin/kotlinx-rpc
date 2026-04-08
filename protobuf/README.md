# Overview

Multiplatform Protobuf runtime, fully independent of the rest of the library and any external code generation. 
Uses platform-specific protobuf libraries for wire encoding/decoding.

Primaraly used with by the generated code by protoc-gen plugins from `/protoc-gen`.

## Modules

```
protobuf-api    -- Wire encoders/decoders, base message class, descriptor system, extensions, config, etc.
    |
protobuf-wkt   -- Google well-known types (Any, Duration, Timestamp, Struct, Descriptor, etc.)
    |
protobuf-core  -- Aggregator; re-exports api + wkt + grpc-marshaller
```

## Generated code contract

See [protoc-gen codegen reference](../protoc-gen/codegen.md) for details on generated code.
