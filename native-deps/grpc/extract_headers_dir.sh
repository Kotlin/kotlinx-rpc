#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Extract all repo-scoped headers of the given target into the destination directory.
# Unlike extract_include_dir.sh, this keeps the full repo-relative layout such as
# include/** and src/** because kgrpc needs grpc internal headers at compile time.

LABEL="${1:?need bazel target label}"
DST="${2:?need output destination}"
KONAN_HOME="${3:?need KONAN_HOME directory}"

KONAN_DEPS="${KONAN_DEPS:-$KONAN_HOME/../dependencies}"
KONAN_LLVM_RESOURCE_DIR="$(KONAN_DEPS="$KONAN_DEPS" python3 ../bazel-support/toolchain/resolve_konan_llvm_resource_dir.py "$KONAN_HOME")"

BAZEL_ARGS=(
  "--define=KONAN_HOME=$KONAN_HOME"
  "--define=KONAN_DEPS=$KONAN_DEPS"
  "--define=KONAN_LLVM_RESOURCE_DIR=$KONAN_LLVM_RESOURCE_DIR"
)

bazel build "$LABEL" "${BAZEL_ARGS[@]}" >/dev/null

out="$(bazel cquery "$LABEL" "${BAZEL_ARGS[@]}" --output=files | head -n1)"
[[ -n "$out" ]] || { echo "No output for $LABEL"; exit 1; }

rm -rf "$DST"
mkdir -p "$DST"

PYTHON_BIN="${PYTHON:-python3}"
SRC_DIR="$out" DST_DIR="$DST" "$PYTHON_BIN" - <<'PY'
import os
import shutil

src = os.environ["SRC_DIR"]
dst = os.environ["DST_DIR"]

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

chmod -R u+w "$DST" 2>/dev/null || true
