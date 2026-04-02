# Overview

`gradle-conventions` is the build-logic module for kotlinx-rpc. 
It provides precompiled Gradle convention plugins that standardize how all subprojects are built, tested, published, and documented.
It also stores common configuration and utilities, making the actual build scripts more concise and clean.
It is an **included build** — not a standalone project.

Its companion, `gradle-conventions-settings/`, provides settings-phase plugins (repositories, version resolution, Develocity). 
Both are included from the root `settings.gradle.kts`.

## Marker-Based Code Rewriting

`settings.gradle.kts` rewrites source files at configuration time based on the Kotlin version. 
Files contain marker comments like `// marker-imports` / `// /marker-imports`, and the content between them is replaced. 
This handles API differences between Kotlin 2.3.x and 2.4.0+. Affected files:
- `util/apiValidation.kt` — ABI validation API changed between versions
- `conventions-kmp.gradle.kts` — klib validation block
- `conventions-jvm.gradle.kts` — enabled flag

**Do not remove marker comments** — they are structural, not documentation.

## Key Patterns

- **`OptionalProperty`** (`util/other/optionalProperty.kt`) — Property delegation for feature toggling via `gradle.properties`. Properties follow `kotlinx.rpc.*` naming.
- **`KmpConfig`** (`util/targets/kmpConfig.kt`) — Determines which KMP targets to enable based on Kotlin version, host OS, and per-module exclusions (`kotlinx.rpc.exclude.<target>=true`).
- **Public vs internal modules** — `includePublic()` (from `gradle-conventions-settings`) marks modules as public. Public modules get full publication, ABI validation, and documentation. Check via `project.isPublicModule`.
- **Artifact ID normalization** — Published artifacts auto-prefixed with `kotlinx-rpc-`. Protoc-gen artifacts use a different scheme.

## Conventions for Editing

- All dependencies must come from `versions-root/libs.versions.toml` — never inline version strings.
- When a convention applies to multiple modules, put the logic here rather than in individual `build.gradle.kts` files.
- New utility code goes under `util/` in the appropriate subdirectory. Plugins themselves should stay thin.
- The `empty.gradle.kts` plugin exists as a no-op placeholder — don't delete it.
