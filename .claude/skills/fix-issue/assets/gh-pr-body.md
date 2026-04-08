# GitHub PR Body Template

The PR body focuses on the **solution** — what was changed and why the approach was
chosen. Problem analysis, root cause investigation, and reproducers belong in the
YouTrack issue, not here. Link to the YT issue so reviewers can dig deeper if needed.

## Template

```markdown
### Subsystem

{{subsystem — e.g., "krpc-client", "compiler-plugin", "grpc-server"}}

### Problem

YouTrack: [{{issue-id}}](https://youtrack.jetbrains.com/issue/{{issue-id}})
{{if-github-issue — GitHub: Fixes #NNN}}

### Solution

{{solution-description — what was changed, which approach was taken, and any
non-obvious design decisions. Keep it concise but specific enough that a reviewer understands the diff}}

---

> [!NOTE]
> Fully autonomous AI-generated PR — no human reviewed the code before submission.
> Problem analysis and root cause details: [{{issue-id}}](https://youtrack.jetbrains.com/issue/{{issue-id}})
```

## Guidelines

- **Labels** — at least one label must be set on the PR (Phase 10 requirement).
