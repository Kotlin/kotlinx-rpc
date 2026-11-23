# KXRPC fields

The `file-youtrack-issue` and `use-youtrack` skills use this file.

## Scope

Required at creation. Use the most specific scopes. An issue can have more than one:
`["Compiler Plugin", "kRPC", "Core"]`.

The annotation and the API give the protocol scope, not the serialization format.
kRPC also supports protobuf serialization, so protobuf doesn't imply gRPC.

| Code uses | Scopes |
|---|---|
| `@Rpc`, `RpcClient`, `callServerStreaming`, kRPC config | kRPC |
| `@Grpc`, gRPC channels, stubs from protoc | gRPC |

| Scope | Use for |
|---|---|
| `Compiler Plugin` | FIR diagnostics, IR code generation, K2 plugin |
| `kRPC` | kRPC in general, when no sub-scope applies |
| `kRPC. Client`, `kRPC. Server` | Behavior of the kRPC client or server |
| `kRPC. Transport` | kRPC wire protocol and transport layer |
| `gRPC` | gRPC in general, when no sub-scope applies |
| `gRPC. Client`, `gRPC. Server` | Behavior of the gRPC client or server |
| `gRPC. Web` | gRPC-Web |
| `Core` | Only the core abstractions: `@Rpc`, `RpcClient`, `RpcServer`, `RpcCall`, descriptors. For protocol behavior, use the protocol scope, also when core APIs are in the call chain. |
| `Protoc Plugin` | Protobuf code generation (`protoc-gen/`) |
| `Gradle Plugin` | `gradle-plugin/` |
| `Infra` | CI, build, publication, project configuration |
| `Housekeeping` | Cleanup, refactoring, maintenance |
| `Documentation` | Docs website, Writerside topics, examples |
| `Other` | All other issues |

## Type

| Type | Use for |
|---|---|
| `Bug` | Wrong behavior |
| `Feature` | New behavior |
| `Task` | Work that isn't a bug or a feature: a migration, a configuration change |
| `Usability Problem` | Behavior that is correct but hard to use |
| `Performance Problem` | Slow code, high memory use |
| `Meta Issue` | A group of related issues |
| `Security Problem` | A vulnerability |

## Priority

Use `Normal` unless there is a clear reason for another priority.

| Priority | Use for |
|---|---|
| `Show-stopper` | Blocks a release or breaks all users |
| `Critical` | Severe bug for many users, no workaround |
| `Major` | Large effect, a workaround exists |
| `Normal` | Default |
| `Minor` | Small effect |

## Tags

| Tag | Use |
|---|---|
| `Vibe-report` | Add to each issue that an AI agent creates. |
| `kRPC 2.0` | Work on the kRPC 2.0 protocol redesign: new protocol, plugin API, auth plugin. Suggest it for such an issue. |
| `Icebox` | Ideas with a low priority. Suggest it when the user doesn't know when to schedule the issue. |
| `Kxrpc Backlog` | Backlog items without a schedule. Suggest it in the same case. |
| `github-issue` | Never add it. The GitHub import adds it. |
| `UFG Claude` | Add it only when the user asks. |

## States

Submitted, Open, In progress, In design, Fixed in branch, Fixed, Wait for reply, Duplicate, Closed.

## Optional fields

Set each of these only when the user gives a value:

- `Assignee`: a user login, for example `alexander.sysoev`.
- `[Kxrpc] Target Release`: a version.
- `[Kxrpc] Planning Period`: a sprint.

To find a login, use `mcp__youtrack__find_user`, or `mcp__youtrack__get_user_group_members` with the group
`kotlinx.rpc Team`.
