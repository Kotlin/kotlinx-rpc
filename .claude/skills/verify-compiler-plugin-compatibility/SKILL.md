---
name: verify-compiler-plugin-compatibility
description: Checks that the compiler plugin compiles and passes its tests on each supported Kotlin and IDE compiler version and on Kotlin Master, and fixes the incompatibilities. Use for each change in compiler-plugin/, for a new Kotlin version, and for a compiler plugin build that fails on one Kotlin version.
---

# Verify Compiler Plugin Compatibility

The compiler plugin (`compiler-plugin/`) uses the
[Kotlin Compiler Plugin DevKit](https://github.com/Kotlin/compiler-plugin-dev-kit).
One build compiles the plugin against each supported Kotlin version. Each version is a
Gradle target.

## Versions and targets

`gradle-conventions/src/main/kotlin/util/devkit.kt` declares the supported versions:

| Function | Versions | `min` in `versions-root/libs.versions.toml` |
|---|---|---|
| `cliVersions(min)` | The Kotlin releases from `min`, and the Beta and RC builds of the next release | `kotlin-min` |
| `ideaVersions(min)` | The compiler versions of IntelliJ IDEA and Android Studio from the build `min` | `idea-min` |

The DevKit has the list of Kotlin releases and IDE builds. To get a new Kotlin release as
a target, update the `devkit` version in `versions-root/libs.versions.toml`.

The target name is `k` and the version in lower case, without `.` and with `_` for `-`:

| Version | Target | Compile task | Test task |
|---|---|---|---|
| `2.4.20` | `k2420` | `compileKotlinK2420` | `k2420Test` |
| `2.5.0-RC` | `k250_rc` | `compileKotlinK250_rc` | `k250_rcTest` |
| `2.4.20-ij262-52` | `k2420_ij262_52` | `compileKotlinK2420_ij262_52` | `k2420_ij262_52Test` |

To check a version that isn't a target, for example a `-dev-` build, use the Kotlin
Master mode (`references/kotlin-master.md`).

## Gradle

- Run all builds with the `running_gradle_builds` and `running_gradle_tests` skills, not
  with `./gradlew`.
- Use the repository root as `projectRoot`. The compiler plugin is the included build
  `:compiler-plugin`.
- To read a compiler API of one Kotlin version, search the Kotlin compiler JAR with the
  `searching_dependency_sources` skill.

## Workflow

1. Compile all versions: `:compiler-plugin:assemble --continue`.
2. Run the tests of all versions: `:compiler-plugin:allTests --continue`.
   With `--continue`, one build gives the failures of all versions.
3. Show the result of each version in a table. If all versions pass, stop.

   ```
   | Version | Compile | Tests | Errors |
   |---------|---------|-------|--------|
   | 2.3.0   | PASS    | PASS  |        |
   | 2.4.0   | FAIL    |       | 3 errors in FirCompat.post24Dev.kt |
   ```
4. For each compile task that fails, get the errors with `query_build`
   (`kind="PROBLEMS"`, or `kind="TASKS"` with the task path). Find the change behind
   each error:

   | Change of the compiler API | Compiler error |
   |---|---|
   | A declaration moved to another package | `Unresolved reference` on an import |
   | The parameters or the return type of a function changed | `None of the following candidates is applicable`, `Too many arguments` |
   | A new API replaced a class or a function | `Unresolved reference` on the class or the function |
   | A property became a function, or a type changed (`List` to `MutableList`) | `Val cannot be reassigned`, `Type mismatch` |
5. Fix each error with the patterns in `references/compat-fix-patterns.md`.
6. If a fix changes the FIR or IR output of the tests, update the golden files
   (`references/golden-test-data.md`).
7. Repeat from step 1 until all versions pass. Then show the table again.
8. Test with Kotlin Master (`references/kotlin-master.md`). Each change of the compiler
   plugin must pass this test.

## Other errors

| Error | Cause | Fix |
|---|---|---|
| A compile error in `commonMain` for one target | The compiler API differs in that version | Move the call behind an `expect` function |
| `Expected ... has no actual declaration` | A target isn't in any group of a split | Check `versionHierarchy` in `compiler-plugin/build.gradle.kts` |
| A compile error for an IDE version (`-ij` or `-dev-`) | A `splitDev` boundary doesn't match the build that changed the API | Use `split("<first build with the change>")` |
