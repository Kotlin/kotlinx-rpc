#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

readonly GRPC_REVISION="b8f09d9168d856020d236bd32f39195f7b5aa2cf"
readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly BUILD_DIR="${GRPC_BENCHMARK_BUILD_DIR:-"${SCRIPT_DIR}/.build"}"
readonly GRPC_ARCHIVE="${BUILD_DIR}/grpc-${GRPC_REVISION}.tar.gz"
readonly GRPC_DIR="${BUILD_DIR}/grpc-${GRPC_REVISION}"
readonly BIN_DIR="${BUILD_DIR}/bin"
readonly SERVER_ASYNC_SOURCE="${GRPC_DIR}/test/cpp/qps/server_async.cc"
readonly SERVER_ASYNC_PATCH="${SCRIPT_DIR}/server_async_zero_payload.patch"

if command -v bazelisk >/dev/null 2>&1; then
    readonly BAZEL_COMMAND="bazelisk"
elif command -v bazel >/dev/null 2>&1; then
    readonly BAZEL_COMMAND="bazel"
else
    echo "Bazelisk or Bazel 8.7.0 is required." >&2
    exit 1
fi

mkdir -p "${BUILD_DIR}"

if [[ ! -f "${GRPC_ARCHIVE}" ]]; then
    curl --fail --location --retry 3 --retry-all-errors \
        --output "${GRPC_ARCHIVE}.tmp" \
        "https://github.com/grpc/grpc/archive/${GRPC_REVISION}.tar.gz"
    mv "${GRPC_ARCHIVE}.tmp" "${GRPC_ARCHIVE}"
fi

if [[ ! -d "${GRPC_DIR}" ]]; then
    tar -xzf "${GRPC_ARCHIVE}" -C "${BUILD_DIR}"
fi

if [[ ! -f "${SERVER_ASYNC_SOURCE}.upstream" ]]; then
    cp "${SERVER_ASYNC_SOURCE}" "${SERVER_ASYNC_SOURCE}.upstream"
fi
cp "${SERVER_ASYNC_SOURCE}.upstream" "${SERVER_ASYNC_SOURCE}"
patch --directory="${GRPC_DIR}" --strip=1 < "${SERVER_ASYNC_PATCH}"

if [[ ! -f "${GRPC_DIR}/test/cpp/qps/BUILD.upstream" ]]; then
    cp "${GRPC_DIR}/test/cpp/qps/BUILD" \
        "${GRPC_DIR}/test/cpp/qps/BUILD.upstream"
fi

cp "${SCRIPT_DIR}/standalone_benchmark_server.cc" "${GRPC_DIR}/test/cpp/qps/"
cp "${GRPC_DIR}/test/cpp/qps/BUILD.upstream" "${GRPC_DIR}/test/cpp/qps/BUILD"
printf '\n' >> "${GRPC_DIR}/test/cpp/qps/BUILD"
cp "${SCRIPT_DIR}/standalone_benchmark_server.BUILD" "${GRPC_DIR}/test/cpp/qps/BUILD.append"
sed -e '1d' "${GRPC_DIR}/test/cpp/qps/BUILD.append" >> "${GRPC_DIR}/test/cpp/qps/BUILD"

cd "${GRPC_DIR}"
"${BAZEL_COMMAND}" --batch --output_user_root="${BUILD_DIR}/bazel-user-root" \
    build --config=opt //test/cpp/qps:standalone_benchmark_server \
    --repository_cache="${BUILD_DIR}/bazel-repository-cache" \
    --disk_cache="${BUILD_DIR}/bazel-disk-cache" \
    --symlink_prefix="${BUILD_DIR}/bazel-" \
    --noshow_progress \
    --output_filter=standalone_benchmark_server \
    --verbose_failures

mkdir -p "${BIN_DIR}"
cp "${BUILD_DIR}/bazel-bin/test/cpp/qps/standalone_benchmark_server" \
    "${BIN_DIR}/grpc-benchmark-server.tmp"
chmod +x "${BIN_DIR}/grpc-benchmark-server.tmp"
mv -f "${BIN_DIR}/grpc-benchmark-server.tmp" \
    "${BIN_DIR}/grpc-benchmark-server"

echo "Built ${BIN_DIR}/grpc-benchmark-server"
