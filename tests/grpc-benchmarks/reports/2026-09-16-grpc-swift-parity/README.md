# grpc-swift-2 iOS backend vs gRPC C-core: performance-gap analysis and parity roadmap

Static, source-level investigation of why the current iOS client (Kotlin/Native → Objective-C/Swift bridge →
grpc-swift-2 → SwiftNIO) is slower than the previous iOS client (Kotlin/Native → gRPC C-core), based on one supplied
benchmark run and the pinned sources. No code was executed on Apple platforms; nothing below is a runtime
measurement other than the supplied CSV.

Evidence labels used throughout:

- **Measured**: directly present in the supplied CSV (`benchmark-data.csv`, tables in `ratios.md`).
- **Observed**: directly established from source at the recorded revisions.
- **Strong inference**: several measurements and source facts point the same way.
- **Hypothesis**: plausible, requires profiling or an A/B experiment.
- **Unknown**: the available evidence cannot distinguish alternatives.

Files in this directory:

- `README.md` (this report)
- `benchmark-data.csv` — the CSV supplied with the task (156 rows, 3 implementations × 52 cases)
- `analyze.py` — read-only script that pairs rows by benchmark/case and prints ratio tables
- `ratios.md` — output of `analyze.py`; every ratio quoted below comes from it

---

## 1. Executive summary

**Where the gap appears (Measured).**

| Workload family | current vs legacy | current vs swift | swift vs legacy |
|---|---|---|---|
| Sequential unary (empty/64 B/1 KiB, c=1) | 3.3–3.9× slower (600 µs vs 153 µs mean) | 1.6–2.1× slower | 1.9–2.3× slower |
| Unary under concurrency (c≥16) | 3.5–11.7× lower throughput; collapses past c8 | 4–18× lower | swift ≥ legacy from c16 |
| Bidi ping-pong (1 msg in flight) | 3.0–3.1× slower per round trip | 1.3–1.5× slower | 2.0–2.3× slower |
| Large responses (64 KiB–4 MiB unary download, server-streaming 64 KiB/1 MiB, bidi download-heavy) | 1.7–3.4× slower | 1.8–3.2× slower | at parity or faster (0.7–1.4×) |
| Small-message download streaming (1 KiB server streaming, stream-concurrency c1) | ≈ parity (0.9–1.2×) at c1; degrades to 1.6× at c16 | 1.9–3.9× slower | swift 1.7× faster |
| Upload streaming (client streaming, stream-message-overhead, bidi upload-heavy/balanced-1k) | **current is faster** (0.6–0.9× of legacy time; up to 1.7× more msg/s at 1 MiB) | 1.2–2.5× slower | swift 1.5–4× faster |

**Where it does not appear.** Every upload-dominated streaming case: `current` beats `legacy` because the C-core
path sends strictly one message at a time (`NativeClientCall.ready` toggling, KRPC-192) while grpc-swift pipelines
writes. The regression is therefore not a uniform "tax"; it is concentrated in (a) per-call and per-round-trip fixed
latency, (b) the response (download) direction per byte, and (c) behaviour under many concurrent calls.

**Most likely dominant causes, by workload (confidence in brackets).**

1. **Build asymmetry: the Swift stack inside the Kotlin binary is very likely compiled without optimisation**
   [Strong inference]. KGP 2.4.0's SwiftPM import compiles the bridge, grpc-swift-2, grpc-swift-nio-transport and
   SwiftNIO with `xcodebuild build -scheme … ` and *no* `-configuration` argument (§5, F1). xcodebuild's default
   configuration for a scheme build is Debug (`-Onone`). The `swift` baseline is built `--configuration release`
   and the C-core archives with `--compilation_mode=opt`. Until this is equalised, the `current`-vs-`swift`
   ratios cannot be attributed to bridge design. It plausibly contributes to every family above, including the
   per-byte and concurrency anomalies.
2. **Per-RPC fixed overhead of the bridge's event/pull protocol** [Strong inference]. A unary call crosses the
   Kotlin↔Swift boundary ≈12 times, creates ≥5 Swift `Task`s (one per pulled event plus the call task), uses
   ≥5 Swift continuations and ≈17 cross-thread hand-offs versus ≈4 Kotlin dispatches for C-core (§4). When
   threads are idle between hops (unary, ping-pong) each hop pays a wake-up; under streaming the pipeline is hot
   and the same hops cost only ~8 µs/message, which is why small-message streaming is near parity.
3. **Flush coalescing in grpc-swift-nio-transport (100 µs timer, both `current` and `swift`)** [Strong inference].
   C-core has no delayed flush. The CSV shows the fingerprint: for the `swift` baseline a 64 KiB upload (which
   hits the 64 KiB immediate-flush threshold) is *faster* than a 1 KiB upload (218 µs vs 388 µs).
4. **Response-path per-byte excess of ≈2–4 µs/KiB that exists only in `current`** [Measured pattern, mechanism
   Hypothesis]. Copy counts are equal to C-core (one copy into a Kotlin `Buffer`), so the excess is attributed to
   the bridge retaining NIO `ByteBuffer`s until the Kotlin/Native GC runs (copy-on-write regrowth of the deframer
   buffer, fresh page allocation) and/or the unoptimised Swift stack.
5. **Concurrency collapse (throughput falls past c8; minimum latency grows with concurrency)** [Measured,
   mechanism Unknown]. Neither `legacy` nor `swift` show it. Leading hypotheses: Kotlin/Native GC work amplified by
   ObjC-associated objects whose release is deferred to the GC sweep and whose Swift `deinit` re-enters Kotlin on
   the GC thread; cross-pool wake-up amplification (Kotlin `Dispatchers.Default` pool + Swift cooperative pool + NIO
   event loop); and the unoptimised Swift stack.

**Bridge vs grpc-swift/NIO share (cautious).** Where `swift` and `current` can be compared like-for-like
(sequential unary/ping-pong), roughly half of the `current`–`legacy` gap is `swift`–`legacy` (grpc-swift/NIO
behaviour + configuration, including flush coalescing) and half is `current`–`swift` (bridge + Kotlin/Native +
possibly the debug build). For large downloads and for concurrency, essentially the whole gap is
`current`-specific. These are single-run point estimates, not a decomposition.

**Highest-priority parity changes (see §7).** (P0) verify and force an optimised Swift build; (P1) disable client
flush coalescing; (P2) replace the per-event `Task` + `AsyncThrowingChannel` pull with a direct single-slot event
hand-off; (P3) release the Swift call and its buffers deterministically when the Kotlin flow completes; (P4) drop the
NIO message buffer immediately after it is copied into Kotlin; (P5) replace the per-message coroutine launch in the
request source with a persistent pull loop.

---

## 2. Inputs, revisions, and limitations

### 2.1 Repository state (Observed)

| Item | Value |
|---|---|
| Branch | `ios-grpc-swift-async` (working tree clean at start; the clone was shallow and was deepened read-only with `git fetch`) |
| HEAD | `25b4330bb2d2c20114bd6301bcbb9437f6960ad8` "Add streaming benchmarks for Kotlin and Swift clients" |
| `origin/main` | `339cdc6b98c016eb607bf86a7a3cfa86c7aed9eb` (a local `main` ref did not exist; `origin/main` was fetched) |
| Merge base | `60cc5663a71f19ea974776921a884b7f0b74afff` (matches the task's stated `main`) |
| Feature-branch commits | 43 (`5e83975a` event-based `GrpcClientTransport`; `c26e6848` moves the callback bridge to `nonIosMain`, both present) |
| Legacy client artifact | `org.jetbrains.kotlinx:kotlinx-rpc-grpc-client:0.11.0-grpc-189` (`tests/grpc-benchmarks/legacy-ios-client/gradle/libs.versions.toml`) built with Kotlin **2.3.0** |
| Legacy source | tag commit `f2f0aad2` "0.11.0-grpc-189" is an ancestor of the merge base (10 commits before it). `git diff f2f0aad2 60cc5663 -- grpc/grpc-client/src grpc/grpc-core/src` touches only 5 lines of `NativeClientCall.kt`; `protobuf/` and `protoc-gen/` are unchanged from `f2f0aad2` to HEAD. The pre-split `main` source is therefore an authoritative stand-in for the legacy binary. |
| `nonIosNativeMain` vs `main:nativeMain` | `NativeClientCall.kt`, `ClientCall.native.kt`, `CompletionQueue.kt`, `utils.native.kt` are byte-identical; `NativeManagedChannel.kt`/`ManagedChannel.native.kt` differ only by the removed `ManagedChannelPlatform` indirection. |
| Current client toolchain | Kotlin **2.4.0** (`versions-root/libs.versions.toml`), kotlinx-coroutines 1.10.2, kotlinx-io 0.9.0 (legacy: same coroutines/io versions) |

### 2.2 Dependency pins (Observed from `grpc/grpc-swift/Package.resolved` and `tests/grpc-benchmarks/swift-client/Package.resolved`)

| Package | Version | Revision | Used by |
|---|---|---|---|
| grpc-swift-2 | 2.4.3 | `ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7` | bridge + swift baseline |
| grpc-swift-nio-transport | 2.9.2 | `eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6` | bridge + swift baseline |
| swift-async-algorithms | 1.1.5 | `3da39bbc4e687d4192af7c9cf4eab805745a0b9c` | bridge only (`AsyncThrowingChannel`) |
| swift-nio | 2.102.0 | `a931f2c1de8dd49381ce3bf2e279d033f68d8865` | both |
| swift-nio-http2 | 1.46.0 | `0f3e54e29c944c2e835ad52159da7d9e1c94ac69` | both |
| swift-nio-transport-services | 1.28.0 | `67787bb645a5e67d2edcdfbe48a216cc549222d5` | both (`HTTP2ClientTransport.TransportServices`) |
| grpc-swift-protobuf / swift-protobuf | 2.4.0 / 1.38.1 | `b05885fa…` / `55d7a1cc…` | swift baseline only |
| gRPC C-core (legacy) | 1.81.0 prebuilt (`native-deps/grpc-c-prebuilt`, Bazel `build:release --compilation_mode=opt --strip=always`) | — | legacy |
| Benchmark server | grpc/grpc `b8f09d9168d856020d236bd32f39195f7b5aa2cf`, `server_type=async`, Bazel `--config=opt` | — | all |

Permalinks use the form `https://github.com/<org>/<repo>/blob/<revision>/<path>`; all paths quoted for dependencies
were read from shallow clones at exactly these revisions.

### 2.3 CSV identity and schema (Observed)

156 rows, header as produced by `run-all.sh` (`benchmark,case,implementation,platform,target,warmup_calls,calls,
concurrency,request_bytes,response_bytes,elapsed_seconds,calls_per_second,application_bytes_per_second,latency_*,
request_messages,response_messages,messages_per_second,time_to_first_response_us,final_response_latency_us`). The
`platform` column reads `ios` for the two Kotlin clients and `ios-simulator-arm64` for the Swift client; `run-all.sh`
runs all three on the same simulator. The CSV is treated as one run per case: no repeats, no confidence intervals.
`stream-concurrency-sweep` contains c1–c16 for all implementations; the c32 case defined in
`StreamingBenchmarks.kt:235` is absent from the CSV.

### 2.4 Execution limitation

This analysis ran on Linux. Nothing was compiled or executed for iOS/macOS. Statements about runtime behaviour are
either CSV facts or inferences from source.

### 2.5 Benchmark comparability (validation of §"Required investigation 1")

Equivalent across the three clients (Observed):

- Case tables, warmups, measured counts, concurrency and payload sizes are identical
  (`shared-kotlin-client/.../UnaryBenchmarks.kt`, `StreamingBenchmarks.kt` vs `swift-client/.../UnaryBenchmarks.swift`,
  `StreamingBenchmarks.swift`).
- One fresh channel/client per case, warm-up on that channel, then the measured phase; timing boundaries around the
  measured phase only (`BenchmarkCli.kt:171-179`, `CallBenchmark.kt:35-49`; `BenchmarkCLI.swift:213-225`,
  `Benchmark.swift:132-149`).
- Response validation per call/message (`payload.body.size == responseBytes` in both).
- Same C++ async server, same plaintext target, same request payload bytes (`i % 251`).
- Both `current` and `swift` use `HTTP2ClientTransport.TransportServices` with plaintext and default HTTP/2 config
  (`SwiftGrpcClient.swift:50-80`, `BenchmarkCLI.swift:213-216`).

Asymmetries that confound specific comparisons:

| # | Asymmetry | Affects | Severity |
|---|---|---|---|
| A1 | **Swift optimisation level.** `swift-client/build.sh:17` builds `--configuration release`. The Kotlin binary links the bridge and all Swift dependencies from KGP's SwiftPM import, which runs `xcodebuild build -scheme KotlinMultiplatformLinkedPackage -destination … -derivedDataPath …` with no `-configuration` and no optimisation build setting (KGP 2.4.0 `XcodebuildDefFileWorkAction.kt:105-122`; the only extra-args hook, `additionalXcodeArgs`, is `@Internal` and documented as test-only, `ConvertSyntheticSwiftPMImportProjectIntoDefFile.kt:82-95`). xcodebuild's default build configuration is Debug. **Strong inference: the `current` Swift stack is `-Onone`.** Must be verified on macOS (§8, V0). | current vs swift, current vs legacy | High |
| A2 | Kotlin/Native compiler and runtime differ: legacy 2.3.0, current 2.4.0 (GC/allocator/interop changes between versions are not analysed here). | current vs legacy | Medium |
| A3 | Kotlin harness runs workers on `Dispatchers.Default` (native `MultiWorkerDispatcher`); Swift harness uses a task group on the cooperative pool. Both are the natural idiom; both add one scheduler between the benchmark loop and the client. | all | Low |
| A4 | The bridge advertises `grpc-accept-encoding: gzip` (`SwiftGrpcClient.swift:62-65`); the Swift baseline uses `Compression.defaults` (none). C-core advertises gzip/deflate by default, so `current` is closer to C-core than `swift` is. The server never compresses, so no decompressor is created (`GRPCStreamStateMachine.swift` `processInboundEncoding`). Negligible. | — | None |
| A5 | Latency sample semantics differ per shape (round trip, inter-arrival, write suspension) but identically across implementations. | — | None |
| A6 | Simulator loopback: TCP loopback and Network.framework on the simulator are not device-representative. | absolute values | Medium |

Reliable comparisons: `current` vs `swift` for call-shape and per-byte *patterns* (same transport, same server),
`legacy` vs the other two for end-to-end regressions. Confounded: any attribution of `current`–`swift` magnitude to
bridge design before A1 is resolved.

---

## 3. Quantitative benchmark analysis

All numbers are Measured. Throughput ratios are (faster ÷ slower) so >1 means `current` is slower; latency ratios
are (`current` ÷ other). Full tables: `ratios.md`.

### 3.1 Sequential unary latency (c = 1)

| case | current mean µs | legacy | swift | cur/leg | cur/swift | swift/leg |
|---|---|---|---|---|---|---|
| unary-latency default (0 B) | 602 | 155 | 291 | 3.89 | 2.07 | 1.88 |
| empty-c1 | 600 | 153 | 351 | 3.92 | 1.71 | 2.29 |
| symmetric-64b | 614 | 168 | 353 | 3.65 | 1.74 | 2.10 |
| symmetric-1k | 621 | 184 | 390 | 3.37 | 1.59 | 2.11 |

The `current` − `legacy` delta is remarkably constant at **≈ 440–450 µs per call** across 0 B, 64 B and 1 KiB, and
`current` − `swift` is ≈ 230–310 µs. This is a per-call fixed cost, independent of payload (Strong inference).

### 3.2 Payload sweep: per-byte behaviour

Deltas relative to the 1 KiB case of the same implementation isolate per-byte cost (derived from
`ratios.md` "unary c1 mean latency deltas"):

| direction | size | current − swift excess (µs) | per KiB | current − legacy excess (µs) | per KiB |
|---|---|---|---|---|---|
| download | 64 KiB | 472 − 268 = 204 | 3.2 | 640 − 440 = 200 | 3.1 |
| download | 1 MiB | 4249 − 268 = 3981 | 3.9 | 2807 − 440 = 2367 | 2.3 |
| download | ~4 MiB | 21224 − 268 = 20956 | 5.1 | 17681 − 440 = 17241 | 4.2 |
| upload | 64 KiB | 432 − 228 = 204 | 3.2 | 329 − 426 = −97 | ≤0 |
| upload | 1 MiB | 3156 − 228 = 2928 | 2.9 | 1590 − 426 = 1164 | 1.1 |
| upload | ~4 MiB | 5873 − 228 = 5645 | 1.4 | 1539 − 426 = 1113 | 0.3 |

Two findings:

- Versus **legacy**, `current` pays a **response-path** per-byte excess of ≈2–4 µs/KiB (≈200–330 MB/s) but only
  0.3–1.1 µs/KiB on the request path. Versus `swift`, both directions show ≈3 µs/KiB.
- `swift` vs `legacy`: per-byte costs are at parity or better in both directions (swift upload is 2–3× faster than
  legacy; download 64 KiB ≈ equal, 1 MiB faster). So the response-path excess is `current`-specific.

The `swift` baseline shows a flush-coalescing fingerprint: `upload-64k` (218 µs) is faster than `upload-1k`
(388 µs). A 64 KiB write reaches `FlushCoalescing.maxBytes` (64 KiB) and is flushed immediately; a 1 KiB write waits
for the 100 µs timer (`HTTP2ClientTransport.swift:262-264`, `FlushCoalescingHandler.swift:99-129`). The same
effect masks the request-path per-byte cost of `current` at 64 KiB (`upload-64k` 649 µs vs `upload-1k` 616 µs).

### 3.3 Unary concurrency scaling

Throughput (calls/s) and mean latency; `x/c1` = throughput relative to the same implementation's c1:

| case | current | cur/c1 | legacy | leg/c1 | swift | swift/c1 | cur min µs | leg min µs | swift min µs |
|---|---|---|---|---|---|---|---|---|---|
| empty-c1 | 1668 | 1.00 | 6534 | 1.00 | 2850 | 1.00 | 540 | 115 | 161 |
| empty-c4 | 4164 | 2.50 | 12932 | 1.98 | 9705 | 3.41 | 626 | 124 | 273 |
| empty-c8 | 4346 | 2.61 | 13981 | 2.14 | 13182 | 4.63 | 876 | 139 | 237 |
| empty-c16 | 3949 | 2.37 | 14380 | 2.20 | 15603 | 5.48 | 896 | 156 | 352 |
| empty-c32 | 3204 | 1.92 | 13165 | 2.01 | 20108 | 7.06 | 1487 | 399 | 293 |
| empty-c64 | 2433 | 1.46 | 13284 | 2.03 | 18316 | 6.43 | 6880 | 797 | 344 |
| empty-c128 | 1849 | 1.11 | 20802 | 3.18 | 22360 | 7.85 | 961 | 180 | 322 |
| 1k-c128 | 1346 | 0.83 | 15737 | 2.72 | 24675 | 8.64 | 843 | 206 | 291 |

`current` peaks at c8 (≈4.3k calls/s) and then *loses* throughput; at c128 it is 11–12× slower than both others and
its mean latency is 69–95 ms (Little's law: 128 ÷ 1849 = 69 ms). The **minimum** latency of `current` rises from
0.54 ms (c1) to 6.9 ms (c64): no call escapes the slowdown, which points at a shared serialised resource, stop-the-
world pauses, or thread over-subscription rather than a fixed pipeline depth. `swift` scales to ≈24k calls/s (it is
latency-bound, not CPU-bound, at low concurrency), `legacy` to ≈13–21k.

### 3.4 Streaming

Messages/s and mean per-message latency sample (inter-arrival for downloads, write-suspension for uploads,
round trip for ping-pong):

| case | current msg/s | legacy | swift | leg/cur | swift/cur | cur mean µs | leg | swift |
|---|---|---|---|---|---|---|---|---|
| bidi-ping-pong empty | 4913 | 15344 | 7488 | 3.12 | 1.52 | 407 | 130 | 267 |
| bidi-ping-pong 1k | 4897 | 14786 | 6493 | 3.02 | 1.33 | 408 | 135 | 308 |
| server-streaming 1k | 56927 | 62070 | 107361 | 1.09 | 1.89 | 17.4 | 16.0 | 9.3 |
| server-streaming 64k | 3005 | 8986 | 7486 | 2.99 | 2.49 | 333 | 111 | 134 |
| server-streaming 1m | 206 | 565 | 522 | 2.74 | 2.53 | 4918 | 1796 | 1945 |
| client-streaming 1k | 31968 | 26065 | 43666 | **0.82** | 1.37 | 31 | 38 | 23 |
| client-streaming 1m | 905 | 573 | 2293 | **0.63** | 2.53 | 1008 | 1720 | 408 |
| stream-message-overhead 1k (65536 × 1 KiB) | 33549 | 26315 | 40309 | **0.78** | 1.20 | 30 | 38 | 25 |
| stream-message-overhead near-4m | 218 | 152 | 521 | **0.70** | 2.39 | 3737 | 6545 | 1588 |
| bidi-full-duplex balanced-1k | 40823 | 32167 | 67927 | **0.79** | 1.66 | 49 | 62 | 29 |
| bidi-full-duplex upload-heavy | 14732 | 10488 | 24660 | **0.71** | 1.67 | 135 | 190 | 81 |
| bidi-full-duplex download-heavy | 5961 | 10996 | 11969 | 1.84 | 2.01 | 335 | 182 | 167 |
| bidi-full-duplex balanced-64k | 4381 | 7982 | 8518 | 1.82 | 1.94 | 456 | 250 | 235 |
| stream-concurrency c1 → c16 | 52.6k → 28.1k | 64.1k → 45.5k | 107k → 110k | 1.22 → 1.62 | 2.04 → 3.91 | 19 → 470 | 15 → 163 | 9 → 81 |

Patterns:

- **Ping-pong** isolates one request message + one response message per round trip with idle threads in between:
  `current` − `swift` ≈ 140 µs, `swift` − `legacy` ≈ 137–173 µs. Compared with unary (`current` − `swift`
  ≈ 250–310 µs) the difference is per-call setup/teardown.
- **Small-message download streaming** is near parity with legacy at c1 (17.4 vs 16.0 µs/message) but degrades with
  concurrency faster than legacy (c16: 470 vs 163 µs mean; p50 483 vs 15 µs). `swift` is flat.
- **Upload streaming** is where `current` beats `legacy` at every size. Legacy's `sendMessage` is strictly one
  message in flight (`NativeClientCall.kt:203-204, 606-625`, `Ready` in `suspendUtils.kt:42-60`), so each message
  pays a C-core completion round trip (38 µs at 1 KiB, 1.7 ms at 1 MiB). grpc-swift's writer pipelines
  (`NIOAsyncWriter` yields do not suspend while the channel is writable). This is the main exception to the premise
  and it is explained by a *legacy* limitation, not a `current` strength: `swift` is 1.5–4× faster still.
- **Large-message download streaming** shows the response-path per-byte excess again: 64 KiB costs `current`
  333 µs/message vs 111 (legacy) and 134 (swift); 1 MiB 4.9 ms vs 1.8/1.9 ms. Time-to-first-response for
  server-streaming-1m: 10.6 ms vs 6.1 ms (legacy) vs 4.6 ms (swift).

### 3.5 What the shapes say about the kind of overhead (Strong inference)

| Regime | Evidence | Kind of overhead |
|---|---|---|
| Unary c1, ping-pong | constant ≈440 µs/call and ≈275 µs/round trip regardless of payload | per-call and per-message fixed cost, dominated by scheduling/hand-offs when threads are idle |
| Small-message streaming c1 | ≈8 µs/message over swift, ≈1.5 µs over legacy | the same hops, but pipelined and cheap when threads are hot |
| Payload sweep | +2–4 µs/KiB, response direction only vs legacy | per-byte, response path |
| Concurrency sweep | throughput peaks at c8 and falls; min latency grows | contention / STW / over-subscription |
| Upload streaming | current faster than legacy | legacy's one-message-in-flight limit dominates |

---

## 4. Architecture and call-path comparison

### 4.1 Layers

```
                    C-core (legacy / nonIos)                      grpc-swift-2 (current iOS)
Kotlin API          GrpcClient.call → unaryRpc/…Rpc (suspendClientCalls.kt)   same
Common boundary     main: doCall() in suspendClientCalls.kt        GrpcClientTransport.execute → Flow<GrpcClientCallEvents>
                    branch: NonIosGrpcClientTransport (nonIosMain)  SwiftGrpcClientTransport (iosMain)
Platform adapter    NativeClientCall + CompletionQueue (cinterop)   SwiftGrpcCallAdapter / KotlinGrpcRequestSource
                                                                    ↕ Objective-C completion handlers (K/N ObjC export)
Swift bridge        —                                               SwiftGrpcClient / SwiftGrpcCall / CallRunner / RawMessageBridge
gRPC library        grpc C-core 1.81 (chttp2, own I/O threads)      GRPCCore 2.4.3 (ClientRPCExecutor, ClientStreamExecutor)
Transport           chttp2 + C-core endpoint                        GRPCNIOTransportCore 2.9.2 + NIOHTTP2 + NIOTransportServices
Threads             Dispatchers.Default pool + C-core threads       Dispatchers.Default pool + Swift cooperative pool + NIOTS event loop
```

The common event boundary introduced on this branch (`GrpcClientTransport.kt:24-46`) differs from the pre-split
`main` code (`git show origin/main:…/suspendClientCalls.kt`, `doCall` lines 213-300) only in that Headers and Closed
are emitted as flow events (`GrpcClientCallEvents`) instead of completing futures from the listener, and the event
channel has capacity 3 instead of 1. Both are Kotlin-only, allocation-light and not plausible contributors to gaps
measured in hundreds of microseconds (Observed; estimate < 1 µs/event, Hypothesis). The legacy binary runs the
pre-split code; the split itself is not a factor in the `current` measurements because iOS never used it.

### 4.2 Unary request/response: side-by-side

**C-core (legacy; identical code in `nonIosNativeMain`)** — Observed from `suspendClientCalls.kt` (main) and
`NativeClientCall.kt`:

1. `doCall` opens `coroutineScope`, `createCall` → `grpc_channel_create_call` (synchronous).
2. `call.start` → `startRecvStatus` batch (RECV_STATUS_ON_CLIENT) + `sendAndReceiveInitialMetadata` batch
   (SEND_INITIAL_METADATA + RECV_INITIAL_METADATA). Metadata converted in-process to `grpc_metadata` arrays.
3. `launch` sender coroutine (1 dispatch) → `sendMessage`: `requestMarshaller.encode` → `Buffer` →
   `grpc_slice_from_copied_buffer` per segment (**copy 1**) → SEND_MESSAGE batch → `halfClose` batch.
4. `emitResponses`: `request(1)` → RECV_MESSAGE batch. Each batch completion arrives on a C-core thread
   (`opsCompleteCb` → `CallbackFuture.complete`) and does `events/responses.trySend` → resumes the collector
   (**1 Kotlin dispatch each** for headers, message, close). Message bytes are copied from `grpc_byte_buffer`
   slices into a `Buffer` (**copy 2**, `utils.native.kt:85-102`), then `decode` copies the `bytes` field into a
   `ByteArray` (**copy 3**, `WireDecoder.native.kt:179-192`).
5. `finishClose` → `listener.onClose` → `grpc_call_unref(raw)` deterministically (`NativeClientCall.kt:267-285`).

Inventory per unary call: 4 Kotlin coroutine dispatches (sender launch, headers, message, close) plus C-core internal
scheduling (combiner/executor, typically inline); 0 foreign-runtime object wrappers; 5 `grpc_call_start_batch`
calls, each under `CompletionQueue.batchStartGuard` (`CompletionQueue.kt:142-166`); 1 `Arena` per batch; metadata
converted twice (send, receive) plus trailers; message copies: request 1, response 2 (buffer + decode).

**grpc-swift-2 (current)** — Observed from `GrpcClientTransport.ios.kt`, `SwiftGrpcRequestSource.kt`,
`SwiftGrpcCallAdapter.kt`, `SwiftGrpcClient.swift`, `SwiftGrpcCall.swift`, `CallRunner.swift`,
`RawMessageBridge.swift`, grpc-swift-2 `ClientRPCExecutor(+OneShotExecutor).swift`, `ClientStreamExecutor.swift`,
grpc-swift-nio-transport `GRPCChannel.swift`, `Connection.swift`, `GRPCClientStreamHandler.swift`, swift-nio
`AsyncChannelHandler.swift`, `NIOAsyncWriter.swift`:

1. `execute` opens `coroutineScope`; `headers.copy()`; constructs `KotlinGrpcRequestSource`
   (NSObject subclass ⇒ K/N allocates an ObjC instance with a back-reference, `ObjCInterop.mm:76-92`) with a
   `SupervisorJob`, a `CoroutineScope`, a `Channel(RENDEZVOUS)`, two atomics, an `invokeOnCompletion` handler and a
   collector coroutine started `UNDISPATCHED` that immediately suspends in `messages.send(request)`
   (`SwiftGrpcRequestSource.kt:33-65`).
2. `startCall` → `headers.toSwift()` (ObjC object + one ObjC call per entry) → **ObjC call**
   `startCallWithFullMethodName` → Swift parses the method name, copies `Metadata`, replaces `user-agent`, allocates
   `SwiftGrpcCall` + `AsyncThrowingChannel` + `PullState` + `CallRunner` and spawns **Task #1** (`callTask`)
   (`SwiftGrpcClient.swift:117-143`, `SwiftGrpcCall.swift:29-55`). Kotlin wraps the returned `SwiftGrpcCall` in a
   Kotlin object (`convertUnmappedObjCObject`, `ObjCExport.mm:286-289`).
3. **Task #1**: `client.bidirectionalStreaming` (always bidi, `CallRunner.swift:40`) → `Mutex` in `GRPCClient` →
   `OneShotExecutor._execute` → `withTaskGroup` → `transport.withStream` → `GRPCChannel.makeStream` (state lock,
   load-balancer pick) → `Connection.makeStream` → `multiplexer.openStream` (**event-loop hop**; creates the HTTP/2
   stream channel, `GRPCClientStreamHandler`, `NIOAsyncChannel` with inbound watermarks 2/10) → `executeThenClose` →
   `ClientStreamExecutor.execute` spawns **Task #2** (`_processRequest`) and awaits the first response part.
4. **Task #2** writes `.metadata` (NIOAsyncWriter yield → `didYield` → **event-loop hop**), then runs our producer
   (`RawMessageBridge.swift:31-42`): `nextRequestMessage()` = `withTaskCancellationHandler` +
   `withCheckedThrowingContinuation` (**Swift continuation #1**) → **ObjC→Kotlin call** `nextRequestWithCompletion`
   (the Swift closure is converted to a Kotlin function object wrapping a retained block, `ObjCExport.mm:448`) →
   Kotlin CAS on `pendingCompletion`, `launch(UNDISPATCHED)` a pull coroutine → `receiveCatching()` takes the element
   and **resumes the collector (Kotlin dispatch #1)** → `encode` (`Buffer`) → `KotlinGrpcRequestMessage`
   (second NSObject-subclass Kotlin object) → **Kotlin→ObjC block call** → Swift continuation resumes (**Swift
   executor hop**) → `copyToRawMessage`: `ByteBuffer(repeating: 0, count:)` (**memset**) + `fillBuffer`
   (**ObjC→Kotlin call**, memcpy per segment = **copy 1**) → `writer.write` → `.map` → `RPCWriter.Closable` →
   `NIOAsyncChannelOutboundWriter.write` → `NIOAsyncWriter.yield` (lock; `didYield` → **event-loop hop**) → on the
   loop: `GRPCClientStreamHandler.write(.message)` → framer append; `flush` → `nextOutboundFrame` → framer copies
   the message into `writeBuffer` (**copy 2**, `GRPCMessageFramer.swift:113-117`) → `context.write(DATA)`. The
   producer loops: second `nextRequestMessage()` (**continuation #2**, **ObjC→Kotlin**, second `launch`) → channel
   closed → completion `(nil, nil)` → resume (**hop**) → `defer { self.cancel() }` (**ObjC→Kotlin** `cancel()`:
   cancels collector, channel, job) → `stream.finish()` → **event-loop hop** → `closeOutbound` + flush with
   END_STREAM → `FlushCoalescingHandler` arms a 100 µs timer (unary uses `delayFlushUntilHalfClosed`, so headers,
   data and END_STREAM go out in one flush, then the timer).
5. Response, on the event loop: HEADERS → `.metadata` → `NIOAsyncChannelHandler` buffers → `channelReadComplete` →
   `source.yield` → resumes Task #1's continuation (**hop to cooperative pool**) → `StreamingClientResponse` →
   handler: `SwiftGrpcHeadersEvent(SwiftGrpcMetadata(copy))` → `eventChannel.send` — rendezvous, suspends until
   Kotlin pulls (**unsafe continuation**, `ChannelStorage.swift:27-73`).
6. Kotlin: `nextEvent()` → `suspendCancellableCoroutine` + `invokeOnCancellation` → **Kotlin→ObjC call**
   `nextEventWithCompletion` (Kotlin lambda → block) → `PullState.beginPull` CAS → spawns **Task #3** per pull
   (`SwiftGrpcCall.swift:76-98`) (**hop**) → new `AsyncThrowingChannel.Iterator` → `next()` (resumes the runner:
   **hop**) → `finishPull` → completion block → **ObjC→Kotlin** → `continuation.resume` → **Kotlin dispatch #2** →
   `event.headers.toKotlin()` (visitor NSObject-subclass Kotlin object + one ObjC→Kotlin call per entry) →
   `emit(Headers)`.
7. DATA → deframe (`readSlice`, no copy for single-frame messages; `writeImmutableBuffer` accumulation copy when a
   message spans frames) → `.message(GRPCNIOTransportBytes)` → NIO yield → runner's `for try await part` → `send`
   (rendezvous) → Kotlin pull: **Task #4**, block, **Kotlin dispatch #3** → `decodeMessage`: `withUnsafeBytes`
   (**Kotlin→ObjC→Kotlin block**) → `copySwiftBytes` into a `Buffer` (**copy 3**, `SwiftGrpcBuffers.kt:55-74`) →
   `decode` (**copy 4** into `ByteArray`) → `emit(Message)`. The `SwiftGrpcMessageEvent` (and the `ByteBuffer` it
   holds) stays alive until the Kotlin wrapper is swept by the K/N GC.
8. Trailers → `.trailingMetadata` → `SwiftGrpcClosedEvent` → `finish(with:)` → Kotlin pull: **Task #5**, block,
   **Kotlin dispatch #4** → `toKotlin()` (metadata + status) → `emit(Closed)` → flow completes → `finally`
   `requestSource.cancel()` (**third** cancel of the same source) → `coroutineScope` completes.
9. Teardown: nothing releases the Swift `SwiftGrpcCall` eagerly. Its Kotlin wrapper becomes garbage; on the next
   K/N GC sweep `ExtraObjectData::Uninstall` → `Kotlin_ObjCExport_releaseAssociatedObject`
   (`mm/ExtraObjectData.cpp:53-65`) → `SwiftGrpcCall.deinit` runs on the special GC thread
   (`native-arc-integration.md`, "Deinitializers") → `callTask.cancel()`, **`requestSource.cancel()` (ObjC→Kotlin
   from the GC thread)**, `eventChannel.finish()`. The Kotlin `KotlinGrpcRequestSource`/`KotlinGrpcRequestMessage`
   objects are GC *roots* while Swift retains them (`ExternalRCRef.cpp:138-171`) and become collectable only in
   the following cycle (the documented two-cycle behaviour).

Inventory per unary call (Observed unless noted):

| Item | C-core (legacy) | grpc-swift-2 (current) |
|---|---|---|
| Kotlin coroutines launched | 1 (sender) | 3 (collector + 2 pull coroutines) + per-call `SupervisorJob`/`CoroutineScope` |
| Kotlin suspensions resumed by another thread | 3 (headers, message, close) + sender launch | 4 (3 events + collector) |
| Swift `Task`s created | 0 | 5 (`callTask`, 3 pulls, grpc-swift `_processRequest`) + 1 task group |
| Swift continuations | 0 | 2 checked (request pulls) + 3 rendezvous producer (unsafe) + up to 3 NIO inbound (unsafe) + NIO writer if back-pressured |
| Kotlin→ObjC crossings | 0 | ≈9 + one per metadata entry (`startCall`, `toSwift`, 3 `nextEvent`, 2 request completions, `withUnsafeBytes`, `length`, property reads) |
| ObjC→Kotlin crossings | 0 | ≈10 + one per metadata entry (2 `nextRequest`, `fillBuffer`, `length`, 3 event completions, `withUnsafeBytes` body, 2–3 `cancel()` incl. one from the GC thread) |
| Cross-thread hand-offs on the critical path (approx.) | ≈4 Kotlin dispatches (+ C-core internal) | ≈17: 4 Kotlin dispatches, 5 Task enqueues, 2 continuation resumes, 3 event-loop hops (metadata, message, finish), 3 loop→pool resumes |
| Foreign object wrappers allocated | 0 | ≥9 (2 Kotlin NSObject subclasses + their ObjC halves, 1 `SwiftGrpcCall` wrapper, 3 event wrappers, 2 metadata wrappers, 1 status wrapper, 2 visitor objects, ≥4 block conversions) |
| Request message copies (Kotlin buffer → wire) | 1 (`grpc_slice_from_copied_buffer`) | 2 (`fillBuffer` into `ByteBuffer` after a memset; framer `writeImmutableBuffer`) |
| Response message copies (wire → decoded `ByteArray`) | 2 | 2 (+ deframer accumulation copy for multi-frame messages, also present in `swift`) |
| Flush behaviour | immediate | 100 µs coalescing timer unless ≥64 KiB pending |
| Call object release | deterministic in `onClose` | deferred to K/N GC sweep + GC-thread `deinit`, two cycles |

The Swift baseline's own unary path (`GRPCClient.unary` → `ClientResponse(stream:)`) uses the same executors and
also treats unary as a stream (`ClientResponse+Convenience.swift`, three `iterator.next()` calls). So "unary through
streaming abstractions" is inherent to grpc-swift-2 and costs `swift` and `current` alike; the bridge's extra cost is
the per-part rendezvous + `Task` protocol on top.

### 4.3 Client-streaming request production

- **C-core**: collector coroutine → `Channel(RENDEZVOUS)` → sender loop → `ready.suspendUntilReady()` →
  `sendMessage` (copy + SEND_MESSAGE batch) → completion on a C-core thread → `turnReady` → conflated channel →
  sender resumes (1 dispatch per message). Strictly one message in flight; `halfClose` after the flow ends.
- **grpc-swift-2**: per message: Swift `nextRequestMessage()` (checked continuation) → ObjC→Kotlin → Kotlin
  `launch(UNDISPATCHED)` → `receiveCatching()` (resumes collector: 1 Kotlin dispatch) → `encode` → ObjC block →
  Swift resume (hop) → memset + `fillBuffer` copy → `writer.write` → NIO yield → event-loop hop → framer copy →
  frame. `NIOAsyncWriter` does not suspend the producer while the channel is writable, so up to the socket buffer's
  worth of messages are in flight — this is why upload beats legacy despite ≈3 hops/message.

### 4.4 Server-streaming response consumption

- **C-core**: `request(1)` after each emit → RECV_MESSAGE completion on a C-core thread → `events.trySend` →
  1 Kotlin dispatch → copy + decode → emit. Depth: one message requested at a time; C-core buffers incoming frames
  internally up to the flow-control window.
- **grpc-swift-2**: NIO reads while fewer than 10 parts are buffered (`NIOAsyncChannel` default watermarks,
  `AsyncChannel.swift:60-65`); the runner iterates and blocks in the rendezvous `send`; each Kotlin pull spawns a
  `Task`, resumes the runner, completes the block, dispatches Kotlin, copies and decodes. Depth as seen by the
  runner: one event at a time (same as C-core's `request(1)`), but each step costs ≈2–3 hand-offs instead of 1.
  Message `ByteBuffer`s are retained until the K/N GC sweeps the event wrapper.

### 4.5 Bidirectional ping-pong and full duplex

Ping-pong combines §4.3 and §4.4 for one message each per round trip with idle threads in between (Kotlin
`Channel(1)` feeding the request flow). Full duplex runs both pipelines concurrently on one stream; `current`
inherits the upload advantage (pipelined writes) and the download penalty (per-byte excess) — visible as
`upload-heavy` 0.71× legacy time but `download-heavy` 1.84×.

### 4.6 Cancellation and terminal status

- **C-core**: `cancel` → `grpc_call_cancel_with_status`; status arrives through RECV_STATUS_ON_CLIENT →
  `markClosePending` → `finishClose` → `grpc_call_unref` (deterministic).
- **grpc-swift-2**: Kotlin `finally` → `requestSource.cancel()` and, if not closed, `cancelAndWait` (another `Task`);
  Swift `CallRunner` `defer { requestSource.cancel() }`; producer `defer { self.cancel() }`; `deinit` cancels again
  from the GC thread. Terminal status is a `SwiftGrpcClosedEvent` sent through the rendezvous channel followed by
  `eventChannel.finish()`. Correct but redundant (three to four idempotent cancels per call), and the final release
  is GC-timed.

---

## 5. Bottleneck findings

Each finding: layer · evidence · affected benchmarks · mechanism · confidence · alternatives.

### F1 — The Swift stack linked into the Kotlin binary is probably unoptimised (Debug)

- **Layer**: benchmark/build asymmetry.
- **Evidence**: KGP 2.4.0 `XcodebuildDefFileWorkAction.kt:105-122` runs `xcodebuild build -scheme
  KotlinMultiplatformLinkedPackage -destination generic/platform=… -derivedDataPath … CC=… LD=… ARCHS=…` with no
  `-configuration` and no `SWIFT_OPTIMIZATION_LEVEL`; the products and linker arguments captured from that build
  (`XcodebuildDefFileUtils.parseLdCall`) are what the Kotlin link consumes. Nothing in this repository sets a
  configuration (`git grep additionalXcodeArgs|-configuration` is empty). The Swift baseline is built with
  `--configuration release` (`swift-client/build.sh:17`); C-core archives with `--compilation_mode=opt`
  (`native-deps/grpc-c-prebuilt/.bazelrc:11`, `build_archives.py:17`).
- **Affected**: every `current` row.
- **Mechanism**: `-Onone` Swift disables generic specialisation and inlining across NIO/grpc-swift, roughly
  multiplying the cost of every ByteBuffer, state-machine, async-sequence and continuation operation, and increases
  allocation and ARC traffic. This would inflate the per-call, per-byte and concurrency numbers simultaneously.
- **Confidence**: Strong inference for the build configuration; **Unknown** for the share of the gap it explains.
- **Alternatives**: xcodebuild might select Release if the generated scheme were configured so — the generator
  (`GenerateSyntheticLinkageImportProject.kt`) contains no configuration settings, so this is unlikely. Verify on
  macOS (V0).

### F2 — Flush coalescing adds up to 100 µs to every flush in low-traffic shapes (both Swift clients)

- **Layer**: our use of grpc-swift-nio-transport configuration (default), upstream behaviour.
- **Evidence**: `Connection.defaults` enables `FlushCoalescing.defaults` (100 µs, 64 KiB)
  (`HTTP2ClientTransport.swift:125-127, 262-264`); `configureGRPCClientPipeline` installs the handler
  (`NIOChannelPipeline+GRPC.swift`); the bridge uses `Transport.Config.defaults` (`SwiftGrpcClient.swift:60`) and
  the baseline uses the default initialiser. CSV: `swift` `upload-64k` (218 µs) < `upload-1k` (388 µs); `swift`
  − `legacy` unary ≈ 137–198 µs, ping-pong ≈ 137–173 µs.
- **Affected**: unary c1, ping-pong, every write in low-concurrency streaming, time-to-first-response.
- **Mechanism**: `FlushCoalescingHandler.flush` defers `context.flush()` to a scheduled callback unless 64 KiB are
  pending or the channel becomes unwritable. C-core writes immediately.
- **Confidence**: Strong inference.
- **Alternatives**: none for the 64 KiB anomaly; the residual `swift` − `legacy` (~40–90 µs) is NIO/Swift concurrency
  overhead and the Network.framework transport (Unknown share).

### F3 — Per-event `Task` + iterator + rendezvous channel in `SwiftGrpcCall.nextEvent`

- **Layer**: our Swift bridge design.
- **Evidence**: `SwiftGrpcCall.swift:65-99` (Task per pull, new iterator per pull, CAS state); `CallRunner.swift:
  46-72, 101-107` (rendezvous `send` per part with two `checkCancellation`); `ChannelStorage.swift` (unsafe
  continuations, `ManagedCriticalState` lock, `OrderedSet` bookkeeping). CSV: constant ≈250–310 µs `current` −
  `swift` per unary call and ≈140 µs per ping-pong round trip; only ≈8 µs/message when pipelined.
- **Affected**: unary, ping-pong, first-response latency; contributes to concurrency (task proliferation).
- **Mechanism**: each of the 3+ events per call costs a `Task` allocation/enqueue, an executor hop for the runner's
  resume, a lock + continuation pair in the channel, a block call and a Kotlin dispatch. C-core delivers the same
  three events with one callback → one dispatch each.
- **Confidence**: Strong inference (mechanism Observed; magnitude Hypothesis until F1 is resolved).
- **Alternatives**: part of the fixed cost is Kotlin-side (`suspendCancellableCoroutine`, sealed-class events) —
  equal to legacy.

### F4 — Per-message coroutine launch and third-party thread hops in the request source

- **Layer**: our Kotlin bridge design (`iosMain`) + Swift concurrency.
- **Evidence**: `SwiftGrpcRequestSource.kt:70-101` (`launch(UNDISPATCHED)` per pull; rendezvous channel; CAS);
  `RawMessageBridge.swift:45-58` (checked continuation per message). CSV: upload shapes are still faster than
  legacy, so this is not a regression driver; it is fixed cost per request message (≈3 hops).
- **Affected**: unary (2 pulls per call), client streaming, ping-pong.
- **Mechanism**: C-core: collector → channel → sender loop (1 channel hand-off + 1 wakeup per message). Current:
  channel hand-off + coroutine allocation + ObjC block + Swift continuation resume + NIO event-loop hop.
- **Confidence**: Observed mechanism; Hypothesis for magnitude (~tens of µs per message when idle).
- **Alternatives**: none.

### F5 — Response-path per-byte excess (≈2–4 µs/KiB) present only in `current`

- **Layer**: bridge design + Kotlin/Native interop lifetime; possibly F1.
- **Evidence**: §3.2 table (download direction only vs legacy; both directions vs swift, but upload vs legacy is
  ≤1 µs/KiB); `server-streaming 64k/1m`, `download-heavy`, `balanced-64k`.
- **Mechanism (Hypothesis, ranked)**:
  1. `SwiftGrpcMessageEvent` retains the message `GRPCNIOTransportBytes` (a slice sharing storage with the
     deframer's accumulation buffer) until the K/N GC sweeps the Kotlin wrapper (§4.2 step 9). While the slice is
     alive, `GRPCMessageDeframer.append`'s `discardReadBytes`/`writeImmutableBuffer` cannot reuse storage and must
     copy-on-write and regrow (`GRPCMessageDecoder.swift:127-139`), and every message touches freshly allocated
     pages instead of recycled ones. C-core destroys its `grpc_byte_buffer` right after decoding
     (`NativeClientCall.kt:533-535`). The Swift baseline drops the slice as soon as SwiftProtobuf has parsed it.
  2. Unoptimised Swift (F1) in the deframer / `ByteBuffer` path.
  3. Kotlin-side segment copying (`copySwiftBytes`, `UnsafeBufferOperations.writeToTail` per 8 KiB) — but legacy's
     `toKotlin()` does the same, so this cannot explain the *excess* unless the K/N 2.4 runtime differs (A2).
- **Confidence**: Measured pattern; Hypothesis mechanism.
- **Alternatives**: NIO inbound watermark/read scheduling stalls (10-element high watermark is by count, so large
  messages buffer up to 10 MiB; stalls are unlikely to dominate); server-side effects (server is shared, and `swift`
  does not show it).

### F6 — GC-timed teardown of the Swift call and GC-rooted Kotlin objects

- **Layer**: Kotlin/Native interop + bridge design.
- **Evidence**: no eager release path in `SwiftGrpcClientTransport.execute` or `SwiftGrpcCallAdapter`;
  `SwiftGrpcCall.deinit` (`SwiftGrpcCall.swift:126-130`) calls back into Kotlin; K/N releases associated objects
  during sweep (`mm/ExtraObjectData.cpp:53-65`) on a special GC thread, and Kotlin objects retained from ObjC are GC
  roots (`ExternalRCRef.cpp:138-171`); docs state two GC cycles are needed for such chains
  (`native-arc-integration.md`). CSV: concurrency collapse and rising minimum latency (§3.3), stream-concurrency
  degradation (c16 p50 483 µs vs 15 µs legacy).
- **Mechanism**: per call, the Swift call object, its `AsyncThrowingChannel`, `CallRunner`, event objects and the
  retained `ByteBuffer`s survive until a GC cycle; the Kotlin request source, its coroutine scope, channel and
  collector survive one more cycle. Live heap and mark work grow with call rate; every ObjC→Kotlin entry (including
  those from the GC thread) participates in safepoints. C-core releases everything in `finishClose`.
- **Confidence**: Observed mechanism; **Hypothesis** that it drives the concurrency collapse.
- **Alternatives**: F7; F1.

### F7 — Cross-pool wake-up amplification under concurrency

- **Layer**: Kotlin coroutine dispatcher + Swift concurrency + NIO event loop (design consequence).
- **Evidence**: three independent thread pools each sized to the core count (`MultiWorkerDispatcher` with a
  `Channel<Runnable>(UNLIMITED)` and worker wake-ups, `MultithreadedDispatchers.kt:78-142`; Swift global executor;
  `singletonNIOTSEventLoopGroup`), and ≈17 hand-offs per unary call (§4.2). CSV: throughput falls as concurrency
  rises while `swift` (two pools) and `legacy` (Kotlin pool + a few C-core threads) keep scaling.
- **Mechanism**: with 128 in-flight calls every hop is a runnable-queue push plus a potential futex wake; with more
  runnable threads than cores the hops turn into context switches; the Kotlin dispatcher's single MPMC queue is a
  shared point for all Swift→Kotlin resumes.
- **Confidence**: Hypothesis.
- **Alternatives**: F6, F1.

### F8 — Metadata conversion crosses the runtime boundary once per entry

- **Layer**: bridge design.
- **Evidence**: `SwiftGrpcConversions.kt:33-58` (visitor object per conversion, one ObjC call per entry each way),
  `SwiftGrpcCallTypes.swift:89-101`; three conversions per call (request headers, response headers, trailers).
- **Affected**: per-call fixed cost (small; a few µs at most for 3–5 entries).
- **Mechanism**: C-core converts in-process into C structs. Each ObjC→Kotlin call switches K/N thread state and
  allocates a Kotlin string.
- **Confidence**: Observed mechanism; low magnitude (Hypothesis).

### F9 — Request path performs a memset and a second copy that C-core does not

- **Layer**: bridge (`RawMessageBridge.swift:64-80`: `ByteBuffer(repeating: 0, count:)` then `fillBuffer`) and
  grpc-swift-nio-transport framer (`GRPCMessageFramer.swift:113-117` copies each message into `writeBuffer`).
- **Evidence**: request-path per-byte excess vs legacy 0.3–1.1 µs/KiB (§3.2); vs swift ≈3 µs/KiB (the framer copy is
  shared with swift, so the bridge-only part is the memset + K/N copy).
- **Mechanism**: C-core copies once (`grpc_slice_from_copied_buffer`) and frames by reference.
- **Confidence**: Observed; low-to-medium magnitude.

### F10 — Remaining `swift` − `legacy` gap (NIO/Swift concurrency/Network.framework)

- **Layer**: grpc-swift-2 core + nio-transport + swift-nio-transport-services.
- **Evidence**: after subtracting the 100 µs coalescing bound, ≈40–90 µs per unary call and ≈40–70 µs per ping-pong
  remain; per-call work includes `withTaskGroup` + child task, `GRPCChannel` locks, `openStream` on the loop,
  `NIOAsyncChannel` creation, per-write `eventLoop.execute` + `flush` (`AsyncChannelHandler.swift:487-490`),
  HPACK, and Network.framework I/O (both `current` and `swift` use `TransportServices`; C-core uses its own
  endpoint).
- **Confidence**: Hypothesis (needs profiling). Out of kotlinx-rpc's control except through configuration or
  upstream contributions.

### Findings about the shared async boundary and Kotlin coroutine layer

- The new `GrpcClientTransport` event boundary adds two Kotlin flow emits per call and one `GrpcClientCallEvents`
  allocation per event compared with pre-split `main`. Observed; not material.
- `suspendClientCalls.kt` wrappers (`flow { emitAll(...) }`, interceptor scope, `singleOrStatus`) are identical for
  legacy and current. Not a factor.

---

## 6. Optimisation proposals (parity-only)

Each proposal answers the five scope questions (current behaviour, C-core behaviour, excess removed, how, evidence)
and passes the test "removes work that C-core does not do".

### P0 — Build the Swift stack with optimisation for release Kotlin binaries

- **Layer / location**: build configuration; KGP SwiftPM import (`ConvertSyntheticSwiftPMImportProjectIntoDefFile`
  task), `grpc/grpc-swift/build.gradle.kts`, `tests/grpc-benchmarks/kotlinx-rpc-client/run.sh`.
- **Current**: `xcodebuild build -scheme …` without `-configuration` → Debug products linked into every Kotlin
  binary, including `linkReleaseExecutableIosSimulatorArm64` (F1).
- **C-core**: prebuilt with `--compilation_mode=opt`.
- **Excess removed**: unoptimised code in bridge, grpc-swift, NIO.
- **Design**: (a) verify (V0); (b) short term, configure the KGP task from Gradle: the task exposes
  `additionalXcodeArgs: ListProperty<String>`; a convention in `gradle-conventions` can locate tasks named
  `convertSyntheticSwiftPMImportProjectIntoDefFile*` and add `-configuration Release` (or the build settings
  `SWIFT_OPTIMIZATION_LEVEL=-O GCC_OPTIMIZATION_LEVEL=s`) — the property is `@Internal`, so treat this as a
  workaround and file a KGP request to derive the configuration from the Kotlin binary's build type; (c) if the
  synthetic package's own targets can carry `swiftSettings: [.unsafeFlags(["-O"])]`, that covers only the bridge,
  not dependencies, so (b) is preferred.
- **Affected**: all benchmarks.
- **Expected impact**: potentially the largest single change; magnitude Unknown until measured.
- **Confidence**: Strong inference (configuration), Unknown (impact).
- **Risks**: none functional; build-time increase; KGP internal API.
- **Ownership**: kotlinx-rpc build + KGP (upstream request).
- **Experiment**: V0.

### P1 — Disable client flush coalescing (`connection.flushCoalescing = nil`)

- **Location**: `grpc/grpc-swift/Sources/GrpcSwiftBridge/SwiftGrpcClient.swift:60-73`.
- **Current**: 100 µs delayed flush unless ≥64 KiB pending (F2).
- **C-core**: immediate write; no timer.
- **Excess removed**: up to 100 µs per flush in sequential shapes; the timer callback itself.
- **Design**: `transportConfiguration.connection.flushCoalescing = nil` (available since nio-transport 2.8). Consider
  exposing it in `GrpcClientConfiguration` later; default to off for parity.
- **Affected**: unary c1–c8, ping-pong, first-response latency, low-concurrency streaming.
- **Expected impact**: −50…−100 µs per RPC/round trip; may *reduce* throughput at c≥32 where coalescing helps `swift`
  (measure both).
- **Confidence**: Strong inference.
- **Risks**: more syscalls under load; no correctness risk.
- **Ownership**: kotlinx-rpc (configuration).
- **Experiment**: V1.

### P2 — Replace the per-event `Task` + `AsyncThrowingChannel` pull with a direct single-slot hand-off

- **Location**: `SwiftGrpcCall.swift`, `CallRunner.swift`, `SwiftGrpcCallAdapter.kt`.
- **Current**: each Kotlin `nextEvent` spawns a `Task`, creates an iterator, suspends the runner in a rendezvous
  `send`, resumes it on pull, resumes the pull task, invokes the completion (F3).
- **C-core**: `request(1)` registers interest; the transport invokes the listener callback directly on its thread
  when the message is available; the Kotlin side does one `trySend` + one dispatch.
- **Excess removed**: per event: 1 `Task`, 1 iterator, 1–2 continuations, 1 lock round, ≥1 executor hop.
- **Design**: a `Mutex`-protected state `{ pendingCompletion?, bufferedEvent? (capacity 1, plus a terminal slot) }`.
  Runner side: `deliver(event)` — if a completion is pending, take it and invoke it synchronously (on the runner's
  thread, which mirrors C-core invoking `onMessage` on its thread); otherwise store the event and *suspend the runner
  with one continuation* until Kotlin pulls (keeps the one-outstanding depth of `request(1)`). Kotlin side:
  `nextEvent(completion)` — if an event is buffered, return it synchronously (and resume the runner's continuation);
  otherwise store the completion. Headers/Closed can be buffered alongside the message (capacity 3, matching
  `NonIosGrpcClientTransport`'s channel of 3) so the runner never blocks on non-message parts.

  ```swift
  final class EventSlot {
    private let state = Mutex<State>(.idle)       // idle | waitingKotlin(completion) | buffered([Event], runnerCont?)
    func deliver(_ e: Event) async {              // called by CallRunner
      switch state.withLock({ $0.deliver(e) }) {
      case .invoke(let completion): completion(e, nil)   // synchronous, no hop
      case .suspendRunner: await withUnsafeContinuation { c in state.withLock { $0.parkRunner(c) } }
      }
    }
    func pull(_ completion: Completion) {           // called from Kotlin via ObjC
      switch state.withLock({ $0.pull(completion) }) {
      case .invoke(let e, let runner): completion(e, nil); runner?.resume()
      case .waiting: break
      }
    }
  }
  ```

  On the Kotlin side, `awaitSwiftGrpcCompletion` stays as is (one `suspendCancellableCoroutine` per event, same as
  legacy's channel receive).
- **Affected**: unary, ping-pong, all streaming (per message), first-response latency.
- **Expected impact**: removes roughly a third of the hand-offs per call; largest in idle-thread shapes.
- **Confidence**: Strong inference on mechanism; magnitude Hypothesis.
- **Risks**: reentrancy (completion invoked while holding no lock — required), ordering (single producer, single
  consumer, sequence preserved by the slot), cancellation (Kotlin cancel must resume a parked runner with an error;
  runner cancellation must fail a pending completion), memory (bounded slot). Same guarantees as today's
  `PullState`.
- **Ownership**: kotlinx-rpc Swift bridge.
- **Experiment**: V2.

### P3 — Deterministic teardown: release the Swift call when the Kotlin flow completes

- **Location**: `GrpcClientTransport.ios.kt` (`finally`), `SwiftGrpcCallAdapter.kt`, `SwiftGrpcCall.swift`.
- **Current**: Swift call, channel, runner, events and retained buffers live until a K/N GC sweep; `deinit` re-enters
  Kotlin from the GC thread; Kotlin request-source objects are GC roots until Swift releases them (F6).
- **C-core**: `finishClose` → `grpc_call_unref` immediately (KRPC-586 comment in `NativeClientCall.kt:85-95`).
- **Excess removed**: GC-deferred lifetimes, two-cycle collection, GC-thread Kotlin re-entry, redundant cancels.
- **Design**: add `@objc func dispose()` to `SwiftGrpcCall` that (idempotently) cancels the task if still running,
  finishes the event slot, and drops `requestSource`, `owner`, `callRunner` references; call it from Kotlin's
  `finally` after the Closed event (and after `cancelAndWait`). Make `deinit` a no-op safety net that does not call
  into Kotlin. Have `KotlinGrpcRequestSource.cancel()` be the single cancellation entry (remove the `defer` in
  `makeStreamingClientRequest` or make the runner's `defer` the only one). Consider `kotlin.native.binary.
  objcDisposeOnMain=false` irrelevant here (objects are passed off-main already).
- **Affected**: concurrency sweeps, stream-concurrency, endurance; indirectly per-byte (buffers freed earlier).
- **Expected impact**: reduces live heap and GC work per call; expected to flatten the c16–c128 collapse if F6 is the
  cause.
- **Confidence**: Observed mechanism; Hypothesis for impact.
- **Risks**: use-after-dispose if a late completion arrives (guard with the slot state); must keep `cancelAndWait`
  semantics for the not-closed path.
- **Ownership**: kotlinx-rpc (Kotlin + Swift bridge).
- **Experiment**: V3.

### P4 — Drop the NIO message buffer immediately after copying into Kotlin

- **Location**: `SwiftGrpcCallTypes.swift` (`SwiftGrpcMessageEvent`), `SwiftGrpcCallAdapter.kt`.
- **Current**: the event retains `GRPCNIOTransportBytes` until GC (F5.1).
- **C-core**: `grpc_byte_buffer_destroy` right after `decode` (`NativeClientCall.kt:533-535`).
- **Excess removed**: retained transport storage, copy-on-write regrowth of the deframer buffer, fresh-page churn.
- **Design**: make `withUnsafeBytes` consume-once — after `body` returns, set `message = nil` (or store the bytes in
  an `Optional` cleared under a small lock); alternatively expose `copyBytes(into: UnsafeMutableRawPointer)` and let
  Kotlin pass a pinned `ByteArray`, still one copy. Keep the Kotlin `Buffer` destination to stay copy-count-equal
  with legacy.
- **Affected**: download-64k/1m/near-4m, server-streaming 64k/1m, download-heavy, balanced-64k.
- **Expected impact**: removes the `current`-only response-path per-byte excess if F5.1 is right (up to −2…−4 µs/KiB).
- **Confidence**: Hypothesis.
- **Risks**: none if single consumption is enforced (`decodeMessage` is the only consumer).
- **Ownership**: kotlinx-rpc Swift bridge.
- **Experiment**: V4.

### P5 — Persistent pull loop in `KotlinGrpcRequestSource` instead of a coroutine per pull

- **Location**: `SwiftGrpcRequestSource.kt:70-101`.
- **Current**: `launch(UNDISPATCHED)` per Swift pull, rendezvous channel, per-call `SupervisorJob` + scope (F4).
- **C-core**: one sender coroutine loops over the request channel and waits on a conflated `Ready` signal.
- **Excess removed**: one coroutine allocation + job registration per message; the extra scope per call.
- **Design**: one coroutine started with the source that loops `for (request in messages) { awaitPull();
  complete(encode(request)) }` where `awaitPull()` waits on a conflated `Channel<Unit>` that `nextRequestWithCompletion`
  signals after storing the completion (exactly the `Ready` pattern of the C-core sender). Completion of the flow
  → `complete(null, null)` on the next pull. Keep the ObjC completion protocol unchanged.
- **Affected**: unary (2 pulls), client streaming, ping-pong.
- **Expected impact**: small per message (allocation + job bookkeeping); larger under concurrency (fewer coroutine
  objects alive).
- **Confidence**: Observed mechanism; low-to-medium magnitude.
- **Risks**: cancellation ordering (the loop must complete a pending completion with an error on cancel — same as
  today's `cancel()`).
- **Ownership**: kotlinx-rpc (`iosMain`).
- **Experiment**: V2 (shared instrumentation).

### P6 — Flatten metadata across the boundary in one crossing

- **Location**: `SwiftGrpcConversions.kt`, `SwiftGrpcCallTypes.swift`.
- **Current**: visitor object + one ObjC↔Kotlin call per entry, three times per call (F8).
- **C-core**: in-process conversion to `grpc_metadata` arrays.
- **Excess removed**: per-entry runtime crossings and thread-state switches; two visitor allocations per call.
- **Design**: encode entries into a single `NSData`/pointer+length (length-prefixed key/value pairs, binary flag)
  produced on one side and decoded on the other in one call; or pass parallel `NSArray<NSString>`s (still one call).
- **Affected**: per-call fixed cost (small).
- **Expected impact**: a few µs per call.
- **Confidence**: Observed; low magnitude.
- **Ownership**: kotlinx-rpc bridge.

### P7 — Remove the request-path memset (and, upstream, the framer copy)

- **Location**: `RawMessageBridge.swift:64-80`; upstream `GRPCMessageFramer.swift`.
- **Current**: `ByteBuffer(repeating: 0, count:)` then fill; framer copies again (F9).
- **C-core**: one copy into slices; frames by reference.
- **Excess removed**: one memset pass per request message (bridge); one copy per message (upstream).
- **Design**: bridge: `var buffer = ByteBufferAllocator().buffer(capacity: length); buffer.writeWithUnsafeMutableBytes
  (minimumWritableBytes: length) { fillBuffer($0.baseAddress, capacity: length) ? length : 0 }`. Upstream: let the
  framer emit the 5-byte header and pass the message buffer through as a separate write when it is large (keep
  coalescing for small messages) — an nio-transport change to `GRPCMessageFramer.nextResult`.
- **Affected**: upload-1m/near-4m, client-streaming 1m, stream-message-overhead 1m/near-4m.
- **Expected impact**: bridge part small (memset of 1 MiB ≈ tens of µs); upstream part moderate for ≥64 KiB messages.
- **Confidence**: Observed; low priority because upload already beats legacy.
- **Ownership**: bridge (memset); grpc-swift-nio-transport (framer).

### Not proposed (see §10.3 for exclusions with the same C-core limitation)

Zero-copy Kotlin↔transport buffers, deeper response prefetch than one message, request batching/buffering beyond one
message, larger flow-control windows, protobuf decode changes, coroutine `flow` wrapper changes.

---

## 7. Prioritised implementation roadmap

| # | Change | Expected contribution | Breadth | Evidence | Effort | Risk | Owner | Depends on |
|---|---|---|---|---|---|---|---|---|
| 1 | **P0** verify/force optimised Swift build | Unknown, potentially dominant across all families | all | Strong inference | S (verify) / M (KGP workaround) | low | kotlinx-rpc build; KGP upstream | — |
| 2 | **P1** disable client flush coalescing | −50…−100 µs per RPC/round trip in sequential shapes | unary c≤8, ping-pong, TTFR | Strong inference | XS | low (throughput at high c) | kotlinx-rpc | re-measure after 1 |
| 3 | **P2** direct single-slot event hand-off (no Task per event) | large share of the ≈250 µs/call bridge fixed cost; per-message hop reduction | unary, ping-pong, all streaming | Strong inference | M | medium (state machine) | Swift bridge | 1 |
| 4 | **P3** deterministic teardown + single cancel path | concurrency collapse (if F6), memory, GC | c≥16, stream-concurrency, endurance | Hypothesis with Observed mechanism | M | medium | Kotlin + Swift bridge | 1 |
| 5 | **P4** release NIO buffer after copy | response-path per-byte excess (if F5.1) | large downloads | Hypothesis | S | low | Swift bridge | 1 |
| 6 | **P5** persistent request pull loop | per-message allocations; concurrency | upload shapes, unary | Observed mechanism | S | low | `iosMain` | 3 |
| 7 | **P6** one-crossing metadata | a few µs/call | all | Observed | S | low | bridge | — |
| 8 | **P7** request memset removal; framer pass-through (upstream) | per-byte upload | ≥64 KiB uploads | Observed | S / M (upstream) | low / medium | bridge / nio-transport | — |

Grouping required by the task:

- **High-confidence immediate changes**: 1 (verification first), 2, 3, 6, 7, 8-bridge part.
- **Medium-confidence changes requiring targeted instrumentation first**: 4, 5.
- **Upstream/library investigations**: F10 (per-write event-loop hop and per-element flush in `NIOAsyncChannel`;
  `withTaskGroup` + child task per RPC in grpc-swift; Network.framework vs Posix transport), 8-upstream.
- **Hypotheses not to implement before profiling**: K/N GC tuning (`kotlin.native.binary.gc`), limiting the Swift or
  Kotlin thread pools, autorelease-pool draining on Kotlin worker threads, moving Kotlin resumes to a dedicated
  dispatcher.

**Likely floor.** Even with all bridge changes, `current` keeps the grpc-swift/NIO structure (`swift` vs `legacy`:
≈2× on sequential unary after coalescing is removed, ≈1.2–1.5× on small-message download; at parity or better on
uploads and at c≥16). Unless F10 items change upstream, sequential-latency parity with C-core is unlikely; parity on
throughput-oriented and upload shapes is plausible.

---

## 8. Validation plan (macOS/iOS simulator, same pinned server)

Each experiment names the hypothesis it tests, the smallest benchmark set, and the instrument.

| ID | Tests | Method | Benchmarks | Pass/fail signal |
|---|---|---|---|---|
| V0 | F1 | Inspect the KGP ld dump (`build/…/ldDump/arm64.ld`) and DerivedData product paths (`Debug-iphonesimulator` vs `Release-iphonesimulator`); `nm`/`otool -l` on the linked bridge objects; then A/B with `-configuration Release` injected via `additionalXcodeArgs` | `unary-latency`, `download-1m`, `1k-c64`, `server-streaming 64k` | Debug path present; A/B deltas quantify F1's share |
| V1 | F2/P1 | A/B `flushCoalescing = nil` in the bridge **and** in `swift-client` | `unary-latency`, `bidi-ping-pong`, `unary-concurrency-sweep` c1/c32/c128 | −50…−100 µs mean at c1; swift `upload-64k` ≥ `upload-1k` anomaly disappears |
| V2 | F3/F4/P2/P5 | Instruments "Swift Tasks" + "Swift Concurrency" to count Task creations per RPC; `os_signpost` counters in the bridge for ObjC crossings and completion invocations; Time Profiler on `swift_task_create`, `swift_continuation_*`, `objc_msgSend` from Kotlin | `unary-latency`, `bidi-ping-pong`, `server-streaming 1k`, `stream-concurrency c16` | Tasks/RPC drop from ≥5 to 2; per-message hops fall; c16 p50 approaches legacy's 15 µs |
| V3 | F6/P3 | `-Xruntime-logs=gc=info` and GC signposts (Instruments) before/after; Allocations instrument for live `SwiftGrpcCall`/`KotlinGrpcRequestSource` counts over time; count ObjC→Kotlin calls on the GC thread | `unary-concurrency-sweep` empty c8→c128, `stream-concurrency` c16 | GC cycles/pause time per 1000 calls drop; minimum latency stops growing with concurrency |
| V4 | F5/P4 | VM Tracker / Allocations: `ByteBuffer` storage allocations and page faults per message; malloc stack traces from `GRPCMessageDeframer.append`; A/B consume-once event | `download-64k/1m/near-4m`, `server-streaming 64k/1m` | per-KiB excess vs swift falls toward 0; deframer regrowth allocations disappear |
| V5 | F7 | Thread State / System Trace: runnable threads vs cores, context switches per RPC; A/B with `Dispatchers.Default.limitedParallelism(n)` in the harness only as a diagnostic | `unary-concurrency-sweep` c16–c128 | if throughput recovers with fewer Kotlin threads, over-subscription is a factor |
| V6 | F10 | Build `swift-client` with `HTTP2ClientTransport.Posix`; Time Profiler on `eventLoop.execute`, `NIOAsyncWriter.yield` | `unary-latency`, `bidi-ping-pong` | quantifies Network.framework vs socket transport share of `swift` − `legacy` |
| V7 | A2 | Build the legacy client with Kotlin 2.4.0 (same artifact) | `unary-latency`, `download-1m` | isolates the K/N version confound |

Kotlin/Native metrics to record in all runs: allocation count/bytes and GC pause totals (`gc=info`), Dispatchers
worker count; Swift: task count, continuation count, retain/release rate (Allocations "record reference counts");
system: CPU utilisation per pool thread, context switches.

---

## 9. Open questions and residual uncertainty

1. Is the Swift stack in the Kotlin binary really Debug (V0)? If so, most `current`-vs-`swift` magnitudes in this
   report must be re-derived after re-running with optimisation.
2. What is the actual cost of one Kotlin↔Swift hand-off on the simulator when threads park (unary) versus when they
   spin (streaming)? The CSV bounds it between ≈3 µs and ≈20 µs but cannot separate the ≈17 hops.
3. Which of F6/F7/F1 drives the concurrency collapse? The minimum-latency growth is the strongest discriminator: STW
   GC would show as periodic plateaus in a latency time series; over-subscription as uniform inflation.
4. Mechanism of the response-path per-byte excess (F5): retained buffers vs debug build vs K/N 2.4 runtime.
5. How much of `swift` − `legacy` is Network.framework (TransportServices) versus grpc-swift core (V6)?
6. Does the C-core client on the simulator use CFStream/Network-backed endpoints or BSD sockets? This affects the
   fairness of comparing transport layers.

---

## 10. Evidence appendix

### 10.1 Benchmark analysis method

`analyze.py` pairs rows by `(benchmark, case)`, uses `messages_per_second` for streaming families and
`calls_per_second` otherwise, and prints (faster ÷ slower) throughput ratios, `current ÷ other` latency ratios, mean
deltas, MiB/s, and per-implementation scaling relative to c1. See `ratios.md`. Per-KiB figures in §3.2 are
`(mean_at_size − mean_at_1KiB)` differences between implementations divided by payload KiB.

### 10.2 Source references (this repository, HEAD `25b4330b`)

| Topic | Location |
|---|---|
| Event boundary | `grpc/grpc-client/src/commonMain/kotlin/kotlinx/rpc/grpc/client/internal/GrpcClientTransport.kt:24-51` |
| Kotlin call wrappers | `…/commonMain/…/suspendClientCalls.kt:112-228`; pre-split `origin/main:…/suspendClientCalls.kt:213-343` |
| iOS transport | `…/iosMain/…/GrpcClientTransport.ios.kt:30-107` |
| iOS channel/startCall | `…/iosMain/…/ManagedChannel.ios.kt:81-110, 119-168` |
| Pull adapter | `…/iosMain/…/SwiftGrpcCallAdapter.kt:35-75`; `SwiftGrpcContinuation.kt:38-59` |
| Request source | `…/iosMain/…/SwiftGrpcRequestSource.kt:28-122` |
| Buffers | `…/iosMain/…/SwiftGrpcBuffers.kt:25-87` |
| Metadata conversion | `…/iosMain/…/SwiftGrpcConversions.kt:33-78` |
| Non-iOS bridge | `…/nonIosMain/…/GrpcClientTransport.nonIos.kt:33-162` |
| C-core call | `…/nonIosNativeMain/…/NativeClientCall.kt:76-640` (`request` 510-550, `sendMessage` 599-626, `finishClose` 267-285) |
| C-core CQ | `grpc/grpc-core/src/nonIosNativeMain/kotlin/kotlinx/rpc/grpc/internal/CompletionQueue.kt:134-175, 226-231` |
| C-core buffer copies | `…/nonIosNativeMain/…/internal/utils.native.kt:85-137` |
| `Ready` | `grpc/grpc-core/src/commonMain/kotlin/kotlinx/rpc/grpc/internal/suspendUtils.kt:42-60` |
| Swift bridge | `grpc/grpc-swift/Sources/GrpcSwiftBridge/SwiftGrpcClient.swift:49-149`, `SwiftGrpcCall.swift:20-168`, `SwiftGrpcCallTypes.swift:68-223`, `internal/CallRunner.swift:6-150`, `internal/RawMessageBridge.swift:7-82`, `internal/Continuation.swift:18-35` |
| Pins | `grpc/grpc-swift/Package.swift`, `Package.resolved`; `tests/grpc-benchmarks/swift-client/Package.resolved` |
| Harness | `tests/grpc-benchmarks/shared-kotlin-client/src/commonMain/kotlin/kotlinx/rpc/grpc/benchmarks/client/{CallBenchmark,StreamingBenchmark,StreamingBenchmarks,UnaryBenchmarks,BenchmarkCli}.kt`; `tests/grpc-benchmarks/swift-client/Sources/SwiftGrpcBenchmarkClient/{Benchmark,BenchmarkCLI,UnaryBenchmarks,StreamingBenchmarks}.swift`; `run-all.sh`; `*/run.sh`; `swift-client/build.sh` |
| Protobuf decode copy | `protobuf/protobuf-lite/src/nativeMain/kotlin/kotlinx/rpc/protobuf/internal/WireDecoder.native.kt:179-192` |
| C-core build mode | `native-deps/grpc-c-prebuilt/.bazelrc:11`, `build_archives.py:17` |

### 10.3 Dependency permalinks (exact pinned revisions)

- grpc-swift-2 `ac33066e`: `Sources/GRPCCore/GRPCClient.swift` (L395-423 `bidirectionalStreaming`),
  `Sources/GRPCCore/Call/Client/Internal/ClientRPCExecutor.swift`, `ClientRPCExecutor+OneShotExecutor.swift`
  (L96-133), `ClientStreamExecutor.swift` (L32-108, 173-251), `ClientResponse+Convenience.swift`,
  `Sources/GRPCCore/Streaming/RPCWriter.swift`, `RPCWriter+Closable.swift`, `Internal/RPCWriter+Map.swift`,
  `Internal/UncheckedAsyncIteratorSequence.swift`.
  `https://github.com/grpc/grpc-swift-2/blob/ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7/<path>`
- grpc-swift-nio-transport `eaad084d`: `Sources/GRPCNIOTransportCore/Client/HTTP2ClientTransport.swift`
  (Config defaults L52-54, 125-127, 190-192, 262-264), `Client/Connection/GRPCChannel.swift` (L237-279 `withStream`,
  320-362 `makeStream`), `Client/Connection/Connection.swift` (L228-283, 419-486),
  `Client/GRPCClientStreamHandler.swift` (L60-100, 218-233, 268-322), `GRPCStreamStateMachine.swift` (L948-1085
  client send, 1231-1380 client receive headers, 1380-1535 receive/next), `GRPCMessageFramer.swift` (L58-119),
  `GRPCMessageDecoder.swift` (L48-147), `GRPCNIOTransportBytes.swift`, `Internal/FlushCoalescingHandler.swift`
  (L99-129), `Internal/NIOChannelPipeline+GRPC.swift` (`configureGRPCClientPipeline`),
  `Sources/GRPCNIOTransportHTTP2TransportServices/HTTP2ClientTransport+TransportServices.swift`.
  `https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/<path>`
- swift-async-algorithms `3da39bbc`: `Sources/AsyncAlgorithms/Channels/AsyncThrowingChannel.swift`,
  `ChannelStorage.swift` (L27-142), `ChannelStateMachine.swift`.
  `https://github.com/apple/swift-async-algorithms/blob/3da39bbc4e687d4192af7c9cf4eab805745a0b9c/<path>`
- swift-nio `a931f2c1`: `Sources/NIOCore/AsyncChannel/AsyncChannel.swift` (L60-65 watermarks),
  `AsyncChannelHandler.swift` (L290-330 inbound delivery, 362-368 `produceMore`, 397-416 `didYield` hop, 487-490
  write+flush per element), `AsyncChannelOutboundWriter.swift` (L117-137), `Sources/NIOCore/AsyncSequences/
  NIOAsyncWriter.swift` (L536-660), `NIOThrowingAsyncSequenceProducer.swift` (L567-700).
  `https://github.com/apple/swift-nio/blob/a931f2c1de8dd49381ce3bf2e279d033f68d8865/<path>`
- swift-nio-http2 `0f3e54e2`: `Sources/NIOHTTP2/InboundWindowManager.swift` (window update at half window),
  `HTTP2StreamChannel.swift`.
  `https://github.com/apple/swift-nio-http2/blob/0f3e54e29c944c2e835ad52159da7d9e1c94ac69/<path>`
- Kotlin/Native runtime `v2.4.0`: `kotlin-native/runtime/src/main/cpp/ObjCExport.mm` (L286-289
  `convertUnmappedObjCObject`, 448 block conversion, 462-501 `refToObjC`), `ObjCInterop.mm` (L76-92
  `allocWithZoneImp`, 355-373 retain/release guards), `kotlin-native/runtime/src/mm/cpp/ExtraObjectData.cpp`
  (L53-65), `ExternalRCRef.cpp` (L138-171).
  `https://github.com/JetBrains/kotlin/blob/v2.4.0/<path>`
- KGP `v2.4.0`: `libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/plugin/mpp/
  apple/swiftimport/XcodebuildDefFileWorkAction.kt` (L105-122), `ConvertSyntheticSwiftPMImportProjectIntoDefFile.kt`
  (L82-95), `XcodebuildDefFileUtils.kt` (L77-130), `SwiftImportSetupAction.kt`.
- kotlinx.coroutines `1.10.2`: `kotlinx-coroutines-core/native/src/Dispatchers.kt` (L8),
  `MultithreadedDispatchers.kt` (L78-142).
- Kotlin docs (kotlin-web-site `master`): `docs/topics/native/native-memory-manager.md` (CMS GC, GC thread,
  `-Xruntime-logs=gc=info`, signposts), `docs/topics/native/native-arc-integration.md` ("Deinitializers": special GC
  thread; two GC cycles for Swift/Kotlin chains).

### 10.4 Rejected hypotheses

| Hypothesis | Why rejected |
|---|---|
| Different HTTP/2 transports between `current` and `swift` | Both use `HTTP2ClientTransport.TransportServices` with default HTTP/2 config (Observed). |
| Compression/decompression on the bridge path | Server never compresses; no decompressor is created unless `grpc-encoding` is present (Observed). |
| Harness asymmetry (warmups, counts, channel reuse, validation) | Identical case tables and lifecycle (Observed). |
| Protobuf runtime or generated-code drift between legacy and current | `protobuf/` and `protoc-gen/` unchanged from `f2f0aad2` to HEAD (Observed). |
| The new common event boundary (`5e83975a`/`c26e6848`) causes the iOS regression | iOS never used the pre-split path; the boundary adds only Kotlin-side allocations (Observed). |
| Kotlin-side per-byte copy in `copySwiftBytes` explains the response-path excess | Legacy's `toKotlin()` performs the same segment-wise copy with the same kotlinx-io version (Observed); remaining difference would be the K/N version (A2). |
| Unary routed through `bidirectionalStreaming` is a bridge-specific penalty | grpc-swift's own `unary` uses the same executor path (Observed). |

### 10.5 Candidate optimisations excluded because C-core has the same limitation

| Idea | C-core equivalent | Reason excluded |
|---|---|---|
| Zero-copy Kotlin↔transport message boundary (response or request) | `grpc_slice_from_copied_buffer` / `toKotlin()` copy | same copy exists in C-core |
| Pipelining/buffering more than one request message in the bridge | `NativeClientCall.ready` allows one in-flight message (KRPC-192) | would exceed C-core behaviour (grpc-swift already pipelines below the bridge) |
| Prefetching more than one response message (`request(n>1)`) | legacy calls `request(1)` after each emit | same depth |
| Increasing HTTP/2 flow-control windows or frame size | C-core defaults are smaller (64 KiB initial window with BDP) | NIO already ≥ C-core |
| Avoiding the protobuf `readBytes` `ByteArray` copy | identical native decoder in both | shared cost |
| Removing `flow {}`/interceptor wrappers in `suspendClientCalls.kt` | identical in legacy | shared cost |
| Replacing `suspendCancellableCoroutine` per event with a channel | legacy uses a channel receive per event (one suspension) | equal count |
| Skipping the `Headers`/`Closed` flow events | legacy completes futures on the same callbacks | negligible, and needed by interceptors |

---

Produced by Air Automations. Name: kotlin-rpc gRPC Swift Performance Analysis / Run: https://air.jetbrains.cloud/org/05cf1a7f-6ab5-713b-abd3-29d0c8a05e2d/automations/eb4a193c-e91a-4544-b86b-e92182612017?run=c44a08a0-9eb5-4c29-9c6c-a5e03a26985f
