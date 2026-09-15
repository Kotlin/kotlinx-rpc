#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly GRPC_BENCHMARKS_DIR="$(cd -- "${SCRIPT_DIR}/.." && pwd)"
readonly BENCHMARK_BINARY="${SCRIPT_DIR}/build/bin/iosSimulatorArm64/releaseExecutable/legacy-grpc-benchmark-client.kexe"

source "${GRPC_BENCHMARKS_DIR}/scripts/ios-simulator.sh"

usage() {
    cat <<'EOF'
Usage: ./run.sh <benchmark arguments...>

Examples:
  ./run.sh list
  ./run.sh run unary-latency
  ./run.sh run unary-throughput --format csv
EOF
}

if [[ $# -eq 0 ]]; then
    usage >&2
    exit 2
fi

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
    usage
    exit 0
fi

# Keep Gradle output away from stdout so machine-readable benchmark output remains valid.
"${SCRIPT_DIR}/gradlew" --project-dir "${SCRIPT_DIR}" linkReleaseExecutableIosSimulatorArm64 >&2
SIMULATOR="$(ensure_ios_simulator)"
readonly SIMULATOR
exec xcrun simctl spawn "${SIMULATOR}" "${BENCHMARK_BINARY}" "$@"
