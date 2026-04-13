---
name: fix-issue
description: >
  Autonomously fix a YouTrack issue end-to-end: from ticket pickup through code fix,
  tests, PR creation, CI validation, and reviewer assignment.
disable-model-invocation: true
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
- **GitHub**: Uses the **AI Agent [Kxrpc]** GitHub App. The private key PEM path
  must be in `$GITHUB_APP_KEY_PATH`. **Never** use the user's `gh` CLI auth —
  always generate a fresh installation token from the App key (see below).

### GitHub App Token Generation

Installation tokens expire after 1 hour. Generate one at workflow start and refresh
if any GitHub API call returns 401. The script manages its own venv and deps.

```bash
export GH_TOKEN=$(scripts/gh-app-token.sh)
```

(Path is relative to this skill's directory: `.claude/skills/fix-issue/scripts/gh-app-token.sh`)

Once `GH_TOKEN` is set, all `gh` CLI commands automatically use it. If you get a
401 mid-workflow, re-run the script to refresh.

### Pre-flight Access Check — MANDATORY

Before any work, run the pre-flight script. It checks connectivity **and identity**
for all three services — verifying that the tokens belong to the AI Agent accounts,
not the human user's personal credentials. If any check fails → **stop and report
to the user**. Do NOT proceed — not even reading the issue.

Note: make sure to generate the GitHub token before running the pre-flight check script.

```bash
.claude/skills/fix-issue/scripts/preflight-check.sh
```

The script verifies:
1. **YouTrack** — token works AND authenticated user is `AI Agent [Kxrpc]`
2. **TeamCity** — token works AND reports the authenticated identity
3. **GitHub** — App token generation succeeds, repo access works, AND authenticated
   entity is a `[bot]` account (not a human user)

Exit code 0 = all passed. Non-zero = at least one check failed — read the output
for details on which service and why.

### No silent fallback policy

If a service becomes unreachable mid-workflow:
- YouTrack MCP → REST via `$YOUTRACK_AGENT_TOKEN` is the only permitted fallback
- TC CLI or GitHub App token generation fails → **stop and report** (no fallback)

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

---

## Workflow Phases

The workflow has four phases with entry/exit conditions at each boundary. If the
approach changes significantly during any phase, loop back to the appropriate phase
rather than pushing forward with stale verification results — reset the branch
cleanly (see Error Recovery), re-run verifications from scratch, and update any
PR/comment descriptions to reflect the new approach.

If context is running low mid-workflow, commit your current state, update the YT
issue with progress (which phase you're in, what's done, what remains), and tell
the user which phase to resume from. The worktree and branch preserve all work.

## Phase A: Claim & Analyze

### A.1: Claim the Issue

Read the issue via `youtrack-agent` MCP. Then do **all four** — none are optional:

1. **Assign** to `AI Agent [Kxrpc]` (look up the login once, cache it).
2. **Set state** to `In progress`.
3. **Set sprint** to the current active planning period if none set. The last
   non-checkmarked `[Kxrpc] Planning Period` is typically active. If ambiguous, check
   which period has `In progress` issues.
4. **Check for linked GH issue** — look for a `github-issue` tag on the ticket. If
   present, find and note the GH issue number now. You will need it in Phase D
   (commit message and PR body `Fixes #NNN`).

### A.2: Analyze the Problem

Read the issue body, **all comments**, and **all linked issues** — comments and linked issues often contain critical context,
reproductions, workarounds, or scope changes that aren't in the original description.

Understand:
- What exactly is broken or missing?
- Which modules/files are likely involved?
- Is this a bug, feature, or task?
- Are there linked issues?

#### Search for similar issues — MANDATORY

Before diving into root cause or planning, check whether this problem has been
seen before or is related to other tracked work. Search **both** YouTrack and
GitHub — prior issues may contain root cause analysis, attempted fixes, or
workarounds that save significant time.

1. **YouTrack**: Search for issues with similar keywords,
   affected modules, or error messages. Look at both open and resolved issues —
   a resolved issue may contain the exact fix pattern you need, and an open
   duplicate means you should link rather than duplicate effort.
2. **GitHub**: Search issues and PRs in the repo (`gh search issues`, `gh search prs`)
   for related keywords and error messages. Closed PRs may contain relevant
   discussion or reverted approaches worth knowing about.

If you find related issues, **always note them** in the triage comment and
**always link them** in YouTrack. If you find an exact duplicate,
stop and report to the user rather than doing redundant work.

#### For bugs: investigate root cause

**Always Invoke** `superpowers:systematic-debugging` (via the Skill tool — not just
mentally following its ideas) before posting the triage comment. The skill's
structured checklist must be followed, not approximated:
- Reproduce the issue (or confirm it from the description)
- Trace the data flow backward from the error to find where incorrect state originates
- Form a specific, concrete hypothesis — not "something with serialization" but
  "Field X is null because `deserialize()` silently drops the payload when..."
- The reproducer you post should confirm or demonstrate this hypothesis

#### Dependency source investigation tip

When researching library internals via `search_dependency_sources`, FULL_TEXT mode
may return "no dependencies found with indices" for method-level searches even when
the class exists. Workaround: use DECLARATION mode to find the class first, then
use `read_dependency_sources` with pagination to read the specific section you need.

#### Post a triage comment — MANDATORY (every issue type)

**Always post a YT comment at this step** — this is unconditional, not optional.
The comment is the proof that analysis happened. One of:

- **Bug with reproducer**: Post using the template in `assets/yt-reproducer-comment.md`.
- **Bug without reproducer** (obvious fix, already has one, trivially reproducible):
  Post a comment **explicitly stating why** (e.g., "Reproducer not needed — the bug
  is visible in any generated KDoc block comment; the issue description itself serves
  as the reproduction").
- **Non-bug** (feature request, task): Post what the analysis found and the planned
  approach.

#### Update the issue body with Agent Analysis

Append your analysis to the issue body using the template in
`assets/yt-issue-update.md`. Fill in affected modules and root cause. This is the
problem analysis — do not include the fix or solution here. Reproducers belong in
the triage comment, not the issue body.

**Exit condition**: Triage comment posted, issue body updated, related issues linked.

## Phase B: Implement

Entry: Phase A complete. Always `git fetch origin main` and rebase on latest
`origin/main` before pushing any commits — even if you just created the worktree.

### B.1: Set Up the Worktree

Work in an isolated worktree. All subsequent operations use the worktree path.

```bash
git fetch origin main
git worktree add -b <branch-name> /tmp/krpc-<issue-id> origin/main
```

Branch naming: `fix/KRPC-<N>` (bugs), `feat/KRPC-<N>` (features), `task/KRPC-<N>` (tasks).

**Create `local.properties` in the worktree** — Gradle builds require it.
Use the template at `assets/local.properties.template` (relative to this skill's directory),
replacing `<User>` with the actual macOS username from `$HOME`.

```bash
sed "s|<User>|$(whoami)|g" .claude/skills/fix-issue/assets/local.properties.template \
  > /tmp/krpc-<issue-id>/local.properties
```

When using Gradle skills, **always use the worktree root** (e.g., `/tmp/krpc-KRPC-NNN`)
as `projectRoot` — even for included builds (`compiler-plugin/`, `gradle-plugin/`,
`protoc-gen/`, `dokka-plugin/`). The Gradle MCP rejects included build subdirectories
as `projectRoot` because they have no Gradle wrapper. Address included build tasks
from the root with their composite prefix (the directory name passed to `includeBuild()`):
`:protoc-gen:common:test`, `:compiler-plugin:compiler-plugin-backend:build`,
`:dokka-plugin:build`, etc.

### B.2: Write a Failing Test (if applicable)

Skip **formal tests** for: docs-only, trivial/obvious fixes, or when existing tests
already cover the scenario.

#### Demonstrate-it-works principle

Even when formal tests are skipped, **every new capability must be demonstrated
end-to-end before you can claim it works.** If you add a native binary, a script,
a code generator, a new build task, or any other executable artifact — you must
actually run it and show it produces the expected output. "It compiled" or "the
file exists" is not verification. The build/infra exemption from formal tests does
NOT exempt you from proving the thing works. If you can't demonstrate it works,
you can't move on.

Examples of what "demonstrate" means:
- New native binary → execute it with representative input, verify output
- New Gradle task → run the task, verify it produces the expected artifact
- New script → execute it, verify it does what it's supposed to do
- New code generator → run generation, verify the output compiles and is correct

#### Writing formal tests

When needed — may be a **new test** or **modification of an existing one**:
1. Identify the correct test module
2. **Build smoke test first**: Before writing any test code, verify the target test
   module compiles (`<module>:compileTestKotlin` or equivalent). If it doesn't,
   diagnose the build issue first — a broken build dependency or missing task wiring
   can block ALL test compilation, and you'll waste cycles writing tests you can't run.
3. Write/modify a test that captures the buggy behavior or missing feature
4. **Always Invoke** `superpowers:test-driven-development` (via the Skill tool — not just
   running the test and eyeballing the output). Then run via `running_gradle_tests`
   and verify the failure is a valid RED state — the test must compile and run, then
   fail on the assertion that captures the actual bug. A compilation error or setup
   crash is not a valid RED.
5. Follow existing test patterns in the same module

After the fix is applied (B.3), re-run to confirm GREEN. If it still fails, the
fix is incomplete — do not move on.

### B.3: Apply the Fix

#### Plan before coding (depth scales with complexity and issue type)

**Features always use the Complex path** — regardless of apparent size. A feature
adds new capability, which means design decisions about API surface, naming,
extensibility, and integration points. Even a "small" feature benefits from
brainstorming alternatives and having a plan reviewed before coding. Do not
downgrade a feature to Standard based on file count or simplicity alone.

**Standard** (obvious approach, no design decisions needed — any issue type, except features):
Brief inline bullet points, start coding.

**Complex** (all features, OR bugs/tasks that are architectural, multi-module,
public API, or have unclear tradeoffs):
1. **Always Invoke** `superpowers:brainstorming` (via the Skill tool — not just thinking
   through alternatives yourself). This surfaces approaches and tradeoffs before
   committing to a plan.
2. Plan agent → detailed plan (root cause, approach, alternatives, files, risks, verification)
3. Spawn a **review agent** to critique the plan before coding (edge cases, simpler
   alternatives, binary compat risks, architectural patterns)
4. Incorporate feedback, then execute. Still no user approval — the review is for
   correctness, not permission.

#### Coding guidelines

- Follow project's conventions
- Keep changes minimal and focused — don't refactor surrounding code
- After fixing, run tests from B.2 + related module tests

**Exit condition**: All tests pass (both new and existing module tests). Fix is complete.

## Phase C: Verify & Review

### C.1: Check Documentation

Use the `update-doc` skill's conventions. Evaluate:
- Changed public API behavior → update KDoc / Writerside topics
- New public API → add KDoc (required)
- Changed config/setup → update Writerside topics
- Deprecations → update/create migration guide

Even if no documentation changes are needed, **state why explicitly** before moving
on (e.g., "No docs changes — internal implementation only, no public API affected").
Do not silently skip this step.

### C.2: Run Local Verifications

**Always** use the `run-local-verifications` skill to check your work. 
Before running any check, reason about whether
your specific change actually falls within its scope — don't run checks mechanically
just because the file is in a published module (e.g., JPMS is irrelevant for native-only
code, ABI check is irrelevant for internal-only logic changes). Never run Gradle builds
in parallel against the same worktree — builds share state and will corrupt
each other.

#### Compiler plugin changes — MANDATORY extra verification

If your change touches **any** file under `compiler-plugin/` (sources, templates, CSM
blocks, or build scripts), you **must invoke** the `verify-compiler-plugin-compatibility`
skill. This is not optional and cannot be skipped regardless of how small the change
appears. The compiler plugin must compile against all supported Kotlin versions and
Kotlin Master. A single-version local build is not sufficient — CSM template interactions
across versions are the most common source of regressions.

**Generated code**: If verifications produce updated generated files (ABI dumps, WKT,
conformance code, platform table, etc.), **do not hand-edit them** — commit the
regenerated output as-is. These go into a separate commit (Phase D).

All relevant checks must pass before proceeding to C.3. If a check fails, fix
and re-run — do not proceed to code review with known failures.

### C.3: Code Review

Spawn **at least 2 independent review agents in parallel** on the full diff.
See `references/code-review-agents.md` for the reviewer menu and prompting guide.

**Always Invoke** `superpowers:requesting-code-review` (via the Skill tool — not just
spawning reviewers directly). The skill structures the review request: capture the
git range, provide each reviewer with structured context (what changed, why, which
requirements it addresses). 

When they are done **always invoke** the `superpowers:receiving-code-review` (via the Skill tool — 
not just blantly going through the reviews) and handle feedback in priority order:

1. **Errors/Critical** — fix immediately. Do not proceed to Phase D with unfixed errors.
2. **Warnings/Important** — fix unless you have a concrete reason to skip (document it).
3. **Nits/Minor** — fix if trivial, skip if it would bloat the diff.

After applying error/warning fixes, re-run the tests from B.2 to confirm no
regressions.

**Exit condition**: All local verifications pass, all review errors fixed, tests
re-confirmed after review fixes.

## Phase D: Ship

After code review passes, follow `references/ship-workflow.md` for commit, PR, CI,
finalization, and YouTrack update steps.

**Exit condition**: PR is not draft, reviewer assigned, CI green, YT issue set to
"Fixed in Branch", completion verification passed.

---

## Addressing PR Comments

This may take **multiple turns**. Each turn:

1. Read all PR comments (human reviews + internal checkbox comment)
2. Human reviewer comments take priority. For the checkbox comment, `[x]` = fix, `[ ]` = skip.
3. **Always Invoke** `superpowers:receiving-code-review` (via the Skill tool — not just
   reading comments and implementing them). The skill enforces critical verification:
   - Verify the reviewer's claim against the actual codebase — don't assume they're correct
   - If the suggestion would break existing functionality, violate YAGNI, or conflict
     with project conventions, push back with technical reasoning in a PR comment reply
   - Implement in priority order: blocking issues first, then simple fixes, then complex
4. Re-run tests/verifications, commit, push, re-trigger CI if needed
5. **Mark comments as handled on GitHub**:
   - Code review comments that were addressed → **resolve the conversation**
   - Regular (non-code-review) comments with addressed feedback → add a **thumbs-up
     reaction** (👍) to acknowledge
6. Update the review checkbox comment per `assets/gh-code-review-comment.md`
7. Update the CI checkbox comment per `assets/gh-ci-report-comment.md`
8. Update the issue body per `assets/yt-issue-update.md` if new information was discovered

"Address the feedback" always means **all of the above** — implementing the fixes,
resolving/reacting to comments, AND updating the checkbox comments. Never treat
the checkboxes as optional when the user asks to address feedback.

## Error Recovery

- **Build fails locally**: Read the error, fix, re-run. Don't just retry.
- **TC build fails**: Investigate with `teamcity run log <id> --failed`, fix and push.
- **TC build canceled** (not failed): Retry once. External cancellation (agent issues,
  queue management) is common and doesn't indicate a code problem. Only investigate
  if it cancels again.
- **Rebase conflicts**: Resolve. No user intervention is expected.
- **Approach pivot**: If the implementation direction is wrong, `git reset --soft` to
  the last clean commit and recommit with the new approach. Run a clean build (`clean`
  task), update PR/comment descriptions, and re-run Phase C from scratch.

Service outages follow the No Silent Fallback policy above.

## Escalate to User

Stop and report instead of continuing to grind when:
- **Ambiguous root cause** — thorough investigation yields multiple plausible
  explanations and you cannot confidently pick the right one
- **CI fails 3+ times** on the same pipeline after distinct fix attempts (not
  retries — actual code changes that still fail)
- **Scope exceeds the ticket** — you discovered a bigger problem than the issue
  describes and the fix would span unrelated modules or concerns
- **Blocked on external input** — needs a library update, infrastructure change,
  or another team's decision

When escalating, include: what you tried, what you learned, where the worktree is,
and a specific question or decision for the user to unblock you.

## Progress Communication

Brief status at each milestone:
- "Claimed KRPC-540, set to In Progress"
- "Reproduced the bug — null pointer when streaming with empty payload"
- "Fix applied, test passing"
- "Code review done — 2 agents, 1 warning fixed"
- "PR #42 created as draft"
- "CI passing, marking PR ready, and adding reviewers"
- "Done — PR: <url>, YT status: Fixed in Branch"
