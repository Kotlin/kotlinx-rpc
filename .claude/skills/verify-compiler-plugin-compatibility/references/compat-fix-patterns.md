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

Each target is in one source set of each split. So an `expect` declaration has one
`actual` declaration in each of the two source sets of one split.

The `expect` declarations are in `commonMain/kotlin/kotlinx/rpc/codegen`:

| File | API |
|---|---|
| `IrLookupCompat.kt` | Lookup of classes and functions in the IR backend |
| `FirCompat.kt` | FIR frontend |
| `MessageCollectorCompat.kt` | `MessageCollector` configuration key |

## DevKit runtime library

Before you add an `expect` declaration, check the DevKit runtime library. It has:

- Functions for an API that differs, in the package
  `org.jetbrains.kotlin.compiler.plugin.devkit`. For example, `fakeElementCompat` and
  `pluginGeneratedSourceElementKind`.
- Backports of a new compiler API to the Kotlin versions that don't have it. A backport
  has the package and the name of the compiler declaration, so `commonMain` calls the new
  API on all versions. For example, the library declares `IrAnnotation` and
  `IrBuilder.irAnnotation` for the versions before 2.3.20. The backports are in the
  `pre<V>DevMain` source sets of
  [compiler-plugin-runtime-compat](https://github.com/Kotlin/compiler-plugin-dev-kit/tree/main/compiler-plugin-runtime-compat/src).

## Pattern: a new Kotlin version changes an API

1. Find the first Kotlin version with the change.
2. If `versionHierarchy` doesn't have a split for that version, add one.
3. Add an `internal expect fun <name>Compat(...)` to the `*Compat.kt` file in `commonMain`.
4. Add the `actual` function with the old API to `pre<V>DevMain`, in the file
   `<Name>Compat.pre<V>Dev.kt`.
5. Add the `actual` function with the new API to `post<V>DevMain`, in the file
   `<Name>Compat.post<V>Dev.kt`.
6. Replace the direct call in `commonMain` with the `expect` function.

Example for a function without the `session` parameter from Kotlin 2.4.0:

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

A declaration that moved to another package needs `expect`/`actual` too, because
`commonMain` can't import a name that some versions don't have.

## Pattern: the change is between two `-dev-` builds

`splitDev(2, 4, 20)` puts all `2.4.20-dev-*` and `2.4.20-ij*` builds into the `post`
source set. If the API changed in a later `-dev-` build, an IDE target fails to compile.
Replace the split with `split("2.4.20-dev-<N>")`, where `<N>` is the first build with the
change. The source sets are then `pre2420Dev<N>Main` and `post2420Dev<N>Main`.

## Pattern: a class signature differs

A base class can change the signature of a function that the plugin overrides.

### An interface with all signatures

Use this pattern when one class can declare the function with each signature.

1. Declare an interface in `commonMain` that has the function with each signature.
2. Make the subclass extend the base class, implement the interface, and override each
   function.

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

On each Kotlin version, one function overrides the base class and the other function
overrides only the interface. When the function of the base class is abstract, the
compiler reports a wrong signature.

### A subclass for each version

Use this pattern when one class can't declare all signatures, for example when only the
return type differs.

1. Put the logic in a class or an object in `commonMain`.
2. Declare the subclass in both source sets of the split. Each subclass calls the code in
   `commonMain`.

## Pattern: a call is ambiguous in a function with context parameters

Kotlin before 2.3.20 and the IDE compilers of the 253 branch have a second overload for
a function that takes a `FirSession` or a `DiagnosticContext`. The second overload has a
`SessionHolder` or a `DiagnosticContext` context parameter:

```
fun ConeKotlinType.toClassSymbol(session: FirSession): FirClassSymbol<*>?
context(sessionHolder: SessionHolder) fun ConeKotlinType.toClassSymbol(s: FirSession): FirClassSymbol<*>?
```

In a function with a `CheckerContext` context parameter, a call with the explicit
argument fails with `Overload resolution ambiguity`. Call the overload that takes the
value from the context:

| Ambiguous call | Correct call |
|---|---|
| `type.toClassSymbol(context.session)` | `type.toClassSymbol()` |
| `reporter.reportOn(source, factory, context = context)` | `reporter.reportOn(source, factory)` |

A function without context parameters must pass the session and the context explicitly.
