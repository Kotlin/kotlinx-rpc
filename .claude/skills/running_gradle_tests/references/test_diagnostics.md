# Test Diagnostics Reference

This document provides detailed instructions for diagnosing test failures using the `inspect_build` tool.

## Inspecting Test Results

When a build fails due to test failures, use `inspect_build` with `testOutcome` or `testName` to get a structured view of the results.

### Listing Failed Tests (Summary Mode)

To quickly see which tests failed without being overwhelmed by logs:

```json
{
  "buildId": "BUILD_ID",
  "testOutcome": "FAILED"
}
```

- **Example**: `inspect_build(buildId="ID", testOutcome="FAILED")`

### Getting Detailed Test Output (Details Mode)

To see the full output and stack trace for a specific test. This is **REQUIRED** for authoritative debugging:

```json
{
  "buildId": "BUILD_ID",
  "mode": "details",
  "testName": "com.example.MyTest.testMethod"
}
```

- **Example**: `inspect_build(buildId="ID", mode="details", testName="com.example.MyTest.testMethod")`

#### **CRITICAL: Individual Test Case vs. Task Output**

When a test fails, you must use the `testName` filter with `mode="details"` to see the failure.

**DO NOT** attempt to read the output of the test task (e.g. `:app:test`) using `taskPath` or `captureTaskOutput`.

- **Task output** is the aggregated log of the entire test process. It is often truncated, interleaved, and lacks the full stack traces and per-test isolation needed for debugging.
- **Individual test output** (retrieved via `testName`) is authoritative, includes full stdout/stderr for just that test case, and provides the complete stack trace for any failure.

## Build Failures vs. Test Failures

Sometimes a test run fails not because a test failed, but because the build itself failed (e.g., compilation error, configuration error, or a task dependency failed).

If `inspect_build` shows no failed tests but the build status is `FAILED`, or if you suspect a non-test issue:

1. **Check Build Failures and Problems Summary**:
   ```json
   {
     "buildId": "BUILD_ID"
   }
   ```
   If a specific failure or problem is found, you can inspect it further using `failureId` or `problemId` with `mode="details"`.
2. **Refer to General Build Failure Analysis**: For a deep dive into non-test related failures, use the `running_gradle_builds` skill.

## Common Test Failure Scenarios

### Assertion Failures

Look for `AssertionError` in the test output. The `inspect_build` tool will provide the expected vs actual values if available.

### Timeouts

If a test times out, it may be marked as `ERROR` or `FAILED`. Check the console output for "Timeout" messages.

### Infrastructure Issues

If many tests fail with similar errors (e.g., `NoClassDefFoundError`, `DatabaseConnectionException`), check the `failures` and `problems` sections of the build inspection as shown above.

## Pagination and Filtering

For builds with a large number of tests, use `limit` and `offset`:

```json
{
  "buildId": "BUILD_ID",
  "pagination": {
    "limit": 50,
    "offset": 0
  },
  "testName": "com.example.service"
}
```

## Functional Identity with Foreground Execution

Monitoring background test runs via `inspect_build` provides exactly the same rich diagnostic data as foreground execution. Both methods utilize progressive disclosure to provide concise summaries and structured results, ensuring session
history remains clean. Background execution is simply a non-blocking alternative that allows you to perform other tasks while the test suite proceeds.
