# gRPC client benchmarks

Compare three complete gRPC client stacks against the same pinned C++ server.
The default platform is an arm64 iOS Simulator on localhost.

## Setup and usage

Requirements: macOS with Xcode and an iOS Simulator runtime, JDK, Python 3.10+,
`curl`, `patch`, `tar`, and Bazelisk or Bazel 8.7.0.
Run these commands from this directory:

```shell
python3 -m venv .venv
source .venv/bin/activate
pip install -r analysis/requirements.txt
./cpp-server/run.sh                 # Build and start the server on port 50051
```

In another terminal, activate the same environment and run:

```shell
source .venv/bin/activate
./bench build
./bench doctor
./bench run unary-latency
./bench compare output/RUN:current output/RUN:swift
./bench analyze output/RUN --format html --output report.html
```

`./bench help` lists the commands and options. `./bench run` defaults to three
trials per implementation, in a shuffled order with a recorded seed.
Use `--implementation current`, `--case NAME`, or `--repetitions N` to narrow a run.
Use `run all` to run every case. Arguments after `--` override client workload
parameters, such as `--operations`, `--concurrency`, and `--request-bytes`.

`analyze` and `compare` read saved results offline. Both support `--format table`,
`csv`, or `html`, and `--output FILE`. Tables use tabulate, statistics use
NumPy/SciPy, and charts use Plotly. HTML reports embed Plotly for offline use.
CSV uses Python's standard library.

## Clients

### Current (`kotlinx-rpc-client/`)

Uses repository sources and the Swift bridge. Supports iOS Simulator, macOS,
and JVM. Select macOS or JVM with `--platform macos` or `--platform jvm` and
`--implementation current`. The default platform is iOS.

### Legacy (`legacy-ios-client/`)

A separate Gradle build with Kotlin 2.3.0 and published
`0.11.0-grpc-189` client and compiler-plugin artifacts. Supports iOS Simulator.
The repository wrapper runs this build with `--project-dir`.

### Swift (`swift-client/`)

Uses grpc-swift-2 directly. Supports iOS Simulator.

Both Kotlin clients compile `shared-kotlin-client/src/commonMain` against their
own dependencies. Add shared workloads there and matching Swift workloads in
`swift-client/Sources/SwiftGrpcBenchmarkClient`. Keep shared Kotlin sources
compatible with the legacy compiler. `./bench doctor` checks workload equivalence.

For a single client invocation, use `kotlinx-rpc-client/run.sh ios COMMAND`,
`legacy-ios-client/run.sh COMMAND`, or `swift-client/run.sh COMMAND`.
The current client also accepts `macos` and `jvm`. Each helper builds before it runs.
Set `KXRPC_BENCHMARK_SIMULATOR` to a simulator UDID to select a device.
Otherwise the helpers use a booted simulator or boot the first available iPhone.

## Configuration

`bench.config` accepts shell `KEY=VALUE` assignments. Command-line options override it.

| Key | Default | Effect |
|---|---|---|
| `KXRPC_BENCH_PORT` | `50051` | Localhost server port (`--port`) |
| `KXRPC_BENCH_PLATFORM` | `ios` | `ios`, `macos`, or `jvm` |
| `KXRPC_BENCH_IMPLEMENTATIONS` | `current,legacy,swift` | Clients to run |
| `KXRPC_BENCH_OUTPUT_DIR` | `output/` | Result directory |
| `KXRPC_BENCH_REPETITIONS` | `3` | Trials per client and case |
| `KXRPC_BENCH_ORDER` | `random` | `random` or `fixed` trial order |
| `KXRPC_BENCH_SEED` | Run time | Shuffle seed |
| `KXRPC_BENCH_TRIAL_TIMEOUT` | `900` | Trial watchdog timeout in seconds |
| `KXRPC_BENCH_GAP_SECONDS` | `0.5` | Pause between trials |
| `KXRPC_BENCH_INJECT_DELAY_MS` | `0` | Added delay per measured operation |

The C++ server uses gRPC revision `b8f09d9168d856020d236bd32f39195f7b5aa2cf`,
matching the vendored protos. Its asynchronous server runs without TLS and has a
local patch that clears reused response payloads for zero-byte responses.

| Server variable | Default |
|---|---|
| `GRPC_BENCHMARK_PORT` | `50051` |
| `GRPC_BENCHMARK_SERVER_TYPE` | `async` (`callback` and `sync` also supported) |
| `GRPC_BENCHMARK_MAX_MESSAGE_BYTES` | 32 MiB |
| `GRPC_BENCHMARK_BUILD_DIR` | `cpp-server/.build` |

`cpp-server/run.sh` forwards extra arguments to the server. The first build downloads dependencies and can take several minutes.

## Workloads and measurements

| Workload | Cases |
|---|---|
| `unary-latency` | Sequential empty calls |
| `unary-throughput` | 1 KiB payload, 16 workers |
| `unary-payload-sweep` | 64 B through 4 MiB minus 1 KiB, symmetric and asymmetric payloads |
| `unary-concurrency-sweep` | 1 through 128 workers, empty and 1 KiB payloads |
| `server-streaming-throughput` | 1 KiB, 64 KiB, and 1 MiB responses |
| `client-streaming-throughput` | 1 KiB, 64 KiB, and 1 MiB requests |
| `bidi-ping-pong` | Sequential empty and 1 KiB round trips |
| `bidi-full-duplex` | Balanced and asymmetric echo streams |
| `stream-message-overhead` | 64 MiB total with different message sizes |
| `stream-concurrency-sweep` | 1 through 16 streams |

The legacy client has a fixed 4 MiB receive limit. The payload sweep leaves
1 KiB for serialization overhead. Server streaming cancels unbounded RPCs.
The concurrency sweep stops at 16 streams to stay within SwiftNIO's retained
reset-stream allowance. Full duplex uses the finite `StreamingCall` echo.

Each trial starts a fresh client process and channel, checks readiness, warms
up, and measures one case. Readiness and warmup aren't measured. Failed trials
retain their status and partial samples. All workloads use closed-loop load.
Streaming `operations` counts messages per direction across all streams.

| Measurement | Interval or unit |
|---|---|
| `call_round_trip` | Unary request to completed response |
| `message_interarrival` | Consecutive response receives |
| `request_write_suspension` | Request write start to write return |
| `ping_pong_round_trip` | Streaming request to completed response |
| `time_to_first_response_ns` | Stream start to first response |
| `stream_duration_ns` | Client-streaming start to final response |
| `final_response_latency_ns` | Last request write to final response |
| CPU counters | Measured-phase process CPU time across all threads |
| `max_rss_bytes` | Process peak resident memory, warmup included |
| Rates | Completed calls, messages, or application payload MiB per measured second |

## Results and comparison

`output/RUN/` holds `manifest.json`, trial JSON records, and stderr logs.
The manifest records configuration, order, seed, revision, host, simulator,
power and thermal state, and trial status. Raw samples stay in each trial record.
Watchdog failures retain a manifest entry and log even when no record exists.

The trial is the statistical unit. Comparisons match workload, parameters,
platform, target, and injected delay. They report median ratios, a seeded 95%
bootstrap interval over independent trials, and a two-sided Mann-Whitney U
p-value with at least eight trials per side. Fewer than three trials gives no
interval. Fewer than five gives low confidence. A difference requires an interval
that excludes 1. Tail percentiles require enough samples per trial (100 for p99).

To check measurement noise, compare two runs of the same implementation.
To check sensitivity, add `--inject-delay-ms N` and compare with an undelayed run
with `--allow-injected-delay-mismatch` on `compare`.

Results compare complete client stacks, including runtimes, serialization, and
compiler versions. Simulator loopback results don't isolate bridge overhead or
predict physical-device performance. CPU time doesn't measure energy.
