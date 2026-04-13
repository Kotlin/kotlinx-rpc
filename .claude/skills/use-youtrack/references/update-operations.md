# YouTrack Update Operations for KRPC

## Change fields

Use `mcp__youtrack__update_issue` to modify summary, description, or custom fields.

**Important:** `Scope` is an array-typed field — always pass it as a JSON array
(e.g., `["Housekeeping", "Infra"]`), never as a comma-separated string.

**Multiline text fields (`description`, `summary`):** The parameter value must contain
actual newlines, not literal `\n` escape sequences. YouTrack stores the string as-is —
literal `\n` will render as visible `\n` characters instead of line breaks.

## Change assignee

Use `mcp__youtrack__change_issue_assignee` with the user login.
Find user logins with `mcp__youtrack__find_user`.

## Manage tags

Use `mcp__youtrack__manage_issue_tags` with operation `"add"` or `"remove"`.

**Always add the `Vibe-report` tag.** When the user asks to tag an issue, always include
`Vibe-report` in addition to whatever tags they specified — never skip it, even if the user
only listed other tags.

## Add comments

Use `mcp__youtrack__add_issue_comment`. Supports Markdown.

## Link issues

Use `mcp__youtrack__link_issues`. Common link types in KRPC:
- `relates to` — general relationship
- `subtask of` / `parent for` — parent-child hierarchy
- `depends on` / `is required for` — dependency chain
- `duplicates` / `is duplicated by` — duplicate tracking

## Log work

Use `mcp__youtrack__log_work` to add time tracking entries.
