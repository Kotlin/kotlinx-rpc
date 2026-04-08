# YouTrack Issue Body Update Template

When the agent analyzes an issue, it updates the issue body to append its analysis
below the original description. The original content is always preserved verbatim —
the agent adds a separator and its analysis section underneath.

Use the `youtrack-agent` MCP `update_issue` tool to update the `description` field,
or fall back to the REST API.

## Template

```markdown
{{original-issue-body — preserved exactly as-is, no edits}}

---

## AI Analysis

**Type**: {{Bug / Feature / Task}}
**Affected modules**: {{comma-separated list of modules, e.g., `:krpc:krpc-client`, `:core`}}
**Root cause**: {{concise root cause explanation — 1-3 sentences}}

{{if-reproducer-was-created}}
### Reproducer

{{code block or step-by-step reproduction, including the test class/method name if a
test was written as the reproducer}}
{{end-if}}

### Fix summary

{{what was changed and why — mirrors the PR Solution section but can be more technical
since this audience is internal. Include file paths and key decisions.}}

### References

- PR: {{pr-url}}
- Branch: `{{branch-name}}`
{{if-linked-gh-issue}}- GitHub issue: #{{number}}{{end-if}}
```

## Guidelines

- **Never modify the original description.** The separator (`---`) clearly marks where
  the human-written content ends and the AI analysis begins.
- **Root cause** should be specific: name the function, the condition, the missing
  check — not vague statements like "a bug in the logic."
- **Affected modules** use Gradle module paths (`:module:submodule`), not file paths.
- Update the issue body once at the end of the workflow (Phase 13), not incrementally.
  All information should be available by then (root cause, fix, PR link).
- If the issue body is empty or very short, still preserve it and add the separator.
