# Overview

This is the Kotlin compiler plugin for kotlinx-rpc. 
It runs in two phases: FIR (frontend diagnostics + declaration generation, uses K2) and IR (backend codegen).

## Build & Test

The [Kotlin Compiler Plugin DevKit](https://github.com/Kotlin/compiler-plugin-dev-kit) compiles the same sources
against each supported Kotlin version and puts the classes of all versions into one JAR.

Each Kotlin version is a Gradle target: `k2420` for Kotlin 2.4.20, `k2420_ij262_52` for an IDE version.

Run the tasks from the repository root, because the tests need projects of the root build.
Use the `running_gradle_builds` and `running_gradle_tests` skills.

| Task | Effect |
|---|---|
| `:compiler-plugin:assemble` | Builds the plugin JAR for all versions |
| `:compiler-plugin:allTests` | Runs all tests on all versions |
| `:compiler-plugin:k2420Test` | Runs all tests on one version |
| `:compiler-plugin:k2420Test --tests "*BoxTestGenerated.testSimple"` | Runs one box test (codegen) |
| `:compiler-plugin:k2420Test --tests "*DiagnosticTestGenerated.testRpcService"` | Runs one diagnostic test |
| `:compiler-plugin:allTests -PupdateTestData` | Updates the expected `.fir.txt` and `.ir.txt` outputs |

Test data is in `src/commonTest/data/`:

| Files | Tests | Content | Expected outputs |
|---|---|---|---|
| `box/*.kt` | Codegen | `// RUN_PIPELINE_TILL: BACKEND`, and `fun box(): String` that returns `"OK"` | `.fir.txt`, `.ir.txt` |
| `diagnostics/*.kt` | FIR diagnostics | `// RUN_PIPELINE_TILL: FRONTEND`, and `<!DIAGNOSTIC_NAME!>code<!>` for each expected diagnostic | `.fir.txt` |

The test data compiles against the `:tests:compiler-plugin-test-libs` project of the root build and its `api` dependencies.

### Expected outputs for different Kotlin versions

| File | Output of |
|---|---|
| Without a version: `simple.ir.txt` | The highest supported Kotlin version |
| With a version: `simple.240_dev_2631.ir.txt`, `grpc.2321.fir.txt` | That version, and the lower versions down to the next file with a version |

A test fails when a file with a version is the same as the file of the next higher version.

## Source sets

| Directory | Content |
|---|---|
| `src/commonMain` | Plugin code for all Kotlin versions, and `expect` declarations for the compiler API that differs between versions |
| `src/pre<version>DevMain`, `src/post<version>DevMain` | `actual` declarations for the Kotlin versions before and from `<version>` |
| `src/commonTestFixtures` | Test runners and their configuration |
| `src/commonTest/data` | Test data |
| `src/generateTests` | The test generator |

| Setting | Location |
|---|---|
| Version groups | `versionHierarchy` in `build.gradle.kts` |
| Supported Kotlin and IDE versions | `gradle-conventions/src/main/kotlin/util/devkit.kt` |
| Minimal versions | `kotlin-min` and `idea-min` in `versions-root/libs.versions.toml` |

## Generation References (detailed)

Protocol-specific codegen and diagnostics details live alongside each protocol module:
- [kRPC codegen](../krpc/codegen.md) -- stub class, companion descriptor, invokators
- [gRPC codegen](../grpc/codegen.md) -- gRPC-specific additions: `delegate()`, `MethodDescriptor`, streaming types
- [Protobuf codegen](../protobuf/codegen.md) -- `Builder` interface, `@WithProtoDescriptor`/`@WithGrpcMarshaller` annotations

## Generated test runners

The test tasks run the `generateTests*` tasks first. These tasks write the JUnit test classes
(`BoxTestGenerated.java`, `DiagnosticTestGenerated.java`) to `build/test-gen/`, one set for each Kotlin version.

## Version Compatibility

See `verify-compiler-plugin-compatibility` skill.

## Compiler internals investigation tip

When debugging issues involving the Kotlin compiler (compiler plugin code, FIR, IR),
**read the implementation of the API you're calling**, not just the signature. Compiler
APIs often have non-obvious side effects — e.g., a method named `addAnnotation` may
also validate argument types and modify the declaration's annotation list. Use
`searching_dependency_sources` to find the class, then `read_dependency_sources` to
read the actual method body. This prevents hours of guessing about behavior that is
plainly visible in the source.
