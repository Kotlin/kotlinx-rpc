#!/usr/bin/env bash
set -euo pipefail
: "${KONAN_HOME:?KONAN_HOME must be set}"

KONAN_DEPS=/Users/johannes.zottele/.konan/dependencies
# Pick your target triple + matching konan sysroot
TRIPLE=x86_64-unknown-linux-gnu
KONAN_GCC="$KONAN_DEPS/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2"
SYSROOT="$KONAN_GCC/$TRIPLE/sysroot"

export SDKROOT=
export CPATH=

exec "$KONAN_HOME/bin/run_konan" clang clang macos_arm64 \
  -target "$TRIPLE" \
  --sysroot="$SYSROOT" \
  -B"$KONAN_GCC/$TRIPLE/bin" \
  --gcc-toolchain="$KONAN_GCC" \
  "$@"
