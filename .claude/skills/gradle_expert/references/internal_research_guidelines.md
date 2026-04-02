# Gradle Internal Research & Documentation Guidelines

A guide for using the specialized Gradle MCP tools to perform authoritative research into the Gradle Build Tool's documentation, internal implementation, and third-party plugin source code.

## 1. Official Documentation Lookup (`gradle_docs`)

ALWAYS prefer the `gradle_docs` tool for searching the official Gradle User Guide, DSL Reference, and Release Notes.

### Searching the User Guide

Tool: `gradle_docs`

```json
{
  "query": "tag:userguide working with files",
  "projectRoot": "/absolute/path/to/project"
}
```

### Navigating the DSL Reference

Tool: `gradle_docs`

```json
{
  "path": "dsl/org.gradle.api.Project.html",
  "projectRoot": "/absolute/path/to/project"
}
```

### Searching for Samples

Official code samples are indexed with the `tag:samples` metadata.
Tool: `gradle_docs`

```json
{
  "query": "tag:samples toolchains",
  "projectRoot": "/absolute/path/to/project"
}
```

### Searching Javadocs

Technical API documentation is indexed with the `tag:javadoc` metadata.
Tool: `gradle_docs`

```json
{
  "query": "tag:javadoc Project",
  "projectRoot": "/absolute/path/to/project"
}
```

## 2. Internal Source Code Research (`search_dependency_sources`)

To understand the "Why" and "How" behind a specific Gradle feature, you MUST research its internal implementation.

### Searching the Gradle Engine

Use `gradleSource = true` and select the appropriate `searchType`.

- Use `DECLARATION` for exact declarations. Do NOT include keywords like `class` or `interface`.
- Use `FULL_TEXT` (default) for patterns, constants, or behavior discovery.
- Use `GLOB` for specific internal file lookup.

Tool: `search_dependency_sources`

```json
{
  "query": "Property",
  "searchType": "DECLARATION",
  "gradleSource": true
}
```

### Exploring Internal APIs

Internal APIs are often located in packages containing `.internal.`. Use `DECLARATION` with a suffix regex for effective discovery.

Tool: `search_dependency_sources`

```json
{
  "query": "org.gradle.api.internal.artifacts.*",
  "searchType": "DECLARATION",
  "gradleSource": true
}
```

## 3. Third-Party Plugin Exploration

When researching how a plugin works, use the authoritative dependency search and source retrieval tools. Plugins are automatically exposed through configurations prefixed with `buildscript:`.

### Identify Plugin Dependencies

Use the `inspect_dependencies` tool to identify the plugin coordinates and versions used in the build script.

Tool: `inspect_dependencies`

```json
{
  "projectPath": ":",
  "configuration": "buildscript:classpath"
}
```

### Search Plugin Sources

Search for specific symbols or patterns within the build script's `classpath` configuration. Note that plugins are NOT on the standard `compileClasspath`.

Tool: `search_dependency_sources`

```json
{
  "query": "class MyPlugin",
  "projectPath": ":",
  "configurationPath": ":buildscript:classpath"
}
```

### Read Plugin Sources

Use `read_dependency_sources` with the paths returned from the search to examine the implementation. You can explicitly target the buildscript classpath:

Tool: `read_dependency_sources`

```json
{
  "projectPath": ":",
  "configurationPath": ":buildscript:classpath",
  "path": "com/example/MyPlugin.kt"
}
```

## 4. Best Practices for Research

- **Use Precise Queries**: Use Lucene syntax for full-text searches to find exact phrases or combine terms.
- **Narrow the Scope**: Use `projectPath` and `configurationPath` to limit searches to relevant modules.
- **Verify Assumptions**: ALWAYS verify your understanding of an API by reading the source code, as documentation can sometimes lag behind implementation.

## Resources

- [Official Gradle Documentation](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle Source Code (GitHub)](https://github.com/gradle/gradle)
