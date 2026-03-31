# native-deps

Standalone builds and shared tooling for native dependency artifacts used by Kotlin/Native parts of the repository.

Structure:
- `grpc-c-prebuilt/` builds and publishes raw native gRPC bundles and public headers. This does not contain any
   kotlin code and is not publishing any KLib artifacts, but .zip artifacts that contain `.a` static libraries.
- `shims/` contains the Kotlin/Native shim build, including the gRPC shim, the protobuf shim, the shared shim annotations, and fixture tests.
- `bazel-support/` contains shared Bazel and Kotlin/Native toolchain support used by the active native-deps builds.

Shim model:
- `shims/grpc/` publishes the gRPC shim artifact and runs cinterop.
- `shims/protobuf/` publishes the protobuf shim artifact and runs cinterop.
- `shims/annotation/` publishes the shared opt-in markers required by shim consumers.
- `shims/klib-patcher/` patches published cinterop KLIB metadata so shim declarations require explicit opt-in.
- `shims/tests/` uses Gradle TestKit to publish local artifacts and compile throwaway consumer projects that verify opt-in enforcement.

Build flow:
1. `grpc-c-prebuilt/` publishes prebuilt grpc-c core static archives `.a` and public headers.
2. A shim module consumes those native archives, produces cinterop KLIBs, and patches only its internal cinterop package.
3. The patched KLIB is published together with its annotation artifact.
4. Fixture tests verify both the published artifact contents and downstream compiler behavior.

Operational notes:
- Manual Bazel and Konan troubleshooting live in [bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).
- The shared `shims/klib-patcher/` module is internal build tooling only. It is not part of the public RPC compiler plugin story.


### Versioning and Publication

1. `grpc-c-prebuilt` artifacts are versioned by the gRPC version they contain.
2. `shims/annotation` has its own versioning scheme (semver)
3. `shims/grpc` also has its own versioning scheme: `<grpc-version>-<shim-version>` and is independently published.
4. `shims/protobuf` also has its own versioning scheme: `<protobuf-version>-<shim-version>` and is independently published.