# Development workflow

> [!NOTE]
> This document is for **human developers only**.
> AI agents and LLMs should use `CLAUDE.md` and skills for project guidelines instead.

For environment setup instructions, see [environment](../docs/environment.md).
For troubleshooting, see [troubleshooting](../docs/troubleshooting.md).

## How to

### How to commit

Make changes granularly, each commit should make sense on its onw.

Add YouTrack issue code or GitHub issue number when possible.

Before pushing, run (maybe separately):

```Bash
./gradlew checkLegacyAbi test allTests detekt :jpms-check:compileJava
```

Beware, that `detekt` doesn't fail the build, but outputs messages into the console.
Makes sense to run it separately. You can also see generated reports

Also, artifact checks: `./scripts/validate_published_artifacts.sh -s`. 
See the [section](#tasks-to-know-about) below. 

### How to work with the compiler plugin

The plugin is one module: [compiler-plugin](../compiler-plugin). Its packages:
- `kotlinx.rpc.codegen` - the entry points and the FIR frontend code. This is the fancy part.
- `kotlinx.rpc.codegen.checkers` - FIR checkers and diagnostics.
- `kotlinx.rpc.codegen.extension` - IR backend code. Here goes all heavy lifting codegen.
- `kotlinx.rpc.codegen.common` - names that the frontend and the backend share.

#### Kotlin versions

The compiler API is not stable, so one build of the plugin can't work with all Kotlin versions.
The [Kotlin Compiler Plugin DevKit](https://github.com/Kotlin/compiler-plugin-dev-kit) solves this:
- It compiles the same sources against each supported Kotlin version. Each version is a Gradle target,
for example `k2420` for Kotlin 2.4.20.
- It puts the classes of all versions into one jar. The jar selects the classes for the compiler that loads it.
- It builds for the compiler versions of IntelliJ IDEA and Android Studio too.

[devkit.kt](../gradle-conventions/src/main/kotlin/util/devkit.kt) declares the supported Kotlin and IDE versions.

The [commonMain](../compiler-plugin/src/commonMain/kotlin) source set has the majority of the code.
It compiles on all Kotlin versions.

Rules for compiler API breaking changes:
- Declare the functionality as an `expect` function in `commonMain`
(see [FirCompat.kt](../compiler-plugin/src/commonMain/kotlin/kotlinx/rpc/codegen/FirCompat.kt)
and [IrCompat.kt](../compiler-plugin/src/commonMain/kotlin/kotlinx/rpc/codegen/IrCompat.kt)).
- Write one `actual` function for the versions before the change and one for the versions from the change.
They go to the `pre<version>DevMain` and `post<version>DevMain` source sets,
for example [pre24DevMain](../compiler-plugin/src/pre24DevMain/kotlin) and
[post24DevMain](../compiler-plugin/src/post24DevMain/kotlin) for a change in Kotlin 2.4.0.
- If no source set exists for the version of the change, add the version to `versionHierarchy`
in [build.gradle.kts](../compiler-plugin/build.gradle.kts).
- `splitDev(2, 4)` puts all builds of 2.4.0, including the `-dev-` and IDE builds, into the `post` group.
If the change is in the middle of the `-dev-` builds, use `split("2.4.0-dev-1234")` with the first build that has the change.
- The DevKit has functions for some changes, for example `fakeElementCompat`. Check its
[DevKitCompat.kt](https://github.com/Kotlin/compiler-plugin-dev-kit/blob/main/compiler-plugin-runtime/src/commonMain/kotlin/org/jetbrains/kotlin/compiler/plugin/devkit/DevKitCompat.kt) first.

#### Frontend FIR

Contains diagnostics (and their checkers), frontend codegen (declarations).

Resources:
- https://github.com/JetBrains/kotlin/blob/master/docs/fir/fir-basics.md
- https://github.com/JetBrains/kotlin/blob/master/docs/fir/fir-plugins.md
- [official plugins](https://github.com/JetBrains/kotlin/tree/master/plugins)
- https://www.youtube.com/watch?v=Pl-89n9wDqo
- https://www.youtube.com/watch?v=Si0r2_N0J88
- https://kotlinconf.com/talks/781372/

#### Backend IR

Contains most of the codegen - implementations for declarations created on the frontend.

No docs here. Two main approaches for the development:
- Don't know how to do something? 
Check previous code and [official plugins](https://github.com/JetBrains/kotlin/tree/master/plugins).
- Write the code you want to generate, dump IR and write IR DSL using the dump.

#### Compiler Tests

Tests are in the compiler plugin module: [compiler-plugin/src](../compiler-plugin/src).
Run them from the repository root, because the test data compiles against the projects of the root build:
- `./gradlew :compiler-plugin:allTests` - all Kotlin versions.
- `./gradlew :compiler-plugin:k2420Test` - one Kotlin version.

`Kotlin Compiler DevKit` IDE plugin is for this (though it might not work and not that necessary).

We use Kotlin's test framework, and I don't think there docs for it, so we navigate on pure luck and might.

Put new test data into the [data](../compiler-plugin/src/commonTest/data) directory:
- Use `box` subdirectory for Box tests:
They are compiled by all staged and then the `box` function is executed. 
It must return `"OK"` if test passes and error message otherwise.
- Use `diagnostic` for diagnostic tests. These only run frontend and check if your custom diagnostics work.
- Only add `.kt` files. Others will be generated.
- The test tasks generate the test classes for new `.kt` files. They are in `compiler-plugin/build/test-gen`.
- In `.kt` files place code to compile and directives (like `// RUN_PIPELINE_TILL: BACKEND`)
- Other files will be generated and test run
  - If a file didn't exist or content changed — the test will fail and tell about content mismatch
  - To update use `./scripts/update_test_data.sh <test-class-name> <?optional-test-name>` script. 
For example, run `./scripts/update_test_data.sh DiagnosticTest` to update all diagnostic tests.
Or `./scripts/update_test_data.sh DiagnosticTest testCheckedAnnotation` to update specific test.
  - Check logs and git to distinguish between content update fails and compilation/other problems.
  - ALWAYS check new content `.fir.txt` and `.ir.txt` files to see if the changes adhere to what you expected to see there.
- The output can differ between Kotlin versions:
  - A file without a version, for example `simple.ir.txt`, is the output of the highest supported Kotlin version.
  - A file with a version, for example `simple.2321.ir.txt`, is the output of that version and of the lower versions,
down to the next file with a version.

#### Debug

Use `kotlin.compiler.execution.strategy=in-process` property in [gradle.properties](../gradle.properties)
and run the task under debug in IDEA. Alternatively - run compiler tests under debug.

### How to work with the new API

All modules that are published require you to specify the return types and visibility explicitly.
Make sure that all public types have KDocs. 

If some declaration should be shared between modules  
but shouldn't be used publicly — mark it with `@InternalRpcApi`.

### How to work with dependencies

Add new dependencies into [libs.versions.toml](../versions-root/libs.versions.toml).

If the project structure behaves funny, or you encounter version mismatch problems
— you can use `./gradlew htmlDependencyReport` for better visual introspection.

### How to work with a filesystem

Always use OS-agnostic paths: `Path.of("a", "b")` and not `Path.of("a/b")`, for example.

### How to work with Kotlin Master

Check TeamCity build `Java, JS, WASM and Linux, Master` and see the steps.

Remember to set `kotlinx.rpc.kotlinMasterBuild=true` in [gradle.properties](../gradle.properties).

To download it locally - use `./scripts/download_kotlin_master.sh` script.

### How to disable KMP target compilation for modules

Set `kotlinx.rpc.exclude.<target_name>=true` in `gradle.properties` of a module 
(or in a parent directory for a set of modules).

Example:
```properties
kotlinx.rpc.exclude.wasmWasi=true
kotlinx.rpc.exclude.watchosDeviceArm64=true
```

### How to add a new module

In [settings.gradle.kts](../settings.gradle.kts) include module:
- as `include("<module-name>")` if it is a meta-module (just a directory for a set of modules)
- as `include("<module-name>")` if it is a module not for publication (like for tests)
- as `includePublic("<module-name>")` if it is a module for publication

`<module-name>` shouldn't contain parent names.

**Naming rules:**
- All public modules are prefixed with `kotlinx-rpc-` during publication. 
Take this into consideration when making a new name.
- If there is a family of modules (`krpc-client`, `krpc-server`, etc...), 
they should have common prefix and the shared module should be named `core` 
(in `krpc` example the prefix is `krpc-` and the shared module is `krpc-core`).

### How to test

Most tests should run just fine from the IDE and from the console.

However, there is [this one](../krpc/krpc-test/src/commonMain/kotlin/kotlinx/rpc/krpc/test/KrpcTransportTestBase.kt)
that fails from IDE. 
Use commands like `./gradlew krpc:krpc-test:jvmTest --tests "Json*bidirectionalAsyncStream"` to run them.

Non-trivial tests are located in `tests` module.

### How to test a local library version with an external project

Use `./scripts/publish_local.sh` script. All artifacts will be in the local directory of `<REPO_ROOT>/build/repo/` .

## How to debug tests/protobuf-conformance

Prerequisite (Only macOS for now): 
```bash
./setup_protoscope.sh
```
It will install the `protoscope` utility: https://github.com/protocolbuffers/protoscope

Now you can run tests:
```bash
gradle :tests:protobuf-conformance:runConformanceTest -Pconformance.test='<test_name>'
```

In the [manual](../tests/protobuf-conformance/build/protobuf-conformance/manual) directory 
you will find files: 
- dump_conformance_input.bin.txt - decoded ConformanceRequest protobuf message
- dump_conformance_output.bin.txt - decoded ConformanceResponse protobuf message
- dump_payload_input.bin.txt - decoded ConformanceRequest.payload (if protobuf)
- dump_payload_output.bin.txt - decoded ConformanceResponse.result (if protobuf)

IMPORTANT: `protoscope` only works with proto3 and not 'editions' messages. 
For proto2 and editions this won't work.

To debug tests, 
use [ConformanceClient.kt](../tests/protobuf-conformance/src/commonMain/kotlin/kotlinx/rpc/protoc/gen/test/ConformanceClient.kt)
and this command:
```bash
gradle :tests:protobuf-conformance:runConformanceTest -Pconformance.test.debug='true' -Pconformance.test='<test_name>'
```

Then use IntelliJ 'Attach to Process' feature to attach to the process on port 5005.
(kill the process if port is already in use: `kill $(lsof -t -i:5005)`)

### How to add a new public package repository

We have repositories like `https://redirector.kotlinlang.org/maven/kxrpc-for-ide/` that users can comsume from.

Follow KT-A-723 Repository Alias (redirector) guide to add a new repository.

## Tasks to know about

- `updateProperties` - keep top-level gradle properties in sync.
When you change something in [gradle.properties](../gradle.properties), 
all included builds (not subprojects) must reflect the change.
- `dokkaGenerate` - generate Dokka documentation. Files can be found in [api](pages/api).
- `checkLegacyAbi` / `updateLegacyAbi` - ABI checks. 
See https://kotlinlang.org/docs/whatsnew22.html#binary-compatibility-validation-included-in-kotlin-gradle-plugin.
Former BCV: https://github.com/Kotlin/binary-compatibility-validator
- `aggregateApiWatchdogReports` - the API watchdog report. The watchdog runs during compilation
of public modules and fails the build when exposed API violates the rules. Use this task
to see the collected diagnostics: `build/reports/api-watchdog/aggregate-report.html` (SARIF
alongside).
- `kotlinUpgradeYarnLock` / `kotlinWasmUpgradeYarnLock` - update [kotlin-js-store](../kotlin-js-store) contents, 
usually after Kotlin version update.
- `updateDocsChangelog` - put modified [CONTRIBUTING.md](../CONTRIBUTING.md) into [topics](pages/kotlinx-rpc/topics)
- `detekt` - run detekt checks.
- `dumpPlatformTable` - Update [platforms.topic](pages/kotlinx-rpc/topics/platforms.topic)
- `generateTests` - see [complier plugin tests](#compiler-tests)
- `clean` / `cleanTest` / `cleanAllTests` - clean tasks.
  - `clean` - everything
  - `cleanTest` - JVM test results
  - `cleanAllTests` - KMP test results
- `validatePublishedArtifacts` task and more importantly `./scripts/validate_published_artifacts.sh` script.
  
  They are used to validate published artifacts and ensure you didn't delete or published something accidentally.
  
  Available options: `--dump` - update files, `-s` - no Gradle output except for errors, `-v` - verbose output.

## Other

See [gradle.properties](../gradle.properties) for additional properties.
