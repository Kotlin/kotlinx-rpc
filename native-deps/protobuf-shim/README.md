# native-deps/protobuf-shim

Standalone Gradle build for publishing the Kotlin/Native protobuf shim.

Using the `klib-patcher`, all cinterop Kotlin declarations are patched to be annotated with
`@InternalNativeRpcApi`. This allows us to publish the shim artifact while clearly marking the
interop surface as unsupported internal implementation detail.

Layout:
- `core/` contains the Bazel build, native sources, headers, and the published shim module
- `annotation/` contains the opt-in marker published with the shim
- `../klib-patcher/` contains the shared internal metadata patcher used only while building `core`
- `tests/` contains TestKit-based fixture tests that publish protobuf-shim artifacts locally and compile throwaway consumer projects

Shared toolchain notes:
- Manual Bazel/Konan troubleshooting and LLVM bundle maintenance live in [../bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).

Useful tasks:
- `../../gradlew -p native-deps/protobuf-shim :core:publishAllPublicationsToVerificationRepository`
- `../../gradlew -p native-deps/protobuf-shim :annotation:publishAllPublicationsToVerificationRepository`
- `../../gradlew -p native-deps/protobuf-shim :tests:test`
