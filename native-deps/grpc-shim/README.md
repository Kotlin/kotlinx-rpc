# native-deps/grpc-shim

Standalone Gradle build for publishing the Kotlin/Native gRPC core shim.

Current shape:
- resolves the published gRPC headers bundle and one target-specific gRPC archive bundle from `native-deps/grpc`
- builds `libkgrpc` with Bazel against grpc headers from the grpc Bazel dependency
- generates a target-specific cinterop `.def` file so the published klib bundles `libkgrpc` together with the grpc archives
- uses the published grpc headers bundle only for Kotlin/Native cinterop include paths under `include/**`
- versions the published artifacts as `<grpcVersion>-<shimVersion>`, so grpc bumps and shim-only bumps stay separate

Useful tasks:
- `../../gradlew -p native-deps/grpc-shim buildGrpcShimIosArm64`
- `../../gradlew -p native-deps/grpc-shim publishToBuildRepo`

Prerequisite:
- publish the required `native-deps/grpc` artifacts to `build/repo` first
