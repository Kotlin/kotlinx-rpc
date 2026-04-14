# CSM Fix Patterns

How to fix compiler plugin incompatibilities using the CSM template system.

## The CSM Template System

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

## Fix Pattern: New Kotlin Version Breaks Existing Code

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

## Fix Pattern: Import Changes

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

## Fix Pattern: New VersionSpecificApi Method

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

## Fix Pattern: FIR Checker Signature Changes

Kotlin periodically changes how FIR checkers receive `CheckerContext` and
`DiagnosticReporter` (explicit params vs context receivers). The VS wrapper classes in
`compiler-plugin-k2/src/main/templates/kotlinx/rpc/codegen/checkers/FirRpcCheckersVS.kt`
adapt between versions. All wrappers follow the same pattern -- fix them all consistently.

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