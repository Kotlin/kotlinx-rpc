---
name: run-local-verifications
description: >
  Determine which LOCAL verification checks to run for kotlinx-rpc changes and execute
  them on the developer's machine. TeamCity builds and GitHub Actions workflows are out
  of scope -- this skill covers only what can and should be run locally before pushing.
  Use this skill whenever changes are made to the codebase and you need to verify
  correctness before committing or opening a PR. Also use it when the user asks to
  "run checks", "verify changes", "run verifications", "what checks do I need",
  "validate my changes", "pre-PR checks", or "local checks". Trigger proactively after
  completing any code modification task -- even if the user doesn't explicitly ask,
  suggest which verifications are relevant based on what changed.
---

# Run Local Verifications

Identify which verification checks are needed based on what changed, then run them
locally. This skill covers only checks that can be executed on the developer's machine.
TeamCity CI builds and GitHub Actions workflows are out of scope -- they run automatically
on push/PR and cannot be triggered from here.

## How to Execute

Use the `running_gradle_builds` skill for all Gradle build/generation tasks and the
`running_gradle_tests` skill for all Gradle test tasks. Do NOT run `./gradlew` directly
via shell. The only exception is `./update_implicit_types.sh` and
`./validatePublishedArtifacts.sh` which are standalone shell scripts.

## Quick Start: Identify What Changed

Before running anything, determine which files were modified. Use `git diff` or
`git status` to see the changed files, then match them against the decision table below.

## Decision Table

Match changed paths to required verifications. Multiple rows can match -- run all that apply.
The "Gradle task" column shows the task name to pass to the appropriate Gradle skill.

| What changed                                                                           | Verification            | Gradle task / command                                                                     | Why                                                                                |
|----------------------------------------------------------------------------------------|-------------------------|-------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| Any public API in published modules (`core/`, `krpc/`, `grpc/`, `protobuf/`, `utils/`) | ABI compatibility       | `checkLegacyAbi`                                                                          | Ensures binary compatibility is preserved for library consumers                    |
| Any Kotlin/Java source file                                                            | Detekt static analysis  | `detekt`                                                                                  | Code quality. Does NOT fail the build, but check console output for new violations |
| Any published JVM or KMP module source (including `commonMain`)                        | JPMS check              | `:jpms-check:compileJava`                                                                 | Verifies Java module system compatibility. commonMain code compiles to JVM too     |
| New/removed modules or KMP target changes                                              | Artifact validation     | Shell: `./validatePublishedArtifacts.sh -s`                                               | Ensures the published artifact list stays consistent                               |
| New/removed modules or KMP target changes                                              | Platform table          | `dumpPlatformTable --no-configuration-cache` then check git status                        | Updates docs/pages platform table                                                  |
| `gradle.properties` in any module                                                      | Properties sync         | `updateProperties` then check git status                                                  | Keeps all module gradle.properties in sync with root                               |
| `compiler-plugin/` sources or templates                                                | Compiler plugin tests   | `:tests:compiler-plugin-tests:test` (use `running_gradle_tests`)                          | Validates codegen and diagnostics                                                  |
| `compiler-plugin/` CSM templates                                                       | Compiler version compat | Use the `verify-compiler-plugin-compatibility` skill                                      | Ensures all supported Kotlin versions still compile                                |
| `protoc-gen/` sources (any change)                                                     | Protobuf conformance    | `tests:protobuf-conformance:bufGenerateCommonMain` then check git status                  | Codegen changes may affect conformance test output                                 |
| `protoc-gen/` sources (any change)                                                     | Protobuf unit tests     | `:tests:protobuf-unittest:test` (use `running_gradle_tests`)                           | Validates protobuf codegen correctness                                             |
| `protoc-gen/` sources (any change)                                                     | Well-Known Types        | `:protobuf:protobuf-wkt:bufGenerateCommonMain` then check git status                      | Codegen changes may affect WKT generated code                                      |
| `protoc-gen/` sources (any change)                                                     | Implicit imports        | Shell: `./update_implicit_types.sh` then check git status                                 | Keeps protoc-gen implicit import list current                                      |
| `protobuf/protobuf-wkt/` sources                                                       | Well-Known Types        | `:protobuf:protobuf-wkt:bufGenerateCommonMain` then check git status                      | Regenerates WKT code                                                               |
| `krpc/` protocol wire format or serialization                                          | Protocol compat tests   | `:tests:krpc-protocol-compatibility-tests:jvmTest` (use `running_gradle_tests`)           | Ensures wire format backward compatibility                                         |
| `krpc/` any changes                                                                    | kRPC compat tests       | `:tests:krpc-compatibility-tests:jvmTest` (use `running_gradle_tests`)                    | Ensures old/new API compatibility                                                  |
| JS/WASM npm dependency changes or Kotlin version update                                | Yarn lock update        | `kotlinUpgradeYarnLock` and/or `kotlinWasmUpgradeYarnLock`                                | Keeps JS/WASM lock files in sync                                                   |
| `tests/protobuf-conformance/**/known_failures.txt`                                     | Conformance known-failures | `:tests:protobuf-conformance:jvmTest` (see conformance details)                           | Removed entries must actually pass now; added entries must actually fail            |

## Module-Specific Tests

After running the verification checks above, run tests for the specific modules you changed
using the `running_gradle_tests` skill.

**Prefer `jvmTest` for KMP modules** -- if your changes are in `commonMain` only, `jvmTest`
is the fastest way to validate them since common code compiles to all targets. Only run
`jsTest`, `wasmJsTest`, or `nativeTest` if the change is target-specific or you want full
coverage.

Examples:
- Single test: task `<module>:jvmTest --tests "TestClass.testMethod"`
- All JVM tests for a module (preferred for commonMain changes): task `<module>:jvmTest`
- Target-specific: task `<module>:jsTest`, `<module>:wasmJsTest`, `<module>:nativeTest`

## Common Scenarios

### "I changed a public API in `:core`"
1. Run `checkLegacyAbi` via `running_gradle_builds` -- if it fails, run `updateLegacyAbi` and commit
2. Run `detekt` via `running_gradle_builds` -- check output
3. Run `:jpms-check:compileJava` via `running_gradle_builds`
4. Run `:core:jvmTest` via `running_gradle_tests`

### "I modified the compiler plugin"
1. Run `:tests:compiler-plugin-tests:test` via `running_gradle_tests`
2. Use `verify-compiler-plugin-compatibility` skill for multi-version check

### "I added a new published module"
1. Run `dumpPlatformTable --no-configuration-cache` via `running_gradle_builds` then commit if changed
2. Run `./validatePublishedArtifacts.sh --dump -s` via shell then commit if changed
3. Run `updateProperties` via `running_gradle_builds` then commit if changed
4. Run `checkLegacyAbi` via `running_gradle_builds`
5. Run `:jpms-check:compileJava` via `running_gradle_builds`

### "I changed protobuf/gRPC code"
1. Run `:protobuf:protobuf-wkt:bufGenerateCommonMain` via `running_gradle_builds` then commit if changed
2. Run `tests:protobuf-conformance:bufGenerateCommonMain` via `running_gradle_builds` then commit if changed
3. Run `:tests:protobuf-unittest:test` via `running_gradle_tests`
4. Run `./update_implicit_types.sh` via shell then commit if changed (if protoc-gen changed)
5. Run `checkLegacyAbi` via `running_gradle_builds`
6. Run relevant module `jvmTest` via `running_gradle_tests`

### "I changed kRPC code"
1. Run `:tests:krpc-protocol-compatibility-tests:jvmTest` via `running_gradle_tests`
2. Run `:tests:krpc-compatibility-tests:jvmTest` via `running_gradle_tests`
3. Run `checkLegacyAbi` via `running_gradle_builds`
4. Run `detekt` via `running_gradle_builds` -- check output
5. Run relevant module `jvmTest` via `running_gradle_tests`

### "I updated the Kotlin version"
1. Use `verify-compiler-plugin-compatibility` skill
2. Run `kotlinUpgradeYarnLock` and `kotlinWasmUpgradeYarnLock` via `running_gradle_builds`
3. Run `checkLegacyAbi` via `running_gradle_builds` -- API dumps may need updating
4. Run `:jpms-check:compileJava` via `running_gradle_builds`
5. Full test suite

### "I only changed documentation"
1. Verify Writerside markup is valid (CI will build it, not runnable locally)
2. If you changed the changelog: run `updateDocsChangelog` via `running_gradle_builds`

## References

- **Detailed verification guides** (what each check does, failure recovery, edge cases): read `references/verification-details.md` — consult when a check fails or behavior is unclear
- **Troubleshooting**: read `references/troubleshooting.md` — consult when verifications fail unexpectedly
