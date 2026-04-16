---
name: use-develocity
description: >
  Query and analyze kotlinx-rpc build scans, test results, and failure patterns
  from the Develocity server (ge.jetbrains.com) using MCP tools. Use this skill
  whenever the user wants to look at build scans, investigate CI failures across
  builds, check build cache hit rates, analyze flaky tests, find recurring
  failure patterns, or query build performance history. Also trigger when the
  user mentions "Develocity", "build scan", "GE", "ge.jetbrains.com",
  "build cache stats", "flaky tests", "failure groups", or asks about build
  trends, test stability, or CI health. Trigger even if the user doesn't say
  "Develocity" explicitly -- if they want to look at historical build data,
  cross-build test analysis, or failure aggregation, this is the right skill.
  Do NOT use this skill for running local Gradle builds -- use
  `running_gradle_builds` or `running_gradle_tests` instead. Do NOT use this
  skill for TeamCity build triggering or queue management -- use `use-teamcity`.
---

# Develocity for kotlinx-rpc

Query build scans, test results, and failure patterns from the kotlinx-rpc
Develocity instance via `mcp__develocity__*` MCP tools.

## Project Setup

- **Server**: `https://ge.jetbrains.com`
- **Build scan URL pattern**: `https://ge.jetbrains.com/s/<buildId>`
- **Gradle project name**: `kotlinx-rpc`
- **Build tool**: `gradle`

### How it's wired

All Develocity configuration lives in a single convention plugin:
`gradle-conventions-settings/develocity/` which exposes
`conventions-develocity` as a settings plugin. Builds opt in by applying
`id("conventions-develocity")` in their `settings.gradle.kts`.

| Build                                | Publishes scans? |
|--------------------------------------|------------------|
| Root (`:`)                           | Yes              |
| `compiler-plugin/`                   | Yes              |
| `dokka-plugin/`                      | Yes              |
| `gradle-plugin/`                     | No               |
| `protoc-gen/`                        | No               |
| `native-deps/shims/`                 | No (hard opt-out)|
| `native-deps/grpc-c-prebuilt/`       | No (hard opt-out)|

### Build cache

- Remote cache at `ge.jetbrains.com` is always **readable**.
- Only CI builds **push** to the remote cache (`isPush = isCIRun`).
- Local cache is disabled on CI.

### Custom scan metadata

Scans include custom values and links that are useful for filtering:

| Context   | Custom value          | Custom link                           |
|-----------|-----------------------|---------------------------------------|
| CI (TC)   | `CI build id`         | `kotlinx.rpc TeamCity build` (link)   |
| Local     | `Git Commit ID`       | `GitHub Commit Link`                  |
| Local     | `Git Branch Name`     | `GitHub Branch Link`                  |
| Local     | `Git Status` (dirty)  | --                                    |

### Opt-out properties

Developers can add these to `~/.gradle/gradle.properties`:
- `kotlinx.rpc.develocity.skipBuildScans=true` -- stop publishing scans
- `kotlinx.rpc.develocity.skipGitTags=true` -- stop adding git metadata

## MCP Tools

Use develocity MCP to query and analyze build scans.
