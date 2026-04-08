# GitHub CI Report Comment Template

Posted on the PR after all CI pipelines have completed (Phase 11). Shows the final
status of every CI run — both TeamCity builds and GitHub Actions workflows — so that
the reviewer sees the full picture at a glance.

## Template

```markdown
## CI Report

| Pipeline | Status | Details |
|----------|--------|---------|
| {{pipeline-name}} | {{status-emoji}} {{Passed / Failed}} | {{details}} |

{{if any failures not retried — add section below}}

### Failed — not retried

- **{{pipeline-name}}**: {{reason}}
```

## Status values

- Passed: use the checkmark emoji followed by "Passed"
- Failed (retried and now passing): use the checkmark emoji followed by "Passed (retry)"
- Failed (not retried): use the cross mark emoji followed by "Failed"

## Details column

- **Passed**: link to the build/run (TC build URL or GH Actions run URL)
- **Failed**: brief failure description + link to the build log

## "Failed — not retried" section

Only include this section if there are failures the agent chose not to retry.
Each entry needs a clear reason, for example:

- "Flaky test `SomeTest.testFoo` — known intermittent failure unrelated to this PR"
- "Infrastructure timeout — TC agent disconnected, not a code issue"
- "Pre-existing failure on `main` — same test fails without this PR's changes"
- "Unrelated module — failure in `:some-module` not touched by this PR"

The goal is transparency: the reviewer should understand exactly what failed and why
the agent decided it was safe to proceed without fixing it.

## Example

```markdown
## CI Report

| Pipeline | Status | Details |
|----------|--------|---------|
| TC: KRPC All Tests (JVM) | :white_check_mark: Passed | [build #1234](https://tc.example.com/...) |
| TC: KRPC All Tests (JS) | :white_check_mark: Passed (retry) | [build #1236](https://tc.example.com/...) |
| GH: Build & Test | :white_check_mark: Passed | [run #567](https://github.com/...) |
| TC: KRPC Native Tests | :x: Failed | [build #1235](https://tc.example.com/...) |

### Failed — not retried

- **TC: KRPC Native Tests**: Pre-existing failure — `NativeProtobufCodecTest.testLittleEndian` fails on `main` as well ([main build #1200](https://tc.example.com/...)). Not related to this PR.
```

## When to post

Post this comment once CI is fully resolved (all builds finished, retries exhausted).
If CI is re-triggered after a fix-and-push cycle, update the existing comment rather
than posting a new one.
