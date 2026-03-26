# native-deps

Standalone builds and shared tooling for native dependency artifacts used by Kotlin/Native parts of the repository.

Structure:
- `grpc/` builds and publishes raw native gRPC bundles and public headers.
- `grpc-shim/` builds the Kotlin/Native gRPC shim on top of the published `grpc/` artifacts.
- `protobuf-shim/` builds the Kotlin/Native protobuf shim with the same internal-only publication model.
- `klib-patcher/` is shared internal build tooling that patches published cinterop KLIB metadata so shim declarations require explicit opt-in via `@InternalNativeRpcApi`.
- `bazel-support/` contains shared Bazel and Kotlin/Native toolchain support used by the active native-deps builds.

Shim model:
- Each shim is split into `core/`, `annotation/`, and `tests/`.
- `core/` publishes the actual shim artifact and runs cinterop.
- `annotation/` publishes the opt-in marker required by shim consumers.
- `tests/` uses Gradle TestKit to publish local artifacts and compile throwaway consumer projects that verify opt-in enforcement.

Build flow:
1. `grpc/` publishes native inputs.
2. A shim `core/` project consumes those native inputs, produces cinterop KLIBs, and patches only its internal cinterop package.
3. The patched KLIB is published together with its annotation artifact.
4. Fixture tests verify both the published artifact contents and downstream compiler behavior.

Operational notes:
- Manual Bazel and Konan troubleshooting live in [bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).
- The shared `klib-patcher/` is internal build tooling only. It is not part of the public RPC compiler plugin story.
