# Development environment

> [!NOTE]
> Tested only on OSX developer systems. Linux and Windows users may encounter unexpected issues.

Also, check out [develocity](../docs/develocity.md) and [proxy-repositories](../docs/proxy-repositories.md) guides.

## IntelliJ IDEA

Use latest stable IntelliJ IDEA Ultimate version (or latest EAP).

In menubar go `Help` -> `Change Memory Settings` and
set the value to no less than `8192 MiB` and no more than `12288 MiB`.

Install `Kotlin External FIR Support` plugin.

Install `Protocol Buffers` plugin.

**Optional**

Install `Kotlin Compiler DevKit` plugin.

Install `Detekt` plugin.
For setup use `config` and `baseline` files in the [detekt](../detekt) directory. 
Then go `Settings` -> `Tools` -> `detekt` and check `Enable background analysis` box.

**JetBrains Only**

Install `Space` IntelliJ plugin.
In `Settings` -> `Tools` -> `Space` log into https://jetbrains.team.

Install `Plugin DevKit` plugin.

## Java

You'll need a JDK on your machine.

In IntelliJ IDEA in menubar go to `File` -> `Project Structure` -> `Project` and select an SDK there.
If none download a new one. We recommend either `zulu` or `temurin` vendors.

Now make sure that the selected SDK is your `JAVA_HOME` env var.
If not setup is done for this, add this code to your `.zshrc` or equivalent shell settings:

```bash
export JAVA_HOME=$(cat ~/.java_home | xargs)
export PATH="$JAVA_HOME/bin:$PATH"
```

And add SDK home location to the `.java_home` file (you can check the location in IDEA):

```
/Users/<USER>/Library/Java/JavaVirtualMachines/azul-24.0.1/Contents/Home
```

Restart all shells and IDEA for changes to take place.

Go to `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Gradle` and make sure:

- `Build and run using` is set to `Gradle`
- `Run tests using` is set to `Gradle`
- `Distribution` is set to `Wrapper`
- `Gradle JVM` is set to `JAVA_HOME` and the value is correct

## Git and GitHub

Make sure your commit signing is set up. Check the
guide [here](https://docs.github.com/en/authentication/managing-commit-signature-verification/about-commit-signature-verification#gpg-commit-signature-verification)

Make sure your email in git credentials is the same as your GitHub's.

**JetBrains Only**

Go `Settings` -> `Version Control` -> `Commit` and check `Update Copyright` box.

For git operations we recommend using SSH instead of HTTP.

## XCode

Install `XCode` and emulators for iOS, iPadOS, watchOS and tvOS.

Run in the shell:

```Bash
xcode-select --install
xcodebuild -runFirstLaunch
```

## Other software

Install `chromedriver`, `node`, `python3` to your shell.

Install `Docker`.

**Optional**

Install `Wireshark`.

**JetBrains Only**

Go to https://jetbrains.team -> `Preferences` -> `Personal Data` and check `Need Docker Subscription` box.

## Documentation and Writerside

Docs are hosted with GitHub pages here: https://kotlin.github.io/kotlinx-rpc/

We use Writerside for hosting and writing our docs.
The source is in the [docs/pages](../docs/pages/kotlinx-rpc) directory.

Install IDEA `Writerside` plugin.

It will detect the source for the docs (hopefully) and you will have panels to manage the tree and see previews.

Check their [docs](https://www.jetbrains.com/help/writerside/discover-writerside.html) for further information.

We use [Dokka](https://kotl.in/dokka) V2 to generate API Reference 
and then with some custom logic put it into the Writerside. 

## TeamCity (JetBrains Only)

We use [TeamCity](https://www.jetbrains.com/teamcity/) for our CI runs.

Our server is: https://krpc.teamcity.com/

Our configs are hosted here: https://jetbrains.team/p/krpc/repositories/krpc-build/

Rule of thumb applies:
do as it was done in the repo before you, or if something new is needed... well, that's unfortunate.

Good luck working with it :)

## IDEA Plugin (JetBrains Only)

We develop `Kotlin External FIR Support` IDEA plugin.
The code is hosted here: https://github.com/Mr3zee/kotlin-plugins.
Docs for using the plugin can be found in that repo in `README.md`, `gradle.properties` and `build.gradle.kts`
and additionally here (https://plugins.jetbrains.com/docs/intellij/welcome.html) and
here (https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html).

## How to

### How to commit

Make changes granularly, each commit should make sense on its onw.

Add YouTrack issue code or GitHub issue number when possible.

Don't use LLMs for messages.

Before pushing, run (maybe separately):

```Bash
./gradlew checkLegacyAbi test allTests detekt :jpms-check:compileJava koverVerify koverHtmlReport
```

Beware, that `detekt` doesn't fail the build, but outputs messages into the console.
Makes sense to run it separately. You can also see generated reports 

### How to work with the compiler plugin

There are four modules there:
- [compiler-plugin-common](../compiler-plugin/compiler-plugin-common) - common for `backend` and `k2`
- [compiler-plugin-backend](../compiler-plugin/compiler-plugin-backend) - IR backend code. 
Here goes all heavy lifting codegen
- [compiler-plugin-cli](../compiler-plugin/compiler-plugin-cli) - FIR frontend code. This is the fancy part.
- [compiler-plugin-k2](../compiler-plugin/compiler-plugin-k2) - Entrypoint for the plugin, combines `backend` and `k2`.

#### Compiler Specific Modules

> [!NOTE] Has its drawbacks, maybe we'll redesign later.

We have a problem when developing a Kotlin compiler plugin:
Compiler API is not stable (not won't be for a while). So how can one develop a plugin that works stably?

We compile for multiple Kotlin versions at once!

We use an in-house ✨technology✨ for this: CSM or Compiler Specific Modules.
The idea is to substitute source sets depending on the current kotlin compiler version
(`kotlin-compiler` in [libs.versions.toml](../versions-root/libs.versions.toml)).

There is always a `core` source set (like this: [core](../compiler-plugin/compiler-plugin-k2/src/main/core)) 
that compiles nicely on all Kotlin versions. It is the majority of the code.
And then we have source sets that are swapped for a specific version 
(check other directories in the [main](../compiler-plugin/compiler-plugin-k2/src/main)).

The code that swaps them is in [compiler-specific-module.gradle.kts](../gradle-conventions/src/main/kotlin/compiler-specific-module.gradle.kts).

The rules are the following:
- Check all source sets that start with `v_` (for example, `v_2_2`, `v_2_1`, `v_2_2_2`). 
It matches the most specific like a tree (so for `2.2.20` it will be `v_2_2_2`, for `2.2.10` - `v_2_2` and for `2.3.0` none).
If found one - its taken.
- If none, then check all that start with `pre_` (for example, `pre_2_0_10`, `pre_2_1_0`, `pre_2_2_0`). 
Again, it matches the most specific inclusively, but chronologically
(for `2.1.0` it will be `pre_2_1_0`, for `2.0.0` - `pre_2_0_10`, for `2.3.0` none)
- Take `latest` otherwise
- Suffixes don't matter (`2.2.0` and `2.2.0-RC` are considered the same)

It allows supporting some past and future Kotlin versions 
(including versions for IDE, which are not in the stable list nor generally public) simultaneously.

Rule for complier API breaking changes:
- If some function or property changed signature or was replaced by another - 
use `VersionSpecificApi` interface in `core` to declare functionality 
and write the implementation in **all** other source sets 
(see [FirVersionSpecificApi.kt](../compiler-plugin/compiler-plugin-k2/src/main/core/kotlinx/rpc/codegen/FirVersionSpecificApi.kt)
and [VersionSpecificApi.kt](../compiler-plugin/compiler-plugin-backend/src/main/core/kotlinx/rpc/codegen/VersionSpecificApi.kt)).
- If some class differs in signature, 
write a proxy in `core` and implementation in source sets. FQ name must match exactly 
(see [FirRpcCheckersVS.kt](../compiler-plugin/compiler-plugin-k2/src/main/v_2_3/kotlinx/rpc/codegen/checkers/FirRpcCheckersVS.kt))
as an example, and all classes with the same names in other source sets.

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

### How to work with for-ide Kotlin versions

Check TeamCity build `Plugins for IDE` and see the steps.
It's complicated, the gist is:
- Locate the Kotlin version you want and set it in `kotlin-compiler` in `libs.versions.toml`
- Set `kotlinx.rpc.firIdeBuild=true` in [gradle.properties](../gradle.properties).

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

TODO: write a guide about kRPC API check tests.

### How to test a local library version with an external project

Use `./publishLocal.sh` script. All artifacts will be in the local directory of `<REPO_ROOT>/build/repo/` .

## Troubleshooting

Nothing works? Well, you are onto a journey!

Here is a 'simple' guide for solving problems:

- Gradle (he he)
    - Next commands in order from simpler to harder problems.
        - `./gradlew clean`
        - `./gradle --stop`
        - `./gradle <task> --rerun-tasks --no-configuration-cache --no-build-cache`
        - `mv ~/.gradle/gradle.properties ~/gradle.properties.temp && rm -rf ~/.gradle && mkdir ~/.gradle && mv ~/gradle.properties.temp ~/.gradle/gradle.properties` -
        use only as a last resort. Probably never needed, as the actual problem is in your code in 99.999 times out of 100.
    - Can't find dependency in repositories? Check dependencies' coordinates, check proxies, check original repo
      downtime.
    - Take a good look into your changes (or recent changes from remote).
- IDEA
    - Most problems with highlighting: `Menubar` -> `File` -> `Cache Recovery` -> `Repair IDE` and follow instructions
      in popups.
    - Other problems:
        - Check code, maybe you are wrong
        - Resync Gradle (maybe more than once)
        - Remove all code in the file and bring it back
        - Save all local changes and do `git clean -xfd` (Google the command effect before executing)
        - Restart your machine
        - Try different IDE version (last resort)
- Docker
    - `Cannot connect to the Docker daemon` - open `Docker Desktop`

Something doesn't work, and you are sure it's not your fault? Report it appropriately! Don't be lazy.

**Update this doc if you think the problem you solved is typical.**

## Tasks to know about

- `updateProperties` - keep top-level gradle properties in sync.
When you change something in [gradle.properties](../gradle.properties), 
all included builds (not subprojects) must reflect the change.
- `dokkaGenerate` - generate Dokka documentation. Files can be found in [api](pages/api).
- `checkLegacyAbi` / `updateLegacyAbi` - ABI checks. 
See https://kotlinlang.org/docs/whatsnew22.html#binary-compatibility-validation-included-in-kotlin-gradle-plugin.
Former BCV: https://github.com/Kotlin/binary-compatibility-validator
- `kotlinUpgradeYarnLock` - update [kotlin-js-store](../kotlin-js-store) contents, usually after Kotlin version update.
- `updateDocsChangelog` - put modified [CONTRIBUTING.md](../CONTRIBUTING.md) into [topics](pages/kotlinx-rpc/topics)
- `detekt` - run detekt checks.
- `verifyPlatformTable` / `dumpPlatformTable` - Update [platforms.topic](pages/kotlinx-rpc/topics/platforms.topic)
- `generateTests` - see [complier plugin tests](#compiler-tests)
- `clean` / `cleanTest` / `cleanAllTests` - clean tasks.
  - `clean` - everything
  - `cleanTest` - JVM test results
  - `cleanAllTests` - KMP test results

## Other

See [gradle.properties](../gradle.properties) for additional properties:

```properties
# Uncomment to skip attempts to publish Develocity build scans
# Add this property to ~/.gradle/gradle.properties to avoid polluting git with unwanted changes
# kotlinx.rpc.develocity.skipBuildScans=true

# Uncomment to skip adding git tags to Develocity build scan (might be good in docker when there is no git)
# Add this property to ~/.gradle/gradle.properties to avoid polluting git with unwanted changes
#kotlinx.rpc.develocity.skipGitTags=true
```
