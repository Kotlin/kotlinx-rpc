---
name: introspecting_gradle_projects
description: >
  Uncovers Gradle project structure, task hierarchies, and resolved property values using core Gradle diagnostic tools;
  use for mapping modules, listing runnable tasks, and auditing build configuration.
  Do NOT use for running builds/tests or source code exploration.
license: Apache-2.0
metadata:
  author: https://github.com/rnett/gradle-mcp
  version: "2.1"
---

# Deep Project Structure & Environment Introspection

Uncovers project modules, discovers runnable tasks, and gain total visibility into your build configuration using core Gradle diagnostic tools and authoritative dependency auditing.

## Constitution

- **ALWAYS** use the `gradle` tool for running introspection tasks instead of raw shell commands.
- **ALWAYS** provide absolute paths for `projectRoot`.
- **ALWAYS** use `captureTaskOutput` when running introspection tasks (e.g., `:projects`, `:tasks`) to filter out startup noise.
- **ALWAYS** use `inspect_dependencies` for auditing dependency graphs; use `gradle` for task-specific dependency insights.
- **NEVER** guess task names or options; use the `help --task <name>` command for authoritative documentation.
- **ALWAYS** use `:properties --property <name>` for surgical property extraction.

## Directives

- **Map structure first**: ALWAYS use the `projects` task to visualize the multi-project hierarchy and identify authoritative project paths.
- **Discover tasks surgically**: ALWAYS use the `tasks` task with `--all` for a comprehensive list, or scope to a specific project.
- **Query task metadata**: ALWAYS use `help --task <name>` to retrieve descriptions, types, and available command-line options for any task.
- **Extract properties precisely**: ALWAYS use the `properties` task with the `--property` flag to isolate a single value and avoid massive console output.
- **Audit dependencies**: Use `inspect_dependencies` for a searchable tree and update check. For low-level variant or transformation analysis, use the built-in diagnostic tasks.
- **Refer to diagnostic guides**: For a complete list of introspection commands, see the [Diagnostic Tasks](./references/diagnostic_tasks.md) reference.

## Authoritative Task Path Syntax

Precision in path syntax is essential for mapping multi-module builds correctly.

### 1. Task Selectors (Recursive)

Providing `tasks` **without a leading colon** lists tasks for **every** project in the build. This is usually very noisy.

### 2. Absolute Task Paths (Targeted)

To inspect a **single specific project**, always use a leading colon.

- **Root Project**: `gradle(commandLine=[":tasks"], captureTaskOutput=":tasks")`
- **Subproject**: `gradle(commandLine=[":app:tasks"], captureTaskOutput=":app:tasks")`

## When to Use

- **Multi-Module Mapping**: When you need to visualize the full project hierarchy and identify correct project paths.
- **Task & Option Discovery**: When you need to find runnable tasks or get authoritative help on a task's configuration.
- **Environment & Runtime Auditing**: When you need to verify Gradle versions, JVM toolchains, or system properties.
- **Surgical Property Inspection**: When you need to extract a specific property value (like an artifact version or build directory) for use in a subsequent task.

## Examples

### List all sub-projects in the build

```json
{
  "commandLine": [":projects"],
  "captureTaskOutput": ":projects"
}
// Reasoning: Using captureTaskOutput to retrieve only the project hierarchy list.
```

### Get authoritative help for a specific task

```json
{
  "commandLine": [":app:help", "--task", "assemble"],
  "captureTaskOutput": ":app:help"
}
// Reasoning: Using the built-in help task to discover available options for 'assemble'.
```

### Surgically inspect the 'version' property

```json
{
  "commandLine": [":properties", "--property", "version"],
  "captureTaskOutput": ":properties"
}
// Reasoning: Using --property to avoid retrieving thousands of unrelated project properties.
```

### Analyze a specific dependency conflict

```json
{
  "commandLine": [
    ":app:dependencyInsight",
    "--dependency",
    "com.google.guava:guava",
    "--configuration",
    "runtimeClasspath"
  ],
  "captureTaskOutput": ":app:dependencyInsight"
}
// Reasoning: Using dependencyInsight to isolate the resolution path for a specific artifact.
```

## Troubleshooting

- **Missing environment variables**: Set `invocationArguments: { envSource: "SHELL" }` if Gradle cannot find expected env vars (e.g., `JAVA_HOME`).

## Resources

- [Diagnostic Tasks](./references/diagnostic_tasks.md)
