#!/usr/bin/env bash
#
# Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Builds one Bazel target for one Kotlin/Native target and copies the produced static
# library to the destination requested by the Gradle task.

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
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

mkdir -p "$(dirname "$DST")"

echo "==> Building $LABEL to $DST" >&2
echo "==> KONAN_HOME: $KONAN_HOME" >&2
echo "==> KONAN_TARGET: $KONAN_TARGET" >&2

KONAN_DEPS="${KONAN_DEPS:-$KONAN_HOME/../dependencies}"
KONAN_LLVM_RESOURCE_DIR="$(
  KONAN_DEPS="$KONAN_DEPS" \
    python3 "$SCRIPT_DIR/../bazel-support/toolchain/resolve_konan_llvm_resource_dir.py" "$KONAN_HOME"
)"

echo "==> KONAN_DEPS: $KONAN_DEPS" >&2
echo "==> KONAN_LLVM_RESOURCE_DIR: $KONAN_LLVM_RESOURCE_DIR" >&2

BAZEL_ARGS=(
  "--config=$KONAN_TARGET"
  "--config=release"
  "--define=KONAN_HOME=$KONAN_HOME"
  "--define=KONAN_DEPS=$KONAN_DEPS"
  "--define=KONAN_LLVM_RESOURCE_DIR=$KONAN_LLVM_RESOURCE_DIR"
)

set -x
"$BAZEL" build "$LABEL" "${BAZEL_ARGS[@]}"
set +x

out="$("$BAZEL" cquery "$LABEL" "${BAZEL_ARGS[@]}" --output=files | head -n1)"
[[ -n "$out" ]] || { echo "No output for $LABEL" >&2; exit 1; }

cp -f "$out" "$DST"
echo "Done. Binary written to: $DST" >&2
