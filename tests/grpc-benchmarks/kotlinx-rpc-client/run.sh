#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly REPOSITORY_ROOT="$(cd -- "${SCRIPT_DIR}/../../.." && pwd)"
readonly GRPC_BENCHMARKS_DIR="$(cd -- "${SCRIPT_DIR}/.." && pwd)"

source "${GRPC_BENCHMARKS_DIR}/scripts/ios-simulator.sh"
source "${GRPC_BENCHMARKS_DIR}/scripts/transient-command.sh"

usage() {
    cat <<'EOF'
Usage: ./run.sh <ios|macos|jvm> <benchmark arguments...>

Examples:
  ./run.sh ios list
  ./run.sh macos run unary-latency
  ./run.sh jvm run unary-throughput --format csv
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

readonly PLATFORM="$1"
shift

case "${PLATFORM}" in
    ios)
        readonly BUILD_TASK=":tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableIosSimulatorArm64"
        readonly BENCHMARK_BINARY="${SCRIPT_DIR}/build/bin/iosSimulatorArm64/releaseExecutable/kotlinx-rpc-grpc-benchmark-client.kexe"
        run_transient_command \
            "platform=ios-simulator-arm64 implementation=current" \
            "${REPOSITORY_ROOT}/gradlew" "${BUILD_TASK}"
        readonly SWIFT_RUNTIME_DIR="$(dirname -- "${BENCHMARK_BINARY}")/Frameworks"
        mkdir -p -- "${SWIFT_RUNTIME_DIR}"
        xcrun swift-stdlib-tool \
            --copy \
            --scan-executable "${BENCHMARK_BINARY}" \
            --platform iphonesimulator \
            --destination "${SWIFT_RUNTIME_DIR}"
        SIMULATOR="$(ensure_ios_simulator)"
        readonly SIMULATOR
        exec xcrun simctl spawn "${SIMULATOR}" "${BENCHMARK_BINARY}" "$@"
        ;;
    macos)
        readonly BUILD_TASK=":tests:grpc-benchmarks:kotlinx-rpc-client:linkReleaseExecutableMacosArm64"
        readonly BENCHMARK_BINARY="${SCRIPT_DIR}/build/bin/macosArm64/releaseExecutable/kotlinx-rpc-grpc-benchmark-client.kexe"
        run_transient_command \
            "platform=macos-arm64 implementation=current" \
            "${REPOSITORY_ROOT}/gradlew" "${BUILD_TASK}"
        exec "${BENCHMARK_BINARY}" "$@"
        ;;
    jvm)
        readonly BUILD_TASK=":tests:grpc-benchmarks:kotlinx-rpc-client:installJvmDist"
        readonly BENCHMARK_BINARY="${SCRIPT_DIR}/build/install/kotlinx-rpc-client-jvm/bin/kotlinx-rpc-client"
        run_transient_command \
            "platform=jvm implementation=current" \
            "${REPOSITORY_ROOT}/gradlew" "${BUILD_TASK}"
        exec "${BENCHMARK_BINARY}" "$@"
        ;;
    *)
        echo "Unknown platform '${PLATFORM}'; expected ios, macos, or jvm." >&2
        usage >&2
        exit 2
        ;;
esac
