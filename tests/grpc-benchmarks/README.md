# gRPC client benchmarks

This directory compares three gRPC client implementations on the same iOS
Simulator, against the same pinned C++ server, with the same payloads:

- [`kotlinx-rpc-client`](kotlinx-rpc-client): the current kotlinx-rpc gRPC client
  from this repository, through the Swift bridge.
- [`legacy-ios-client`](legacy-ios-client): the published legacy kotlinx-rpc iOS
  client, built from `0.11.0-grpc-189` artifacts.
- [`swift-client`](swift-client): the direct grpc-swift-2 client.

The goal is a comparison that survives scrutiny: repeated independent trials,
explicit trial order, per-trial raw measurements, and an analysis that states
its uncertainty instead of a bare ranking.

## Quick start

```shell
./cpp-server/run.sh                 # Start the pinned benchmark server on :50051
./bench doctor                       # Check tools, server, binaries, and workload equivalence
./bench build                        # Build the release binaries of all clients
./bench run unary-latency            # 3 trials per client, seeded random order
./bench compare output/RUN:current output/RUN:swift
./bench analyze output/RUN --format html --output report.html
```

`./bench help` lists every command. Progress goes to stderr; paths and
machine-readable results go to stdout.

## Configuration

`./bench` reads `bench.config` next to the script before it parses the command
line. The file holds `KEY=VALUE` lines; the command line overrides it.

| Key | Effect |
|---|---|
| `KXRPC_BENCH_TARGET` | Benchmark server, `HOST:PORT` |
| `KXRPC_BENCH_PLATFORM` | `ios`, `macos`, or `jvm` (only `current` builds the last two) |
| `KXRPC_BENCH_IMPLEMENTATIONS` | Comma-separated client list |
| `KXRPC_BENCH_OUTPUT_DIR` | Parent directory of the run directories |
| `KXRPC_BENCH_REPETITIONS` | Trials per implementation and case |
| `KXRPC_BENCH_ORDER` | `random` or `fixed` |
| `KXRPC_BENCH_SEED` | Seed of the trial order; the default is the run time |
| `KXRPC_BENCH_TRIAL_TIMEOUT` | Seconds before the watchdog stops a trial |
| `KXRPC_BENCH_GAP_SECONDS` | Pause between trials |
| `KXRPC_BENCH_INJECT_DELAY_MS` | Known delay per operation, for sensitivity checks |

## What one measurement is

A **trial** is one client process that runs exactly one benchmark case once:
its own channel, its own readiness probe, its own warmup, one measured phase,
and one JSON record with the raw latency samples. The process exits after the
trial.

A **run** is a set of trials produced by one `./bench run` invocation. The run
directory holds the trial records, a manifest, and one stderr log per trial.

Every trial passes through the same phases, in this order:

1. Channel creation and readiness probe. The client repeats one unary call
   until the server answers, and records the readiness time. This phase is
   never part of a measurement.
2. Warmup. The same operations as the measured phase, without recording. Warms
   the connection, the serializer, and the allocator.
3. Measured phase. The elapsed time covers only this phase. Latency samples
   are recorded per operation.

A trial stops at the first failed operation. Its record keeps the `failed`
status, the error, and the samples that were recorded before the failure.

## Trial order

`./bench run` interleaves all (implementation, case, repetition) trials in one
list, then shuffles that list with a recorded seed (`--order random`, the
default) or keeps the definition order (`--order fixed`). A fixed order, such
as always running `current`, then `legacy`, then `swift`, lets thermal drift
and server state align with one implementation and distort its results. The
manifest records the seed, the order mode, and the status of every trial.

## Workload matrix

Every benchmark must be registered in both Kotlin clients and the Swift client
with identical parameters. `./bench doctor` runs `list --format json` on every
client and reports any drift, so the implementations can't silently diverge.

A streaming case sets `operations` to the number of messages per direction,
split across `concurrency` parallel streams on one channel. A unary case sets
`operations` to the number of calls.

### Unary calls

- [x] `unary-latency` — Empty request and response, sequential calls over one
  warm channel. One case.
- [x] `unary-throughput` — 1 KiB request and response, 16 concurrent workers
  over one warm channel. One case.
- [x] `unary-payload-sweep` — Request and response sizes of 64 B, 1 KiB,
  64 KiB, 1 MiB, and 4 MiB minus 1 KiB; symmetric, upload-heavy, and
  download-heavy.
- [x] `unary-concurrency-sweep` — Concurrency 1, 2, 4, 8, 16, 32, 64, and 128
  with empty and 1 KiB payloads.
- [ ] `unary-connection-startup` — Cold channel creation and first call, first
  call on an unconnected channel, and a warm call on an established connection.

The payload sweep stops just below 4 MiB because the published legacy client
has a non-configurable 4 MiB receive limit. The final point uses a
4 MiB-minus-1 KiB body so the serialized message stays below that limit.
Larger-message testing should return when every implementation can configure
its message limits.

### Streaming calls

- [ ] `server-streaming-latency` — Time to first message and completion for one
  request followed by many small responses.
- [x] `server-streaming-throughput` — Download goodput for 1 KiB, 64 KiB, and
  1 MiB messages.
- [x] `client-streaming-throughput` — Upload goodput for 1 KiB, 64 KiB, and
  1 MiB messages. Records the stream duration and the final-response latency.
- [x] `bidi-ping-pong` — Strict send-one/receive-one round trips, empty and
  1 KiB payloads.
- [x] `bidi-full-duplex` — Echo stream with balanced and asymmetric message
  sizes.
- [x] `stream-message-overhead` — The same 64 MiB total uploaded as
  65,536 × 1 KiB, 1,024 × 64 KiB, 64 × 1 MiB, and sixteen
  (4 MiB - 1 KiB) messages plus one 16 KiB remainder. The byte total stays
  fixed so the comparison isolates per-message overhead from bulk byte cost.
- [x] `stream-concurrency-sweep` — 1, 2, 4, 8, and 16 concurrent streams on one
  channel.

`StreamingFromServer` is unbounded, so completing each measured stream cancels
its RPC. SwiftNIO retains a bounded number of recently reset HTTP/2 streams
and can close the connection when late frames arrive for an evicted stream.
The sweep stops at 16 streams so the warmup doesn't exhaust that allowance.
The full-duplex workload uses the finite `StreamingCall` echo because the
pinned asynchronous server doesn't register `StreamingBothWays`.

### Not implemented yet

- Backpressure and mixed load: slow consumers and producers, asymmetric rates,
  and tiny-unary-under-load cases.
- Compression: identity versus gzip, per direction.
- Deadlines, cancellation, and recovery: deadline and cancellation paths,
  cancellation storms, and connection loss.
- Endurance and lifecycle: channel and call lifecycles, sustained workloads
  with resource sampling.
- Physical device runs, TLS, and network profiles (added latency, bandwidth
  limits, jitter, packet loss).

Simulator loopback is the default optimization environment. Runs on a physical
device or under a network profile validate that a local improvement survives
realistic conditions; they don't need to be part of every development run.

## Metric definitions

Every trial record states what its latency samples measure, so the analysis
never pools different intervals:

| `latency_metric` | Interval |
|---|---|
| `call_round_trip` | One unary request send to the completed receive of its response. |
| `message_interarrival` | The receive of one response message to the receive of the next. |
| `request_write_suspension` | The start of one request write to the return of the write call. |
| `ping_pong_round_trip` | One streaming request send to the completed receive of its response. |

Per-stream events are recorded separately and never mixed into the latency
samples:

| Event | Meaning |
|---|---|
| `time_to_first_response_ns` | Stream start to the first response message. |
| `stream_duration_ns` | Client-streaming stream start to the single response. |
| `final_response_latency_ns` | Last request write to the response of a client-streaming call. |

Process resource counters state their phase:

- `cpu_user_ns`, `cpu_system_ns`, and `cpu_total_ns`: the CPU time of the measured
  phase, summed over all threads of the client process, from resource snapshots at
  the phase boundaries. The JVM client reports only the combined total.
- `max_rss_bytes`: the process-lifetime peak resident size at the end of the trial,
  warmup included.

CPU time is a proxy for energy, not its measurement: clock frequency, core
placement, and memory power are outside it. The analysis reports CPU per
operation, CPU per MiB, and the CPU-to-wall ratio; the manifest records the host
power source and thermal state of every run.

Rates state their denominator:

- `operations_per_second`: completed RPCs divided by the measured seconds.
  Reported for unary trials only.
- `request_messages_per_second` and `response_messages_per_second`: messages
  per direction divided by the measured seconds. Reported for streaming
  trials.
- `application_request_mib_per_second` and `application_response_mib_per_second`:
  application payload bytes, not wire bytes. Framing, headers, and compression
  aren't part of these numbers.

All workloads are closed-loop: a fixed number of workers or streams issues the
next operation as soon as the previous one completes. The latency samples are
therefore operation latency under closed-loop load, not the latency an
externally paced client would see, and the analysis never presents them as
such.

## Result artifacts

A run directory contains:

```
output/run-20261104-211248-s1793823166/
  manifest.json                  Resolved configuration, order, seed, environment
  trials/
    0000-swift-unary-latency-default-rep1.json
    0000-swift-unary-latency-default-rep1.stderr.log
    ...
```

The trial record (schema version 2, identical across the three clients; the analysis also
reads version 1 records, which carry no resource counters):

```json
{
  "schema_version": 1,
  "implementation": "current",
  "implementation_version": "workspace",
  "platform": "ios-simulator-arm64",
  "benchmark": "unary-latency",
  "case": "default",
  "parameters": {"warmup_operations": 200, "operations": 10000, "concurrency": 1,
                 "request_bytes": 0, "response_bytes": 0},
  "latency_metric": "call_round_trip",
  "operation_unit": "rpc",
  "load_shape": "closed_loop",
  "target": "localhost:50051",
  "injected_delay_ms": 0,
  "status": "completed",
  "error": null,
  "started_at_epoch_ms": 1762268000000,
  "connection_ready_ns": 11242542,
  "elapsed_ns": 1200000000,
  "counts": {"operations": 10000, "request_messages": null, "response_messages": null,
             "application_request_bytes": 0, "application_response_bytes": 0},
  "cpu_user_ns": 1500000, "cpu_system_ns": 500000, "cpu_total_ns": 2000000,
  "max_rss_bytes": 67108864,
  "latency_samples_ns": [12000, 13500, "..."],
  "events": {"time_to_first_response_ns": [], "stream_duration_ns": [],
             "final_response_latency_ns": []}
}
```

The manifest records the seed, the trial order, every trial's status, the git
revision and dirty state, the host, the Xcode version, the thermal and power
state, the simulator, and the server configuration. `./bench analyze` works
offline from these artifacts and never starts a client or contacts the server.

Failed, timed-out, and crashed trials stay in the run directory with an
explicit status. A trial that the watchdog stops leaves a stderr log and a
manifest entry, but no record.

## Statistics

The experimental unit is the trial. The latency percentiles of one trial are
one observation, no matter how many samples the trial holds. Individual
operations are never treated as independent repetitions.

`./bench compare BASE CANDIDATE` pairs completed trials by benchmark, case,
platform, target, parameters, and injected delay. For each metric it reports:

- The median of each side and the ratio of the medians.
- A 95th percentile bootstrap interval of the ratio, resampled over trials.
- A two-sided Mann-Whitney U p-value when each side has at least eight trials.
- A verdict. The comparison reports a difference only when the bootstrap
  interval excludes 1. Otherwise the verdict is `inconclusive`, and the
  comparison never forces a ranking.
- The confidence level. Fewer than five trials on a side is reported as low
  confidence.

Tail percentiles are flagged when a trial has too few samples to support them:
p99 needs 100 samples per trial, and p99.9 would need 1,000.

Two validation workflows are built in:

- **A/A comparison**: run the same implementation twice
  (`./bench run --implementation current ...`), then
  `./bench compare RUN_A:current RUN_B:current`. Most verdicts must be
  inconclusive. A difference detected here measures the noise floor.
- **Positive control**: `./bench run --inject-delay-ms N` adds a known delay to
  every measured operation of every implementation, outside the production
  code paths, and the trial record reports it. Comparing a delayed run against
  an undelayed run must detect the delay.

## Method sources

The defaults follow the established benchmark harnesses:

- **Google Benchmark**: repetitions with mean, median, standard deviation, and
  coefficient of variation over runs; seeded random interleaving of
  repetitions to reduce system-state effects; a measured window of at least
  half a second; and `compare.py`, which runs a Mann-Whitney U test and needs
  at least nine repetitions for a meaningful result.
  <https://github.com/google/benchmark/blob/main/docs/user_guide.md>
- **JMH**: forked processes as the way to measure run-to-run variance, warmup
  iterations that are discarded, and the rule that no infrastructure is free
  of overhead.
  <https://github.com/openjdk/jmh/tree/master/jmh-samples>
- **ghz**: fixed-total or duration-based load with a constant concurrency of
  closed-loop workers, per-request timeouts, and summary output with
  percentiles.
  <https://github.com/bojand/ghz>
- **gRPC performance framework**: a warmup phase before the measured phase,
  histogram-based latency, and the p50/p90/p95/p99/p99.9 set.
  <https://github.com/grpc/grpc/blob/master/test/cpp/qps/driver.cc>

Established tools can't replace this harness: JMH and Google Benchmark can't
run Kotlin/Native or Swift clients, and ghz drives grpc clients of its own
rather than the implementations under comparison. The methodology is adopted,
not the tools.

## What the numbers mean

`current` minus `swift` isn't "the bridge overhead". The two clients differ
in runtime (Kotlin/Native versus Swift), serialization (kotlinx-protobuf
versus Swift Protobuf), concurrency (kotlinx.coroutines versus structured
concurrency), and API behavior. The comparison measures complete client
stacks on the same workload. Only a workload that isolates one layer can
attribute its cost, and the roadmap marks those cases separately.

The legacy client builds with Kotlin 2.3.0 and its own compiler plugin, so its
results compare the complete legacy and current stacks, not only their iOS
backends.

## Conclusions the setup supports

- Differences between complete client stacks on the same workload, with an
  uncertainty interval from repeated trials.
- Order-controlled and seeded runs, so the same experiment can repeat.
- Noise floors from A/A comparisons and sensitivity from injected delays.

The setup doesn't support, on this machine:

- Physical device or network-limited conclusions. The default environment is
  one simulator on one host over loopback.
- Server capacity conclusions. The pinned C++ server is a fixed reference,
  and its own processing time is inside every sample.
- Absolute energy or thermal claims. The manifest records the host thermal
  and power state at run time, nothing more.

## Directory map

| Directory | Purpose |
|---|---|
| `bench` | The one CLI: `list`, `doctor`, `build`, `run`, `analyze`, `compare` |
| `analysis/` | Offline analysis, statistics, and reports (Python, standard library only) |
| `analysis/tests/` | Unit tests: run `python3 -m unittest discover -s analysis/tests` |
| `cpp-server/` | The pinned standalone C++ benchmark server (Bazel build) |
| `kotlinx-rpc-client/` | The current kotlinx-rpc client, iOS/macOS/JVM |
| `legacy-ios-client/` | Standalone Gradle build against the published legacy artifacts |
| `shared-kotlin-client/` | Source-only harness shared by both Kotlin clients |
| `swift-client/` | The direct grpc-swift-2 baseline client |
| `scripts/` | Simulator helpers and the manifest writer |

Client-level details live in the README of each directory. The benchmark
harness and its tests live in `shared-kotlin-client`; each Kotlin client
compiles those sources against its own artifacts, so only the small
generated-service adapters differ.
