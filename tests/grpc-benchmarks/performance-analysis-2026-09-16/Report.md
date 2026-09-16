# grpc-swift-2 versus C-core: performance parity investigation

## 1. Executive summary

**The strongest actionable explanation for sequential small-call latency is a transport configuration difference, compounded by avoidable bridge scheduling.** Both Swift clients inherit grpc-swift-nio-transport 2.9.2's default flush coalescing: a requested flush can wait for a 100 µs timer unless 64 KiB accumulates or writability changes. C-core's ordinary, non-buffer-hinted send schedules a write without that deliberate timer. This is **Observed** in the exact pinned sources, not a measured attribution of the whole regression. Disable that extra delay first in an Apple A/B experiment. [K5] [H3] [D1] [D2] [C1]

**Measured:** the standalone empty unary mean is 432.015 µs current, 165.205 µs legacy, and 289.419 µs direct Swift: current/legacy **2.615×**. But the longer empty-c1 sweep is 404.479/164.086/363.237 µs. These are separate cases in one run, not repeat measurements with identical sample counts. The observed current–Swift mean difference ranges from 41.242 µs in empty-c1 to 142.596 µs in standalone unary; do not report either as a universal bridge cost. Small payload unary cases more consistently leave about 47–57 µs between current and Swift. Both Swift paths being slow at c1, and direct Swift becoming faster than legacy at higher concurrency, fit a delay that bulk traffic amortizes. [Q]

**Observed bridge excess:** every successful unary normally creates one Swift call task and three additional Swift event-pull tasks, with three fresh channel iterators and three Kotlin completion adapters. A request collector plus two per-pull Kotlin coroutine launches (message and EOF) are used even for one request. Pre-split C-core uses one sender coroutine and `requests.single()` for unary, a one-message response channel, and direct metadata/status callbacks. Both paths serialize and copy at the Kotlin/backend boundary; eliminating those equivalent copies is explicitly excluded. [K1–K10] [B1]

**Measured exceptions are central to the explanation:** current beats legacy in all supplied client-streaming cases (1.427–2.324× throughput), every full-duplex case (1.113–3.106×), every fixed-byte upload case (1.353–2.391×), and every small-message stream-concurrency case (1.046–1.310×). It also slightly beats legacy for unary download-1m (1.011×). This is not an across-the-board inferior backend. Buffered writes can be cheaper than C-core's message readiness/completion gating, and independent producer/consumer pipelines hide round-trip scheduling. [Q] [K8] [D4–D6]

Three regimes deserve different remedies:

1. **Small unary and ping-pong:** flush delay first, then eliminate redundant bridge event tasks and single-request pull machinery. The direct Swift ping-pong mean already accounts for most of the observed absolute difference from legacy; this is localization evidence, not a causal decomposition.
2. **High-concurrency unary:** current loses much more to direct Swift and has very large tails. Bridge task/allocation traffic is real; its contribution versus Kotlin compiler/runtime changes and GC is **Unknown**. Reduce proven redundant work, instrument the stalls, and retain the same Kotlin compiler in the comparison.
3. **Large responses:** direct Swift itself trails legacy in server-streaming (0.815–0.839× throughput), while current is only another 6–8% below Swift. Inspect receive consolidation, Network.framework-to-NIO copying, and actual HTTP/2 frame/window behavior. Large unary means also include rare hundreds-of-milliseconds stalls; they cannot be explained by a fixed byte-copy multiplier alone.

The highest-priority implementation sequence is: expose/select no delayed coalescing for latency parity; make request-source completion/cleanup allocation-light and once-only; replace task-per-event pulling with a bounded completion mailbox; specialize the single-request bridge. Upstream byte-path work follows profiling. No change was implemented in production or validated on Apple platforms, and full parity cannot be promised.

## 2. Inputs, revisions, and limitations

### Checkout and history

Initial branch: **ios-grpc-swift-async**, clean working tree. An initial read-only `git status --short --branch`, HEAD lookup, main lookup, and merge-base lookup found a shallow checkout with no local main. History and remote refs were fetched; the active branch and checkout were never switched. Production files remained unchanged.

| Input | Revision / fact |
|---|---|
| Analyzed HEAD | `335cf68d7fd7458405681e8c13f325522a3b452a` |
| Actual fetched origin/main | `1750c217df9ad2cdd2b9134ab01918652c0e3fef` |
| Actual merge base | Same `1750c217…` |
| Supplied historical main | `60cc5663a71f19ea974776921a884b7f0b74afff`, available and inspected; not substituted for current main |
| Published legacy tag | `0.11.0-grpc-189`, resolves to `a48afb14112025a0e7666de4994ff14148a16bbf` |
| Actual split commits | `2ba324d8` (event boundary), `f2856b26` (callback bridge move); supplied `5e83975a`/`c26e6848` are not the identities in this branch's log |
| Current Kotlin / coroutines | Catalog 2.4.20 / 1.10.2 |
| Legacy harness Kotlin / coroutines | Standalone catalog 2.3.0 / 1.10.2 |
| Current / legacy C-core shim | 1.81.1-3 / 1.81.1-2; upstream gRPC 1.81.1 |

`git log`, rename-aware diffs, `git show` of main and the release tag, and blame of the bridge's cleanup implementation were used. NativeClientCall is unchanged by the source-set move. Main and release-tag buffer code establish the authoritative baseline even though today's non-iOS path has the new event layer. The report commit, if present, is a deliverable after the analyzed HEAD; it is not the measured revision. The supplied CSV does not record its originating Git revision, compiler build, date, or executable hashes. **Unknown:** whether the benchmark binaries were built from this exact HEAD. In particular HEAD has a later Kotlin upgrade. Source explanations are conditional on the benchmark having the inspected behavior. [B1–B3] [K14]

### Exact dependency pins

Six Swift dependencies were cloned into temporary storage and their checked-out hashes compared with the lockfile; all matched. Upstream C-core v1.81.1 was inspected at the revision below. This establishes source behavior, not proof of the published binary's compilation flags or selected endpoint engine.

| Dependency | Version | Revision |
|---|---|---|
| grpc-swift-2 | 2.4.3 | `ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7` |
| grpc-swift-nio-transport | 2.9.2 | `eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6` |
| swift-nio | 2.102.0 | `a931f2c1de8dd49381ce3bf2e279d033f68d8865` |
| swift-nio-http2 | 1.46.0 | `0f3e54e29c944c2e835ad52159da7d9e1c94ac69` |
| swift-async-algorithms | 1.1.5 | `3da39bbc4e687d4192af7c9cf4eab805745a0b9c` |
| swift-nio-transport-services | 1.28.0 | `67787bb645a5e67d2edcdfbe48a216cc549222d5` |
| gRPC C-core | 1.81.1 | `e84a8a2f04095f2772ba42a4abccde4f9243e75b` |
| Direct Swift grpc-swift-protobuf | 2.4.0 | `b05885fa9bdd88f1eab2e7162f1ee81340b0da33` |
| Direct Swift swift-protobuf | 1.38.1 | `55d7a1cc5666b85c13464aea1c4b4a90feccb4c8` |

The direct Swift lockfile agrees on the relevant shared transport/core/NIO revisions. Its swift-log is 1.15.1 (`9c6fb142…`) versus bridge 1.15.0 (`3ffafb97…`); no evidence ties this difference to the measurements. The bridge declares Swift tools 6.3, direct Swift tools 6.1; both select Swift language mode 6. Tools-version declarations are minimum/package semantics, not evidence of two actual compiler executables. [K12] [H4]

### CSV identity and calculation scope

Input identity: the **Benchmark Data CSV in this automation prompt**, 156 rows, 52 unique benchmark/case combinations, three implementations, ten families. Treat it as one run. Schema: benchmark/case/implementation/platform/target; warmup_calls/calls/concurrency/request_bytes/response_bytes; elapsed_seconds/calls_per_second/application_bytes_per_second; latency_min_us/mean/p50/p90/p95/p99/p999/max; request_messages/response_messages/messages_per_second/time_to_first_response_us/final_response_latency_us. Unary streaming-specific fields are blank. Payload bytes count application body bytes, not protobuf or HTTP/2 bytes.

[Q] preserves **selected fields**, explicitly not the original complete CSV: all 156 throughput values, plus mean/p50/p99/p999/max for 21 important paired cases. The script generates all 52 throughput triplets and 105 latency-metric triplets and checks uniqueness, positive values, percentile ordering, and ratio identities. SHA256 of the selected-input file: `7be16924ad9631f4d48ddb13499af912664427a08ae940163747144c241b76c2`. This is not a hash of the original prompt CSV. Other streaming statistics quoted below were read directly from the prompt.

Linux constraint: no iOS/Swift builds, benchmark execution, Instruments traces, or runtime profiles were attempted. **Measured** means supplied CSV only; **Observed** means inspected source; **Strong inference** means several consistent source/data observations; **Hypothesis** requires experiment; **Unknown** means evidence does not distinguish causes. No confidence intervals or significance claims are possible.

### Benchmark comparability audit

**Observed:** current and legacy compile the same shared Kotlin harness. `CallBenchmark.run` prepares a request/call, warms up, then times worker execution; `executeCalls` launches `async(Dispatchers.Default)` workers, even at c1. Request payload construction is outside measured unary time and reused; serialization, RPC completion, deserialization, and response-size validation are inside. Warmup is on the same client/channel. Each case gets a new channel; creation/shutdown are outside the case elapsed interval. Swift has a separately implemented task-group harness with the same case parameters, pattern bytes, response-size checks, and per-case channel lifetime. [H1–H3]

`run-all.sh` runs current, legacy, Swift sequentially with identical CLI arguments, the same selected simulator, and matching CSV headers. It does not randomize order, collect machine load, verify the running server's build hash, or make these separate processes simultaneous controls. CSV's `ios` versus `ios-simulator-arm64` is a labeling difference consistent with runners targeting simulator arm64; the CSV itself does not prove physical-device identity. [H5]

All three use plaintext localhost:50051. The server build pins gRPC `b8f09d9168d856020d236bd32f39195f7b5aa2cf`; the runner defaults to the asynchronous C++ server and 32 MiB limit. The caller must start it separately and can override defaults. Protos are shared canonical benchmark definitions. Full-duplex is finite StreamingCall echo, not an independently generating StreamingBothWays server. StreamingFromServer is unbounded and cancelled after N responses. All near-4m bodies fit below the legacy 4 MiB receive cap. [H6]

Current and legacy runners select release Kotlin executables; direct Swift explicitly invokes release SwiftPM. **Unknown:** the actual generated Kotlin SwiftPM import build commands/optimization flags for the bridge, assertions and logging environment in the measured binaries, actual compiler/runtime version, and server flags. The bridge Gradle declaration imports a local Swift package and does not explicitly show an optimization flag; a release Kotlin executable alone does not prove every imported Swift dependency was optimized. There is no source evidence of per-message logging in the inspected happy bridge path. Do not assert debug/release parity beyond what the scripts establish. [H4] [K13]

**Observed streaming timing detail:** unlike unary preparation, streaming `executeStream` constructs request payloads and sample arrays after the outer elapsed clock starts, then starts its local stream clock. The fixed-total-byte case may construct a final remainder request as well. Thus total streaming throughput includes payload preparation/worker setup that per-write samples and time-to-first-response may exclude. At near-4m, this can materially affect short-run current/Swift comparisons; it is not evidence of a backend-only copy bottleneck. Current and legacy share this code, but their runtimes differ. [H2–H3]

Remaining confounds: Kotlin 2.4.20 versus 2.3.0 defaults; current versus published generated protobuf/marshaller code; SwiftProtobuf versus Kotlin protobuf; native object lifetimes/GC versus Swift ARC; Objective-C interop; compiler-specialized async code; Swift's ping-pong AsyncStream token gate versus Kotlin Channel(1); warmup message counts insufficient to establish identical adaptive transport state; finite low-count large-message runs; cancellation cleanup in streaming elapsed time. These make `current − swift` a localization clue, not a pure bridge measurement.

## 3. Quantitative benchmark analysis

Full case tables are in [Q]. Below C=current, L=legacy, S=direct Swift. Throughput ratios above 1 favor the numerator; latency ratios above 1 favor the denominator. Never average these families together.

| Workload | C/L throughput | C/S throughput | S/L throughput | Interpretation |
|---|---:|---:|---:|---|
| Empty unary latency case | 0.383 | 0.670 | 0.571 | Both Swift paths slow sequentially |
| 1k unary throughput c16 | 0.782 | 0.587 | 1.332 | Direct Swift wins; current still loses |
| Empty unary c128 | 0.521 | 0.487 | 1.070 | Current scaling/tails need separate explanation |
| 1k unary c128 | 0.593 | 0.367 | 1.617 | Not an intrinsic Swift throughput ceiling |
| Ping-pong empty / 1k | 0.429 / 0.445 | 0.912 / 0.881 | 0.470 / 0.505 | Same-stream round trips still slow without per-call setup |
| Client stream 1k / 64k / 1m | 2.324 / 1.427 / 2.200 | 1.009 / 0.678 / 0.536 | 2.304 / 2.104 / 4.104 | Upload throughput contradicts universal scheduling penalty |
| Server stream 1k / 64k / 1m | 1.228 / 0.749 / 0.785 | 0.648 / 0.919 / 0.935 | 1.897 / 0.815 / 0.839 | Small-response bridge cost; large-response backend cost |
| Full duplex balanced-1k | 3.106 | 0.921 | 3.374 | Pipelining changes the outcome dramatically |
| Full duplex balanced-64k | 1.207 | 1.225 | 0.985 | Current can beat even direct Swift |
| Full duplex upload / download heavy | 1.864 / 1.113 | 1.158 / 1.100 | 1.610 / 1.012 | No full-duplex regression in this run |

### Unary latency, throughput, and concurrency

Standalone unary mean ratios C/L, C/S, S/L are 2.615, 1.493, 1.752; p50 ratios 2.747, 1.464, 1.877. Its p99 C/L is only 1.515 and S/L 0.828: legacy is not better at every percentile. This already cautions against fitting one multiplier.

Empty c1→c128 throughput: current 2,472→10,559, legacy 6,093→20,267, Swift 2,753→21,677 units/s. Current peaks near c32 (11,061); Swift continues scaling. At 1k, current 2,264→8,694, legacy 5,186→14,657, Swift 2,634→23,694. Direct Swift exceeds legacy from 1k-c8 onward, reaches ~24k at c64, while current remains below 9k. Every c1/c2/c4/c8/c16/c32/c64/c128 point appears in [Q]; there is no assumption of monotonic legacy behavior (its c128 jump is real in this run and unexplained).

High-concurrency mean and median diverge. At 1k-c128 current mean/p50/p99 is 14.717/6.209/359.773 ms; legacy 8.718/6.234/99.240 ms; Swift 5.400/5.274/7.775 ms. Current's median is essentially legacy parity (0.996×), while mean is 1.688× and p99 3.625×. For the standalone c16 throughput case current p50 is also slightly better than legacy (1,160 versus 1,173 µs), but p99.9 is 403 ms versus 118 ms versus Swift 2.02 ms. **Strong inference:** rare pauses materially impair current throughput; **Unknown:** whether they are GC, interop reclamation, scheduler starvation, server interference, or another source. Concurrent tail samples may overlap in wall time, so summing them does not estimate lost throughput.

### Payload and direction sweeps

| Unary case | C/L throughput | C/S throughput | S/L throughput | C/L median latency |
|---|---:|---:|---:|---:|
| symmetric-64b | 0.413 | 0.874 | 0.473 | 2.615 |
| symmetric-1k | 0.450 | 0.872 | 0.516 | 2.622 |
| symmetric-64k | 0.847 | 0.670 | 1.266 | 1.167 |
| symmetric-1m | 0.881 | 0.410 | 2.150 | 0.836 |
| symmetric-near-4m | 0.704 | 0.390 | 1.803 | 0.810 |
| upload-1k | 0.451 | 0.891 | 0.506 | 2.477 |
| upload-64k | 0.783 | 0.532 | 1.471 | 1.288 |
| upload-1m | 0.809 | 0.197 | 4.101 | 0.957 |
| upload-near-4m | 0.787 | 0.179 | 4.407 | 0.999 |
| download-1k | 0.412 | 0.869 | 0.474 | 2.700 |
| download-64k | 0.607 | 0.769 | 0.790 | 1.635 |
| download-1m | 1.011 | 0.567 | 1.784 | 1.131 |
| download-near-4m | 0.566 | 0.395 | 1.433 | 1.037 |

**Measured threshold clue:** direct Swift upload-64k has *lower* mean than upload-1k, 223.971 versus 383.881 µs, despite more data. Current also falls, 420.777 versus 431.035 µs (median 330 versus 420). An ordinary per-byte-copy model does not predict that reversal; crossing the 64 KiB flush threshold does. At download-64k the request is still only 64 bytes and Swift remains 585.814 µs versus legacy 462.705. This directional difference supports testing request-flush delay before treating large-response latency as entirely a deserialization problem.

**Measured tail warning:** symmetric-1m current mean 7.751 ms exceeds its p99 5.634 ms; max is 403 ms. Its median 3.954 ms is *better* than legacy 4.728 ms. Near-4m symmetric median is also better (14.744 versus 18.196 ms), while mean is worse (37.067 versus 26.097 ms). Download-near-4m medians are 10.421/10.050/10.101 ms, almost aligned, despite means 25.973/14.691/10.252 ms. At 50 samples, extreme percentiles select individual outliers; p99 and p99.9 may equal maximum. One 393.356 ms upload-1m current call occupies about 55.5% of its 708.935 ms sequential elapsed interval. It is invalid to label the whole current/Swift throughput difference a byte-copy cost.

### Streaming families and what their latency columns mean

Client-streaming emit/write duration is not network delivery acknowledgement. Current's 1k mean/p50/p99 write sample is 23.846/8.333/708.958 µs; direct Swift 24.110/0.916/29.958 µs. Near-equal total throughput coexists with very different buffering/suspension distributions. Final-response latencies for 1k/64k/1m are current 1,513/579/1,440 µs, legacy 250/397/2,941, Swift 1,481/451/2,620. This is not a uniform current teardown penalty. [H2]

Server-streaming latency is inter-arrival time, with the first sample including startup. Large-response current application throughput is ~404–423 MB/s (decimal), Swift ~440–453 MB/s, legacy ~539–540 MB/s. That direction-specific plateau motivates transport receive-path inspection. Small-message current remains faster than legacy but is only 0.648 of Swift; response event scheduling is a plausible additional limiter.

Ping-pong means are empty 329/141/300 µs and 1k 353/157/310 µs. Subtracting means descriptively gives current–Swift ~29/42 µs and Swift–legacy ~159/154 µs. Per-call setup cannot explain this because the stream persists. Timer delay and per-message request/response scheduling can. Kotlin and Swift token-gating differences prevent exact attribution.

Full-duplex inter-arrival means must not be called round-trip latency: request production and response consumption overlap. Current balanced-1k wins 3.106× versus legacy even with first response at 101 ms versus legacy 327 µs and Swift 21 ms. For download-heavy, first response is current 22 ms, Swift 28 ms, legacy 554 µs, yet current's total throughput wins. The echo server and buffered production can delay first visibility while keeping sustained throughput high. This disconfirms “more tasks must slow every message” and makes first-response anomalies a separate diagnostic, not a general startup estimate.

Fixed-total-byte uploads all transfer **64 MiB**. Rates C/L are 1.744, 1.353, 2.391, 1.570 for 1k/64k/1m/near-4m; C/S 1.000, 0.604, 0.528, 0.412. Current elapsed falls from 1.605 s to 0.127 s to 0.056 s, then rises to 0.084 s. Near-4m is **16 × (4 MiB−1 KiB) plus 16 KiB**, 17 messages, not 17 full-size messages. Application goodput therefore must use the recorded byte count. This supports per-message amortization but not a claim that the bridge dominates small-upload throughput: current and Swift are equal at the smallest size. The largest case is short and tail-sensitive; in current its sum of timed emits is only about 55 ms while total elapsed is 84 ms. Timing not represented by individual emit samples matters too.

Stream concurrency uses 8,192 **total** responses distributed across 1/2/4/8/16 streams, not that many responses per stream. Current stays ~56.5–63.2k units/s; Swift ~90.8–103.9k; legacy ~46.2–60.0k. C/S is consistently 0.60–0.63 while C/L is always above 1. Many streams do not erase current's small-message delivery overhead, but this benchmark does not establish a parity deficit against legacy. Increasing p99.9 and first-response averages also reflect shared-channel scheduling and cancelled unbounded streams, not unary request latency. The README contains an older prose reference to stopping at 32; executable cases and the supplied CSV stop at 16. [H2] [H5]

## 4. Architecture and call-path comparison

### Call shapes, ownership, and terminal semantics

| Shape | Pre-split/published C-core path | Current Swift path |
|---|---|---|
| Unary | Generated service → unaryRpc → interceptor flow → createCall; start status + initial metadata; sender gets `requests.single()`, encodes, copies to C slices, sends and half-closes; CQ message callback decodes/copies and trySends to response channel; collector requests next to verify completion; close callback reports status | Same user/common interceptor API → transport event flow → request source collector → Swift call task; grpc-swift request child pulls message and EOF through callbacks; raw bridge copies into ByteBuffer; NIO stream and framing; response handler sends headers/message/closed via AsyncThrowingChannel; each Kotlin pull starts a new Swift task; adapter decodes and common flow dispatches events |
| Client stream | Eager collector → rendezvous request channel → persistent sender → Ready per message → C-core send batch; one response through bounded response channel | Eager collector → rendezvous channel → per-pull Kotlin launch and Swift checked continuation → NIO async writer, which waits on writability rather than equivalent C-core per-message completion; normal response event pipeline |
| Server stream | One request via `single`; callback receive batch after `request(1)`; decode → one-slot channel → emit → next request; early collection end cancels call | One request still uses source collector/message/EOF protocol; persistent Swift body iteration → response rendezvous channel → per-event task/callback → Kotlin copy/decode → emit; early end cancels request source and waits for callTask |
| Bidi ping-pong | Streaming producer plus sender, response collector feeds next request gate | Same logical dependencies plus Swift pull/request writer and event mailbox round trips; no new call task per ping |
| Full duplex | Producer and response collector proceed independently, bounded by C-core readiness and receive credit | Producer and response consumer proceed independently, bounded by Kotlin rendezvous, NIO writer writability, event rendezvous, NIO inbound watermark and HTTP/2 windows; buffering permits multiple messages in flight |

C-core is not “synchronous with no scheduling”: NativeClientCall uses arenas, native batch operations, readiness state, lifecycle locks/atomics, completion-queue callbacks/futures, Kotlin channels/coroutines, and native executor/combiner scheduling. It starts receiving status independently and coordinates final close with outstanding work. `request(1)` gates application delivery, not necessarily every transport read. HTTP/2 buffering exists on both sides. [K8–K10] [C1–C3]

**C-core batch inventory:** one status receive batch, one combined send/receive-initial-metadata batch, R message-send batches, M message-receive batches, and one send-close batch; an additional receive may observe EOF, subject to close races suppressing submission. This is R+M+3 normal batches, plus possible EOF receive (ordinary unary 5–6), rather than one monolithic native unary operation. CompletionQueue uses the C callback CQ API, a submission guard, native tags/StableRefs and CallbackFuture completion; it does not create a Kotlin polling thread or a Kotlin coroutine for each native completion. These costs are real baseline work that the Swift task counts must not ignore. [K9] [K15]

Swift cancellation: cancelling an outstanding Kotlin continuation calls Swift cancel; that cancels callTask and request source. Source cancellation must complete any request callback because a Swift checked continuation does not resume automatically on Task cancellation. `CallRunner` checks cancellation around nonthrowing channel send, fails the event channel on cancellation, and otherwise turns RPC errors into Closed events. The Kotlin transport's `finally` cancels the source; if Closed was not consumed it uses NonCancellable `cancelAndWait`, which creates another Swift task awaiting callTask. On normal Closed it does not join that task. Terminal consumption can therefore precede completion of all Swift task cleanup; keep this timing difference in mind. [K1–K7]

### Source-level inventory (ordinary successful, no deadline/retry/interceptors)

Counts below are **Observed call sites**, not guaranteed scheduler suspensions, heap allocations, or context switches. Immediate completion, inline execution, ARC optimizations and compiler specialization can remove runtime work. Let R=request messages, M=response messages, E=M+2 for normal headers + messages + closed. Rejected calls may have fewer events.

| Operation | Current Swift bridge/core | Pre-split C-core |
|---|---|---|
| Swift task creations | 1 callTask + E event-pull Tasks; core adds 1 request child task; persistent connection task is per channel | 0 Swift tasks; native scheduling still exists |
| Normal unary above | 4 explicit bridge Tasks + 1 core child (not 5 threads) | 1 Kotlin sender coroutine; no unary request collector |
| Kotlin request source jobs | 1 SupervisorJob, 1 eager collector, R+1 undispatched per-pull launches for finite normal stream (EOF included) | 1 sender; additionally 1 collector only for streaming requests |
| Request continuation adapters | R+1 Swift checked throwing continuations + cancellation handlers | C batch callbacks/readiness, no Swift checked continuations |
| Request interop | R+1 Swift→Kotlin pull entries and completions; per message length/property access and fillBuffer callback; encode once per message | encode once; native slice/batch API calls |
| Response completion adapters | E Kotlin suspendCancellableCoroutine invocations and ObjC request/completion pairs | response channel receive; metadata/status callbacks directly complete futures |
| Response channel/iterators | E AsyncThrowingChannel sends, receives, fresh iterators; 1 persistent grpc-swift body iterator (plus wrapper iterators) | Channel<Response>(1), persistent channel iterator |
| Response event state | E PullState CAS attempts and release stores on valid pulls; channel locked state on each send/next; Swift and Kotlin event wrappers | readiness/native lifetime state, channel state; no bridge PullState |
| Request callback state | CAS to claim pending callback, getAndSet to release it per pull; failure atomic mainly exceptional | readiness/lifetime state and CQ tag management |
| Byte boundary | one copy Kotlin→RawMessage and one RawMessage→Kotlin per message; raw serializer/deserializer are casts | one copy Kotlin→C slices and one C slices→Kotlin per message |
| Added byte processing | request zero-fill; NIO framer copies into framed buffer; receive consolidation and NIOTS data copy | C-core frames by slice references/moves; receive parser accumulates slices |
| Metadata | Kotlin→Swift metadata wrapper; binary copy; reverse visitor callbacks; status/string conversion | native metadata array/slice conversion and binary copies also exist |
| Shared async split | extra Flow event dispatch and Kotlin event wrappers; same collector coroutine, not automatically a new coroutine per event | original main callbacks handle headers/close outside response channel |

A unary successful response normally takes three Kotlin event adapters, **not three Swift checked continuations**: the checked Swift continuations are on the request pulls. AsyncAlgorithms creates unsafe continuations only when send/next cannot complete through its locked fast path. A fresh channel iterator is a small wrapper sharing storage, not a fresh channel allocation. Kotlin undispatched launches enter on the caller thread and may complete without dispatch. These distinctions prevent inflated operation counts. [K3–K7] [D3–D7]

### Inside pinned grpc-swift/NIO

`GRPCClient.unary` itself delegates to `bidirectionalStreaming(StreamingClientRequest(single:))` and constructs `ClientResponse(stream:)`. Core selects a one-shot executor absent retry/hedging policy; it creates a task group, opens a transport stream, adds a child for request processing, and waits for initial response metadata in the parent. Request processing writes metadata, maps each message through the serializer, and finishes the outbound writer. The body sequence lazily deserializes messages and turns trailing status into metadata/error. Deadline child tasks are conditional; the benchmark does not justify counting them on every call. [D3] [D4]

Transport selection follows GRPCChannel → load balancer → subchannel → Connection.makeStream. These states use locks; readiness continuations apply when unavailable, not necessarily to every warmed call. Stream creation uses the HTTP/2 multiplexer, which deliberately schedules creation on the next event-loop tick to prevent reentrant activation, and bridges its future through `get()`. Each stream installs a gRPC stream handler and NIOAsyncChannel. Current defaults set 16 KiB max inbound frame size and an 8 MiB target window; the async inbound buffer uses low/high message watermarks 2/10. These are distinct controls, not the same buffer. [D5] [D7] [D8]

NIOAsyncWriter generates a yield identifier, enters locked state and a cancellation handler; on a writable fast path it calls the delegate, and only the blocked path constructs an unsafe continuation. Delegate writes/finish enqueue onto the event loop when off-loop, where writes flush. NIO inbound delivery accumulates channel reads until readComplete, yields batches into its async producer, and changes read demand at watermarks. This can amortize receives; there is no justified constant “one event-loop hop per payload” count. [D6]

NIO HTTP/2 tracks connection/stream windows, including buffered bytes, and replenishes around half of target. C-core has adaptive BDP/window/frame-size logic rather than the identical fixed configuration. C-core also defers/schedules writes and may wait for flow control; absence of the 100 µs coalescing timer is not absence of all write scheduling. The observed direct Swift high-concurrency wins reject treating NIO locks or per-stream channel creation alone as a proven throughput ceiling. [D8] [C1] [C3]

### Copy ledger: compare like boundaries

- **Application serialization:** both Kotlin clients encode/decode protobuf. Direct Swift uses another protobuf implementation. No recommendation to eliminate this shared work.
- **Kotlin→backend:** C-core copies each Kotlin buffer segment with `grpc_slice_from_copied_buffer`; Swift fills backend-owned RawMessage with memcpy. One payload copy each. Swift first constructs zero-filled storage; that extra initialization is separate from the required copy.
- **Framing:** C-core appends a 5-byte message header and refs payload slices, then emits HTTP/2 frame headers and moves slice ranges. NIO's identity framer writes the payload into its framing ByteBuffer. This additional transport-layer copy is demonstrably distinct from the equal application boundary.
- **Receive:** C-core accumulates/refers to DATA slices, removes five header bytes, and exposes message slices. NIO deframer adopts the first buffer then appends later fragments with `writeImmutableBuffer`, possibly compacting or reallocating; a completed message is `readSlice` (sharing, not another copy). RawMessageDeserializer is a cast. Kotlin still copies once in either backend.
- **Network.framework:** NIOTS receive copies Data into an allocated ByteBuffer. Outbound `getData` uses automatic strategy: copies buffers ≤256 KiB and shares storage above that threshold. Those are *transport buffer* sizes after HTTP/2 framing, not application message sizes. C-core's inspected POSIX endpoint receives into slice-backed iovecs. Actual legacy Apple endpoint selection is not established from the benchmark, so NIOTS-versus-C-core endpoint parity remains conditional.

Do not turn this ledger into an exact total-memory-bandwidth estimate: protobuf allocations, ByteBuffer COW/compaction, actual fragmentation, OS/kernel copies, compression and allocator behavior have not been measured. [K9] [K10] [D9–D12] [C1] [C2] [C4]

## 5. Bottleneck findings

| Finding / layer | Evidence and mechanism | Supporting pattern | Disconfirmation / alternatives | Confidence |
|---|---|---|---|---|
| Timer coalescing / transport configuration | Default 100 µs/64 KiB handler delays low-volume flush; bridge and Swift CLI leave it enabled; C-core non-buffer-hinted sends initiate writes | Both Swift c1 and ping-pong slow; upload-64k faster than upload-1k; high concurrency amortizes | Does not explain 0.4 s tails; timer firings not measured; Network.framework/executor scheduling also contributes | Observed difference; Strong inference of material latency impact |
| Event task/handshake / bridge design | E tasks, iterators, callback adapters, channel rendezvous and double wrappers | C/S ~0.60–0.65 for small server streams; current-specific high-concurrency deficit | Current still beats C-core small server streams/full duplex; fixed 1k uploads are C≈S | Observed excess; Hypothesis for magnitude |
| Single-request machinery / coroutine + bridge | Collector/channel/R+1 pulls even unary; C-core single request sender | Small unary C>S; overhead per call can amplify at c128 | Cannot explain persistent ping-pong gap or large-response plateau | Observed excess; Hypothesis for timing share |
| Repeated cleanup / bridge lifecycle | `cancel()` eagerly constructs CancellationException and NSError, even if no pending callback; invoked from several normal-completion paths | Many unary calls and tail-heavy current run make allocation traffic worth testing | No GC trace; legacy also has tails; explicit cleanup refutes a demonstrated leak | Observed redundant construction; Hypothesis for tail effect |
| Shared event split / common async boundary | Extra event Flow/wrappers; headers/closed now traverse event pipeline | All current calls traverse it | Additional Flow is not automatically a new dispatch; no post-split C-core iOS benchmark isolates it | Observed; Unknown quantitative contribution |
| Extra byte passes / bridge + NIO | Zero-fill, framing copy, receive consolidation, NIOTS receive copy | Large server-stream S<L and C≈S; C/S upload separation grows with payload | Current large-upload median can match/beat legacy; duplex current wins; shared Kotlin protobuf/copy costs explain some C/S gap | Observed byte operations; Hypothesis bottleneck |
| Task/GC/runtime stalls / Kotlin Native and interop | Large max/p999 and mean/median divergence, many source allocations | Both Kotlin clients have tails, current worse at high concurrency | Compiler versions differ; scheduling/server stalls not ruled out; no leak proof | Measured tails; cause Unknown |
| HTTP/2 flow control / NIO configuration | Fixed 8 MiB target, 16 KiB max frames versus C-core adaptive logic | Direction-specific large-response plateau | No wire window-starvation trace; 8 MiB is not a tiny default; plaintext loopback | Hypothesis only |
| Build asymmetry | Kotlin compiler defaults differ; imported Swift optimization flags unrecorded | Could affect allocations and all comparisons | Upload wins contradict simplistic universal debug slowdown but do not prove release parity | Observed configuration uncertainty; effect Unknown |

## 6. Optimization proposals (C-core parity test)

All designs below are proposals, not compiled patches. “High confidence” concerns existence of excess work; it does not mean a measured speedup. Safeguards belong in the implementation, not as optional follow-up work.

### P1. Remove the extra low-load flush timer

**Layer/owner:** kotlinx-rpc Swift transport configuration, [K5]; upstream setting/handler [D1–D2]. **Current:** uses Transport.Config.defaults, retaining delayed coalescing. **C-core:** ordinary send with no GRPC_WRITE_BUFFER_HINT initiates native writing immediately under its scheduler. **Excess:** intentional timer wait and timer management, not the existence of batching.

**Design:** expose a latency-parity setting and initially A/B `transportConfiguration.connection.flushCoalescing = nil`. Apply the same variant to direct Swift to isolate the underlying backend effect. Keep natural event-loop batching; do not add a second artificial timer. If choosing a library default, evaluate mixed loads first because this option is connection-wide and affects all streams. Setting delay to zero still schedules a callback; nil actually removes the handler behavior.

**Expected effect / evidence:** strongest candidate for small unary/ping-pong medians and the upload-64k reversal; concurrency and bulk traffic should benefit less. May reduce upload throughput because small sends lose timer-driven batching; all upload/full-duplex wins must remain explicit comparison points. It cannot remove runtime tails. **Confidence:** high source, strong-inference impact. **Effort/risk:** low implementation effort, meaningful throughput tradeoff.

**Correctness:** no message/order/API semantic change intended; cancellation/close must still flush or terminate appropriately through existing NIO handlers. No new memory buffer, thread-safety or reentrancy code. Preserve user configuration; do not mutate a live connection unsafely. **Validation/falsifier:** empty unary, empty ping-pong, 1k/64k uploads, c16 unary, 1k full duplex; record flush requested/performed timestamps and timer counts. If delays are not on the critical path or median does not fall with nil, downgrade this cause. Never promise “exactly 100 µs saved.”

### P2. Make request-source cleanup once-only and build errors only for an actual waiter

**Layer/owner:** Kotlin bridge lifecycle, `KotlinGrpcRequestSource.cancel/complete` [K2], Swift request producer defer/CallRunner defer/transport finally/deinit [K1] [K4] [K6]. **Current:** every cancel constructs a cancellation exception, cancels three objects, and constructs NSError before `complete` discovers that the atomic callback slot is empty. Successful finite calls reach several cleanup paths. **C-core:** normal completion releases call/batch resources; it has no Swift request-source cancel/error conversion round trips. **Excess:** duplicate source teardown and unused NSError/map/string construction, not ordinary cancellation support.

**Design:** one terminal state machine owns request-source completion and callback claiming; normal EOF is a normal terminal state, while cancellation records a cancellation reason. Subsequent stop calls return cheaply. Construct the NSError only after atomically claiming an outstanding callback. Capture/clear callback under state synchronization; stop producer/job/channel before invoking external code; invoke outside locks. A concurrent late pull must immediately receive EOF/error exactly once, never register into an already stopped source. Do not add an isolated boolean guard without coordinating the pending callback and future pulls.

```
stop(reason):
  under state lock: transition active -> stopped; take pending callback
  if already stopped: return
  cancel producer/channel/job if still active
  if callback exists: callback(nil, makeErrorOnlyIfNeeded(reason))
next(callback):
  under same state lock: either register or obtain terminal result
  deliver terminal result outside lock
```

**Expected effect / evidence:** fewer per-call allocations and interop callbacks, potentially useful for c32–c128 unary and tail-heavy large unary; little sustained-stream throughput effect. **Confidence:** high source, low-to-medium impact, no proven GC causality. **Effort/risk:** small-to-medium, race-sensitive. **Correctness:** preserve originalFailure, distinguish normal EOF from cancellation, keep exact-once completion under cancellation/encode-failure races and reentrant callbacks, release retained buffers/blocks on every path. Maintain current ObjC protocol compatibility. **Validation:** counters for stop calls, allocated NSError with/without waiter and outstanding callbacks; allocation profiling; cancel before first pull, during encode, after EOF, after response close. If error-allocation removal does not reduce runtime cost, keep only justified cleanup simplicity rather than claiming a performance fix.

### P3. Replace task-per-event pulls with a bounded callback mailbox

**Layer/owner:** Swift bridge `SwiftGrpcCall.nextEvent`, `CallRunner.send`, Kotlin adapter [K3–K5]. **Current:** a new Task and AsyncThrowingChannel iterator receives one event, then calls Kotlin completion. **C-core:** native callback delivers to a one-message Kotlin channel; headers and close do not require a new callback-consumer task. **Excess:** a consumer Swift Task and intermediary async-channel rendezvous for every event, in addition to the existing call task and Kotlin coroutine consumer.

**Design:** retain one call task as producer. Replace eventChannel + PullState with one lock-protected state containing at most one pending pull, one pending event, a producer acknowledgement continuation, and terminal state. `nextEvent` registers the completion synchronously or takes an available event; the call-task producer completes it directly. Suspend the producer only when credit is absent. Do not replace it with an unbounded AsyncStream. Preserve existing callback signatures initially; Kotlin still has its event adapter but loses per-event Swift task/iterator creation.

```
nextEvent(callback):
  lock: reject duplicate/terminal pull; match pending event or register callback
  unlock: invoke matched callback; resume producer acknowledgement
send(event) async:
  lock: match registered callback or install one pending event
  unlock: callback(event) if matched
  await acknowledgement only when needed
```

This sketch omits race state details deliberately: terminal must be delivered exactly once, and cancellation must complete both the registered Kotlin callback and any producer acknowledgement. Do not hold locks during callbacks/resumes. Mark the pull reusable before callback invocation to allow reentrancy. Retain event byte storage until Kotlin's scoped read ends. The current channel handshake releases producer when the event is taken, not after application processing; the replacement must not accidentally permit unbounded prefetch. One in-flight message plus transport buffering is sufficient; document the exact credit point.

**Expected effect / evidence:** reduces fixed per-event scheduling in every shape, most visible small server streams and unary/ping-pong C/S residual; high concurrency may reduce allocation churn. Streaming current>L means this is removal of demonstrable excess, not proof the existing mailbox dominates parity. **Confidence:** high source, medium expected contribution. **Effort/risk:** medium/high; cancellation, reentrancy and terminal races are the main risks. **Compatibility:** internal bridge rewrite with same ObjC interface; maintain interceptor ordering and cancellation precedence. **Validation:** E task count must become zero event-pull tasks, single persistent call task remains; compare c1/c128 empty and 1k, 1k server stream, ping-pong and early take(N). Stress concurrent pulls, cancellation while producer/consumer waits, error after message, headers rejected by interceptor. A persistent consumer task alone is a fallback but leaves more scheduling than the mailbox.

### P4. Specialize single-request bridge execution; do not merely rename the grpc-swift API

**Layer/owner:** iOS transport and request source [K1–K2], RawMessageBridge/CallRunner [K4] [K6], optional internal common transport extension [K7]. **Current:** all request shapes use collector + rendezvous + per-pull coroutine + Swift checked continuation, including one request and EOF. **C-core:** unary/server-stream requests are collected with `single()` by one sender coroutine, without request channel/collector. **Excess:** the single-request streaming handoff topology and EOF pull, not grpc-swift's general streaming implementation.

**Design phase A:** after Kotlin interceptors have produced the actual request Flow, select method type. Provide a single-request source adapter which collects `single()` in the owning call scope and completes one raw-message callback; supply grpc-swift's single request producer locally from that raw value, with no second Kotlin EOF callback. Avoid moving serialization before interceptors or outside benchmark timing. Preserve call-start/metadata timing: a cancellable single-request provider can run alongside stream opening, rather than blocking transport startup on an interceptor-suspended flow. Reject 0/2 requests with the existing status semantics.

**Phase B, only if needed after P3:** offer a unary response result carrying one message and terminal status while preserving onHeaders timing. C-core already routes headers and close through callbacks rather than message-channel events. A specialized result can avoid separate message/closed bridge handshakes and shared event wrappers. Headers must still be delivered before response processing and must be able to cancel; do not silently postpone onHeaders until EOF. Preserve unary cardinality and non-OK trailers. The generic event path remains for streaming and interceptors whose semantics require it.

Switching to `GRPCClient.unary` alone is **not** this optimization: its pinned implementation delegates to bidi. There is still one core request child task; eliminating that is upstream work and not part of this proposal.

**Evidence/impact:** small unary C>S and per-call scaling; server-stream startup; not sustained ping-pong after startup. **Confidence:** high excess, medium impact. **Effort/risk:** medium phase A; higher phase B. **Correctness:** parent cancellation owns request work; exactly one completion; no eager serialization outside the call; response-before-request-finished cancels producer; reentrant callbacks outside locks; bounded message lifetime; retain empty/one/many request validation and repeated Flow collection/retry semantics. Internal bridge/transport API change, public API unchanged. **Validation:** source job and pull counters for unary should fall from collector+2 pull launches to one producer operation; compare c1/c128 unary and server-stream first response. Cancellation at every boundary and interceptor-modified request tests are prerequisites.

### P5. Remove the redundant zero-initialization pass, retaining the required boundary copy

**Layer/owner:** RawMessageBridge.copyToRawMessage [K6], GRPCNIOTransportBytes [D9]. **Current:** initializes `length` bytes to zero, then Kotlin fills exactly those bytes. **C-core:** allocates copied slices and writes source bytes; no explicit equivalent full payload zero-fill in that copied-buffer path. **Excess:** initial zero write only; **the Kotlin→backend memcpy remains**.

**Design:** add/use an upstream fill-initializer for GRPCNIOTransportBytes that allocates ByteBuffer capacity, exposes writable bytes, verifies the written length, then advances writerIndex. It must not expose uninitialized bytes as readable. With the current public wrapper, arbitrary access to its internal buffer is unavailable; a practical upstream initializer is preferable to an unsafe cast or creating an extra temporary array. The closure fills directly from the existing pinned Kotlin segments.

**Expected effect/evidence:** fewer memory writes for large requests; fixed-byte 64k/1m/near-4m current/Swift separation is compatible but also includes equivalent Kotlin serialization/copy work, so does not establish a parity bottleneck. Current large-upload medians match/beat legacy, limiting expected parity value. **Confidence:** high explicit source operation, low-to-medium performance value; compiler may optimize initialization in ways static analysis cannot quantify. **Effort/risk:** small upstream API, strict memory correctness. **Correctness:** validate nonnegative/bounded length, propagate fill failure without publishing bytes, retain pointer lifetime only inside closure, account for zero length and COW uniqueness, preserve cancellation surrounding fill. **Validation:** bytes zeroed/copied counters and allocation samples on fixed-byte uploads; retain one boundary copy per byte; no gain means deprioritize.

### P6. Reduce additional NIO framing/consolidation copies toward C-core slice behavior

**Layer/owner:** grpc-swift-nio-transport framer/deframer [D10], potentially GRPCCore bytes API. **Current:** identity framing writes payload into a framing buffer; deframing appends fragmented payloads into contiguous storage. **C-core:** keeps refcounted payload slices through framing and deframing [C1–C2]. **Excess:** transport-specific payload linearization/copying above the unchanged Kotlin boundary copy.

**Practical outbound patch:** for an uncompressed individual message, emit a small five-byte header buffer followed by a retained payload buffer through an internal queue of byte buffers, preserving ordered write promises. Teach the stream state machine/framer consumer to drain the sequence while respecting HTTP/2 frame/window limits. Keep coalescing of tiny messages where measured beneficial; avoid copying entire large payloads merely to prepend a header. This requires changing the existing `nextResult` contract that currently returns one ByteBuffer. Compression keeps its existing path. Do not claim this alone fixes upload parity, since current already wins sustained uploads.

**Inbound investigation:** first measure bytes copied by append, discardReadBytes and COW growth. A fragment queue matching C-core can avoid repeated consolidation, but GRPCContiguousBytes and MessageDeserializer currently require contiguous access. A full removal needs a segmented-message transport/core extension and a raw bridge visitor which copies segments directly into Kotlin's Buffer **once**, just as C-core does. The generic SwiftProtobuf path would still need its own compatible deserializer or a final consolidation. Do not advertise this as a small bridge-only patch or as zero-copy Kotlin messaging. A smaller patch can reduce demonstrably repeated compaction/COW copies while retaining one necessary consolidation.

**Evidence/impact:** large server-stream S/L 0.815/0.839 with C/S 0.919/0.935 localizes a shared receive-path candidate; upload NIO framing copies are proven but upload wins lower priority. Full-duplex and large-unary median parity disconfirm a universal bandwidth explanation. **Confidence:** high mechanisms, medium-to-low causal importance. **Effort/risk:** medium outbound, high inbound/API. **Correctness:** retain fragment owners to write completion; exact ordering and combined promise failure; bounded buffered bytes under slow consumers; no pointer escapes; handle compressed frames, truncated messages, message limits, EOF/trailers, cancellation mid-frame, and reentrant channel events. **Validation:** only after P1/P3, profile 64k/1m server streams and fixed-byte uploads, trace copy volume and allocation stacks; compare original and segmented transport while preserving Kotlin copy counts. Reject if CPU time is elsewhere.

### P7. Conditional transport/HTTP2 parity experiments, not default changes yet

**Layer/owner:** SwiftNIO Transport Services/transport configuration [D8] [D11–D12]. **Current:** Network.framework Data→ByteBuffer copy and queue callbacks, plus fixed advertised max frame/window settings. **Comparable C-core:** inspected POSIX endpoint reads into slice-backed iovecs and CHTTP2 adapts receive settings using BDP. **Critical missing proof:** actual legacy Apple endpoint engine/build configuration and negotiated settings. Until captured, these are conditional candidates, not approved parity optimizations.

**Design A:** if the measured legacy engine uses the inspected socket path, A/B grpc-swift's supported Posix transport on the same simulator. The specific target is the additional Network.framework adaptation/copy layer, not “try another backend because it might be faster.” If Apple deployment requirements require TransportServices, retain it and investigate Data-backed buffer ownership upstream; do not introduce unsupported platform networking behavior.

**Design B:** capture SETTINGS, DATA frame sizes, WINDOW_UPDATE timing, and time with a blocked receive window in both paths. If C-core has negotiated larger effective frames or an adaptive window that avoids stalls, match those observed effective settings in Swift or add corresponding adaptation. Increasing max inbound frame size can reduce per-frame handler and NIOTS packet-buffer overhead; targetWindowSize can reduce proven window stalls. An arbitrary larger window with no observed excess stall fails the parity criterion.

**Evidence/impact:** large-response streaming plateau and upload/download asymmetry; small unary and 0.4 s Kotlin tails are not explained. **Confidence:** hypothesis, explicitly gated. **Effort/risk:** low experiment; transport replacement and adaptation are higher-risk upstream/configuration work. **Correctness:** preserve TLS/authority, connectivity behavior, flow-control limits, ordering, cancellation and bounded memory; settings negotiated per connection and must obey HTTP/2 bounds; no thread-affinity assumptions from switching event-loop implementation. **Validation:** 64k/1m server streams first; compare copy volume, packets/frames, window-stall duration, CPU and wall throughput. If no difference in stalls/copies or no CPU saving, discard this path. No shipping change recommended without this evidence.

## 7. Prioritized implementation roadmap

| Order / category | Work | Expected contribution | Effort / correctness risk | Owner / dependency |
|---|---|---|---|---|
| 0 — measurement prerequisite | Preserve exact binaries/commands and compare compiler-aligned legacy/current; record imported Swift optimization flags | Prevents attributing compiler/build differences to transport | Small / low | Benchmark environment |
| 1 — high-confidence immediate candidate | P1 disable delayed coalescing A/B; expose explicit option | Largest plausible c1/ping-pong median gain; cannot fix all tails | Small / low semantics, throughput tradeoff | kotlinx-rpc configuration, no upstream patch |
| 2 — high-confidence redundant work | P2 once-only cleanup and lazy error conversion | Allocation reduction per call; tail benefit unknown | Small–medium / race-sensitive | Kotlin bridge |
| 3 — medium-confidence impact | P3 bounded callback mailbox | Broad bridge scheduling reduction; small responses/concurrency | Medium / high concurrency-test burden | Swift bridge |
| 4 — medium-confidence impact | P4 single-request specialization; phase B only if residual cost warrants | Unary/server-start fixed cost | Medium–high / interceptor and cancellation ordering | Kotlin + Swift bridge |
| 5 — instrumentation-gated | Attribute current tail stalls after 1–4 | Potentially crucial for mean throughput | Medium profiling / no speculative production change | Runtime/interop investigation |
| 6 — upstream focused work | P5 zero-fill API, P6 outbound framing / inbound consolidation | Large-byte paths; limited evidence of upload parity deficit | Small to high / ownership and backpressure | NIO transport + possibly GRPCCore |
| 7 — hypotheses only | P7 endpoint and negotiated frame/window parity | Possible large-response plateau improvement | Small experiment, potentially high product risk | NIO/configuration, contingent on evidence |

Do not implement a generic GC tuning change, disable safety checks, remove all channels, or rewrite NIO scheduling based on this run. The shared event boundary may deserve phase-B specialization, but the common layer has no demonstrated scheduler hop to delete blindly. No summed speedup estimate is defensible: proposals overlap and traffic changes their marginal effects.

## 8. Focused Apple validation plan

Run independent A/B cases on one warmed simulator/server configuration, rotating variant order and saving individual samples plus exact binaries. A handful of repeated runs distinguishes a repeatable effect from the single-run tails; do not infer statistical significance from this input. Keep original call counts for parity, with a longer run only to quantify identified tail events. Instrumented runs are separate from timing runs.

| Hypothesis | Minimal cases / variants | Instrument / counters | Success or falsification criterion |
|---|---|---|---|
| P1 timer on critical path | empty unary, empty ping-pong, upload-1k/64k; defaults vs nil in current and direct Swift | signposts flush-request/timer-fire/flush-performed, Time Profiler / thread activity | Lower median correlates with removed timer wait; no shift falsifies dominance; quantify bulk regression |
| P2 unused teardown allocations | empty c128, unary upload-1m | allocations + retain/release sampling; cancellation/NSError-without-waiter counters; Kotlin allocation/GC stats | Counters drop with identical terminal outcomes; correlate rather than assume tail improvement |
| P3 task-per-event overhead | server-stream 1k, empty/1k c1 and c128, ping-pong | Swift Concurrency task creation/lifetime, event callback counts, queue delays, CPU | Remove E pull tasks and maintain bounded outstanding events; benefit absent means another limiter |
| P4 unary topology | empty c1/c128, server-stream first response | Kotlin job/pull counts, Swift checked continuation counts, interceptor timestamp sequence | Collector/channel/EOF callback removed only for single requests; identical errors and headers ordering |
| Tail attribution | 1k c128, symmetric-1m, download-near-4m | Instruments Time Profiler + Swift Concurrency, Kotlin GC pauses/allocation/heap, scheduler tracks, process CPU/RSS, per-call timelines | Show overlap between stalled calls and actual GC/executor/server events; no overlap rejects GC hypothesis |
| P5/P6 byte passes | fixed-byte 64k/1m/near-4m, server-stream 64k/1m | zeroed/copied-byte counters at Kotlin boundary, framer, deframer, NIOTS; COW allocation stacks | Boundary stays one copy each way; extra transport passes diminish and CPU/throughput improve |
| P7 endpoint/settings | server-stream 64k/1m | endpoint engine identification, negotiated SETTINGS/frame histogram/WINDOW_UPDATE stall time; Data-copy counters | Only change configuration if observed extra copy/stall is removed versus actual legacy path |

Keep 1k client stream and all four full-duplex cases as regression guards for P1/P3: the existing wins are part of parity, not noise to optimize away. Validate slow-consumer bounded memory and request/response cancellation with short purpose-built cases: cancel before start, outstanding pull, outstanding send, after headers, after final response, and take(N) on unbounded server stream. These validate proposed synchronization rewrites; they are not a request to run the entire benchmark roadmap.

For shared-boundary isolation, on Apple build the pre-split C-core implementation and the refactored callback/event implementation with the **same** Kotlin/compiler/protobuf/server settings in separate checkouts. The original working tree need not be changed. This controls a confound the three CSV implementations cannot resolve. Record packet/server CPU timing if a stall appears in all clients. Never classify a source-level continuation as a measured context switch.

## 9. Open questions and residual uncertainty

1. What exact source revision/compiler flags/runtime versions generated the CSV, particularly imported Swift optimization and Kotlin 2.3 versus 2.4.20? The prompt has no build manifest.
2. How much flush-delay time actually lies on each request's critical path? Timer duration is known; scheduling overshoot, number of flushes, and overlap are not.
3. What produces current's ~0.2–0.4 s unary tails? Explicit request-source cleanup exists, so a leak claim is unsupported; allocation/GC, Objective-C reclamation and scheduler starvation remain alternatives.
4. Is large-response throughput CPU-bound in NIOTS copying, deframer consolidation, protobuf decode, or flow control? Exact source shows all plausible costs but supplies no CPU/wire evidence.
5. Which endpoint engine/options were used by the published C-core Apple binary, and what adaptive settings did it negotiate against this server?
6. How much of the common event split remains after removing bridge tasks? No measurement in this CSV holds the backend and compiler fixed while changing only the split.
7. Will a latency-parity transport default sacrifice useful bulk throughput? This run's strong upload/full-duplex results require an explicit tradeoff, not an assumption.

A remaining architectural floor is plausible: ObjC crossings, dual runtime ownership and async coordination cannot all be made identical to a C API, while NIO intentionally isolates event-loop state. However, the direct Swift high-concurrency wins show that a universal “Swift is slower” floor is unsupported. Source inspection cannot determine the achievable parity bound.

## 10. Evidence appendix

### Reproducibility and verification

[Q] contains all throughput cases and the selected latency ratios. Formula for metric x is C/L=x_current/x_legacy, C/S=x_current/x_swift, S/L=x_swift/x_legacy. For same-case message counts/byte counts, throughput ratios use the same numerator workload, so messages/s and nonzero application-byte/s give the same ratios up to CSV rounding. For zero-byte cases byte ratios are undefined. Latency ratios are computed independently; at concurrency >1 they are not simply elapsed ratios. No incompatible-case averages are used.

The analysis script was run locally; it verified 52 unique pairs, ten families, and 105 selected latency comparisons. Selected values were checked against the prompt, with special attention to tail values and near-4m byte accounting. No Gradle or Apple execution was needed for these report-only deliverables. Original production sources remain untouched. Links below bind source facts to exact symbols/revisions; historical references use commit-specific source, current workspace links refer to the recorded analyzed HEAD.

### Rejected hypotheses and excluded optimizations

| Idea | Why excluded or not established |
|---|---|
| Zero-copy Kotlin↔backend boundary | C-core also copies once in both directions. Removing it is outside parity scope even if current/Swift shows a byte-size gap. |
| Avoid protobuf serialization or cache benchmark requests as serialized bytes | Both Kotlin implementations serialize each RPC; changing that shared cost is outside scope and changes benchmark semantics. |
| Switch bidi API to unary API and expect all stream tasks to disappear | Pinned grpc-swift unary delegates to bidi. P4 targets our extra request handoff, not the API name. |
| Remove all coroutine channels/backpressure | C-core has response channel, streaming request rendezvous and readiness. Only extra bridge stages qualify; unbounded queues violate memory/backpressure requirements. |
| Cache/skip metadata or disable cardinality validation generally | C-core also converts metadata and validates call shape; no measured metadata-heavy case supports this. Preserve headers/status/interceptor semantics. |
| Increase connection count | All measurements deliberately reuse one channel; direct Swift high-concurrency wins do not establish a C-core-equivalent need for a pool. |
| Assume default receive window is 64 KiB | Inspected NIO transport target is 8 MiB. Only observed negotiated-window stalls justify P7. |
| Attribute every 1 MiB slowdown to copies | Current large-upload and symmetric medians can beat legacy; means are tail-sensitive. |
| Treat per-await as per-thread hop | AsyncAlgorithms/NIO have immediate fast paths; Kotlin launches are often undispatched. Source call counts are not scheduling profiles. |
| Claim request-source SupervisorJob leak | Parent hook, Kotlin finally, Swift defers/cancel/deinit explicitly stop it. No heap-growth trace establishes a leak. |
| Replace checked with unsafe continuations globally | Core/NIO already use conditional unsafe fast paths in places; deleting runtime checks alone is not a demonstrated primary parity issue. First remove redundant whole handoffs. |
| Generic GC tuning or disabling assertions/logging | Same limitations may affect legacy; actual flags/profiles are missing. Align build evidence and target additional bridge allocations instead. |
| Blindly remove HTTP/2 next-tick stream creation | Pinned source explains reentrancy protection; C-core also schedules stream work. No measured excess justifies unsafe inline activation. |

### Source index

- **K1**: [GrpcClientTransport.ios.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/iosMain/kotlin/kotlinx/rpc/grpc/client/internal/GrpcClientTransport.ios.kt?type=file&root=%252F) — SwiftGrpcClientTransport.execute.
- **K2**: [SwiftGrpcRequestSource.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/iosMain/kotlin/kotlinx/rpc/grpc/client/internal/SwiftGrpcRequestSource.kt?type=file&root=%252F) — KotlinGrpcRequestSource; nextRequestWithCompletion; cancel; complete.
- **K3**: [SwiftGrpcCallAdapter.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/iosMain/kotlin/kotlinx/rpc/grpc/client/internal/SwiftGrpcCallAdapter.kt?type=file&root=%252F) — nextEvent; decodeMessage; [SwiftGrpcContinuation.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/iosMain/kotlin/kotlinx/rpc/grpc/client/internal/SwiftGrpcContinuation.kt?type=file&root=%252F) — awaitSwiftGrpcCompletion.
- **K4**: [SwiftGrpcCall.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Sources/GrpcSwiftBridge/SwiftGrpcCall.swift?type=file&root=%252F) — init; nextEvent; PullState; cancelAndWait; [CallRunner.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Sources/GrpcSwiftBridge/internal/CallRunner.swift?type=file&root=%252F) — run; send; finish.
- **K5**: [SwiftGrpcClient.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Sources/GrpcSwiftBridge/SwiftGrpcClient.swift?type=file&root=%252F) — init(configuration:); startCall; MethodDescriptor init.
- **K6**: [RawMessageBridge.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Sources/GrpcSwiftBridge/internal/RawMessageBridge.swift?type=file&root=%252F) — makeStreamingClientRequest; nextRequestMessage; copyToRawMessage; raw serializer/deserializer; [Continuation.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Sources/GrpcSwiftBridge/internal/Continuation.swift?type=file&root=%252F) — awaitSwiftGrpcCompletion.
- **K7**: [GrpcClientTransport.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/commonMain/kotlin/kotlinx/rpc/grpc/client/internal/GrpcClientTransport.kt?type=file&root=%252F) — GrpcClientCallEvents; [suspendClientCalls.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/commonMain/kotlin/kotlinx/rpc/grpc/client/internal/suspendClientCalls.kt?type=file&root=%252F) — ClientCallScopeImpl.doCall.
- **K8**: [GrpcClientTransport.nonIos.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/nonIosMain/kotlin/kotlinx/rpc/grpc/client/internal/GrpcClientTransport.nonIos.kt?type=file&root=%252F) — execute; channelResponseListener; emitEvents.
- **K9**: [NativeClientCall.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/nonIosNativeMain/kotlin/kotlinx/rpc/grpc/client/internal/NativeClientCall.kt?type=file&root=%252F) — start; request; sendMessage; cancel; runBatch.
- **K10**: [utils.native.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-core/src/nonIosNativeMain/kotlin/kotlinx/rpc/grpc/internal/utils.native.kt?type=file&root=%252F) — Buffer.toGrpcByteBuffer; CPointer<grpc_byte_buffer>.toKotlin; [SwiftGrpcBuffers.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/iosMain/kotlin/kotlinx/rpc/grpc/client/internal/SwiftGrpcBuffers.kt?type=file&root=%252F) — KotlinGrpcRequestMessage.fillBuffer; copySwiftBytes.
- **K11**: [SwiftGrpcConversions.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-client/src/iosMain/kotlin/kotlinx/rpc/grpc/client/internal/SwiftGrpcConversions.kt?type=file&root=%252F) — metadata conversions; [SwiftGrpcCallTypes.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Sources/GrpcSwiftBridge/SwiftGrpcCallTypes.swift?type=file&root=%252F) — SwiftGrpcMetadata; SwiftGrpcCallEvent.
- **K12**: [Package.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Package.swift?type=file&root=%252F) — declared exact dependencies; [Package.resolved](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/Package.resolved?type=file&root=%252F) — all pins.
- **K13**: [build.gradle.kts](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-swift/build.gradle.kts?type=file&root=%252F) — swiftPMDependencies.localSwiftPackage.
- **K15**: [CompletionQueue.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/grpc/grpc-core/src/nonIosNativeMain/kotlin/kotlinx/rpc/grpc/internal/CompletionQueue.kt?type=file&root=%252F) — runBatch; callback CQ; native functor/tag lifecycle.
- **K14**: [libs.versions.toml](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/versions-root/libs.versions.toml?type=file&root=%252F) — Kotlin, coroutines, shim pins; [libs.versions.toml](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/legacy-ios-client/gradle/libs.versions.toml?type=file&root=%252F) — published client/compiler pins.
- **H1**: [CallBenchmark.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/shared-kotlin-client/src/commonMain/kotlin/kotlinx/rpc/grpc/benchmarks/client/CallBenchmark.kt?type=file&root=%252F) — run; executeCalls; [UnaryBenchmarks.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/shared-kotlin-client/src/commonMain/kotlin/kotlinx/rpc/grpc/benchmarks/client/UnaryBenchmarks.kt?type=file&root=%252F) — prepare; benchmarkRequest.
- **H2**: [StreamingBenchmark.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/shared-kotlin-client/src/commonMain/kotlin/kotlinx/rpc/grpc/benchmarks/client/StreamingBenchmark.kt?type=file&root=%252F) — run; executeStreams; [StreamingBenchmarks.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/shared-kotlin-client/src/commonMain/kotlin/kotlinx/rpc/grpc/benchmarks/client/StreamingBenchmarks.kt?type=file&root=%252F) — clientStreamingBenchmark; ping-pong, full duplex, sweeps.
- **H3**: [BenchmarkCli.kt](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/shared-kotlin-client/src/commonMain/kotlin/kotlinx/rpc/grpc/benchmarks/client/BenchmarkCli.kt?type=file&root=%252F) — channel lifecycle; [BenchmarkCLI.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/swift-client/Sources/SwiftGrpcBenchmarkClient/BenchmarkCLI.swift?type=file&root=%252F) — transport construction and per-case withGRPCClient; [UnaryBenchmarks.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/swift-client/Sources/SwiftGrpcBenchmarkClient/UnaryBenchmarks.swift?type=file&root=%252F) — unary harness; [StreamingBenchmarks.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/swift-client/Sources/SwiftGrpcBenchmarkClient/StreamingBenchmarks.swift?type=file&root=%252F) — streaming harness.
- **H4**: [Package.swift](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/swift-client/Package.swift?type=file&root=%252F) — package/tools declarations; [Package.resolved](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/swift-client/Package.resolved?type=file&root=%252F) — pins; [build.sh](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/swift-client/build.sh?type=file&root=%252F) — release build; [run.sh](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/kotlinx-rpc-client/run.sh?type=file&root=%252F) — release executable; [run.sh](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/legacy-ios-client/run.sh?type=file&root=%252F) — release executable.
- **H5**: [run-all.sh](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/run-all.sh?type=file&root=%252F) — sequential combined runner; [README.md](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/README.md?type=file&root=%252F) — benchmark definitions and known omissions.
- **H6**: [build.sh](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/cpp-server/build.sh?type=file&root=%252F) — GRPC_REVISION; [run.sh](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/cpp-server/run.sh?type=file&root=%252F) — server defaults; [benchmark.proto](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/swift-client/Sources/SwiftGrpcBenchmarkClient/Protos/benchmark.proto?type=file&root=%252F) — canonical benchmark messages/RPCs.
- **Q**: [tables.md](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/performance-analysis-2026-09-16/tables.md?type=file&root=%252F) — all 52 throughput rows and 105 selected latency comparisons; [measurements.py](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/performance-analysis-2026-09-16/measurements.py?type=file&root=%252F) — selected prompt fields; [analyze.py](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/performance-analysis-2026-09-16/analyze.py?type=file&root=%252F) — reproduction/validation script; [throughput-ratios.csv](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/performance-analysis-2026-09-16/throughput-ratios.csv?type=file&root=%252F) — machine-readable ratios; [latency-ratios.csv](air-file://t84dtjdru6e6s3pu0dtj/workspaces/kotlinx-rpc/tests/grpc-benchmarks/performance-analysis-2026-09-16/latency-ratios.csv?type=file&root=%252F) — machine-readable latency ratios.
- **B1**: revision `1750c217df9ad2cdd2b9134ab01918652c0e3fef`: [suspendClientCalls.kt](https://github.com/kotlin/kotlinx-rpc/blob/1750c217df9ad2cdd2b9134ab01918652c0e3fef/grpc/grpc-client/src/commonMain/kotlin/kotlinx/rpc/grpc/client/internal/suspendClientCalls.kt), [NativeClientCall.kt](https://github.com/kotlin/kotlinx-rpc/blob/1750c217df9ad2cdd2b9134ab01918652c0e3fef/grpc/grpc-client/src/nativeMain/kotlin/kotlinx/rpc/grpc/client/internal/NativeClientCall.kt).
- **B2**: revision `a48afb14112025a0e7666de4994ff14148a16bbf`: [NativeClientCall.kt](https://github.com/kotlin/kotlinx-rpc/blob/a48afb14112025a0e7666de4994ff14148a16bbf/grpc/grpc-client/src/nativeMain/kotlin/kotlinx/rpc/grpc/client/internal/NativeClientCall.kt), [utils.native.kt](https://github.com/kotlin/kotlinx-rpc/blob/a48afb14112025a0e7666de4994ff14148a16bbf/grpc/grpc-core/src/nativeMain/kotlin/kotlinx/rpc/grpc/internal/utils.native.kt), [libs.versions.toml](https://github.com/kotlin/kotlinx-rpc/blob/a48afb14112025a0e7666de4994ff14148a16bbf/versions-root/libs.versions.toml).
- **B3**: revision `60cc5663a71f19ea974776921a884b7f0b74afff`: [suspendClientCalls.kt](https://github.com/kotlin/kotlinx-rpc/blob/60cc5663a71f19ea974776921a884b7f0b74afff/grpc/grpc-client/src/commonMain/kotlin/kotlinx/rpc/grpc/client/internal/suspendClientCalls.kt).
- **D1**: grpc-swift-nio-transport, revision `eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6`: [HTTP2ClientTransport.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/Client/HTTP2ClientTransport.swift).
- **D2**: grpc-swift-nio-transport, revision `eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6`: [FlushCoalescingHandler.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/Internal/FlushCoalescingHandler.swift), [NIOChannelPipeline+GRPC.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/Internal/NIOChannelPipeline%2BGRPC.swift).
- **D3**: grpc-swift-2, revision `ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7`: [GRPCClient.swift](https://github.com/grpc/grpc-swift-2/blob/ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7/Sources/GRPCCore/GRPCClient.swift), [ClientRPCExecutor.swift](https://github.com/grpc/grpc-swift-2/blob/ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7/Sources/GRPCCore/Call/Client/Internal/ClientRPCExecutor.swift).
- **D4**: grpc-swift-2, revision `ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7`: [ClientRPCExecutor+OneShotExecutor.swift](https://github.com/grpc/grpc-swift-2/blob/ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7/Sources/GRPCCore/Call/Client/Internal/ClientRPCExecutor%2BOneShotExecutor.swift), [ClientStreamExecutor.swift](https://github.com/grpc/grpc-swift-2/blob/ac33066eb6edb1a21a6ca172ea8184a9b06f3cc7/Sources/GRPCCore/Call/Client/Internal/ClientStreamExecutor.swift).
- **D5**: grpc-swift-nio-transport, revision `eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6`: [GRPCChannel.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/Client/Connection/GRPCChannel.swift), [Connection.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/Client/Connection/Connection.swift).
- **D6**: swift-nio, revision `a931f2c1de8dd49381ce3bf2e279d033f68d8865`: [AsyncChannelHandler.swift](https://github.com/apple/swift-nio/blob/a931f2c1de8dd49381ce3bf2e279d033f68d8865/Sources/NIOCore/AsyncChannel/AsyncChannelHandler.swift), [AsyncChannel.swift](https://github.com/apple/swift-nio/blob/a931f2c1de8dd49381ce3bf2e279d033f68d8865/Sources/NIOCore/AsyncChannel/AsyncChannel.swift), [NIOAsyncWriter.swift](https://github.com/apple/swift-nio/blob/a931f2c1de8dd49381ce3bf2e279d033f68d8865/Sources/NIOCore/AsyncSequences/NIOAsyncWriter.swift).
- **D7**: swift-async-algorithms, revision `3da39bbc4e687d4192af7c9cf4eab805745a0b9c`: [ChannelStorage.swift](https://github.com/apple/swift-async-algorithms/blob/3da39bbc4e687d4192af7c9cf4eab805745a0b9c/Sources/AsyncAlgorithms/Channels/ChannelStorage.swift), [AsyncThrowingChannel.swift](https://github.com/apple/swift-async-algorithms/blob/3da39bbc4e687d4192af7c9cf4eab805745a0b9c/Sources/AsyncAlgorithms/Channels/AsyncThrowingChannel.swift).
- **D8**: swift-nio-http2, revision `0f3e54e29c944c2e835ad52159da7d9e1c94ac69`: [HTTP2ChannelHandler+InlineStreamMultiplexer.swift](https://github.com/apple/swift-nio-http2/blob/0f3e54e29c944c2e835ad52159da7d9e1c94ac69/Sources/NIOHTTP2/HTTP2ChannelHandler%2BInlineStreamMultiplexer.swift), [InboundWindowManager.swift](https://github.com/apple/swift-nio-http2/blob/0f3e54e29c944c2e835ad52159da7d9e1c94ac69/Sources/NIOHTTP2/InboundWindowManager.swift).
- **D9**: grpc-swift-nio-transport, revision `eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6`: [GRPCNIOTransportBytes.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/GRPCNIOTransportBytes.swift).
- **D10**: grpc-swift-nio-transport, revision `eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6`: [GRPCMessageFramer.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/GRPCMessageFramer.swift), [GRPCMessageDecoder.swift](https://github.com/grpc/grpc-swift-nio-transport/blob/eaad084d6c26ff1f2e96f9c2ab76ef84d7165ab6/Sources/GRPCNIOTransportCore/GRPCMessageDecoder.swift).
- **D11**: swift-nio-transport-services, revision `67787bb645a5e67d2edcdfbe48a216cc549222d5`: [StateManagedNWConnectionChannel.swift](https://github.com/apple/swift-nio-transport-services/blob/67787bb645a5e67d2edcdfbe48a216cc549222d5/Sources/NIOTransportServices/StateManagedNWConnectionChannel.swift).
- **D12**: swift-nio, revision `a931f2c1de8dd49381ce3bf2e279d033f68d8865`: [ByteBuffer-foundation.swift](https://github.com/apple/swift-nio/blob/a931f2c1de8dd49381ce3bf2e279d033f68d8865/Sources/NIOFoundationEssentialsCompat/ByteBuffer-foundation.swift).
- **C1**: grpc-ccore, revision `e84a8a2f04095f2772ba42a4abccde4f9243e75b`: [chttp2_transport.cc](https://github.com/grpc/grpc/blob/e84a8a2f04095f2772ba42a4abccde4f9243e75b/src/core/ext/transport/chttp2/transport/chttp2_transport.cc).
- **C2**: grpc-ccore, revision `e84a8a2f04095f2772ba42a4abccde4f9243e75b`: [frame_data.cc](https://github.com/grpc/grpc/blob/e84a8a2f04095f2772ba42a4abccde4f9243e75b/src/core/ext/transport/chttp2/transport/frame_data.cc).
- **C3**: grpc-ccore, revision `e84a8a2f04095f2772ba42a4abccde4f9243e75b`: [flow_control.cc](https://github.com/grpc/grpc/blob/e84a8a2f04095f2772ba42a4abccde4f9243e75b/src/core/ext/transport/chttp2/transport/flow_control.cc).
- **C4**: grpc-ccore, revision `e84a8a2f04095f2772ba42a4abccde4f9243e75b`: [posix_endpoint.cc](https://github.com/grpc/grpc/blob/e84a8a2f04095f2772ba42a4abccde4f9243e75b/src/core/lib/event_engine/posix_engine/posix_endpoint.cc), [slice.cc](https://github.com/grpc/grpc/blob/e84a8a2f04095f2772ba42a4abccde4f9243e75b/src/core/lib/slice/slice.cc).

Produced by Air Automations. Name: kotlin-rpc gRPC Swift Performance Analysis / Run: [automation run](https://air.jetbrains.cloud/org/05cf1a7f-6ab5-713b-abd3-29d0c8a05e2d/automations/eb4a193c-e91a-4544-b86b-e92182612017?run=53f4060c-4e0d-4a0f-ab9a-2325dbf9a86e)
