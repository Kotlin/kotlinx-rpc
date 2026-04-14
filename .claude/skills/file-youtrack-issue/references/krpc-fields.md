# KRPC YouTrack Field Reference

Canonical field definitions for the KRPC YouTrack project. Used by both
`file-youtrack-issue` and `use-youtrack` skills.

## Scope (required at creation)

Every KRPC issue **must** have at least one Scope. Pick the most specific match(es).

**How to determine the correct protocol scope:**

The key signal is which annotation/API the code uses, **not** the serialization format:
- Code using `@Rpc` annotation, `RpcClient`, `callServerStreaming`, kRPC config -> **kRPC** scopes
- Code using `@Grpc` annotation, gRPC channels, protoc-generated stubs -> **gRPC** scopes
- Both kRPC and gRPC support multiple serialization formats (JSON, protobuf, CBOR, etc.).
  Do NOT assume gRPC just because protobuf is involved -- kRPC supports protobuf serialization too.

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

Multiple scopes are fine when an issue spans areas (e.g., `["Compiler Plugin", "kRPC", "Core"]`).

## Type (pick one)

| Type                    | Use for                                                                |
|-------------------------|------------------------------------------------------------------------|
| `Bug`                   | Something is broken or behaves incorrectly                             |
| `Feature`               | New capability or behavior                                             |
| `Task`                  | Work item that isn't a bug or feature (migration, config change, etc.) |
| `Usability Problem`     | Works but is confusing, awkward, or has poor ergonomics                |
| `Performance Problem`   | Slow, uses too much memory, etc.                                       |
| `Meta Issue`            | Umbrella issue grouping related work                                   |
| `Security Problem`      | Security vulnerability or concern                                      |

## Priority

Default to `Normal` unless there's a clear reason otherwise:

- **Show-stopper**: Blocks a release or breaks all users
- **Critical**: Severe bug affecting many users, no workaround
- **Major**: Significant impact, workaround exists
- **Normal**: Standard priority (default)
- **Minor**: Nice-to-have, low impact

## Tags

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
- `github-issue` -- auto-imported from GitHub
- `UFG Claude` -- only add if the user explicitly asks for it

## States

Submitted, Open, In progress, In design, Fixed in branch, Fixed,
Wait for reply, Duplicate, Closed

## Optional Fields

- **Assignee**: User login (e.g., `"alexander.sysoev"`). Only set if the user specifies who should own it.
- **[Kxrpc] Target Release**: Version string. Only set if the user specifies a release target.
- **[Kxrpc] Planning Period**: Sprint. Only set if the user specifies a sprint.

## Team Members

Use `mcp__youtrack__find_user` to look up logins, or
`mcp__youtrack__get_user_group_members` with group name `"kotlinx.rpc Team"`.
