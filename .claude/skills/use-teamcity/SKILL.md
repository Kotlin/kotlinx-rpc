---
name: use-teamcity
description: Works with the kotlinx-rpc TeamCity project through the `teamcity` CLI, for example to start builds, read status, logs, and test results, select the builds for a change, and run release builds. Use for each CI question that isn't about GitHub Actions. For local builds, use the Gradle skills.
---

# TeamCity for kotlinx-rpc

Use the `teamcity` CLI. `teamcity <command> --help` gives all flags.
Check the login with `teamcity auth status`. If the user isn't logged in, ask the user to run `! teamcity auth login`.

The root project is `kRPC`. It has the `Build_kRPC` project for CI builds and the `Release_kRPC` project for publication.

| Reference | Content |
|---|---|
| `references/builds.md` | The builds to start for a change, and all other build IDs |
| `references/release-builds.md` | The release builds, and how to test them |

## Commands

```bash
teamcity run start <build-id> --branch <branch>      # --watch waits for the end, --dry-run starts nothing
teamcity run start <build-id> --branch main -E KOTLIN_VERSION=2.3.20   # -E environment variable, -P parameter

teamcity run list --job <build-id> --branch main --limit 5
teamcity run list --status failure --since 24h       # also --status running, --until, --json=id,status,webUrl

teamcity run view <run-id>
teamcity run log <run-id> --failed                   # the summary of the failure
teamcity run tests <run-id>
teamcity run changes <run-id>
teamcity run artifacts <run-id>                      # `run download` gets the artifacts
teamcity run cancel <run-id> --comment "<reason>"
teamcity run restart <run-id>

teamcity project tree kRPC
teamcity job list --project Build_kRPC
teamcity job tree <build-id>                         # the snapshot dependencies
teamcity api "/app/rest/builds?locator=..."          # REST, for all that the CLI doesn't have
```

- Build IDs are case-sensitive.
- A composite build shows the failures of its dependencies. Read the log of the dependency that failed, not of the composite.

## Build configuration sources

The Kotlin DSL is in the sibling repository, in `../kotlinx-rpc-build/.teamcity/src/`.
If the directory doesn't exist, ask the user to clone `https://github.com/JetBrains/ked-kotlinx-rpc-build.git`.
Pull the repository before you read it.

| Path | Content |
|---|---|
| `project/Project.kt`, `project/vcs.kt` | The root project, the VCS roots |
| `project/build/`, `project/release/` | The build and release projects. `__project.kt` has the structure. |
| `util/id.kt` | All build IDs |
| `util/target.kt`, `util/agent.kt`, `util/repository.kt` | Platform targets, agent requirements, publication repositories |
| `settings/` | Kotlin version parameters (`kotlin.kt`), artifact rules (`artifacts.kt`), publication (`publication.kt`) |
| `dsl/build/`, `dsl/release/` | The DSL builders |
