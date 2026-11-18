# Testing with Kotlin Master

Kotlin Master is the latest dev/nightly build from the Kotlin team. It is used to catch
incompatibilities before a Kotlin version is officially released.

## Step 1: Download Kotlin Master Artifacts

Run the `scripts/download_kotlin_master.sh` script in the project root:

```bash
./scripts/download_kotlin_master.sh
```

This requires a `BUILD_SERVER_TOKEN` -- either as an environment variable or in
`~/.gradle/gradle.properties` as `buildserver.token=<token>`. The script:
1. Queries JetBrains TeamCity for the latest successful `Kotlin_KotlinPublic_Artifacts_aggregate` build
2. Downloads the Maven artifacts archive
3. Extracts them into a `lib-kotlin/` directory at the project root

The script prints the build ID and the Kotlin version number (e.g., `2.4.0-dev-12345`).
Note this version -- it is the value to pass as `-Pkotlin.compiler=<VERSION>`.

## Step 2: Enable Kotlin Master Mode

Set the Gradle property `kotlinx.rpc.kotlinMasterBuild=true`. This does two things:
- Adds the `lib-kotlin/` directory as a Maven repository so Gradle resolves the
  downloaded artifacts
- Replaces the supported Kotlin versions of the compiler plugin with one version: the
  value of `-Pkotlin.compiler`. The DevKit also keeps the target of the Kotlin Gradle
  plugin version, for example `k2420`.

You can set this property via:
- Command-line: `-Pkotlinx.rpc.kotlinMasterBuild=true`
- Or uncomment the line in `gradle.properties`:
  ```properties
  kotlinx.rpc.kotlinMasterBuild=true
  ```

## Step 3: Build

Use the `running_gradle_builds` skill to build the compiler plugin with the master
version:
- Tasks: `:compiler-plugin:assemble --continue`
- Extra args: `-Pkotlin.compiler=<MASTER_VERSION> -Pkotlinx.rpc.kotlinMasterBuild=true`

The compile task of the master version is `compileKotlinK<version>`, for example
`compileKotlinK2520_dev_4223` for `2.5.20-dev-4223`.

To run the tests on the master version, use the same arguments with
`:compiler-plugin:allTests`.

## Step 4: Fix and Re-verify

Follow the same Phase 2-4 workflow as for released versions. The new API goes into the
`post` source set of a new split, and the previous API goes into the `pre` source set.
Use `split("<MASTER_VERSION>")` when the base version of the master build already has
`-dev-` or IDE targets with the old API.

## Caveats

- The Kotlin Gradle plugin of the project compiles the plugin against the master compiler.
  The compiler reads the metadata of one newer minor version. When the master version is
  further ahead, build the whole project with the master version as `-Pkotlin.lang` too.
- Kotlin Master APIs may change again before release. Fixes for Master should be
  considered provisional until the Kotlin version is released.
- Remember to clean up: when done, you can remove the `lib-kotlin/` directory and
  re-comment `kotlinx.rpc.kotlinMasterBuild` in `gradle.properties`.
