#!/usr/bin/env bash
# Pre-flight checks for the fix-issue workflow.
# Verifies that all three services (YouTrack, TeamCity, GitHub) are reachable
# AND that the authenticated identity is the dedicated AI Agent — not the
# human user's personal credentials.
#
# Outputs a structured report to stdout. Exits 0 only if every check passes.
#
# Required env vars:
#   YOUTRACK_AGENT_TOKEN   — YouTrack token for the AI Agent
#   TEAMCITY_AGENT_TOKEN   — TeamCity token for the AI Agent
#   GITHUB_APP_KEY_PATH    — path to the GitHub App private key PEM

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ── Expected identities ────────────────────────────────────────────
# YouTrack: the agent account's display name (from /api/users/me)
EXPECTED_YT_NAME="AI Agent [Kxrpc]"

# TeamCity: the agent account's username (from /app/rest/users/current)
EXPECTED_TC_USERNAME="ai_agent"

# GitHub App ID hard-coded in gh-app-token.sh
EXPECTED_GH_APP_ID=3304175

# ── Helpers ─────────────────────────────────────────────────────────
PASS=0
FAIL=0

pass() { echo "  PASS  $1"; PASS=$((PASS + 1)); }
fail() { echo "  FAIL  $1"; FAIL=$((FAIL + 1)); }
info() { echo "        $1"; }

# ── 1. YouTrack ─────────────────────────────────────────────────────
echo "== YouTrack =="

if [ -z "${YOUTRACK_AGENT_TOKEN:-}" ]; then
  fail "YOUTRACK_AGENT_TOKEN is not set"
else
  YT_RESPONSE=$(curl -sf \
    -H "Authorization: Bearer $YOUTRACK_AGENT_TOKEN" \
    "https://youtrack.jetbrains.com/api/users/me?fields=login,name" 2>&1) || true

  if [ -z "$YT_RESPONSE" ]; then
    fail "Could not reach YouTrack API (curl returned empty/error)"
  else
    YT_NAME=$(echo "$YT_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('name',''))" 2>/dev/null) || true
    YT_LOGIN=$(echo "$YT_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('login',''))" 2>/dev/null) || true

    if [ -z "$YT_NAME" ]; then
      fail "YouTrack auth failed — could not parse user from response"
      info "Response: $YT_RESPONSE"
    else
      pass "YouTrack reachable, authenticated as: $YT_NAME (login: $YT_LOGIN)"

      if [ "$YT_NAME" = "$EXPECTED_YT_NAME" ]; then
        pass "YouTrack identity matches expected: $EXPECTED_YT_NAME"
      else
        fail "YouTrack identity mismatch — expected '$EXPECTED_YT_NAME', got '$YT_NAME'"
        info "This looks like the wrong token. Check that YOUTRACK_AGENT_TOKEN belongs to the AI Agent account."
      fi
    fi
  fi
fi

echo ""

# ── 2. TeamCity ─────────────────────────────────────────────────────
echo "== TeamCity =="

if [ -z "${TEAMCITY_AGENT_TOKEN:-}" ]; then
  fail "TEAMCITY_AGENT_TOKEN is not set"
else
  # Verify the token works by listing projects
  TC_OUTPUT=$(TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN teamcity project list --limit 1 2>&1) || true

  if echo "$TC_OUTPUT" | grep -qi "error\|unauthorized\|forbidden\|not found\|unknown command"; then
    fail "TeamCity auth failed or CLI error"
    info "Output: $TC_OUTPUT"
  else
    pass "TeamCity reachable, token accepted"
  fi

  # Verify the authenticated identity via REST
  TC_USER_RESPONSE=$(TEAMCITY_TOKEN=$TEAMCITY_AGENT_TOKEN \
    teamcity api "/app/rest/users/current?fields=username,name" 2>&1) || true

  if [ -z "$TC_USER_RESPONSE" ]; then
    fail "Could not fetch TeamCity user identity (REST returned empty)"
  else
    TC_USERNAME=$(echo "$TC_USER_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('username',''))" 2>/dev/null) || true
    TC_NAME=$(echo "$TC_USER_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('name',''))" 2>/dev/null) || true

    if [ -z "$TC_USERNAME" ]; then
      fail "Could not parse TeamCity user identity from REST response"
      info "Response: $TC_USER_RESPONSE"
    elif [ "$TC_USERNAME" = "$EXPECTED_TC_USERNAME" ]; then
      pass "TeamCity identity matches expected: $TC_NAME (username: $TC_USERNAME)"
    else
      fail "TeamCity identity mismatch — expected username '$EXPECTED_TC_USERNAME', got '$TC_USERNAME' ($TC_NAME)"
      info "This looks like the wrong token. Check that TEAMCITY_AGENT_TOKEN belongs to the AI Agent account."
    fi
  fi
fi

echo ""

# ── 3. GitHub ───────────────────────────────────────────────────────
echo "== GitHub =="

if [ -z "${GITHUB_APP_KEY_PATH:-}" ]; then
  fail "GITHUB_APP_KEY_PATH is not set"
else
  if [ ! -f "$GITHUB_APP_KEY_PATH" ]; then
    fail "Private key file not found at $GITHUB_APP_KEY_PATH"
  else
    pass "GitHub App private key file exists"

    # Generate an installation token using the existing script
    GH_TOKEN_VALUE=$("$SCRIPT_DIR/gh-app-token.sh" 2>/dev/null) || true

    if [ -z "$GH_TOKEN_VALUE" ]; then
      fail "GitHub App token generation failed (gh-app-token.sh returned empty)"
      info "Check that the private key is valid and GitHub API is reachable."
    else
      pass "GitHub App installation token generated"

      # Verify repo access
      GH_REPO=$(GH_TOKEN="$GH_TOKEN_VALUE" gh api /repos/Kotlin/kotlinx-rpc --jq .full_name 2>/dev/null) || true

      if [ "$GH_REPO" = "Kotlin/kotlinx-rpc" ]; then
        pass "GitHub repo access verified: $GH_REPO"
      else
        fail "GitHub repo access check failed — expected 'Kotlin/kotlinx-rpc'"
        info "Response: $GH_REPO"
      fi

      # Verify this is an App installation token, not a human personal token.
      # Installation tokens get 403 on /user (only human tokens succeed there).
      # Conversely, /installation/repositories only works with installation tokens.
      GH_USER_STATUS=$(GH_TOKEN="$GH_TOKEN_VALUE" gh api /user --silent 2>&1 && echo "OK" || echo "DENIED")
      GH_INSTALL_REPOS=$(GH_TOKEN="$GH_TOKEN_VALUE" gh api /installation/repositories --jq .total_count 2>/dev/null) || true

      if [ "$GH_USER_STATUS" = "OK" ]; then
        # /user succeeded — this is a human personal token, not an App token
        GH_USER_LOGIN=$(GH_TOKEN="$GH_TOKEN_VALUE" gh api /user --jq .login 2>/dev/null) || true
        fail "GitHub identity is a human user: $GH_USER_LOGIN (expected App installation token)"
        info "The GH_TOKEN should come from gh-app-token.sh, not personal auth."
      elif [ -n "$GH_INSTALL_REPOS" ]; then
        pass "GitHub identity is an App installation token (repos accessible: $GH_INSTALL_REPOS)"
      else
        fail "Could not confirm GitHub token type — /user and /installation/repositories both failed"
      fi
    fi
  fi
fi

echo ""

# ── Summary ─────────────────────────────────────────────────────────
echo "== Summary =="
echo "  Passed: $PASS"
echo "  Failed: $FAIL"

if [ "$FAIL" -gt 0 ]; then
  echo ""
  echo "Pre-flight FAILED. Do NOT proceed with the fix-issue workflow."
  exit 1
else
  echo ""
  echo "Pre-flight PASSED. All services authenticated with correct identities."
  exit 0
fi
