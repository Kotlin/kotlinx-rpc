#!/usr/bin/env bash
#
# Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#
# Local validation for the native-deps protobuf shim.
#
# Reproduces the full local validation flow used when bumping the protobuf
# native dependency version:
#
#   1. protobuf shim        -> assemble (all enabled targets)
#   2. publish shim         -> publishAllPublicationsToNativeDepsBuildRepoRepository (LOCAL only)
#   3. consumer compiles    -> :protobuf:protobuf-lite for the host KN target
#
# Nothing is published to any remote repository.
#
# ---------------------------------------------------------------------------------------------------
# IMPORTANT CAVEATS
# ---------------------------------------------------------------------------------------------------
# * On macOS the protobuf shim KRPC-540 Linux symbol rewrite needs a host llvm-objcopy. If none
#   is on PATH this script discovers one (Homebrew llvm, or a Kotlin/Native NDK bundle under
#   ~/.konan) and exposes it via a small shim dir on PATH. On Linux the Konan-managed objcopy is used.
#
set -euo pipefail

# ----------------------------------------------------------------------------- paths / constants
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# This script lives in native-deps/scripts/, so native-deps is its parent directory.
NATIVE_DEPS="$(cd "$SCRIPT_DIR/.." && pwd)"
REPO_ROOT="$(cd "$NATIVE_DEPS/.." && pwd)"
SHIMS="$NATIVE_DEPS/shims"
CATALOG="$REPO_ROOT/versions-root/libs.versions.toml"

# ----------------------------------------------------------------------------- options
usage() {
  cat <<EOF
Usage: native-deps/scripts/validate_shims_local.sh [options]

  -h, --help            Show this help

Env:
  EXTRA_GRADLE_ARGS     Extra args appended to every Gradle invocation (e.g. "--stacktrace")
EOF
}

while [ $# -gt 0 ]; do
  case "$1" in
    -h|--help)           usage; exit 0 ;;
    *) echo "Unknown option: $1" >&2; usage; exit 2 ;;
  esac
  shift
done

EXTRA_GRADLE_ARGS="${EXTRA_GRADLE_ARGS:-}"

# ----------------------------------------------------------------------------- logging helpers
section() { printf '\n==== %s ====\n' "$*"; }
info()    { printf '  %s\n' "$*"; }
warn()    { printf 'WARNING: %s\n' "$*" >&2; }
die()     { printf 'ERROR: %s\n' "$*" >&2; exit 1; }

cap_first() { # capitalize first character (bash 3.2 safe)
  local s="$1" first rest
  first=$(printf '%s' "$s" | cut -c1 | tr '[:lower:]' '[:upper:]')
  rest=$(printf '%s' "$s" | cut -c2-)
  printf '%s%s' "$first" "$rest"
}

catalog_version() { # alias -> version string
  grep -E "^$1[[:space:]]*=" "$CATALOG" | head -1 | sed -E 's/.*= *"//; s/".*//'
}

run_gradle() { # dir task...
  local dir="$1"; shift
  info "${dir#$REPO_ROOT/}: ./gradlew $*"
  ( cd "$dir" && ./gradlew --console=plain $EXTRA_GRADLE_ARGS "$@" )
}

# ----------------------------------------------------------------------------- host detection
HOST_OS="$(uname -s)"
HOST_ARCH="$(uname -m)"
case "$HOST_OS-$HOST_ARCH" in
  Darwin-arm64)              HOST_KOTLIN=macosArm64 ;;
  Linux-x86_64)              HOST_KOTLIN=linuxX64 ;;
  Linux-aarch64|Linux-arm64) HOST_KOTLIN=linuxArm64 ;;
  *) die "Unsupported host: $HOST_OS-$HOST_ARCH" ;;
esac
HOST_KOTLIN_CAP="$(cap_first "$HOST_KOTLIN")"

# ----------------------------------------------------------------------------- prerequisites
check_prereqs() {
  section "Prerequisites"
  command -v java >/dev/null 2>&1 || die "java not found on PATH"
  if ! command -v bazel >/dev/null 2>&1 && ! command -v bazelisk >/dev/null 2>&1; then
    die "bazel/bazelisk not found on PATH; install bazelisk, e.g. brew install bazelisk"
  fi
  if [ "$HOST_OS" = "Darwin" ] && [ -d "/Library/Developer/CommandLineTools" ] && [ ! -d "/Applications/Xcode.app" ]; then
    warn "Only Command Line Tools detected; full Xcode is recommended for Apple cross-compilation."
  fi
  info "host: $HOST_OS/$HOST_ARCH  ->  KN target $HOST_KOTLIN"
  info "protobuf native shim: $(catalog_version internal-native-protobuf-shim)"
}

# ----------------------------------------------------------------------------- host objcopy (macOS)
ensure_host_objcopy() {
  section "Host objcopy - protobuf KRPC-540 Linux symbol rewrite"
  if [ "$HOST_OS" != "Darwin" ]; then
    info "Linux host: the protobuf shim uses the Konan-managed objcopy automatically."
    return 0
  fi
  if command -v llvm-objcopy >/dev/null 2>&1 || command -v objcopy >/dev/null 2>&1 || xcrun --find llvm-objcopy >/dev/null 2>&1; then
    info "Host objcopy already available."
    return 0
  fi
  local found="" cand
  if command -v brew >/dev/null 2>&1; then
    local brew_llvm=""
    brew_llvm="$(brew --prefix llvm 2>/dev/null)" || brew_llvm=""
    cand="$brew_llvm/bin/llvm-objcopy"
    [ -x "$cand" ] && found="$cand"
  fi
  if [ -z "$found" ]; then
    for cand in "$HOME"/.konan/dependencies/*/bin/llvm-objcopy; do
      [ -x "$cand" ] && "$cand" --version >/dev/null 2>&1 && { found="$cand"; break; }
    done
  fi
  [ -n "$found" ] || die "No host llvm-objcopy/objcopy found. Install one: 'brew install llvm'."
  local shimdir="$HOME/.krpc-objcopy-shim/bin"
  mkdir -p "$shimdir"
  ln -sf "$found" "$shimdir/llvm-objcopy"
  export PATH="$shimdir:$PATH"
  info "Linked host llvm-objcopy: $found"
}

# ----------------------------------------------------------------------------- steps
build_protobuf_shim() {
  section "1/3  protobuf shim :assemble"
  run_gradle "$SHIMS" ":kotlinx-rpc-protobuf-shim:assemble"
}

publish_shims_local() {
  section "2/3  publish protobuf shim + annotation to LOCAL native-deps/build/repo"
  run_gradle "$SHIMS" \
    ":kotlinx-rpc-protobuf-shim:publishAllPublicationsToNativeDepsBuildRepoRepository" \
    ":kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository"
}

consumer_compiles() {
  section "3/3  main-project consumer compiles: $HOST_KOTLIN"
  run_gradle "$REPO_ROOT" \
    ":protobuf:protobuf-lite:compileKotlin${HOST_KOTLIN_CAP}"
}

# ----------------------------------------------------------------------------- main
main() {
  local start; start="$(date +%s 2>/dev/null)" || start=0
  check_prereqs
  ensure_host_objcopy

  build_protobuf_shim
  publish_shims_local
  consumer_compiles

  local end; end="$(date +%s 2>/dev/null)" || end=0
  section "DONE"
  info "All local validations completed successfully."
  if [ "$start" != 0 ]; then
    local elapsed=$(( end - start ))
    local mins=$(( elapsed / 60 ))
    local secs=$(( elapsed % 60 ))
    info "Elapsed: ${mins}m ${secs}s"
  fi
  echo
}

main "$@"
