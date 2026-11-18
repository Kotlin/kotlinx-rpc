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
    splitDev(2, 3, 20)  // pre2320DevMain, post2320DevMain
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
| `IrLookupCompat.kt` | Lookup of classes and functions in the IR backend |
| `FirCompat.kt` | FIR frontend |
| `MessageCollectorCompat.kt` | `MessageCollector` configuration key |

The FIR code and the IR code call the `expect` functions directly.

The DevKit runtime library covers some changes. Check it before you add an `expect` declaration:

- Functions for an API that differs, for example `fakeElementCompat` and `pluginGeneratedSourceElementKind`
  (package `org.jetbrains.kotlin.compiler.plugin.devkit`).
- Backports of a new compiler API to the Kotlin versions that don't have it. A backport has the package and
  the name of the compiler declaration, so `commonMain` calls the new API on all versions.
  For example, `IrAnnotation` and `IrBuilder.irAnnotation` exist from Kotlin 2.3.20, and the library declares
  them for the versions before 2.3.20. The `pre<V>DevMain` source sets of
  [compiler-plugin-runtime-compat](https://github.com/Kotlin/compiler-plugin-dev-kit/tree/main/compiler-plugin-runtime-compat/src)
  have the backports.

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
There are two ways to handle it.

### An interface with all signatures

Use it when one class can declare the function with each signature.

1. Declare an interface in `commonMain` that has the function with each signature.
2. The subclass extends the base class, implements the interface, and overrides each function.

```kotlin
// Before 2.4.0: abstract class Foo { abstract fun foo(i: Int) }
// From 2.4.0:   abstract class Foo { abstract fun foo(s: String) }

interface FooCompat {
    fun foo(i: Int)
    fun foo(s: String)
}

class FooImpl : Foo(), FooCompat {
    override fun foo(i: Int) { /* ... */ }
    override fun foo(s: String) { /* ... */ }
}
```

On each Kotlin version, one function overrides the base class and the other overrides only the interface.
When the function of the base class is abstract, the compiler reports a wrong signature.

### A subclass for each version

Use it when one class can't declare all signatures, for example when only the return type differs.

1. Keep the logic in a class or an object in `commonMain`.
2. Declare the subclass in both source sets of the split. Each subclass calls the shared code.

## Pattern: a call is ambiguous in a function with context parameters

Before Kotlin 2.3.20, and in the IDE compilers of the 253 branch, the compiler has additional overloads that make a call with an explicit
`FirSession` or `DiagnosticContext` ambiguous when the context has a `SessionHolder` or a `DiagnosticContext`.
A `CheckerContext` is both. The error is `Overload resolution ambiguity`, for example between these candidates:

```
fun ConeKotlinType.toClassSymbol(session: FirSession): FirClassSymbol<*>?
context(sessionHolder: SessionHolder) fun ConeKotlinType.toClassSymbol(s: FirSession): FirClassSymbol<*>?
```

The ambiguity is intentional. Call the overload that takes the value from the context:

| Ambiguous call                                         | Correct call                         |
|--------------------------------------------------------|--------------------------------------|
| `type.toClassSymbol(context.session)`                  | `type.toClassSymbol()`               |
| `reporter.reportOn(source, factory, context = context)` | `reporter.reportOn(source, factory)` |

The checkers in `checkers/` use these calls. A function without context parameters still passes the
session and the context explicitly.
