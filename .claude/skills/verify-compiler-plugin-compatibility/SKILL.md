---
name: verify-compiler-plugin-compatibility
description: >
  Verify that the kotlinx-rpc compiler plugin compiles successfully against multiple
  Kotlin compiler versions, and fix any incompatibilities found. Use this skill whenever
  the user wants to check compiler plugin compatibility across Kotlin versions, test a
  new Kotlin version, fix compiler plugin build failures after a Kotlin upgrade, or
  ensure the CSM templates produce valid code for all supported versions. Trigger on
  phrases like "verify compiler plugin", "check compatibility", "test Kotlin versions",
  "compiler plugin broken", "fix for Kotlin X.Y", or "support new Kotlin version".
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

## Workflow

### Phase 1: Verify Each Version

For each Kotlin version, build the compiler plugin:

```bash
cd compiler-plugin && ../gradlew clean compileKotlin -Pkotlin.compiler=<VERSION> 2>&1
```

The `-Pkotlin.compiler=<VERSION>` flag overrides the Kotlin compiler version at build
time. The CSM template processor reads this to select the right code blocks.

Record the result for each version: PASS or FAIL with the full error output.

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

For each failing version, categorize every compilation error:

**Category A -- Import change**: A class/function moved to a different package.
Symptom: `Unresolved reference` on an import.

**Category B -- Signature change**: A method's parameters or return type changed.
Symptom: `None of the following candidates is applicable`, `Too many arguments`.

**Category C -- API replacement**: An entire API was replaced by a new mechanism.
Symptom: `Unresolved reference` on a class or method that no longer exists.

**Category D -- Structural change**: Property became function, List became MutableList, etc.
Symptom: `Val cannot be reassigned`, `Type mismatch`.

To understand what the new API looks like, use the `searching_dependency_sources` skill
to search the Kotlin compiler sources for the class or method name. This reveals the new
signatures, package locations, and parameter lists.

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

After applying fixes, re-run the build for every version that failed:

```bash
cd compiler-plugin && ../gradlew clean compileKotlin -Pkotlin.compiler=<VERSION> 2>&1
```

If new errors appear, go back to Phase 2. Repeat until all versions pass.

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

- Inspect generated CSM output in `<module>/build/generated-sources/csm/` after building.
- CSM logs matched blocks: look for `[CSM] Matched specific` or `[CSM] Matched default`.
- If an import section error occurs, check that the `specific` block has ALL needed imports, not just the changed ones.
- When unsure about new API shape, use `searching_dependency_sources` to search the Kotlin compiler jar for the symbol name.
