---
name: gradle_expert
description: >
  Provides expert build engineer guidance on Gradle Kotlin DSL scripts, plugin development, and deep internals research;
  use for build failures, compilation errors, dependency conflicts, or complex build authoring.
  Do NOT use for executing builds/tests (use `running_gradle_builds`/`running_gradle_tests`) or dependency graph auditing.
license: Apache-2.0
metadata:
  author: https://github.com/rnett/gradle-mcp
  version: "1.0"
---

# Senior Gradle Build Engineering & Internal Research

Provides authoritative guidance and automation for creating, modifying, and auditing Gradle build logic. Integrates official documentation, best practices, and deep-dive source research into a unified workflow for build logic maintenance.

## Constitution

- **ALWAYS** check for existing conventions in the current project before proposing changes.
- **ALWAYS** prefer Kotlin DSL (`.kts`) unless the project explicitly uses Groovy.
- **ALWAYS** use lazy APIs (e.g., `register` instead of `create`) to maintain configuration performance.
- **ALWAYS** use `libs.versions.toml` for dependency management if it exists.
- **ALWAYS** use `gradle_docs` for authoritative documentation lookup instead of generic web searches.
- **ALWAYS** use `search_dependency_sources` with `gradleSource = true` when researching core Gradle behavior.
- **ALWAYS** use `inspect_build` with `testName` and `mode="details"` for individual test output instead of generic `taskPath`, `captureTaskOutput`, or shell `grep`.
- **ALWAYS** use safe navigation (`?.url?.toString()`) and provide fallback values when accessing `ArtifactRepository` URLs in Gradle init scripts or plugins to prevent `NullPointerException`.
- **STRONGLY PREFERRED**: Use `inspect_build` for all failure diagnostics. It is more token-efficient than reading raw console logs and provides structured access to failures, stack traces, and problems.
- **NEVER** guess internal API behavior; verify it by reading the source code of the Gradle Build Tool.

## Surgical Failure Diagnostics with `inspect_build`

As a Senior Build Engineer, you must move beyond raw logs. The `inspect_build` tool is your surgical diagnostic suite.

### 1. Build Summary (Finding the Root Cause)

Start with a summary to find IDs for specific failures or problems.

- **Example**: `inspect_build(buildId="ID")`

### 2. Individual Test Failures

**CRITICAL**: NEVER use `taskPath` or shell `grep` for tests. ALWAYS use `testName` with `mode="details"` to see the full output and stack trace.

- **Example**: `inspect_build(buildId="ID", mode="details", testName="com.example.MyTest.shouldWork")`

### 3. Build-Level Failures

For compilation or configuration errors, use `failureId` found in the build summary.

- **Example**: `inspect_build(buildId="ID", mode="details", failureId="F0")`

### 4. Problems & Warnings

For deep-dives into specific problems (e.g., deprecations, plugin issues), use `problemId`.

- **Example**: `inspect_build(buildId="ID", mode="details", problemId="P1")`

## Directives

- **Author builds idiomatically**: Use standard patterns for multi-project builds and convention plugins.
- **Perform performance audits**: Identify configuration bottlenecks and recommend lazy API migrations.
- **Research internals authoritatively**: Use `gradle_docs` and internal source search to understand "how it works" at the engine level. Use `read_dependency_sources` to explore implementation details.
- **Diagnose failures surgically**: Use `inspect_build` with `testName` and `mode="details"` to analyze test failures and stack traces instead of reading raw console logs. DO NOT use `taskPath` or `captureTaskOutput` for tests.
- **Resolve dependencies precisely**: Use `inspect_dependencies` and `managing_gradle_dependencies` for auditing and updates.
- **Consult best practices**: Refer to the [Best Practices Snapshot](./references/best_practices.md) for a high-level overview. **ALWAYS** use `gradle_docs` with `tag:best-practices` to retrieve the latest and most comprehensive
  guidelines from the official documentation.
- **Use `envSource: SHELL` if environment variables are missing**: If Gradle fails to find expected environment variables (e.g., `JAVA_HOME` or specific JDKs), it may be because the host process started before the shell environment was
  fully loaded. Set `invocationArguments: { envSource: "SHELL" }` to force a new shell process to query the environment.

## Workflows

### 1. Creating a New Module

1. **Identify the Project Context**: Use the `gradle` tool with `commandLine: ["projects"]` or the `introspecting_gradle_projects` skill to find the correct parent path.
2. **Create Directory Structure**: Use `run_shell_command` with `mkdir subproject/src/main/kotlin` (or equivalent).
3. **Add to `settings.gradle.kts`**: Use `replace` or `write_file` to append `include(":<module-name>")`.
4. **Create `build.gradle.kts`**: Use idiomatic patterns (e.g., applying convention plugins).
5. **Verify**: Run `gradle` tool with `commandLine: [":<module-name>:tasks"]` to ensure it's correctly integrated.

### 2. Adding a Dependency

1. **Search Maven Central**: Use the `lookup_maven_versions` tool to find the artifact.
2. **Update `libs.versions.toml`**: Add the dependency coordinates to the catalog.
3. **Apply to `build.gradle.kts`**: Use the type-safe accessor from the catalog.
4. **Verify**: Run the `gradle` tool with `commandLine: ["dependencies"]` to check resolution.

### 3. Performance Audit

1. **Enable Configuration Cache**: Run the `gradle` tool with `commandLine: ["help", "--configuration-cache"]`.
2. **Analyze Violations**: Identify tasks that are not compatible with the cache.
3. **Propose Fixes**: Recommend migrating to lazy APIs (`Property`, `Provider`) or using `@Internal`/`@Input` correctly.

## Examples

### Adding a new dependency to a module

Tool: `lookup_maven_versions`

```json
{
  "coordinates": "com.google.guava:guava"
}
```

// Reasoning: Searching Maven Central for the exact coordinates and latest version.

### Creating a new sub-project

Tool: `run_shell_command`

```json
{
  "command": "New-Item -ItemType Directory -Force -Path subproject/src/main/kotlin"
}
```

// Reasoning: Creating the standard directory structure for a Kotlin JVM project using correct PowerShell syntax.

### Searching for Gradle internal engine source code

Tool: `search_dependency_sources`

```json
{
  "query": "Property",
  "searchType": "DECLARATION",
  "gradleSource": true
}
```

## When to Use

- **New Module Creation**: When adding a new project or module to a multi-project build.
- **Dependency Migration**: When updating dependencies or moving to version catalogs.
- **Build Logic Refactoring**: When cleaning up complex build scripts or creating convention plugins.
- **Performance Troubleshooting**: When builds are slow or failing during the configuration phase.
- **Deep Technical Research**: When you need to understand the internal implementation of a Gradle feature or plugin.

## Resources

- [Best Practices](./references/best_practices.md)
- [Common Build Patterns](./references/common_build_patterns.md)
- [Internal Research Guidelines](./references/internal_research_guidelines.md)
