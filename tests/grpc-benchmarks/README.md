# gRPC client benchmarks

> [!IMPORTANT]
> This benchmark suite was written by AI and is only used to research potential performance opportunities. It does not represent the performance of real-world application.

The suite compares three gRPC clients against the same C++ server on localhost.
The default platform is an arm64 iOS Simulator.

## Setup and use

Requirements: macOS with Xcode and an iOS Simulator runtime, a JDK, Python 3.10 or newer, `curl`, `patch`, `tar`,
and Bazelisk or Bazel 8.7.0. Run the commands in this directory.

```shell
python3 -m venv .venv
source .venv/bin/activate
pip install -r analysis/requirements.txt
./cpp-server/run.sh                 # builds the server and starts it on port 50051
```

In a second terminal, with the same environment:

```shell
source .venv/bin/activate
./bench build
./bench doctor
./bench run unary-latency
./bench compare output/RUN:current output/RUN:swift
./bench analyze output/RUN --format html --output report.html
```

- `./bench help` lists the commands and options.
- `run` makes three trials for each implementation, in a random order with a recorded seed.
  `--implementation current`, `--case NAME`, and `--repetitions N` select a part. `run all` runs all cases.
- Arguments after `--` override the workload parameters of the client, for example `--operations`, `--concurrency`,
  and `--request-bytes`.
- `analyze` and `compare` read saved results. Both take `--format table`, `csv`, or `html`, and `--output FILE`.
  An HTML report has Plotly inside and works offline.

## Clients

| Client | Directory | Stack | Platforms |
|---|---|---|---|
| `current` | `kotlinx-rpc-client/` | The sources of this repository with the Swift bridge | iOS Simulator, macOS, JVM |
| `legacy` | `legacy-ios-client/` | A separate Gradle build with Kotlin 2.3.0 and the published `0.11.0-grpc-189` client and compiler plugin. The Gradle wrapper of the repository runs it with `--project-dir`. | iOS Simulator |
| `swift` | `swift-client/` | grpc-swift-2 | iOS Simulator |

- Select macOS or the JVM with `--platform macos` or `--platform jvm`, and `--implementation current`.
- Both Kotlin clients compile `shared-kotlin-client/src/commonMain` against their own dependencies.
  Add a shared workload there, and the same workload for Swift in `swift-client/Sources/SwiftGrpcBenchmarkClient`.
  The shared Kotlin sources must compile with the legacy compiler. `./bench doctor` checks that the workloads are equal.
- For one run of one client, use `kotlinx-rpc-client/run.sh ios COMMAND` (also `macos` and `jvm`),
  `legacy-ios-client/run.sh COMMAND`, or `swift-client/run.sh COMMAND`. Each helper builds before it runs.
- Set `KXRPC_BENCHMARK_SIMULATOR` to a simulator UDID to select a device. Without it, the helpers use a booted
  simulator, or boot the first available iPhone.

## Configuration

`bench.config` has shell `KEY=VALUE` assignments. Command-line options override it.

| Key | Default | Effect |
|---|---|---|
| `KXRPC_BENCH_PORT` | `50051` | Server port on localhost (`--port`) |
| `KXRPC_BENCH_PLATFORM` | `ios` | `ios`, `macos`, or `jvm` |
| `KXRPC_BENCH_IMPLEMENTATIONS` | `current,legacy,swift` | Clients to run |
| `KXRPC_BENCH_OUTPUT_DIR` | `output/` | Result directory |
| `KXRPC_BENCH_REPETITIONS` | `3` | Trials for each client and case |
| `KXRPC_BENCH_ORDER` | `random` | `random` or `fixed` trial order |
| `KXRPC_BENCH_SEED` | The run time | Seed of the random order |
| `KXRPC_BENCH_TRIAL_TIMEOUT` | `900` | Timeout of a trial, in seconds |
| `KXRPC_BENCH_GAP_SECONDS` | `0.5` | Pause between trials |
| `KXRPC_BENCH_INJECT_DELAY_MS` | `0` | Delay added to each measured operation |

The C++ server uses gRPC revision `b8f09d9168d856020d236bd32f39195f7b5aa2cf`, the revision of the vendored protos.
It is asynchronous, has no TLS, and has a local patch that clears reused response payloads for zero-byte responses.
`cpp-server/run.sh` passes extra arguments to the server. The first build downloads dependencies and can take several minutes.

| Server variable | Default |
|---|---|
| `GRPC_BENCHMARK_PORT` | `50051` |
| `GRPC_BENCHMARK_SERVER_TYPE` | `async`. Also `callback` and `sync`. |
| `GRPC_BENCHMARK_MAX_MESSAGE_BYTES` | 32 MiB |
| `GRPC_BENCHMARK_BUILD_DIR` | `cpp-server/.build` |

## Workloads

| Workload | Cases |
|---|---|
| `unary-latency` | Sequential empty calls |
| `unary-sparse-bursts` | Single calls after 1, 10, and 100 ms pauses. Bursts of 8 and 32 calls after 10 ms pauses. |
| `unary-throughput` | 1 KiB payload, 16 workers |
| `unary-payload-sweep` | 64 B to 4 MiB minus 1 KiB, symmetric and asymmetric payloads |
| `unary-concurrency-sweep` | 1 to 128 workers, empty and 1 KiB payloads |
| `server-streaming-throughput` | 1 KiB, 64 KiB, and 1 MiB responses |
| `client-streaming-throughput` | 1 KiB, 64 KiB, and 1 MiB requests |
| `bidi-ping-pong` | Sequential empty and 1 KiB round trips |
| `bidi-full-duplex` | Balanced and asymmetric echo streams |
| `stream-message-overhead` | 64 MiB total with different message sizes |
| `stream-concurrency-sweep` | 1 to 16 streams |

All workloads:

- Each trial starts a new client process and channel, checks readiness, warms up, and measures one case.
  The measurement excludes the readiness check and the warmup.
- A failed trial records its status and its partial samples.
- The load is closed-loop.
- Streaming `operations` counts the messages for each direction, over all streams.

`unary-sparse-bursts`:

- It uses 1 KiB requests and responses on a warm channel.
- Each group starts after the previous group completes and the idle pause ends.
  `concurrency` is the group size, and `operations` counts single calls.
- The warmup has the same pauses and groups.
- The latency of a call excludes the pauses. The elapsed time and the rates include them.
- The JSON records have `parameters.pause_ms`.

Limits:

- The legacy client has a fixed receive limit of 4 MiB, so the payload sweep leaves 1 KiB for serialization overhead.
- Server streaming cancels unbounded RPCs.
- The concurrency sweep stops at 16 streams, because SwiftNIO allows a limited number of reset streams.
- Full duplex uses the finite `StreamingCall` echo.

## Measurements

| Measurement | Interval or unit |
|---|---|
| `call_round_trip` | Unary request to completed response |
| `message_interarrival` | One response receive to the next |
| `request_write_suspension` | Start of a request write to its return |
| `ping_pong_round_trip` | Streaming request to completed response |
| `time_to_first_response_ns` | Stream start to first response |
| `stream_duration_ns` | Client-streaming start to final response |
| `final_response_latency_ns` | Last request write to final response |
| CPU counters | CPU time of the process in the measured phase, all threads |
| `max_rss_bytes` | Peak resident memory of the process, with the warmup |
| Rates | Completed calls, messages, or application payload MiB for each measured second |

## Results and comparison

`output/RUN/` has `manifest.json`, one JSON record for each trial, and the stderr logs.

- The manifest records the configuration, the order, the seed, the revision, the host, the simulator, the power
  and thermal state, and the status of each trial.
- The raw samples are in the trial records.
- A trial that the timeout stops has a manifest entry and a log, also when it has no record.

Statistics:

- The trial is the statistical unit.
- A comparison matches the workload, the parameters, the platform, the target, and the injected delay.
- A comparison reports the ratio of the medians, a seeded 95% bootstrap interval over independent trials, and
  a two-sided Mann-Whitney U p-value when each side has eight trials or more.
- Fewer than three trials give no interval. Fewer than five give low confidence.
- A difference requires an interval that excludes 1.
- A tail percentile requires enough samples in each trial: 100 for p99.

Checks:

- Measurement noise: compare two runs of the same implementation.
- Sensitivity: run with `--inject-delay-ms N`, and compare with a run without delay.
  Give `compare` the `--allow-injected-delay-mismatch` option.

The results compare complete client stacks, with their runtimes, serialization, and compiler versions.
Results on the simulator loopback don't isolate the bridge overhead and don't predict the performance of a
physical device. CPU time doesn't measure energy.
