---
name: use-develocity
description: Queries the kotlinx-rpc build scans on develocity.jetbrains.com with the Develocity MCP tools, for build history, failures across builds, unstable tests, cache hit rates, and build performance. For local builds, use the Gradle skills. For TeamCity builds, use use-teamcity.
---

# Develocity for kotlinx-rpc

Query the scans with the `mcp__develocity__*` tools.

| Setting | Value |
|---|---|
| Server | `https://develocity.jetbrains.com` |
| Build scan URL | `https://develocity.jetbrains.com/s/<buildId>` |
| Gradle project name | `kotlinx-rpc` |
| Build tool | `gradle` |

## Project setup

The settings plugin `conventions-develocity` in `gradle-conventions-settings/develocity/` has the whole configuration.

- The root build, `compiler-plugin/`, and `dokka-plugin/` publish scans. `gradle-plugin/` and `protoc-gen/` don't.
- All builds read the remote build cache. Only CI builds write to it.
- A scan has these custom values and links for filters:

  | Build | Custom values | Custom links |
  |---|---|---|
  | CI | `CI build id` | `kotlinx.rpc TeamCity build` |
  | Local | `Git Commit ID`, `Git Branch Name`, `Git Status` (only with local changes) | `GitHub Commit Link`, `GitHub Branch Link` |

- A developer can set these properties in `~/.gradle/gradle.properties`:
  - `kotlinx.rpc.develocity.skipBuildScans=true`: the build doesn't publish scans.
  - `kotlinx.rpc.develocity.skipGitTags=true`: the scan has no Git values.
