# native-deps/bazel-support

Shared Bazel infrastructure for native dependency builds.

Currently contains:
- `platforms/`
- `toolchain/`
- `tools/collect_headers.bzl`

Used by:
- `native-deps/grpc`
- `native-deps/grpc-shim`
- `native-deps/protobuf-shim`

Kotlin/Native toolchain notes:
- Linux target builds use the Kotlin/Native toolchain via the shared Bazel toolchain in `toolchain/`.
- `toolchain/konan_llvm_bundles.json` pins LLVM bundle names per Kotlin compiler version.
- Do not manually patch toolchain internals when Kotlin changes; refresh the mapping instead.

Updating Kotlin compiler support:
- Run `./toolchain/precompute_konan_llvm_bundles.py` from this directory to refresh `konan_llvm_bundles.json`.
- The script reads upstream Kotlin Native metadata and updates the host-to-LLVM bundle mapping used by native-deps builds.
- CI verifies that the mapping matches the current project Kotlin compiler version.

Troubleshooting:
- `Missing precomputed LLVM mapping for Kotlin compiler version ...`
  Run `./toolchain/precompute_konan_llvm_bundles.py <kotlin-version>` and commit the updated `konan_llvm_bundles.json`.
- `Expected LLVM bundle directory is not installed ...`
  Refresh Kotlin/Native dependencies via Gradle and ensure `KONAN_HOME` and `KONAN_DEPS` point to the same installation.

Manual Bazel invocation:
- For direct Linux target `bazel build` calls, pass:
  `--define=KONAN_HOME=... --define=KONAN_DEPS=... --define=KONAN_LLVM_RESOURCE_DIR=...`
