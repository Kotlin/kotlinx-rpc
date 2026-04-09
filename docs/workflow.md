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

Also, artifact checks: `./validatePublishedArtifacts.sh -s`. 
See the [section](#tasks-to-know-about) below. 

### How to work with the compiler plugin

There are four modules there:
- [compiler-plugin-common](../compiler-plugin/compiler-plugin-common) - common for `backend` and `k2`
- [compiler-plugin-backend](../compiler-plugin/compiler-plugin-backend) - IR backend code. 
Here goes all heavy lifting codegen
- [compiler-plugin-cli](../compiler-plugin/compiler-plugin-cli) - FIR frontend code. This is the fancy part.
- [compiler-plugin-k2](../compiler-plugin/compiler-plugin-k2) - Entrypoint for the plugin, combines `backend` and `k2`.

#### Compiler Specific Modules

We have a problem when developing a Kotlin compiler plugin:
Compiler API is not stable (not won't be for a while). So how can one develop a plugin that works stably?

We compile for multiple Kotlin versions at once!

We use an in-house ✨technology✨ for this: CSM or Compiler Specific Modules.
This is a templating engine, which resolution is based on the current kotlin compiler version
(`kotlin-compiler` in [libs.versions.toml](../versions-root/libs.versions.toml)).

There is always a core source set (like this: [kotlin](../compiler-plugin/compiler-plugin-k2/src/main/kotlin)) 
that compiles nicely on all Kotlin versions. It is the majority of the code.
And then we have a template set, which is processed before compilation for a specific version. 
([templates](../compiler-plugin/compiler-plugin-k2/src/main/templates)).

The code that swaps them is in [template.kt](../gradle-conventions/src/main/kotlin/util/csm/template.kt).

The rules are the following:
- We enclose code dependent on a version in `##csm` tags.
- Every such block must:
  - Start with `//##csm <block_name>`
  - End with `//##csm /<block_name>`
  - Specify inside zero of more of the code blocks:
    - `//##csm default` + `//##csm /default`
    - `//##csm specific=[<version_patten>]` + `//##csm /specific`

Example code:
```kotlin
fun functionAvailableInAllVersions() {
    
}
fun csmFunction() {
    //##csm my-block
    //##csm default
    return 1
    //##csm /default
    //##csm specific=[2.0.0...2.1.0, 2.1.0-ij-*]
    return 2
    //##csm /specific
    //##csm /my-block
}
```

Templates matches `specific` versions greedy, so with multiple `specific` blocks - first one matched will be chosen, 
or `default` if none, or nothing if there is no `default`.

`<version_pattern>` works like this:
- It's a comma-separated array of versions matchers
- Version matcher is either:
  - Range — consists of `from` and `to` version separated by `...`. 
  `From` must be an exact version without a suffix.
  `To` must be a version without a suffix, where `*` can be used for any part: `2.2.*`, `2.*` but not `2.2.2*`.
  - Prefix matcher — matches a version by prefix: `2.2.0-*`, `2.2.2*`, `2.3.0-dev-*`

This allows us to support some past and future Kotlin versions 
(including versions for IDE, which are not in the stable list nor generally public) simultaneously.

Rules for complier API breaking changes:
- If some function or property changed signature or was replaced by another - 
use `VersionSpecificApi` interface in core to declare functionality  
(see [FirVersionSpecificApi.kt](../compiler-plugin/compiler-plugin-k2/src/main/kotlin/kotlinx/rpc/codegen/FirVersionSpecificApi.kt)
and [VersionSpecificApi.kt](../compiler-plugin/compiler-plugin-backend/src/main/kotlin/kotlinx/rpc/codegen/VersionSpecificApi.kt)).
- If some class differs in signature, 
write a proxy in core and implementation in the template. 
FQ name must match exactly.
(see [FirRpcCheckersVS.kt](../compiler-plugin/compiler-plugin-k2/src/main/templates/kotlinx/rpc/codegen/checkers/FirRpcCheckersVS.kt)
as an example)

TeamCity is set up to work with multiple Kotlin versions for testing and releases.

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

Tests are located in the [tests/compiler-plugin-tests](../tests/compiler-plugin-tests) directory.

`Kotlin Compiler DevKit` IDE plugin is for this (though it might not work and not that necessary).

We use Kotlin's test framework, and I don't think there docs for it, so we navigate on pure luck and might.

Put new test data into the [testData](../tests/compiler-plugin-tests/src/testData) directory:
- Use `box` subdirectory for Box tests:
They are compiled by all staged and then the `box` function is executed. 
It must return `"OK"` if test passes and error message otherwise.
- Use `diagnostic` for diagnostic tests. These only run frontend and check if your custom diagnostics work.
- Only add `.kt` files. Others will be generated.
- After adding new `.kt` file, run `./gradlew generateTests`.
- In `.kt` files place code to compile and directives (like `// RUN_PIPELINE_TILL: BACKEND`)
- Other files will be generated and test run
  - If a file didn't exist or content changed — the test will fail and tell about content mismatch
  - To update use `./updateTestData.sh <test-class-name> <?optional-test-name>` script. 
For example, run `./updateTestData.sh DiagnosticTest` 
to update tests in [DiagnosticTestGenerated.java](../tests/compiler-plugin-tests/src/test-gen/kotlinx/rpc/codegen/test/runners/DiagnosticTestGenerated.java).
Or `./updateTestData.sh DiagnosticTest testCheckedAnnotation` to update specific test.
  - Check logs and git to distinguish between content update fails and compilation/other problems.
  - ALWAYS check new content `.fir.txt` and `.fir.ir.txt` files to see if the changes adhere to what you expected to see there.

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

To download it locally - use `./download_kotlin_master.sh` script.

### How to disable KMP target compilation for modules

Set `kotlinx.rpc.exclude.<target_name>=true` in `gradle.properties` of a module 
(or in a parent directory for a set of modules).

Example:
```properties
kotlinx.rpc.exclude.wasmWasi=true
kotlinx.rpc.exclude.watchosArm32=true
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

Use `./publishLocal.sh` script. All artifacts will be in the local directory of `<REPO_ROOT>/build/repo/` .

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
- `validatePublishedArtifacts` task and more importantly `./validatePublishedArtifacts.sh` script.
  
  They are used to validate published artifacts and ensure you didn't delete or published something accidentally.
  
  Available options: `--dump` - update files, `-s` - no Gradle output except for errors, `-v` - verbose output.

## Other

See [gradle.properties](../gradle.properties) for additional properties.
