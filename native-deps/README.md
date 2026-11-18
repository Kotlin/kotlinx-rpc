# native-deps

Native dependencies of the Kotlin/Native parts of the repository.

`kotlinx-rpc-grpc-swift/` is a git submodule with the sources of the
[kotlinx-rpc-grpc-swift](https://github.com/Kotlin/kotlinx-rpc-grpc-swift) Swift package:

- The gRPC client imports its `GrpcSwiftBridge` library on Apple platforms.
- The `grpc-swift-bridge` version in the version catalog names the tag of the published package that the build
  resolves. The submodule commit must match that tag. The `verify-grpc-swift-bridge` GitHub workflow checks this on
  pull requests.
- Run `git submodule update --init --recursive` after a clone to read or edit the sources.
