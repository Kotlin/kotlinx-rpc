# native-deps/shims

Shared standalone Gradle build for the Kotlin/Native shim artifacts.

Layout:
- `grpc/` backs the `:kotlinx-rpc-grpc-core-shim` publication and native interop artifacts.
- `protobuf/` backs the `:kotlinx-rpc-protobuf-shim` publication and native interop artifacts.
- `annotation/` backs the `:kotlinx-rpc-native-shims-annotation` publication.
- `klib-patcher/` contains the shared internal metadata patcher used only while building the shim KLIBs.
- `tests/` contains TestKit-based fixture tests that publish shim artifacts locally and compile throwaway consumer projects.

Shared toolchain notes:
- Manual Bazel/Konan troubleshooting and LLVM bundle maintenance live in [../bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).

Useful tasks:
- `./gradlew :kotlinx-rpc-grpc-core-shim:publishAllPublicationsToBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToBuildRepoRepository`
- `./gradlew :kotlinx-rpc-protobuf-shim:publishAllPublicationsToBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToBuildRepoRepository`
- `./gradlew :tests:test`
