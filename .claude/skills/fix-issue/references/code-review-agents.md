# Code Review Agents

Reference for Phase 8 of the fix-issue workflow. Pick at least 2 agents based on what
changed — this is a menu, not a checklist.

## Available reviewers

| Agent | Good for | Focus |
|---|---|---|
| **Kotlin style** | Any Kotlin change | Naming, types, visibility, `@InternalRpcApi`, KDoc, complexity |
| **Concurrency** | Coroutines, flows, shared state | Scope usage, cancellation, `Flow` patterns, dispatchers, blocking calls |
| **Public API** | Published module API changes | Binary compat, `@Deprecated`, `@ExperimentalRpcApi`, internal type exposure |
| **Gradle expert** | Build files, module config | Kotlin DSL, version catalog, conventions, `includePublic()` vs `include()` |
| **Serialization** | Wire format, protobuf | Backward compat, `@Serializable`, schema evolution, field numbering |
| **Security** | Auth, transport, input | Injection, validation, secure defaults, credential leakage |

Spawn unlisted reviewers too if warranted (docs, performance, etc.).

## How to prompt each agent

Spawn each reviewer as a `superpowers:code-reviewer` agent type. Collect the diff
(`git diff HEAD` in the worktree, or `git diff` + `git diff --staged` if some changes
are staged), then spawn each agent with:

> You are reviewing a code change for the kotlinx-rpc library. Focus ONLY on
> **[your focus area]**. Review the diff below.
> If you find no issues in your area, say so explicitly.
>
> \<the diff\>

## Severity handling

- **Error** → must fix before committing.
- **Warning** → fix unless you have a good reason not to. Note why if skipping.
- **Nit** → fix if trivial, skip if it would bloat the diff.

If two reviewers flag the same area with conflicting advice, use your judgment and
follow repo conventions (CLAUDE.md) as the tiebreaker.

After applying fixes, re-run relevant tests to make sure nothing broke.
