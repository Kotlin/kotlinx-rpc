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

### Required at creation: `Scope`

Every KRPC issue **must** have at least one Scope. Pick the most specific match(es).

**How to determine the correct protocol scope:**

The key signal is which annotation/API the code uses, **not** the serialization format:
- Code using `@Rpc` annotation, `RpcClient`, `callServerStreaming`, kRPC config → **kRPC** scopes
- Code using `@Grpc` annotation, gRPC channels, protoc-generated stubs → **gRPC** scopes
- Both kRPC and gRPC support multiple serialization formats (JSON, protobuf, CBOR, etc.).
  Do NOT assume gRPC just because protobuf is involved — kRPC supports protobuf serialization too.

| Scope             | When to use                                                                                                                                                                                                                                                                          |
|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Compiler Plugin` | FIR diagnostics, IR codegen, K2 plugin issues                                                                                                                                                                                                                                        |
| `kRPC`            | General kRPC protocol issues (use sub-scopes below when more specific)                                                                                                                                                                                                               |
| `kRPC. Client`    | kRPC client-side behavior                                                                                                                                                                                                                                                            |
| `kRPC. Server`    | kRPC server-side behavior                                                                                                                                                                                                                                                            |
| `kRPC. Transport` | kRPC wire protocol, transport layer                                                                                                                                                                                                                                                  |
| `gRPC`            | General gRPC protocol issues (use sub-scopes below when more specific)                                                                                                                                                                                                               |
| `gRPC. Client`    | gRPC client-side behavior                                                                                                                                                                                                                                                            |
| `gRPC. Server`    | gRPC server-side behavior                                                                                                                                                                                                                                                            |
| `gRPC. Web`       | gRPC-Web support                                                                                                                                                                                                                                                                     |
| `Core`            | **Only** for core API abstractions themselves: `@Rpc`, `RpcClient`, `RpcServer`, `RpcCall`, descriptors. If the bug/feature is about protocol behavior (how calls are serialized, sent, received), use the protocol scope instead, even if core APIs are involved in the call chain. |
| `Protoc Plugin`   | Protobuf code generation (`protoc-gen/`)                                                                                                                                                                                                                                             |
| `Gradle Plugin`   | `org.jetbrains.kotlinx.rpc.plugin` Gradle plugin (`gradle-plugin/`)                                                                                                                                                                                                                  |
| `Infra`           | CI, build system, publishing, project configuration                                                                                                                                                                                                                                  |
| `Housekeeping`    | Cleanup, refactoring, maintenance tasks                                                                                                                                                                                                                                              |
| `Documentation`   | Docs site, Writerside pages, examples                                                                                                                                                                                                                                                |
| `Other`           | Anything that doesn't fit above                                                                                                                                                                                                                                                      |

Multiple scopes are fine when an issue spans areas (e.g., `"Compiler Plugin, kRPC, Core"`).

### Type (pick one)

| Type                    | Use for                                                                |
|-------------------------|------------------------------------------------------------------------|
| `Bug`                   | Something is broken or behaves incorrectly                             |
| `Feature`               | New capability or behavior                                             |
| `Task`                  | Work item that isn't a bug or feature (migration, config change, etc.) |
| `Usability Problem`     | Works but is confusing, awkward, or has poor ergonomics                |
| `Performance Problem`   | Slow, uses too much memory, etc.                                       |
| `Meta Issue`            | Umbrella issue grouping related work                                   |
| `Security Problem`      | Security vulnerability or concern                                      |

### Priority

Default to `Normal` unless there's a clear reason otherwise:

- **Show-stopper**: Blocks a release or breaks all users
- **Critical**: Severe bug affecting many users, no workaround
- **Major**: Significant impact, workaround exists
- **Normal**: Standard priority (default)
- **Minor**: Nice-to-have, low impact

### Tags

After creating an issue, add relevant tags using `mcp__youtrack__manage_issue_tags`.

| Tag             | When to use                                                                                 |
|-----------------|---------------------------------------------------------------------------------------------|
| `Vibe-report`   | **Always add** when an AI agent creates the issue. This is mandatory for AI-created issues. |
| `kRPC 2.0`      | Issues related to the kRPC 2.0 protocol redesign / next-gen features                        |
| `Icebox`        | Low-priority ideas or issues parked for future consideration                                |
| `Kxrpc Backlog` | General backlog items not yet scheduled                                                     |

If the issue is clearly about kRPC 2.0 work (new protocol design, plugin API, auth plugin,
etc.), suggest the `kRPC 2.0` tag. If the user isn't sure when to schedule something, suggest
`Icebox` or `Kxrpc Backlog`.

**Tags to never add manually:**
- `github-issue` — auto-imported from GitHub
- `UFG Claude` — only add if the user explicitly asks for it

### Optional Fields

- **Assignee**: User login (e.g., `"alexander.sysoev"`). Only set if the user specifies who should own it.
- **[Kxrpc] Target Release**: Version string. Only set if the user specifies a release target.
- **[Kxrpc] Planning Period**: Sprint. Only set if the user specifies a sprint.

## Writing the Description

Adapt the description format to the issue type. The team uses Markdown.

**Critical rule: Do NOT include proposed solutions, implementation approaches, or design
suggestions.** The agent's job is to **analyze and report** the problem clearly, not to solve
it. Leave the "how to fix" to the team. Only include an approach if the user explicitly
dictates one.

### Bug Reports

Use this structure:

```markdown
## Problem

Clear description of what's broken and the user-visible impact.

## Steps to Reproduce

1. Version info (Kotlin, Gradle, kotlinx-rpc versions)
2. Configuration or setup steps
3. Code that triggers the bug (use fenced code blocks)

## Expected Behavior

What should happen instead.

## Actual Behavior

What actually happens (error messages, stack traces in code blocks).
```

If the bug originates from a GitHub issue, start the description with:
```
Original GitHub issue: <url>

---
```
Then include the reproduction details.

### Features / Tasks

Keep it concise — state what needs to happen and why. Do not propose how to implement it.

```markdown
Brief description of the work and its motivation.

## Context

Why this matters — what problem it solves or what it enables.
```

### Meta Issues

Just a brief paragraph describing the umbrella goal and motivation. Subtasks will carry the details.

### Usability / Performance Problems

Similar to bugs, but focus on the current behavior and why it's problematic rather than "broken":

```markdown
## Current Behavior

What happens today and why it's a problem.

## Desired Behavior

How it should work instead.

## Context

Who is affected and how frequently.
```

## Creating the Issue

Use `mcp__youtrack__create_issue` with these parameters:

```
project: "KRPC"
summary: "<concise title — imperative or noun phrase>"
description: "<markdown description following patterns above>"
customFields: {
  "Type": "<one of the types above>",
  "Priority": "<priority>",
  "Scope": "<comma-separated scopes>"
}
```

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
