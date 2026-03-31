# Investigating Build Failures

This guide provides advanced workflows for diagnosing complex Gradle build failures using `inspect_build`.

## Diagnostic Workflow

When a build fails, follow these steps to identify the root cause:

### 1. Get the Build Summary

Start by getting a high-level overview of what went wrong.

```json
{
  "buildId": "BUILD_ID"
}
```

- **Example**: `inspect_build(buildId="ID")`
- This provides a structured summary of failures, problems, and failed tests. Use this to find specific IDs for deeper inspection.

### 2. Inspect a Specific Failure

Use `failureId` with `mode="details"` to see the full failure message and stack trace.

```json
{
  "buildId": "BUILD_ID",
  "mode": "details",
  "failureId": "F0"
}
```

- **Example**: `inspect_build(buildId="ID", mode="details", failureId="F0")`

### 3. Inspect a Specific Problem

Use `problemId` with `mode="details"` for detailed problem reports, including file locations and suggestions.

```json
{
  "buildId": "BUILD_ID",
  "mode": "details",
  "problemId": "P1"
}
```

- **Example**: `inspect_build(buildId="ID", mode="details", problemId="P1")`

### 4. Inspect Failed Tasks

If the failure is task-related, check the output of specific tasks.

```json
{
  "buildId": "BUILD_ID",
  "mode": "details",
  "taskPath": ":app:compileJava"
}
```

- **Example**: `inspect_build(buildId="ID", mode="details", taskPath=":app:compileJava")`

### 5. Deep Dive into Console Logs

If structured reports aren't enough, examine the console output.

```json
{
  "buildId": "BUILD_ID",
  "consoleTail": true,
  "pagination": {
    "limit": 100
  }
}
```

- **Tail (last N lines)**: `inspect_build(buildId="ID", consoleTail=true)`
- **Head (first N lines)**: `inspect_build(buildId="ID", consoleTail=false)`

### 6. Check for Test Failures

If the build failed during testing, use the test-specific inspection.

```json
{
  "buildId": "BUILD_ID",
  "testOutcome": "FAILED"
}
```

- This will list failed tests. For more details on test diagnostics (including how to get full stack traces for individual tests), use the `running_gradle_tests` skill.

## Common Failure Scenarios

### Compilation Errors

- Look for `problems` with `severity: ERROR`.
- Check the `console` output for the specific compiler error message and file location.

### Dependency Resolution Issues

- Check `failures` for messages like "Could not resolve all dependencies".
- Use the `managing_gradle_dependencies` skill to investigate the dependency graph.

### Task Execution Failures

- Check `tasks` to see which task failed.
- Inspect the `console` output for that specific task.

### Build Script Errors

- These usually appear in the `failures` section with a stack trace or a pointer to the line in `build.gradle.kts`.

## Advanced Diagnostics

### Build Scans

If the failure is particularly elusive, re-run the build with `--scan` and use a Develocity MCP server for even deeper analysis.

```json
{
  "commandLine": ["build", "--scan"]
}
```

*(Note: You will be prompted to accept the Terms of Service via an elicitation prompt if needed.)*
