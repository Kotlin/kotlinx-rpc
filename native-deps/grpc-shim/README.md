# native-deps/grpc-shim

Standalone Gradle build for publishing the Kotlin/Native gRPC core shim.

Layout:
- `core/` contains the Bazel build, native sources, headers, and the published shim module
- `annotation/` contains the opt-in marker published with the shim
- `klib-patcher/` contains the internal metadata patcher used only while building `core`
- `tests/` contains TestKit-based fixture tests that publish grpc-shim artifacts locally and compile throwaway consumer projects

Useful tasks:
- `../../gradlew -p native-deps/grpc-shim :core:buildGrpcShimIosArm64`
- `../../gradlew -p native-deps/grpc-shim publishToBuildRepo`
- `../../gradlew -p native-deps/grpc-shim :tests:test`

Prerequisite:
- publish the required `native-deps/grpc` artifacts to `build/repo` first
