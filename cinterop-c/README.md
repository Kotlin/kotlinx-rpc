This subdirectory contains the C sources required by native targets.
It uses the Bazel build system and contains two libraries: protowire and kgrpc.

### Protowire

Is a thin layer over the CodedStream implementation of the C++ protobuf library. 
To build (e.g. ios_arm64) run 
```bash
bazel build :protowire_fat --config=ios_arm64 --config=release
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
