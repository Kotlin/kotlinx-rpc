#!/usr/bin/env bash
# Triggers TeamCity builds and polls until all complete or timeout.
#
# Usage: poll-tc-builds.sh <branch-name> <build-config-id> [<build-config-id> ...]
#
# Required env vars:
#   TEAMCITY_AGENT_TOKEN  — TeamCity token for the AI Agent
#
# Exits 0 if all builds succeed, 1 if any fail, 2 on timeout.
# Outputs a structured report to stdout.

set -euo pipefail

INTERVAL=90      # seconds between polls
MAX_ITERATIONS=20 # 20 × 90s = 30min

if [ $# -lt 2 ]; then
  echo "Usage: $0 <branch-name> <build-config-id> [<build-config-id> ...]" >&2
  exit 1
fi

if [ -z "${TEAMCITY_AGENT_TOKEN:-}" ]; then
  echo "ERROR: TEAMCITY_AGENT_TOKEN is not set" >&2
  exit 1
fi

export TEAMCITY_TOKEN="$TEAMCITY_AGENT_TOKEN"

BRANCH="$1"
shift

# Parallel arrays: BUILD_CONFIGS[i] <-> BUILD_IDS[i]
BUILD_CONFIGS=()
BUILD_IDS=()

# ── Trigger all builds ─────────────────────────────────────────────
echo "Triggering $# build(s) on branch '$BRANCH'..."

for CONFIG in "$@"; do
  BUILD_CONFIGS+=("$CONFIG")

  OUTPUT=$(teamcity run start "$CONFIG" --branch "$BRANCH" --json 2>&1) || {
    echo "ERROR: Failed to trigger $CONFIG: $OUTPUT" >&2
    exit 1
  }

  # Extract build ID from JSON output
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

  BUILD_IDS+=("$BUILD_ID")
  echo "  Triggered $CONFIG -> build #$BUILD_ID"
done

NUM_BUILDS=${#BUILD_IDS[@]}
echo ""
echo "Polling $NUM_BUILDS build(s) (every ${INTERVAL}s, max ${MAX_ITERATIONS} iterations)..."

# ── Helper: get a JSON field from a build ──────────────────────────
get_field() {
  local bid="$1"
  local field="$2"
  teamcity run view "$bid" --json 2>/dev/null \
    | python3 -c "import sys,json; print(json.load(sys.stdin).get('$field','unknown'))" 2>/dev/null \
    || echo "unknown"
}

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
echo "== TeamCity Report (branch: $BRANCH) =="
echo ""

HAS_FAILURE=false
HAS_TIMEOUT=false

for idx in $(seq 0 $((NUM_BUILDS - 1))); do
  CONFIG="${BUILD_CONFIGS[$idx]}"
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
  exit 1
elif [ "$HAS_TIMEOUT" = true ]; then
  echo "RESULT: TIMEOUT"
  exit 2
else
  echo "RESULT: SUCCESS"
  echo "All builds passed."
  exit 0
fi
