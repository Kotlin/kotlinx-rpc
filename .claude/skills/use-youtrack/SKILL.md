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

Read `references/query-syntax.md` for the full query syntax reference and common search
patterns (sprint work, backlog, recently closed, etc.).

Key point: YouTrack has **no semantic search** — always run multiple queries with different
keywords to get good coverage.

## Reading Issues

Use `mcp__youtrack__get_issue` with the issue ID (e.g., `KRPC-531`). This returns:
- Summary, description, reporter, timestamps
- Custom fields (Type, State, Priority, Scope, Assignee, Target Release, Planning Period)
- Recent comments (configurable count via `recentCommentsCount`)
- Tags, votes, linked issue counts, attachments

For full comment history, use `mcp__youtrack__get_issue_comments`.

## Updating Issues

Read `references/update-operations.md` for the full reference on changing fields, assignees,
tags, comments, links, and logging work.

Key rules:
- `Scope` must be a **JSON array**, even for a single value
- Multiline text fields need **actual newlines**, not literal `\n` escape sequences
- **Always add the `Vibe-report` tag** alongside any other tags the user requests

## KRPC Project Reference

For full field definitions (scopes with determination logic, types, priorities, tags,
optional fields, team members), read
`file-youtrack-issue/references/krpc-fields.md` (relative to `.claude/skills/`).

Quick lists for common lookups:

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

## Knowledge Base Articles

YouTrack also hosts articles (wiki-like docs). Use:
- `mcp__youtrack__search_articles` — find articles by keyword
- `mcp__youtrack__get_article` — read a specific article
- `mcp__youtrack__create_article` / `mcp__youtrack__update_article` — manage articles

## Important Terminology

**kRPC and gRPC are protocols, not transports.** This matters in issue descriptions,
comments, and when choosing scopes. kRPC is the custom RPC protocol; gRPC is HTTP/2 +
Protocol Buffers. Both support multiple serialization formats.
