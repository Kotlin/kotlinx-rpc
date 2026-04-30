#!/usr/bin/env bash
# Persistent stress harness for the :grpc:grpc-core native test suite (KRPC-597).
#
# Loops the K/N debug-test binary N times under the LD_PRELOAD/DYLD_INSERT_LIBRARIES
# crashbt shim and counts process-exit signatures (SIGSEGV/SIGABRT/SIGBUS) vs normal
# test failures vs successes. Per-run stdout+stderr lands in
# autoresearch-runs/batch_<stamp>_<target>/run_NNN.log; the shim's backtrace +
# memory map is interleaved into stderr on crash.
#
# Usage:
#   scripts/stress-native-grpc.sh [TARGET] [RUNS]
#     TARGET — linuxX64 | macosX64 | macosArm64 (default: auto-detect from host)
#     RUNS   — number of iterations (default: 400)
#
# Pre-conditions: the test binary, crashbt shim, and grpc-test-server fixture
# must already be built. From Gradle:
#   ./gradlew :grpc:grpc-core:linkDebugTest<Target> \
#             :buildCrashbtShim \
#             :tests:grpc-test-server:installDist
#
# A subset of grpc-core native tests (RawClient*, GrpcCoreClient*) need the
# grpc-test-server JVM fixture running on localhost:50051. The Gradle
# `<target>Test` task starts it via `withBackgroundTask` (see
# grpc-core/build.gradle.kts); when running the kexe directly we have to
# launch+await+kill it ourselves -- this script does so.
#
# In TeamCity, the build config invokes those Gradle tasks before this script.
# TC sets TEAMCITY_VERSION automatically, which switches the script into TC mode
# (build statistics + crash signatures emitted as ##teamcity[...] messages).
#
# Exit code: 0 if zero crashes, 1 otherwise. Test failures (assertion mismatches)
# are NOT counted as crashes — only process-exit signatures are.

set -u
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

# ---- args -----------------------------------------------------------------

detect_target() {
    case "$(uname -s)-$(uname -m)" in
        Linux-x86_64)  echo linuxX64 ;;
        Darwin-x86_64) echo macosX64 ;;
        Darwin-arm64)  echo macosArm64 ;;
        *)
            echo "ERROR: unsupported host: $(uname -s) $(uname -m)" >&2
            exit 2
            ;;
    esac
}

capitalise_target() {
    case "$1" in
        linuxX64)    echo LinuxX64 ;;
        macosX64)    echo MacosX64 ;;
        macosArm64)  echo MacosArm64 ;;
    esac
}

TARGET="${1:-$(detect_target)}"
RUNS="${2:-400}"

case "$TARGET" in
    linuxX64|macosX64|macosArm64) ;;
    *)
        echo "ERROR: unsupported target '$TARGET' (expected linuxX64 | macosX64 | macosArm64)" >&2
        exit 2
        ;;
esac

# ---- locate kexe + shim ---------------------------------------------------

KEXE="grpc/grpc-core/build/bin/${TARGET}/debugTest/test.kexe"

if [ ! -f "$KEXE" ]; then
    echo "ERROR: $KEXE not found. Build it first:" >&2
    echo "  ./gradlew :grpc:grpc-core:linkDebugTest$(capitalise_target "$TARGET")" >&2
    exit 2
fi

case "$(uname -s)" in
    Linux)  SHIM="build/crashbt/libcrashbt.so" ;;
    Darwin) SHIM="build/crashbt/libcrashbt.dylib" ;;
    *)
        echo "ERROR: unsupported host OS for crashbt shim: $(uname -s)" >&2
        exit 2
        ;;
esac

if [ ! -f "$SHIM" ]; then
    echo "ERROR: $SHIM not found. Build it first:" >&2
    echo "  ./gradlew :buildCrashbtShim" >&2
    exit 2
fi

# ---- grpc-test-server fixture --------------------------------------------

TEST_SERVER_DIR="tests/grpc-test-server/build"
TEST_SERVER_BIN="$TEST_SERVER_DIR/install/grpc-test-server/bin/grpc-test-server"

if [ ! -x "$TEST_SERVER_BIN" ]; then
    echo "ERROR: $TEST_SERVER_BIN not found. Build it first:" >&2
    echo "  ./gradlew :tests:grpc-test-server:installDist" >&2
    exit 2
fi

# ---- output dirs ----------------------------------------------------------

LOG_DIR="$ROOT/autoresearch-runs"
mkdir -p "$LOG_DIR"

STAMP="$(date +%Y%m%d_%H%M%S)"
BATCH_DIR="$LOG_DIR/batch_${STAMP}_${TARGET}"
mkdir -p "$BATCH_DIR"

# ---- TC mode --------------------------------------------------------------

IN_TC=0
if [ -n "${TEAMCITY_VERSION:-}" ]; then
    IN_TC=1
fi

tc_msg() {
    if [ "$IN_TC" -eq 1 ]; then
        echo "$1"
    fi
}

# ---- loop ------------------------------------------------------------------

echo "=== Native gRPC stress harness ===" >&2
echo "Target:      $TARGET" >&2
echo "Binary:      $KEXE" >&2
echo "Shim:        $SHIM" >&2
echo "Test server: $TEST_SERVER_BIN" >&2
echo "Runs:        $RUNS" >&2
echo "Batch dir:   $BATCH_DIR" >&2
echo "TC mode:     $IN_TC" >&2
echo >&2

# ---- start grpc-test-server fixture ---------------------------------------
# Mirrors the Gradle `withBackgroundTask` wiring in grpc-core/build.gradle.kts.
# RawClient*/GrpcCoreClient* tests dial localhost:50051 and would fail without it.
TEST_SERVER_LOG="$BATCH_DIR/test-server.log"
TEST_SERVER_PID=""

cleanup_test_server() {
    if [ -n "$TEST_SERVER_PID" ] && kill -0 "$TEST_SERVER_PID" 2>/dev/null; then
        echo "Stopping grpc-test-server (pid $TEST_SERVER_PID)..." >&2
        kill "$TEST_SERVER_PID" 2>/dev/null || true
        # Give it a moment to release the port, then SIGKILL if still alive.
        for _ in 1 2 3 4 5; do
            kill -0 "$TEST_SERVER_PID" 2>/dev/null || break
            sleep 0.5
        done
        kill -9 "$TEST_SERVER_PID" 2>/dev/null || true
    fi
}
trap cleanup_test_server EXIT INT TERM

echo "Starting grpc-test-server fixture..." >&2
(cd "$TEST_SERVER_DIR" && exec "./install/grpc-test-server/bin/grpc-test-server") \
    > "$TEST_SERVER_LOG" 2>&1 &
TEST_SERVER_PID=$!

# Wait for "[GRPC-TEST-SERVER] Server started" or 30s timeout.
ready=0
for _ in $(seq 1 60); do
    if grep -q "GRPC-TEST-SERVER. Server started" "$TEST_SERVER_LOG" 2>/dev/null; then
        ready=1
        break
    fi
    if ! kill -0 "$TEST_SERVER_PID" 2>/dev/null; then
        echo "ERROR: grpc-test-server exited before becoming ready. Log:" >&2
        cat "$TEST_SERVER_LOG" >&2
        exit 2
    fi
    sleep 0.5
done

if [ "$ready" -ne 1 ]; then
    echo "ERROR: grpc-test-server did not become ready within 30s. Log:" >&2
    cat "$TEST_SERVER_LOG" >&2
    exit 2
fi

echo "grpc-test-server ready (pid $TEST_SERVER_PID)" >&2
echo >&2

crashes=0
failures=0
successes=0
crash_signatures=()

# Tests excluded from stress runs:
#   GrpcCoreClientTest.shutdownNowInMiddleOfCall -- pre-existing 10s timeout on
#     macosArm64 (and likely linuxX64); fails identically under
#     `:grpc:grpc-core:<target>Test`. Including it would consume 75% of each
#     iteration's runtime on a known-broken test and push the 400-iter stress
#     run past the 20-minute budget.
KTEST_NEGATIVE_FILTER="*shutdownNowInMiddleOfCall*"

run_kexe() {
    local out="$1"
    case "$(uname -s)" in
        Linux)
            LD_PRELOAD="$ROOT/$SHIM" \
                "./$KEXE" "--ktest_negative_gradle_filter=$KTEST_NEGATIVE_FILTER" \
                > "$out" 2>&1
            ;;
        Darwin)
            DYLD_INSERT_LIBRARIES="$ROOT/$SHIM" \
            DYLD_FORCE_FLAT_NAMESPACE=1 \
                "./$KEXE" "--ktest_negative_gradle_filter=$KTEST_NEGATIVE_FILTER" \
                > "$out" 2>&1
            ;;
    esac
}

for i in $(seq 1 "$RUNS"); do
    OUT="$BATCH_DIR/run_$(printf '%03d' "$i").log"
    ts="$(date +%H:%M:%S)"
    label="$(printf '%03d/%03d' "$i" "$RUNS")"
    tc_msg "##teamcity[progressMessage 'Stress run $label']"

    run_kexe "$OUT"
    rc=$?

    if [ $rc -eq 0 ]; then
        echo "[$ts] run $label: ok"
        successes=$((successes + 1))
    elif grep -qE "SIGSEGV|SIGBUS|SIGABRT|SIGKILL|signal 11|signal 6|signal 9|Check failed:|core dumped|Aborted|Segmentation fault|pure virtual method called|terminate called|crashbt: signal" "$OUT"; then
        echo "[$ts] run $label: CRASH (rc=$rc)"
        crashes=$((crashes + 1))
        sig="$(grep -oE 'pure virtual method called|Segmentation fault|Aborted|SIGSEGV|SIGBUS|SIGABRT|Check failed:[^\\n]*|crashbt: signal [0-9]+' "$OUT" | head -1)"
        crash_signatures+=("run $i: $sig")
    elif grep -qE "FAILED|Assertion|AssertionError" "$OUT"; then
        echo "[$ts] run $label: test fail (rc=$rc)"
        failures=$((failures + 1))
    else
        echo "[$ts] run $label: CRASH (rc=$rc, unclassified)"
        crashes=$((crashes + 1))
        crash_signatures+=("run $i: unclassified rc=$rc")
    fi
done

# ---- summary ---------------------------------------------------------------

total=$((successes + failures + crashes))
crash_frac=$(awk -v c="$crashes" -v t="$total" 'BEGIN { printf "%.4f", c / t }')

echo
echo "=== Summary ==="
echo "Target:       $TARGET"
echo "Total runs:   $total"
echo "Successes:    $successes"
echo "Crashes:      $crashes"
echo "Test fails:   $failures"
echo "Crash frac:   $crash_frac"
echo "Batch dir:    $BATCH_DIR"

if [ "$IN_TC" -eq 1 ]; then
    echo "##teamcity[buildStatisticValue key='nativeStress.runs' value='$total']"
    echo "##teamcity[buildStatisticValue key='nativeStress.successes' value='$successes']"
    echo "##teamcity[buildStatisticValue key='nativeStress.crashes' value='$crashes']"
    echo "##teamcity[buildStatisticValue key='nativeStress.failures' value='$failures']"

    if [ "$crashes" -gt 0 ]; then
        for sig in "${crash_signatures[@]}"; do
            echo "##teamcity[message text='$sig' status='ERROR']"
        done
        echo "##teamcity[buildStatus text='{build.status.text}; native stress: $crashes/$total crashes ($crash_frac), $failures fails']"
    else
        echo "##teamcity[buildStatus text='{build.status.text}; native stress: $successes/$total clean, $failures fails']"
    fi
fi

if [ "$crashes" -gt 0 ]; then
    exit 1
fi

exit 0
