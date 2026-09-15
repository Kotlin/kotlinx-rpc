#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly DEFAULT_OUTPUT_DIR="${SCRIPT_DIR}/output"

usage() {
    cat <<'EOF'
Usage: ./run-all.sh [--output-dir DIRECTORY] <benchmark|all> [benchmark options...]

Runs the selected benchmark on the current kotlinx-rpc, legacy kotlinx-rpc,
and direct Swift clients using the iOS Simulator. Results are combined into a
timestamped CSV file.

Examples:
  ./run-all.sh unary-latency
  ./run-all.sh all
  ./run-all.sh --output-dir ./results unary-payload-sweep --case symmetric-1m
  ./run-all.sh unary-throughput --calls 1000 --target localhost:50051

The benchmark server must already be running. The output format is always CSV;
--format is therefore not accepted.
EOF
}

output_dir="${DEFAULT_OUTPUT_DIR}"
while [[ $# -gt 0 ]]; do
    case "$1" in
        -h|--help)
            usage
            exit 0
            ;;
        --output-dir)
            if [[ $# -lt 2 || -z "$2" ]]; then
                echo "--output-dir requires a non-empty directory." >&2
                exit 2
            fi
            output_dir="$2"
            shift 2
            ;;
        --output-dir=*)
            output_dir="${1#*=}"
            if [[ -z "${output_dir}" ]]; then
                echo "--output-dir requires a non-empty directory." >&2
                exit 2
            fi
            shift
            ;;
        --)
            shift
            break
            ;;
        *)
            break
            ;;
    esac
done

if [[ $# -eq 0 ]]; then
    usage >&2
    exit 2
fi

for argument in "$@"; do
    if [[ "${argument}" == "--format" || "${argument}" == --format=* ]]; then
        echo "The combined runner always uses CSV output; remove '${argument}'." >&2
        exit 2
    fi
done

readonly -a BENCHMARK_ARGUMENTS=("$@")
readonly -a CLIENT_NAMES=("current" "legacy" "swift")
readonly timestamp="$(date +%Y%m%d-%H%M%S)"

mkdir -p -- "${output_dir}"
result_file="${output_dir}/grpc-benchmarks-${timestamp}.csv"
if [[ -e "${result_file}" ]]; then
    result_file="${output_dir}/grpc-benchmarks-${timestamp}-$$.csv"
fi
readonly result_file

temporary_dir="$(mktemp -d "${output_dir}/.grpc-benchmarks.XXXXXX")"
readonly temporary_dir
trap 'rm -rf -- "${temporary_dir}"' EXIT

combined_result="${temporary_dir}/combined.csv"
readonly combined_result
expected_header=""

for client_index in "${!CLIENT_NAMES[@]}"; do
    client_name="${CLIENT_NAMES[client_index]}"
    client_number=$((client_index + 1))
    client_result="${temporary_dir}/${client_name}.csv"

    echo "[client ${client_number}/${#CLIENT_NAMES[@]}] platform=ios-simulator-arm64 implementation=${client_name}" >&2
    case "${client_name}" in
        current)
            "${SCRIPT_DIR}/kotlinx-rpc-client/run.sh" ios run "${BENCHMARK_ARGUMENTS[@]}" \
                --format csv > "${client_result}"
            ;;
        legacy)
            "${SCRIPT_DIR}/legacy-ios-client/run.sh" run "${BENCHMARK_ARGUMENTS[@]}" \
                --format csv > "${client_result}"
            ;;
        swift)
            "${SCRIPT_DIR}/swift-client/run.sh" run "${BENCHMARK_ARGUMENTS[@]}" \
                --format csv > "${client_result}"
            ;;
    esac

    header="$(head -n 1 "${client_result}")"
    line_count="$(awk 'END { print NR }' "${client_result}")"
    if [[ -z "${header}" || "${line_count}" -lt 2 ]]; then
        echo "Client '${client_name}' did not produce CSV benchmark rows." >&2
        exit 1
    fi
    if [[ -z "${expected_header}" ]]; then
        expected_header="${header}"
        printf '%s\n' "${header}" > "${combined_result}"
    elif [[ "${header}" != "${expected_header}" ]]; then
        echo "Client '${client_name}' produced an incompatible CSV header." >&2
        exit 1
    fi
    tail -n +2 "${client_result}" >> "${combined_result}"
done

mv -- "${combined_result}" "${result_file}"
echo "Benchmark results: ${result_file}"
