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
