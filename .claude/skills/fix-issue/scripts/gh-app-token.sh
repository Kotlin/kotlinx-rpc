#!/usr/bin/env bash
# Generates a GitHub installation access token for the AI Agent [Kxrpc] GitHub App.
# Outputs ONLY the token string to stdout. All other output goes to stderr.
#
# Requires: GITHUB_APP_KEY_PATH env var pointing to the private key PEM file.
# Dependencies (PyJWT + cryptography) are installed into a venv automatically.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VENV_DIR="$SCRIPT_DIR/.venv"

if [ -z "${GITHUB_APP_KEY_PATH:-}" ]; then
  echo "ERROR: GITHUB_APP_KEY_PATH is not set" >&2
  exit 1
fi

if [ ! -f "$GITHUB_APP_KEY_PATH" ]; then
  echo "ERROR: Private key not found at $GITHUB_APP_KEY_PATH" >&2
  exit 1
fi

# Bootstrap venv + deps on first run
if [ ! -f "$VENV_DIR/bin/python3" ]; then
  echo "Creating venv and installing dependencies..." >&2
  python3 -m venv "$VENV_DIR"
  "$VENV_DIR/bin/pip" install --quiet PyJWT cryptography
fi

"$VENV_DIR/bin/python3" -c "
import jwt, time, json, os
from urllib.request import Request, urlopen

with open(os.environ['GITHUB_APP_KEY_PATH']) as f:
    key = f.read()

now = int(time.time())
t = jwt.encode({'iat': now - 60, 'exp': now + 600, 'iss': '3304175'}, key, algorithm='RS256')

req = Request('https://api.github.com/app/installations/122442297/access_tokens', method='POST', data=b'')
req.add_header('Authorization', f'Bearer {t}')
req.add_header('Accept', 'application/vnd.github+json')

resp = urlopen(req)
print(json.loads(resp.read())['token'])
"
