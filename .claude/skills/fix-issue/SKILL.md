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

### YouTrack

Use the **`youtrack-agent`** MCP server for all YouTrack operations. The tool prefix is
`mcp__youtrack_agent__` (not `mcp__youtrack_agent__`). This server authenticates as the AI agent
account, so all issue updates, comments, and assignments are attributed to the agent.

For REST API fallback, use `YOUTRACK_AGENT_TOKEN` (not `YOUTRACK_TOKEN`):
```bash
curl -s -H "Authorization: Bearer $YOUTRACK_AGENT_TOKEN" \
  "https://youtrack.jetbrains.com/api/<endpoint>?fields=<fields>"
```

### TeamCity

**Never** run `teamcity auth status` or `teamcity auth login`. Instead, prefix every
`teamcity` CLI command with the agent token:
```bash
TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity <command>
```

For example:
```bash
TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run start Build_kRPC_All --branch fix/KRPC-540
TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run log <build-id> --failed
```

This applies to **every** `teamcity` invocation in this workflow — no exceptions.

### Pre-flight Access Check — MANDATORY

Before doing any work, verify that **all three services** are accessible. Run these
checks in parallel and report results:

1. **YouTrack**: Call `mcp__youtrack_agent__get_current_user` to confirm the agent MCP
   server responds. If MCP fails, try the REST fallback:
   ```bash
   curl -sf -H "Authorization: Bearer $YOUTRACK_AGENT_TOKEN" \
     "https://youtrack.jetbrains.com/api/users/me?fields=login,name"
   ```
   If both fail → **stop and report**.

2. **TeamCity**: Run a simple read-only command:
   ```bash
   TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity project list --limit 1
   ```
   If this fails → **stop and report**.

3. **GitHub**: Run:
   ```bash
   gh auth status
   ```
   If this fails → **stop and report**.

If **any** check fails, tell the user which service is unreachable and what error was
returned. Do NOT proceed with any work — not even reading the issue. The entire workflow
depends on all three services being available.

Only after all three checks pass, continue to the Input phase.

### No silent fallback policy

If a service becomes unreachable **during** the workflow (not just at pre-flight):
- YouTrack MCP → REST API fallback via `$YOUTRACK_AGENT_TOKEN` is permitted
- If REST also fails → **stop and report**
- TeamCity CLI fails → **stop and report** (no fallback)
- GitHub CLI fails → **stop and report** (no fallback)

Never silently skip a step because a service is down. Never invent data or assume
success. If you cannot complete a phase because a service is unreachable, stop the
workflow, tell the user exactly what failed and at which phase, and wait for instructions.

---

## Input

**Required**: A YouTrack issue ID (e.g., `KRPC-540`).

**Alternative**: The user asks to scan for available issues. In this case:
1. Search for issues tagged `UFG Claude` that are unassigned or assigned to the AI agent:
   ```
   project: KRPC tag: {UFG Claude} State: Open sort by: Priority asc, created asc
   ```
2. Then also check `Submitted` state:
   ```
   project: KRPC tag: {UFG Claude} State: Submitted sort by: Priority asc, created asc
   ```
3. Prioritize `Open` over `Submitted` issues, and higher priority issues first.
4. Present the candidates to the user and let them pick, or take the top one if they
   said to just go ahead.

## Phase 1: Claim the Issue

Read the issue with `mcp__youtrack_agent__get_issue` to understand it fully — summary,
description, comments, tags, custom fields. Then update the issue:

1. **Assign** to `AI Agent [Kxrpc]` using `mcp__youtrack_agent__change_issue_assignee`.
   First, find the exact login with `mcp__youtrack_agent__find_user` searching for "AI Agent"
   to get the correct login string. Cache this for the session — you only need to look
   it up once.

2. **Set state** to `In progress` using `mcp__youtrack_agent__update_issue`:
   ```
   customFields: {"State": "In progress"}
   ```

3. **Set sprint** (Planning Period) to the current active sprint if the issue doesn't
   already have one. To find the current sprint, use `mcp__youtrack_agent__get_issue_fields_schema`
   and look at the `[Kxrpc] Planning Period` field — the last non-checkmarked period
   is typically the active one.

## Phase 2: Analyze the Problem

Read the issue thoroughly. Understand:
- What exactly is broken or missing?
- Which modules/files are likely involved? (Use the Module Map from CLAUDE.md)
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
- If not reproducing: explain why a reproducer isn't needed (e.g., "This is a feature
  request — no reproducer needed" or "The bug is clearly visible in `FooClass.kt:42`
  where X is null-checked but Y is not")

Use `mcp__youtrack_agent__add_issue_comment` for this.

## Phase 3: Set Up the Worktree

Always work in an isolated git worktree to avoid disrupting the user's working directory.

1. Make sure main is up to date:
   ```bash
   git fetch origin main
   ```

2. Create a branch name from the issue ID:
   - Format: `fix/KRPC-<number>` for bugs, `feat/KRPC-<number>` for features,
     `task/KRPC-<number>` for tasks
   - Example: `fix/KRPC-540`

3. Create the worktree:
   ```bash
   git worktree add -b <branch-name> /tmp/krpc-<issue-id> origin/main
   ```

4. All subsequent file operations and Gradle commands must use the worktree path
   as the working directory / project root.

**Important**: When using Gradle skills in the worktree, always specify the worktree
path as `projectRoot`. For shell commands, `cd` into the worktree first.

## Phase 4: Write a Failing Test (if applicable)

Not every issue needs a new test. Skip this phase when:
- The issue is purely about documentation
- The issue is about build/infra configuration
- The existing test suite already covers the scenario (run the existing tests to confirm
  they fail as expected)
- The fix is trivial and obviously correct (e.g., fixing a typo in an error message)

When writing a test:
1. Identify the correct test module from the Module Map (e.g., `:tests:compiler-plugin-tests`,
   `:tests:krpc-compatibility-tests`, or a module-level test like `:krpc:krpc-client:jvmTest`)
2. Write a test that **fails** with the current code and will **pass** after the fix
3. Run it using the `running_gradle_tests` skill to confirm it fails
4. Use existing test patterns from the same test module as a guide — don't invent
   new testing patterns when established ones exist

## Phase 5: Apply the Fix

### Always plan before coding

Every fix needs a plan — the depth scales with complexity:

**Simple fixes** (single file, obvious change — e.g., null check, typo, config tweak):
A brief inline plan is fine — a few bullet points of what you'll change and why.

**Medium fixes** (2-5 files, clear approach but multiple steps):
Use the Plan agent to produce a structured plan: which files to change, in what order,
what the expected behavior change is, and how you'll verify it. Then start executing
immediately — do not wait for user approval.

**Complex fixes** (architectural changes, multiple modules, public API changes, unclear
tradeoffs, or anything where a wrong approach would be costly to undo):
1. Use the Plan agent to produce a detailed plan covering:
   - Root cause analysis
   - Proposed approach and alternatives considered
   - Files to change with expected modifications
   - Risk areas (backward compat, concurrency, performance)
   - Verification strategy
2. **Spawn a separate review agent** to critique the plan before you start coding.
   Give it the issue description, your plan, and the relevant source files. Ask it
   to check for:
   - Missed edge cases or failure modes
   - Simpler alternatives you may have overlooked
   - Risks to binary compatibility or wire format
   - Whether the plan follows the project's architectural patterns
3. Incorporate the reviewer's feedback into the plan.
4. Then start executing — still do not wait for user approval. The plan review is
   for correctness, not permission.

### Post the final plan to YouTrack

After the plan is finalized (including reviewer feedback for complex fixes), post it
to the issue so there's a record of the approach taken:

**Small/medium plans** (fits comfortably in a few paragraphs): Post directly as a
YouTrack comment using `mcp__youtrack_agent__add_issue_comment`. Example:

```markdown
## Fix plan

- Root cause: `StreamHandler` doesn't check for null payload in `onNext()`
- Fix: Add null guard in `StreamHandler.onNext()`, propagate `IllegalArgumentException`
- Test: Add test case in `StreamHandlerTest` for null payload scenario
- Files: `krpc-client/src/.../StreamHandler.kt`, `krpc-client/src/.../StreamHandlerTest.kt`
```

**Large/complex plans** (multi-module, detailed alternatives analysis, long risk
assessment): Post a TL;DR comment and attach the full plan as a `.md` file:

1. Write the full plan to a temporary file (e.g., `/tmp/KRPC-<number>-plan.md`)
2. Attach it to the issue via REST API:
   ```bash
   curl -s -H "Authorization: Bearer $YOUTRACK_AGENT_TOKEN" \
     -F "file=@/tmp/KRPC-<number>-plan.md" \
     "https://youtrack.jetbrains.com/api/issues/KRPC-<number>/attachments?fields=id,name"
   ```
3. Post a TL;DR comment referencing the attachment:
   ```markdown
   ## Fix plan (TL;DR)

   Full plan attached as `KRPC-<number>-plan.md`.

   **Approach**: <1-2 sentence summary>
   **Key changes**: <bullet list of affected modules>
   **Risks**: <main risk in one line>
   ```

When in doubt about complexity, err toward more planning — a few minutes spent on a
plan is cheaper than reworking a bad approach.

### Coding guidelines

- Follow all conventions in CLAUDE.md (explicit return types, visibility, KDoc on public APIs,
  `@InternalRpcApi` for cross-module internals, dependencies in version catalog, etc.)
- Keep changes minimal and focused on the issue — don't refactor surrounding code
- If the fix touches public API, you'll need to update ABI dumps later (Phase 7 handles this)

### Run the fix test

After applying the fix, run the test from Phase 4 (if you wrote one) to confirm it passes.
Also run any other directly related tests for the modules you touched.

## Phase 6: Check Documentation

After the code fix is complete, evaluate whether docs need updating:

- Did you change public API behavior? → Update KDoc and possibly Writerside topics
- Did you add new public API? → Add KDoc (required for all public types)
- Did you change configuration or setup steps? → Update relevant Writerside topics
- Did you deprecate anything? → Update migration guide if one exists

Use the `update-doc` skill's conventions when making doc changes. Documentation lives in
`docs/pages/kotlinx-rpc/topics/`.

## Phase 7: Run Local Verifications

Before committing, run the verifications relevant to your changes. Use the
`run-local-verifications` skill's decision table to determine which checks apply.

At minimum:
- If you touched published module source: `checkLegacyAbi`, `detekt`, `:jpms-check:compileJava`
- If ABI check fails: run `updateLegacyAbi` and include the updated dumps
- If you touched `compiler-plugin/`: `:tests:compiler-plugin-tests:test` and use the
  `verify-compiler-plugin-compatibility` skill for multi-Kotlin-version checks
- If you touched `krpc/`: protocol and API compatibility tests
- If you touched `protoc-gen/`: conformance, unit tests, WKT, implicit imports

## Phase 8: Code Review

Before committing, run an internal code review using **at least two independent agents
in parallel**. Each agent focuses on a specific concern and reviews the full diff of your
changes (use `git diff` in the worktree to collect it). The agents should not see each
other's feedback — they review independently, then you synthesize.

### Choosing reviewers

Pick at least 2 review agents based on what the change actually touches. Use your judgment —
the list below is a menu of available perspectives, not a checklist. For a small bug fix
in one file you might only need two. For a cross-cutting change you might want four or five.

| Agent | Good for | What it checks |
|---|---|---|
| **Kotlin style** | Any Kotlin source change | Naming conventions, explicit return types and visibility on public APIs, proper use of `@InternalRpcApi` for cross-module internals, KDoc on all public types, no inline dependency versions, OS-agnostic paths, unnecessary complexity, dead code |
| **Concurrency & coroutines** | Code with coroutines, flows, shared state | Correct coroutine scope usage, no `GlobalScope`, proper cancellation handling, `Flow` collection patterns, `Mutex` vs `synchronized`, dispatcher choices, suspend function contracts, no blocking calls on coroutine dispatchers |
| **Public API** | Changes to public API in published modules (`core/`, `krpc/`, `grpc/`, `protobuf/`, `utils/`) | Binary compatibility implications, `@Deprecated` with proper `ReplaceWith` and level, `@ExperimentalRpcApi` on new APIs that may change, parameter naming consistency with existing API surface, no accidental exposure of internal types |
| **Gradle expert** | Build files, Gradle conventions, module configuration | Use the `gradle_expert` skill — correct Kotlin DSL patterns, version catalog usage, convention plugin alignment, proper `includePublic()` vs `include()` |
| **Serialization** | Serialization, wire format, protobuf code | Backward compatibility of serialized formats, proper `@Serializable` usage, schema evolution safety, protobuf field numbering rules |
| **Security** | Auth, transport, input handling | No injection vectors, proper input validation at system boundaries, secure defaults, no credential leakage |

You can also spawn reviewers not on this list if the change warrants it (e.g., a
documentation reviewer for large doc changes, or a performance reviewer for hot-path
optimizations). The point is independent eyes on the diff, not rigid process.

### How to run the review

1. Collect the diff:
   ```bash
   cd <worktree-path>
   git diff HEAD
   ```
   (If some changes are already staged, use `git diff` + `git diff --staged` to capture everything.)

2. Spawn your chosen agents in parallel, each with a prompt like:
   > You are reviewing a code change for the kotlinx-rpc library. Focus ONLY on
   > **[your focus area]**. Review the diff below and report issues as a list.
   > For each issue: file path, line, severity (error/warning/nit), and explanation.
   > If you find no issues in your area, say so explicitly.
   >
   > <the diff>

3. Collect all agent responses. For each issue reported:
   - **Error**: Must fix before committing. Apply the fix.
   - **Warning**: Fix unless you have a good reason not to. If skipping, note why.
   - **Nit**: Fix if trivial, skip if it would bloat the diff.

4. If fixes were applied, re-run the relevant tests from Phase 7 to make sure
   nothing broke.

5. If two reviewers flag the same area with conflicting advice, use your judgment
   and follow repo conventions as the tiebreaker.

### Post unaddressed issues to the PR

After committing (Phase 9) and creating the PR (Phase 10), collect all review issues
that were **not fixed** (skipped warnings and nits) and post them as a single PR comment
with checkboxes. This ensures nothing is lost and gives the user control over what to
address later.

Format the comment like this:
```markdown
## Internal code review — unaddressed items

The following issues were flagged during the automated code review but not fixed in this PR.
Check the ones you'd like me to address:

**Kotlin style**
- [ ] `path/to/File.kt:42` — (warning) Missing KDoc on public `FooBar` class
- [ ] `path/to/File.kt:87` — (nit) Could use `also` instead of explicit variable

**Concurrency**
- [ ] `path/to/Other.kt:15` — (nit) Consider using `Dispatchers.Default` instead of `Dispatchers.IO` for CPU-bound work
```

Post this using `gh pr comment <pr-number> --body "..."`. Group items by reviewer agent.
If all issues were fixed, skip this step — no comment needed.

## Phase 9: Commit Changes

Separate commits logically. Each category gets its own commit:

1. **Fix commit**: The actual code fix. Message follows `commit-changes` skill conventions.
   Include the YouTrack ticket ID. Example:
   ```
   KRPC-540 Fix null pointer in kRPC client stream handling
   ```

2. **Test commit** (if tests were added/modified):
   ```
   KRPC-540 Add test for client stream null handling
   ```

3. **Docs commit** (if documentation was updated):
   ```
   KRPC-540 Update streaming documentation for null handling
   ```

4. **Other commits** (ABI dumps, generated files, infra changes — anything that doesn't
   fit the above three categories):
   ```
   KRPC-540 Update ABI dumps
   ```

Use the `commit-changes` skill conventions for all messages: imperative mood, no period,
module prefix when scoped, under 72 characters.

If the YouTrack ticket references a GitHub issue, include the GH issue number too:
```
KRPC-540 Fix null pointer in kRPC client stream handling (#123)
```

## Phase 10: Create a Draft PR

1. Push the branch:
   ```bash
   git push -u origin <branch-name>
   ```

2. **Check for linked GitHub issue** before creating the PR: If the YouTrack ticket
   has a `github-issue` tag, find the original GitHub issue number (it's usually in
   the YT description or comments as a URL like `https://github.com/.../issues/NNN`).
   You'll include it in the PR body below.

3. Create a draft PR using `gh`. If there's a linked GitHub issue, include `Fixes #NNN`
   in the body — GitHub has no public API for the Development sidebar, so closing
   keywords in the PR body are the programmatic way to link and auto-close on merge:
   ```bash
   gh pr create --draft --title "<concise title>" --body "$(cat <<'EOF'
   **Subsystem**
   <affected modules/subsystems>

   **Problem Description**
   Fixes https://youtrack.jetbrains.com/issue/KRPC-<number>
   Fixes #<github-issue-number>  ← only if a linked GH issue exists, remove otherwise

   **Solution**
   <brief explanation of the fix approach — don't duplicate the issue description,
   just explain what was done and why this approach was chosen>

   ---
   *This PR was created by an AI agent.*
   EOF
   )"
   ```

4. **Add a label** based on the issue type. Use `gh pr edit <number> --add-label <label>`:
   - Bug → `bug`
   - Feature → `feature`
   - Task → `housekeeping` (or `infra` if it's infrastructure)
   - Usability Problem → `bug`
   - Performance Problem → `bug`
   - Documentation-only → `docs`
   - If the fix includes breaking changes → also add `breaking`
   - If the fix includes deprecations → also add `deprecation`

   At least one label from this set is **required**: `feature`, `bug`, `breaking`, `infra`,
   `docs`, `deprecation`, `release`, `housekeeping`, `dependencies`.
   Draft PRs skip the label check, but add it now so it's ready when the draft is removed.

## Phase 11: Run CI Checks (TeamCity + GitHub Actions in parallel)

Both TeamCity and GitHub Actions must pass. GH Actions trigger automatically on push;
TeamCity you trigger manually. **Monitor both in parallel** — don't wait for one to
finish before checking the other.

### TeamCity

Use the `use-teamcity` skill to trigger CI builds. Use your judgment on what to run —
maybe a single targeted build, maybe several, maybe none if the change is trivial and
local verifications already covered it (e.g., a docs-only or typo fix).

Reference for picking configurations:

| What changed | Suggested build |
|---|---|
| Core / kRPC / gRPC modules (JVM) | `Build_kRPC_Java_JS_WASM_and_Linux` |
| Apple-specific targets | `Build_kRPC_Apple` |
| Windows-specific targets | `Build_kRPC_Windows` |
| Compiler plugin | `Build_kRPC_All_Compiler_Plugins` |
| Gradle plugin | `Build_kRPC_All_Gradle_Plugin` |
| ABI / Detekt / JPMS / Artifacts | `Build_Checks_All` |
| Broad changes or unsure | `Build_kRPC_All` (runs everything) |

Trigger the build (remember the token prefix from the Authentication section):
```bash
TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run start <build-id> --branch <branch-name>
```

### GitHub Actions

GH Actions checks run automatically on push. The label check is skipped for drafts and
activates when you remove draft status — make sure the label is already set (Phase 10,
step 3).

### Monitor both simultaneously

After triggering TC and pushing (which starts GH Actions), check both:
```bash
# GH Actions — run in background
gh pr checks <pr-number> --watch
```
```bash
# TeamCity — check status
TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run view <build-id>
```

If either fails:
1. Investigate: `TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run log <build-id> --failed` for TC, `gh pr checks` for GH Actions
2. Fix the issue in the worktree
3. Commit the fix, push, and re-trigger TC if needed (GH Actions restart on push)

Iterate until both pass. If a failure is clearly unrelated to your changes (flaky test,
infra issue), note it in the PR description and proceed.

## Phase 12: Finalize the PR

Once all CI checks pass:

1. **Rebase on latest main** to ensure no conflicts:
   ```bash
   cd <worktree-path>
   git fetch origin main
   git rebase origin/main
   git push --force-with-lease
   ```

2. **Remove draft status**:
   ```bash
   gh pr ready <pr-number>
   ```

3. **Add reviewers**:
   ```bash
   gh pr edit <pr-number> --add-reviewer Mr3zee
   ```

## Phase 13: Update YouTrack

Set the issue state to `Fixed in Branch`:
```
mcp__youtrack_agent__update_issue(issueId: "KRPC-<number>", customFields: {"State": "Fixed in Branch"})
```

## Phase 14: Done

Stop here. Do NOT wait for PR review comments. Report to the user:
- The PR URL
- The YouTrack issue status
- A brief summary of what was fixed and how

Do NOT poll for comments or wait for review feedback.

## Addressing PR Comments (on user request)

This may take **multiple turns**. The user might ask you to address comments, you fix
some, then they come back later with more feedback or check more boxes. Each turn
follows the same process below.

### Each turn

1. **Read all PR comments**, including the internal code review checkbox comment:
   ```bash
   gh api repos/{owner}/{repo}/pulls/{pr-number}/comments
   gh api repos/{owner}/{repo}/issues/{pr-number}/comments
   ```

2. **Check the internal review comment** for checked checkboxes. Parse the markdown:
   - `- [x]` = user wants this fixed
   - `- [ ]` = user chose to skip for now (may check later in a future turn)

3. **Read human reviewer comments** (from @Mr3zee or others). These take priority
   over the internal review items.

4. Fix all:
   - Human reviewer comments (all of them, unless explicitly marked as optional)
   - Checked checkboxes from the internal review comment

5. Re-run relevant tests and verifications for the new changes.

6. Commit the fixes (separate commit: `KRPC-<number> Address PR review comments`).

7. Push and re-trigger CI if needed.

8. **Update the internal review comment** to reflect current state. Edit the comment
   body using `gh api` — restructure it into two sections:

   ```markdown
   ## Internal code review — unaddressed items

   Check the ones you'd like me to address:

   **Kotlin style**
   - [ ] `path/to/File.kt:87` — (nit) Could use `also` instead of explicit variable

   ---

   ### Fixed
   - ~~`path/to/File.kt:42` — (warning) Missing KDoc on public `FooBar` class~~ (fixed in <commit-sha>)
   ```

   Move fixed items to the "Fixed" section with strikethrough and the commit SHA.
   Leave unchecked items in the top section so the user can check them in a future
   turn if they change their mind. This keeps the comment a living document across
   multiple review rounds.

## Rebase Discipline

Throughout the entire workflow, keep the branch rebased on the latest `main`. Specifically:
- Before starting work (Phase 3 — the worktree is created from `origin/main`)
- Before pushing for CI (Phase 11)
- Before finalizing the PR (Phase 12)

Always use `git rebase origin/main` followed by `git push --force-with-lease`.

## Skill Dependencies

This skill orchestrates many other skills. Use them as documented, **but with the
authentication overrides from the Authentication section above**:

| Task | Skill to use | Auth override |
|---|---|---|
| Read/update YouTrack issues | `use-youtrack` | Use `mcp__youtrack_agent__*` tools and `YOUTRACK_AGENT_TOKEN` instead of defaults |
| Run Gradle builds | `running_gradle_builds` | None |
| Run Gradle tests | `running_gradle_tests` | None |
| Write commit messages | `commit-changes` | None |
| Run local verifications | `run-local-verifications` | None |
| Update documentation | `update-doc` | None |
| Trigger CI builds | `use-teamcity` | Prefix all `teamcity` CLI with `TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN`, never auth check |
| Compiler plugin compat | `verify-compiler-plugin-compatibility` | None |

When referencing `use-youtrack` or `use-teamcity` skill conventions (query syntax,
build IDs, etc.), follow their guidance for **everything except authentication**.
The auth instructions in this skill override those skills' defaults.

## Error Recovery

- **Build fails locally**: Read the error, fix the code, re-run. Don't just retry.
- **TC build fails**: Investigate with `TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run log <id> --failed`, fix and push.
- **Rebase conflicts**: Resolve them. If the conflict is complex, report to the user.
- **YouTrack MCP fails**: Fall back to REST API via `$YOUTRACK_AGENT_TOKEN`. If REST also fails → **stop and report to the user**.
- **TeamCity CLI fails**: **Stop and report to the user** — no fallback available.
- **GitHub CLI fails**: **Stop and report to the user** — no fallback available.
- **Worktree issues**: If the worktree gets into a bad state, remove it with
  `git worktree remove <path>` and recreate from Phase 3.

## Progress Communication

At each phase completion, briefly tell the user what happened:
- "Claimed KRPC-540, set to In Progress"
- "Reproduced the bug — null pointer when streaming with empty payload"
- "Fix applied, test passing"
- "Code review done — 2 agents, 1 warning fixed"
- "PR #42 created as draft"
- "TC build passing, marking PR ready and adding reviewers"
- "Done — PR: <url>, YT status: Fixed in Branch"

Keep it concise. The user can check the PR and issue for details.
