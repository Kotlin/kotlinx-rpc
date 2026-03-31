---
name: commit-changes
description: >
  Guides commit message writing for the kotlinx-rpc project. Use this skill whenever
  committing code, writing commit messages, or when the user asks about commit conventions.
  Also use it when reviewing PRs for commit message quality. Triggers on: /commit,
  "commit", "commit message", "git commit", writing changelogs from commits.
---

# kotlinx-rpc Commit Message Style

This project follows specific commit message conventions documented in CONTRIBUTING.md
and reinforced by years of project history. The goal is clear, scannable git history
that tells you what changed and where, at a glance.

## Subject Line Rules

1. **English only**
2. **Imperative mood, present tense** — "Fix" not "Fixed", "Add" not "Added", "Update" not "Updated"
3. **No period** at the end of the subject line
4. **Keep under ~72 characters** (GitHub truncates beyond this)

## Module Prefix

When a change is scoped to a specific module or subsystem, prefix the subject with the
module name and a colon. This is the most distinctive convention in this project — it
makes `git log --oneline` scannable by area.

Use **lowercase** for the prefix. Common prefixes from the project history:

| Prefix | Scope |
|---|---|
| `grpc:` | gRPC protocol modules |
| `pb:` | Protobuf modules |
| `protoc-gen:` | Protobuf code generator |
| `compiler-plugin:` | Kotlin compiler plugin |
| `cinterop:` / `cinterop-c:` | C interop / native bindings |

After the prefix, capitalize the first word:

**Example 1:**
`pb: Add support for extensions`

**Example 2:**
`grpc: Fix retry behavior for client RPC calls`

**Example 3:**
`compiler-plugin: Refactor annotation creation in ProtoDescriptorGenerator`

If the change spans multiple modules or is project-wide, omit the prefix:

**Example 4:**
`Bump version to 0.11.0-SNAPSHOT`

**Example 5:**
`Update to Gradle 9.4`

## YouTrack Issue References

When a commit addresses a YouTrack issue, include the ticket ID (format: `KRPC-NNN`).
Two accepted placements:

- **As a prefix** (before the description): `KRPC-522 Build is broken for AGP projects`
- **In parentheses** (after the description): `pb: Use shared defaults for generated protobuf messages (KRPC-520)`

Both styles are used in the project. When a module prefix is present, the parenthesized
style reads better. When there's no module prefix, leading with the ticket ID is fine.

### GitHub Issue Cross-References

Some YouTrack tickets originate from GitHub issues. When the YT ticket references an
original GitHub issue, include the GH issue number in the commit message too so both
trackers are linked from the git history. Place it alongside the KRPC ticket:

**Example:** `KRPC-522 Fix AGP project builds (#636)`
**Example:** `pb: Fix size discrepancy (KRPC-520, #566)`

Check the YouTrack ticket for a GitHub issue link before committing — if one exists, add it.

## PR Numbers

GitHub automatically appends `(#NNN)` when squash-merging. Do NOT manually add PR
numbers to commit messages — GitHub handles this.

## Commit Body

Most commits in this project have **no body** — a clear subject line is sufficient. Add
a body only when the change genuinely needs explanation beyond what the subject says:

- Multi-part changes that benefit from a bullet list
- Non-obvious motivation or tradeoffs
- Breaking changes

When writing a body, separate it from the subject with a blank line. Bullet lists with
`-` are preferred for multi-item descriptions.

**Example with body:**
```
Updated grpc-kmp-app:
- Added ktor server variant
- Added Android client
- Added way for clients to distinguish between servers (rest button)
```

## Release and Version Commits

These follow a simple pattern:
- `Release X.Y.Z` or `Release X.Y.Z-qualifier (#NNN)`
- `Bump version to X.Y.Z-SNAPSHOT`
- `CHANGELOG.md` (for changelog-only updates)

## Quick Reference

```
<module>: <Imperative verb> <what changed> (<KRPC-NNN>)
│         │                                │
│         │                                └─ Optional: YouTrack ticket
│         └─ Capitalized, imperative mood, no period
└─ Optional: lowercase module prefix with colon
```

Good: `grpc: Add timeout support`
Good: `KRPC-192 grpc-pb: Implement message recursion limit`
Good: `Fix compiler plugin for 2.1.* versions`
Bad:  `fixed bug` (past tense, vague)
Bad:  `grpc: added timeout support.` (past tense, trailing period)
Bad:  `FEAT: Add timeout support for gRPC` (not a project convention — no conventional commits)
