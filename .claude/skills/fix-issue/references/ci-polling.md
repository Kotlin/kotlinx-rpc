# CI Polling

Concrete patterns for the two CI polls (TeamCity + GitHub Actions) that run
during Phase D.3. Run both in parallel as background `Bash` calls — no
subagents, no hand-rolled loops.

## First decision — have you already triggered the builds?

Work out which path applies **before** doing anything else. The rest of this
doc depends on it.

- **You have not queued any builds yet** (no `teamcity run start` was called
  during this turn) → use **trigger + poll** mode. The script queues the
  builds and polls them.
- **Builds are already queued** — you (or another agent) ran
  `teamcity run start`, or TC shows queued builds for the branch at your
  HEAD SHA → use **attach + poll** mode. Pass the existing build IDs and
  the script joins them in progress.

Do not mix: never queue some builds manually and then trigger the rest from
the script. Pick one mode per run.

## Never roll your own poll loop

The only supported way to monitor CI in this skill is the polling scripts
in `scripts/`. Do not write a shell loop around `teamcity run view` or
`gh pr checks` — the scripts exist specifically because the ad-hoc approach
keeps breaking.

If the script is missing or errors in a way you cannot explain, stop and
report to the user — do not substitute a hand-rolled equivalent.

Single one-shot reads are fine (`teamcity run view <id>` once, or
`gh-bot.sh pr checks <pr>` once) when you need a point-in-time status check
outside of a polling run. "Loop" is the forbidden thing. Subagents for polling 
are also forbidden.

## Run both polls in parallel as background Bash

The scripts cap at 30 minutes (20 iterations × 90 s). The `Bash` tool's
blocking `timeout` maxes out at 10 minutes, so a blocking foreground call
won't wait a slow CI run out. The right primitive is `Bash` with
`run_in_background: true` — the call returns a shell ID immediately, the
main agent can continue working (or wait), and the runtime notifies the
main agent automatically when the script exits.

### How to launch

In a single message, dispatch both polls in parallel:

```
Bash(
  command="TEAMCITY_AGENT_TOKEN=$TEAMCITY_AGENT_TOKEN \
    .claude/skills/fix-issue/scripts/poll-tc-builds.sh --attach 53149 53150",
  run_in_background=true,
  description="Poll TeamCity builds"
)

Bash(
  command=".claude/skills/fix-issue/scripts/poll-gh-actions.sh <pr-number>",
  run_in_background=true,
  description="Poll GitHub Actions checks"
)
```

Each returns a shell ID. Do **not** sleep, do **not** re-check early — the
runtime delivers a completion notification when each script exits. That
notification is your cue to read the final stdout.

### Reading the result

When a background shell completes, read its stdout and look for the final
`__POLL_RESULT__` line, which the scripts emit on every exit path
(SUCCESS / FAILURE / TIMEOUT). That single line carries the status and
exit code — everything else in the stdout is human-legible detail (per-build
status, failure log excerpts) that you relay into the CI report comment.

Treat absence of `__POLL_RESULT__` as script failure.

## TeamCity polling

Run from the worktree root.

**Trigger + poll (builds not yet queued):**

```bash
TEAMCITY_AGENT_TOKEN=$TEAMCITY_AGENT_TOKEN \
  .claude/skills/fix-issue/scripts/poll-tc-builds.sh \
  <branch-name> <build-config-id> [<build-config-id> ...]
```

**Attach + poll (builds already queued):**

```bash
TEAMCITY_AGENT_TOKEN=$TEAMCITY_AGENT_TOKEN \
  .claude/skills/fix-issue/scripts/poll-tc-builds.sh \
  --attach <build-id> [<build-id> ...]
```

Exit codes (same for both modes):

- `0` — all builds succeeded
- `1` — at least one build failed (the script fetches
  `teamcity run log <id> --failed` output for each failed build and includes
  it in the report)
- `2` — timeout (some builds still running after 30 min)

Report: relay the script's structured report into the CI report comment —
per-build status plus the final `__POLL_RESULT__` line.

## GitHub Actions polling

GH Actions start automatically on push; there is no trigger step. Run the
script from the worktree root:

```bash
.claude/skills/fix-issue/scripts/poll-gh-actions.sh <pr-number>
```

The script wraps `gh` via `gh-bot`, so it re-authenticates per invocation —
no `GH_TOKEN` needs to be set in the parent shell.

Exit codes:

- `0` — all checks passed
- `1` — at least one check failed
- `2` — timeout (some checks still running after 30 min)

Report: relay the script's structured report (check table + `__POLL_RESULT__`
line). On failure, highlight which checks failed so the main agent can
investigate.

## Polling guidelines

- **Interval**: 90 seconds (hardcoded in the scripts). CI builds typically
  take 5–20 minutes; polling faster wastes API calls, polling slower
  delays feedback.
- **Timeout**: 30 minutes (20 iterations × 90 s). If a build hasn't
  completed by then the script reports current state and exits with
  code 2. Decide based on the situation whether to wait longer or
  investigate.
- **Report format**: each script's stdout ends with `__POLL_RESULT__
  exit=N status=...`. Everything above that is human-readable detail
  (build name/ID, final status, failure log excerpts). Paste the stdout
  into the CI report comment verbatim — no summary, no paraphrase.
- **Token refresh**: both scripts re-authenticate per invocation (TC uses
  `TEAMCITY_AGENT_TOKEN`; GH uses `gh-bot`). There is no separate refresh
  step.
