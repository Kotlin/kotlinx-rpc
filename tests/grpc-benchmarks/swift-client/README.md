# gRPC Swift benchmark client

This directory contains command-line benchmarks for the gRPC Swift 2 client.
It builds an optimized arm64 iOS Simulator executable and runs against the
standalone C++ benchmark server in `../cpp-server`.

Build and run it with:

```shell
./build.sh
./run.sh list
./run.sh run unary-latency
./run.sh run unary-throughput \
    --request-bytes 4096 --response-bytes 4096 --concurrency 32 --format csv
./run.sh run unary-payload-sweep --case symmetric-1m
./run.sh run unary-concurrency-sweep --case 1k-c64 --format csv
```

`run.sh` uses a currently booted simulator, or boots the first available
iPhone. Set `KXRPC_BENCHMARK_SIMULATOR` to a simulator UDID to select another
device. The default benchmark server is `localhost:50051`.

The initial presets match the kotlinx-rpc client:

- `unary-latency`: 100 warmups, 1,000 measured calls, concurrency 1, empty payloads
- `unary-throughput`: 100 warmups, 10,000 measured calls, concurrency 16, 1 KiB request and response

The payload and concurrency sweeps emit one result per named case. Use
`--case NAME` to run one point or omit it to run the complete sweep.

## Adding a benchmark

Implement `Benchmark` directly, or use `CallBenchmark` when the measurement is
a repeated operation. Register the new definition alongside `unaryBenchmarks()`
in `BenchmarkCLI`. The shared runner handles warmup, concurrent workers,
latency sampling, throughput calculation, and human or CSV output.

The protobuf file contains the wire-compatible subset of the official
`grpc.testing.BenchmarkService` needed by registered Swift benchmarks. Extend
it when a new benchmark requires another RPC or message field.
