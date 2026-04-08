# GitHub CI Report Comment Template

Posted on the PR after all CI pipelines have completed (Step 11). Shows the final
status of every CI run — both TeamCity builds and GitHub Actions workflows — so that
the reviewer sees the full picture at a glance.

## Template

```markdown
## CI Report

### Passed

| Pipeline | Details |
|----------|---------|
| {{pipeline-name}} | [build #NNN](url) |

### Failed

{{if no failures}}
None
{{else}}
Check a box to request a retry/fix from the agent.

- [ ] **{{pipeline-name}}** — {{brief failure description}} | [build #NNN](url)
  {{reason why agent did not retry — flaky test, pre-existing on main, etc.}}
{{end-if}}
```

## How it works

**Passed table**: Simple table with pipeline name and link. No status column needed —
everything in this table passed.

**Failed list**: Checkbox format — the agent posts all failures as unchecked `[ ]`
with a reason why it didn't retry. The **reviewer** checks a box `[x]` to request
the agent retry or fix that pipeline. When the agent sees checked boxes on a
subsequent pass, it retries/fixes those pipelines and moves them to the Passed table
once green.

## Common "not retrying" reasons

- "Flaky test `SomeTest.testFoo` — known intermittent failure unrelated to this PR"
- "Pre-existing failure on `main` — same test fails without this PR's changes"

The goal is transparency: the reviewer should understand exactly what failed and why
the agent decided it was safe to proceed without fixing it.

## Example

```markdown
## CI Report

### Passed

| Pipeline | Details |
|----------|---------|
| TC: KRPC All Tests (JVM) | [build #1234](https://tc.example.com/...) |
| TC: KRPC All Tests (JS) | [build #1236](https://tc.example.com/...) (retry) |
| GH: Build & Test | [run #567](https://github.com/...) |
| GH: Gradle Wrapper Validation | [run #568](https://github.com/...) |

### Failed

- [ ] **TC: KRPC Native Tests** — `NativeProtobufCodecTest.testLittleEndian` | [build #1237](https://tc.example.com/...)
  Reason: pre-existing failure on `main` ([main build #1200](https://tc.example.com/...)), not related to this PR
```

## When to post

Post this comment once CI is fully resolved (all builds finished, retries exhausted).
If CI is re-triggered after a fix-and-push cycle, update the existing comment rather
than posting a new one. Move pipelines between sections as their status changes.
