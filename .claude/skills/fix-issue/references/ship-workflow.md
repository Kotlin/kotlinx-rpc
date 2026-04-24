# Ship Workflow (Phase D)

Entry condition: Phase C complete — all local verifications pass, code review
errors fixed, tests confirmed green after review fixes.

## D.1: Commit Changes

Separate commits **logically** — what matters is that each commit tells a coherent
story, not that categories are mechanically split:
- **Default split**: fix (source), tests, docs, generated/infra — when each is
  independently meaningful.
- **Single commit preferred** for atomic refactorings where all changes form one
  logical unit (e.g., package relocation touching 34 files). Splitting would create
  noise without aiding review.
- **Always separate** generated files / ABI dumps from human-authored code.
  Reviewers need to see the fix isolated from mechanical regeneration output.

**Never hand-edit generated files** (`.api`, `.klib.api`, conformance code, WKT,
etc.) — always run the appropriate regeneration task and commit the output as-is.
The klib dump sort order, for instance, differs from what's intuitive; manual edits
will produce wrong output.

### Commit message format

Follow the `commit-changes` skill. Short version: single line, no body, starts
with the YouTrack ticket ID in the exact form required by
`.github/workflows/bot-pr-title.yml` (regex `^KRPC-[0-9]+: .+`) — so
`KRPC-NNN:` with a colon and a single space. Imperative mood, present tense,
no trailing period, keep under ~72 characters. If the YT ticket references an
original GitHub issue, include that issue number in parentheses at the end;
do not add a PR number — squash-merge appends one automatically.

```
KRPC-NNN: <Imperative verb> <what changed> (#GH-issue if exists)
```

Message examples:
- `KRPC-123: Add DSL builder for ProtoConfig`
- `KRPC-245: Strip common enum value prefixes (#12345)`

## D.2: Create a Draft PR

Every `gh` invocation in this phase goes through the `gh-bot` wrapper
(`.claude/skills/fix-issue/scripts/gh-bot.sh`). See SKILL.md § "Using `gh` —
always through the `gh-bot` wrapper" for why — short version: a bare `gh`
call in a fresh Bash shell silently authenticates as the human user.

1. Push: `git push -u origin <branch-name>`
2. Create draft PR with `gh-bot.sh pr create --draft`. Use the template in
   `assets/gh-pr-body.md` — the PR body contains only the **solution**, not problem
   analysis or root cause (those belong in the YT issue). Include `Fixes #NNN` if a
   linked GH issue was found in Phase A (A.1).
3. Add label. At least one label must be set.
4. Post any skipped review warnings/nits as a checkbox comment on the PR
   (see `assets/gh-code-review-comment.md`). If all review issues were fixed,
   don't skip the comment, just post that all internal comments were addressed.
5. **Identity assertion** — immediately after PR creation, confirm the PR
   was authored by the bot, not the human user:

   ```bash
   .claude/skills/fix-issue/scripts/gh-bot.sh pr view <pr-number> \
     --json author --jq '.author.login'
   ```

   Expected: the bot login (e.g. `ai-agent-kxrpc[bot]`). If the result is
   a human login, the PR was created under the wrong identity — delete it
   and redo using `gh-bot.sh`. Do not paper over it.

## D.3: Run CI Checks (TeamCity + GitHub Actions in parallel)

Both must pass. GH Actions trigger on push; TeamCity you trigger manually.

**TeamCity**: Use `use-teamcity` skill. Pick builds based on what changed — consult
the decision table in `use-teamcity/references/remote-verification-table.md` to select the
minimal set of targeted builds. Never use `_All` composites. May skip TC entirely
if the change is trivial and local verifications covered it.

**GH Actions**: Are run automatically.

**Run both polls in parallel via background `Bash`**: in a single message,
launch `poll-tc-builds.sh` and `poll-gh-actions.sh` as two `Bash` calls with
`run_in_background: true`. Each returns a shell ID and runs up to 30 min; the
runtime notifies you on completion and you read the final stdout directly. No
subagents — the scripts' output is bounded and the main agent reads it
reliably. See `references/ci-polling.md` for script usage, auth token
requirements, and the `__POLL_RESULT__` sentinel format.

See Error Recovery in SKILL.md for handling canceled or failed builds.

### D.3.1: Build status decision table

Before investigating a failure, classify it. The same investigation procedure
applies to most statuses, but the **retry vs. move-on vs. debug** decision
depends on whether the failure is pre-existing or introduced by this PR.

| Status   | Same test failing each run? | Fails on `main` with same symptom? | Action |
|----------|-----------------------------|------------------------------------|--------|
| canceled | n/a | n/a | Retry once (external cancellation is common) |
| failed   | no — different test each run | yes | **Pre-existing flake.** Retry up to 3× for a green run; if none, note in CI report comment and proceed — do not revert bundled changes |
| failed   | yes — same test each run | no  | **Likely regression introduced by this PR.** Invoke `superpowers:systematic-debugging`; do not retry blindly |
| failed   | yes | yes | **Pre-existing broken test.** Note in CI report comment and proceed |

On failure — **always invoke** `superpowers:systematic-debugging` (via the Skill tool —
not just reading the log and guessing), not blind retries:
1. Read the full error/stack trace, not just the test name.
2. Classify via the D.3.1 decision table above. **If TC fails on a task that
   also fails on main**, you may fix it if the fix is trivial and within scope
   of this issue — otherwise note it as pre-existing in the CI report comment
   and move on. Do not block the PR on failures unrelated to your change.
3. Reproduce locally if feasible via `running_gradle_tests` — faster than CI round-trips.
4. Form a hypothesis before fixing. If your first fix doesn't work, return to the
   error output — do not stack guesses.
5. Fix, commit, push. GH Actions restart automatically; re-trigger TC if needed.
   Re-launch the two poll scripts as background `Bash` calls (see D.3 above).

**Post a CI report comment on the PR** — this is mandatory, not optional. The
reviewer needs to see pipeline status directly on the PR. Use the template in
`assets/gh-ci-report-comment.md`. Every pipeline gets a row.
Failed ones that weren't retried get an explanation
of why the agent chose not to retry. Update this comment (don't post a new one) if
CI is re-triggered after fixes. Do not just report CI results in the conversation —
the PR comment is the deliverable.

## D.4: Finalize the PR

Once all CI passes:
1. Rebase on latest main: `git fetch origin main && git rebase origin/main` +
   `git push --force-with-lease`
2. Remove draft: `gh-bot.sh pr ready <pr-number>`
3. Add reviewer: `gh-bot.sh pr edit <pr-number> --add-reviewer Mr3zee`
4. Verify: `gh-bot.sh pr view <pr-number> --json isDraft,reviewRequests,headRefOid` —
   confirm draft removed, reviewer assigned, and head SHA matches local HEAD.

### If step 3 returns `422 Review cannot be requested from pull request author`

This error does **not** mean the App installer is being treated as the
"author" for review-request purposes. It means the authenticating token
belongs to the user you are trying to add as reviewer — GitHub's plain
self-review rejection, disguised as an authorship rule. Its presence here is
the telltale that `gh` was invoked as a human user, not as the App.

Diagnose and recover:

```bash
.claude/skills/fix-issue/scripts/gh-bot.sh api user --jq .login
```

- `403 Resource not accessible by integration` — gh-bot is authenticating
  as the App (App installation tokens cannot hit `/user`). Then the 422 is
  unexpected and worth investigating separately.
- A human login (e.g. `Mr3zee`) — you ran a bare `gh` somewhere, bypassing
  the wrapper. Find the offending call, switch it to `gh-bot.sh`, and redo
  any state-changing operations it performed (PR create, comments, ready,
  edit). Do not just retry the `--add-reviewer` call against a broken-state
  PR.

## D.5: Update YouTrack

1. **Set state** to `Fixed in Branch` via `youtrack-agent`.
2. **Update the issue body** — if you uncovered new information about the issue during
   the fix (e.g., deeper root cause, additional affected modules, edge cases found
   during testing), append those findings to the Agent Analysis section from Phase A (A.2).
   Do not duplicate the fix summary or solution here — those belong in the PR body.
   If nothing new was discovered, skip the body update.

## D.6: Verify Completion

Before reporting completion, **always invoke** `superpowers:verification-before-completion`
(via the Skill tool — not just manually checking). Verify actual state, do not
assume previous phases succeeded:

1. `gh-bot.sh pr view <pr-number> --json isDraft,reviewRequests,author` —
   draft removed, reviewer assigned, and `author.login` is the bot (not a
   human user).
2. Verify CI report comment exists on the PR.
3. Verify YT issue state is `Fixed in Branch` via `youtrack-agent`.

If any check fails, fix it before reporting. Then report:
PR URL, YT status, brief fix summary, worktree path.

Do NOT wait for review feedback in the session, feedback would be posted in the PR,
and you will be notified by a user.

**Exit condition**: PR is not draft, reviewer assigned, CI green, YT issue set to
"Fixed in Branch", completion verification passed.
