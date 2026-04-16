#!/usr/bin/env bash
# Polls GitHub Actions check runs for a PR until all complete or timeout.
#
# Usage: poll-gh-actions.sh <pr-number>
#
# Required env vars:
#   GH_TOKEN  — GitHub App installation token (from gh-app-token.sh)
#
# Exits 0 if all checks pass, 1 if any fail, 2 on timeout.
# Outputs a structured report to stdout.

set -euo pipefail

REPO="Kotlin/kotlinx-rpc"
INTERVAL=90      # seconds between polls
MAX_ITERATIONS=20 # 20 × 90s = 30min

if [ $# -lt 1 ]; then
  echo "Usage: $0 <pr-number>" >&2
  exit 1
fi

PR_NUMBER="$1"

if [ -z "${GH_TOKEN:-}" ]; then
  echo "ERROR: GH_TOKEN is not set" >&2
  exit 1
fi

echo "Polling GH Actions for PR #$PR_NUMBER (every ${INTERVAL}s, max ${MAX_ITERATIONS} iterations)..."

for i in $(seq 1 "$MAX_ITERATIONS"); do
  CHECKS_JSON=$(GH_TOKEN="$GH_TOKEN" gh pr checks "$PR_NUMBER" \
    --repo "$REPO" --json name,state,bucket 2>&1) || {
    echo "ERROR: gh pr checks failed: $CHECKS_JSON" >&2
    exit 1
  }

  # PENDING = still running; everything else (SUCCESS, FAILURE, SKIPPED, etc.) = done
  PENDING=$(echo "$CHECKS_JSON" | python3 -c "
import sys, json
checks = json.load(sys.stdin)
print(len([c for c in checks if c.get('state') == 'PENDING']))
" 2>/dev/null)

  if [ "$PENDING" = "0" ]; then
    echo ""
    echo "== GH Actions Report (PR #$PR_NUMBER) =="
    echo ""

    # Full table for readability
    GH_TOKEN="$GH_TOKEN" gh pr checks "$PR_NUMBER" --repo "$REPO"
    echo ""

    # Check for failures via the bucket field (fail/pass/skipping/pending)
    FAILURES=$(echo "$CHECKS_JSON" | python3 -c "
import sys, json
checks = json.load(sys.stdin)
failed = [c['name'] for c in checks if c.get('bucket') == 'fail']
for f in failed:
    print(f'  - {f}')
" 2>/dev/null)

    if [ -n "$FAILURES" ]; then
      echo "RESULT: FAILURE"
      echo "Failed checks:"
      echo "$FAILURES"
      exit 1
    else
      echo "RESULT: SUCCESS"
      echo "All checks passed."
      exit 0
    fi
  fi

  echo "[$i/$MAX_ITERATIONS] $PENDING check(s) still running..."
  sleep "$INTERVAL"
done

echo ""
echo "== GH Actions Report (PR #$PR_NUMBER) =="
echo "RESULT: TIMEOUT (${MAX_ITERATIONS} iterations, $((MAX_ITERATIONS * INTERVAL))s elapsed)"
echo ""
echo "Current state:"
GH_TOKEN="$GH_TOKEN" gh pr checks "$PR_NUMBER" --repo "$REPO"
exit 2
