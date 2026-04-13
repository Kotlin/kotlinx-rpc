# Bug Fix Verification with External Reproducers

When the issue includes an **external reproducer** (a separate project, repo, or
code sample that triggers the bug), end-to-end verification against that reproducer
is **mandatory**. Unit tests prove the fix is correct in isolation; the reproducer
proves it works in the context where the user actually hit the bug. This is often
the single strongest piece of evidence that a fix is right.

## Before/after verification protocol

1. **Baseline (before fix)**: Before applying your fix, publish the library locally
   from the worktree's current state (`./publishLocal.sh`). Clone or set up the
   reproducer project, point it at the local publication, and **confirm the bug
   exists** — the reproducer must actually fail/crash/misbehave as described in the
   issue. If it doesn't reproduce, investigate why before proceeding (wrong version,
   different environment, already fixed on main, etc.).

2. **Verification (after fix)**: Apply the fix, publish locally again, and re-run
   the reproducer. **Confirm the bug is gone** — the reproducer must now succeed.

3. **Post a verification matrix** as a YT comment on the issue (or append to the
   triage comment). This table is the primary evidence artifact for the fix.

## Verification matrix format

Post as a YT comment:

| Scenario | Old (pre-fix) | New (post-fix) |
|----------|---------------|----------------|
| _reproducer name/link_ | _failure description_ | _success description_ |

Each cell should describe what actually happened — not just pass/fail but the
concrete error vs. concrete success (e.g., "LinkageError at runtime" →
"Compiles and runs cleanly"). Add rows for variants if the reproducer covers
multiple scenarios.

## When to skip

If the issue has no external reproducer and the bug is fully covered by the
tests written in B.1, the matrix is not required. But if a reproducer exists —
even a snippet pasted in a YT comment — use it.
