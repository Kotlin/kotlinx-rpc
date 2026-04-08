# GitHub Internal Code Review Comment Template

Posted on the PR after Phase 10 if there are skipped warnings/nits from automated
code review (Phase 8). If all issues were fixed during review, skip this comment.

## Initial Comment Template

```markdown
## Internal code review

Items flagged during automated code review but not fixed in this PR.
Check the box to request a fix:

**{{reviewer-agent-name}}**
- [ ] `{{file-path}}:{{line}}` — ({{severity}}) {{description}}
- [ ] `{{file-path}}:{{line}}` — ({{severity}}) {{description}}

**{{another-reviewer-agent-name}}**
- [ ] `{{file-path}}:{{line}}` — ({{severity}}) {{description}}
```

## After Fixing Checked Items

Edit the comment via `gh api` to split into two sections — remaining items on top,
fixed items below with strikethrough and commit reference:

```markdown
## Internal code review

Check the box to request a fix:

**{{reviewer-agent-name}}**
- [ ] `{{file-path}}:{{line}}` — ({{severity}}) {{description}}

---

### Fixed
- ~~`{{file-path}}:{{line}}` — ({{severity}}) {{description}}~~ (fixed in {{short-sha}})
- ~~`{{file-path}}:{{line}}` — ({{severity}}) {{description}}~~ (fixed in {{short-sha}})
```

## Rules

- Group items by reviewer agent name.
- Severities: `error`, `warning`, `nit`. Errors should already be fixed — only
  warnings and nits appear here.
- `- [x]` = user wants this fixed. `- [ ]` = skip for now.
- Keep the comment as a living document — update it across review rounds rather
  than posting new comments.
