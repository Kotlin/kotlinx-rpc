# gRPC client benchmark roadmap

This directory compares the iOS gRPC client implementations on the same device,
against the same server, with the same payloads and workload. Every benchmark in
this roadmap must be implemented in both:

- [`shared-kotlin-client`](shared-kotlin-client), used by the current and legacy
  kotlinx-rpc clients;
- [`swift-client`](swift-client), the direct grpc-swift-2 baseline.

The most useful comparison is the current Kotlin client result minus the direct
Swift result. It estimates the cost added by Kotlin/Native, the Objective-C
bridge, and the surrounding coroutine/Swift concurrency integration.

## Status convention

`[x]` means that the scenario is registered, runnable, and reports comparable
results in both clients. A benchmark implemented in only one client stays
unchecked. A generic CLI override does not make a multi-point sweep complete:
the required points must be reproducibly enumerated and identified in the
output.

Keep the two implementations behaviorally equivalent. They must use matching
warmups, measured operations, concurrency, payload contents, validation, timing
boundaries, and connection lifecycle.

## Benchmark checklist

### Unary calls

- [x] `unary-latency` — Empty request and response, sequential calls over one
  warm channel.
- [x] `unary-throughput` — 1 KiB request and response, concurrent calls over
  one warm channel.
- [x] `unary-payload-sweep` — Independent request/response sizes of 64 B,
  1 KiB, 64 KiB, 1 MiB, and 4 MiB minus 1 KiB; include upload-heavy,
  download-heavy, and symmetric cases.
- [x] `unary-concurrency-sweep` — Concurrency 1, 2, 4, 8, 16, 32, 64, and 128
  on one warm channel, using empty and 1 KiB payloads.
- [ ] `unary-connection-startup` — Cold channel creation and first call, first
  call on an unconnected channel, and a warm call on an established connection.

The payload sweep stops just below 4 MiB because the published legacy client
has a non-configurable 4 MiB receive limit. The final point uses a
4 MiB-minus-1 KiB body so the serialized protobuf message, including fields
beyond the body, remains below that limit. Larger-message testing should be
added when configurable client message limits are available in every
implementation.

Sweeps emit one result per named case. Use `--case NAME` to run a single point;
`list` enumerates every case and its parameters. Payload sweep operation counts
decrease as payload sizes grow, while concurrency sweep operation counts
increase where necessary to keep every worker active long enough for a stable
measurement.

### Streaming calls

- [ ] `server-streaming-latency` — Time to first message and completion for one
  request followed by many small responses.
- [ ] `server-streaming-throughput` — Download goodput for many small messages
  and for fewer large messages.
- [ ] `client-streaming-throughput` — Upload goodput for many small messages
  and for fewer large messages; also record latency from the final request to
  the response.
- [ ] `bidi-ping-pong` — Strict send-one/receive-one latency, plus a small fixed
  window of messages in flight.
- [ ] `bidi-full-duplex` — Independent concurrent upload and download, with
  balanced and asymmetric message sizes.
- [ ] `stream-message-overhead` — Send the same 64 MiB total as
  65,536 × 1 KiB, 1,024 × 64 KiB, 64 × 1 MiB, and 4 × 16 MiB.
- [ ] `stream-concurrency-sweep` — 1, 2, 4, 8, 16, 32, and 64 concurrent
  streams on one channel.

The fixed-total-byte benchmark is required because it separates per-message
interop and scheduling overhead from bulk byte-copying cost.

### Backpressure and mixed load

- [ ] `server-streaming-slow-consumer` — Compare an immediately available
  response consumer with paced and bursty consumers; record throughput and peak
  memory.
- [ ] `client-streaming-slow-producer` — Compare an immediately available
  request producer with paced and bursty producers.
- [ ] `bidi-asymmetric-rates` — Run independent producer and consumer rates,
  including a slow request producer and a slow response consumer.
- [ ] `mixed-unary-download` — Measure tiny unary latency while a large
  server-streaming download is active.
- [ ] `mixed-unary-upload` — Measure tiny unary latency while a large
  client-streaming upload is active.
- [ ] `mixed-ping-pong-bidi` — Measure ping-pong tail latency alongside a bulk
  bidirectional stream.

These cases cover the backpressure and HTTP/2 fairness questions that are likely
to affect the backend. Buffer-capacity and prefetch experiments belong as
variants of these benchmarks rather than as separate benchmark names.

### Compression

- [ ] `unary-compression` — Identity versus gzip for a small payload,
  compressible 1 MiB payload, and incompressible 1 MiB payload; vary request and
  response compression independently.
- [ ] `streaming-compression` — Identity versus gzip for compressible and
  incompressible streaming payloads.
- [ ] `compression-concurrency` — Increase concurrent compressed calls until
  CPU saturation becomes visible.

Report application goodput separately from wire throughput for compression
benchmarks.

### Deadlines, cancellation, and recovery

- [ ] `unary-deadline` — Already-expired deadline and deadline while waiting
  for a response.
- [ ] `streaming-deadline` — Deadline during request streaming and during
  response streaming.
- [ ] `unary-cancellation` — Cancellation before start and while a unary
  response is outstanding.
- [ ] `streaming-cancellation` — Cancel with an outstanding request or response
  operation, and stop response consumption early.
- [ ] `cancellation-storm` — Concurrent cancellation of 64 or more active
  calls.
- [ ] `connection-loss-recovery` — Interrupt an active unary or streaming
  workload, then record failure and recovery latency.

Each failure-path benchmark must record both user-visible completion latency and
the time required to release call resources.

### Endurance and lifecycle

- [ ] `channel-lifecycle` — Repeated create, first call, and graceful shutdown
  cycles.
- [ ] `call-cancel-lifecycle` — Repeated call creation and cancellation,
  returning to idle between batches.
- [ ] `unary-endurance` — Sustained small unary workload long enough to expose
  latency drift and leaks.
- [ ] `streaming-endurance` — Sustained bidirectional stream with millions of
  messages.
- [ ] `mixed-endurance` — Long-running mixture of unary and streaming traffic
  with periodic cancellation.

Endurance runs must sample memory and concurrency resources during the workload
and after returning to idle. A fixed operation count is preferable to a
multi-hour default; longer soak durations can be selected by the caller.

## Execution matrix

Run every completed end-to-end benchmark against the same pinned C++ server in
[`cpp-server`](cpp-server). At minimum, preserve these comparison baselines:

- [x] Current kotlinx-rpc client through the Swift bridge.
- [x] Published legacy kotlinx-rpc iOS client.
- [x] Direct grpc-swift-2 client.
- [ ] Physical iOS device support for the current Kotlin and Swift clients.
- [ ] TLS execution mode in addition to the current plaintext mode.
- [ ] Repeatable network profiles for added latency, bandwidth limits, jitter,
  and packet loss.

Simulator loopback is the default optimization environment. Physical-device and
network-profile runs validate that a local improvement remains relevant under
realistic conditions; they do not need to be part of every development run.

Run the same benchmark across the current kotlinx-rpc, published legacy, and
direct Swift clients with the combined runner (the C++ benchmark server must
already be running):

```shell
./run-all.sh unary-latency
./run-all.sh all
./run-all.sh --output-dir ./results unary-payload-sweep --case symmetric-1m
```

The runner uses the same iOS Simulator for all clients and writes their results
to one timestamped CSV file under `output/` by default. Use `--output-dir` to
choose another directory. Multi-case runs report a progress bar to stderr with
the current platform, implementation, benchmark, and case; CSV output remains
machine-readable.

Build output uses one transient terminal line and is cleared after a successful
build. If a build fails, its captured output is printed for diagnosis. Set
`KXRPC_BENCHMARK_VERBOSE_BUILD=1` to keep the complete build output visible.
When stderr is redirected or is otherwise not a terminal, build output remains
plain so automation logs continue to show activity.

## Result requirements

The clients must emit compatible machine-readable fields so their results can
be paired without interpretation. The following measurements are required as
the corresponding benchmarks are added:

- [x] Total elapsed time.
- [x] Calls per second.
- [x] Application bytes per second.
- [x] Latency minimum, mean, p50, p90, p95, p99, p99.9, and maximum.
- [ ] Messages per second for streaming benchmarks.
- [ ] Time to headers, first message, final response, and terminal status where
  applicable.
- [ ] Wire bytes per second for compression and network benchmarks.
- [ ] CPU time and utilization.
- [ ] Peak and steady-state resident memory.
- [ ] Kotlin and Swift allocation counts where measurable.
- [ ] Kotlin/Native garbage-collection activity and pauses.
- [ ] Active tasks, coroutines, threads, sockets, and file descriptors for
  lifecycle and endurance benchmarks.
- [ ] Resource state after the client returns to idle.

## Deliberate omissions

This roadmap does not include Kotlin-to-Swift bridge-only microbenchmarks,
protobuf payload-shape microbenchmarks, grpc-java comparisons, or every possible
network and channel option. Bridge-only tests cannot be implemented equivalently
in `swift-client`; payload-shape costs are primarily protobuf serialization
questions rather than measurements of the gRPC backend. Add either category in
a separate suite if an end-to-end result identifies it as the likely bottleneck.

Similarly, pathological server behaviors such as responding before consuming a
request stream or permanently stopping request reads require a purpose-built
server. They should be added only when diagnosing a concrete correctness or
resource-management problem.
