---
name: verify-compiler-plugin-compatibility
description: >
  Verify that the kotlinx-rpc compiler plugin compiles successfully against multiple
  Kotlin compiler versions, and fix any incompatibilities found. Use this skill whenever
  the user wants to check compiler plugin compatibility across Kotlin versions, test a
  new Kotlin version, fix compiler plugin build failures after a Kotlin upgrade, ensure
  the CSM templates produce valid code for all supported versions, or test against Kotlin
  Master. Trigger on phrases like "verify compiler plugin", "check compatibility", "test
  Kotlin versions", "compiler plugin broken", "fix for Kotlin X.Y", "support new Kotlin
  version", or "Kotlin master".
---

# Verify Compiler Plugin Compatibility

Systematically build the compiler plugin against each provided Kotlin compiler version,
identify compilation failures, and fix them.

## Input

The user provides one or more Kotlin versions to verify. If none are given, determine
the set of versions to test by inspecting the CSM template version ranges in these files:
- `compiler-plugin/compiler-plugin-backend/src/main/templates/kotlinx/rpc/codegen/VersionSpecificApiImpl.kt`
- `compiler-plugin/compiler-plugin-k2/src/main/templates/kotlinx/rpc/codegen/FirVersionSpecificApiImpl.kt`

Extract the distinct version ranges from `//##csm specific=[...]` directives. For each
range, pick one **actually published** Kotlin release that falls within it. Also include
the current default from `versions-root/libs.versions.toml` `kotlin-lang`.

### CSM range boundaries are NOT real Kotlin versions

CSM range lower bounds (e.g., `2.1.22`, `2.1.11`, `2.4.0`) represent the Kotlin version
that introduced an API change, but that exact version may never have been published as a
stable release. Kotlin publishes specific patch versions (like `2.1.0`, `2.1.20`, `2.2.0`,
`2.2.20`), not every number in sequence. **Never use CSM boundary values directly as build
versions** — they will fail to resolve from Maven Central.

### Resolving real versions for each range

For each CSM range, find the lowest published Kotlin release that falls within it:

1. Read the range bounds (e.g., `2.1.22...2.3.*`)
2. Look up actual published Kotlin releases. Use the `managing_gradle_dependencies` skill
   to query Maven Central for available versions of `org.jetbrains.kotlin:kotlin-stdlib`
   if you are unsure which versions exist.
3. Pick the first published version that falls within the range.
4. If the range's lower bound hasn't been published yet and no published version falls
   within it, that range covers a future Kotlin version — test it via the **Kotlin Master**
   workflow (see `references/kotlin-master.md`), not by guessing a version number.

Kotlin publishes in a pattern: `X.Y.0`, then `X.Y.10`, `X.Y.20`, etc. (not sequential
patches). For example, after `2.1.0` the next release was `2.1.10`, then `2.1.20`,
then `2.1.21` — there was never a `2.1.1` through `2.1.9` or a `2.1.11` through `2.1.19`.

## Build Execution: Use Gradle Skills

**All builds MUST go through the `running_gradle_builds` skill.** Do not run `./gradlew`
directly via shell. The `running_gradle_builds` skill provides background orchestration,
structured failure analysis via `inspect_build`, and reliable output capture.

When invoking builds through the skill, use:
- `projectRoot`: absolute path to `compiler-plugin/` directory
- Gradle task: `clean compileKotlin`
- Additional arguments: `-Pkotlin.compiler=<VERSION>`

Use `inspect_build` to diagnose failures -- it gives structured access to errors,
task outputs, and build problems rather than raw log parsing.

When you need to understand what a Kotlin compiler API looks like in a specific version,
use the `searching_dependency_sources` skill to search the Kotlin compiler jar for the
class or method name. This reveals new signatures, package locations, and parameter lists.

## Workflow

### Phase 1: Verify Each Version

For each Kotlin version, build the compiler plugin using the `running_gradle_builds`
skill with these arguments:
- Tasks: `clean compileKotlin`
- Extra args: `-Pkotlin.compiler=<VERSION>`
- Project root: absolute path to the `compiler-plugin/` directory

The `-Pkotlin.compiler=<VERSION>` flag overrides the Kotlin compiler version at build
time. The CSM template processor reads this to select the right code blocks.

Record the result for each version: PASS or FAIL with the error summary.

After testing all versions, present a summary table:

```
| Version | Status | Errors |
|---------|--------|--------|
| 2.1.0   | PASS   |        |
| 2.1.20  | PASS   |        |
| 2.2.0   | FAIL   | 3 errors in FirVersionSpecificApiImpl.kt |
| 2.3.0   | FAIL   | 5 errors in VersionSpecificApiImpl.kt    |
```

If everything passes, report success and stop.

### Phase 2: Diagnose Failures

For each failing version, use `inspect_build` with `mode="details"` to get structured
error information. Categorize every compilation error:

**Category A -- Import change**: A class/function moved to a different package.
Symptom: `Unresolved reference` on an import.

**Category B -- Signature change**: A method's parameters or return type changed.
Symptom: `None of the following candidates is applicable`, `Too many arguments`.

**Category C -- API replacement**: An entire API was replaced by a new mechanism.
Symptom: `Unresolved reference` on a class or method that no longer exists.

**Category D -- Structural change**: Property became function, List became MutableList, etc.
Symptom: `Val cannot be reassigned`, `Type mismatch`.

To understand what the new API looks like, use the `searching_dependency_sources` skill
to search the Kotlin compiler sources for the class or method name.

### Phase 3: Fix Incompatibilities

Read `references/csm-fix-patterns.md` for the CSM template system, fix patterns (import
changes, signature changes, new VS methods, checker signatures), and the template files
reference table listing all CSM files that may need changes.

The key principle: **`default` blocks hold the latest version's code; `specific` blocks
hold older versions.**

### Phase 4: Re-verify

After applying fixes, re-run the build (via the `running_gradle_builds` skill) for every
version that failed. If new errors appear, go back to Phase 2. Repeat until all versions
pass.

Then present the final summary:

```
All versions verified successfully:
| Version | Status |
|---------|--------|
| 2.1.0   | PASS   |
| 2.1.20  | PASS   |
| 2.2.0   | PASS   |
| 2.3.0   | PASS   |
```

## Testing with Kotlin Master

**This is a mandatory test for all changes to the compiler plugin.**

If the user requests Kotlin Master testing, or after fixing incompatibilities for released
versions, read `references/kotlin-master.md` for the full download, build, and verification
workflow.

## Updating Golden Test Data

After fixing CSM templates, golden file mismatches may occur in
`:tests:compiler-plugin-tests`. Read `references/golden-test-data.md` for the
`updateTestData.sh` usage and important caveats about passing properties to the test JVM.

## Debugging Tips

- Use `inspect_build` from the `running_gradle_builds` skill to get structured error
  details instead of parsing raw logs.
- Inspect generated CSM output in `<module>/build/generated-sources/csm/` after building.
- CSM logs matched blocks: look for `[CSM] Matched specific` or `[CSM] Matched default`
  in build output (use `inspect_build` with `consoleTail=true` to find these).
- If an import section error occurs, check that the `specific` block has ALL needed
  imports for that version, not just the changed ones.
- When unsure about new API shape, use the `searching_dependency_sources` skill to search
  the Kotlin compiler jar for the symbol name.
