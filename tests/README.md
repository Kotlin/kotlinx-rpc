# Overview

The `tests/` directory contains integration, compatibility, and conformance tests for kotlinx-rpc.
This directory is intended to store tests that require a more complex setup than a simple unit test.
None of these modules are published -- they exist solely for verification. All JVM test modules use JUnit 5 with `useJUnitPlatform()`.

## Module Map

| Module                              | What it tests                                                                         |
|-------------------------------------|---------------------------------------------------------------------------------------|
| `compiler-plugin-test-libs`         | Nothing. The test data of the compiler plugin tests compiles against it               |
| `functional-test-repository`        | Nothing. It publishes the libraries for the functional tests of the Gradle plugin     |
| `krpc-compatibility-tests`          | kRPC API backward compatibility (old client/new server and vice versa)                |
| `krpc-protocol-compatibility-tests` | kRPC wire protocol compatibility across released versions (v0.8, v0.9, v0.10, Latest) |
| `protobuf-conformance`              | Protobuf spec conformance via Google's comformance tests                              |
| `protobuf-unittest`                 | Protobuf generation and correctness with Google's tests                               |
| `test-utils`                        | Shared KMP test utilities (`runTestWithCoroutinesProbes`, `WaitCounter`)              |
| `test-protos`                       | Shared `.proto` definitions for gRPC tests                                            |
| `grpc-test-server`                  | grpc-java reference server for the gRPC client tests ([README](grpc-test-server/README.md)) |
| `grpc-benchmarks`                   | Cross-implementation gRPC performance clients, canonical protos, and C++ reference-server integration ([README](grpc-benchmarks/README.md)) |

## Compiler Plugin Tests

The compiler plugin tests are in the [compiler-plugin](../compiler-plugin/README.md) module.
`compiler-plugin-test-libs` has the declarations that their test data uses, for example `TestRpcClient`.

## Generated code — Do not edit manually

Several files in this directory are machine-generated.
**Never modify them by hand** — always run the appropriate regeneration task.

| Generated files | Regeneration task | When to regenerate |
|---|---|---|
| `protobuf-conformance/src/main/generated-code/` | `:tests:protobuf-conformance:bufGenerateMain` | After `protoc-gen/` changes |
