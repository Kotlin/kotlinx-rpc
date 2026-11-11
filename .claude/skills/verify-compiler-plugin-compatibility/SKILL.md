---
name: verify-compiler-plugin-compatibility
description: >
  Verify that the kotlinx-rpc compiler plugin compiles successfully against multiple
  Kotlin compiler versions, and fix any incompatibilities found. Use this skill whenever
  the user wants to check compiler plugin compatibility across Kotlin versions, test a
  new Kotlin version, fix compiler plugin build failures after a Kotlin upgrade, ensure
  the expect/actual version-specific code is valid for all supported versions, or test
  against Kotlin Master. Trigger on phrases like "verify compiler plugin", "check
  compatibility", "test Kotlin versions", "compiler plugin broken", "fix for Kotlin X.Y",
  "support new Kotlin version", or "Kotlin master".
---

# Verify Compiler Plugin Compatibility

The compiler plugin (`compiler-plugin/`) uses the
[Kotlin Compiler Plugin DevKit](https://github.com/Kotlin/compiler-plugin-dev-kit).
One build compiles the plugin against every supported Kotlin version. Each version is a
Gradle target.

## Versions and targets

`gradle-conventions/src/main/kotlin/util/devkit.kt` declares the supported versions:

- `cliVersions(min)`: the Kotlin releases from `min`, and the Beta and RC builds of the
  next release.
- `ideaVersions(min)`: the compiler versions of IntelliJ IDEA and Android Studio from
  the build `min`.

The DevKit has a built-in list of Kotlin releases and IDE builds. A new Kotlin release
becomes a target after an update of the `devkit` version in
`versions-root/libs.versions.toml`, and of the same version in
`compiler-plugin/settings.gradle.kts` and `gradle-plugin/settings.gradle.kts`.

The target name is `k` and the version in lower case, without `.` and with `_` for `-`:

| Version | Target | Compile task | Test task |
|---|---|---|---|
| `2.4.20` | `k2420` | `compileKotlinK2420` | `k2420Test` |
| `2.5.0-RC` | `k250_rc` | `compileKotlinK250_rc` | `k250_rcTest` |
| `2.4.20-ij262-52` | `k2420_ij262_52` | `compileKotlinK2420_ij262_52` | `k2420_ij262_52Test` |

To verify one version that is not a target, for example a `-dev-` build, use the Kotlin
Master mode: `-Pkotlin.compiler=<VERSION> -Pkotlinx.rpc.kotlinMasterBuild=true`
(see `references/kotlin-master.md`).

## Build Execution: Use Gradle Skills

**All builds MUST go through the `running_gradle_builds` and `running_gradle_tests`
skills.** Do not run `./gradlew` directly via shell.

Use the repository root as `projectRoot`. The compiler plugin is the included build
`:compiler-plugin`.

When you need to understand what a Kotlin compiler API looks like in a specific version,
use the `searching_dependency_sources` skill to search the Kotlin compiler jar for the
class or method name.

## Workflow

### Phase 1: Verify All Versions

1. Compile all versions: `:compiler-plugin:assemble --continue`.
2. Run the tests of all versions: `:compiler-plugin:allTests --continue`.

`--continue` gives the failures of all versions in one build.

Record the result for each version and present a summary table:

```
| Version | Compile | Tests | Errors |
|---------|---------|-------|--------|
| 2.2.20  | PASS    | PASS  |        |
| 2.4.0   | FAIL    |       | 3 errors in IrCompat.post24Dev.kt |
```

If everything passes, report success and stop.

### Phase 2: Diagnose Failures

For each failing compile task, get the errors with `query_build` (`kind="PROBLEMS"` or
`kind="TASKS"` with the task path). Categorize every compilation error:

**Category A -- Import change**: A class/function moved to a different package.
Symptom: `Unresolved reference` on an import.

**Category B -- Signature change**: A method's parameters or return type changed.
Symptom: `None of the following candidates is applicable`, `Too many arguments`.

**Category C -- API replacement**: An entire API was replaced by a new mechanism.
Symptom: `Unresolved reference` on a class or method that no longer exists.

**Category D -- Structural change**: Property became function, List became MutableList, etc.
Symptom: `Val cannot be reassigned`, `Type mismatch`.

### Phase 3: Fix Incompatibilities

Read `references/compat-fix-patterns.md` for the `expect`/`actual` layout and the fix
patterns.

### Phase 4: Re-verify

Repeat Phase 1 until all versions pass. Then present the final summary table.

## Testing with Kotlin Master

**This is a mandatory test for all changes to the compiler plugin.**

Read `references/kotlin-master.md`.

## Updating Golden Test Data

A fix can change the FIR or IR output of the tests. Read `references/golden-test-data.md`.

## Debugging Tips

- A compile error in `commonMain` for one target only means that the compiler API differs
  in that version. Move the call behind an `expect` function.
- `Expected ... has no actual declaration` means that a target is in no group of the
  split. Check `versionHierarchy` in `compiler-plugin/build.gradle.kts`.
- An IDE version (`-ij` or `-dev-`) can fail when a `splitDev` boundary doesn't match the
  build that changed the API. Use `split("<first build with the change>")`.
