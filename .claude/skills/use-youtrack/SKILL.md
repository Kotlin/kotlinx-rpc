---
name: use-youtrack
description: >
  General-purpose YouTrack operations for the kotlinx-rpc project (KRPC). Use this skill
  whenever the user wants to search, read, update, comment on, link, tag, or otherwise
  interact with existing YouTrack issues — anything that isn't creating a brand new issue.
  Also use it when the user asks about issue status, sprint progress, who's working on what,
  backlog queries, or wants to browse/filter issues. Trigger this even if the user doesn't
  say "YouTrack" explicitly — if they mention a KRPC-### issue ID, ask about tickets, want
  to check on an issue, update a status, or log work, this is the right skill.
  Do NOT use this skill for creating new issues — use file-youtrack-issue instead.
---

# YouTrack Operations for kotlinx-rpc (KRPC)

General-purpose skill for interacting with the KRPC YouTrack project.
Project key: **KRPC** | URL: `https://youtrack.jetbrains.com/issue/KRPC-*`

For **creating new issues**, use the `file-youtrack-issue` skill instead.

## Agent workflow override

When this skill is invoked from the `fix-issue` skill (autonomous issue-fixing workflow),
**all YouTrack operations must use the agent identity** instead of the user's:
- MCP server: `youtrack-agent` (not `youtrack`)
- REST API token: `$YOUTRACK_AGENT_TOKEN` (not `$YOUTRACK_TOKEN`)

The `fix-issue` skill's Authentication section has full details. Outside of `fix-issue`,
the defaults below apply.

## Tools Available

Use `mcp__youtrack__*` MCP tools for standard operations. When MCP tools lack a capability
(bulk updates, custom queries, attachment management, etc.), fall back to the YouTrack REST
API via `YOUTRACK_TOKEN`:

```bash
curl -s -H "Authorization: Bearer $YOUTRACK_TOKEN" \
  "https://youtrack.jetbrains.com/api/<endpoint>?fields=<fields>"
```

REST API base: `https://youtrack.jetbrains.com/api`
Docs: [YouTrack REST API](https://www.jetbrains.com/help/youtrack/devportal/api-reference.html)

## Searching Issues

YouTrack has **no semantic search** — it matches exact keywords only. Always run multiple
queries with different keyword combinations to get good coverage.

### Query syntax quick reference

```
project: KRPC                          # All KRPC issues
project: KRPC State: Open             # Open issues
project: KRPC for: me                 # Assigned to current user
project: KRPC for: alexander.sysoev   # Assigned to specific user
project: KRPC reporter: me            # Reported by current user
project: KRPC tag: {kRPC 2.0}         # By tag (braces for multi-word)
project: KRPC Scope: {kRPC. Client}   # By scope (braces for values with dots/spaces)
project: KRPC Type: Bug State: Open   # Combine filters
project: KRPC created: {this week}    # Recent issues
project: KRPC updated: today          # Updated today
project: KRPC sort by: created desc   # Sort order
project: KRPC #resolved              # Resolved issues
project: KRPC #unresolved            # Unresolved issues
```

### Common search patterns

**Current sprint work** (use the latest planning period value):
```
project: KRPC [Kxrpc] Planning Period: {20.03-24.04 (2026)} sort by: updated desc
```
To find the current sprint, look for the most recent planning period that has issues
in progress. You can check with `mcp__youtrack__get_issue_fields_schema` — the last
non-checkmarked period in the enum is typically the active one.

**Backlog for a specific scope:**
```
project: KRPC tag: {Kxrpc Backlog} Scope: {kRPC}
```

**Recently closed:**
```
project: KRPC State: Fixed updated: {this week} sort by: updated desc
```

**Issues by sprint (Planning Period):**
```
project: KRPC [Kxrpc] Planning Period: {20.03-24.04 (2026)}
```

Use `mcp__youtrack__search_issues` for these queries. Request relevant custom fields via
`customFieldsToReturn` — the defaults are `["Type", "State", "Assignee"]` but you can add
`"Priority"`, `"Scope"`, `"[Kxrpc] Target Release"`, `"[Kxrpc] Planning Period"` (sprint) as needed.

## Reading Issues

Use `mcp__youtrack__get_issue` with the issue ID (e.g., `KRPC-531`). This returns:
- Summary, description, reporter, timestamps
- Custom fields (Type, State, Priority, Scope, Assignee, Target Release, Planning Period)
- Recent comments (configurable count via `recentCommentsCount`)
- Tags, votes, linked issue counts, attachments

For full comment history, use `mcp__youtrack__get_issue_comments`.

## Updating Issues

### Change fields
Use `mcp__youtrack__update_issue` to modify summary, description, or custom fields.

**Important:** `Scope` is an array-typed field — always pass it as a JSON array
(e.g., `["Housekeeping", "Infra"]`), never as a comma-separated string.

**Multiline text fields (`description`, `summary`):** The parameter value must contain
actual newlines, not literal `\n` escape sequences. YouTrack stores the string as-is —
literal `\n` will render as visible `\n` characters instead of line breaks.

### Change assignee
Use `mcp__youtrack__change_issue_assignee` with the user login.
Find user logins with `mcp__youtrack__find_user`.

### Manage tags
Use `mcp__youtrack__manage_issue_tags` with operation `"add"` or `"remove"`.

**Always add the `Vibe-report` tag.** When the user asks to tag an issue, always include
`Vibe-report` in addition to whatever tags they specified — never skip it, even if the user
only listed other tags.

### Add comments
Use `mcp__youtrack__add_issue_comment`. Supports Markdown.

### Link issues
Use `mcp__youtrack__link_issues`. Common link types in KRPC:
- `relates to` — general relationship
- `subtask of` / `parent for` — parent-child hierarchy
- `depends on` / `is required for` — dependency chain
- `duplicates` / `is duplicated by` — duplicate tracking

### Log work
Use `mcp__youtrack__log_work` to add time tracking entries.

## KRPC Project Reference

### Scopes
Compiler Plugin, kRPC, kRPC. Client, kRPC. Server, kRPC. Transport,
gRPC, gRPC. Client, gRPC. Server, gRPC. Web, Core, Protoc Plugin,
Gradle Plugin, Infra, Housekeeping, Documentation, Other

### States
Submitted, Open, In progress, In design, Fixed in branch, Fixed,
Wait for reply, Duplicate, Closed

### Types
Bug, Feature, Task, Usability Problem, Performance Problem, Meta Issue,
Security Problem

### Tags (common)
`Vibe-report`, `kRPC 2.0`, `Icebox`, `Kxrpc Backlog`, `github-issue` (auto-imported)

### Team members
Use `mcp__youtrack__find_user` to look up logins, or
`mcp__youtrack__get_user_group_members` with group name `"kotlinx.rpc Team"`.

## Knowledge Base Articles

YouTrack also hosts articles (wiki-like docs). Use:
- `mcp__youtrack__search_articles` — find articles by keyword
- `mcp__youtrack__get_article` — read a specific article
- `mcp__youtrack__create_article` / `mcp__youtrack__update_article` — manage articles

## Important Terminology

**kRPC and gRPC are protocols, not transports.** This matters in issue descriptions,
comments, and when choosing scopes. kRPC is the custom RPC protocol; gRPC is HTTP/2 +
Protocol Buffers. Both support multiple serialization formats.
