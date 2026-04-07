---
name: use-teamcity
description: >
  Interact with the kotlinx-rpc TeamCity CI/CD project using the `teamcity` CLI.
  Use this skill whenever the user wants to trigger builds, check build status,
  view build logs, monitor failures, manage the build queue, inspect agents,
  or do anything related to TeamCity CI. Also use it when the user mentions
  "TC", "TeamCity", "CI build", "run build", "build status", "build log",
  "trigger build", "check CI", or references a build configuration name or ID. 
  Trigger even if the user doesn't say "TeamCity" explicitly -- if 
  they ask about CI status, build failures, or want to run something 
  on CI rather than locally, this is the right skill. 
  Do NOT use this skill for local Gradle builds -- use
  `running_gradle_builds` or `running_gradle_tests` instead.
---

# TeamCity CLI for kotlinx-rpc

Interact with the kotlinx-rpc TeamCity project via the `teamcity` CLI (expected
to be installed at `/opt/homebrew/bin/teamcity`).

For **local** Gradle builds and tests, use `running_gradle_builds` /
`running_gradle_tests` skills instead.

## Agent workflow override

When this skill is invoked from the `fix-issue` skill (autonomous issue-fixing workflow),
**never run `teamcity auth status` or `teamcity auth login`**. Instead, prefix every
`teamcity` command with `TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN`. The `fix-issue` skill's
Authentication section has full details. Outside of `fix-issue`, the defaults below apply.

## Prerequisites

Before running any commands, verify authentication:

```bash
teamcity auth status
```

If not authenticated, the user needs to log in interactively -- suggest they run
`! teamcity auth login` in the prompt so the browser-based auth flow works in
their session.

## TeamCity Project Structure

The root project is **kRPC**. It has two main branches:

- **Build** (`Build_kRPC`) -- CI builds triggered on PRs and commits
- **Release** (`Release_kRPC`) -- Publication to Maven Central, Space, GitHub

### Build Pipeline

| Composite Build                   | What it runs                                  |
|-----------------------------------|-----------------------------------------------|
| `Build_kRPC_All`                  | Everything below                              |
| `Build_Code_All`                  | All platform code builds                      |
| `Build_Checks_All`                | ABI check, Detekt, JPMS, Artifacts, Changelog |
| `Build_kRPC_All_Compiler_Plugins` | Compiler plugin for all Kotlin versions       |
| `Build_kRPC_All_Gradle_Plugin`    | Gradle plugin build + tests                   |

### Platform-Specific Code Builds

| Build ID | Platforms | Agent |
|---|---|---|
| `Build_kRPC_Apple` | macOS, iOS, tvOS, watchOS | macOS |
| `Build_kRPC_Windows` | mingwX64 | Windows |
| `Build_kRPC_Java_JS_WASM_and_Linux` | JVM, JS, WASM, linuxX64/Arm64 | Linux |
| `Build_kRPC_StressTests` | JVM stress tests | Linux |

### Quality Checks

| Build ID | What |
|---|---|
| `Build_kRPC_CheckApi` | Binary compatibility (ABI) |
| `Build_kRPC_DetektVerify` | Static analysis |
| `Build_kRPC_JPMS` | Java module system |
| `Build_kRPC_ArtifactsValidation` | Published artifact list |
| `Build_kRPC_Changelog` | Changelog validation |
| `Build_kRPC_Samples` | Sample projects |
| `Build_kRPC_Protoc_Plugin_Test` | Protoc plugin tests |
| `Build_kRPC_Gradle_Plugin_Test` | Gradle plugin tests |
| `Build_kRPC_Gradle_Plugin_CheckApi` | Gradle plugin ABI |
| `Build_kRPC_Gradle_Plugin_DetektVerify` | Gradle plugin Detekt |

### Compiler Plugin Builds

Build IDs follow the pattern `Build_kRPC_Plugins_<version>_KotlinVersion_` where
`<version>` is the Kotlin version with dots replaced by underscores.

Example: Kotlin 2.3.20 -> `Build_kRPC_Plugins2_3_20_KotlinVersion_`

Check TeamCity for currently supported Kotlin versions.

### Special Builds

| Build ID                                        | Purpose                              | Trigger            |
|-------------------------------------------------|--------------------------------------|--------------------|
| `Build_Compiler_Plugins_Latest_Public_Unstable` | Build against latest Kotlin unstable | Nightly            |
| `Build_Compiler_Plugins_Latest_Kotlin_Master`   | Build against Kotlin master          | Daily 10:00 Berlin |
| `Build_kRPC_Plugins_For_IDE`                    | IDE plugin artifacts                 | On demand          |
| `Build_kRPC_New_Ide_Versions_Checker`           | Detect new IntelliJ/Android Studio   | Every 20 mins      |

### Release Builds

Release builds publish to three repositories. IDs follow patterns:

- **Sonatype** (Maven Central): `Release_kRPC_ToSonatype_*`
- **Space EAP**: `Release_kRPC_ToSpace_*`
- **Space gRPC**: `Release_kRPC_ToGrpc_*`

Key release configurations:

| Build ID                                | Purpose                                                                 |
|-----------------------------------------|-------------------------------------------------------------------------|
| `Release_kRPC_ToSonatype_All`           | Assemble all artifacts to later release to Sonatype                     |
| `Release_kRPC_Upload_To_Central_Portal` | Upload assembled artifacts (from sonatype type build) to Central Portal |
| `Release_kRPC_ToSpace_All`              | Release everything to Space EAP                                         |
| `Release_kRPC_ToGrpc_All`               | Release everything to Space gRPC.                                       |
| `Release_kRPC_Produce`                  | Generate Dokka documentation                                            |

## Common Workflows

### Trigger a build on a branch

```bash
teamcity run start Build_kRPC_All --branch feature/my-branch
```

You can add the `--watch` flag to streams progress in real-time. Without it, the command returns
immediately after queuing.

To trigger with custom parameters (e.g., override Kotlin version):

```bash
teamcity run start Build_kRPC_All --branch main -E KOTLIN_COMPILER_VERSION_ENV=2.3.20
```

### Check recent build status

```bash
# Recent failures across all builds
teamcity run list --status failure --since 24h

# Recent builds for a specific configuration
teamcity run list --job Build_kRPC_All --limit 10

# Recent builds on a specific branch
teamcity run list --job Build_kRPC_All --branch main --limit 5

# Only running builds
teamcity run list --status running
```

### Investigate a build failure

```bash
# View build details
teamcity run view <build-id>

# Show failure summary (most useful for quick diagnosis)
teamcity run log <build-id> --failed

# Stream full build log (opens in pager)
teamcity run log <build-id>

# Show test results
teamcity run tests <build-id>

# Show VCS changes that triggered the build
teamcity run changes <build-id>
```

### Cancel or restart builds

```bash
# Cancel a running or queued build
teamcity run cancel <build-id> --comment "Cancelling: wrong branch"

# Restart a failed build
teamcity run restart <build-id>
```

### Download build artifacts

```bash
# List artifacts
teamcity run artifacts <build-id>

# Download artifacts
teamcity run download <build-id>
```

### View project/job hierarchy

```bash
# List all projects
teamcity project list

# View project tree
teamcity project tree kRPC

# List jobs in a project
teamcity job list --project Build_kRPC

# View snapshot dependency tree for a build config
teamcity job tree Build_kRPC_All
```

### Dry-run (preview without triggering)

```bash
teamcity run start Build_kRPC_All --branch main --dry-run
```

### JSON output for scripting

```bash
# Full JSON
teamcity run list --json --limit 5

# Specific fields
teamcity run list --json=id,status,webUrl --limit 5

# Plain text (for grep/awk)
teamcity run list --plain --limit 10
```

## Build Configuration DSL

The TeamCity Kotlin DSL configs live in the sibling repository:

```
../kotlinx-rpc-build/.teamcity/src/
```

Ensure that the repository is cloned and up-to-date before running commands.
Remote: https://git.jetbrains.team/krpc/krpc-build.git
If not cloned - notify the user to clone it.

Key files when you need to understand or modify build configs:

| Path (relative to `.teamcity/src/`) | Purpose |
|---|---|
| `project/Project.kt` | Root project definition |
| `project/build/__project.kt` | Build subproject structure |
| `project/release/__project.kt` | Release subproject structure |
| `util/id.kt` | All build configuration IDs |
| `util/target.kt` | Platform target definitions |
| `util/compilerVersions.kt` | Supported Kotlin versions list |
| `util/agent.kt` | Agent requirements |
| `util/repository.kt` | Publication repository definitions |
| `settings/kotlin.kt` | Kotlin version parameters, IDE integration |
| `settings/artifacts.kt` | Artifact collection rules |
| `settings/publishing.kt` | Publishing configuration |
| `dsl/build/` | Build DSL builders (per-platform) |
| `dsl/release/` | Release DSL builders |
| `project/vcs.kt` | VCS root configuration |

Read these files from `../kotlinx-rpc-build/` when the user asks about
how a specific build is configured or wants to modify CI behavior.

## Tips

- Build IDs are case-sensitive -- use them exactly as listed above.
- For composite builds (like `Build_kRPC_All`), failures in any dependency build
  will show in the composite. Use `run log <id> --failed` on the specific
  dependency build that failed, not the composite.
- Use `--since` and `--until` with durations like `24h`, `7d`, `1h` for time-based
  filtering.
- The `teamcity api` command can make raw REST API calls for anything the CLI
  doesn't directly support: `teamcity api /app/rest/builds?locator=...`
