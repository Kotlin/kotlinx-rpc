#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Builds a static library for a specific platform (os/arch).
#
# Usage:
#   ./build_target.sh //path:libtarget dest konan_target konan_home
# Example:
#   ./build_target.sh :protowire_static out/libprotowire_static.ios_arm64.a ios_arm64 \
#             $HOME/.konan/kotlin-native-prebuilt-macos-aarch64-2.2.10
#
# The example will produce ./out/libprotowire_static.ios_arm64.a

LABEL="${1:?need bazel target label}"
DST="${2:?need output destination}"
KONAN_TARGET="${3:?need the konan_target name}"
KONAN_HOME="${4:?need the konan_home path}"

CONFIG=release

mkdir -p "$(dirname "$DST")"

echo "==> Building $LABEL to $DST" >&2
echo "==> KONAN_HOME: $KONAN_HOME" >&2
echo "==> KONAN_TARGET: $KONAN_TARGET" >&2
KONAN_DEP="--define=KONAN_DEPS=$HOME/.konan/dependencies"
set -x # set shell tracing
bazel build "$LABEL" --config="$KONAN_TARGET" --config="$CONFIG" "$KONAN_DEP" "--define=KONAN_HOME=$KONAN_HOME"
set +x

# Ask Bazel what file(s) this target produced under this platform
out="$(bazel cquery "$LABEL" --config="$KONAN_TARGET" --config="$CONFIG" "$KONAN_DEP" "--define=KONAN_HOME=$KONAN_HOME" --output=files | head -n1)"
[[ -n "$out" ]] || { echo "No output for $LABEL ($SHORT)"; exit 1; }

cp -f "$out" "$DST"

echo "Done. Binary written to: $DST" >&2
