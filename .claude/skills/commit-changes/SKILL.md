---
name: commit-changes
description: >
  Guides commit message writing for the kotlinx-rpc project. Use this skill whenever
  committing code, writing commit messages, or when the user asks about commit conventions.
  Also use it when reviewing PRs for commit message quality. Triggers on: /commit,
  "commit", "commit message", "git commit", writing changelogs from commits.
---

# kotlinx-rpc Commit Message Style

Every commit message is a **single line** that starts with a YouTrack issue ID.
No module prefixes, no parenthesized ticket references, no bodies.

The format is enforced for bot-authored PRs by
`.github/workflows/bot-pr-title.yml`, which rejects any PR title that does not
match `^KRPC-[0-9]+: .+`. Squash-merge makes the PR title the commit subject,
so the same pattern applies to commits.

## Format

```
KRPC-<number>: <Imperative verb> <what changed> (#GH-issue if exists)
```

**Example 1:**
`KRPC-586: Release grpc_call deterministically in NativeClientCall`

**Example 2:**
`KRPC-572: Enable local Gradle build cache alongside remote Develocity cache`

**Example 3:**
`KRPC-252: Support type resolution in comments for protoc-gen`

**Example 4 (with GH-issue cross-reference):**
`KRPC-245: Strip common enum value prefixes (#12345)`

## Rules

1. **Start with the YouTrack ID** as `KRPC-NNN:` — colon, then a single space.
2. **English only.**
3. **Imperative mood, present tense** — "Fix" not "Fixed", "Add" not "Added".
4. **Capitalize the first word** after the colon.
5. **No trailing period.**
6. **Keep under ~72 characters** so it stays readable in `git log --oneline`
   and on GitHub.
7. **No body, ever.** Everything a reviewer needs goes on the subject line.
   Longer context belongs in the YouTrack ticket or the PR description —
   never in the commit body. When invoking `git commit`, pass a single `-m`
   with one line; do not use `-m` twice and do not use a HEREDOC that produces
   multiple paragraphs.

## GitHub issue cross-references

Some YouTrack tickets originate from a GitHub issue. When the YT ticket
links to an original GH issue, append that issue number in parentheses at
the end of the subject so both trackers are reachable from the git history.
Check the YouTrack ticket for a linked GH issue before committing; if one
exists, add it. This is **not** the PR number — squash-merge appends that
separately, and a linked GH issue and the merge PR are different numbers.

**Example:** `KRPC-245: Strip common enum value prefixes (#12345)`

If there is no linked GH issue, omit the suffix entirely.

## No module prefixes

Older commits used `grpc:`, `pb:`, `compiler-plugin:` prefixes, sometimes with
the ticket tacked on in parentheses at the end. Do not copy that style. The
ticket ID comes first; the subject text itself already names the area being
changed (e.g. "Release grpc_call deterministically in NativeClientCall" makes
it obvious this touches gRPC native-client code).

## No YouTrack ticket yet?

If a change needs committing but no YouTrack issue exists, create one first.
The workflow check has no fallback — there is no prefix-less style for
committed work, and release/version-bump commits are not an exception. Use the
`file-youtrack-issue` skill to create a ticket, then commit against it.

## PR numbers

GitHub automatically appends `(#NNN)` when squash-merging. Do not add PR
numbers to commit subjects by hand.

## Generated files

Never hand-edit generated files. Always run the appropriate regeneration task
and commit the output. Keep generated-file changes in a **separate commit**
from hand-authored code so reviewers can distinguish mechanical output from
real edits. Both commits still follow the `KRPC-NNN:` subject rule.

## Quick reference

```
KRPC-<number>: <Imperative verb> <what changed> (#GH-issue if exists)
│              │                                │
│              │                                └─ Optional: linked GH issue from the YT ticket
│              └─ Capitalized, imperative mood, no period, single line
└─ Required YouTrack ticket, followed by ": "
```

Good: `KRPC-583: Reduce configuration phase overhead from settings plugins`
Good: `KRPC-574: Make BufGenerateTask cacheable`
Good: `KRPC-576: Fix native test process crash on linuxX64 in negative test paths`

Bad:  `grpc: Fix retry behavior (KRPC-586)` — module prefix, ticket at the end
Bad:  `KRPC-123: fixed bug` — past tense, lowercase first word
Bad:  `KRPC-123: Add timeout support.` — trailing period
Bad:  Any multi-line message — bodies are not used on this project
