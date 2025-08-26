#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Builds a static library for a specific platform (os/arch).
#
# Usage:
#   ./build_target.sh //path:libtarget out_dir <platform> <short> <os>
# Example:
#   ./build_target.sh :protowire_static out @build_bazel_apple_support//platforms:ios_arm64 ios_arm64 ios
#
# The example will produce ./out/libprotowire_static.ios_arm64.a

LABEL="${1:?need bazel target label}"
DST="${2:?need output destination}"

CONFIG=release

mkdir -p $(dirname "$DST")

bazel build "$LABEL" >/dev/null

# Ask Bazel what file(s) this target produced
out="$(bazel cquery "$LABEL" --output=files | head -n1)"
[[ -n "$out/include" ]] || { echo "No output for $LABEL"; exit 1; }

rm -rf "$DST/include"
cp -rLf "$out/include" "$DST/include"
chmod -R u+w "$DST"