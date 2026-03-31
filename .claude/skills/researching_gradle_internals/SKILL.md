---
name: researching_gradle_internals
description: >
  Searches and retrieves official Gradle User Guide, DSL Reference, and internal engine source code authoritatively;
  use for researching core Gradle features, verifying behavior, and deep-diving into internals.
  Do NOT use for project dependency source exploration (use `searching_dependency_sources`) or running builds.
license: Apache-2.0
metadata:
  author: https://github.com/rnett/gradle-mcp
  version: "4.2"
---

# Authoritative Gradle Documentation & Source Research

Researches official documentation and probes Gradle's internal source code with absolute precision to understand the build tool's behavior and protocols.

## Constitution

- **ALWAYS** use the `gradle_docs` tool as the primary source for documentation queries.
- **ALWAYS** use `search_dependency_sources` with `gradleSource: true` when probing Gradle's internal logic.
- **ALWAYS** provide absolute paths for `projectRoot`.
- **ALWAYS** scope documentation searches using the `tag:<section>` syntax (e.g., `tag:dsl`, `tag:userguide`).
- **NEVER** assume behavior without verifying it against the documentation OR the actual source implementation.
- **ALWAYS** check for breaking changes in the `release-notes` tag when investigating version-specific regressions.

## Directives

- **Identify the research target**: ALWAYS determine if you need authoritative guidance (Documentation) or the ground-truth implementation (Source).
- **Scope documentation surgically**: ALWAYS use tags like `userguide`, `dsl`, and `release-notes` in your `gradle_docs` query to minimize irrelevant results.
- **Probe Gradle sources authoritatively**: ALWAYS use `gradleSource: true` in `search_dependency_sources` to target Gradle's internal engine.
- **Read implementation details**: ALWAYS use `read_dependency_sources` with `gradleSource: true` to examine the actual code of any Gradle interface or class once identified.
- **Escape Lucene special characters**: When searching documentation via `gradle_docs` or source code via `search_dependency_sources` (with `searchType: "FULL_TEXT"`), ALWAYS escape special characters like `:`, `=`, `+`, `-`, `*`, `/` with
  a backslash (e.g., `\:`) or enclose them in double quotes (e.g., `"val x = 10"`) for literal searches to avoid Lucene syntax errors.
- **Identify Search Mode for Internals**:
    - Use `DECLARATION` to find the exact declaration of a Gradle interface or class. All declaration searches are **case-sensitive**. Do NOT include keywords like `class` or `interface` (e.g., use `Project`, not `interface Project`).
    - You can search by simple name (e.g., `Project`), full name (e.g., `org.gradle.api.Project`), or partial package paths (e.g., `api.Project`).
    - Supports **glob wildcards** for FQNs: `*` matches one segment, `**` matches multiple (e.g., `fqn:org.gradle.*.Project`, `fqn:org.**.Project`).
    - Supports full **Lucene query syntax** (e.g., `name:Project AND fqn:org.gradle.api.*`).
    - Use `FULL_TEXT` to find internal usage patterns, constants, or behavior described in the source. `FULL_TEXT` searches are **case-insensitive**.
    - Use `GLOB` to locate Gradle's internal resource files or build scripts. `GLOB` searches are **case-insensitive**.
- **Verify against the local version**: The tools automatically target the Gradle version used by the current project. ALWAYS use the `version` argument for `gradle_docs` when researching other releases.

## Available Documentation Tags

| Tag              | Section                                                    |
|------------------|------------------------------------------------------------|
| `userguide`      | The official Gradle User Guide.                            |
| `dsl`            | The Gradle DSL Reference (Groovy and Kotlin DSL).          |
| `javadoc`        | The Gradle Java API Reference.                             |
| `samples`        | Official Gradle samples and examples.                      |
| `release-notes`  | Version-specific release insights.                         |
| `best-practices` | Official Gradle best practices and performance guidelines. |

## When to Use

- **DSL & Plugin Verification**: When you need to understand the precise syntax for DSL elements (e.g., `publishing`, `signing`) or official plugin configurations. Use `tag:dsl`.
- **Feature Implementation Analysis**: When researching advanced topics like custom task authoring or plugin development. Use `tag:userguide` for guidance and `gradleSource: true` for implementations.
- **Version Compatibility Auditing**: When checking for new features, deprecations, or breaking changes in a specific Gradle release. Use `tag:release-notes`.
- **Internal Engine Exploration**: When performing deep-dives into Gradle's behavior (e.g., dependency resolution logic, task execution engine) using `gradleSource: true`.

## Workflows

### 1. Researching a Core Feature

1. Search the `userguide` via `gradle_docs(query="tag:userguide <term>")`.
2. Review the markdown results and read specific pages using the `path`.
3. If the behavior is undocumented, search for the corresponding classes in the Gradle source via `search_dependency_sources(query="class <Name>", gradleSource=true)`.

### 2. Probing Plugin APIs

1. Use `tag:dsl` to find the documentation for the plugin's configuration block.
2. Search for the plugin's implementation classes in `gradleSource` to understand how it handles the configuration at runtime.

### 3. Reading Implementation Logic

1. Identify the path of a Gradle internal class from `search_dependency_sources`.
2. Call `read_dependency_sources(path="org/gradle/...", gradleSource=true)` to retrieve the implementation.

## Examples

### Search for Kotlin DSL documentation

```json
{
  "query": "tag:dsl kotlin dsl"
}
// Reasoning: Scoping the search to the DSL Reference to find authoritative syntax for Kotlin scripts.
```

### Probe Gradle's Project interface implementation

```json
{
  "query": "Project",
  "searchType": "DECLARATION",
  "gradleSource": true
}
// Reasoning: Using gradleSource and DECLARATION search to find the ground-truth implementation of the core Project API.
```

### Search for a symbol with glob wildcards

```json
{
  "query": "fqn:org.gradle.*.Project",
  "searchType": "DECLARATION",
  "gradleSource": true
}
// Reasoning: Using a glob wildcard to find Project declarations in any direct sub-package of org.gradle.
```

### Search release notes for a specific version

```json
{
  "query": "tag:release-notes",
  "version": "8.6"
}
// Reasoning: Directly targeting the release notes of a specific version to audit breaking changes.
```

### Read a specific Gradle internal class

```json
{
  "path": "org/gradle/api/Project.java",
  "gradleSource": true
}
// Reasoning: Retrieving the source code for a fundamental Gradle class for high-resolution analysis.
```

## Troubleshooting

- **Source Not Found**: Some Gradle internal modules may not be fully indexed. Try a broader full-text search or browse the directory structure.
- **Documentation Mismatch**: Ensure you are targeting the correct version. Use the `version` argument if the project's detected version is not the one you intended to research.
