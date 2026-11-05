---
name: use-grpc-benchmarks
description: >-
  Run, compare, and interpret the gRPC client benchmarks in tests/grpc-benchmarks.
  Use the bench script (list, doctor, run, analyze, compare), cpp-server/run.sh,
  and swift-client/build.sh. Covers the client run.sh helpers, simulator selection
  in scripts/ios-simulator.sh, and offline analysis/cli.py. Use for gRPC performance
  investigations, current/legacy/Swift client comparisons, before/after measurements,
  CPU and memory measurements, and benchmark workload changes.
---

# Use gRPC benchmarks

Read [the benchmark README](../../../tests/grpc-benchmarks/README.md) for prerequisites, configuration, and the full workload matrix.
Paths below are relative to the repository root unless a command changes directories.

## Choose the comparison

The harness compares complete gRPC client stacks against one pinned C++ server:

| Implementation | Source and runtime | Platforms |
|---|---|---|
| `current` | Repository sources with the Swift bridge on iOS, C-core on macOS, and grpc-java/Netty on JVM | iOS Simulator, macOS, JVM |
| `legacy` | Published `0.11.0-grpc-189` artifacts with Kotlin 2.3.0 | iOS Simulator |
| `swift` | grpc-swift-2 directly | iOS Simulator |

Use iOS Simulator for comparisons across implementations. For macOS or JVM, select `--implementation current --platform macos` or `--platform jvm`.
Each `bench run` selects one platform. Measure different platforms in separate runs.
These measurements include runtime, serialization, compiler, and client differences. They don't isolate Swift bridge overhead.
Simulator loopback results don't predict physical-device or remote-network performance.

Choose workloads from the question:

| Question | Workload |
|---|---|
| Warm sequential call cost | `unary-latency` |
| Calls after idle pauses or short concurrent bursts | `unary-sparse-bursts` |
| Sustained unary rate | `unary-throughput` |
| Payload size or upload/download asymmetry | `unary-payload-sweep` |
| Unary scaling or saturation | `unary-concurrency-sweep` |
| Streaming rates by direction | `server-streaming-throughput`, `client-streaming-throughput`, `bidi-full-duplex` |
| Sequential streaming round trips | `bidi-ping-pong` |
| Per-message cost at a fixed total payload | `stream-message-overhead` |
| Concurrent stream scaling | `stream-concurrency-sweep` |

Use `./bench list --format json` after the binaries exist to get exact cases and parameters.
Start with the case that matches the question. Use `all` when the requested coverage needs the full matrix.

## Prepare the environment and binaries

Run shell examples in `tests/grpc-benchmarks/`:

```shell
cd tests/grpc-benchmarks
python3 -m venv .venv
source .venv/bin/activate
pip install -r analysis/requirements.txt
```

Reuse an existing environment when available. The README lists the platform and tool prerequisites.

**Use `running_gradle_builds` for all Kotlin builds.** `bench build`, `bench run --build`, and both Kotlin `run.sh` helpers call Gradle directly.
Translate their build steps into Gradle MCP calls with an absolute repository `projectRoot`:

| Client and platform | Gradle `commandLine` |
|---|---|
| Current iOS | `[":tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableIosSimulatorArm64"]` |
| Current macOS | `[":tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableMacosArm64"]` |
| Current JVM | `[":tests:grpc-benchmarks:kotlinx-rpc-client:installJvmDist"]` |
| Legacy iOS | `["--project-dir", "<absolute-repository>/tests/grpc-benchmarks/legacy-ios-client", "linkReleaseExecutableIosSimulatorArm64"]` |

After the current iOS build, copy its Swift runtime libraries as `bench build` does:

```shell
binary_dir=kotlinx-rpc-client/build/bin/iosSimulatorArm64/releaseExecutable
mkdir -p "$binary_dir/Frameworks"
xcrun swift-stdlib-tool --copy \
  --scan-executable "$binary_dir/kotlinx-rpc-grpc-benchmark-client.kexe" \
  --platform iphonesimulator --destination "$binary_dir/Frameworks"
```

Use `./swift-client/build.sh` for the release Swift binary.
Rebuild affected binaries after source changes. `bench run` checks their presence but doesn't check freshness.
For before/after runs, keep baseline results before the candidate build replaces the binary.

The manifest records the checkout at run time, not the build revision of a reused binary.
For reused binaries, record their build revisions, resolved dependency locks, and file hashes.
Compare dependency locks before and after a rebuild to find unintended dependency changes.
If you copy executables, include adjacent runtime directories such as current iOS `Frameworks/`.

Start `./cpp-server/run.sh` as a managed background process or in a separate terminal.
The helper builds the pinned server if its binary is missing. The first build downloads dependencies and can take several minutes.
The default is an asynchronous plaintext server on localhost port 50051. Keep its zero-payload reset patch.
Stop the server process that you started when the benchmark session ends.

For reproducible simulator selection, set `KXRPC_BENCHMARK_SIMULATOR` to a device UDID.
Without this setting, the helper selects a booted simulator or boots an available device.

## Check and run

Run `./bench doctor` before comparisons. It checks readiness, binaries, and workload equivalence across the selected clients.
`doctor` reads configuration settings and doesn't parse selection or port flags.
For example, use `KXRPC_BENCH_IMPLEMENTATIONS=current KXRPC_BENCH_PLATFORM=jvm ./bench doctor` for JVM.
Inspect `bench.config` first because the script sources that file after environment defaults.
For commands with supported flags, command-line values override the configuration file.

Choose a run size:

| Option | Trials per implementation and case | Use |
|---|---|---|
| `--preset smoke` | 1 | Check execution and output |
| Default or `--preset default` | 3 | Initial exploration with low confidence |
| `--preset rigorous` | 9 | Comparisons with intervals and a p-value |
| `--repetitions N` | N | An explicit trial count instead of a preset |

Count cases from `list` before a full run. Total trials equal cases × configurations × repetitions.
Use a smoke run or the first complete pass to estimate duration.
Include client and simulator startup, readiness, warmup, and the gap between trials in the wall-clock budget.
Reducing measured operations doesn't reduce these fixed costs. Presets change repetitions, not workload sizes.

Examples after preparation:

```shell
./bench doctor
./bench run unary-latency --preset smoke
./bench run unary-sparse-bursts --case burst-8-10ms --preset rigorous --seed 12345
./bench run unary-payload-sweep --case symmetric-1k \
  --implementation current,swift --preset rigorous
./bench run unary-latency --implementation current --platform jvm \
  --repetitions 9 -- --warmup 1000 --operations 20000
```

Arguments after `--` override client workload parameters. Apply identical overrides to both sides of a comparison.
When you shorten runs, use the same payload sizes, concurrency, and workload semantics.
The default `stream-message-overhead` cases upload 64 MiB. A shared `--operations` override gives different totals for different message sizes.
For a fixed-volume comparison, keep the default counts or check for equal `counts.application_request_bytes` across cases.
Leave the harness-controlled JSON format, platform label, and case selection intact.
Use `--port` for the harness and `GRPC_BENCHMARK_PORT` for the server when the port changes.

Each trial starts a new process and channel, checks readiness, warms up, and measures one case.
Readiness and warmup aren't measured. These are warm, closed-loop workloads, so they don't measure cold connection cost or open-loop overload latency.
Increasing `--operations` adds samples in a trial. Increasing repetitions adds independent trials.
Warmup is an operation count, not a duration or a check for stable performance. Some default cases measure less than one second.
Check measured durations and stability in each trial before you interpret small differences.
If short windows or startup drift dominate, change warmup and operation counts equally across clients and recheck stability.
On JVM, each new trial process restarts JIT warmup. More repetitions don't warm later processes, and compilation can continue during measurement.
Record the JDK version and distinguish short JVM runs from measurements with stable performance after warmup.

Run trials sequentially on an otherwise idle machine. Avoid concurrent builds, profilers, or other benchmark runs.
Compute statistics and generate reports after timed trials finish to avoid CPU contention.
Use the same simulator, server configuration, power source, and workload on both sides.
Wait for build heat to subside before measurement and inspect power and thermal metadata.
Check thermal metadata for command errors from the writer's `pmset -g ther` invocation.
Treat errors as missing data. Use `pmset -g therm` separately when you need thermal evidence.
Keep the default randomized order and record the seed to reduce systematic order effects.

## Analyze saved results

`bench run` prints progress to stderr and the run directory to stdout.
Keep the whole directory, including `manifest.json`, trial JSON records, and stderr logs.
Check the run exit status and manifest before a performance claim. Comparisons exclude failed trials.
Timeouts and crashes can have logs and manifest entries without a trial JSON record.
An interrupt stops the runner gracefully and it writes a manifest for its finished trials. A killed
runner leaves trial records without a manifest, and `analyze` then rejects the whole directory.
Rerun such a measurement instead of salvaging its records.

Replace `RUN`, `BASELINE`, and `CANDIDATE` with actual run directories:

```shell
./bench analyze output/RUN
./bench analyze output/RUN --format html --output report.html
./bench compare output/RUN:legacy output/RUN:current
./bench compare output/BASELINE:current output/CANDIDATE:current \
  --format csv --output comparison.csv
```

`analyze` and `compare` run offline through `analysis/cli.py`. Neither needs the server or client binaries.
Use `RUN_DIR:IMPLEMENTATION` when a directory has several implementations. The first selector is the baseline.
`compare` takes exactly one run directory per side. Pass several directories to `analyze` for a merged
summary, and pick the run that covers a workload when you compare implementations.

`compare` rejects cross-platform pairs. `analyze` accepts several platforms but gives each run separate charts.
For a requested cross-platform comparison, summarize matching cases separately and label each series with both implementation and platform.
Keep original platform labels in the records. Changing them to force a match hides a real comparison constraint.

Interpret results with these constraints:

- Comparisons match workload, case, parameters, platform, target, and injected delay.
- Matching keys don't establish matching machines, simulator runtimes, toolchains, or server settings. Check the manifests and session notes.
- The manifest's server fields have fixed defaults, not measured settings. Record server overrides separately.
- The manifest records the Git revision and dirty flag, but doesn't store a dirty diff. Write the diff to a file for uncommitted experiments.
- The trial is the statistical unit. Don't pool per-operation samples from several trials into independent observations.
- Ratios are candidate median divided by baseline median. Lower favors latency and CPU cost. Higher favors throughput.
- The seeded 95% bootstrap interval needs at least three trials per side. Fewer than five trials gives low confidence.
- The two-sided Mann-Whitney U p-value needs at least eight trials per side. The difference verdict uses whether the interval excludes 1.
- An interval that includes 1 means no difference was detected. It doesn't prove equal performance.
- HTML chart bars show the minimum and maximum of per-trial metric values. Use the reported ratio interval for statistical uncertainty.
- p99 needs at least 100 samples in each trial. More operations don't compensate for too few independent trials.
- Keep slow completed trials. Record any extra repetitions prompted by initial results.
- Each metric's interval has no correction for multiple comparisons. Treat isolated differences in a full-matrix comparison as exploratory.
- A primary-metric case count can hide CPU or p99 regressions. Include those outcomes separately.

Read each metric as its actual interval or unit:

| Metric | Interpretation |
|---|---|
| `call_round_trip`, `ping_pong_round_trip` | Request send to completed response |
| `message_interarrival` | Consecutive response receives, not individual message round trips |
| `request_write_suspension` | Request write start to write return, not server acknowledgement |
| Streaming `parameters.operations` | Requested messages for each active direction across all streams |
| Streaming `counts.operations` | Sum of request and response messages, including control requests and final responses |
| Application MiB/s | Payload bytes, excluding protocol overhead |
| Sparse/burst latency | Excludes idle pauses, while elapsed time and rates include pauses |
| CPU time | Measured-phase process CPU across all threads, excluding the separate server process |
| `cpu_to_wall_ratio` | Can exceed 1 when several threads consume CPU |
| `max_rss_bytes` | Process peak resident memory, including warmup, not allocation volume |

To check streaming completion, compare the requested count with `counts.request_messages` for uploads or `counts.response_messages` for downloads.
Bidirectional workloads send the requested count in each direction, so `counts.operations` is twice `parameters.operations`.

CPU time doesn't measure energy. Missing counters aren't zero.
The JVM client reports only the combined CPU total and no resident size; the iOS and macOS clients
report the user and system split as well.
Wall-clock parity under concurrency doesn't imply CPU parity. A client can hold the same rate by
using more threads, and its CPU per operation then exceeds its wall-clock difference. Check CPU per
operation before calling a concurrent result a tie, especially when battery use matters.
Compare CPU per operation or MiB alongside throughput. A smaller CPU-to-wall ratio alone doesn't establish better efficiency.

For noise investigations, inspect successive sample windows in single-worker trials for drift.
Concurrent sample arrays combine worker or stream results and aren't a global timeline. Don't interpret array chunks as successive time windows.
Message-interarrival p50 can change with buffering and consumer scheduling even when total throughput stays similar. Check both metrics.

For a noise check, compare separate runs of the same implementation.
For a sensitivity check, add `--inject-delay-ms N` and compare with an undelayed run.
The Kotlin coroutine delay adds roughly 1.5 to 2 times its nominal value to each operation on
Kotlin/Native, so expect the measured shift above the nominal delay.
Use `--allow-injected-delay-mismatch` only for that check and identify the artificial delay in the report.

Report the case, effective parameters, platform, revisions, successful and failed trial counts, ratio, interval, and artifact paths.
State uncertainty and environmental limits alongside the result.
Counts of favorable cases don't establish how many real applications benefit.

## Change workloads

Add shared Kotlin workloads in `tests/grpc-benchmarks/shared-kotlin-client/src/commonMain`.
Add equivalent Swift workloads in `tests/grpc-benchmarks/swift-client/Sources/SwiftGrpcBenchmarkClient`.
The Kotlin sources also compile with the legacy Kotlin 2.3.0 compiler.
Rebuild affected clients and run `doctor` to compare case definitions, parameters, latency metrics, and operation units.
Also check execution semantics because equal workload matrices don't prove equal measurement boundaries.
Don't add tests for the benchmark setup. Use builds, `doctor`, and shortened live runs to check completion, counts, and measurement boundaries.

Keep the workload limits unless the task explicitly changes them:

- The legacy receive limit is 4 MiB. The largest unary payload leaves 1 KiB for serialization overhead.
- Server-streaming cases cancel unbounded RPCs. The stream concurrency sweep stops at 16 for SwiftNIO's retained reset-stream allowance.
- Full duplex uses finite `StreamingCall` echo streams. Maintain equivalent completion and cancellation behavior across clients.

Use `kotlinx-rpc-client/run.sh`, `legacy-ios-client/run.sh`, and `swift-client/run.sh` as references for direct client execution.
For recorded comparisons, prefer `bench run` so trial order, failures, and environment metadata remain available.
