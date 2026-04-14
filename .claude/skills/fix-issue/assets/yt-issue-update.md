# YouTrack Issue Body Update Template

When the agent works on an issue, it updates the issue body to append its analysis
below the original description. The update happens in two phases:
1. **Step 2 (Analyze)** — initial analysis
2. **Step 13 (Update YouTrack)** — only if new findings emerged during the fix

The original content is always preserved verbatim — the agent adds a separator and
its analysis section underneath.

## Template (Step 2)

Follow the `<details>` template format exactly — do not substitute it with plain
`##` headings or other layouts, no exceptions.

```markdown
{{original-issue-body — preserved exactly as-is, no edits}}

---

<details>
<summary><b>Agent Analysis</b></summary>

### Affected modules
{{comma-separated list of modules, e.g., `:krpc:krpc-client`, `:core`}}

### Root cause
{{root cause explanation - keep it to the point, assume the reader knows the project context}}

</details>
```

## Step 13 updates (only if new findings)

If during the fix you discovered things not known at Step 2 — append them below the
existing Agent Analysis section. Examples:
- Root cause was deeper than initially assessed
- Additional modules turned out to be affected
- Edge cases found during testing
- Related issues discovered

## Formatting — CRITICAL

When passing the `description` parameter to the YouTrack MCP tool, the value must
contain **actual newlines** — not literal `\n` escape sequences. If you write `\n`
in the parameter string, YouTrack will store and render it as the literal characters
`\n`, producing a wall of unformatted text.

**Wrong** (literal escape sequences — renders as one line):
```
"First line\n\nSecond line\n\n## Heading"
```

**Correct** (actual newlines in the parameter value):
```
"First line

Second line

## Heading"
```

## Guidelines

- **Never modify the original description.** The separator (`---`) clearly marks where
  the human-written content ends and the Agent analysis begins.
- If the issue body is empty or very short, still preserve it and add the separator.
- Do not add fix summary or solution — those belong in the PR body.
