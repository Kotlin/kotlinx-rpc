# CI Polling Subagents

Concrete patterns for the two background subagents that monitor CI during Phase D.3.

## GitHub Actions polling subagent

Run the polling script from the worktree root:

```bash
GH_TOKEN=$GH_TOKEN .claude/skills/fix-issue/scripts/poll-gh-actions.sh <pr-number>
```

The script polls every 90s for up to 30 minutes. Exit codes:
- `0` — all checks passed
- `1` — at least one check failed
- `2` — timeout (some checks still running after 30min)

Report: relay the script's structured report (check table + RESULT line).
On failure, highlight which checks failed so the main agent can investigate.

## TeamCity polling subagent

Trigger builds and poll from the worktree root:

```bash
TEAMCITY_AGENT_TOKEN=$TEAMCITY_AGENT_TOKEN \
  .claude/skills/fix-issue/scripts/poll-tc-builds.sh <branch-name> <build-config-id> [<build-config-id> ...]
```

The script triggers all specified build configs, collects their IDs, then polls
every 90s for up to 30 minutes. Exit codes:
- `0` — all builds succeeded
- `1` — at least one build failed (failure details included in output)
- `2` — timeout (some builds still running after 30min)

Report: relay the script's structured report (per-build status + RESULT line).
On failure, the script automatically fetches `teamcity run log <id> --failed`
output for each failed build — include this in the report.

## Polling guidelines

- **Interval**: 90 seconds (hardcoded in scripts). CI builds typically take
  5–20 minutes; polling faster wastes API calls, polling slower delays feedback.
- **Timeout**: 30 minutes (20 iterations x 90s). If a build hasn't completed by
  then, the script reports the current state and exits with code 2. The main
  agent decides whether to wait longer or investigate.
- **Report format**: Each script outputs a structured summary — build name/ID,
  final status (SUCCESS/FAILURE/TIMEOUT), and for failures the first lines of
  the error. Subagents should relay this output verbatim.
- **Token refresh**: If the GH Actions script gets a 401, regenerate `GH_TOKEN`
  via `scripts/gh-app-token.sh` and re-run.