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
    --request-bytes 4096 --response-bytes 4096 --concurrency 32 --format json
./run.sh run unary-payload-sweep --case symmetric-1m
```

`run.sh` uses a currently booted simulator, or boots the first available
iPhone. Set `KXRPC_BENCHMARK_SIMULATOR` to a simulator UDID to select another
device. The default benchmark server is `localhost:50051`.

The workload matrix matches the Kotlin clients exactly; `../bench doctor`
verifies it. `run --format json` writes one trial record per line, with the raw
latency samples, in the same schema as the Kotlin clients.

For repeated, order-controlled measurements of all implementations, prefer
`../bench`:

```shell
../bench build
../bench run --implementation current,legacy,swift unary-latency
```

## Adding a benchmark

Implement `Benchmark` directly, or use `CallBenchmark` when the measurement is
a repeated operation. Register the new definition alongside `unaryBenchmarks()`
in `BenchmarkCLI`. The shared runner handles warmup, concurrent workers,
latency sampling, throughput calculation, and human or CSV output.

The protobuf file contains the wire-compatible subset of the official
`grpc.testing.BenchmarkService` needed by registered Swift benchmarks. Extend
it when a new benchmark requires another RPC or message field.
