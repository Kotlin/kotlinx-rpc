# native-deps/grpc

Standalone Gradle build for compiling and publishing prebuilt native gRPC artifacts.

Versioning:
- `gradle.properties` is the source of truth for `grpcVersion`.
- Gradle rewrites `MODULE.bazel` before Bazel-backed build/package/publish tasks so the Bazel `grpc` dependency stays in sync.
- Manual edits to `GRPC_VERSION` in `MODULE.bazel` will be overwritten on the next Gradle run.

Current shape:
- Bazel builds the ordered archive set for `@com_github_grpc_grpc//:grpc` and extracts gRPC headers.
- Shared Bazel `platforms` and `toolchain` live in `../bazel-support`.
- Gradle publishes one shared headers bundle and one native archive bundle per target.

Useful tasks:
- `../../gradlew -p native-deps/grpc buildGrpcHeaders`
- `../../gradlew -p native-deps/grpc packageGrpcHeaders`
- `../../gradlew -p native-deps/grpc buildGrpcIosArm64`
- `../../gradlew -p native-deps/grpc buildAllGrpcBundles`
- `../../gradlew -p native-deps/grpc publishAllPublicationsToBuildRepoRepository`

Published headers bundle contains:
- `include/**`
- `src/**` for grpc-internal headers needed by shim builds like `kgrpc`
- transitive dependency headers such as `absl/**` when grpc internal headers include them

Each published target bundle contains:
- `lib/**` with all transitive grpc dependency archives
- `metadata/archives.txt`

This is the initial standalone setup for the multi-archive packaging. It isolates gRPC build and publication from `kgrpc`, but it does not yet switch consumers over to the new bundle format.
