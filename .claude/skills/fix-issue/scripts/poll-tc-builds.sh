#!/usr/bin/env bash
# Triggers TeamCity builds and polls until all complete or timeout.
#
# Two usage modes:
#
#   1. Trigger + poll (default):
#      poll-tc-builds.sh <branch-name> <build-config-id> [<build-config-id> ...]
#
#      Triggers every listed build config on <branch-name>, collects the build
#      IDs, and polls them. Use this when you have not already queued the
#      builds.
#
#   2. Attach + poll:
#      poll-tc-builds.sh --attach <build-id> [<build-id> ...]
#
#      Skips the trigger phase and polls the given numeric build IDs
#      directly. Use this when you (or another agent) already triggered the
#      builds via `teamcity run start` or the TC UI and now need to monitor
#      them. Build config names and branch are looked up per-build from TC.
#
# Required env vars:
#   TEAMCITY_AGENT_TOKEN  — TeamCity token for the AI Agent
#
# Exits 0 if all builds succeed, 1 if any fail, 2 on timeout.
# Outputs a structured report to stdout.
#
# Do not roll your own polling loop. The authoritative completion signal is
# the JSON field `state == "finished"` from `teamcity run view --json`. The
# human-readable `Status:` line in non-JSON output changes content mid-run
# (e.g. "Tests passed: N, ignored: M") and is not stable — do not grep it.

set -euo pipefail

INTERVAL=90      # seconds between polls
MAX_ITERATIONS=20 # 20 × 90s = 30min

if [ $# -lt 2 ]; then
  echo "Usage:" >&2
  echo "  $0 <branch-name> <build-config-id> [<build-config-id> ...]" >&2
  echo "  $0 --attach <build-id> [<build-id> ...]" >&2
  exit 1
fi

if [ -z "${TEAMCITY_AGENT_TOKEN:-}" ]; then
  echo "ERROR: TEAMCITY_AGENT_TOKEN is not set" >&2
  exit 1
fi

export TEAMCITY_TOKEN="$TEAMCITY_AGENT_TOKEN"

# ── Helper: get a JSON field from a build ──────────────────────────
get_field() {
  local bid="$1"
  local field="$2"
  teamcity run view "$bid" --json 2>/dev/null \
    | python3 -c "import sys,json; print(json.load(sys.stdin).get('$field','unknown'))" 2>/dev/null \
    || echo "unknown"
}

# Parallel arrays: BUILD_LABELS[i] <-> BUILD_IDS[i]
# In trigger mode, BUILD_LABELS holds the build-config IDs the caller passed.
# In attach mode, BUILD_LABELS holds buildTypeId looked up from TC.
BUILD_LABELS=()
BUILD_IDS=()
BRANCH_LABEL=""

if [ "$1" = "--attach" ]; then
  shift
  BRANCH_LABEL="(attached)"
  echo "Attaching to $# already-triggered build(s)..."

  for BID in "$@"; do
    if ! [[ "$BID" =~ ^[0-9]+$ ]]; then
      echo "ERROR: --attach expects numeric build IDs; got '$BID'" >&2
      exit 1
    fi

    # Resolve the build-config ID so the report is legible.
    CONFIG=$(get_field "$BID" "buildTypeId")
    if [ "$CONFIG" = "unknown" ] || [ -z "$CONFIG" ]; then
      echo "ERROR: Could not resolve build #$BID — check the ID and your token" >&2
      exit 1
    fi

    BUILD_LABELS+=("$CONFIG")
    BUILD_IDS+=("$BID")
    echo "  Attached $CONFIG -> build #$BID"
  done
else
  BRANCH_LABEL="$1"
  shift

  echo "Triggering $# build(s) on branch '$BRANCH_LABEL'..."

  for CONFIG in "$@"; do
    OUTPUT=$(teamcity run start "$CONFIG" --branch "$BRANCH_LABEL" --json 2>&1) || {
      echo "ERROR: Failed to trigger $CONFIG: $OUTPUT" >&2
      exit 1
    }

    BUILD_ID=$(echo "$OUTPUT" | python3 -c "
import sys, json
data = json.load(sys.stdin)
print(data.get('id', ''))
" 2>/dev/null)

    if [ -z "$BUILD_ID" ]; then
      echo "ERROR: Could not extract build ID for $CONFIG from output" >&2
      echo "$OUTPUT" >&2
      exit 1
    fi

    BUILD_LABELS+=("$CONFIG")
    BUILD_IDS+=("$BUILD_ID")
    echo "  Triggered $CONFIG -> build #$BUILD_ID"
  done
fi

NUM_BUILDS=${#BUILD_IDS[@]}
echo ""
echo "Polling $NUM_BUILDS build(s) (every ${INTERVAL}s, max ${MAX_ITERATIONS} iterations)..."

# ── Poll loop ──────────────────────────────────────────────────────
for i in $(seq 1 "$MAX_ITERATIONS"); do
  STILL_RUNNING=0

  for idx in $(seq 0 $((NUM_BUILDS - 1))); do
    STATE=$(get_field "${BUILD_IDS[$idx]}" "state")
    if [ "$STATE" != "finished" ]; then
      STILL_RUNNING=$((STILL_RUNNING + 1))
    fi
  done

  if [ "$STILL_RUNNING" -eq 0 ]; then
    break
  fi

  echo "[$i/$MAX_ITERATIONS] $STILL_RUNNING build(s) still running..."
  sleep "$INTERVAL"
done

# ── Report ─────────────────────────────────────────────────────────
echo ""
echo "== TeamCity Report (branch: $BRANCH_LABEL) =="
echo ""

HAS_FAILURE=false
HAS_TIMEOUT=false

for idx in $(seq 0 $((NUM_BUILDS - 1))); do
  CONFIG="${BUILD_LABELS[$idx]}"
  BUILD_ID="${BUILD_IDS[$idx]}"
  STATE=$(get_field "$BUILD_ID" "state")
  STATUS=$(get_field "$BUILD_ID" "status")

  if [ "$STATE" != "finished" ]; then
    echo "  $CONFIG (#$BUILD_ID): TIMEOUT (state=$STATE)"
    HAS_TIMEOUT=true
  elif echo "$STATUS" | grep -qi "failure"; then
    echo "  $CONFIG (#$BUILD_ID): FAILURE"
    HAS_FAILURE=true

    # Fetch failure details
    echo "  Failure details:"
    teamcity run log "$BUILD_ID" --failed 2>&1 | head -30 | sed 's/^/    /'
    echo ""
  else
    echo "  $CONFIG (#$BUILD_ID): SUCCESS"
  fi
done

echo ""

if [ "$HAS_FAILURE" = true ]; then
  echo "RESULT: FAILURE"
  echo "__POLL_RESULT__ exit=1 status=FAILURE builds=$NUM_BUILDS"
  exit 1
elif [ "$HAS_TIMEOUT" = true ]; then
  echo "RESULT: TIMEOUT"
  echo "__POLL_RESULT__ exit=2 status=TIMEOUT builds=$NUM_BUILDS"
  exit 2
else
  echo "RESULT: SUCCESS"
  echo "All builds passed."
  echo "__POLL_RESULT__ exit=0 status=SUCCESS builds=$NUM_BUILDS"
  exit 0
fi
