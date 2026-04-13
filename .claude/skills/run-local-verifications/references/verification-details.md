# Detailed Verification Guide

Reference for each verification check — what it does, how to recover from failures,
and edge cases. Read the relevant section when a check fails or when the decision
table entry needs clarification.

## Contents

1. [ABI Compatibility](#1-abi-compatibility)
2. [Detekt Static Analysis](#2-detekt-static-analysis)
3. [JPMS Check](#3-jpms-check)
4. [Artifact Validation](#4-artifact-validation)
5. [Properties Sync](#5-properties-sync)
6. [Platform Table](#6-platform-table)
7. [Compiler Plugin Tests](#7-compiler-plugin-tests)
8. [Generated File Checks](#8-generated-file-checks-implicit-imports-conformance-wkt)
9. [kRPC Compatibility Tests](#9-krpc-compatibility-tests)
10. [Protobuf / Protoc-gen Tests](#10-protobuf--protoc-gen-tests)
11. [Yarn Lock Updates](#11-yarn-lock-updates)
12. [Conformance Known-Failures Check](#12-conformance-known-failures-check)

---

## 1. ABI Compatibility

**Task**: `checkLegacyAbi` (use `running_gradle_builds`)

**When**: Any change to public API surface in published modules -- adding/removing/changing
public or protected classes, functions, properties, or their signatures.

**What it checks**: Binary compatibility using Kotlin's ABI validation. Compares current
API against stored API dumps. Internal APIs (annotated `@InternalRpcApi` or in
`*.internal.**` packages) are excluded.

**If it fails**:
- If you intentionally changed the API, run task `updateLegacyAbi` and commit the updated dumps
- If the change was unintentional, revert the public API change
- For KMP modules, klib dumps are also validated

## 2. Detekt Static Analysis

**Task**: `detekt` (use `running_gradle_builds`)

**When**: Any Kotlin source change. Run it proactively -- it catches real issues.

**What it checks**: Code complexity, coroutine anti-patterns, naming conventions,
potential bugs, performance issues. Configuration in `detekt/config.yaml`.

**Important**: Detekt does NOT fail the build (`ignoreFailures = true`). You must
read the console output to see violations. Check `detekt/reports/` for HTML reports.

**If violations found**: Fix them. The baseline file (`detekt/baseline.xml`) contains
pre-existing violations that are grandfathered in -- new code should not add to it.

## 3. JPMS Check

**Task**: `:jpms-check:compileJava` (use `running_gradle_builds`)

**When**: Changes to any published JVM or KMP module, including `commonMain` source sets
(because common code compiles to JVM). Especially important when adding new dependencies
or changing module structure.

**What it checks**: Compiles a generated `module-info.java` that requires all published
kotlinx-rpc modules, verifying they declare proper JPMS exports. Uses Java 17 toolchain.

**If it fails**: A module is missing proper JPMS metadata. Check the generated
`jpms-check/src/main/java/module-info.java` and ensure the failing module's
`Automatic-Module-Name` is set correctly in its jar manifest.

## 4. Artifact Validation

**Command**: Shell script `./validatePublishedArtifacts.sh -s`

**When**: Adding/removing published modules, changing which KMP targets a module supports,
or modifying publication configuration.

**What it checks**: The set of artifacts produced by the build matches the expected list
stored in `gradle/artifacts/` dump files.

**If it fails**:
- If you intentionally changed artifacts, run `./validatePublishedArtifacts.sh --dump -s` and commit
- Review the diff in `gradle/artifacts/` to confirm only expected changes

## 5. Properties Sync

**Task**: `updateProperties` (use `running_gradle_builds`), then check `git status`

**When**: Changing `gradle.properties` in the root or any subproject.

**What it checks**: All subproject `gradle.properties` files are in sync with the root.

**Pattern**: Run the task, then check `git status`. If there are changes to `.properties`
files, commit them alongside your other changes.

## 6. Platform Table

**Task**: `dumpPlatformTable --no-configuration-cache` (use `running_gradle_builds`), then check `git status`

**When**: Adding/removing modules or changing which KMP targets a module declares.

**What it checks**: The platform support table in `docs/pages/kotlinx-rpc/topics/platforms.topic`
reflects the actual KMP target configuration.

**Pattern**: Run the task, then check `git status`. If `platforms.topic` changed, commit it.

## 7. Compiler Plugin Tests

**Task**: `:tests:compiler-plugin-tests:test` (use `running_gradle_tests`)

**When**: Any change to `compiler-plugin/` code, CSM templates, or code generation logic.

**What it checks**: Box tests verify generated code compiles and runs correctly.
Diagnostic tests verify the compiler plugin reports errors/warnings correctly.

**Test data**: `tests/compiler-plugin-tests/src/testData/box/` and `.../diagnostics/`

**Updating golden files**: Use the `updateTestData.sh` script:
```bash
./updateTestData.sh BoxTest           # update all box test golden files
./updateTestData.sh DiagnosticTest    # update all diagnostic test golden files
./updateTestData.sh BoxTest testName  # update a single test's golden file
```

The script passes `-Pkotlin.test.update.test.data=true` to Gradle. The build script at
`tests/compiler-plugin-tests/build.gradle.kts` reads this Gradle project property and
forwards it to the test JVM via `systemProperty()`. The test framework then overwrites
golden files (`.fir.txt`, `.fir.ir.txt`) with actual output instead of asserting.

**Do NOT use the Gradle MCP's `additionalSystemProps`** to pass this property -- that
sets properties on the Gradle daemon process, not on the forked test JVM, so it has no
effect. The property must be passed as a `-P` Gradle project property (either via the
shell script or by adding `-Pkotlin.test.update.test.data=true` to the Gradle arguments
when using the `running_gradle_tests` skill).

After updating, review diffs with `git diff` to confirm changes match expectations.

**Note**: These tests are skipped for Kotlin master builds.

## 8. Generated File Checks (implicit imports, conformance, WKT)

Several checks follow the same pattern: run a generation task, then verify no files
changed in git. These catch cases where generated files are stale.

**Pattern for all of these**:
1. Run the generation task via `running_gradle_builds` (or shell for `./update_implicit_types.sh`)
2. Check `git status` for changes in the relevant directory
3. If files changed, commit the regenerated files with your PR

The specific tasks are in the decision table in SKILL.md. The key insight is that these
are *not* tests that pass or fail -- they're generators that must be re-run when their
inputs change, and the generated output must be committed.

## 9. kRPC Compatibility Tests

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

## 10. Protobuf / Protoc-gen Tests

**When**: Any changes to `protoc-gen/` sources.

All five checks should be run together because protoc-gen is the code generator that
produces output consumed by conformance tests, unit tests, WKT, and implicit imports:
1. Task `tests:protobuf-conformance:bufGenerateCommonMain` (use `running_gradle_builds`) then check git status
2. Task `:tests:protobuf-unittest:test` (use `running_gradle_tests`)
3. Task `:tests:protobuf-conformance:jvmTest` (use `running_gradle_tests`)
   **Native coverage**: `jvmTest` runs both `conformance()` (JVM client) and
   `nativeConformance()` (builds and executes the host native binary). No separate
   native test task is needed.
4. Task `:protobuf:protobuf-wkt:bufGenerateCommonMain` (use `running_gradle_builds`) then check git status
5. Shell: `./update_implicit_types.sh` then check git status

## 11. Yarn Lock Updates

**Tasks**: `kotlinUpgradeYarnLock` and/or `kotlinWasmUpgradeYarnLock` (use `running_gradle_builds`)

**When**: Changing JS or WASM npm dependencies, or updating the Kotlin version (which
can change the Kotlin/JS or Kotlin/WASM runtime).

**If builds fail after**: Delete `build/js/` and `build/wasm/` plus any `package-lock.json`,
then re-run the upgrade tasks.

## 12. Conformance Known-Failures Check

**Files** (both under `tests/protobuf-conformance/`):
- `src/jvmTest/resources/known_failures.txt` -- JVM conformance failures
- `src/jvmTest/resources/native_known_failures.txt` -- native-specific conformance failures

**Task**: `:tests:protobuf-conformance:jvmTest` (use `running_gradle_tests`)

**When**: Any change to either known-failures file. Each file is a contract -- listed
tests are expected to fail, unlisted tests are expected to pass. When you remove an
entry (claiming the fix makes it pass) or add an entry (acknowledging a new failure),
the conformance test suite must confirm reality matches the file.

**How to verify**:
1. Run `:tests:protobuf-conformance:jvmTest` via `running_gradle_tests`
2. Check that **removed entries now pass** and **added entries actually fail**
3. If a removed entry still fails, either the fix is incomplete or the wrong entry
   was removed -- investigate before proceeding

**Native coverage**: `jvmTest` runs two `@TestFactory` methods: `conformance()` (JVM
client, uses `known_failures.txt`) and `nativeConformance()` (native binary, uses
`native_known_failures.txt`). The native binary is built and executed as part of
`jvmTest` -- no separate native test task is needed.
