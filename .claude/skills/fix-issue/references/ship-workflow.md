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

**Subject line only — no body.** Keep under 72 characters. Include the YT ticket ID
and GH issue number (if the YT ticket references one). Use imperative mood, present
tense, no trailing period.

```
KRPC-NNN: <Imperative verb> <what changed> (#GH-issue if exists)
```

Message examples:
- `KRPC-123 Add DSL builder for ProtoConfig`
- `KRPC-245: Strip common enum value prefixes (#12345)`

## D.2: Create a Draft PR

1. Push: `git push -u origin <branch-name>`
2. Create draft PR with `gh pr create --draft`. Use the template in
   `assets/gh-pr-body.md` — the PR body contains only the **solution**, not problem
   analysis or root cause (those belong in the YT issue). Include `Fixes #NNN` if a
   linked GH issue was found in Phase A (A.1).
3. Add label. At least one label must be set.
4. Post any skipped review warnings/nits as a checkbox comment on the PR
   (see `assets/gh-code-review-comment.md`). If all review issues were fixed,
   don't skip the comment, just post that all internal comments were addressed.

## D.3: Run CI Checks (TeamCity + GitHub Actions in parallel)

Both must pass. GH Actions trigger on push; TeamCity you trigger manually.

**TeamCity**: Use `use-teamcity` skill. Pick builds based on what changed — consult
the decision table in `use-teamcity/remote-verification-table.md` to select the
minimal set of targeted builds. Never use `_All` composites. May skip TC entirely
if the change is trivial and local verifications covered it.

**GH Actions**: Are run automatically.

**Monitor both via subagents**: Spawn two background subagents — one for GitHub
Actions, one for TeamCity. Each polls its CI system and reports back with
pass/fail results. See `references/ci-polling.md` for concrete poll loops,
auth token usage, interval/timeout guidelines, and expected report format.

See Error Recovery in SKILL.md for handling canceled or failed builds.

On failure — **always invoke** `superpowers:systematic-debugging` (via the Skill tool —
not just reading the log and guessing), not blind retries:
1. Read the full error/stack trace, not just the test name.
2. Check if pre-existing — run the same test against `main` (or check recent TC
   history). **If TC fails on a task that also fails on main**, you may fix it if the
   fix is trivial and within scope of this issue — otherwise note it as pre-existing
   in the CI report comment and move on. Do not block the PR on failures unrelated
   to your change.
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

## D.4: Finalize the PR

Once all CI passes:
1. Rebase on latest main: `git fetch origin main && git rebase origin/main` +
   `git push --force-with-lease`
2. Remove draft: `gh pr ready <pr-number>`
3. Add reviewer: `gh pr edit <pr-number> --add-reviewer Mr3zee`
4. Verify: `gh pr view <pr-number> --json isDraft,reviewRequests,headRefOid` — confirm
   draft removed, reviewer assigned, and head SHA matches local HEAD.

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

1. `gh pr view <pr-number> --json isDraft,reviewRequests` — draft removed, reviewer assigned
2. Verify CI report comment exists on the PR
3. Verify YT issue state is `Fixed in Branch` via `youtrack-agent`

If any check fails, fix it before reporting. Then report:
PR URL, YT status, brief fix summary, worktree path.

Do NOT wait for review feedback in the session, feedback would be posted in the PR,
and you will be notified by a user.

**Exit condition**: PR is not draft, reviewer assigned, CI green, YT issue set to
"Fixed in Branch", completion verification passed.
