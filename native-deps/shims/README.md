# native-deps/shims

Shared standalone Gradle build for the Kotlin/Native shim artifacts.

Layout:
- `grpc/` backs the `:kotlinx-rpc-grpc-core-shim` publication and native interop artifacts.
- `protobuf/` backs the `:kotlinx-rpc-protobuf-shim` publication and native interop artifacts.
- `annotation/` backs the `:kotlinx-rpc-native-shims-annotation` publication.

Shared toolchain notes:
- Manual Bazel/Konan troubleshooting and LLVM bundle maintenance live in [../bazel-support/README.md](/Users/jozott/development/jetbrains/kotlinx-rpc/native-deps/bazel-support/README.md).

Useful tasks:
- `./gradlew :kotlinx-rpc-grpc-core-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository`
- `./gradlew :kotlinx-rpc-protobuf-shim:publishAllPublicationsToNativeDepsBuildRepoRepository :kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository`

gRPC overlap workflow (KRPC-540):
- Problem: grpc-core already depends on protobuf-lite, so a final native binary can see both shim KLIBs.
  When grpc-shim and protobuf-shim each embed the same native protobuf/absl archives, Linux linking may fail
  with duplicate symbol errors such as `AbslInternalGetFileMappingHint`.
- `utf8_range` can be excluded from grpc-shim because protobuf-shim already provides it transitively.
- `libsymbolize` cannot be excluded at archive granularity: grpc and protobuf currently bundle different
  Abseil LTS namespaces, so both `absl::...::Symbolize` implementations must remain available.
  The temporary workaround rewrites protobuf-shim's `AbslInternalGetFileMappingHint` helper symbol on Linux
  so the duplicate plain C symbol no longer collides with grpc-shim's copy.
  Remove this KRPC-540 workaround once protobuf is turned into a Kotlin-only implementation.
- Edit `grpc/overlap-archive-excludes.txt` to remove grpc bundle archives already supplied by the protobuf shim.
- Run `grpc/tools/analyze_overlap.py` against a grpc shim KLIB and a protobuf shim KLIB to find candidate exclusions before updating that file. Use `--write-excludes` together with `--exclude-scope <target>` if you want the script to append scoped entries directly.

iOS link fails for Kotlin 2.4.20+ (KXRPC-679):
- Problem: Kotlin 2.4.20 assumes that cinterop KLIB metadata references only the standard library or other cinterop
  KLIBs, and crashes when it encounters the previously injected annotation dependency.
- Solution: Shim KLIB metadata is no longer patched with internal opt-in annotations. Users can therefore access shim
  declarations without an internal annotation opt-in. This is acceptable while the project transitions to grpc-swift-2.
