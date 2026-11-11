# Overview

This is the Kotlin compiler plugin for kotlinx-rpc. 
It runs in two phases: FIR (frontend diagnostics + declaration generation, uses K2) and IR (backend codegen).

## Build & Test

The module uses the [Kotlin Compiler Plugin DevKit](https://github.com/Kotlin/compiler-plugin-dev-kit).
The DevKit compiles the same sources against each supported Kotlin version and puts all of them into one jar.

Each Kotlin version is a Gradle target. The target name is `k` and the version without dots,
for example `k2420` for Kotlin 2.4.20 and `k2420_ij262_52` for an IDE version.

Run the tasks from the repository root. The tests need projects of the root build.
Use the `running_gradle_builds` and `running_gradle_tests` skills:
- Build the plugin jar for all versions: `:compiler-plugin:assemble`
- All tests on all versions: `:compiler-plugin:allTests`
- All tests on one version: `:compiler-plugin:k2420Test`
- Single box test (codegen): `:compiler-plugin:k2420Test --tests "*BoxTestGenerated.testSimple"`
- Single diagnostic test: `:compiler-plugin:k2420Test --tests "*DiagnosticTestGenerated.testRpcService"`
- Update expected `.fir.txt` / `.ir.txt` outputs after a codegen change: `:compiler-plugin:allTests -PupdateTestData`

Test data is in `src/commonTest/data/`:
- `box/*.kt` -- Black-box codegen tests. Must define `fun box(): String` returning `"OK"`. Paired with `.fir.txt` and `.ir.txt` expected outputs.
- `diagnostics/*.kt` -- FIR diagnostic tests. Use `<!DIAGNOSTIC_NAME!>code<!>` markers for expected errors. Paired with `.fir.txt` expected output.

Directives: `// RUN_PIPELINE_TILL: BACKEND` (full pipeline) or `// RUN_PIPELINE_TILL: FRONTEND` (FIR only).

The test data compiles against the `:tests:compiler-plugin-test-libs` project of the root build and its `api` dependencies.

### Expected outputs for different Kotlin versions

A file without a version, for example `simple.ir.txt`, is the output of the highest supported Kotlin version.
A lower version uses the file of the nearest higher version that has one.
When the output of a version is different, the version has its own file, for example `simple.2321.ir.txt`.
A test fails when a file with a version is the same as the file it replaces.

## Source sets

| Directory | Content |
|---|---|
| `src/commonMain` | Plugin code for all Kotlin versions, and `expect` declarations for the compiler API that differs between versions. |
| `src/pre<version>DevMain`, `src/post<version>DevMain` | `actual` declarations for the Kotlin versions before and from `<version>`. |
| `src/commonTestFixtures` | Test runners and their configuration. |
| `src/commonTest/data` | Test data. |
| `src/generateTests` | The test generator. |

`versionHierarchy` in `build.gradle.kts` declares the version groups.
`gradle-conventions/src/main/kotlin/util/devkit.kt` declares the supported Kotlin and IDE versions.

## Generation References (detailed)

Protocol-specific codegen and diagnostics details live alongside each protocol module:
- [kRPC codegen](../krpc/codegen.md) -- stub class, companion descriptor, invokators
- [gRPC codegen](../grpc/codegen.md) -- gRPC-specific additions: `delegate()`, `MethodDescriptor`, streaming types
- [Protobuf codegen](../protobuf/codegen.md) -- `Builder` interface, `@WithProtoDescriptor`/`@WithGrpcMarshaller` annotations

## Generated test runners — Do not edit manually

The `generateTests` task writes the JUnit test classes (`BoxTestGenerated.java`, `DiagnosticTestGenerated.java`)
to `build/test-gen/`, one set for each Kotlin version. The test tasks run it first.

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
