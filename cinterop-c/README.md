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
bazel build //:protowire --config=linux_arm64 --define=KONAN_HOME=$HOME/.konan/kotlin-native-prebuilt-macos-aarch64-2.2.10
```

#### Upgrading the Kotlin Compiler Version

When we upgrade the project's Kotlin compiler version, compilation for Linux will fail.
Bazel will throw an error like

```
[1,351 / 1,353] Compiling src/kgrpc.cpp; 1s darwin-sandbox
ERROR: /Users/jozott/development/jetbrains/kotlinx-rpc/cinterop-c/BUILD.bazel:11:11: Compiling src/kgrpc.cpp failed: absolute path inclusion(s) found in rule '//:kgrpc_lib':
the source file 'src/kgrpc.cpp' includes the following non-builtin files with absolute paths (if these are builtin files, make sure these paths are in your toolchain):
/Users/jozott/development/jetbrains/kotlinx-rpc/cinterop-c/BUILD.bazel:11:11: Compiling src/kgrpc.cpp failed: absolute path inclusion(s) found in rule '//:kgrpc_lib':

/Users/jozott/.konan/dependencies/llvm-19-aarch64-macos-essentials-79/bin/clang ... -c src/kgrpc.cpp -o bazel-out/linux_arm64-opt/bin/_objs/kgrpc_lib/kgrpc.o
  '/Users/jozott/.konan/dependencies/llvm-19-aarch64-macos-essentials-79/lib/clang/19/include/stdbool.h'
  '/Users/jozott/.konan/dependencies/llvm-19-aarch64-macos-essentials-79/lib/clang/19/include/stdint.h'
  ...
```

To fix this, we need to adjust the clang built-in include paths defined in the `toolchain/cc_toolchain_config.bzl` file.
In the case above, the path at `cxx_builtin_include_directories` must be replaced by   
`deps + "llvm-19-aarch64-macos-essentials-79/lib/clang/19/include"`.
This must be done for aarch64 and x86_64 separately.
The current LLVM toolchain bundle paths can be found in
[kotlin/kotlin-native/gradle.properties](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/gradle.properties)
of the Kotlin repository.
