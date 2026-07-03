#!/bin/bash

#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

# setup_protocscope.sh
#
# Installs the `protoscope` CLI (via Homebrew + `go install`) and records its path in
# local.properties (protoscope_path=...) so tests that inspect the Protobuf wire format
# can locate it.
#
# Usage:
#   ./scripts/setup_protocscope.sh
#
# Prerequisites: macOS with Homebrew. Run from the repository root (writes local.properties).

set -euo pipefail

echo "Installing protoscope"

brew install go
go install github.com/protocolbuffers/protoscope/cmd/protoscope...@latest

PROTOSCOPE_PATH=~/go/bin/protoscope
if [ -f "$PROTOSCOPE_PATH" ]; then
  if grep -q "protoscope_path=" local.properties; then
    sed -i '' "s|protoscope_path=.*|protoscope_path=$PROTOSCOPE_PATH|" local.properties
  else
    echo "protoscope_path=$PROTOSCOPE_PATH" >> local.properties
  fi
else
  echo "Error: protoscope not found at $PROTOSCOPE_PATH"
  exit 1
fi
