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

The user provides one or more Kotlin versions to verify. If none are given, check all
currently known supported versions by inspecting the CSM template version ranges in
these files:
- `compiler-plugin/compiler-plugin-backend/src/main/templates/kotlinx/rpc/codegen/VersionSpecificApiImpl.kt`
- `compiler-plugin/compiler-plugin-k2/src/main/templates/kotlinx/rpc/codegen/FirVersionSpecificApiImpl.kt`

Extract the distinct version ranges from `//##csm specific=[...]` directives and pick
one representative version per range (the lower bound), plus the current default from
`versions-root/libs.versions.toml` `kotlin-lang`. These are the versions to verify.

The user may also request testing against **Kotlin Master** -- see the dedicated section
below.

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
| 2.2.0   | PASS   |        |
| 2.3.0   | FAIL   | 3 errors in FirVersionSpecificApiImpl.kt |
| 2.4.0   | FAIL   | 5 errors in VersionSpecificApiImpl.kt    |
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

Apply fixes using the project's established patterns. All version-dependent code lives in
CSM template files (under `src/main/templates/` in each module). The key principle:
**`default` blocks hold the latest version's code; `specific` blocks hold older versions.**

#### The CSM Template System

Template files use `//##csm` directives that the build processes based on the active
Kotlin version. Only one code path is selected per section.

```kotlin
//##csm <section-name>
//##csm specific=[<version-range>]
// code for older version range
//##csm /specific
//##csm default
// code for latest/current versions
//##csm /default
//##csm /<section-name>
```

Version ranges: `2.1.0...2.1.21` (inclusive), `2.3.0...2.*` (wildcard), comma-separated
for multiple: `[2.1.0...2.1.10, 2.1.20-ij243-*]`.

#### Fix Pattern: New Kotlin Version Breaks Existing Code

Move the current `default` code into a `specific` block bounded to its working range,
then write the new version's code as the new `default`:

```kotlin
//##csm someSection
//##csm specific=[<old-lower>...<old-upper>]
// old code (was previously in default)
//##csm /specific
//##csm default
// new code for the new Kotlin version
//##csm /default
//##csm /someSection
```

If there were already `specific` blocks, keep them and adjust ranges as needed.

#### Fix Pattern: Import Changes

Import sections use a `<filename>-import` naming convention. Each `specific` block must
contain the COMPLETE set of imports for that version -- not just the diff:

```kotlin
//##csm MyFile.kt-import
//##csm specific=[2.1.0...2.3.*]
import org.jetbrains.kotlin.old.package.SomeClass
import org.jetbrains.kotlin.other.Thing  // unchanged but still listed
//##csm /specific
//##csm default
import org.jetbrains.kotlin.new.package.SomeClass
import org.jetbrains.kotlin.other.Thing  // unchanged but still listed
//##csm /default
//##csm /MyFile.kt-import
```

#### Fix Pattern: New VersionSpecificApi Method

When a non-template source file needs version-dependent behavior, add a method to the
abstraction layer:

**For IR backend:**
1. Add method to interface: `compiler-plugin-backend/src/main/kotlin/kotlinx/rpc/codegen/VersionSpecificApi.kt`
2. Implement with CSM blocks in: `compiler-plugin-backend/src/main/templates/kotlinx/rpc/codegen/VersionSpecificApiImpl.kt`
3. Name convention: append `VS` suffix (e.g., `referenceClassVS`)
4. Call sites use: `context.vsApi { myNewMethodVS(...) }`

**For FIR K2:**
1. Add method to interface: `compiler-plugin-k2/src/main/kotlin/kotlinx/rpc/codegen/FirVersionSpecificApi.kt`
2. Implement with CSM blocks in: `compiler-plugin-k2/src/main/templates/kotlinx/rpc/codegen/FirVersionSpecificApiImpl.kt`
3. Call sites use: `vsApi { myNewMethodVS(...) }`

#### Fix Pattern: FIR Checker Signature Changes

Kotlin periodically changes how FIR checkers receive `CheckerContext` and
`DiagnosticReporter` (explicit params vs context receivers). The VS wrapper classes in
`compiler-plugin-k2/src/main/templates/kotlinx/rpc/codegen/checkers/FirRpcCheckersVS.kt`
adapt between versions. All wrappers follow the same pattern -- fix them all consistently.

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
| 2.2.0   | PASS   |
| 2.3.0   | PASS   |
| 2.4.0   | PASS   |
```

## Testing with Kotlin Master

**This is a mandatory test for all changes to the compiler plugin,.**

Kotlin Master is the latest dev/nightly build from the Kotlin team. It is used to catch
incompatibilities before a Kotlin version is officially released.

### Step 1: Download Kotlin Master Artifacts

Run the `dowload_kotlin_master.sh` script in the project root:

```bash
./dowload_kotlin_master.sh
```

This requires a `BUILD_SERVER_TOKEN` -- either as an environment variable or in
`~/.gradle/gradle.properties` as `buildserver.token=<token>`. The script:
1. Queries JetBrains TeamCity for the latest successful `Kotlin_KotlinPublic_Artifacts` build
2. Downloads the Maven artifacts archive
3. Extracts them into a `lib-kotlin/` directory at the project root

The script prints the build ID and the Kotlin version number (e.g., `2.4.0-dev-12345`).
Note this version -- it is the value to pass as `-Pkotlin.compiler=<VERSION>`.

### Step 2: Enable Kotlin Master Mode

Set the Gradle property `kotlinx.rpc.kotlinMasterBuild=true`. This does two things:
- Adds the `lib-kotlin/` directory as a Maven repository so Gradle resolves the
  downloaded artifacts
- **Skips** `:tests:compiler-plugin-tests` (box/diagnostic tests), which typically do not
  compile against unreleased Kotlin versions

You can set this property via:
- Command-line: `-Pkotlinx.rpc.kotlinMasterBuild=true`
- Or uncomment the line in `gradle.properties`:
  ```properties
  kotlinx.rpc.kotlinMasterBuild=true
  ```

### Step 3: Build

Use the `running_gradle_builds` skill to build the compiler plugin with the master
version:
- Tasks: `clean compileKotlin`
- Extra args: `-Pkotlin.compiler=<MASTER_VERSION> -Pkotlinx.rpc.kotlinMasterBuild=true`

#### Step 4: Fix and Re-verify

Follow the same Phase 2-4 workflow as for released versions. When adding CSM blocks for
Kotlin Master, the new code goes in `default` (since Master represents the future), and
the previous release's code moves into a `specific` block with a bounded range.

### Kotlin Master Caveats

- Master versions have dev suffixes like `2.4.0-dev-12345`. Exact prefix matches work
  fine (e.g., `2.4.0-dev-*`), but version ranges strip the suffix before comparison, so
  a range like `2.4.0...2.4.*` will match `2.4.0-dev-12345` as `2.4.0`. Non-stable
  suffixes are not allowed inside range bounds -- use prefix patterns instead for dev
  versions.
- Kotlin Master APIs may change again before release. Fixes for Master should be
  considered provisional until the Kotlin version is released.
- Remember to clean up: when done, you can remove the `lib-kotlin/` directory and
  re-comment `kotlinx.rpc.kotlinMasterBuild` in `gradle.properties`.

## Template Files Reference

All CSM template files that may need changes:

| Module | File | What it handles |
|--------|------|-----------------|
| backend | `templates/.../VersionSpecificApiImpl.kt` | IR API abstraction (symbol lookup, constructors, parameters, annotations) |
| backend | `templates/.../RpcIrServiceProcessorDelegate.kt` | IR visitor base class (`IrElementTransformer` vs `IrTransformer`) |
| backend | `templates/.../RpcIrProtoProcessorDelegate.kt` | Protobuf IR visitor base class |
| k2 | `templates/.../FirVersionSpecificApiImpl.kt` | FIR API abstraction (type resolution, declarations, annotations) |
| k2 | `templates/.../checkers/FirRpcCheckersVS.kt` | FIR checker method signatures |
| k2 | `templates/.../checkers/diagnostics/RpcKtDiagnosticsContainer.kt` | Diagnostic renderer registration |
| k2 | `templates/.../checkers/diagnostics/RpcKtDiagnosticFactoryToRendererMap.kt` | Diagnostic map construction |
| cli | `templates/.../CompilerPluginRegistrar.kt` | Plugin registration (`pluginId` property) |

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
