# Verification Special Cases (Phase C.2)

Mandatory extra verifications beyond `run-local-verifications`. These cannot be
skipped when they apply.

## Conformance `known_failures.txt` changes

If you modified `known_failures.txt` or `native_known_failures.txt` under
`tests/protobuf-conformance/` (removed or added entries), run
`:tests:protobuf-conformance:jvmTest` to confirm:

- removed entries now pass
- added entries actually fail

The `run-local-verifications` skill has the full procedure (§12). This single
`jvmTest` run covers both JVM and native conformance — it builds and invokes the
native binary too.

## Compiler plugin changes

If your change touches **any** file under `compiler-plugin/` (sources, templates,
CSM blocks, or build scripts), invoke the `verify-compiler-plugin-compatibility`
skill. This is not optional and cannot be skipped regardless of how small the
change appears.

The compiler plugin must compile against all supported Kotlin versions and Kotlin
Master. A single-version local build is not sufficient — CSM template interactions
across versions are the most common source of regressions.
