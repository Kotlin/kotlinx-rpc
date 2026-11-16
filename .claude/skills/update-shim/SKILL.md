---
name: update-shim
description: >
  Modify, build, publish, and verify the Kotlin/Native shim artifacts (the
  protobuf shim and the shared annotations) in native-deps/shims/. Use this skill
  whenever the user wants
  to change shim C/C++ source code, update shim headers or .def files, bump a shim
  upstream version, update the annotation module, publish shim artifacts
  locally, or debug any native shim build failure. Also trigger when the user mentions
  "shim", "cinterop", "native interop", "protobuf shim",
  "native-deps", "Bazel shim build", or "shim verification".
---

# Updating Native Shims

Native shims are Kotlin/Native cinterop wrappers around third-party C/C++ libraries
(the protobuf wire library). They live in `native-deps/shims/` as a **standalone
Gradle build** (separate from the main kotlinx-rpc project).

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

  settings.gradle.kts
  gradle.properties

native-deps/scripts/
  build_target.sh                # Runs Bazel for one target, copies static library out
```

The build helper `build_target.sh` lives in the sibling `native-deps/scripts/`
directory (alongside the other native-deps utility scripts). It is still invoked
from each shim module directory (which owns the Bazel workspace) by the
`registerNativeShimBazelBuildTask` Gradle helper.

The protobuf shim builds its own fat archive via Bazel directly.

## Build Pipeline (per shim, per native target)

1. **Bazel builds** a target-specific static library (`build_target.sh` wraps `bazel build`).
2. **Gradle uses** the Bazel output directly (protobuf).
3. **CInterop** generates Kotlin bindings from the `.def` file + headers + static libs.
4. The generated KLIB is **published** alongside the annotation artifact.

## Versioning

Versions live in `versions-root/libs.versions.toml`:

| Alias | Current | Format |
|---|---|---|
| `internal-native-protobuf-shim` | `34.0-1` | `<upstream-protobuf-version>-<shim-revision>` |
| `internal-native-shim-annotation` | `0.1.0` | Independent semver |

When making shim code changes (not upstream bumps), bump only the **shim revision**
(the part after the dash). When bumping the upstream library version, update the
**base version** and reset the shim revision to `1`.

The base version is automatically synced to the corresponding `MODULE.bazel` file
(the `PROTOBUF_VERSION` variable) by a Gradle task at build time.

---

## Making Changes

### Changing C/C++ shim code or headers

The shim C/C++ source lives alongside its Bazel build:

- **Protobuf**: `protobuf/src/cpp/protowire.cpp` + `protobuf/include/protowire.h`

After editing:
1. If you added/removed/renamed C functions, update the corresponding `.def` file
   to reflect the new API surface. The `.def` file controls what CInterop exposes
   to Kotlin.
2. Bump the shim revision in `versions-root/libs.versions.toml`.
3. Build and verify (see sections below).

### Changing CInterop definitions

- **Protobuf**: `protobuf/src/nativeInterop/cinterop/libprotowire.def` — this file
  lists per-target `staticLibraries` inline (one `staticLibraries.{target}` entry
  per native target). Edit header includes and filter rules here.

### Changing the annotation module

Annotations live in `annotation/src/commonMain/kotlin/`. Both are `@RequiresOptIn(level = ERROR)`.
Changes here are rare — typically only when adding a new shim family.

### Bumping an upstream version

1. Update the base version in `versions-root/libs.versions.toml` (e.g., change
   `internal-native-protobuf-shim` from `34.0-1` to `35.0-1`).
2. The `MODULE.bazel` version variable is synced automatically by the
   `syncProtobufVersionToBazelModule` Gradle task.
3. Build and verify (see sections below).

---

## Publishing Locally

All tasks in this section target the shims standalone build (projectRoot: `native-deps/shims/`).

**Prerequisite**: Bazel (or Bazelisk) must be on PATH. On macOS, Xcode (not just
CommandLineTools) should be installed for cross-compilation to iOS/tvOS/watchOS.

### Publish to the local build repo

- **Protobuf shim + annotation**: task
  `:kotlinx-rpc-protobuf-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository`

Published artifacts land in `native-deps/build/repo/`. The main kotlinx-rpc project
is configured to resolve from this path during development.

### Build without publishing (sanity check)

- Protobuf shim: task `:kotlinx-rpc-protobuf-shim:assemble`

---

## Verification

### Verify in the main project

After publishing locally, build the shim consumers in the **main** project
(default projectRoot) via `running_gradle_builds`:

- Protobuf shim consumer: task `:protobuf:protobuf-lite:compileKotlinMacosArm64`

Use whichever native target matches your host. If it compiles cleanly, the shim
changes integrate correctly with the rest of the project.

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

**Clean rebuild** (projectRoot: `native-deps/shims/`):
1. Run `clean` task
2. Optionally run `bazel clean --expunge` via shell for Bazel cache
3. Run `:kotlinx-rpc-protobuf-shim:assemble` task
