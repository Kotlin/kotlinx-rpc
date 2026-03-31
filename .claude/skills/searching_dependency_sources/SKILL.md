---
name: searching_dependency_sources
description: >-
  Explores and searches source code of all external library dependencies, plugins, and Gradle internals
  via indexed symbol, full-text, and glob search; STRONGLY PREFERRED for understanding APIs, finding class/method definitions, and reading implementation logic.
  Do NOT use for project source code (use grep), Gradle documentation (use `researching_gradle_internals`), or Maven Central discovery (use `managing_gradle_dependencies`).
license: Apache-2.0
metadata:
  author: https://github.com/rnett/gradle-mcp
  version: "1.4"
---

# Authoritative Dependency Source & API Exploration

Explores, navigates, and analyzes the internal logic, APIs, and symbol implementations of external libraries and plugins with absolute precision using high-performance, indexed searching.

## Constitution

- **ALWAYS** use `search_dependency_sources` as the primary discovery tool for external library and plugin code.
- **ALWAYS** prefer reading source code over interactive REPL exploration for understanding unfamiliar library APIs.
- **ALWAYS** provide absolute paths for `projectRoot`.
- **ALWAYS** use the `dependency` parameter to target a single library (e.g., `dependency="org.mongodb:mongodb-driver-sync"`) when the target is known. This is significantly faster and more focused than searching the entire project graph.
  Note that when `dependency` is used in `search_dependency_sources` or `read_dependency_sources`, the `path` and all results are relative to the library root (omitting the `<group>/<artifact>...` prefix).
- **NEVER** use `gradleSource: true` in this skill; use `researching_gradle_internals` for Gradle's internal implementation.
- **NEVER** use generic shell tools like `grep` or `find` to *locate* dependency sources; they reside in remote caches whose paths are not predictable in advance.
- **MAY** use shell tools like `rg` or `ast-grep` to *operate on* a sources root path explicitly returned by `read_dependency_sources` or `search_dependency_sources` in the `Sources root: <path>` header line. Dependency directories inside
  the sources root are symlinks; always pass `--follow` to `rg` (e.g., `rg --follow <pattern> <sources-root>`).
- **ALWAYS** escape Lucene special characters (`:`, `=`, `+`, `-`, `*`, `/`) in `FULL_TEXT` searches using a backslash (e.g., `\:`) or double quotes.
- **ALWAYS** use `read_dependency_sources` once a specific file path has been identified via search.
- **ALWAYS** use `fresh: true` if a search returns a `SearchResponse` with an `error` indicating a missing index; the tool will return an error message rather than throwing an exception if the index is not found.
- **BE AWARE** that indexing and extraction failures (e.g., `ZipException`) are still propagated and will cause the tool to fail with a descriptive error.
- **NOTE** that the `dependency` filter targets ONLY the specific library version matched, NOT its transitive dependencies.

## Directives

- **Identify Search Mode Authoritatively**:
    - **DECLARATION**: Best for classes, methods, or interfaces. Matches against the simple name (tokenized for CamelCase) and the full path (exact literal). All declaration searches are **case-sensitive**. Do NOT include keywords like
      `class`, `interface`, or `fun`. Supports exact names, exact FQNs, glob wildcards (e.g., `*`, `**`), and regular expressions. Partial package paths require wildcards (e.g., use `*.MyClass` to find `com.example.MyClass`). You can target
      specific fields using `name:` (discovery) or `fqn:` (precision) prefixes.
    - **FULL_TEXT**: Best for literal strings, constants, and complex code patterns using Lucene. **Case-insensitive**.
    - **GLOB**: Best for finding specific files (XML, properties, etc.) by name or extension. **Case-insensitive**.
- **Invoke Precisely**: ALWAYS set `searchType` explicitly if the intent is not a general full-text search. This improves result accuracy and reduces noise.
- **Scope Surgically**: Use `projectPath`, `configurationPath`, or `sourceSetPath` to narrow the search and improve performance if the target library's context is known. To search a plugin, use `configurationPath=":buildscript:classpath"`.
- **Target Libraries Directly**: Use the `dependency` parameter to search or read from a single library. It supports `group:name:version:variant`, `group:name:version`, `group:name`, or just `group`. This bypasses project-level index
  merging and provides instantaneous results from the global extracted source cache. Note that results will be relative to the targeted library root.
- **Troubleshoot Targeted Searches**: If a targeted search using the `dependency` parameter fails or returns no matches, use `inspect_dependencies` first to verify the exact coordinates (group, name, version, variant) of the dependency as
  resolved by Gradle.
- **Refresh Indices**: Use `fresh: true` if project dependencies have recently changed to ensure the index is up-to-date.
- **Use Returned Sources Root**: Every response from `read_dependency_sources` and `search_dependency_sources` includes a `Sources root: <absolute-path>` header. Use this path with `rg`, `ast-grep`, or other shell tools for operations not
  covered by the MCP tools (e.g., regex-heavy searches). Because dependency directories are symlinks, always pass `--follow` to `rg`: `rg --follow <pattern> <sources-root>`.
- **Explore Packages Authoritatively**: Use `read_dependency_sources` with a dot-separated package path (e.g., `org.gradle.api`) to list its direct symbols and sub-packages. This is backed by the symbol index and is more reliable than
  directory-based exploration for Kotlin projects.
- **Analyze Implementation**: Use `read_dependency_sources` to retrieve the implementation logic. If the file is large, use `pagination` to read specific sections. You can target plugins by passing
  `configurationPath=":buildscript:classpath"`.
- **Trace Symbols Authoritatively**: When encountering an unknown symbol, use `DECLARATION` search to jump directly to its definition in the library. This is the only reliable way to understand exact behavior and available methods.

## When to Use

- **API & Symbol Discovery**: When you need to find the implementation, signature, or documentation of a class, interface, or method imported from a library.
- **Library Usage Research**: When understanding how to use a library's API by reading its internal implementation or looking for usage patterns in its source.
- **Internal Logic Auditing**: When researching how a dependency handles specific operations, edge cases, or performance-critical logic.
- **Resource File Location**: When searching for configuration files (XML, JSON, properties), specific named files (e.g., `build.gradle`), or metadata (AndroidManifest.xml) packaged within library jars.
- **Constant & Literal Research**: When searching for specific constant values, error strings, or literal keys within external code.

## Workflows

### 1. Tracing a Symbol from Project Code

1. Identify the symbol name (e.g., `JsonConfiguration`) or fully qualified name from an import.
2. Call `search_dependency_sources(query="<SymbolName>", searchType="DECLARATION")`.
3. Identify the correct file path from the results.
4. Call `read_dependency_sources(path="<path>")` to analyze the implementation.

### 2. Discovering API Usage through Source

1. Search for a known entry point (e.g., a constructor or main class) using `DECLARATION` or `FULL_TEXT`.
2. Once the file is found, use `read_dependency_sources` to read its source.
3. Look for internal calls, helper methods, or factory patterns to understand the library's preferred usage.

### 3. Searching for Constants or Error Codes

1. Identify the constant name or a snippet of an error message.
2. Call `search_dependency_sources(query="\"<text>\"")` (defaults to `FULL_TEXT`).
3. Review matches to find where the value is defined or used.

### Targeted Search for a Single Library

1. Identify the dependency coordinates (e.g., from `inspect_dependencies` or project files).
2. Call `search_dependency_sources(query="<query>", dependency="<group:artifact>")`.
3. The results will be scoped ONLY to that library, ensuring maximum speed and relevance.

**Path Relativity and Targeted Searching**

When using the `dependency` parameter, all file paths and search results are relative to the library's root, omitting the `<group>/<artifact>...` prefix.

* **Merged Scope (No Filter)**: `path = "org/mongodb/mongodb-driver-sync/4.11.1-sources/org/mongodb/client/MongoClient.kt"`
* **Targeted Scope (`dependency="org.mongodb:mongodb-driver-sync"`)**: `path = "org/mongodb/client/MongoClient.kt"`

This approach is significantly faster and simplifies path handling when you are focused on a specific library.

## Examples

### Search for a specific class definition within a targeted library

```json
{
  "query": "MongoClient",
  "searchType": "DECLARATION",
  "dependency": "org.mongodb:mongodb-driver-sync"
}
// Reasoning: Using the 'dependency' parameter to target only the 'mongodb-driver-sync' library for a fast, focused search.
```

### Read sources from a specific dependency

```json
{
  "dependency": "org.jetbrains.kotlinx:kotlinx-coroutines-core",
  "path": "kotlinx/coroutines/Job.kt"
}
// Reasoning: Reading 'Job.kt' directly from the targeted 'kotlinx-coroutines-core' library. Note the 'group/artifact...' prefix is omitted.
```

### Search for a specific class definition

```json
{
  "query": "JsonConfiguration",
  "searchType": "DECLARATION"
}
// Reasoning: Using DECLARATION search to find a class named 'JsonConfiguration' across both name and FQN fields.
```

### Search with field-specific precision

```json
{
  "query": "fqn:kotlinx.serialization.json.*",
  "searchType": "DECLARATION"
}
// Reasoning: Using the 'fqn:' prefix with a wildcard to find all declarations within a specific package literal.
```

### Search with name-specific discovery (CamelCase)

```json
{
  "query": "name:Configuration",
  "searchType": "DECLARATION"
}
// Reasoning: Using the 'name:' prefix to find classes like 'JsonConfiguration' via CamelCase tokenization.
```

### Trace a method signature using wildcards

```json
{
  "query": "encodeTo*",
  "searchType": "DECLARATION"
}
// Reasoning: Finding all definitions starting with 'encodeTo' across both name and FQN fields.
```

### Use regular expressions for complex matching

```json
{
  "query": "fqn:/.*\\.internal\\..*/",
  "searchType": "DECLARATION"
}
// Reasoning: Using a regular expression on the 'fqn' field to find all internal declarations.
```

### Search for a constant value assignment

```json
{
  "query": "DEFAULT_TIMEOUT_MS \\: 5000"
}
// Reasoning: Using FULL_TEXT (default) with escaped colon to find a specific constant assignment.
```

### Locate a specific file by its exact name

```json
{
  "query": "**/AndroidManifest.xml",
  "searchType": "GLOB"
}
// Reasoning: Using GLOB search to find a specific file by name across the dependency graph.
```

### Read a specific dependency source file

```json
{
  "path": "kotlinx/serialization/json/Json.kt"
}
// Reasoning: Reading the implementation of a known class path identified from previous search results.
```

### Explore a package via its FQN

```json
{
  "path": "org.gradle.api"
}
// Reasoning: Listing the direct symbols and sub-packages of 'org.gradle.api' using index-backed exploration.
```

## Resources

- **Lucene Query Syntax**: Refer to the tool description for `search_dependency_sources` for details on complex queries and escaping.
- **Troubleshooting Targeted Searches**: If `dependency` filter fails, run `inspect_dependencies` to confirm the exact coordinates.
