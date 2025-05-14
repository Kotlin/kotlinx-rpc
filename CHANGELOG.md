# 0.7.0
> Published 13 May 2025

## Announcement
This release enforces ERROR as a default reporting level for APIs that are forbidden by the strict mode.
You can still change the level manually, but in `0.8.0` strict mode will be enforced irreversibly.

## What's Changed

### Breaking Changes 🔴
* Change strict mode to level ERROR by default by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/338

### Other Changes 🧹
* Update Kotlin to 2.1.21 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/337
* Version 0.7.0-SNAPSHOT by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/325
* Samples: version 0.6.2 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/326
* 2.2.0 compiler migration by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/335

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.6.2...0.7.0

# 0.6.2
> Published 17 April 2025

### Bug fixes 🐛
* Fix flows that emit Units by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/323

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.6.1...0.6.2

# 0.6.1
> Published 11 April 2025

### Bug fixes 🐛
* Fix bidirectional flows in non-suspend streams by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/316
* Fix KRPC-173 (#315) by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/317

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.6.0...0.6.1

# 0.6.0
> Published 4 April 2025

### Features 🎉
* 2.1.20 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/309
* Non suspend flow by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/299

### Documentation 📗
* Update gRPC Docs and Sample by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/284

### Infra 🚧
* Update monitior by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/282
* Fix build config for for-ide builds by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/287
* Update build for custom KC versions by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/293
* Fix kotlin master compilation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/306

### Other Changes 🧹
* Upgrade Gradle to 8.12.1 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/286
* Version 0.6.0-SNAPSHOT by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/283
* Update leftover sources from jvm-only to kmp by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/300
* KRPC-129 Move compatibility tests from Toolbox to Kotlin RPC repo by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/304
* Dependency bump by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/303
* Better compiler error message for checked annotations by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/302

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.5.1...0.6.0

# 0.5.1
> Published 12 February 2025

### Features 🎉
* 2.1.10 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/271

### Documentation 📗
* Fix typo in README.md by @SebastianAigner in https://github.com/Kotlin/kotlinx-rpc/pull/266
* Added IDE plugin docs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/273

### Infra 🚧
* Update for IDE configs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/272
* Added python deps automerge by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/276

### Other Changes 🧹
* Update README.md by @pambrose in https://github.com/Kotlin/kotlinx-rpc/pull/268
* Added monitor app for deps publication by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/265
* Fix kotlin master by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/274
* Fix println tests by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/277
* Update Python Monitor Dependencies by @renovate in https://github.com/Kotlin/kotlinx-rpc/pull/278

## New Contributors
* @SebastianAigner made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/266
* @pambrose made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/268

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.5.0...0.5.1

# 0.5.0
> Published 27 January 2025

### Features 🎉
* Update Service Descriptors Generation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/227
* Kotlin 2.1.0 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/244
* Added basic CheckedTypeAnnotation impl with compiler plugin check by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/240
* Strict mode by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/243

### Breaking Changes 🔴
* Api naming by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/236
* Update Service Descriptors Generation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/227
* Added basic CheckedTypeAnnotation impl with compiler plugin check by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/240

### Deprecations ⚠️
* Api naming by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/236
* Strict mode by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/243

### Infra 🚧
* Update the project structure to work with kotlin-master by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/234
* Fixed version formatting with ENV vars by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/235
* Fix Kotlin master compilation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/245
* Opt-out from annotations type safety analysis by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/246

### Other Changes 🧹
* Added test for non-serializable params by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/237
* Updated descriptor to use `RpcType` instead of `KType` directly by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/239

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.4.0...0.5.0

# 0.4.0
> Published 5 November 2024

### Features 🎉
* Experimental support for: KRPC-125 Manual stream scope management by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/214
* Introduce @Rpc annotation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/216
* Support Kotlin 2.0.21 and 2.0.20 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/217

### Breaking Changes 🔴
* Introduce @Rpc annotation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/216
* Remove support for Kotlin versions prior to 2.0 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/218

### Infra 🚧
* Added proxy repository settings by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/191
* Added Kotlin for ide configs to project by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/219

### Bug fixes 🐛
* Fix for KT-41082 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/225

### Other Changes 🧹
* Bump core deps by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/220

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.3.0...0.4.0

# 0.3.0
> Published 1 October 2024

### Features 🎉
* Wasm Support by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/190

### Breaking Changes 🔴
* Move kRPC declarations from core by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/186

### Bug fixes 🐛
* Fix kotlin/js code and samples by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/179
* Fix regression of nested declarations in RPC interfaces by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/181

### Documentation 📗
* Wording fixes by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/175

### Infra 🚧
* Infra enhancements by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/206
* Fixed Wasm Publication by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/208
* Update renovate configs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/185
* Configure JPMS checks by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/187
* Update some ide configs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/188
* Added Gradle Doctor to the build by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/189
* Added build cache and develocity plugin by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/192
* Support variable Kotlin and project versions by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/201
* Update renovate and some deps by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/198

### Other Changes 🧹
* Bump version to 0.3.0-SNAPSHOT by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/174
* Fix compiler tests after #172 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/180
* Remove unused and inactive annotation by @kez-lab in https://github.com/Kotlin/kotlinx-rpc/pull/182
* Use built-in JsClass getter by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/184
* Update actions/configure-pages action to v5 by @renovate in https://github.com/Kotlin/kotlinx-rpc/pull/200
* Update Samples dependencies by @renovate in https://github.com/Kotlin/kotlinx-rpc/pull/199
* Update Core dependencies (non-major) by @renovate in https://github.com/Kotlin/kotlinx-rpc/pull/194
* Update Core dependencies (non-major) by @renovate in https://github.com/Kotlin/kotlinx-rpc/pull/205

## New Contributors
* @kez-lab made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/182

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.2.4...0.3.0

# 0.2.4
> Published 20 August 2024

### Features
* KRPC-18 Add K2 and IR code generation plugins, preserve KSP for K1 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/105
* Added 1.9.25 and 2.0.10 Kotlin Versions by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/168

### Bug fixes
* KRPC-101 Check if the entire stream is not already closed. by @pikinier20 in https://github.com/Kotlin/kotlinx-rpc/pull/158
* KRPC-119 Exception Deserialization by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/170
* Fix compilation on Kotlin/Native by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/172

### Infra
* Add issue templates by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/167

## New Contributors
* @pikinier20 made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/158

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.2.2...0.2.4

# 0.2.2
> Published 5 August 2024

### Bug fixes
* Fix log error messages by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/139
* KRPC-97 Race condition in stream cancellation locks the transport by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/138
* Fix call cancellation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/141

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.2.1...0.2.2


# 0.2.1
> Published 1 July 2024

### Breaking changes
Release contains breaking changes, see the [migration guide](https://kotlin.github.io/kotlinx-rpc/0-2-0.html)

* Update the project structure to use a new versioning by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/92
* Consistent module structure by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/114
* KRPC-63 Reorganize modules into a logical structure by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/110

### Features
* KRPC-62 WebSocketSession KtorRPCClient by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/107

### Bug fixes
* Fix typo by @fatalistt in https://github.com/Kotlin/kotlinx-rpc/pull/76
* Fix README by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/79
* Remove todos from repo links by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/85
* Fix typo in README.md by @zhelenskiy in https://github.com/Kotlin/kotlinx-rpc/pull/87
* Fix KDoc reference by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/103
* Fix native targets compiler plugins (#93) by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/112

### Deprecations
* KRPC-59 streamScoped has internal package by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/104

### Documentation
* Updated links on the docs website by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/71
* Added search indexes publication to Algolia action by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/73
* Fix algolia artifact in GH Actions by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/80
* Docs: Add structure to existing content by @vnikolova in https://github.com/Kotlin/kotlinx-rpc/pull/86
* Added build and test docs on PR by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/97
* Fixed GH Actions for docs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/99
* Disable docs website autoupdate by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/108
* KRPC-60 Update docs to match the new versioning scheme by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/109

### Infra
* Update version to 0.1.1-SNAPSHOT by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/64
* Version updates by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/72
* Signing and Publication by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/74
* IDEA. Issue links by @turansky in https://github.com/Kotlin/kotlinx-rpc/pull/96
* IDEA. Vector icon by @turansky in https://github.com/Kotlin/kotlinx-rpc/pull/98
* KRPC-71 Type-safe project accessors by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/106

## New Contributors
* @jvmusin made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/69
* @fatalistt made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/76
* @zhelenskiy made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/87
* @turansky made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/96
* @vnikolova made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/86

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.1.0...0.2.1
