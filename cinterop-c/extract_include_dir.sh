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
#   ./extract_include_dir.sh //prebuilt-deps/grpc_fat:grpc_include_dir prebuilt-deps/grpc_fat
#
# The example will produce the prebuilt-deps/grpc_fat/include directory with all headers.

LABEL="${1:?need bazel target label}"
DST="${2:?need output destination}"
KONAN_HOME="${3:?need KONAN_HOME directory}"

CONFIG=release

mkdir -p $(dirname "$DST")

KH_DEFINE="--define=KONAN_HOME=$KONAN_HOME"
bazel build "$LABEL" $KH_DEFINE >/dev/null

# Ask Bazel what file(s) this target produced
out="$(bazel cquery "$LABEL" $KH_DEFINE --output=files | head -n1)"
[[ -n "$out/include" ]] || { echo "No output for $LABEL"; exit 1; }

SRC_INCLUDE="$out/include"
DST_INCLUDE="$DST/include"

rm -rf "$DST_INCLUDE"
mkdir -p "$DST_INCLUDE"

# Copy headers without trying to adjust permissions (some mounts reject chmod).
PYTHON_BIN="${PYTHON:-python3}"
SRC_INCLUDE="$SRC_INCLUDE" DST_INCLUDE="$DST_INCLUDE" "$PYTHON_BIN" - <<'PY'
import os
import shutil
import sys

src = os.environ["SRC_INCLUDE"]
dst = os.environ["DST_INCLUDE"]

for root, dirs, files in os.walk(src, followlinks=True):
	rel = os.path.relpath(root, src)
	target_root = dst if rel == "." else os.path.join(dst, rel)
	os.makedirs(target_root, exist_ok=True)

	for name in files:
		src_path = os.path.join(root, name)
		dst_path = os.path.join(target_root, name)
		with open(src_path, "rb") as sf, open(dst_path, "wb") as df:
			shutil.copyfileobj(sf, df)
PY

# Best-effort: make destination writable; ignore if the mount forbids chmod.
chmod -R u+w "$DST" 2>/dev/null || true