#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly BUILD_DIR="${GRPC_BENCHMARK_BUILD_DIR:-"${SCRIPT_DIR}/.build"}"
readonly SERVER_BINARY="${BUILD_DIR}/bin/grpc-benchmark-server"
readonly SERVER_PORT="${GRPC_BENCHMARK_PORT:-50051}"
readonly SERVER_TYPE="${GRPC_BENCHMARK_SERVER_TYPE:-async}"
readonly MAX_MESSAGE_BYTES="${GRPC_BENCHMARK_MAX_MESSAGE_BYTES:-33554432}"

if [[ ! -x "${SERVER_BINARY}" ]]; then
    "${SCRIPT_DIR}/build.sh"
fi

exec "${SERVER_BINARY}" \
    --port="${SERVER_PORT}" \
    --server_type="${SERVER_TYPE}" \
    --max_message_bytes="${MAX_MESSAGE_BYTES}" \
    "$@"
