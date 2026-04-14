# CI Polling Subagents

Concrete patterns for the two background subagents that monitor CI during Phase D.3.

## GitHub Actions polling subagent

The subagent should run a poll loop using `gh`:

```bash
# Wait for all GH Actions check runs to complete (poll every 90s, up to 30min)
PR_NUMBER=<pr-number>
for i in $(seq 1 20); do
  STATUS=$(GH_TOKEN=$GH_TOKEN gh pr checks "$PR_NUMBER" \
    --repo Kotlin/kotlinx-rpc --json name,state,conclusion \
    --jq '[.[] | select(.state != "COMPLETED")] | length')
  if [ "$STATUS" = "0" ]; then
    echo "All GH Actions complete."
    GH_TOKEN=$GH_TOKEN gh pr checks "$PR_NUMBER" --repo Kotlin/kotlinx-rpc
    break
  fi
  echo "Iteration $i: $STATUS check(s) still running..."
  sleep 90
done
```

Report: the full `gh pr checks` table on completion, highlighting any failures.

## TeamCity polling subagent

The subagent should trigger the required builds, collect their IDs, then poll:

```bash
# 1. Trigger builds and capture IDs
BUILD_ID=$(TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run start <build-config> \
  --branch <branch-name> --json | jq -r '.id')

# 2. Poll each build (every 90s, up to 30min)
for i in $(seq 1 20); do
  STATUS=$(TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run view "$BUILD_ID" \
    --json=state,status | jq -r '.state')
  if [ "$STATUS" = "finished" ]; then
    echo "TC build $BUILD_ID finished."
    TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run view "$BUILD_ID"
    break
  fi
  echo "Iteration $i: build $BUILD_ID state=$STATUS..."
  sleep 90
done
```

For multiple TC builds, poll all IDs in the same loop — check each, exit when
all are `finished`. On any failure, immediately run
`TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity run log <build-id> --failed`
and include the failure summary in the report.

## Polling guidelines

- **Interval**: 90 seconds. CI builds typically take 5–20 minutes; polling faster
  wastes API calls, polling slower delays feedback unnecessarily.
- **Timeout**: 30 minutes (20 iterations × 90s). If a build hasn't completed by
  then, report the current state and let the main agent decide whether to wait
  longer or investigate.
- **Report format**: Each subagent should return a structured summary —
  build name/ID, final status (success/failure/timeout), duration, and for
  failures the first few lines of the error.
