#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Builds a target-specific grpc shim static library from the unpacked grpc bundle.

find_bazel() {
  if command -v bazel >/dev/null 2>&1; then
    echo bazel
    return
  fi
  if command -v bazelisk >/dev/null 2>&1; then
    echo bazelisk
    return
  fi
  echo "Neither bazel nor bazelisk was found on PATH" >&2
  exit 1
}

LABEL="${1:?need bazel target label}"
DST="${2:?need output destination}"
KONAN_TARGET="${3:?need the konan_target name}"
KONAN_HOME="${4:?need the konan_home path}"
BAZEL="$(find_bazel)"

mkdir -p "$(dirname "$DST")"

KONAN_DEPS="${KONAN_DEPS:-$KONAN_HOME/../dependencies}"
KONAN_LLVM_RESOURCE_DIR="$(KONAN_DEPS="$KONAN_DEPS" python3 ../bazel-support/toolchain/resolve_konan_llvm_resource_dir.py "$KONAN_HOME")"

BAZEL_ARGS=(
  "--config=$KONAN_TARGET"
  "--config=release"
  "--define=KONAN_HOME=$KONAN_HOME"
  "--define=KONAN_DEPS=$KONAN_DEPS"
  "--define=KONAN_LLVM_RESOURCE_DIR=$KONAN_LLVM_RESOURCE_DIR"
)

"$BAZEL" build "$LABEL" "${BAZEL_ARGS[@]}" >/dev/null

out="$("$BAZEL" cquery "$LABEL" "${BAZEL_ARGS[@]}" --output=files | head -n1)"
[[ -n "$out" ]] || { echo "No output for $LABEL"; exit 1; }

cp -f "$out" "$DST"
