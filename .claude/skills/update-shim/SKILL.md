---
name: update-shim
description: >
  Modify, build, publish, and verify the Kotlin/Native shim artifacts (gRPC shim
  and protobuf shim) in native-deps/shims/. Use this skill whenever the user wants
  to change shim C/C++ source code, update shim headers or .def files, bump a shim
  upstream version, add or remove archives
  from the overlap excludes list, patch KLIB metadata, update the annotation module,
  run shim fixture tests, publish shim artifacts locally, or debug any native shim
  build failure. Also trigger when the user mentions "shim", "cinterop", "native
  interop", "grpc shim", "protobuf shim", "KLIB patcher", "overlap archives",
  "native-deps", "Bazel shim build", or "shim verification".
---

# Updating Native Shims

Native shims are Kotlin/Native cinterop wrappers around third-party C/C++ libraries
(gRPC core and protobuf wire). They live in `native-deps/shims/` as a **standalone
Gradle build** (separate from the main kotlinx-rpc project). Each shim produces a
patched KLIB that requires explicit `@OptIn` to use, keeping native internals out
of the public API surface.

## How to Execute

Use the `running_gradle_builds` skill for all Gradle build/publish tasks and the
`running_gradle_tests` skill for all Gradle test tasks. Do NOT run `./gradlew`
directly via shell.

Because the shims build is a **standalone Gradle project**, you must set `projectRoot`
to the absolute path of `native-deps/shims/` when invoking Gradle skills for shim
tasks. For verification tasks that run in the **main** kotlinx-rpc project, use the
default projectRoot (omit it or point to the repo root).

## Module Layout

```
native-deps/shims/
  grpc/                          # :kotlinx-rpc-grpc-core-shim
    src/cpp/kgrpc.cpp            # C shim wrapper source
    src/nativeInterop/cinterop/
      grpcCoreInterop.def        # CInterop definition (template — staticLibraries injected at build)
    include/kgrpc.h              # Shim header
    BUILD.bazel                  # Bazel target :grpc_shim
    MODULE.bazel                 # Bazel module (GRPC_VERSION synced from version catalog)
    overlap-archive-excludes.txt # Archives excluded because protobuf-shim already provides them
    tools/analyze_overlap.py     # Symbol overlap analysis helper
    build.gradle.kts

  protobuf/                      # :kotlinx-rpc-protobuf-shim
    src/cpp/protowire.cpp        # C shim wrapper source
    src/nativeInterop/cinterop/
      libprotowire.def           # CInterop definition (per-target static libs listed inline)
    include/protowire.h          # Shim header
    BUILD.bazel                  # Bazel target :protowire_fat
    MODULE.bazel                 # Bazel module (PROTOBUF_VERSION synced from version catalog)
    build.gradle.kts

  annotation/                    # :kotlinx-rpc-native-shims-annotation
    src/commonMain/kotlin/
      kotlinx/rpc/grpc/internal/shim/InternalNativeRpcApi.kt
      kotlinx/rpc/protobuf/internal/shim/InternalNativeProtobufApi.kt
    build.gradle.kts

  klib-patcher/                  # Internal build tool (not published)
    src/main/kotlin/.../KlibPatcher.kt

  tests/                         # Gradle TestKit fixture tests
    src/test/kotlin/...

  build_target.sh                # Runs Bazel for one target, copies static library out
  settings.gradle.kts
  gradle.properties
```

The `native-deps/grpc-c-prebuilt/` sibling directory publishes raw `.a` static
archives and headers consumed by the gRPC shim. The protobuf shim builds its own
fat archive via Bazel directly.

## Build Pipeline (per shim, per native target)

1. **Bazel builds** a target-specific static library (`build_target.sh` wraps `bazel build`).
2. **Gradle unpacks** prebuilt archives (gRPC) or uses the Bazel output directly (protobuf).
3. **CInterop** generates Kotlin bindings from the `.def` file + headers + static libs.
4. **KlibPatcher** post-processes the KLIB to inject `@RequiresOptIn` annotations on all
   declarations, so consumers must `@OptIn(InternalNativeRpcApi::class)` (or the protobuf
   equivalent) to use them.
5. The patched KLIB is **published** alongside the annotation artifact.

## Versioning

Versions live in `versions-root/libs.versions.toml`:

| Alias | Current | Format |
|---|---|---|
| `internal-native-grpc-shim` | `1.74.1-2` | `<upstream-grpc-version>-<shim-revision>` |
| `internal-native-protobuf-shim` | `31.1-2` | `<upstream-protobuf-version>-<shim-revision>` |
| `internal-native-shim-annotation` | `0.1.0` | Independent semver |

When making shim code changes (not upstream bumps), bump only the **shim revision**
(the part after the dash). When bumping the upstream library version, update the
**base version** and reset the shim revision to `1`.

The base version is automatically synced to the corresponding `MODULE.bazel` file
(`GRPC_VERSION` or `PROTOBUF_VERSION` variable) by a Gradle task at build time.

---

## Making Changes

### Changing C/C++ shim code or headers

The shim C/C++ source lives alongside its Bazel build:

- **gRPC**: `grpc/src/cpp/kgrpc.cpp` + `grpc/include/kgrpc.h`
- **Protobuf**: `protobuf/src/cpp/protowire.cpp` + `protobuf/include/protowire.h`

After editing:
1. If you added/removed/renamed C functions, update the corresponding `.def` file
   to reflect the new API surface. The `.def` file controls what CInterop exposes
   to Kotlin.
2. Bump the shim revision in `versions-root/libs.versions.toml`.
3. Build and verify (see sections below).

### Changing CInterop definitions

- **gRPC**: `grpc/src/nativeInterop/cinterop/grpcCoreInterop.def` — this is a
  **template**. The `__STATIC_LIBRARIES__` placeholder is replaced at build time
  with the full archive list from the unpacked gRPC bundle. Edit filter rules,
  header includes, strict enums, and noStringConversion entries here.
- **Protobuf**: `protobuf/src/nativeInterop/cinterop/libprotowire.def` — this file
  lists per-target `staticLibraries` inline (one `staticLibraries.{target}` entry
  per native target). Edit header includes and filter rules here.

### Changing the annotation module

Annotations live in `annotation/src/commonMain/kotlin/`. Both are `@RequiresOptIn(level = ERROR)`.
Changes here are rare — typically only when adding a new shim family.

### Changing the KLIB patcher

`klib-patcher/src/main/kotlin/kotlinx/rpc/nativedeps/tooling/KlibPatcher.kt`
patches KLIB metadata post-cinterop. It uses `kotlinx-metadata-klib` to rewrite
`.knm` files, adding the opt-in annotation to every declaration in the target
package. Changes here are delicate — test thoroughly.

### Managing archive overlaps (KRPC-540)

gRPC and protobuf both bundle native Abseil/protobuf archives. When a final binary
links both shims, duplicate symbols can cause linker failures (especially on Linux).

**Overlap excludes file**: `grpc/overlap-archive-excludes.txt`
- Format: `all:<archive>` (every target) or `<target>:<archive>` (target-specific)
- Archives can be named in bundle-manifest form (`lib/foo/bar.a`) or flattened
  interop form (`foo+__bar.a`)
- Currently excludes `utf8_range` from gRPC shim since protobuf-shim provides it

**Symbol rewrite workaround** (Linux only, temporary — remove when protobuf becomes
Kotlin-only): The protobuf shim build rewrites `AbslInternalGetFileMappingHint` to
`KrpcProtobuf_AbslInternalGetFileMappingHint` using `llvm-objcopy --redefine-sym`.
This lives in `protobuf/build.gradle.kts`.

**To investigate new overlaps:**
```bash
cd native-deps/shims
python3 grpc/tools/analyze_overlap.py \
  --grpc-klib <path-to-grpc-cinterop-klib> \
  --protobuf-klib <path-to-protobuf-cinterop-klib> \
  --target linux_x64
```
Use `--write-excludes <file> --exclude-scope <target>` to auto-append entries.

### Bumping an upstream version

1. Update the base version in `versions-root/libs.versions.toml` (e.g., change
   `internal-native-grpc-shim` from `1.74.1-2` to `1.75.0-1`).
2. The `MODULE.bazel` version variable is synced automatically by the
   `syncGrpcVersionToBazelModule` / `syncProtobufVersionToBazelModule` Gradle task.
3. For gRPC: also update the `grpc-c-prebuilt` version if the prebuilt archive
   version needs to match.
4. Build, verify, and check for new symbol overlaps.

---

## Publishing Locally

All tasks in this section target the shims standalone build (projectRoot: `native-deps/shims/`).

**Prerequisite**: Bazel (or Bazelisk) must be on PATH. On macOS, Xcode (not just
CommandLineTools) should be installed for cross-compilation to iOS/tvOS/watchOS.

### Publish to the local build repo

- **gRPC shim + annotation**: task
  `:kotlinx-rpc-grpc-core-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository`
- **Protobuf shim + annotation**: task
  `:kotlinx-rpc-protobuf-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository`

Published artifacts land in `native-deps/build/repo/`. The main kotlinx-rpc project
is configured to resolve from this path during development.

### Publish both shims together

Task: `:kotlinx-rpc-grpc-core-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-protobuf-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository`

### Build without publishing (sanity check)

- gRPC shim: task `:kotlinx-rpc-grpc-core-shim:assemble`
- Protobuf shim: task `:kotlinx-rpc-protobuf-shim:assemble`

---

## Verification

### 1. Run fixture tests

The `tests/` module uses Gradle TestKit to publish shim artifacts to a temporary
verification repo and compile throwaway consumer projects against them.

Via `running_gradle_tests` (projectRoot: `native-deps/shims/`), task: `:tests:test`

This runs 8 test cases (4 per shim):

| Test         | What it verifies                                                                                       |
|--------------|--------------------------------------------------------------------------------------------------------|
| **negative** | Code using shim symbols without `@OptIn` fails to compile with the expected diagnostic message         |
| **positive** | Code with `@OptIn(InternalNativeRpcApi::class)` compiles successfully                                  |
| **scope**    | Unrelated native code is unaffected by shim markers                                                    |
| **artifact** | Published KLIB manifest contains the annotation dependency and `.knm` metadata carries annotation markers |

Single test category examples:
- `:tests:test --tests "*grpcNegativeTest"`
- `:tests:test --tests "*protobufArtifactTest"`

### 2. Verify in the main project

After publishing locally, build the shim consumers in the **main** project
(default projectRoot) via `running_gradle_builds`:

- Protobuf shim consumer: task `:protobuf:protobuf-api:compileKotlinMacosArm64`
- gRPC shim consumer: task `:grpc:grpc-core:compileKotlinMacosArm64`

Use whichever native target matches your host. If both compile cleanly, the shim
changes integrate correctly with the rest of the project.

### 3. Check for symbol overlaps (when changing archives)

If you modified archive contents, overlap excludes, or bumped an upstream version:

```bash
cd native-deps/shims
python3 grpc/tools/analyze_overlap.py \
  --grpc-klib build/classes/kotlin/macosArm64/main/cinterop/cinterop-grpcCoreInterop.klib \
  --protobuf-klib build/classes/kotlin/macosArm64/main/cinterop/cinterop-libprotowire.klib \
  --target macos_arm64
```

Check Linux targets separately (they have the KRPC-540 symbol rewrite):
```bash
python3 grpc/tools/analyze_overlap.py \
  --grpc-klib build/classes/kotlin/linuxX64/main/cinterop/cinterop-grpcCoreInterop.klib \
  --protobuf-klib build/classes/kotlin/linuxX64/main/cinterop/cinterop-libprotowire.klib \
  --target linux_x64
```

---

## Troubleshooting

**Bazel not found**: Install Bazelisk (`brew install bazelisk` on macOS) — it
manages Bazel versions automatically.

**Xcode issues on macOS**: The build needs full Xcode for Apple platform SDKs.
If `DEVELOPER_DIR` is not set, the build script defaults to
`/Applications/Xcode.app/Contents/Developer`.

**KONAN_HOME resolution**: The build downloads Kotlin/Native automatically via the
`:core:downloadKotlinNativeDistribution` task from the main project. If this fails,
check that the main project builds first.

**Linux symbol collision at link time**: If you see duplicate
`AbslInternalGetFileMappingHint` errors, the KRPC-540 workaround in
`protobuf/build.gradle.kts` may need updating. Check that `llvm-objcopy` is
available (the build searches Konan dependencies, then PATH).

**KLIB patcher failures**: Usually caused by a Kotlin version mismatch in the
metadata API. Check that `klib-patcher/build.gradle.kts` dependencies match the
Kotlin version used by the shim build.

**Clean rebuild** (projectRoot: `native-deps/shims/`):
1. Run `clean` task
2. Optionally run `bazel clean --expunge` via shell for Bazel cache
3. Run `:kotlinx-rpc-grpc-core-shim:assemble` task
