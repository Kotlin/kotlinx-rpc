# Development environment

> [!NOTE]
> This document is for **human developers only** — it covers machine setup, IDE configuration, and software installation.
> AI agents and LLMs should use `CLAUDE.md` and skills for project guidelines instead.

> [!NOTE]
> Tested only on OSX developer systems. Linux and Windows users may encounter unexpected issues.

Also, check out [develocity](../docs/develocity.md) and [proxy-repositories](../docs/proxy-repositories.md) guides.

For development workflows, see [workflow](../docs/workflow.md).
For troubleshooting, see [troubleshooting](../docs/troubleshooting.md).

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

## Android SDK
In the root of the project you should create a `local.properties` file with the location of the Android SDK:
```properties
sdk.dir=/Users/<user>/Library/Android/sdk
```

## YouTrack MCP

The project includes a `.mcp.json` that connects Claude Code to YouTrack via MCP.
To authenticate, set the `YOUTRACK_TOKEN` environment variable.

1. Go to https://youtrack.jetbrains.com/users/me
2. Navigate to **Account Security** -> **Tokens** -> **New token...**
3. Select **YouTrack** as the scope, create the token and copy it
4. Add to your `.zshrc` (or equivalent):

```bash
export YOUTRACK_TOKEN="your-token-here"
```

5. Restart your shell (or `source ~/.zshrc`)

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
