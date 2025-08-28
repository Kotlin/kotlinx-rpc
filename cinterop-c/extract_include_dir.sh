#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Extract all headers in the /include directory of the given target.
#
# Usage:
#   ./extract_include_dir.sh <target> <output-directory>
# Example:
#   ./extract_include_dir.sh //prebuilt-deps/grpc_fat:grpc_fat prebuilt-deps/grpc_fat
#
# The example will produce the prebuilt-deps/grpc_fat/include directory with all headers.

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