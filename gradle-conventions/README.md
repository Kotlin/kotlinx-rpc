# Overview

`gradle-conventions` is the included build with the build logic. Its precompiled convention plugins configure how
the subprojects are built, tested, published, and documented.

`gradle-conventions-settings/` has the settings plugins: repositories, version resolution, Develocity.
The root `settings.gradle.kts` includes both builds.

| Code | Purpose |
|---|---|
| `OptionalProperty` (`util/other/optionalProperty.kt`) | Property delegate for the `kotlinx.rpc.*` switches in `gradle.properties` |
| `KmpConfig` (`util/targets/kmpConfig.kt`) | Selects the KMP targets from the Kotlin version, the host OS, and the `kotlinx.rpc.exclude.<target>=true` properties of the module |
| `includePublic()` (in `gradle-conventions-settings`) | Makes a module public: publication, ABI validation, documentation. Check with `project.isPublicModule`. |

Published artifacts get the `kotlinx-rpc-` prefix. The protoc-gen artifacts have their own naming.

## Rules

- Take each dependency from `versions-root/libs.versions.toml`.
- Put logic for more than one module here, not into the `build.gradle.kts` files.
- Put utility code into a subdirectory of `util/`. A plugin file stays thin.
- Don't delete the `empty.gradle.kts` plugin. It is a placeholder without effect.
