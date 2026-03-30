#!/usr/bin/env bash
#
# Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

readonly LABEL="${1:?need bazel target label}"
readonly DST="${2:?need output destination directory}"
readonly KONAN_TARGET="${3:?need the konan_target name}"
readonly KONAN_HOME="${4:?need the konan_home path}"
readonly CONFIG="release"

log() {
  echo "==> $*" >&2
}

# Kotlin/Native ships its own LLVM toolchain. Bazel needs this information for Linux toolchains; Apple builds usually
# ignore these defines, but the script passes one uniform Bazel argument set for every target. We also reuse the
# matching llvm-ar binary later when we need to rebuild logical archives from object files.
resolve_konan_llvm_resource_dir() {
  BAZEL_KONAN_DEPS="$1" python3 ../bazel-support/toolchain/resolve_konan_llvm_resource_dir.py "$KONAN_HOME"
}

# Query the final C/C++ link inputs from CcInfo instead of relying on a fat archive target. The query returns:
# 1. the logical archive path used for ordering inside the manifest
# 2. the materialized archive path, when Bazel emitted one
# 3. the backing object files, used when the archive is only logical in CcInfo
build_archive_query_expr() {
  cat <<'EOF'
[
  (
    lib.pic_static_library.short_path if lib.pic_static_library else lib.static_library.short_path,
    lib.pic_static_library.path if lib.pic_static_library else lib.static_library.path,
    [file.path for file in (lib.pic_objects if lib.pic_static_library else lib.objects)]
  )
  for linker_input in providers(target)["CcInfo"].linking_context.linker_inputs.to_list()
  for lib in linker_input.libraries
  if lib.pic_static_library or lib.static_library
]
EOF
}

mkdir -p "$DST"

readonly KONAN_DEPS="${KONAN_DEPS:-$KONAN_HOME/../dependencies}"
readonly KONAN_LLVM_RESOURCE_DIR="$(resolve_konan_llvm_resource_dir "$KONAN_DEPS")"
readonly LLVM_AR="$(cd "$(dirname "$KONAN_LLVM_RESOURCE_DIR")/../../.." && pwd)/bin/llvm-ar"
readonly QUERY_EXPR="$(build_archive_query_expr)"

# Keep the Bazel invocation flags in one place so the build and cquery steps operate on the exact same target graph.
# The KONAN_* defines are mainly relevant for Linux, but keeping one shared argument list makes the target handling
# simpler and avoids drifting build vs. query inputs.
readonly BAZEL_ARGS=(
  "--config=$KONAN_TARGET"
  "--config=$CONFIG"
  "--define=KONAN_DEPS=$KONAN_DEPS"
  "--define=KONAN_LLVM_RESOURCE_DIR=$KONAN_LLVM_RESOURCE_DIR"
  "--define=KONAN_HOME=$KONAN_HOME"
)

log "Building $LABEL archives into $DST"
log "KONAN_HOME: $KONAN_HOME"
log "KONAN_TARGET: $KONAN_TARGET"
log "KONAN_DEPS: $KONAN_DEPS"
log "KONAN_LLVM_RESOURCE_DIR: $KONAN_LLVM_RESOURCE_DIR"

set -x
bazel build "$LABEL" "${BAZEL_ARGS[@]}"
set +x

# After building the target once, query the ordered linker inputs that should be published in the bundle.
archive_tuples="$(
  bazel cquery "$LABEL" \
    "${BAZEL_ARGS[@]}" \
    --output=starlark \
    --starlark:expr="$QUERY_EXPR"
)"

ARCHIVE_TUPLES="$archive_tuples" ARCHIVE_DST="$DST" ARCHIVE_LLVM_AR="$LLVM_AR" python3 - <<'PY'
import ast
import os
import shutil
import subprocess
from pathlib import Path
from typing import List

tuples = ast.literal_eval(os.environ["ARCHIVE_TUPLES"])
dst = Path(os.environ["ARCHIVE_DST"])
llvm_ar = Path(os.environ["ARCHIVE_LLVM_AR"])
lib_root = dst / "lib"
manifest = dst / "metadata" / "archives.txt"


def to_bundle_relative_path(short_path: str) -> Path:
    # Bazel reports external repos as "../repo+/...". Drop the Bazel-specific "../" / "external" framing, but keep
    # the repo-qualified structure itself so repeated basenames like Abseil's libinternal.a do not collide.
    if short_path.startswith("../"):
        short_path = short_path[3:]
    return normalize_bundle_relative_path(Path(short_path))


def normalize_bundle_relative_path(path: Path) -> Path:
    # Bazel exposes c-ares as libares.lo even though it is an ar archive. Normalize logical archive
    # names to .a so downstream static linkers treat the bundle entries as archives consistently.
    if path.suffix == ".lo":
        return path.with_suffix(".a")
    return path


def pack_archive_from_objects(destination: Path, short_path: str, object_paths: List[str]) -> None:
    materialized_objects = [Path(object_path) for object_path in object_paths]
    missing_objects = [str(object_path) for object_path in materialized_objects if not object_path.exists()]
    if missing_objects:
        raise RuntimeError(
            f"Archive {short_path} is not materialized and some object files are missing: {missing_objects}"
        )
    if not materialized_objects:
        raise RuntimeError(f"Archive {short_path} is not materialized and has no object files to pack")

    # Some CcInfo entries are only logical archives. Rebuild those from their object list so the published bundle
    # still contains a complete archive set instead of depending on Bazel's internal linker behavior.
    subprocess.run(
        [str(llvm_ar), "rcs", str(destination), *(str(object_path) for object_path in materialized_objects)],
        check=True,
    )


def materialize_archive(short_path: str, archive_path: str, object_paths: List[str]) -> Path:
    relative_path = to_bundle_relative_path(short_path)
    destination = lib_root / relative_path
    destination.parent.mkdir(parents=True, exist_ok=True)

    # Prefer Bazel's emitted archive when it exists. Fall back to rebuilding only for logical libraries that appear
    # in CcInfo but were not written as standalone .a files in bazel-out.
    source = Path(archive_path)
    if source.exists():
        shutil.copy2(source, destination)
    else:
        pack_archive_from_objects(destination, short_path, object_paths)

    return relative_path

if lib_root.exists():
    shutil.rmtree(lib_root)
manifest.parent.mkdir(parents=True, exist_ok=True)
lib_root.mkdir(parents=True, exist_ok=True)

manifest_lines = []
seen = set()

for short_path, archive_path, object_paths in tuples:
    relative = to_bundle_relative_path(short_path)
    destination_key = str(lib_root / relative)
    if destination_key not in seen:
        materialize_archive(short_path, archive_path, object_paths)
        seen.add(destination_key)

    # Keep duplicates in the manifest if Bazel listed them multiple times. Link order matters; file copying does not.
    manifest_lines.append(f"lib/{relative.as_posix()}")

manifest.write_text("\n".join(manifest_lines) + "\n")
PY
