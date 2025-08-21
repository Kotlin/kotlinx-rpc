#!/usr/bin/env bash
#
# Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#

set -Eeuo pipefail
trap 'echo "ERROR: Build failed at ${BASH_SOURCE}:${LINENO}" >&2' ERR

# Builds a static library for all platforms (os/arch).
#
# Usage:
#   ./build_all.sh //path:libtarget out_dir
# Example:
#   ./build_all.sh :protowire_static out


LABEL="${1:?need bazel label}"
OUTDIR="${2:?need output dir}"
mkdir -p "$OUTDIR"

# Compilation Config
CONFIG=release

# Platform labels
MACOS=@build_bazel_apple_support//platforms:macos_arm64
IOS_DEV=@build_bazel_apple_support//platforms:ios_arm64
IOS_SIM=@build_bazel_apple_support//platforms:ios_sim_arm64
WATCHOS_ARM64_32_DEV=@build_bazel_apple_support//platforms:watchos_arm64_32
WATCHOS_ARM64_SIM=@build_bazel_apple_support//platforms:watchos_arm64

build_one() {
  local plat="$1" short="$2" os="$3"
  echo "==> Building $LABEL for $short" >&2
  bazel build "$LABEL" --platforms="$plat" --apple_platform_type="$os" --config="$CONFIG" --announce_rc >/dev/null

  # Ask Bazel what file(s) this target produced under this platform
  local out
  out="$(bazel cquery "$LABEL" --platforms="$plat" --apple_platform_type="$os" --config="$CONFIG" --output=files | head -n1)"
  [[ -n "$out" ]] || { echo "No output for $LABEL ($short)"; exit 1; }

  local dst="$OUTDIR/$(basename "$out" .a).${short}.a"
  cp -f "$out" "$dst"
}

build_one "$MACOS" macos_arm64 macos
build_one "$IOS_DEV" ios_arm64 ios
build_one "$IOS_SIM" ios_sim_arm64 ios
# TODO: Uncomment when activating WatchOS
#build_one "$WATCHOS_ARM64_32_DEV" watchos_arm64_32 watchos
#build_one "$WATCHOS_ARM64_SIM" watchos_sim_arm64 watchos

echo "Done. Artifacts in $OUTDIR"
