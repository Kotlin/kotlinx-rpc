# Filing Follow-Up Issues During the Workflow

When you discover related bugs, latent risks, or follow-up work during the fix-issue
workflow, file them immediately rather than losing the context. Use the `youtrack-agent`
MCP server (same credentials as the rest of the workflow).

## Rules

Don't use `file-youtrack-issue`, this reference is a drop-in replacement.

### Tags — MANDATORY

After `create_issue`, immediately add the `Vibe-report` tag.
Never add `UFG Claude` or `github-issue` — those are managed by other processes.

### Description — no solutions

Describe the **problem only**. Do not include proposed fixes, implementation approaches,
or design suggestions. The team decides how to fix it.

- What is broken or at risk
- Where in the code (file paths, line numbers)
- How it was discovered (e.g., "found during KRPC-559 code review")
- Link to the parent issue for context

### Required fields

```
project: "KRPC"
customFields: {
  "Type": "...",
  "Priority": "...",
  "Scope": ["<scope>"]   // MUST be a JSON array
}
```

### After creation

1. Add tags (see above)
2. Link to the parent or related issue: `link_issues(targetIssueId, "relates to", parentIssueId)`
3. Report the filed issue ID and URL in the conversation
