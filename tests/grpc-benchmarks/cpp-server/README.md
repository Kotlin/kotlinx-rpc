# C++ benchmark server

This directory provides a standalone launcher for the official gRPC C++ QPS
server implementation. The upstream `qps_worker` cannot be used as a regular
benchmark endpoint because it first requires a gRPC performance driver to
configure and keep its server alive.

The build downloads `grpc/grpc` at revision
`b8f09d9168d856020d236bd32f39195f7b5aa2cf`, adds the standalone launcher as a
local Bazel target, and builds it with optimizations enabled. This is the same
revision from which the benchmark protos were vendored.

## Requirements

- `curl` and `tar`
- Bazelisk or Bazel 8.7.0
- A C++ build toolchain supported by gRPC

## Build and run

```shell
./build.sh
./run.sh
```

The server listens without TLS on `[::]:50051` and uses the asynchronous C++
server by default. Benchmark clients can connect to `localhost:50051`.

The following environment variables customize the launcher:

- `GRPC_BENCHMARK_PORT`: listening port; defaults to `50051`.
- `GRPC_BENCHMARK_SERVER_TYPE`: `async`, `callback`, or `sync`; defaults to
  `async`.
- `GRPC_BENCHMARK_MAX_MESSAGE_BYTES`: maximum request and response message
  size; defaults to 32 MiB for future large-message benchmark cases.
- `GRPC_BENCHMARK_BUILD_DIR`: source and build directory; defaults to
  `.build` inside this directory.

Additional arguments passed to `run.sh` are forwarded to the server binary.
The first build downloads the pinned gRPC checkout and its dependencies and can
take several minutes.
