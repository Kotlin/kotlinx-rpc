#!/usr/bin/env bash
# KRPC-604 / KRPC-597 stress harness — TeamCity Linux agent variant.
#
# Assumes test.kexe has already been built by an earlier gradle step in the
# same TC build (./gradlew :grpc:grpc-core:linkDebugTestLinuxX64). The TC agent
# is itself Linux, so we run the binary directly — no Docker needed.
#
# Each run is launched with LD_PRELOAD=libcrashbt.so (built once from
# scripts/crashbt.c at the start of this script). The shim installs an
# in-process sigaction handler for SIGABRT/SIGSEGV/SIGBUS that prints a
# backtrace + /proc/self/maps to stderr before re-raising the signal, so a
# fatal exit (e.g. "pure virtual method called" → libstdc++ verbose terminate
# → abort) yields enough info in the per-run log to symbolicate via
# `addr2line -fpe test.kexe`. The kexe is built with embedded debug_info.
#
# Why not gdb -batch? gdb's ptrace intercept of SIGSEGV/SIGBUS perturbs K/N's
# runtime (which installs its own SIGSEGV-as-safepoint handlers during init),
# and the previous gdb-wrapped run (TC build 54004) had every iteration fail
# in 3s — the shell wrapper / ptrace combo broke startup. The LD_PRELOAD
# approach is just an extra sigaction() in __attribute__((constructor)) — it
# does not perturb execution and was validated locally on this binary.
#
# Usage:
#   scripts/stress-native-grpc-tc.sh [RUNS]
#     RUNS — number of iterations (default: 30)
#
# Exit code: 0 if zero crashes, 1 otherwise. Test failures (assertion mismatches)
# are NOT counted as crashes — only process-exit signatures (segfault, abort,
# pure-virtual, GPR check failure) are.

set -u
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

RUNS="${1:-30}"

KEXE="grpc/grpc-core/build/bin/linuxX64/debugTest/test.kexe"

if [ ! -f "$KEXE" ]; then
    echo "ERROR: $KEXE not found. The earlier gradle step should have built it." >&2
    exit 2
fi

# Build the LD_PRELOAD crash-backtrace shim. The TC stress build runs inside
# jozott/ktor-test-image (eclipse-temurin:21-jdk-noble base), which ships gcc
# in newer revisions but may not in older ones, so install gcc + libc6-dev
# defensively. The container runs as root, so apt-get works without sudo.
SHIM_SRC="$ROOT/scripts/crashbt.c"
SHIM_SO="$ROOT/scripts/libcrashbt.so"

if [ ! -f "$SHIM_SRC" ]; then
    echo "ERROR: $SHIM_SRC not found" >&2
    exit 2
fi

if ! command -v gcc >/dev/null 2>&1; then
    if command -v apt-get >/dev/null 2>&1; then
        echo "Installing gcc + libc6-dev..." >&2
        apt-get update -qq >/dev/null 2>&1 || true
        apt-get install -y -qq gcc libc6-dev >/dev/null 2>&1 || {
            echo "ERROR: failed to install gcc/libc6-dev via apt-get" >&2
            exit 2
        }
    else
        echo "ERROR: gcc not found and apt-get unavailable" >&2
        exit 2
    fi
fi

echo "Building $SHIM_SO from $SHIM_SRC..." >&2
gcc -O0 -g -fPIC -shared -o "$SHIM_SO" "$SHIM_SRC" -ldl || {
    echo "ERROR: failed to build crashbt shim" >&2
    exit 2
}

LOG_DIR="$ROOT/autoresearch-runs"
mkdir -p "$LOG_DIR"

STAMP="$(date +%Y%m%d_%H%M%S)"
BATCH_DIR="$LOG_DIR/batch_${STAMP}_tc_linuxX64"
mkdir -p "$BATCH_DIR"

# Canonical KRPC-604 crashing test classes. Negative pattern excludes the env-dependent
# default-credentials TLS test (requires external host).
TEST_FILTER='kotlinx.rpc.grpc.test.integration.ClientInterceptorTest.*:kotlinx.rpc.grpc.test.integration.GrpcTlsTest.*:kotlinx.rpc.grpc.test.integration.ServerInterceptorTest.*:kotlinx.rpc.grpc.test.integration.MetadataTest.*:kotlinx.rpc.grpc.test.integration.StreamingTest.*:kotlinx.rpc.grpc.test.integration.GrpcTimeoutTest.*:kotlinx.rpc.grpc.test.integration.GrpcEdgeCaseTest.*-*test?client?side?TLS?with?default?credentials*'

echo "=== KRPC-597/604 stress harness (TC linuxX64) ===" >&2
echo "Binary:    $KEXE" >&2
echo "Runs:      $RUNS" >&2
echo "Batch dir: $BATCH_DIR" >&2
echo >&2

crashes=0
failures=0
successes=0
crash_signatures=()

for i in $(seq 1 "$RUNS"); do
    OUT="$BATCH_DIR/run_$(printf '%03d' "$i").log"
    ts="$(date +%H:%M:%S)"
    label="$(printf '%02d/%02d' "$i" "$RUNS")"
    echo "##teamcity[progressMessage 'Stress run $label']"

    # Run the kexe directly with LD_PRELOAD'd crashbt shim. On a fatal signal
    # (SIGABRT/SIGSEGV/SIGBUS) the in-process handler prints a backtrace and
    # /proc/self/maps to stderr (captured into $OUT) before re-raising under
    # the default disposition. Validated locally as non-intrusive on
    # Kotlin/Native binaries.
    LD_PRELOAD="$SHIM_SO" "$KEXE" --ktest_filter="$TEST_FILTER" > "$OUT" 2>&1
    rc=$?

    if [ $rc -eq 0 ]; then
        echo "[$ts] run $label: ok"
        successes=$((successes + 1))
    elif grep -qE "SIGSEGV|SIGBUS|SIGABRT|SIGKILL|signal 11|signal 6|signal 9|Check failed:|F[0-9]+ .*filter_stack_call|core dumped|Aborted|Segmentation fault|pure virtual method called|terminate called|crashbt: signal" "$OUT"; then
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

total=$((successes + failures + crashes))
crash_frac=$(awk -v c="$crashes" -v t="$total" 'BEGIN { printf "%.4f", c / t }')

echo
echo "=== Summary ==="
echo "Total runs:   $total"
echo "Successes:    $successes"
echo "Crashes:      $crashes"
echo "Test fails:   $failures"
echo "Crash frac:   $crash_frac"
echo "Batch dir:    $BATCH_DIR"

# TC service messages — make the crash count visible on the build summary.
echo "##teamcity[buildStatisticValue key='krpc604.stress.runs' value='$total']"
echo "##teamcity[buildStatisticValue key='krpc604.stress.successes' value='$successes']"
echo "##teamcity[buildStatisticValue key='krpc604.stress.crashes' value='$crashes']"
echo "##teamcity[buildStatisticValue key='krpc604.stress.failures' value='$failures']"

if [ "$crashes" -gt 0 ]; then
    for sig in "${crash_signatures[@]}"; do
        echo "##teamcity[message text='$sig' status='ERROR']"
    done
    echo "##teamcity[buildStatus text='{build.status.text}; KRPC-604 stress: $crashes/$total crashes ($crash_frac), $failures fails']"
    exit 1
fi

echo "##teamcity[buildStatus text='{build.status.text}; KRPC-604 stress: $successes/$total clean, $failures fails']"
exit 0
