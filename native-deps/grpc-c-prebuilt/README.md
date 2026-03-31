# native-deps/grpc-c-prebuilt

Standalone Gradle build for compiling and publishing prebuilt native gRPC artifacts (static libraries `.a`).

Versioning:
- `versions-root/libs.versions.toml` is the source of truth for the gRPC version used here via `internal-native-grpc-shim`.
- Gradle rewrites `MODULE.bazel` before Bazel-backed build/package/publish tasks so the Bazel `grpc` dependency stays in sync.
- Manual edits to `GRPC_VERSION` in `MODULE.bazel` will be overwritten on the next Gradle run.

Current shape:
- Bazel builds the ordered archive set for `@com_github_grpc_grpc//:grpc` and extracts the public gRPC C headers.
- Shared Bazel `platforms` and `toolchain` live in `../bazel-support`.
- Gradle publishes one shared headers bundle and one native archive bundle per target.

Shared toolchain notes:
- Manual Bazel/Konan troubleshooting and LLVM bundle maintenance live in [../bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).

Useful tasks:
- `./gradlew buildGrpcHeaders`
- `./gradlew packageGrpcHeaders`
- `./gradlew buildGrpcIosArm64`
- `./gradlew buildAllGrpcBundles`
- `./gradlew publishAllPublicationsToBuildRepoRepository`

Published headers bundle contains:
- `include/**`

Each published target bundle contains:
- `lib/**` with all transitive grpc dependency archives
- `metadata/archives.txt`

This is the initial standalone setup for the multi-archive packaging. It isolates gRPC build and publication from the shim consumers, but it does not yet switch every consumer over to the new bundle format.
