# Version-specific code: layout and fix patterns

## Layout

| Source set | Kotlin versions | Content |
|---|---|---|
| `compiler-plugin/src/commonMain` | All | The plugin code, and `expect` declarations for the API that differs |
| `compiler-plugin/src/pre<V>DevMain` | Before `<V>` | `actual` declarations |
| `compiler-plugin/src/post<V>DevMain` | From `<V>` | `actual` declarations |

`<V>` is the Kotlin version without dots: `24` for 2.4.0, `2420` for 2.4.20.

`versionHierarchy` in `compiler-plugin/build.gradle.kts` declares the splits:

```kotlin
versionHierarchy {
    splitDev(2, 3)      // pre23DevMain, post23DevMain
    splitDev(2, 4)      // pre24DevMain, post24DevMain
    splitDev(2, 4, 20)  // pre2420DevMain, post2420DevMain
    splitDev(2, 5)      // pre25DevMain, post25DevMain
}
```

A target is in one group of each split. So one `expect` declaration has its two `actual`
declarations in the two source sets of one split.

The `expect` declarations:

| File in `commonMain/kotlin/kotlinx/rpc/codegen` | API |
|---|---|
| `IrCompat.kt` | IR backend |
| `FirCompat.kt` | FIR frontend |
| `MessageCollectorCompat.kt` | `MessageCollector` configuration key |

The FIR code calls the `expect` functions directly.

`VersionSpecificApiImpl.kt` implements the `VersionSpecificApi` interface for all versions. The IR code calls
the interface, and the implementation calls the `expect` functions where the API differs.

The DevKit runtime library has `expect`/`actual` functions for some changes, for example
`fakeElementCompat` and `pluginGeneratedSourceElementKind`
(package `org.jetbrains.kotlin.compiler.plugin.devkit`). Use them when they fit.

## Pattern: a new Kotlin version changes an API

1. Find the first Kotlin version with the change.
2. If `versionHierarchy` has no split for that version, add one.
3. Add an `internal expect fun <name>Compat(...)` to the `*Compat.kt` file in `commonMain`.
4. Add the `actual` function with the old API to `pre<V>DevMain`, in the file
   `<Name>Compat.pre<V>Dev.kt`.
5. Add the `actual` function with the new API to `post<V>DevMain`, in the file
   `<Name>Compat.post<V>Dev.kt`.
6. Replace the direct call in `commonMain` with the `expect` function.

Example for a function that lost its `session` parameter in Kotlin 2.4.0:

```kotlin
// commonMain/.../FirCompat.kt
internal expect fun FirAnnotation.getStringArgumentCompat(name: Name, session: FirSession): String?

// pre24DevMain/.../FirCompat.pre24Dev.kt
internal actual fun FirAnnotation.getStringArgumentCompat(name: Name, session: FirSession): String? {
    return getStringArgument(name, session)
}

// post24DevMain/.../FirCompat.post24Dev.kt
internal actual fun FirAnnotation.getStringArgumentCompat(name: Name, session: FirSession): String? {
    return getStringArgument(name)
}
```

## Pattern: only an import changes

A declaration that moved to another package needs `expect`/`actual` too. An import of a
name that doesn't exist is an error, so `commonMain` can't import it.

## Pattern: the change is between two `-dev-` builds

`splitDev(2, 4, 20)` puts all `2.4.20-dev-*` and `2.4.20-ij*` builds into the `post`
group. If the API changed in the middle of the `-dev-` builds, an IDE target fails to
compile. Replace the split with `split("2.4.20-dev-<N>")`, where `<N>` is the first
build with the change. The source sets are then `pre2420Dev<N>Main` and
`post2420Dev<N>Main`.

## Pattern: a class signature differs

A base class can change the signature of a function that the plugin overrides.

1. Keep the logic in an object in `commonMain`.
2. Declare the subclass in both source sets of the split. Each subclass calls the object.

`checkers/FirRpcCheckersVS.kt` has such subclasses for the checkers. The checker API is
the same in all supported versions, so the file is in `commonMain`.
