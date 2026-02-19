This subdirectory contains the C sources required by native targets.
It uses the Bazel build system and contains two libraries: protowire and kgrpc.

### Protowire

Is a thin layer over the CodedStream implementation of the C++ protobuf library.
To build (e.g. ios_arm64) run

```bash
bazel build :protowire_fat --config=ios_arm64 --config=release
```

You can manually run a Bazel task e.g., with
```
bazel build :protowire_fat --config=linux_x64 --config=release \
    --define=KONAN_DEPS=/root/.konan/dependencies \
    --define=KONAN_HOME=/root/.konan/kotlin-native-prebuilt-linux-x86_64-2.2.21
```

### KgRPC

We are using the gRPC-core library, which already exports its API with a C ABI.
Therefore, the KgRPC library is almost empty primarily used for convenient functions
or API that is not exposed by the C API.

Because the gRPC takes a while to build when compiling for multiple targets, we store
it as a prebuilt static (fat) in `prebuilt-deps/grpc_fat`.
The binary can be updated by running

```bash
./gradlew :grpc:grpc-core:buildDependencyCLibGrpc_fat_iosArm64
```

### Compiling for Apple targets

All Konan Apple targets have a corresponding build config in `.bazelrc`.
To tell Bazel the apple target constraints, we use the `apple_support` rules.
All apple target rules are defined in https://github.com/bazelbuild/apple_support/blob/master/configs/platforms.bzl.
For Konan Target to Apple triplet mapping, see https://kotlinlang.org/docs/native-target-support.html.

### Compiling for Linux

To produce K/N compatible static libraries, we use the Konan toolchain for compilation.
The Bazel toolchain is specified in `toolchain/` and requires the user to specify the
`KONAN_HOME` variable like

```bash
bazel build //:protowire --config=linux_arm64 \
  --define=KONAN_HOME=$HOME/.konan/kotlin-native-prebuilt-macos-aarch64-2.3.0 \
  --define=KONAN_DEPS=$HOME/.konan/dependencies \
  --define=KONAN_LLVM_RESOURCE_DIR=$HOME/.konan/dependencies/llvm-19-aarch64-macos-essentials-79/lib/clang/19/include
```

#### Upgrading the Kotlin Compiler Version

LLVM bundle names are pinned per Kotlin compiler version in
`toolchain/konan_llvm_bundles.json`.
Do not manually edit `toolchain/cc_toolchain_config.bzl` when Kotlin compiler changes.

When Kotlin compiler version changes, refresh the mapping:

```bash
cd cinterop-c
./toolchain/precompute_konan_llvm_bundles.py
```

This script reads upstream Kotlin metadata from
`https://raw.githubusercontent.com/JetBrains/kotlin/v<kotlin-version>/kotlin-native/gradle.properties`
and updates `toolchain/konan_llvm_bundles.json`.

A GitHub workflow verifies the mapping for the current project Kotlin compiler version.
If the mapping is stale, run the script and commit the updated JSON file.

#### Troubleshooting

- `Missing precomputed LLVM mapping for Kotlin compiler version ...` \
  : run `./toolchain/precompute_konan_llvm_bundles.py <kotlin-version>` and commit the resulting JSON update.

- `Expected LLVM bundle directory is not installed ...`
  : refresh Kotlin/Native dependencies (e.g. via `./gradlew`) and ensure `KONAN_HOME`/`KONAN_DEPS` point to the same installation.

- For direct manual `bazel build` invocations on Linux targets, always pass:
  : `--define=KONAN_HOME=... --define=KONAN_DEPS=... --define=KONAN_LLVM_RESOURCE_DIR=...`.
