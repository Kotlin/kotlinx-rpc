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

If your change touches **any** file under `compiler-plugin/` (sources or build
scripts), use the `verify-compiler-plugin-compatibility` skill. Don't skip it for a
small change.

The compiler plugin must compile against all supported Kotlin versions and Kotlin
Master. A local build for one version isn't sufficient, because the compiler API
differs between versions.
