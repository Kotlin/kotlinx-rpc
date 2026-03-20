#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -euo pipefail
: "${KONAN_HOME:?KONAN_HOME must be set}"
: "${KONAN_TARGET:?KONAN_TARGET must be set}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$SCRIPT_DIR/run_konan" clang clang "$KONAN_TARGET" -- "$@"