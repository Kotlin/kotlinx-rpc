#!/usr/bin/env bash
# Self-contained wrapper around `gh` that always authenticates as the
# AI Agent [Kxrpc] GitHub App, never as the user's keyring token.
#
# Why this exists: every Bash tool call runs in a fresh shell, so
# `export GH_TOKEN=...` from an earlier call does not persist. Without a
# live GH_TOKEN, the `gh` CLI silently falls back to whatever identity
# `gh auth status` reports — on a developer machine that is the human
# user. All gh operations then post under the human's name, and
# `gh pr edit --add-reviewer <self>` returns a misleading 422
# "cannot request review from PR author" error.
#
# Solution: invoke `gh` only through this wrapper. It regenerates an
# installation token inline and execs gh with GH_TOKEN set — making
# each invocation self-contained regardless of the parent shell.
#
# Usage (drop-in for gh):
#   .claude/skills/fix-issue/scripts/gh-bot.sh pr create ...
#   .claude/skills/fix-issue/scripts/gh-bot.sh api user --jq .login
#
# Requires: GITHUB_APP_KEY_PATH (same as gh-app-token.sh).

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

TOKEN=$("$SCRIPT_DIR/gh-app-token.sh")

if [ -z "$TOKEN" ]; then
  echo "ERROR: gh-bot could not obtain an App installation token" >&2
  exit 1
fi

# Unset any user-visible GH_* that could confuse gh's auth precedence,
# then exec gh with our token. exec replaces the shell so the caller
# sees gh's own exit code.
unset GITHUB_TOKEN
GH_TOKEN="$TOKEN" exec gh "$@"
