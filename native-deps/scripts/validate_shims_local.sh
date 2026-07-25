#!/usr/bin/env bash
#
# Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
#
# Local validation for the native-deps shims (protobuf + gRPC).
#
# Reproduces the full local validation flow used when bumping the gRPC / protobuf
# native dependency versions:
#
#   1. protobuf shim        -> assemble (all enabled targets)
#   2. gRPC C prebuilt      -> build + publish to the local root build/repo, then mirror
#                              the artifacts into native-deps/build/repo (where the shim resolves them)
#   3. gRPC shim            -> assemble (all enabled targets)
#   4. symbol-overlap       -> analyze each target; NON-DESTRUCTIVELY merge newly-overlapping
#                              gRPC Abseil archives into grpc/overlap-archive-excludes.txt,
#                              then rebuild the gRPC shim and re-check
#   5. fixture tests        -> :tests:test (opt-in enforcement + KLIB metadata, both shims)
#   6. publish both shims   -> publishAllPublicationsToNativeDepsBuildRepoRepository (LOCAL only)
#   7. consumer compiles    -> :protobuf:protobuf-lite + :grpc:grpc-core for the host KN target
#
# Nothing is published to any remote repository.
#
# ---------------------------------------------------------------------------------------------------
# IMPORTANT CAVEATS
# ---------------------------------------------------------------------------------------------------
# * The gRPC NATIVE version is capped by the Bazel Central Registry (bcr.bazel.build/modules/grpc).
#   The `--features=-layering_check` workaround for gRPC 1.81.x, protobuf 33.x `upb_c_proto_library`
#   generates well-known-type upb headers that are not layering_check-clean; lives in
#   native-deps/grpc-c-prebuilt/.bazelrc. This script verifies it is present.
#
# * The overlap excludes written by this script drop gRPC bundled Abseil archives that the
#   protobuf shim already ships (gRPC-core gets them transitively from protobuf-shim). These are
#   correct only if the two shims share the SAME Abseil LTS namespace (they do once gRPC/protobuf
#   versions align). This MUST be link-validated on a real Linux host (CI): a non-Linux host can
#   build/analyze but cannot prove final Linux linking. Review `git diff` on the excludes file.
#
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
PREBUILT="$NATIVE_DEPS/grpc-c-prebuilt"
CATALOG="$REPO_ROOT/versions-root/libs.versions.toml"
EXCLUDES_FILE="$SHIMS/grpc/overlap-archive-excludes.txt"
ANALYZER="$SHIMS/grpc/tools/analyze_overlap.py"
ROOT_REPO_DIR="$REPO_ROOT/build/repo/org/jetbrains/kotlinx"
NATIVE_DEPS_REPO_DIR="$NATIVE_DEPS/build/repo/org/jetbrains/kotlinx"

# kotlinName:bazelName for every native-deps target.
ALL_TARGETS="iosArm64:ios_arm64 iosSimulatorArm64:ios_simulator_arm64 iosX64:ios_x64 macosArm64:macos_arm64 tvosArm64:tvos_arm64 tvosSimulatorArm64:tvos_simulator_arm64 watchosArm64:watchos_arm64 watchosDeviceArm64:watchos_device_arm64 watchosSimulatorArm64:watchos_simulator_arm64 linuxArm64:linux_arm64 linuxX64:linux_x64"

# ----------------------------------------------------------------------------- options
WRITE_EXCLUDES=1     # merge overlap excludes (default on; --no-write-excludes to disable)
SKIP_PREBUILT=0      # skip the long gRPC C prebuilt build + mirror
ANALYSIS_ONLY=0      # only run the overlap analysis against already-built klibs

usage() {
  cat <<EOF
Usage: native-deps/scripts/validate_shims_local.sh [options]

  --no-write-excludes   Run overlap analysis read-only (do not modify overlap-archive-excludes.txt)
  --skip-prebuilt       Skip building/mirroring the gRPC C prebuilt (assume native-deps/build/repo is populated)
  --analysis-only       Only (re)run the symbol-overlap analysis against already-built klibs, then exit
  -h, --help            Show this help

Env:
  EXTRA_GRADLE_ARGS     Extra args appended to every Gradle invocation (e.g. "--stacktrace")
EOF
}

while [ $# -gt 0 ]; do
  case "$1" in
    --no-write-excludes) WRITE_EXCLUDES=0 ;;
    --skip-prebuilt)     SKIP_PREBUILT=1 ;;
    --analysis-only)     ANALYSIS_ONLY=1 ;;
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
  Darwin-arm64)            HOST_KOTLIN=macosArm64; HOST_BAZEL=macos_arm64 ;;
  Linux-x86_64)            HOST_KOTLIN=linuxX64;   HOST_BAZEL=linux_x64 ;;
  Linux-aarch64|Linux-arm64) HOST_KOTLIN=linuxArm64; HOST_BAZEL=linux_arm64 ;;
  *) die "Unsupported host: $HOST_OS-$HOST_ARCH" ;;
esac
HOST_KOTLIN_CAP="$(cap_first "$HOST_KOTLIN")"

# ----------------------------------------------------------------------------- prerequisites
check_prereqs() {
  section "Prerequisites"
  command -v java >/dev/null 2>&1 || die "java not found on PATH"
  command -v python3 >/dev/null 2>&1 || die "python3 not found on PATH; needed for overlap analysis"
  if ! command -v bazel >/dev/null 2>&1 && ! command -v bazelisk >/dev/null 2>&1; then
    die "bazel/bazelisk not found on PATH; install bazelisk, e.g. brew install bazelisk"
  fi
  if [ "$HOST_OS" = "Darwin" ] && [ -d "/Library/Developer/CommandLineTools" ] && [ ! -d "/Applications/Xcode.app" ]; then
    warn "Only Command Line Tools detected; full Xcode is recommended for Apple cross-compilation."
  fi
  if ! grep -q -- '--features=-layering_check' "$PREBUILT/.bazelrc" 2>/dev/null; then
    warn "native-deps/grpc-c-prebuilt/.bazelrc has no '--features=-layering_check'; the gRPC 1.81.x prebuilt build may fail on the macOS host layering_check."
  fi
  info "host: $HOST_OS/$HOST_ARCH  ->  KN target $HOST_KOTLIN [$HOST_BAZEL]"
  info "grpc native shim: $(catalog_version internal-native-grpc-shim)   protobuf native shim: $(catalog_version internal-native-protobuf-shim)"
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
  section "1/7  protobuf shim :assemble"
  run_gradle "$SHIMS" ":kotlinx-rpc-protobuf-shim:assemble"
}

build_and_mirror_prebuilt() {
  section "2/7  gRPC C prebuilt -> local build/repo -> native-deps/build/repo"
  if [ "$SKIP_PREBUILT" -eq 1 ]; then
    info "--skip-prebuilt set; not rebuilding. Verifying mirror is present..."
  else
    info "Building all gRPC C bundles; this is the long step, ~30 min cold."
    run_gradle "$PREBUILT" "publishAllPublicationsToBuildRepoRepository"
    info "Mirroring grpc-core-c-* artifacts into native-deps/build/repo ..."
    mkdir -p "$NATIVE_DEPS_REPO_DIR"
    local n=0 d
    shopt -s nullglob
    for d in "$ROOT_REPO_DIR"/kotlinx-rpc-grpc-core-c-*; do
      cp -R "$d" "$NATIVE_DEPS_REPO_DIR/"
      n=$((n + 1))
    done
    shopt -u nullglob
    info "Mirrored $n grpc-core-c artifact dirs."
  fi
  local grpc_base; grpc_base="$(catalog_version internal-native-grpc-shim)"; grpc_base="${grpc_base%-*}"
  [ -f "$NATIVE_DEPS_REPO_DIR/kotlinx-rpc-grpc-core-c-deps-${HOST_BAZEL//_/}/$grpc_base/kotlinx-rpc-grpc-core-c-deps-${HOST_BAZEL//_/}-$grpc_base.zip" ] \
    || warn "Host prebuilt bundle for $grpc_base not found in native-deps/build/repo; the gRPC shim build may fail to resolve it."
}

build_grpc_shim() {
  section "3/7  gRPC shim :assemble"
  run_gradle "$SHIMS" ":kotlinx-rpc-grpc-core-shim:assemble"
}

# Resolve a single cinterop klib by glob (errors if missing/ambiguous).
resolve_klib() { # absoluteGlob
  local matches=""
  matches="$(ls $1 2>/dev/null)" || matches=""
  [ -n "$matches" ] || return 1
  printf '%s\n' "$matches" | head -1
}

# Run the analyzer read-only and print "Candidate grpc archives to exclude" archive names only.
overlap_candidates() { # kotlinName bazelName
  local kotlin="$1" bazel="$2" gk pk out
  gk=$(resolve_klib "$SHIMS/grpc/build/libs/kotlinx-rpc-grpc-core-shim-${kotlin}Cinterop-grpcCoreInteropMain-*.klib") || return 0
  pk=$(resolve_klib "$SHIMS/protobuf/build/libs/kotlinx-rpc-protobuf-shim-${kotlin}Cinterop-libprotowireMain-*.klib") || return 0
  out="$(python3 "$ANALYZER" --grpc-klib "$gk" --protobuf-klib "$pk" --target "$bazel")" || { warn "overlap analysis failed for $bazel"; return 0; }
  # Echo the full report to stderr for visibility, parse archive names from the candidate block to stdout.
  printf '%s\n' "$out" >&2
  printf '%s\n' "$out" | awk '/^Candidate grpc archives to exclude:/{g=1;next} /^Archives requiring manual handling:/{g=0} /^[[:space:]]*$/{g=0} g && /^- /{print $2}'
}

# Idempotently append "<scope>:<archive>" to the excludes file (preserves all existing content).
add_exclude() { # scope archive
  local line="$1:$2"
  grep -qxF "$line" "$EXCLUDES_FILE" 2>/dev/null && return 0
  printf '%s\n' "$line" >> "$EXCLUDES_FILE"
  info "+ $line"
}

analyze_overlaps() {
  local we_label="off"
  if [ "$WRITE_EXCLUDES" -eq 1 ]; then we_label="on"; fi
  section "4/7  symbol-overlap analysis, write-excludes=$we_label"
  command -v llvm-nm >/dev/null 2>&1 || command -v nm >/dev/null 2>&1 || die "llvm-nm/nm not found; needed by the overlap analyzer"
  local before after pair kotlin bazel arch changed=0 total=0
  before="$(cksum "$EXCLUDES_FILE" 2>/dev/null | awk '{print $1}')"
  for pair in $ALL_TARGETS; do
    kotlin="${pair%%:*}"; bazel="${pair##*:}"
    printf '\n  --- overlap: %s ---\n' "$bazel"
    # Collect candidate archives (analyzer report is echoed to stderr by overlap_candidates).
    local cands; cands="$(overlap_candidates "$kotlin" "$bazel")"
    [ -n "$cands" ] || { info "no candidate overlaps"; continue; }
    total=$((total + 1))
    if [ "$WRITE_EXCLUDES" -eq 1 ]; then
      for arch in $cands; do add_exclude "$bazel" "$arch"; done
    else
      for arch in $cands; do info "candidate, not written: $bazel:$arch"; done
    fi
  done
  after="$(cksum "$EXCLUDES_FILE" 2>/dev/null | awk '{print $1}')"
  [ "$before" != "$after" ] && changed=1

  if [ "$changed" -eq 1 ]; then
    warn "overlap-archive-excludes.txt was updated. Review 'git diff' and LINK-VALIDATE on a Linux host - CI."
    if [ "$ANALYSIS_ONLY" -eq 0 ]; then
      section "4b   rebuild gRPC shim to apply new excludes, then re-check"
      run_gradle "$SHIMS" ":kotlinx-rpc-grpc-core-shim:assemble"
      local residual=0
      for pair in $ALL_TARGETS; do
        kotlin="${pair%%:*}"; bazel="${pair##*:}"
        local cands; cands="$(overlap_candidates "$kotlin" "$bazel")"
        if [ -n "$cands" ]; then
          residual=1
          warn "residual overlap remains on $bazel after excludes: $cands"
        fi
      done
      [ "$residual" -eq 0 ] && info "Re-check clean: no residual overlaps after applying excludes."
    fi
  else
    info "No overlap changes; excludes already cover everything, or no overlaps found."
  fi
}

run_fixture_tests() {
  section "5/7  fixture tests :tests:test"
  run_gradle "$SHIMS" ":tests:test"
}

publish_shims_local() {
  section "6/7  publish both shims + annotation to LOCAL native-deps/build/repo"
  run_gradle "$SHIMS" \
    ":kotlinx-rpc-grpc-core-shim:publishAllPublicationsToNativeDepsBuildRepoRepository" \
    ":kotlinx-rpc-protobuf-shim:publishAllPublicationsToNativeDepsBuildRepoRepository" \
    ":kotlinx-rpc-native-shims-annotation:publishAllPublicationsToNativeDepsBuildRepoRepository"
}

consumer_compiles() {
  section "7/7  main-project consumer compiles: $HOST_KOTLIN"
  run_gradle "$REPO_ROOT" \
    ":protobuf:protobuf-lite:compileKotlin${HOST_KOTLIN_CAP}" \
    ":grpc:grpc-core:compileKotlin${HOST_KOTLIN_CAP}"
}

# ----------------------------------------------------------------------------- main
main() {
  local start; start="$(date +%s 2>/dev/null)" || start=0
  check_prereqs
  ensure_host_objcopy

  if [ "$ANALYSIS_ONLY" -eq 1 ]; then
    analyze_overlaps
  else
    build_protobuf_shim
    build_and_mirror_prebuilt
    build_grpc_shim
    analyze_overlaps
    run_fixture_tests
    publish_shims_local
    consumer_compiles
  fi

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
  warn "If overlap-archive-excludes.txt changed, the new Linux excludes are NOT link-validated by this"
  warn "macOS/host run. Validate a Linux binary that links BOTH shims on CI before relying on them."
}

main "$@"
