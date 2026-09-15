# kotlinx-rpc gRPC benchmark client

This module contains command-line benchmarks for the current kotlinx-rpc iOS
gRPC client. Benchmarks use the official gRPC benchmark service in `../protos`
and the standalone C++ server in `../cpp-server`.

The benchmark harness and tests live in `../shared-kotlin-client`. They are
compiled independently by this module and `../legacy-ios-client`; only the
small generated-service adapters differ between implementations. Human and CSV
results identify this client as the `current` implementation.

Build the release binary if necessary and run it with the helper script:

```shell
./run.sh ios list
./run.sh ios run unary-latency
./run.sh ios run unary-throughput \
    --request-bytes 4096 --response-bytes 4096 --concurrency 32 --format csv
```

The same helper runs the native macOS and JVM clients:

```shell
./run.sh macos run unary-latency
./run.sh jvm run unary-throughput --format csv
```

The helper invokes the platform's incremental release-link or JVM distribution
task before every run, so binaries are rebuilt only when needed. Gradle output
goes to stderr, leaving stdout clean for CSV results. The iOS option uses a
currently booted simulator, or boots the first available iPhone and waits for
it to become ready. Set `KXRPC_BENCHMARK_SIMULATOR` to a simulator UDID to
select another one; the helper boots that device when necessary.

To build the optimized Apple Silicon iOS Simulator binary without launching it:

```shell
./gradlew :tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableIosSimulatorArm64
```

For local development, the same benchmark CLI can also be built and executed as
a macOS binary:

```shell
./gradlew :tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableMacosArm64
./build/bin/macosArm64/releaseExecutable/kotlinx-rpc-grpc-benchmark-client.kexe list
```

Use `run all` to execute every registered benchmark. Command-line values
override a benchmark's defaults, which makes payload and concurrency sweeps easy
to script.

## Adding a benchmark

Add shared benchmark definitions under `../shared-kotlin-client/src/commonMain`.
For ordinary request/response measurements, create a `CallBenchmark`: the
shared runner handles warmup, concurrent workers, latency samples, aggregate
throughput, and output formatting. Keep only generated-service API differences
in `CurrentBenchmarkBackend` and `LegacyBenchmarkBackend`.
