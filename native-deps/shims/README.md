# native-deps/shims

Shared standalone Gradle build for the Kotlin/Native shim artifacts.

Layout:
- `protobuf/` backs the `:kotlinx-rpc-protobuf-shim` publication and native interop artifacts.
- `annotation/` backs the `:kotlinx-rpc-native-shims-annotation` publication.

Shared toolchain notes:
- Manual Bazel/Konan troubleshooting and LLVM bundle maintenance live in [../bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).

Useful tasks:
- `./gradlew :kotlinx-rpc-protobuf-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository`

iOS link fails for Kotlin 2.4.20+ (KXRPC-679):
- Problem: Kotlin 2.4.20 assumes that cinterop KLIB metadata references only the standard library or other cinterop
  KLIBs, and crashes when it encounters the previously injected annotation dependency.
- Solution: Shim KLIB metadata is no longer patched with internal opt-in annotations. Users can therefore access shim
  declarations without an internal annotation opt-in.
