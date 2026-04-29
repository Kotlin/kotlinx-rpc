#!/usr/bin/env bash
# Docker stress harness for the KRPC-597/604 native crash investigation.
#
# Assumes grpc/grpc-core/build/bin/linuxX64/debugTest/test.kexe has already
# been cross-compiled from macOS via:
#   ./gradlew :grpc:grpc-core:linkDebugTestLinuxX64
#
# Runs test.kexe inside a linux/amd64 Ubuntu container N times, classifies
# each run (ok / crash / test fail), emits crash-fraction on final stdout line.
#
# Usage:
#   scripts/stress-native-grpc-docker.sh [RUNS] [IMAGE]
#     RUNS  — number of iterations (default: 20)
#     IMAGE — Docker image (default: ubuntu:22.04)
#
# The Kotlin/Native test binary accepts `--ktest_filter=` to narrow test classes.

set -u
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

RUNS="${1:-20}"
IMAGE="${2:-ubuntu:22.04}"

KEXE="grpc/grpc-core/build/bin/linuxX64/debugTest/test.kexe"

if [ ! -f "$KEXE" ]; then
    echo "ERROR: $KEXE not found. Build it first:" >&2
    echo "  ./gradlew :grpc:grpc-core:linkDebugTestLinuxX64" >&2
    exit 2
fi

LOG_DIR="$ROOT/autoresearch-runs"
mkdir -p "$LOG_DIR"

STAMP="$(date +%Y%m%d_%H%M%S)"
BATCH_DIR="$LOG_DIR/batch_${STAMP}_docker_linuxX64"
mkdir -p "$BATCH_DIR"

# Canonical crashing test classes from KRPC-604 evidence. Kotlin/Native gtest-style
# filter uses `:` as separator between patterns (not `|`), `*` matches any substring.
# Negative pattern after `-` excludes env-dependent test (requires external host).
TEST_FILTER='kotlinx.rpc.grpc.test.integration.ClientInterceptorTest.*:kotlinx.rpc.grpc.test.integration.GrpcTlsTest.*:kotlinx.rpc.grpc.test.integration.ServerInterceptorTest.*:kotlinx.rpc.grpc.test.integration.MetadataTest.*:kotlinx.rpc.grpc.test.integration.StreamingTest.*:kotlinx.rpc.grpc.test.integration.GrpcTimeoutTest.*:kotlinx.rpc.grpc.test.integration.GrpcEdgeCaseTest.*-*test?client?side?TLS?with?default?credentials*'

echo "=== KRPC-597/604 stress harness (Docker linuxX64) ===" >&2
echo "Image:     $IMAGE" >&2
echo "Binary:    $KEXE" >&2
echo "Runs:      $RUNS" >&2
echo "Batch dir: $BATCH_DIR" >&2
echo "Filter:    $(echo "$TEST_FILTER" | tr '|' '\n' | wc -l | tr -d ' ') test classes" >&2
echo >&2

# Sanity: make sure the image is pulled (silent on success).
docker pull --platform linux/amd64 "$IMAGE" >/dev/null 2>&1 || true

# Smoke-check the binary runs at all (non-stress).
echo -n "Smoke check: " >&2
docker run --rm --platform linux/amd64 \
    -v "$ROOT:$ROOT:ro" \
    -w "$ROOT" \
    "$IMAGE" \
    "$KEXE" --ktest_filter='kotlinx.rpc.grpc.test.integration.GrpcEdgeCaseTest.*' \
    > "$BATCH_DIR/smoke.log" 2>&1
rc=$?
if [ $rc -ne 0 ]; then
    echo "FAILED (rc=$rc) — check $BATCH_DIR/smoke.log" >&2
    tail -20 "$BATCH_DIR/smoke.log" >&2
    echo "NaN"
    exit 3
fi
echo "ok" >&2
echo >&2

crashes=0
failures=0
successes=0

for i in $(seq 1 "$RUNS"); do
    OUT="$BATCH_DIR/run_$(printf '%02d' "$i").log"
    ts="$(date +%H:%M:%S)"
    echo -n "[$ts] run $i/$RUNS … " >&2

    docker run --rm --platform linux/amd64 \
        --network=host \
        -v "$ROOT:$ROOT:ro" \
        -w "$ROOT" \
        "$IMAGE" \
        "$KEXE" --ktest_filter="$TEST_FILTER" \
        > "$OUT" 2>&1
    rc=$?

    if [ $rc -eq 0 ]; then
        # Kotlin/Native test runner returns 0 only on all-pass.
        echo "ok" >&2
        successes=$((successes + 1))
    elif grep -qE "SIGSEGV|SIGBUS|SIGABRT|SIGKILL|signal 11|signal 6|signal 9|Check failed:|F[0-9]+ .*filter_stack_call|core dumped|Aborted|Segmentation fault" "$OUT"; then
        echo "CRASH (rc=$rc)" >&2
        crashes=$((crashes + 1))
    elif grep -qE "FAILED|Assertion|AssertionError" "$OUT"; then
        echo "test fail (rc=$rc)" >&2
        failures=$((failures + 1))
    else
        # Non-zero exit, no recognizable signature — classify as crash to be safe.
        echo "CRASH (rc=$rc, unclassified)" >&2
        crashes=$((crashes + 1))
    fi
done

total=$((successes + failures + crashes))
if [ "$total" -eq 0 ]; then
    echo "ERROR: no runs completed" >&2
    echo "NaN"
    exit 4
fi

crash_frac=$(awk -v c="$crashes" -v t="$total" 'BEGIN { printf "%.4f", c / t }')

{
    echo
    echo "=== Summary ==="
    echo "Total runs:   $total"
    echo "Successes:    $successes"
    echo "Crashes:      $crashes"
    echo "Test fails:   $failures"
    echo "Crash frac:   $crash_frac"
    echo "Batch dir:    $BATCH_DIR"
} >&2

echo "$crash_frac"
