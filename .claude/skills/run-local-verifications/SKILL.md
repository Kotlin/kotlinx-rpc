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
| `protoc-gen/` sources (any change)                                                     | Protobuf conformance    | `tests:protobuf-conformance:bufGenerateMain` then check git status                        | Codegen changes may affect conformance test output                                 |
| `protoc-gen/` sources (any change)                                                     | Protobuf unit tests     | `:tests:protobuf-unittest:jvmTest` (use `running_gradle_tests`)                           | Validates protobuf codegen correctness                                             |
| `protoc-gen/` sources (any change)                                                     | Well-Known Types        | `:protobuf:protobuf-wkt:bufGenerateCommonMain` then check git status                      | Codegen changes may affect WKT generated code                                      |
| `protoc-gen/` sources (any change)                                                     | Implicit imports        | Shell: `./update_implicit_types.sh` then check git status                                 | Keeps protoc-gen implicit import list current                                      |
| `protobuf/protobuf-wkt/` sources                                                       | Well-Known Types        | `:protobuf:protobuf-wkt:bufGenerateCommonMain` then check git status                      | Regenerates WKT code                                                               |
| `krpc/` protocol wire format or serialization                                          | Protocol compat tests   | `:tests:krpc-protocol-compatibility-tests:jvmTest` (use `running_gradle_tests`)           | Ensures wire format backward compatibility                                         |
| `krpc/` any changes                                                                    | kRPC compat tests       | `:tests:krpc-compatibility-tests:jvmTest` (use `running_gradle_tests`)                    | Ensures old/new API compatibility                                                  |
| JS/WASM npm dependency changes or Kotlin version update                                | Yarn lock update        | `kotlinUpgradeYarnLock` and/or `kotlinWasmUpgradeYarnLock`                                | Keeps JS/WASM lock files in sync                                                   |

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

## Detailed Verification Guide

### 1. ABI Compatibility

**Task**: `checkLegacyAbi` (use `running_gradle_builds`)

**When**: Any change to public API surface in published modules -- adding/removing/changing
public or protected classes, functions, properties, or their signatures.

**What it checks**: Binary compatibility using Kotlin's ABI validation. Compares current
API against stored API dumps. Internal APIs (annotated `@InternalRpcApi` or in
`*.internal.**` packages) are excluded.

**If it fails**:
- If you intentionally changed the API, run task `legacyApiDump` and commit the updated dumps
- If the change was unintentional, revert the public API change
- For KMP modules, klib dumps are also validated

### 2. Detekt Static Analysis

**Task**: `detekt` (use `running_gradle_builds`)

**When**: Any Kotlin source change. Run it proactively -- it catches real issues.

**What it checks**: Code complexity, coroutine anti-patterns, naming conventions,
potential bugs, performance issues. Configuration in `detekt/config.yaml`.

**Important**: Detekt does NOT fail the build (`ignoreFailures = true`). You must
read the console output to see violations. Check `detekt/reports/` for HTML reports.

**If violations found**: Fix them. The baseline file (`detekt/baseline.xml`) contains
pre-existing violations that are grandfathered in -- new code should not add to it.

### 3. JPMS Check

**Task**: `:jpms-check:compileJava` (use `running_gradle_builds`)

**When**: Changes to any published JVM or KMP module, including `commonMain` source sets
(because common code compiles to JVM). Especially important when adding new dependencies
or changing module structure.

**What it checks**: Compiles a generated `module-info.java` that requires all published
kotlinx-rpc modules, verifying they declare proper JPMS exports. Uses Java 17 toolchain.

**If it fails**: A module is missing proper JPMS metadata. Check the generated
`jpms-check/src/main/java/module-info.java` and ensure the failing module's
`Automatic-Module-Name` is set correctly in its jar manifest.

### 4. Artifact Validation

**Command**: Shell script `./validatePublishedArtifacts.sh -s`

**When**: Adding/removing published modules, changing which KMP targets a module supports,
or modifying publication configuration.

**What it checks**: The set of artifacts produced by the build matches the expected list
stored in `gradle/artifacts/` dump files.

**If it fails**:
- If you intentionally changed artifacts, run `./validatePublishedArtifacts.sh --dump -s` and commit
- Review the diff in `gradle/artifacts/` to confirm only expected changes

### 5. Properties Sync

**Task**: `updateProperties` (use `running_gradle_builds`), then check `git status`

**When**: Changing `gradle.properties` in the root or any subproject.

**What it checks**: All subproject `gradle.properties` files are in sync with the root.

**Pattern**: Run the task, then check `git status`. If there are changes to `.properties`
files, commit them alongside your other changes.

### 6. Platform Table

**Task**: `dumpPlatformTable --no-configuration-cache` (use `running_gradle_builds`), then check `git status`

**When**: Adding/removing modules or changing which KMP targets a module declares.

**What it checks**: The platform support table in `docs/pages/kotlinx-rpc/topics/platforms.topic`
reflects the actual KMP target configuration.

**Pattern**: Run the task, then check `git status`. If `platforms.topic` changed, commit it.

### 7. Compiler Plugin Tests

**Task**: `:tests:compiler-plugin-tests:test` (use `running_gradle_tests`)

**When**: Any change to `compiler-plugin/` code, CSM templates, or code generation logic.

**What it checks**: Box tests verify generated code compiles and runs correctly.
Diagnostic tests verify the compiler plugin reports errors/warnings correctly.

**Test data**: `tests/compiler-plugin-tests/src/testData/box/` and `.../diagnostics/`

**Updating golden files**: pass system property `kotlin.test.update.test.data=true`

**Note**: These tests are skipped for Kotlin master builds.

### 8. Generated File Checks (implicit imports, conformance, WKT)

Several checks follow the same pattern: run a generation task, then verify no files
changed in git. These catch cases where generated files are stale.

**Pattern for all of these**:
1. Run the generation task via `running_gradle_builds` (or shell for `./update_implicit_types.sh`)
2. Check `git status` for changes in the relevant directory
3. If files changed, commit the regenerated files with your PR

The specific tasks are in the decision table above. The key insight is that these are
*not* tests that pass or fail -- they're generators that must be re-run when their inputs
change, and the generated output must be committed.

### 9. kRPC Compatibility Tests

**Tasks** (use `running_gradle_tests` for both):
- `:tests:krpc-protocol-compatibility-tests:jvmTest` -- wire format compatibility
- `:tests:krpc-compatibility-tests:jvmTest` -- API compatibility

**When**: Any changes to `krpc/` modules. Protocol compat tests are especially critical
when touching wire format, serialization, or message framing.

**What they check**: Protocol compat tests verify that messages produced by the current
version can be read by older versions and vice versa (covers v0.8, v0.9, v0.10, latest).
API compat tests verify old API works with new implementation using custom `oldApi` and
`newApi` source sets.

**If protocol compat fails**: You likely broke backward wire compatibility. This is a
serious issue -- discuss with the team before proceeding.

### 10. Protobuf / Protoc-gen Tests

**When**: Any changes to `protoc-gen/` sources.

All four checks should be run together because protoc-gen is the code generator that
produces output consumed by conformance tests, unit tests, WKT, and implicit imports:
1. Task `tests:protobuf-conformance:bufGenerateMain` (use `running_gradle_builds`) then check git status
2. Task `:tests:protobuf-unittest:jvmTest` (use `running_gradle_tests`)
3. Task `:protobuf:protobuf-wkt:bufGenerateCommonMain` (use `running_gradle_builds`) then check git status
4. Shell: `./update_implicit_types.sh` then check git status

### 11. Yarn Lock Updates

**Tasks**: `kotlinUpgradeYarnLock` and/or `kotlinWasmUpgradeYarnLock` (use `running_gradle_builds`)

**When**: Changing JS or WASM npm dependencies, or updating the Kotlin version (which
can change the Kotlin/JS or Kotlin/WASM runtime).

**If builds fail after**: Delete `build/js/` and `build/wasm/` plus any `package-lock.json`,
then re-run the upgrade tasks.

## Common Scenarios

### "I changed a public API in `:core`"
1. Run `checkLegacyAbi` via `running_gradle_builds` -- if it fails, run `legacyApiDump` and commit
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
2. Run `tests:protobuf-conformance:bufGenerateMain` via `running_gradle_builds` then commit if changed
3. Run `:tests:protobuf-unittest:jvmTest` via `running_gradle_tests`
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
1. Run `cd cinterop-c && ./toolchain/precompute_konan_llvm_bundles.py` via shell then commit if changed
2. Use `verify-compiler-plugin-compatibility` skill
3. Run `kotlinUpgradeYarnLock` and `kotlinWasmUpgradeYarnLock` via `running_gradle_builds`
4. Run `checkLegacyAbi` via `running_gradle_builds` -- API dumps may need updating
5. Run `:jpms-check:compileJava` via `running_gradle_builds`
6. Full test suite

### "I only changed documentation"
1. Verify Writerside markup is valid (CI will build it, not runnable locally)
2. If you changed the changelog: run `updateDocsChangelog` via `running_gradle_builds`

## Troubleshooting Verification Failures

If verifications fail unexpectedly:

1. **Clean and retry**: Run `clean` via `running_gradle_builds`, then stop daemons with `--stop`, then rerun
2. **Skip caches**: Add `--rerun-tasks --no-configuration-cache --no-build-cache` flags
3. **JS/WASM issues**: Delete `package-lock.json` + `build/{js,wasm}`, then run yarn lock upgrade tasks
