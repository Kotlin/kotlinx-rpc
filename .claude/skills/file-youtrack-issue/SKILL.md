---
name: file-youtrack-issue
description: >
  File YouTrack issues for the kotlinx-rpc project (KRPC). Use this skill whenever the user
  wants to create a bug report, feature request, task, or any issue in YouTrack for kotlinx-rpc.
  Also use it when the user mentions filing a ticket, reporting a bug, creating a task,
  logging an issue, or tracking work in YouTrack — even if they don't say "YouTrack" explicitly
  but the context is clearly about issue tracking for this project. Covers choosing the right
  issue type, scope, priority, tags, and writing well-structured descriptions that match the
  team's conventions.
---

# Filing YouTrack Issues for kotlinx-rpc (KRPC)

This skill guides you through creating well-formed issues in the KRPC YouTrack project.
The project key is **KRPC** and issues are tracked at `https://youtrack.jetbrains.com/issue/KRPC-*`.

## Important Terminology

**kRPC and gRPC are protocols, not transports.** This distinction matters throughout the
project. kRPC is the custom RPC protocol; gRPC is the HTTP/2 + Protocol Buffers RPC protocol.
Always refer to them as protocols in issue descriptions and summaries.

## Before You Start

Use the `mcp__youtrack__*` MCP tools for standard operations (create, search, update, tag).
Before creating an issue, always confirm the summary, type, scope, and description with
the user — issues are visible to the team immediately.

### When MCP tools aren't enough

If MCP tools lack a capability you need (e.g., bulk operations, complex queries, attachment
uploads, or reading fields not exposed by the MCP tools), fall back to the YouTrack REST API
directly. The `YOUTRACK_TOKEN` environment variable contains a permanent Bearer token:

```bash
curl -s -H "Authorization: Bearer $YOUTRACK_TOKEN" \
  "https://youtrack.jetbrains.com/api/issues?query=project:KRPC&fields=id,summary"
```

The REST API base URL is `https://youtrack.jetbrains.com/api`. See the
[YouTrack REST API docs](https://www.jetbrains.com/help/youtrack/devportal/api-reference.html)
for available endpoints.

## Required and Recommended Fields

Read `references/krpc-fields.md` for the full field reference (scopes, types, priority, tags,
optional fields). The key points:

- Every issue **must** have at least one **Scope** — determine it from the annotation/API used,
  not the serialization format.
- Default **Priority** to `Normal`.
- **Always add the `Vibe-report` tag** after creation (mandatory for AI-created issues).
- `Scope` must be a **JSON array**, even for a single value.

## Writing the Description

Read `references/description-templates.md` for per-type templates (Bug, Feature/Task,
Meta Issue, Usability/Performance).

The critical rule: **Do NOT include proposed solutions or implementation approaches.**
The agent's job is to analyze and report the problem clearly, not to solve it. Only include
an approach if the user explicitly dictates one.

## Searching Before Filing

YouTrack does **not** have semantic search — it only matches exact keywords. A single broad
query will miss duplicates. Run **at least 3-5 targeted queries** using different keyword
combinations and synonyms:

```
mcp__youtrack__search_issues(query="project: KRPC <exact keyword 1>")
mcp__youtrack__search_issues(query="project: KRPC <exact keyword 2>")
mcp__youtrack__search_issues(query="project: KRPC <synonym or related term>")
mcp__youtrack__search_issues(query="project: KRPC Scope: {<relevant scope>}")
```

For example, for a streaming flow completion bug, search for:
- `project: KRPC callServerStreaming`
- `project: KRPC streaming flow completes`
- `project: KRPC flow hang`
- `project: KRPC Scope: {kRPC. Client}`

Also use scope-filtered searches to browse related issues. If a potential duplicate exists,
mention it to the user and let them decide.

## Creating the Issue

Use `mcp__youtrack__create_issue` with these parameters:

```
project: "KRPC"
summary: "<concise title — imperative or noun phrase>"
description: "<markdown description following templates from references/description-templates.md>"
customFields: {
  "Type": "<one of the types from references/krpc-fields.md>",
  "Priority": "<priority>",
  "Scope": ["<scope1>", "<scope2>"]   # MUST be a JSON array, not a comma-separated string, even if there's only one
}
```

**Multiline text fields (`description`, `summary`):** The parameter value must contain
actual newlines, not literal `\n` escape sequences. YouTrack stores the string as-is —
literal `\n` will render as visible `\n` characters instead of line breaks.

Then immediately add the `Vibe-report` tag (mandatory for AI-created issues) plus any other
relevant tags with `mcp__youtrack__manage_issue_tags`.

### Summary Guidelines

- Keep summaries concise but specific (under ~80 characters)
- For GitHub-sourced issues, prefix with `[GitHub]`
- Use noun phrases or imperative form: "KLIB unique_name collision" or "Add context7.json"
- Include the affected component when it aids clarity

### After Creation

After creating the issue, report back:
1. The issue ID and URL (e.g., `KRPC-540 https://youtrack.jetbrains.com/issue/KRPC-540`)
2. A brief summary of what was filed
3. Which tags were added

If the user wants to link it to a parent issue, use the `parentIssue` parameter. If they want
to add comments or other modifications after creation, use `mcp__youtrack__update_issue`.

## Quick Reference: Common Workflows

**User reports a bug while coding:**
1. Ask what happened (or infer from context)
2. Identify scope from the annotations/APIs used (not serialization format)
3. Search for duplicates with multiple keyword queries
4. Draft the issue and confirm with user
5. Create it and add `Vibe-report` tag + any other relevant tags

**User wants to track a task:**
1. Clarify what needs to be done
2. Pick Type=Task and appropriate scope
3. Create with brief description (no approach/solution)
4. Add `Vibe-report` tag + any other relevant tags

**User mentions a GitHub issue to track:**
1. Get the GitHub URL
2. Create with `[GitHub]` prefix and link in description
3. Type is usually Bug unless stated otherwise
4. Do NOT add `github-issue` tag — it's auto-imported
5. Add `Vibe-report` tag
