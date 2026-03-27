#!/usr/bin/env bash
#
# Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Builds a static library for a specific platform (os/arch).
#
# Usage:
#   ./build_target.sh //path:libtarget dest konan_target konan_home
# Example:
#   ./build_target.sh :protowire_fat out/libprotowire_fat.ios_arm64.a ios_arm64 \
#             $HOME/.konan/kotlin-native-prebuilt-macos-aarch64-2.3.0
#
# The example will produce ./out/libprotowire_fat.ios_arm64.a

LABEL="${1:?need bazel target label}"
DST="${2:?need output destination}"
KONAN_TARGET="${3:?need the konan_target name}"
KONAN_HOME="${4:?need the konan_home path}"

CONFIG=release

mkdir -p "$(dirname "$DST")"

echo "==> Building $LABEL to $DST" >&2
echo "==> KONAN_HOME: $KONAN_HOME" >&2
echo "==> KONAN_TARGET: $KONAN_TARGET" >&2
KONAN_DEPS="${KONAN_DEPS:-$KONAN_HOME/../dependencies}"
KONAN_LLVM_RESOURCE_DIR="$(KONAN_DEPS="$KONAN_DEPS" python3 ../../bazel-support/toolchain/resolve_konan_llvm_resource_dir.py "$KONAN_HOME")"
echo "==> KONAN_DEPS: $KONAN_DEPS" >&2
echo "==> KONAN_LLVM_RESOURCE_DIR: $KONAN_LLVM_RESOURCE_DIR" >&2

KONAN_DEP="--define=KONAN_DEPS=$KONAN_DEPS"
KONAN_LLVM_DEF="--define=KONAN_LLVM_RESOURCE_DIR=$KONAN_LLVM_RESOURCE_DIR"
set -x
bazel build "$LABEL" --config="$KONAN_TARGET" --config="$CONFIG" "$KONAN_DEP" "$KONAN_LLVM_DEF" "--define=KONAN_HOME=$KONAN_HOME"
set +x

out="$(bazel cquery "$LABEL" --config="$KONAN_TARGET" --config="$CONFIG" "$KONAN_DEP" "$KONAN_LLVM_DEF" "--define=KONAN_HOME=$KONAN_HOME" --output=files | head -n1)"
[[ -n "$out" ]] || { echo "No output for $LABEL"; exit 1; }

cp -f "$out" "$DST"

echo "Done. Binary written to: $DST" >&2
