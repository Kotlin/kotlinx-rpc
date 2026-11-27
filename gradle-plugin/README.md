# Overview

The `gradle-plugin` included build has the `org.jetbrains.kotlinx.rpc.plugin` Gradle plugin. The plugin does two things:

- It adds the kotlinx-rpc compiler plugin to the Kotlin compilation.
- It generates code from `.proto` files with `buf generate`, for JVM, KMP, and Android projects.

On apply, the plugin:

1. Checks that the Kotlin Gradle plugin is on the classpath.
2. Creates the `rpc {}` extension (`RpcExtension`).
3. Applies the compiler plugin.
4. Creates the protobuf and gRPC generation setup (`createProtoExtensions()`), but only when the build script
   calls `rpc { protoc() }` or `gradle.properties` has `kotlinx.rpc.protoc=true`.

## Build and test

Run single tests. The full suite is slow, because it runs many combinations of Gradle, KGP, and AGP versions.
Run all tests only on request.

- Run the tests from the root build: `:gradle-plugin:test` and `:gradle-plugin:allVersionedFunctionalTests`.
- A standalone build (`-p gradle-plugin`) can run `test`, but not the functional tests, because they need the
  libraries of the root build.
- The standalone build includes the `compiler-plugin` build. Another build that includes `gradle-plugin` must
  include `compiler-plugin` too.

## Protobuf generation

When the build script calls `rpc { protoc() }`:

1. `DefaultProtocExtension` finds the source sets (Kotlin, Android, or Android KMP) and creates a `ProtoSourceSet`
   for each Kotlin source set.
2. It registers these tasks for each source set: `processXxxProtoFiles`, `processXxxProtoFilesImports`,
   `generateBufYamlXxx`, `generateBufGenYamlXxx`, `protobufGenerateXxx`.

| Output | Directory |
|---|---|
| Buf projects | `build/protoBuild/sourceSets/<sourceSet>/` |
| Kotlin sources | `build/protoBuild/generated/<sourceSet>/kotlin-multiplatform/` |

The plugin runs the `buf` CLI, not `protoc`. The code is in `buf/`:

| Class | Purpose |
|---|---|
| `BufExtension` | Flags of `buf generate`: include-imports, include-wkt, error-format, comments |
| `ProtobufGenerateTask` | Runs `buf generate` |
| `GenerateBufYaml`, `GenerateBufGenYaml` | Write `buf.yaml` and `buf.gen.yaml` from the DSL |

## Compiler plugin artifact

The compiler plugin is one artifact for all Kotlin versions: `org.jetbrains.kotlinx:kotlinx-rpc-compiler-plugin:<library version>`.
In the root build, Gradle replaces the artifact with the `compiler-plugin` included build.

`pluginDevKit {}` in `build.gradle.kts` generates the `PluginInfo` object with the compiler plugin ID and the artifact coordinates.
`RpcCompilerSupportPlugin` passes them to the Kotlin Gradle plugin.

## Generated sources

`build.gradle.kts` generates two files on each build. Don't edit them:

| File | Constants |
|---|---|
| `build/generated-sources/kotlin/main/Versions.kt` | `LIBRARY_VERSION`, `BUF_TOOL_VERSION` |
| `build/generated-sources/kotlin/test/TestVersions.kt` | `RPC_VERSION`, `ANDROID_HOME_DIR`, `BUILD_REPO` |

## Tests

### Unit tests

- `BaseTest.kt` declares the combinations of Gradle, KGP, and AGP versions.
  A test uses `@TestFactory` with `runWithGradleVersions()` to make one dynamic test for each combination.
- Each test function has a project template in `src/test/resources/projects/<TestClassName>/<test_function_name>/`:
  a `build.gradle.kts` and the proto files. `<test_function_name>` is the function name in lower case, with `_`
  for each space.
- `BaseTest.setupTest()` replaces the `<kotlin-version>`, `<rpc-version>`, and `<android-version>` placeholders.

### Functional tests

The tests in `src/functionalTest` apply the Gradle plugin to a project with each supported Kotlin version.
The project depends on the `kotlinx-rpc-core` library of this repository. One test runs the generated code.

- `:gradle-plugin:k<version>FunctionalTest` runs one Kotlin version, for example `k2420FunctionalTest` for 2.4.20.
- [functional-test-repository](../tests/functional-test-repository/build.gradle.kts) lists the libraries
  that the root build publishes for the tests.
- A compiler can't read a Kotlin standard library that is more than one minor version newer.
  On such a Kotlin version, the test that runs the generated code doesn't run.

## Conventions

- Mark a dangerous API, for example one that disables the annotation type checks, with `@RpcDangerousApi`.
- Mark an internal API with `@InternalRpcApi`.
