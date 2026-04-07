---
name: fix-issue
description: >
  Autonomously fix a YouTrack issue end-to-end: from ticket pickup through code fix, tests,
  PR creation, CI validation, and reviewer assignment. Use this skill whenever the user
  provides a KRPC issue ID to fix (e.g., "fix KRPC-540"), asks to pick up an issue, or
  asks to scan for issues to work on. Also trigger when the user says "fix issue",
  "work on ticket", "pick up KRPC-###", "scan for issues", "find issues to fix",
  "auto-fix", or any variation of autonomously resolving a YouTrack ticket.
  Do NOT use this skill for just reading/searching issues (use use-youtrack),
  creating new issues (use file-youtrack-issue), or running builds without
  an issue context (use running_gradle_builds/running_gradle_tests).
---

# Fix YouTrack Issue — Autonomous Workflow

This skill guides you through the complete lifecycle of fixing a KRPC YouTrack issue:
from picking up the ticket, through analysis, coding, testing, PR creation, CI validation,
and reviewer assignment. The goal is full autonomy — execute each phase without waiting
for user approval, but communicate progress at natural milestones.

## Authentication — CRITICAL

This workflow uses **dedicated agent credentials**, not the user's personal tokens.
These overrides take precedence over defaults in `use-youtrack` and `use-teamcity` skills.

- **YouTrack**: Use the **`youtrack-agent`** MCP server (not `youtrack`). For REST
  fallback, use `$YOUTRACK_AGENT_TOKEN` (not `$YOUTRACK_TOKEN`).
- **TeamCity**: **Never** run `teamcity auth status/login`. Prefix every command with
  `TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN`.
- **GitHub**: Standard `gh` CLI auth.

### Pre-flight Access Check — MANDATORY

Before any work, verify **all three services** in parallel. If any fails → **stop and
report to the user**. Do NOT proceed — not even reading the issue.

1. **YouTrack**: `get_current_user` on `youtrack-agent` MCP. If MCP fails, try REST:
   `curl -sf -H "Authorization: Bearer $YOUTRACK_AGENT_TOKEN" "https://youtrack.jetbrains.com/api/users/me?fields=login,name"`
2. **TeamCity**: `TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity project list --limit 1`
3. **GitHub**: `gh auth status`

### No silent fallback policy

If a service becomes unreachable mid-workflow:
- YouTrack MCP → REST via `$YOUTRACK_AGENT_TOKEN` is the only permitted fallback
- TC CLI or GH CLI fails → **stop and report** (no fallback)

Never silently skip a step. Never invent data. Stop, report what failed and at which
phase, wait for instructions.

---

## Input

**Required**: A YouTrack issue ID (e.g., `KRPC-540`).

**Alternative**: User asks to scan for issues. Search `UFG Claude`-tagged issues:
```
project: KRPC tag: {UFG Claude} State: Open sort by: Priority asc, created asc
project: KRPC tag: {UFG Claude} State: Submitted sort by: Priority asc, created asc
```
Prioritize `Open` over `Submitted`, higher priority first. Let the user pick or take
the top one.

## Phase 1: Claim the Issue

Read the issue via `youtrack-agent` MCP. Then:

1. **Assign** to `AI Agent [Kxrpc]` (look up the login once, cache it).
2. **Set state** to `In progress`.
3. **Set sprint** to the current active planning period if none set. The last
   non-checkmarked `[Kxrpc] Planning Period` is typically active. If ambiguous, check
   which period has `In progress` issues.

## Phase 2: Analyze the Problem

Read the issue thoroughly. Understand:
- What exactly is broken or missing?
- Which modules/files are likely involved?
- Is this a bug, feature, or task?
- Are there linked issues or comments with additional context?

### Determine if a reproducer is needed

A reproducer is needed when:
- The issue is a **bug** and the steps to reproduce are unclear or missing
- The issue describes unexpected behavior but doesn't include code that triggers it
- You need to confirm the bug exists before fixing it

A reproducer is NOT needed when:
- The issue is a **feature request** or **task** (nothing to reproduce)
- The issue already includes a clear, verified reproducer
- The fix is obvious from the description (e.g., typo in docs, missing null check
  visible in the code, configuration issue)
- The issue is about build/infra changes

**Post a comment** on the issue explaining your analysis:
- If reproducing: describe how you reproduced the issue and the observed behavior
- If not reproducing: explain why a reproducer isn't needed

## Phase 3: Set Up the Worktree

Work in an isolated worktree. All subsequent operations use the worktree path.

```bash
git fetch origin main
git worktree add -b <branch-name> /tmp/krpc-<issue-id> origin/main
```

Branch naming: `fix/KRPC-<N>` (bugs), `feat/KRPC-<N>` (features), `task/KRPC-<N>` (tasks).

When using Gradle skills, specify the worktree path as `projectRoot`.

## Phase 4: Write a Failing Test (if applicable)

Skip for: docs-only, build/infra, trivial/obvious fixes, or when existing tests
already cover the scenario.

When needed — may be a **new test** or **modification of an existing one**:
1. Identify the correct test module
2. Write/modify a test that **fails** now and **passes** after the fix
3. Run via `running_gradle_tests` to confirm failure
4. Follow existing test patterns in the same module

## Phase 5: Apply the Fix

### Plan before coding (depth scales with complexity)

**Simple** (single file, obvious): Brief inline bullet points.

**Medium** (2-5 files, clear approach): Use the Plan agent for a structured plan.
Start executing immediately — no user approval needed.

**Complex** (architectural, multi-module, public API, unclear tradeoffs):
1. Plan agent → detailed plan (root cause, approach, alternatives, files, risks, verification)
2. Spawn a **review agent** to critique the plan before coding (edge cases, simpler
   alternatives, binary compat risks, architectural patterns)
3. Incorporate feedback, then execute. Still no user approval — the review is for
   correctness, not permission.

### Post the plan to YouTrack

**Small/medium**: Post as a comment (root cause, fix, test, files).

**Large/complex**: Attach full plan as `.md` file via REST (see
`references/youtrack-attachments.md` for the exact curl command),
post a TL;DR comment referencing it.

### Coding guidelines

- Follow project's conventions
- Keep changes minimal and focused — don't refactor surrounding code
- After fixing, run tests from Phase 4 + related module tests

## Phase 6: Check Documentation

Use the `update-doc` skill's conventions. Evaluate:
- Changed public API behavior → update KDoc / Writerside topics
- New public API → add KDoc (required)
- Changed config/setup → update Writerside topics
- Deprecations → update/create migration guide

## Phase 7: Run Local Verifications

Use the `run-local-verifications` skill.

## Phase 8: Code Review

Spawn **at least 2 independent review agents in parallel** on the full diff.
See `references/code-review-agents.md` for the reviewer menu, prompting guide,
and severity handling. Errors must be fixed; warnings/nits are your judgment call.

After creating the PR (Phase 10), post any skipped warnings/nits as a checkbox
comment on the PR. See `references/pr-review-comment-format.md` for the template.
If all issues were fixed, skip this.

## Phase 9: Commit Changes

Separate commits logically:
1. **Fix** — the code change
2. **Tests** — if added/modified
3. **Docs** — if updated
4. **Other** — ABI dumps, generated files, infra

Use the `commit-changes` skill for conventions. Always include the YT ticket ID;
include GH issue number if the YT ticket references one.

## Phase 10: Create a Draft PR

1. Push: `git push -u origin <branch-name>`
2. Check if the YT ticket has a `github-issue` tag → find the GH issue number
3. Create draft PR with `gh pr create --draft`. Include `Fixes #NNN` in the body if
   a linked GH issue exists (auto-closes on merge). Use the repo's PR template format:
   Subsystem, Problem Description (link to YT issue), Solution, agent attribution.
4. Add label. At least one label must be set.

## Phase 11: Run CI Checks (TeamCity + GitHub Actions in parallel)

Both must pass. GH Actions trigger on push; TeamCity you trigger manually.

**TeamCity**: Use `use-teamcity` skill. Pick builds based on what changed (see skill
for build IDs). May skip if trivial and local verifications covered it.

**GH Actions**: Run automatically. Label check activates when draft is removed.

**Monitor both via subagents**: Spawn two background subagents — one polling
GitHub, one polling TeamCity. Each reports back on completion.

On failure: read the report, fix, commit, push (GH Actions restart automatically),
re-trigger TC if needed, spawn monitoring subagents again. Iterate until both pass.
Unrelated failures (flaky tests) → note in PR description and proceed.

## Phase 12: Finalize the PR

Once all CI passes:
1. Rebase on latest main: `git rebase origin/main` + `git push --force-with-lease`
2. Remove draft: `gh pr ready <pr-number>`
3. Add reviewer: `gh pr edit <pr-number> --add-reviewer Mr3zee`

## Phase 13: Update YouTrack

Set state to `Fixed in Branch` via `youtrack-agent`. Optionally comment with PR link.

## Phase 14: Done

Report: PR URL, YT status, brief fix summary, worktree path (if kept).
Do NOT wait for review feedback in the session, feedback would be posted in the PR,
and you will be notified by a user.

## Addressing PR Comments

This may take **multiple turns**. Each turn:

1. Read all PR comments (human reviews + internal checkbox comment)
2. Human reviewer comments take priority. For the checkbox comment, `[x]` = fix, `[ ]` = skip.
3. Fix all: human comments + checked checkboxes
4. Re-run tests/verifications, commit, push, re-trigger CI if needed
5. Update the checkbox comment per `references/pr-review-comment-format.md`

## Rebase Discipline

Keep the branch on latest `main`:
- Phase 3: worktree created from `origin/main`
- Phase 11: before CI push
- Phase 12: before finalizing

Always `git rebase origin/main` + `git push --force-with-lease`.

## Error Recovery

- **Build fails locally**: Read the error, fix, re-run. Don't just retry.
- **TC build fails**: Investigate with `teamcity run log <id> --failed`, fix and push.
- **Rebase conflicts**: Resolve. No user intervention is expected.

Service outages follow the No Silent Fallback policy above.

## Progress Communication

Brief status at each milestone:
- "Claimed KRPC-540, set to In Progress"
- "Reproduced the bug — null pointer when streaming with empty payload"
- "Fix applied, test passing"
- "Code review done — 2 agents, 1 warning fixed"
- "PR #42 created as draft"
- "CI passing, marking PR ready and adding reviewers"
- "Done — PR: <url>, YT status: Fixed in Branch"
