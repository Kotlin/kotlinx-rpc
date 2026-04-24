---
name: fix-issue
description: >
  Autonomously fix a YouTrack issue end-to-end: from ticket pickup through code fix,
  tests, PR creation, CI validation, and reviewer assignment.
disable-model-invocation: true
---

# Fix YouTrack Issue — Autonomous Workflow

Complete lifecycle of fixing a KRPC YouTrack issue: pickup → analysis → code →
tests → PR → CI → reviewer assignment. Full autonomy is a hard requirement — no user approval between
phases. Communicate progress at natural milestones.

## Authentication — CRITICAL

This workflow uses **dedicated agent credentials**, not the user's personal tokens.
These overrides take precedence over defaults in `use-youtrack` and `use-teamcity` skills.

- **YouTrack**: Use the **`youtrack-agent`** MCP server (not `youtrack`). For REST
  fallback, use `$YOUTRACK_AGENT_TOKEN` (not `$YOUTRACK_TOKEN`).
- **TeamCity**: **Never** run `teamcity auth status/login`. Prefix every command
  with `TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN`.
- **GitHub**: Always go through the `gh-bot` wrapper — see next section.

### Using `gh` — always through the `gh-bot` wrapper

Every `gh` invocation in this skill MUST go through:

```bash
.claude/skills/fix-issue/scripts/gh-bot.sh <gh args...>
```

`gh-bot` generates a fresh App installation token on every call and execs `gh`
with that token set inline. It is a drop-in replacement for `gh`. 
You MUST NEVER call `gh` directly.

Examples:
```bash
.claude/skills/fix-issue/scripts/gh-bot.sh pr create --draft ...
.claude/skills/fix-issue/scripts/gh-bot.sh pr ready 123
.claude/skills/fix-issue/scripts/gh-bot.sh api /repos/Kotlin/kotlinx-rpc/issues/42/comments ...
```

### Pre-flight Access Check — MANDATORY

Before any work — including reading the issue — run the pre-flight script. It
checks connectivity AND identity for all three services, verifying tokens belong
to the AI Agent accounts, not the user's personal credentials. If any check fails
→ **stop and report**. Do not proceed.

```bash
.claude/skills/fix-issue/scripts/preflight-check.sh
```

Exit code 0 = all passed. Non-zero = at least one check failed; read output for
which service and why, never proceed with a failed check.

### No silent fallback policy

If a service becomes unreachable mid-workflow:
- YouTrack MCP → REST via `$YOUTRACK_AGENT_TOKEN` is the only permitted fallback
- TC CLI or `gh-bot` fails → **stop and report** (no fallback)

Never silently skip a step. Never invent data. Stop, report what failed and at
which phase, wait for instructions.

---

## Skills used in this workflow

When this skill says "invoke `superpowers:X`", **use the Skill tool — don't just
think through the ideas yourself**. Autonomous mode tends to skip skill
invocations, which defeats the discipline (TDD red-green, structured debugging,
code-review formatting) those skills encode.

---

## References

Detailed guidance for specific situations and phases:

- **`references/investigation-tips.md`** — Phase A.3 analysis patterns: prior-issue search (mandatory) and the `search_dependency_sources` FULL_TEXT workaround.
- **`references/develocity-tips.md`** — How to query Develocity without overflowing the chat.
- **`references/audit-style-scoping.md`** — Scope discipline for "Audit X" / "Review Y" tickets.
- **`references/reproducer-verification.md`** — Before/after verification protocol when the issue includes an external reproducer.
- **`references/verification-special-cases.md`** — Mandatory extra verifications for conformance `known_failures.txt` and compiler-plugin changes (Phase C.2).
- **`references/code-review-agents.md`** — Reviewer menu and prompting guide for Phase C.3.
- **`references/ship-workflow.md`** — Phase D steps: commits, PR creation, CI polling, finalization, YouTrack update.
- **`references/ci-polling.md`** — How to run the TC + GitHub Actions poll scripts as background bash in Phase D.3.
- **`references/addressing-pr-comments.md`** — Multi-turn workflow for handling PR feedback after the PR is up.
- **`references/filing-follow-up-issues.md`** — Drop-in replacement for `file-youtrack-issue` when creating YT issues mid-workflow.
- **`references/youtrack-attachments.md`** — REST recipe for attaching files to YT issues when the MCP server can't.

---

## Input

**Required**: A YouTrack issue ID (e.g., `KRPC-540`).

**Alternative**: User asks to scan for issues. Search `UFG Claude`-tagged issues:
```
project: KRPC tag: {UFG Claude} State: Open sort by: Priority asc, created asc
project: KRPC tag: {UFG Claude} State: Submitted sort by: Priority asc, created asc
```
Prioritize `Open` over `Submitted`, higher priority first. Take the top one.

---

## Workflow Phases

Four phases (A–D), each with exit conditions. If the approach changes
significantly mid-phase, loop back — reset the branch cleanly (see Error
Recovery), re-run verifications, update PR/comment descriptions with new approach.

## Phase A: Claim & Analyze

### A.1: Claim the Issue

Read the issue via `youtrack-agent` MCP. Then do **all four** — none are optional:

1. **Assign** to `AI Agent [Kxrpc]` (look up the login once, cache it).
2. **Set state** to `In progress`.
3. **Set sprint** to the current active planning period if none is set. Find it
   via `get_issue_fields_schema` for the KRPC project — the sprint/planning
   period field enumerates all periods. The last non-checkmarked
   `[Kxrpc] Planning Period` is typically active. Planning periods are custom
   fields, not tags.
4. **Check for linked GH issue** — look for a `github-issue` tag. If present,
   note the GH issue number now; you'll need it in Phase D for the commit
   message and PR body (`Fixes #NNN`).

### A.2: Set Up the Worktree

Create the worktree **before** exploring code so all analysis runs against the
latest `origin/main`, not whatever the original repo has checked out.

```bash
git fetch origin main
git worktree add -b <branch-name> /tmp/krpc-<issue-id> origin/main
```

Branch naming: `fix/KRPC-<N>` (bugs), `feat/KRPC-<N>` (features),
`task/KRPC-<N>` (tasks).

**Create `local.properties` in the worktree** — Gradle requires it:

```bash
sed "s|<User>|$(whoami)|g" .claude/skills/fix-issue/assets/local.properties.template \
  > /tmp/krpc-<issue-id>/local.properties
```

When using Gradle skills, **always use the worktree root** as `projectRoot` —
even for included builds (`compiler-plugin/`, `gradle-plugin/`, `protoc-gen/`,
`dokka-plugin/`). The Gradle MCP rejects included build subdirectories because
they have no Gradle wrapper. Address included build tasks from the root using
the composite prefix: `:protoc-gen:common:test`,
`:compiler-plugin:compiler-plugin-backend:build`, `:dokka-plugin:build`.

All subsequent operations — exploration, builds, tests — use the worktree path.

### A.3: Analyze the Problem

Read the issue body, **all comments**, and **all linked issues** — they often
contain critical context, reproductions, or scope changes missing from the
description. Understand what's broken/missing, which modules are involved, the
type (bug/feature/task), and linked issues.

For situational guidance (similar-issues search, dependency-source gotcha,
Develocity usage, audit-style scope traps), consult the References section
above.

For bugs, you must invoke `superpowers:systematic-debugging` before posting the triage
comment: reproduce, trace data flow backward to where incorrect state
originates, form a specific hypothesis ("Field X is null because `deserialize()`
silently drops the payload when..."), and have your reproducer confirm it.

**Exception**: usability problems (confusing behavior, poor defaults, rough
ergonomics) with an obvious root cause aren't bugs in this sense — treat them
like tasks (document in the triage comment, follow the Standard path in B.2).

#### Post a triage comment — MANDATORY (every issue type)

The comment is proof analysis happened. One of:

- **Bug with reproducer**: use `assets/yt-reproducer-comment.md`.
- **Bug without reproducer** (obvious fix, already has one, trivially
  reproducible): comment **explicitly stating why** ("Reproducer not needed —
  the bug is visible in any generated KDoc block; the issue description itself
  serves as the reproduction").
- **Non-bug** (feature, task): post analysis findings and planned approach.

#### Update the issue body

Append your analysis using `assets/yt-issue-update.md`. **Re-read the template
before writing — do not reconstruct from memory.** Fill in affected modules and
root cause. Problem analysis only — no fix or solution. Reproducers go in the
triage comment, not the body.

**Exit condition**: triage comment posted, issue body updated, related issues
linked.

## Phase B: Implement

Entry: Phase A complete. `git fetch origin main` and rebase before pushing any
commits.

### B.1: Write a Failing Test (if applicable)

Skip **formal tests** for: docs-only, trivial/obvious fixes, or when existing
tests already cover the scenario.

#### Demonstrate-it-works principle

Even when formal tests are skipped, **every new capability must be demonstrated
end-to-end before you can claim it works.** If you add a native binary, a
script, a code generator, a new build task, or any other executable artifact —
you must actually run it and show it produces the expected output. "It compiled"
or "the file exists" is not verification. The build/infra exemption from formal
tests does NOT exempt you from proving the thing works. If you can't demonstrate
it works, you can't move on.

Examples:
- New native binary → execute it with representative input, verify output
- New Gradle task → run the task, verify it produces the expected artifact
- New script → execute it, verify it does what it's supposed to do
- New code generator → run generation, verify the output compiles and is correct

When the issue includes an **external reproducer** (a separate project, repo, or
code sample), end-to-end verification against that reproducer is **mandatory** —
follow `references/reproducer-verification.md`. The before/after verification
matrix posted to YT is the primary evidence artifact for the fix.

#### Writing formal tests

When needed (new test or modification of existing):

1. Identify the correct test module.
2. **Build smoke test first**: verify the target test module compiles
   (`<module>:compileTestKotlin` or equivalent). A broken build dependency or
   missing task wiring blocks all test compilation — surface that before writing
   tests.
3. Write/modify a test that captures the buggy behavior or missing feature.
4. Invoke `superpowers:test-driven-development`. Run via `running_gradle_tests`
   and verify a valid RED state — the test must compile and run, then fail on
   the assertion. A compilation error or setup crash is not a valid RED.
5. Follow existing patterns in the same module.

After the fix is applied (B.2), re-run to confirm GREEN. **If it still fails,
the fix is incomplete — do not move on.**

### B.2: Apply the Fix

#### Plan before coding

**Features always use the Complex path** regardless of size — they involve
design decisions (API surface, naming, extensibility, integration) that benefit
from brainstorming and a reviewed plan.

**Standard** (obvious approach, no design decisions, non-feature): brief inline
bullets, start coding.

**Complex** (all features, OR bugs/tasks that are architectural, multi-module,
public API, or have unclear tradeoffs):

1. Invoke `superpowers:brainstorming` to surface approaches and tradeoffs.
2. Plan agent → detailed plan (root cause, approach, alternatives, files,
   risks, verification).
3. Spawn a **review agent** to critique the plan before coding (edge cases,
   simpler alternatives, binary-compat risks, architectural patterns).
4. Incorporate feedback, then execute. No user approval — the review is for
   correctness, not permission.

Follow project conventions. Keep changes minimal and focused — don't refactor
surrounding code. After fixing, run B.1 tests + related module tests.

**Exit condition**: all tests pass (new + existing module tests).

## Phase C: Verify & Review

### C.1: Check Documentation

Use `update-doc` skill conventions. Evaluate:

- Changed public API behavior → update KDoc / Writerside topics.
- New public API → add KDoc (required).
- Changed config/setup → update Writerside topics.
- Deprecations → update/create migration guide.

If no doc changes are needed, **state why explicitly** before moving on (e.g.,
"No docs changes — internal implementation only, no public API affected"). Do
not silently skip.

### C.2: Run Local Verifications

**Always** use the `run-local-verifications` skill. Before running any check,
reason about whether your change actually falls within its scope — don't run
mechanically (JPMS is irrelevant for native-only code; ABI check is irrelevant
for internal-only logic). Never run Gradle builds in parallel against the same
worktree — builds share state and corrupt each other.

For special-case verifications (conformance `known_failures.txt` changes, any
compiler-plugin change), see `references/verification-special-cases.md`. These
are mandatory extras that cannot be skipped when they apply.

**Generated code**: if verifications produce updated generated files (ABI dumps,
WKT, conformance code, platform table), **do not hand-edit them** — commit the
regenerated output as-is. They go into a separate commit (Phase D).

All relevant checks must pass before proceeding to C.3.

### C.3: Code Review

Spawn **at least 2 independent review agents in parallel** on the full diff.
See `references/code-review-agents.md` for the reviewer menu and prompting
guide.

Invoke `superpowers:requesting-code-review` to structure the request (capture
git range, give each reviewer structured context).

When reviews come back, invoke `superpowers:receiving-code-review` and handle
feedback in priority order:

1. **Errors/Critical** — fix immediately. Do not proceed to Phase D with
   unfixed errors.
2. **Warnings/Important** — fix unless you have a concrete reason to skip
   (document it).
3. **Nits/Minor** — fix if trivial, skip if it would bloat the diff.

After applying error/warning fixes, re-run B.1 tests to confirm no regressions.

**Exit condition**: all local verifications pass, all review errors fixed,
tests re-confirmed.

## Phase D: Ship

Follow `references/ship-workflow.md` for commit, PR, CI, finalization, and
YouTrack update steps.

**Exit condition**: PR is not draft, reviewer assigned, CI green, YT issue set
to "Fixed in Branch", completion verification passed.

---

## Addressing PR Comments

When asked to address PR comments / feedback after the PR is up, follow
`references/addressing-pr-comments.md`. The reference covers reading comments,
implementing fixes, resolving conversations vs. reactions, updating checkbox
comments, and re-requesting review. "Address the feedback" means **all of
these**, not just the code fixes.

## Filing Follow-Up Issues

If you discover related bugs, latent risks, or follow-up work during any phase,
file them immediately rather than losing context. **Read
`references/filing-follow-up-issues.md` first** — it replaces
`file-youtrack-issue` for this workflow and encodes the required field shapes.

## Error Recovery

- **Build fails locally**: read the error, fix, re-run. Don't just retry.
- **TC build fails**: investigate with `teamcity run log <id> --failed`, fix
  and push.
- **TC build canceled** (not failed): retry once. External cancellation (agent
  or queue issues) is common and doesn't indicate a code problem. Only
  investigate if it cancels again.
- **Rebase conflicts**: resolve. No user intervention expected.
- **Approach pivot**: `git reset --soft` to the last clean commit, recommit
  with the new approach, run a clean build (`clean` task), update PR/comment
  descriptions, re-run Phase C from scratch.

Service outages follow the No Silent Fallback policy above.

## Escalate to User

Stop and report instead of grinding when:

- **Ambiguous root cause** — investigation yields multiple plausible
  explanations and you can't confidently pick.
- **CI fails 3+ times** on the same pipeline after distinct fix attempts (not
  retries — actual code changes that still fail).
- **Scope exceeds the ticket** — you found a bigger problem than the issue
  describes; fix would span unrelated modules.
- **Blocked on external input** — needs library update, infra change, or
  another team's decision.

When escalating, include: what you tried, what you learned, where the worktree
is, and a specific question/decision for the user to unblock you.

## Progress Communication

Brief status at each milestone:

- "Claimed KRPC-540, set to In Progress"
- "Reproduced the bug — null pointer when streaming with empty payload"
- "Fix applied, test passing"
- "Code review done — 2 agents, 1 warning fixed"
- "PR #42 created as draft"
- "CI passing, marking PR ready, and adding reviewers"
- "Done — PR: <url>, YT status: Fixed in Branch"
