# Protobuf benchmarks

Benchmarks for the protobuf encoder and decoder of `protobuf-lite`, on the JVM and on the native target of the
host. The benchmarks use only the `GrpcMarshaller` API, so the same code measures different codec versions.

## Run

```bash
./gradlew :tests:protobuf-benchmarks:benchmark -Pprotobuf.bench.label=<label>
```

- `benchmark` runs all targets. `jvmBenchmark` and `<nativeTarget>Benchmark` run one target, for example
  `macosArm64Benchmark`.
- Each run writes one JSON file for each target to `results/<label>/main/<timestamp>/`. Git ignores `results/`.
  The default label is `local`.
- To compare two codec versions, run the benchmark on each version with a different label.

## Cases

| Case | Message |
|---|---|
| `scalars` | One field of each scalar type. |
| `unknownFields` | `Scalars` bytes, decoded with `ScalarsSubset`. 13 fields are unknown. |
| `strings` | 100 ASCII and 100 non-ASCII strings. |
| `blob` | One 64 KiB `bytes` field. |
| `packed` | Four packed repeated fields with 1,000 values each. |
| `tree` | Nested messages, 5 levels deep with 3 children on each node. |
| `large` | About 1 MiB: 10,000 `Scalars` rows, 1,000 strings, and a 256 KiB `bytes` field. |

## Benchmarks

| Class | Platforms | What it measures |
|---|---|---|
| `CodecBenchmark` | JVM, native | Encode to a `ByteArray` and a `Buffer`. Decode from a `ByteArray` and a multi-segment `Buffer`. |
| `NativeMemoryBenchmark` | Native | Encode into and decode from native memory through `NativeBufferMessageWriter` and `NativeBufferMessageReader`. |
| `NewMessageBenchmark` | JVM, native | Builds a new `Scalars` message, with and without the encode, to measure the size computation. |

Each message caches its encoded size. So the encode benchmarks in `CodecBenchmark` and `NativeMemoryBenchmark` don't
count the size computation.
