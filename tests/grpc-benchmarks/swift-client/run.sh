#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly GRPC_BENCHMARKS_DIR="$(cd -- "${SCRIPT_DIR}/.." && pwd)"
readonly BENCHMARK_BINARY="${SCRIPT_DIR}/.build/arm64-apple-ios-simulator/release/swift-grpc-benchmark-client"

source "${GRPC_BENCHMARKS_DIR}/scripts/transient-command.sh"

usage() {
    cat <<'EOF'
Usage: ./run.sh <benchmark arguments...>

Examples:
  ./run.sh list
  ./run.sh run unary-latency
  ./run.sh run unary-throughput --format csv
EOF
}

select_ios_simulator() {
    local state="$1"
    local device_family="${2:-}"

    awk -v state="${state}" -v device_family="${device_family}" '
        /^-- iOS / { ios_runtime = 1; next }
        /^-- / { ios_runtime = 0; next }
        ios_runtime && $0 ~ "\\(" state "\\)[[:space:]]*$" {
            if (device_family != "" && $0 !~ "^[[:space:]]+" device_family) next
            if (match($0, /\([[:xdigit:]-]+\)/)) {
                print substr($0, RSTART + 1, RLENGTH - 2)
                exit
            }
        }
    '
}

ensure_ios_simulator() {
    local devices
    local simulator
    local requested_simulator="${KXRPC_BENCHMARK_SIMULATOR:-}"

    devices="$(xcrun simctl list devices available)"
    if [[ -n "${requested_simulator}" && "${requested_simulator}" != "booted" ]]; then
        simulator="${requested_simulator}"
        if ! grep -Fq "(${simulator})" <<< "${devices}"; then
            echo "iOS Simulator '${simulator}' is not available." >&2
            exit 2
        fi
    else
        simulator="$(select_ios_simulator Booted <<< "${devices}")"
        if [[ -z "${simulator}" ]]; then
            simulator="$(select_ios_simulator Shutdown iPhone <<< "${devices}")"
        fi
        if [[ -z "${simulator}" ]]; then
            simulator="$(select_ios_simulator Shutdown <<< "${devices}")"
        fi
        if [[ -z "${simulator}" ]]; then
            echo "No available iOS Simulator device found." >&2
            exit 2
        fi
    fi

    if ! grep -F "(${simulator})" <<< "${devices}" | grep -q '(Booted)'; then
        echo "Booting iOS Simulator ${simulator}..." >&2
        xcrun simctl boot "${simulator}" >&2
    fi
    xcrun simctl bootstatus "${simulator}" -b >&2
    printf '%s\n' "${simulator}"
}

if [[ $# -eq 0 ]]; then
    usage >&2
    exit 2
fi
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
    usage
    exit 0
fi

run_transient_command \
    "platform=ios-simulator-arm64 implementation=swift" \
    "${SCRIPT_DIR}/build.sh"
SIMULATOR="$(ensure_ios_simulator)"
readonly SIMULATOR

exec xcrun simctl spawn "${SIMULATOR}" "${BENCHMARK_BINARY}" "$@"
