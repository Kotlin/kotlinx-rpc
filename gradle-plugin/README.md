# Overview

This is the `gradle-plugin` subproject of kotlinx-rpc — an included Gradle build that provides the `org.jetbrains.kotlinx.rpc.plugin` Gradle plugin.

## Build & Test

**Prefer running individual tests** — the full suite is slow (multiple Gradle/KGP/AGP version combinations). 
Only run all tests when explicitly asked.

## What the plugin does

1. Verifies Kotlin Gradle Plugin is on the classpath
2. Creates the `rpc {}` DSL extension (`RpcExtension`)
3. Applies compiler plugin
4. Creates protobuf/gRPC code generation infrastructure (`createProtoExtensions()`) on demand (only when `rpc { protoc() }` is called in the build script)

## Two concerns in one plugin

The plugin handles two distinct responsibilities:
- **Compiler plugin integration** — wires the kotlinx-rpc compiler plugin into Kotlin compilation.
- **Protobuf/gRPC code generation** — manages `buf generate` for `.proto` files across JVM, KMP, and Android projects.

## Details

### Protoc-gen generation

When users call `rpc { protoc() }` in their build script:
1. `DefaultProtocExtension` discovers source sets (Kotlin, Android, or Android KMP) and creates a `ProtoSourceSet` per Kotlin source set
2. For each source set, it registers Gradle tasks: `processXxxProtoFiles`, `processXxxProtoFilesImports`, `generateBufYamlXxx`, `generateBufGenYamlXxx`, `bufGenerateXxx`
3. Generated Buf projects land in `build/protoBuild/sourceSets/<sourceSet>/`
4. Generated Kotlin sources land in `build/protoBuild/generated/<sourceSet>/kotlin-multiplatform/`

### Buf integration (`buf/`)

The plugin uses `buf` CLI tool (not `protoc` directly) for code generation:
- `BufExtension` — configuration for `buf generate` flags (include-imports, include-wkt, error-format, comments)
- `BufGenerateTask` — Gradle task wrapping `buf generate`
- `GenerateBufYaml`/`GenerateBufGenYaml` — tasks that generate `buf.yaml` and `buf.gen.yaml` configuration files from the DSL

### Internal development mode

When `kotlinx.rpc.plugin.internalDevelopment=true` (set in `gradle.properties`), the compiler plugin artifacts are resolved from local project paths instead of Maven coordinates. 
This strips the `kotlinx-rpc-` prefix from artifact IDs (`RpcPluginConst.kt`, `KotlinCompilerPluginBuilder.kt`).

### Generated sources at build time

`build.gradle.kts` generates two files:
- `Versions.kt` (main) — `LIBRARY_VERSION`, `BUF_TOOL_VERSION` constants
- `TestVersions.kt` (test) — `RPC_VERSION`, `ANDROID_HOME_DIR`, `BUILD_REPO` constants

### Test matrix

Tests run against multiple Gradle/KGP/AGP version combinations (defined in `BaseTest.kt`).

### Test project templates

Each test method has a corresponding directory under `src/test/resources/projects/<TestClassName>/<test_method_name>/` containing a `build.gradle.kts` template and proto source files. 
Templates use placeholders (`<kotlin-version>`, `<rpc-version>`, `<android-version>`) that `BaseTest.setupTest()` replaces at runtime.

Tests use `@TestFactory` with `runWithGradleVersions()` to generate dynamic tests per version combination.

`<test_method_name>` is mapped by taking the name of the test method, making it lowercase and replacing spaces with underscores.
That way tests can have more readable names.

## Conventions

- Dangerous APIs (like disabling annotation type-safety) are marked with `@RpcDangerousApi`
- Internal APIs use `@InternalRpcApi`
