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
Prioritize `Open` over `Submitted`, higher priority first. Take the top one.

## Step 1: Claim the Issue

Read the issue via `youtrack-agent` MCP. Then do **all three** — none are optional:

1. **Assign** to `AI Agent [Kxrpc]` (look up the login once, cache it).
2. **Set state** to `In progress`.
3. **Set sprint** to the current active planning period if none set. The last
   non-checkmarked `[Kxrpc] Planning Period` is typically active. If ambiguous, check
   which period has `In progress` issues.

**Post the workflow checklist** as the first comment on the issue. Use the template
in `assets/yt-workflow-checklist.md`. This checklist tracks your progress — update it
by editing the comment as you complete each step throughout the workflow.

## Step 2: Analyze the Problem

Read the issue body **and all comments** — comments often contain critical context,
reproductions, workarounds, or scope changes that aren't in the original description.

Understand:
- What exactly is broken or missing?
- Which modules/files are likely involved?
- Is this a bug, feature, or task?
- Are there linked issues?

### For bugs: investigate root cause

Apply the `superpowers:systematic-debugging` discipline before posting the reproducer:
- Reproduce the issue (or confirm it from the description)
- Trace the data flow backward from the error to find where incorrect state originates
- Form a specific, concrete hypothesis — not "something with serialization" but
  "Field X is null because `deserialize()` silently drops the payload when..."
- The reproducer you post should confirm or demonstrate this hypothesis

### Post a reproducer

If the issue is a bug and reproduction steps are unclear or missing, post a
reproducer comment using the template in `assets/yt-reproducer-comment.md`.
If no reproducer is needed (feature request, task, obvious fix, already has one),
post the explanation why.

### Update the issue body with Agent Analysis

Append your analysis to the issue body using the template in
`assets/yt-issue-update.md`. Fill in affected modules and root cause. This is the
problem analysis — do not include the fix or solution here. Reproducers belong in
the triage comment, not the issue body.

## Step 3: Set Up the Worktree

Work in an isolated worktree. All subsequent operations use the worktree path.

```bash
git fetch origin main
git worktree add -b <branch-name> /tmp/krpc-<issue-id> origin/main
```

Branch naming: `fix/KRPC-<N>` (bugs), `feat/KRPC-<N>` (features), `task/KRPC-<N>` (tasks).

When using Gradle skills, specify the worktree path as `projectRoot`.

## Step 4: Write a Failing Test (if applicable)

Skip for: docs-only, build/infra, trivial/obvious fixes, or when existing tests
already cover the scenario.

When needed — may be a **new test** or **modification of an existing one**:
1. Identify the correct test module
2. Write/modify a test that captures the buggy behavior or missing feature
3. Run via `running_gradle_tests` and verify the failure is a valid RED state per
   `superpowers:test-driven-development` — the test must compile and run, then fail
   on the assertion that captures the actual bug. A compilation error or setup crash
   is not a valid RED.
4. Follow existing test patterns in the same module

After the fix is applied (Step 5), re-run to confirm GREEN. If it still fails, the
fix is incomplete — do not move on.

## Step 5: Apply the Fix

### Plan before coding (depth scales with complexity)

**Simple** (single file, obvious): Brief inline bullet points.

**Medium** (2-5 files, clear approach): Use the Plan agent for a structured plan.
Start executing immediately — no user approval needed.

**Complex** (architectural, multi-module, public API, unclear tradeoffs):
1. Use `superpowers:brainstorming` to explore approaches — this surfaces alternatives
   and tradeoffs before committing to a plan.
2. Plan agent → detailed plan (root cause, approach, alternatives, files, risks, verification)
3. Spawn a **review agent** to critique the plan before coding (edge cases, simpler
   alternatives, binary compat risks, architectural patterns)
4. Incorporate feedback, then execute. Still no user approval — the review is for
   correctness, not permission.

### Coding guidelines

- Follow project's conventions
- Keep changes minimal and focused — don't refactor surrounding code
- After fixing, run tests from Step 4 + related module tests

## Step 6: Check Documentation

Use the `update-doc` skill's conventions. Evaluate:
- Changed public API behavior → update KDoc / Writerside topics
- New public API → add KDoc (required)
- Changed config/setup → update Writerside topics
- Deprecations → update/create migration guide

Even if no documentation changes are needed, **state why explicitly** before moving
on (e.g., "No docs changes — internal implementation only, no public API affected").
Do not silently skip this step.

## Step 7: Run Local Verifications

**Always** use the `run-local-verifications` skill to check your work. 
Before running any check, reason about whether
your specific change actually falls within its scope — don't run checks mechanically
just because the file is in a published module (e.g., JPMS is irrelevant for native-only
code, ABI check is irrelevant for internal-only logic changes). Never run Gradle builds
in parallel against the same worktree — builds share state and will corrupt
each other.

All relevant checks must pass before proceeding to Step 8. If a check fails, fix
and re-run — do not proceed to code review with known failures.

## Step 8: Code Review

Spawn **at least 2 independent review agents in parallel** on the full diff.
See `references/code-review-agents.md` for the reviewer menu and prompting guide.

Use the `superpowers:requesting-code-review` discipline: capture the git range,
provide each reviewer with structured context (what changed, why, which requirements
it addresses). Then handle feedback in priority order:

1. **Errors/Critical** — fix immediately. Do not proceed to Step 9 with unfixed errors.
2. **Warnings/Important** — fix unless you have a concrete reason to skip (document it).
3. **Nits/Minor** — fix if trivial, skip if it would bloat the diff.

After applying error/warning fixes, re-run the tests from Step 4 to confirm no
regressions.

## Step 9: Commit Changes

Separate commits logically:
1. **Fix** — the code change
2. **Tests** — if added/modified
3. **Docs** — if updated
4. **Other** — ABI dumps, generated files, infra

### Commit message format

**Subject line only — no body.** Keep under 72 characters. Include the YT ticket ID
and GH issue number (if the YT ticket references one). Use imperative mood, present
tense, no trailing period.

```
KRPC-NNN: <Imperative verb> <what changed> (#GH-issue if exists)
```

Message examples:
- `KRPC-123 Add DSL builder for ProtoConfig`
- `KRPC-245: Strip common enum value prefixes (#12345)`

## Step 10: Create a Draft PR

1. Push: `git push -u origin <branch-name>`
2. Check if the YT ticket has a `github-issue` tag → find the GH issue number
3. Create draft PR with `gh pr create --draft`. Use the template in
   `assets/gh-pr-body.md` — the PR body contains only the **solution**, not problem
   analysis or root cause (those belong in the YT issue). Include `Fixes #NNN` if a
   linked GH issue exists.
4. Add label. At least one label must be set.
5. Post any skipped review warnings/nits as a checkbox comment on the PR
   (see `assets/gh-code-review-comment.md`). If all review issues were fixed,
   don't skip the comment, just post that all internal comments were addressed.

## Step 11: Run CI Checks (TeamCity + GitHub Actions in parallel)

Both must pass. GH Actions trigger on push; TeamCity you trigger manually.

**TeamCity**: Use `use-teamcity` skill. Pick builds based on what changed (see skill
for build IDs). May skip if trivial and local verifications covered it.

**GH Actions**: Are run automatically. 

**Monitor both via subagents**: Spawn two background subagents — one polling
GitHub, one polling TeamCity. Each reports back on completion.

On failure — apply `superpowers:systematic-debugging` discipline, not blind retries:
1. Read the full error/stack trace, not just the test name.
2. Check if pre-existing — run the same test against `main` (or check recent TC
   history). Pre-existing failures get noted in the CI comment, not fixed.
3. Reproduce locally if feasible via `running_gradle_tests` — faster than CI round-trips.
4. Form a hypothesis before fixing. If your first fix doesn't work, return to the
   error output — do not stack guesses.
5. Fix, commit, push. GH Actions restart automatically; re-trigger TC if needed.
   Spawn monitoring subagents again.

**Post a CI report comment on the PR** — this is mandatory, not optional. The
reviewer needs to see pipeline status directly on the PR. Use the template in
`assets/gh-ci-report-comment.md`. Every pipeline gets a row. 
Failed ones that weren't retried get an explanation
of why the agent chose not to retry. Update this comment (don't post a new one) if
CI is re-triggered after fixes. Do not just report CI results in the conversation —
the PR comment is the deliverable.

## Step 12: Finalize the PR

Once all CI passes:
1. Rebase on latest main: `git rebase origin/main` + `git push --force-with-lease`
2. Remove draft: `gh pr ready <pr-number>`
3. Add reviewer: `gh pr edit <pr-number> --add-reviewer Mr3zee`
4. Verify: `gh pr view <pr-number> --json isDraft,reviewRequests,headRefOid` — confirm
   draft removed, reviewer assigned, and head SHA matches local HEAD.

## Step 13: Update YouTrack

1. **Set state** to `Fixed in Branch` via `youtrack-agent`.
2. **Update the issue body** — if you uncovered new information about the issue during
   the fix (e.g., deeper root cause, additional affected modules, edge cases found
   during testing), append those findings to the Agent Analysis section from Step 2.
   Do not duplicate the fix summary or solution here — those belong in the PR body.
   If nothing new was discovered, skip the body update.

## Step 14: The Main Cycle is Done

Before reporting completion, apply `superpowers:verification-before-completion` —
verify actual state, do not assume previous steps succeeded:

1. `gh pr view <pr-number> --json isDraft,reviewRequests` — draft removed, reviewer assigned
2. Verify CI report comment exists on the PR
3. Verify YT issue state is `Fixed in Branch` via `youtrack-agent`
4. Verify the workflow checklist comment is fully checked off

If any check fails, fix it before reporting. Then report:
PR URL, YT status, brief fix summary, worktree path.

Do NOT wait for review feedback in the session, feedback would be posted in the PR,
and you will be notified by a user.

## Addressing PR Comments

This may take **multiple turns**. Each turn:

1. Read all PR comments (human reviews + internal checkbox comment)
2. Human reviewer comments take priority. For the checkbox comment, `[x]` = fix, `[ ]` = skip.
3. **Before implementing feedback, apply `superpowers:receiving-code-review` discipline**:
   - Verify the reviewer's claim against the actual codebase — don't assume they're correct
   - If the suggestion would break existing functionality, violate YAGNI, or conflict
     with project conventions, push back with technical reasoning in a PR comment reply
   - Implement in priority order: blocking issues first, then simple fixes, then complex
4. Re-run tests/verifications, commit, push, re-trigger CI if needed
5. Update the review checkbox comment per `assets/gh-code-review-comment.md`
6. Update the CI checkbox comment per `assets/gh-ci-report-comment.md`
7. Update the issue body per `assets/yt-issue-update.md` if new information was discovered

## Rebase Discipline

Keep the branch on latest `main`. Always `git fetch origin main` then
`git rebase origin/main` + `git push --force-with-lease`. Required at:
- Step 11: before CI push
- Step 12: before finalizing

Even if you just created the worktree from `origin/main`, always re-verify before
pushing — changes may have landed on main in the meantime. Don't reason that "I just
branched off, so it's fresh" — always fetch and rebase.

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

And always update the progress checklist as per `assets/yt-workflow-checklist.md`.
