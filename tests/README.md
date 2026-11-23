# Overview

The `tests/` directory has the integration, compatibility, and conformance tests: the tests that need more setup than a unit test.
The modules aren't published.

## Module Map

| Module                              | What it tests                                                                         |
|-------------------------------------|---------------------------------------------------------------------------------------|
| `compiler-plugin-test-libs`         | Nothing. It has the declarations that the compiler plugin test data uses              |
| `functional-test-repository`        | Nothing. It publishes the libraries for the functional tests of the Gradle plugin     |
| `krpc-compatibility-tests`          | kRPC API backward compatibility (old client/new server and vice versa)                |
| `krpc-protocol-compatibility-tests` | kRPC wire protocol compatibility across released versions (v0.8, v0.9, v0.10, Latest) |
| `protobuf-conformance`              | Protobuf spec conformance via Google's conformance tests                              |
| `protobuf-unittest`                 | Protobuf generation and correctness with Google's tests                               |
| `test-utils`                        | Shared KMP test utilities (`runTestWithCoroutinesProbes`, `WaitCounter`)              |
| `test-protos`                       | Shared `.proto` definitions for gRPC tests                                            |
| `grpc-test-server`                  | grpc-java reference server for the gRPC client tests ([README](grpc-test-server/README.md)) |
| `grpc-benchmarks`                   | Cross-implementation gRPC performance clients, canonical protos, and C++ reference-server integration ([README](grpc-benchmarks/README.md)) |

## Compiler Plugin Tests

The compiler plugin tests are in the [compiler-plugin](../compiler-plugin/README.md) module.

## Generated code

Never edit generated code by hand. Regenerate it:

| Generated files | Task | When |
|---|---|---|
| `protobuf-conformance/src/commonMain/generated-code/` | `:tests:protobuf-conformance:bufGenerateCommonMain` | After a change in `protoc-gen/` |
