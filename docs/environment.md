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
use [ConformanceClient.kt](../tests/protobuf-conformance/src/main/kotlin/kotlinx/rpc/protoc/gen/test/ConformanceClient.kt)
and this command:
```bash
gradle :tests:protobuf-conformance:runConformanceTest -Pconformance.test.debug='true' -Pconformance.test='<test_name>'
```

Then use IntelliJ 'Attach to Process' feature to attach to the process on port 5005.
(kill the process if port is already in use: `kill $(lsof -t -i:5005)`)

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
- Kotlin/Js or Kotlin/Wasm
  - `kotlinUpgradePackageLock` or `kotlinWasmUpgradePackageLock` (and also `kotlinNpmInstall` or `kotlinWasmNpmInstall`)
  have a funny tendency to fail sometimes, and you don't know why. 
    
    I'll tell you! 
    
    We use proxy repos, and sometimes dependencies get downloaded from the wrong source.
    Make sure ALL urls in `package-lock.json` files start with `https://packages.jetbrains.team/npm/p/krpc/build-deps/`.
    
    If something doesn't work, your steps are:
    - Delete `package-lock.json` file
    - Delete `<REPO_ROOT>/build/js` / `<REPO_ROOT>/build/wasm`
    - Run `kotlinUpgradePackageLock` / `kotlinWasmUpgradePackageLock`
    - If the problem persists:
      - Check that `<REPO_ROOT>/build/<target>/.npmrc` AND `<REPO_ROOT>/build/<target>/.yarnrc` are present
      - Check that `.yarnrc` contains one line: `registry: "https://packages.jetbrains.team/npm/p/krpc/build-deps/"`
      - Check that `.npmrc` contains the following lines:
        - `registry="https://packages.jetbrains.team/npm/p/krpc/build-deps/"`
        - `always-auth=true`
        - `save-exact=true`
        - `//packages.jetbrains.team/npm/p/krpc/build-deps/:_authToken=<your_auth_token>`, 
          where `<your_auth_token>` is from the [proxy-repositories.md](proxy-repositories.md) guide.
      - Check that `<USER_HOME>/.npmrc` / `<USER_HOME>/.yarnrc` don't interfere
      command to debug. Replace versions of tools if needed.
  - When you get the following error — `puppeteer` failed to run the installation script. 
    Reasons vary, try updating the version to a newer one, 
    check the [.puppeteerrc.cjs](../.puppeteerrc.cjs) and [chrome_bin.js](../karma/chrome_bin.js) files if they are valid js.
  
        For (2), check out our guide on configuring puppeteer at https://pptr.dev/guides/configuration.
        at ChromeLauncher.resolveExecutablePath (/rpc/build/js/packages/kotlinx-rpc-utils-test/node_modules/puppeteer-core/lib/cjs/puppeteer/node/ProductLauncher.js:295:27)

  - When the previous error is gone, you may get the next one.
         
        Errors occurred during launch of browser for testing.
            - ChromeHeadless
            Please make sure that you have installed browsers.
            Or change it via
            browser {
                testTask {
                    useKarma {
                        useFirefox()
                        useChrome()
                        useSafari()
                    }
                }
            }
    This means the `puppeteer` failed to locate Chrome.
    Either the cache dir is wrong (check [.puppeteerrc.cjs](../.puppeteerrc.cjs) file) or it really isn't there.
    
    Reasons again vary.
    
    - When `npm` installs `puppeteer`, it should execute script to install the browser too
    (On CI to the `<ROOT_DIR>/.puppeteer/browsers` directory).
    This absence may be caused by the `--ignore-scripts` flag. 
    
      Check the clean installation (`rm -rf build && ./gradlew clean cleanJsBrowserTest`) with `--debug` flag.
      (Something like `./gradlew jsBrowserTest --debug`).
    
      The property is set in [npm.kt](../gradle-conventions/src/main/kotlin/util/tasks/npm.kt), see `ignoreScripts`, 
      it should be `false`. 
   
      **IMPORTANT: run in docker with `TEAMCITY_VERSION` env var set, if you are chasing a CI fail**.
    
    - If this is not the case, check the debug log for other `node`-related issues.
      Try installing browsers manually: 
    
          ~/.gradle/nodejs/node-v22.0.0-linux-x64/bin/node build/js/node_modules/puppeteer/install.mjs
    
      If this works — problem is somewhere in KGP and probably your configs. 
    Check that your config (like ones with `ignoreScript`) are actually applied, 
    as they use on demand execution and may target wrong plugin or extension and never be executed.
    
      **Bonus**: it may not be installed, because npm install doesn't do this. 
    See the long comment in [npm.kt](../gradle-conventions/src/main/kotlin/util/tasks/npm.kt).

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
- `kotlinUpgradePackageLock` / `kotlinWasmUpgradePackageLock` - update [kotlin-js-store](../kotlin-js-store) contents, 
usually after Kotlin version update.
- `updateDocsChangelog` - put modified [CONTRIBUTING.md](../CONTRIBUTING.md) into [topics](pages/kotlinx-rpc/topics)
- `detekt` - run detekt checks.
- `verifyPlatformTable` / `dumpPlatformTable` - Update [platforms.topic](pages/kotlinx-rpc/topics/platforms.topic)
- `generateTests` - see [complier plugin tests](#compiler-tests)
- `clean` / `cleanTest` / `cleanAllTests` - clean tasks.
  - `clean` - everything
  - `cleanTest` - JVM test results
  - `cleanAllTests` - KMP test results
- `validatePublishedArtifacts` task and more importantly `./validatePublishedArtifacts.sh` script.
  
  They are used to validate published artifacts and ensure you didn't delete or published something accidentally.
  
  Available options: `--dump` - update files, `-s` - no Gradle output except for errors, `-v` - verbose output.

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
