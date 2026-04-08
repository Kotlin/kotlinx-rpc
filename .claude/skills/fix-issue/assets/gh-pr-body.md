# GitHub PR Body Template

The PR body focuses on the **solution** — what was changed and why the approach was
chosen. Problem analysis, root cause investigation, and reproducers belong in the
YouTrack issue, not here. Link to the YT issue so reviewers can dig deeper if needed.

## Template

```markdown
### Subsystem

{{subsystem — e.g., "krpc-client", "compiler-plugin", "grpc-server"}}

### Problem

[{{issue-id}}](https://youtrack.jetbrains.com/issue/{{issue-id}}) {{if-github-issue — Fixes #NNN}}

### Solution

{{solution-description — what was changed, which approach was taken, and any
non-obvious design decisions. Keep it concise but specific enough that a reviewer
understands the diff without reading the YT issue. 3-8 sentences is typical.}}

---

> [!NOTE]
> Fully autonomous AI-generated PR — no human reviewed the code before submission.
> Problem analysis and root cause details: [{{issue-id}}](https://youtrack.jetbrains.com/issue/{{issue-id}})
```

## Guidelines

- **No root cause analysis** in the PR. A reviewer reading the diff should understand
  *what* changed; if they need to understand *why* the bug happened, they follow the
  YT link.
- **`Fixes #NNN`** goes on a separate line inside the Problem section (not the title).
  GitHub auto-closes the issue on merge.
- **Subsystem** matches the primary module affected. If multiple, list the main one
  and mention others in the Solution.
- **Labels** — at least one label must be set on the PR (Phase 10 requirement).
