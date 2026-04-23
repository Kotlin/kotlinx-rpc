# CI Polling Subagents

Concrete patterns for the two background subagents that monitor CI during Phase D.3.

## First decision — have you already triggered the builds?

Work out which path applies **before** doing anything else. The rest of this
doc depends on it.

- **You have not queued any builds yet** (no `teamcity run start` was called
  during this turn) → use **trigger + poll** mode. The script queues the
  builds and polls them.
- **Builds are already queued** — you (or another agent) ran
  `teamcity run start`, or the TC shows queued builds for the branch at
  your HEAD SHA → use **attach + poll** mode. Pass the existing build IDs
  and the script joins them in progress.

Do not mix: never queue some builds manually and then trigger the rest from
the script. Pick one mode per run.

## Never roll your own poll loop

The only supported way to monitor CI in this skill is the polling scripts
in `scripts/`. Do not write a shell loop around `teamcity run view` or
`gh pr checks`. The scripts exist specifically because the ad-hoc approach
keeps breaking.

If the script is missing or erroring in a way you cannot explain, stop and
report to the user — do not substitute a hand-rolled equivalent.

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

Report: relay the script's structured report verbatim — per-build status
line plus the final `RESULT:` line.

## GitHub Actions polling

GH Actions start automatically on push; there is no trigger step. Run the
script from the worktree root:

```bash
.claude/skills/fix-issue/scripts/poll-gh-actions.sh <pr-number>
```

The wrapper re-authenticates per call via `gh-bot`, so no `GH_TOKEN` needs
to be set in the parent shell.

Exit codes:

- `0` — all checks passed
- `1` — at least one check failed
- `2` — timeout (some checks still running after 30 min)

Report: relay the script's structured report (check table + RESULT line).
On failure, highlight which checks failed so the main agent can investigate.

## Dispatching subagents — required prompt shape

When you delegate polling to a subagent, the prompt must be narrow enough
that there is no room to improvise. Past incidents have come from prompts
that offered a choice between "run the script" and "poll directly if you
prefer" — the subagent always picked the wrong branch, and in one case
returned "I'll report back once there are results" without actually
blocking.

Use this template verbatim, adjusted only for the script and its arguments.

> Run this exact command and nothing else:
>
> ```
> TEAMCITY_AGENT_TOKEN=$TEAMCITY_AGENT_TOKEN \
>   .claude/skills/fix-issue/scripts/poll-tc-builds.sh --attach 53149 53150
> ```
>
> Do **not** write a substitute loop. Do **not** call `teamcity run view`
> directly. Do **not** return a text response until the script has exited.
> When it does, paste its stdout verbatim and then report the exit code
> (0 = success, 1 = failure, 2 = timeout). If the script is missing or errors
> for a reason other than a CI failure, quote the error and stop; do not
> attempt a workaround.

For GH Actions, the same template with
`.claude/skills/fix-issue/scripts/poll-gh-actions.sh <pr-number>` instead.

The wording matters — it is tight for a reason. "Or you can poll directly"
anywhere in the prompt is a bug.

## Polling guidelines

- **Interval**: 90 seconds (hardcoded in the scripts). CI builds typically
  take 5–20 minutes; polling faster wastes API calls, polling slower delays
  feedback.
- **Timeout**: 30 minutes (20 iterations × 90s). If a build has not
  completed by then, the script reports the current state and exits with
  code 2. The main agent decides whether to wait longer or investigate.
- **Report format**: each script outputs a structured summary — build
  name/ID, final status, and for failures the first lines of the error.
  Subagents relay this output verbatim.
- **Token refresh**: both scripts re-authenticate per invocation (TC uses
  the token in `TEAMCITY_AGENT_TOKEN`; GH uses `gh-bot`). There is no
  separate refresh step.
