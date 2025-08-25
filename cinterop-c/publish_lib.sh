#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -euo pipefail

# Usage:
#   ./publish_lib.sh <dep_file> <out_name> <namespace> <version>
# Example:
#   ./publish_lib.sh bazel-bin/libprotobuf_lite_fat.a libprotobuf_lite_fat.ios_arm64.a com_github_protobuffers_protobuf v31.1
#
# Requires: SPACE_TOKEN in env (e.g., SPACE_TOKEN="***" when run via Bazel)

dep_file="${1:?dep file required}"
out_name="${2:?out name required}"
namespace="${3:?namespace required}"
version="${4:?version required}"

: "${SPACE_TOKEN:?Set SPACE_TOKEN)}"

base_url="https://packages.jetbrains.team/files/p/krpc/bazel-build-deps"
dest_url="${base_url}/${namespace}/${version}/${out_name}"

if [[ ! -f "$dep_file" ]]; then
  echo "ERROR: File not found: $dep_file" >&2
  exit 1
fi

tmp="tmp/dest/$out_name"
mkdir -p "$(dirname "$tmp")"
# we must "rename" the dep_file to out_name.
# the hard link avoids a copy
ln -f "$dep_file" "$tmp"

echo "Uploading ${out_name} -> ${dest_url}"
curl -fSs --retry 3 \
  -H "Authorization: Bearer ${SPACE_TOKEN}" \
  --upload-file "$tmp" \
  "$dest_url"

echo "OK: uploaded ${out_name}"