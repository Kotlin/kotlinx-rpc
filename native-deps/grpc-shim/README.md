# native-deps/grpc-shim

Standalone Gradle build for publishing the Kotlin/Native gRPC core shim.

Using the `klib-patcher` all cinterop kotlin definitions are patched to be annotated with the `@InternalNativeRpcApi` 
annotation. 
This allows us to publish the shim without investing in backward compatibility API.

Layout:
- `core/` contains the Bazel build, native sources, headers, and the published shim module
- `annotation/` contains the opt-in marker published with the shim
- `klib-patcher/` contains the internal metadata patcher used only while building `core`
- `tests/` contains TestKit-based fixture tests that publish grpc-shim artifacts locally and compile throwaway consumer projects

Shared toolchain notes:
- Manual Bazel/Konan troubleshooting and LLVM bundle maintenance live in [../bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).

Useful tasks:
- `../../gradlew -p native-deps/grpc-shim :core:buildGrpcShimIosArm64`
- `../../gradlew -p native-deps/grpc-shim :core:publishAllPublicationsToBuildRepoRepository :annotation:publishAllPublicationsToBuildRepoRepository`
- `../../gradlew -p native-deps/grpc-shim :tests:test`

Prerequisite:
- publish the required `native-deps/grpc` artifacts to `build/repo` first
