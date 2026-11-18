# Test with Kotlin Master

Kotlin Master is the latest dev build of the Kotlin compiler. The test finds
incompatibilities before the release of a Kotlin version.

## 1. Download the artifacts

```bash
./scripts/download_kotlin_master.sh
```

The script needs a token: the `BUILD_SERVER_TOKEN` environment variable, or
`buildserver.token=<token>` in `~/.gradle/gradle.properties`.

The script gets the Maven artifacts of the latest successful
`Kotlin_KotlinPublic_Artifacts_aggregate` build from the JetBrains TeamCity. It puts them
into the `lib-kotlin/` directory in the project root, and prints the Kotlin version, for
example `2.4.0-dev-12345`. This version is `<VERSION>` in the next steps.

## 2. Build and test

Use the `running_gradle_builds` and `running_gradle_tests` skills:

- Tasks: `:compiler-plugin:assemble --continue`, then `:compiler-plugin:allTests --continue`.
- Arguments: `-Pkotlin.compiler=<VERSION> -Pkotlinx.rpc.kotlinMasterBuild=true`.

`kotlinx.rpc.kotlinMasterBuild=true` has two effects:

- Gradle resolves the artifacts in `lib-kotlin/`.
- The compiler plugin has only `<VERSION>`, not the supported Kotlin versions.

You can also set the property in `gradle.properties`: uncomment the
`kotlinx.rpc.kotlinMasterBuild=true` line.

The compile task of the master version is `compileKotlinK<version>`, for example
`compileKotlinK2520_dev_4223` for `2.5.20-dev-4223`.

The build also has the target of the Kotlin Gradle plugin version, for example `k2420`.
The DevKit adds this target because the build script registers `<VERSION>` after the
DevKit reads its Gradle properties.
`-Pkotlin.compiler.plugin.devkit.testAgainst=<VERSION>` removes the target. Don't use
it for a `dev` version, because the DevKit then registers `<VERSION>` as an IDE version.

## 3. Fix

Fix the failures in the same way as for a released version
(`compat-fix-patterns.md`):

- The new API goes into the `post` source set of a new split. The previous API goes
  into the `pre` source set.
- When the base version of the master build already has `-dev-` or IDE targets with the
  old API, use `split("<VERSION>")`.

## Limits

- The Kotlin compiler of the project compiles the plugin against the master compiler. It
  can read the metadata of a compiler that is at most one minor version newer. When the
  master version is newer than that, also build the whole project with
  `-Pkotlin.lang=<VERSION>`.
- A Kotlin Master API can change again before the release. A fix for Kotlin Master is
  provisional until then.
- After the test, delete the `lib-kotlin/` directory. If you changed `gradle.properties`,
  comment out `kotlinx.rpc.kotlinMasterBuild` again.
