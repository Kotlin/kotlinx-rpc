#!/usr/bin/env bash
set -euo pipefail
: "${KONAN_HOME:?KONAN_HOME must be set}"
exec "$KONAN_HOME/bin/run_konan" llvm llvm-ar "$@"