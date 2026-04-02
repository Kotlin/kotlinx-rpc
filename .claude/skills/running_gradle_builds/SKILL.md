---
name: running_gradle_builds
description: >
  Executes and orchestrates Gradle builds with background management, surgical task output capturing, and structured failure diagnostics;
  ALWAYS use instead of `./gradlew` for core lifecycle tasks (build, assemble), dev servers, and troubleshooting.
  Do NOT use for running tests (use `running_gradle_tests`) or dependency graph auditing.
license: Apache-2.0
metadata:
  author: https://github.com/rnett/gradle-mcp
  version: "3.4"
---

# Authoritative Gradle Build Execution & Orchestration

Executes Gradle builds with managed background orchestration and surgical failure diagnostics.

## Constitution

- **ALWAYS** use the `gradle` tool instead of `./gradlew` via shell.
- **ALWAYS** provide absolute paths for `projectRoot`.
- **NEVER** use `--rerun-tasks` unless investigating project-wide cache-specific corruption; prioritize Gradle's native caching. Prefer `--rerun` for individual tasks to ensure they are executed even if up-to-date.
- **ALWAYS** prefer foreground execution (default) unless the task is persistent (e.g., servers) or extremely long-running (>2 minutes).
- **ALWAYS** use `captureTaskOutput` when you need the isolated output of a specific task (e.g., `help`, `dependencies`).
- **ALWAYS** check the build dashboard (`inspect_build()`) to manage active processes and historical results.
- **STRONGLY PREFERRED**: Use `inspect_build` for all diagnostics. It is more token-efficient than reading raw console logs and provides structured access to failures.
- **NEVER** leave background builds running; use `stopBuildId` to release resources when finished.

## Surgical Inspection with `inspect_build`

The `inspect_build` tool is your primary window into build results. Use it to move from high-level summaries to deep-dive diagnostics.

### 1. Build Dashboard (No Arguments)

Call `inspect_build()` without arguments to see the **Build Dashboard**. This shows active background builds and recently completed builds with their `BuildId`, status, and failure counts.

- **Example**: `inspect_build()`

### 2. Build Summary

Provide a `buildId` to get a summary of that specific build, including failures, problems, and test results. This summary also contains a guide on how to inspect specific details.

- **Example**: `inspect_build(buildId="ID")`

### 3. Detailed Inspection (`mode="details"`)

To get exhaustive information, ALWAYS use `mode="details"` combined with a specific target:

- **Individual Tests**: `testName="FullTestName"`, `mode="details"` (REQUIRED for full output/stack trace).
    - **Prefix Support**: Both `testName` and `taskPath` support **unique prefix matching**. Providing a unique prefix (e.g., `testName="com.example.MyTest"` or `taskPath=":app:compile"`) will automatically select the item if it's
      unambiguous.
- **Task Outputs**: `taskPath=":path:to:task"`, `mode="details"`.
- **Build Failures**: `failureId="ID"`, `mode="details"` (find IDs in the build summary).
- **Problems/Errors**: `problemId="ID"`, `mode="details"` (find IDs in the build summary).
- **Console Logs**: `consoleTail=true` (last N lines) or `consoleTail=false` (first N lines).

### 4. Progress Monitoring

Use `timeout`, `waitFor`, or `waitForTask` to block until a condition is met in a background build.

- **Example**: `inspect_build(buildId="ID", timeout=60, waitFor="Started Application")`
- **Wait for completion**: If `timeout` is set without a wait condition (`waitFor`/`waitForTask`), the tool waits for the build to finish.

### 5. Monitoring Test Progress

While a build is running, the progress notification (and the `inspect_build` summary) provides real-time counts of passed, failed, and skipped tests. This gives immediate feedback on the health of the test suite.

- **Example**: Call `inspect_build(buildId="ID", mode="summary")` repeatedly to see updated test counts: `(5 passed, 1 failed)`.

## Directives

- **ALWAYS use foreground for authoritative builds**: If you intend to wait for a result, ALWAYS use foreground execution. It provides superior progressive disclosure and simpler control flow than starting a background build only to
  immediately call `inspect_build(timeout=...)`.
- **Background ONLY for persistent tasks**: Use `background: true` ONLY for tasks that must remain active (e.g., `bootRun`, `continuous` builds) or when you explicitly intend to perform independent research while the build proceeds.
- **Monitor with `inspect_build`**: Use `inspect_build` to check the status of background builds or to perform deep-dives into any historical build started by the server.
- **Provide absolute `projectRoot`**: Provide `projectRoot` as an **absolute file system path** to all Gradle MCP tools. Relative paths are not supported.
- **Manage resources via dashboard**: Frequently call `inspect_build` without arguments to view the build dashboard and ensure no orphaned background builds are consuming system resources.

## Authoritative Task Path Syntax

Gradle utilizes two primary ways to identify tasks from the command line. Precision here prevents running redundant tasks in multi-project builds.

### 1. Task Selectors (Recursive Execution)

Providing a task name **without a leading colon** (e.g., `test`, `build`) acts as a selector. Gradle will execute that task in **every project** (root and all subprojects) that contains a task with that name.

- **Example**: `gradle(commandLine=["test"])` -> Executes `test` in **all** projects.

### 2. Absolute Task Paths (Targeted Execution)

Providing a task path **with a leading colon** (e.g., `:test`, `:app:test`) targets a **single specific project**.

- **Root Project Only**: Use a single leading colon.
    - **Example**: `gradle(commandLine=[":test"])` -> Executes `test` in the **root project ONLY**.
- **Subproject Only**: Use the subproject name(s) separated by colons.
    - **Example**: `gradle(commandLine=[":app:test"])` -> Executes `test` in the **'app' subproject ONLY**.

## When to Use

- **Core Lifecycle Execution**: When you need to execute standard Gradle tasks like `build`, `assemble`, or `clean` with maximum reliability and clean, parseable output.
- **Persistent Development Processes**: When starting development servers (e.g., `bootRun`) or continuous builds where background management and real-time log monitoring are required.
- **Surgical Build Troubleshooting**: When a build has failed and you need to perform deep-dive analysis of task failures or console logs using the `inspect_build` diagnostic suite. For test failures, ALWAYS use `testName` with
  `mode="details"`.
- **Task-Specific Information Retrieval**: When you need to extract isolated output from a single task (like `help` or `properties`) without the noise of the full build log.

## Workflows

### Running a Foreground Build

1. Identify the task(s) to run (e.g., `["clean", "build"]`).
2. Call `gradle` with the `commandLine`.
3. If the build fails, the tool will return a high-signal failure summary. Use `inspect_build` with the `buildId` for deeper diagnostics.

### Orchestrating Background Jobs

1. Start the build with `background: true` to receive a `BuildId`.
2. Use `inspect_build(buildId=ID, timeout=..., waitFor=...)` to block until a specific state or log pattern is reached.
3. Call `inspect_build()` (no arguments) to manage active jobs in the dashboard.
4. Stop the job using `gradle(stopBuildId=ID)` once its utility is complete.

## Examples

### Run build in all projects

```json
{
  "commandLine": ["build"]
}
// Reasoning: Using a task selector to verify build health across the entire multi-project structure.
```

### Run test only in a specific subproject

```json
{
  "commandLine": [":app:test"]
}
// Reasoning: Using an absolute path to target a specific module, minimizing execution time and context usage.
```

### Inspect Help Output for a Specific Task

```json
{
  "commandLine": [":app:help", "--task", "test"],
  "captureTaskOutput": ":app:help"
}
// Reasoning: Using captureTaskOutput to retrieve clean, isolated documentation for the 'test' task without Gradle's general console noise.
```

### Start a Dev Server and Wait for Readiness

```json
// 1. Start the server in the background
{
  "commandLine": [":app:bootRun"],
  "background": true
}
// Response: { "buildId": "build_123" }

// 2. Wait for the 'Started Application' log pattern
{
  "buildId": "build_123",
  "timeout": 60,
  "waitFor": "Started Application"
}
// Reasoning: Using background orchestration to allow the server to remain active while waiting for a specific readiness signal.
```

## Troubleshooting

- **Build Not Found**: If a `BuildId` is not recognized, it may have expired from the recent history cache. Check the dashboard (`inspect_build()`) for valid active and historical IDs.
- **Task Output Not Captured**: Ensure the path provided to `captureTaskOutput` matches exactly one of the tasks in the `commandLine`.
- **Missing environment variables**: Set `invocationArguments: { envSource: "SHELL" }` if Gradle cannot find expected env vars (e.g., `JAVA_HOME`).

## Resources

- [Background Monitoring](./references/background_monitoring.md)
- [Failure Analysis](./references/failure_analysis.md)
