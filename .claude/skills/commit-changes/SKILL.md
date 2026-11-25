---
name: commit-changes
description: Commit message format of kotlinx-rpc. Use before each `git commit` and for each PR title.
---

# Commit messages

A commit message is one line, and `Co-Authored-By` trailers when the commit has co-authors:

```
KXRPC-<N>: <Summary> (#<GitHub issue>)
```

| Part | Rule |
|---|---|
| `KXRPC-<N>: ` | The YouTrack issue, a colon, and one space. |
| `<Summary>` | English, imperative mood, first word capitalized, no period at the end. The whole line has about 72 characters at most. |
| `(#<GitHub issue>)` | Only when the YouTrack issue links to an original GitHub issue. Never the PR number: the squash-merge adds it. |

- Don't write a body. Context goes into the YouTrack issue or the PR description.
- `Co-Authored-By` trailers are the only lines after the first line. Pass the first line in one `-m` and all trailers in a second `-m`.
- Don't write a module prefix (`grpc:`, `pb:`). Older commits have one. The summary names the changed area.
- The squash-merge makes the PR title the commit subject, so a PR title has the same format.
- Commit regenerated files separately from hand-written code.

| Message | Verdict |
|---|---|
| `KXRPC-586: Release grpc_call deterministically in NativeClientCall` | Correct |
| `KXRPC-245: Strip common enum value prefixes (#12345)` | Correct, with the linked GitHub issue |
| `grpc: Fix retry behavior (KXRPC-586)` | Wrong: module prefix, issue at the end |
| `KXRPC-123: fixed bug.` | Wrong: past tense, lowercase, period |

## No YouTrack issue

| Case | Action |
|---|---|
| The user asks for a commit, and the conversation has no issue | Commit without the `KXRPC-<N>: ` prefix. Don't object. The other rules apply. |
| You or another skill decided to commit | Create an issue with the `file-youtrack-issue` skill first. |
