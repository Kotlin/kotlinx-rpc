#!/usr/bin/env bash
# Stress harness for the KRPC-597/604 native crash investigation.
#
# Runs the canonical crashing gRPC native tests in a loop, counts process-exit
# crashes and normal test failures, writes a crash-fraction metric to stdout
# (final line is a bare number for the autoresearch verify pipeline to pick up).
#
# Usage:
#   scripts/stress-native-grpc.sh [RUNS] [TARGET]
#     RUNS   — number of iterations (default: 10)
#     TARGET — gradle task name (default: macosArm64Test)
#
# Output:
#   Human-readable progress to stderr; every stdout line is for the harness
#   caller. The final stdout line is the crash-fraction (0.0 .. 1.0).

set -u
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

RUNS="${1:-10}"
TARGET="${2:-macosArm64Test}"

LOG_DIR="${ROOT}/autoresearch-runs"
mkdir -p "$LOG_DIR"

STAMP="$(date +%Y%m%d_%H%M%S)"
BATCH_DIR="${LOG_DIR}/batch_${STAMP}_${TARGET}"
mkdir -p "$BATCH_DIR"

# Canonical tests implicated in KRPC-604 (linuxX64 process-exit crashes).
# Each entry: fully-qualified test class. A --tests pattern joins them.
TEST_CLASSES=(
    "kotlinx.rpc.grpc.test.integration.ClientInterceptorTest"
    "kotlinx.rpc.grpc.test.integration.GrpcTlsTest"
    "kotlinx.rpc.grpc.test.integration.ServerInterceptorTest"
    "kotlinx.rpc.grpc.test.integration.MetadataTest"
    "kotlinx.rpc.grpc.test.integration.StreamingTest"
    "kotlinx.rpc.grpc.test.integration.GrpcTimeoutTest"
    "kotlinx.rpc.grpc.test.integration.GrpcEdgeCaseTest"
)
TEST_ARGS=()
for t in "${TEST_CLASSES[@]}"; do
    TEST_ARGS+=(--tests "$t")
done

# Gradle task path — grpc-core owns the desktopTest set (these tests are in
# commonTest/desktopTest and compile into :grpc:grpc-core:$TARGET).
GRADLE_TASK=":grpc:grpc-core:${TARGET}"

echo "=== KRPC-597/604 stress harness ===" >&2
echo "Target:    $GRADLE_TASK" >&2
echo "Runs:      $RUNS" >&2
echo "Batch dir: $BATCH_DIR" >&2
echo "Tests:" >&2
for t in "${TEST_CLASSES[@]}"; do echo "  - $t" >&2; done
echo >&2

crashes=0
failures=0
successes=0
metric_error=0

for i in $(seq 1 "$RUNS"); do
    OUT="$BATCH_DIR/run_$(printf '%02d' "$i").log"
    ts="$(date +%H:%M:%S)"
    echo -n "[$ts] run $i/$RUNS … " >&2

    # Keep compile/link caches (we're running the same binary); just rerun the test task.
    # Env var enables the debug-mode resource counter inside the test process.
    KOTLINX_RPC_NATIVE_DEBUG_COUNTERS=1 \
    ./gradlew "$GRADLE_TASK" "${TEST_ARGS[@]}" \
        --rerun-tasks \
        --no-daemon \
        > "$OUT" 2>&1
    rc=$?

    if [ $rc -eq 0 ]; then
        # Look for debug-counter leak assertion failures even on exit 0 — they
        # show up as non-fatal logs in the current iteration.
        if grep -q "NATIVE_COUNTER_LEAK" "$OUT"; then
            echo "LEAK (rc=0 but counters non-zero)" >&2
            failures=$((failures + 1))
        else
            echo "ok" >&2
            successes=$((successes + 1))
        fi
    else
        # Classify crash vs test failure by looking for the KRPC-597 signature.
        if grep -qE "Test running process exited unexpectedly|process exited unexpectedly|SIGSEGV|SIGBUS|SIGABRT|signal 11|signal 6" "$OUT"; then
            echo "CRASH (rc=$rc)" >&2
            crashes=$((crashes + 1))
        elif grep -qE "Tests? .* completed, .* failed" "$OUT" && ! grep -qE "Tests? .* completed, 0 failed" "$OUT"; then
            echo "test fail (rc=$rc)" >&2
            failures=$((failures + 1))
        else
            # Ambiguous — treat as test failure not crash.
            echo "fail (rc=$rc, unclassified)" >&2
            failures=$((failures + 1))
        fi
    fi
done

total=$((successes + failures + crashes))
if [ "$total" -eq 0 ]; then
    echo "ERROR: no runs completed" >&2
    # Output sentinel so autoresearch can detect metric-error
    echo "NaN"
    exit 2
fi

# Crash fraction only (the primary metric). Failures tracked separately for
# guard purposes (if N tests fail that didn't use to, we have a regression).
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

# Final stdout line — the autoresearch verify metric.
echo "$crash_frac"
