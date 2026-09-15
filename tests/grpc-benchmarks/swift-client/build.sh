#!/usr/bin/env bash

# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

set -euo pipefail

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly TARGET="arm64-apple-ios18.0-simulator"
readonly SDK_PATH="$(xcrun --sdk iphonesimulator --show-sdk-path)"
readonly MODULE_CACHE="${SCRIPT_DIR}/.build/module-cache"
readonly PRODUCT_NAME="swift-grpc-benchmark-client"
readonly STABLE_BINARY="${SCRIPT_DIR}/.build/${PRODUCT_NAME}"
readonly -a BUILD_ARGUMENTS=(
    --package-path "${SCRIPT_DIR}"
    --scratch-path "${SCRIPT_DIR}/.build"
    --disable-sandbox
    --configuration release
    --triple "${TARGET}"
    --sdk "${SDK_PATH}"
)

mkdir -p "${MODULE_CACHE}"
export CLANG_MODULE_CACHE_PATH="${MODULE_CACHE}"
export SWIFTPM_MODULECACHE_OVERRIDE="${MODULE_CACHE}"

swift build "${BUILD_ARGUMENTS[@]}" >&2

binary_directory="$(swift build "${BUILD_ARGUMENTS[@]}" --show-bin-path)"
readonly binary_directory
readonly BINARY="${binary_directory}/${PRODUCT_NAME}"

if [[ ! -x "${BINARY}" ]]; then
    echo "Expected benchmark binary was not produced at ${BINARY}" >&2
    exit 1
fi

if ! lipo "${BINARY}" -verify_arch arm64; then
    echo "Benchmark binary does not contain the requested arm64 architecture" >&2
    exit 1
fi

ln -sfn -- "${BINARY}" "${STABLE_BINARY}"
printf '%s\n' "${STABLE_BINARY}"
