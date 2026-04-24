# Overview

The `tests/` directory contains integration, compatibility, conformance, and compiler plugin tests for kotlinx-rpc.
This directory is intended to store tests that require a more complex setup than a simple unit test.
None of these modules are published -- they exist solely for verification. All JVM test modules use JUnit 5 with `useJUnitPlatform()`.

### Updating compiler plugin test expectations

Set the Gradle property to auto-update `.fir.txt` / `.fir.ir.txt` golden files:
```bash
./gradlew :tests:compiler-plugin-tests:test -Pkotlin.test.update.test.data=true
```

## Module Map

| Module                              | What it tests                                                                         |
|-------------------------------------|---------------------------------------------------------------------------------------|
| `compiler-plugin-tests`             | Kotlin compiler plugin codegen  functional (box) and diagnostics                      |
| `krpc-compatibility-tests`          | kRPC API backward compatibility (old client/new server and vice versa)                |
| `krpc-protocol-compatibility-tests` | kRPC wire protocol compatibility across released versions (v0.8, v0.9, v0.10, Latest) |
| `protobuf-conformance`              | Protobuf spec conformance via Google's comformance tests                              |
| `protobuf-unittest`                 | Protobuf generation and correctness with Google's tests                               |
| `test-utils`                        | Shared KMP test utilities (`runTestWithCoroutinesProbes`, `WaitCounter`)              |
| `test-protos`                       | Shared `.proto` definitions for gRPC tests                                            |
| `grpc-test-server`                  | Standalone gRPC test server executable for gRPC tests                                 |

## Compiler Plugin Tests

Tests use Kotlin's compiler test framework (`kotlin-compiler-test-framework`). Test cases are auto-generated from files in `src/testData/`:

### Test data format

- **Box tests** (`src/testData/box/*.kt`): Compile-and-run tests. Must define a `fun box(): String` returning `"OK"` on success. Golden files: `*.fir.txt`, `*.fir.ir.txt`
- **Diagnostic tests** (`src/testData/diagnostics/*.kt`): FIR frontend diagnostics. Expected errors/warnings inline as `<!DIAGNOSTIC_NAME!>code<!>`. Golden file: `*.fir.txt`
- Pipeline directive: `// RUN_PIPELINE_TILL: BACKEND` (box) or `// RUN_PIPELINE_TILL: FRONTEND` (diagnostics)

### Adding a new test

1. Add a `.kt` file in `src/testData/box/` or `src/testData/diagnostics/`
2. Run `./gradlew :tests:compiler-plugin-tests:generateTests` to regenerate the test suite
3. Run with `-Pkotlin.test.update.test.data=true` to generate initial golden files
4. Verify the generated `.fir.txt` / `.fir.ir.txt` are correct

## Generated code — Do not edit manually

Several files in this directory are machine-generated.
**Never modify them by hand** — always run the appropriate regeneration task.

| Generated files | Regeneration task | When to regenerate |
|---|---|---|
| `compiler-plugin-tests/src/test-gen/` (`BoxTestGenerated.java`, `DiagnosticTestGenerated.java`) | `:tests:compiler-plugin-tests:generateTests` | After adding, removing, or renaming test data files in `src/testData/` |
| `protobuf-conformance/build/protoBuild/generated/commonMain/` (not committed; regenerated on every build) | `:tests:protobuf-conformance:bufGenerateCommonMain` | Automatically after `protoc-gen/` or `.proto` changes |
