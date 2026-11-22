---
name: use-grpc-benchmarks
description: Runs, compares, and interprets the gRPC client benchmarks in tests/grpc-benchmarks. Use for gRPC performance questions, comparisons of clients, measurements before and after a change, CPU and memory measurements, and changes of the benchmark workloads.
---

# Use gRPC benchmarks

First read [the benchmark README](../../../tests/grpc-benchmarks/README.md). It has the setup, the clients, the
configuration, the workloads, the measurements, and the statistics. This skill adds the rules for an agent.
Run the commands in `tests/grpc-benchmarks/`.

## Select the comparison

- Compare implementations on the iOS Simulator. Only `current` runs on macOS and the JVM.
- One `bench run` has one platform. Measure each platform in a separate run.
- `./bench list --format json` gives the exact cases and parameters. It needs the binaries.
- Run the case for the question. Run `all` only when the question needs the full matrix.

| Question | Workload |
|---|---|
| Cost of a sequential call on a warm channel | `unary-latency` |
| Calls after idle pauses, short concurrent bursts | `unary-sparse-bursts` |
| Unary rate under constant load | `unary-throughput` |
| Payload size, upload and download asymmetry | `unary-payload-sweep` |
| Unary scaling and saturation | `unary-concurrency-sweep` |
| Streaming rates for each direction | `server-streaming-throughput`, `client-streaming-throughput`, `bidi-full-duplex` |
| Sequential streaming round trips | `bidi-ping-pong` |
| Cost of one message at a fixed total payload | `stream-message-overhead` |
| Scaling of concurrent streams | `stream-concurrency-sweep` |

## Build

`bench build`, `bench run --build`, and the Kotlin `run.sh` helpers call Gradle directly. Don't use them to build.
Build the Kotlin clients with `running_gradle_builds` and the repository root as `projectRoot`:

| Client | Gradle `commandLine` |
|---|---|
| Current, iOS | `[":tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableIosSimulatorArm64"]` |
| Current, macOS | `[":tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableMacosArm64"]` |
| Current, JVM | `[":tests:grpc-benchmarks:kotlinx-rpc-client:installJvmDist"]` |
| Legacy, iOS | `["--project-dir", "<repository>/tests/grpc-benchmarks/legacy-ios-client", "linkReleaseExecutableIosSimulatorArm64"]` |

After the current iOS build, copy the Swift runtime libraries, as `bench build` does:

```shell
binary_dir=kotlinx-rpc-client/build/bin/iosSimulatorArm64/releaseExecutable
mkdir -p "$binary_dir/Frameworks"
xcrun swift-stdlib-tool --copy \
  --scan-executable "$binary_dir/kotlinx-rpc-grpc-benchmark-client.kexe" \
  --platform iphonesimulator --destination "$binary_dir/Frameworks"
```

- Build the Swift client with `./swift-client/build.sh`.
- `bench run` checks that a binary exists, not that it is current. Build again after each source change.
- For a comparison before and after a change, save the baseline results before the new build replaces the binary.
- Start `./cpp-server/run.sh` as a background process, and stop it when the session ends.

## Run

Run `./bench doctor` before a comparison. It checks the binaries and that the clients have equal workloads.

| Option | Trials for each implementation and case | Use |
|---|---|---|
| `--preset smoke` | 1 | Check that the run works. Estimate the duration. |
| None, or `--preset default` | 3 | First look, low confidence |
| `--preset rigorous` | 9 | Comparison with intervals and a p-value |
| `--repetitions N` | N | An explicit count |

A preset changes the repetitions, not the workload sizes.
The total is cases × implementations × repetitions, so count the cases with `list` before a full run.

```shell
./bench run unary-latency --preset smoke
./bench run unary-payload-sweep --case symmetric-1k \
  --implementation current,swift --preset rigorous
./bench run unary-latency --implementation current --platform jvm \
  --repetitions 9 -- --warmup 1000 --operations 20000
```

- Give both sides of a comparison the same overrides after `--`. Don't override the JSON format, the platform
  label, or the case selection: the harness sets them.
- If the port changes, set `--port` for the harness and `GRPC_BENCHMARK_PORT` for the server.
- Run the trials sequentially on an idle machine: no builds, profilers, or other benchmark runs.
  Use the same simulator, server configuration, and power source on both sides.
- Don't change the random trial order. Record the seed.
- The workloads are warm and closed-loop. They don't measure the cost of a cold connection or the latency
  under open-loop overload.
- The warmup is a number of operations, and some default cases measure less than one second.
  Check the measured durations before you interpret a small difference.
- On the JVM, each trial is a new process with a new JIT warmup. Record the JDK version.

## Analyze

`bench run` prints the run directory to stdout and the progress to stderr.

```shell
./bench analyze output/RUN
./bench compare output/RUN:legacy output/RUN:current
./bench compare output/BASELINE:current output/CANDIDATE:current --format csv --output comparison.csv
```

- Save the whole run directory. Check the exit code and `manifest.json` before a claim. `compare` excludes failed trials.
- If the runner was killed, the directory has no manifest and `analyze` rejects it. Run the measurement again.
- A selector is `RUN_DIR:IMPLEMENTATION`. The first selector is the baseline.
- `compare` takes one run directory for each side, and rejects two platforms. Don't change a platform label to
  get a match. `analyze` takes more than one directory.

Interpretation:

- A ratio is the candidate median divided by the baseline median. A lower ratio is better for latency and CPU,
  a higher ratio for throughput.
- Equal comparison keys don't prove equal machines, simulator runtimes, toolchains, or server settings.
  Check the manifests.
- The manifest has the Git revision and a dirty flag, but not the diff. For an experiment that isn't committed,
  save the diff to a file.
- Don't use the samples of all trials as independent observations.
  More operations don't replace missing trials.
- The intervals have no correction for multiple comparisons. One difference in a full matrix is a lead, not a result.
- Report CPU and p99 separately. The count of cases by primary metric can hide a regression in them.
- Equal wall-clock rates under concurrency don't mean equal CPU cost, because a client can use more threads.
  Compare the CPU for each operation or MiB before you call a result a tie.
- A missing counter isn't zero. The JVM client reports only the total CPU time and no resident size.
- Use `--allow-injected-delay-mismatch` only for the sensitivity check of the README, and name the delay in the report.

| Metric | Meaning |
|---|---|
| `message_interarrival` | Time between two response receives. Not the round trip of a message. |
| `request_write_suspension` | Start to return of a request write. Not the acknowledgement of the server. |
| Streaming `parameters.operations` | Requested messages for each active direction, over all streams |
| Streaming `counts.operations` | Request and response messages together, with control requests and final responses. To check that a stream completed, compare the requested count with `counts.request_messages` or `counts.response_messages`. |
| Application MiB/s | Payload bytes, without protocol overhead |
| CPU time | CPU of the client process in the measured phase, all threads. The server process isn't included. |
| `cpu_to_wall_ratio` | Can be more than 1 with more than one thread |
| `max_rss_bytes` | Peak resident memory with the warmup. Not the allocation volume. |

Report the case, the effective parameters, the platform, the revisions, the counts of successful and failed
trials, the ratio, the interval, and the paths of the results. State the uncertainty and the limits of the environment.

## Change workloads

- After a workload change, build the changed clients and run `doctor`.
- Equal workload matrices don't prove equal measurements. Check that the clients complete, cancel, and count
  operations in the same way.
- Don't add tests for the benchmark setup. Check it with builds, `doctor`, and short runs of the real clients.
- For a recorded comparison, use `bench run`, not the `run.sh` helpers: it records the trial order, the failures,
  and the environment.
