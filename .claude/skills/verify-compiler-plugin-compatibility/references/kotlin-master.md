# Testing with Kotlin Master

Kotlin Master is the latest dev/nightly build from the Kotlin team. It is used to catch
incompatibilities before a Kotlin version is officially released.

## Step 1: Download Kotlin Master Artifacts

Run the `dowload_kotlin_master.sh` script in the project root:

```bash
./dowload_kotlin_master.sh
```

This requires a `BUILD_SERVER_TOKEN` -- either as an environment variable or in
`~/.gradle/gradle.properties` as `buildserver.token=<token>`. The script:
1. Queries JetBrains TeamCity for the latest successful `Kotlin_KotlinPublic_Artifacts` build
2. Downloads the Maven artifacts archive
3. Extracts them into a `lib-kotlin/` directory at the project root

The script prints the build ID and the Kotlin version number (e.g., `2.4.0-dev-12345`).
Note this version -- it is the value to pass as `-Pkotlin.compiler=<VERSION>`.

## Step 2: Enable Kotlin Master Mode

Set the Gradle property `kotlinx.rpc.kotlinMasterBuild=true`. This does two things:
- Adds the `lib-kotlin/` directory as a Maven repository so Gradle resolves the
  downloaded artifacts
- **Skips** `:tests:compiler-plugin-tests` (box/diagnostic tests), which typically do not
  compile against unreleased Kotlin versions

You can set this property via:
- Command-line: `-Pkotlinx.rpc.kotlinMasterBuild=true`
- Or uncomment the line in `gradle.properties`:
  ```properties
  kotlinx.rpc.kotlinMasterBuild=true
  ```

## Step 3: Build

Use the `running_gradle_builds` skill to build the compiler plugin with the master
version:
- Tasks: `clean compileKotlin`
- Extra args: `-Pkotlin.compiler=<MASTER_VERSION> -Pkotlinx.rpc.kotlinMasterBuild=true`

## Step 4: Fix and Re-verify

Follow the same Phase 2-4 workflow as for released versions. When adding CSM blocks for
Kotlin Master, the new code goes in `default` (since Master represents the future), and
the previous release's code moves into a `specific` block with a bounded range.

## Caveats

- Master versions have dev suffixes like `2.4.0-dev-12345`. Exact prefix matches work
  fine (e.g., `2.4.0-dev-*`), but version ranges strip the suffix before comparison, so
  a range like `2.4.0...2.4.*` will match `2.4.0-dev-12345` as `2.4.0`. Non-stable
  suffixes are not allowed inside range bounds -- use prefix patterns instead for dev
  versions.
- Kotlin Master APIs may change again before release. Fixes for Master should be
  considered provisional until the Kotlin version is released.
- Remember to clean up: when done, you can remove the `lib-kotlin/` directory and
  re-comment `kotlinx.rpc.kotlinMasterBuild` in `gradle.properties`.