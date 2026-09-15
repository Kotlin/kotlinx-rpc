# Legacy iOS benchmark client

This is a standalone Gradle build for benchmarking the published legacy
kotlinx-rpc gRPC client on iOS. It intentionally remains outside the repository
root `settings.gradle.kts` so that the legacy and current implementations cannot
be substituted for one another or linked into the same native executable.

The benchmark harness comes from `../shared-kotlin-client` and is compiled in
this build against the published legacy artifacts. Only
`LegacyBenchmarkBackend` is implementation-specific; it adapts the legacy
generated service method names. Human and CSV results identify this client as
the `legacy` implementation.

The build uses:

- Kotlin `2.3.0`;
- `org.jetbrains.kotlinx.rpc.plugin:0.11.0-grpc-189`;
- `org.jetbrains.kotlinx:kotlinx-rpc-grpc-client:0.11.0-grpc-189`;
- the canonical protos from `../protos/src/commonMain/proto`.

Build and run the Apple Silicon simulator executable with the helper script:

```shell
./run.sh list
./run.sh run unary-latency
./run.sh run unary-throughput --format csv
```

The helper incrementally links the release executable, selects or boots an iOS
Simulator, and forwards all remaining arguments to the shared benchmark CLI.
Set `KXRPC_BENCHMARK_SIMULATOR` to a simulator UDID to select another device.

The legacy build uses Kotlin 2.3.0 while the repository currently uses a newer
Kotlin version. Results therefore compare the complete legacy and current
client stacks, not only their underlying iOS backends.
