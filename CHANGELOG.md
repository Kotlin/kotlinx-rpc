# 0.10.1
> Published 27 October 2025

### Features 🎉
* Kotlin 2.2.21 Support by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/519 
* Expose RpcServiceDescriptor.callables by @rnett in https://github.com/Kotlin/kotlinx-rpc/pull/516
* Added watchosDeviceArm64 and watchosArm32 targets to kRPC by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/513

### Bug fixes 🐛
* Fix one more ClosedSendChannelException by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/514
* Fix scope initialization on the kRPC Client by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/505
* Fix wording for the `perCallBufferSize` docs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/500

### Infra 🚧
* Added readme safeguard by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/515
* Fixed kRPC compatibility tests by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/518
* Update compiler tests infra by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/510

## New Contributors
* @rnett made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/516

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.10.0...0.10.1

# 0.10.0
> Published 7 October 2025

## Overview
This release brings a lot of changes, work:
- Kotlin 2.2.20 and 2.2.10 support
- kRPC: Backpressure

To read about the backpressure feature, 
see the updated [kRPC Configuration](https://kotlin.github.io/kotlinx-rpc/configuration.html#connector-dsl) page.

### Breaking Changes 🔴
* Allow suspend calls inside ktor rpc builder #433 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/439

### Features 🎉
* Kotlin 2.2.20 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/478
* Kotlin 2.2.10 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/456
* kRPC: Backpressure by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/462
* Add support for Wasm/Wasi to krpc #465 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/480

### Bug fixes 🐛
* Add collect once check for client streams by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/431
* Fix diagnostic rendering for compiler plugins checkers by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/432
* fix wrong unchecked null cast (potential NPE) by @y9maly in https://github.com/Kotlin/kotlinx-rpc/pull/445

### Documentation 📗
* Docs for gRPC with Ktor by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/394
* Add a doc for KMP source sets with gRPC by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/405
* Update strict-mode.topic by @BierDav in https://github.com/Kotlin/kotlinx-rpc/pull/440
* Update grpc-configuration.topic by @flockbastian in https://github.com/Kotlin/kotlinx-rpc/pull/450
* Added docs for release by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/482
* Fix typo in docs grpc-configuration.topic by @sebaslogen in https://github.com/Kotlin/kotlinx-rpc/pull/495

### Infra 🚧
* Fix docs yaml and signing tasks by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/404
* Fix jdk resolution problems on CI by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/406
* Use compat-patrouille for compatibility settings by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/438

### Other Changes 🧹
* Fix how we create 'publishMavenArtifact' tasks by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/416
* Update grpc-sample app by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/425
* Fix LV and signing by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/424
* Update ktor-all-platforms-app sample to sync service creation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/455
* Added Ktor closure tests and Cancellation tests, + minor fixes  by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/479
* Fix flaky tests by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/481

## New Contributors
* @flockbastian made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/450
* @y9maly made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/445
* @sebaslogen made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/495

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.9.1...0.10.0

# 0.9.1
> Published 17 July 2025

### Bug fixes 🐛
* Support nullable contextual serializers by @yakivy in https://github.com/Kotlin/kotlinx-rpc/pull/392
* Make WS plugin installation for Ktor server more flexible by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/398
* Make KtorRpcClient inherit KrpcClient by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/396

### Documentation 📗
* Update gRPC doc by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/391
* Add stub targets tags for platforms table by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/397

### Infra 🚧
* Remove the monitor application by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/388

### Other Changes 🧹
* Update version for 0.9.0-SNAPSHOT by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/387

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.8.1...0.9.1

# 0.8.1
> Published 9 July 2025

### Bug fixes 🐛
* Propagate transport coroutine context by @yakivy in https://github.com/Kotlin/kotlinx-rpc/pull/374
* Fix compiler bugs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/382
* kRPC client initialization is not single shot by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/385

### Documentation 📗
* Grpc sample update by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/371
* Fix dokka API button for all modules by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/381

### Infra 🚧
* Update pages publication and api link by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/375
* Added artifacts validation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/377

### Other Changes 🧹
* Ci updates by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/370
* Updated CSM sources for template generation by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/372
* Update strict-mode.topic by @BierDav in https://github.com/Kotlin/kotlinx-rpc/pull/380
* Update Ktor to 3.2.1 and samples by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/383

## New Contributors
* @yakivy made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/374
* @BierDav made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/380

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.8.0...0.8.1

# 0.8.0
> Published 30 June 2025

## Overview
This release brings a lot of changes, including breaking changes:
- Kotlin 2.2.0 Update
- Decoupling of `kotlinx.serialization` from the core functionality
- Simplifying lifetime schema (services lost their `CoroutineScope`)
- Irreversible enforcement of the [strict mode](https://kotlin.github.io/kotlinx-rpc/strict-mode.html)

These changes significantly reduce the number of footguns and improve the overall usability of the library.

Additionally, the internal structure of kRPC protocol and our compiler plugin reduced its complexity. 
That allows us to provide better quality in future releases
(and this also applies to gRPC, even though in this particular release it was not a priority).

For the full list of changes that require migration,
see the [Migration Guide](https://kotlin.github.io/kotlinx-rpc/0-8-0.html).

## Docs update

We added three new documentation sections:
- [API Reference](https://kotlin.github.io/kotlinx-rpc/api/index.html) 
- [Platform compatibility overview](https://kotlin.github.io/kotlinx-rpc/platforms.html) 
- [Changelog](https://kotlin.github.io/kotlinx-rpc/changelog.html) in the web

### Features 🎉
* Serialization decoupling by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/354
* Update Kotlin to 2.2.0 by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/359

### Breaking Changes 🔴
* [Meta] Strict mode, deprecations, lifetime by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/353

### Bug fixes 🐛
* Fix compilation for standalone k2 module by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/350
* Fix Pupperteer Version by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/355

### Documentation 📗
* Platforms Table and docs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/356
* Dokka  by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/357
* Add Changelog.md to Docs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/358
* Onboarding by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/363

### Other Changes 🧹
* Advance version to 0.8.0-SNAPSHOT by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/340
* Update README.md to use non-suspend flows by @brokenhappy in https://github.com/Kotlin/kotlinx-rpc/pull/342
* Fix kotlin master builds by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/345
* Simplify Gradle Configs by @Mr3zee in https://github.com/Kotlin/kotlinx-rpc/pull/360

## New Contributors
* @brokenhappy made their first contribution in https://github.com/Kotlin/kotlinx-rpc/pull/342

**Full Changelog**: https://github.com/Kotlin/kotlinx-rpc/compare/0.7.0...0.8.0

# 0.7.0
> Published 13 May 2025

## Announcement
This release enforces ERROR as a default reporting level for APIs that are forbidden by the strict mode.
You can still change the level manually, but in `0.8.0` strict mode will be enforced irreversibly.

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
