/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

/**
 * The process resource counters of one point in time.
 *
 * The CPU values are the totals of all threads of the process. A platform that can't split the
 * counters reports only [cpuTotalNanos], and a platform without the counters returns null.
 */
internal class ProcessResourceUsage(
    val cpuUserNanos: Long?,
    val cpuSystemNanos: Long?,
    val cpuTotalNanos: Long,
    val maxRssBytes: Long?,
)

/** Reads the current process resource counters, or returns null when the platform can't. */
internal expect fun processResourceUsage(): ProcessResourceUsage?

/**
 * The CPU time that the measured phase consumed, in nanoseconds, from two resource snapshots.
 */
internal fun cpuDelta(
    before: ProcessResourceUsage?,
    after: ProcessResourceUsage?,
): ProcessResourceUsage? {
    if (before == null || after == null) return null
    return ProcessResourceUsage(
        cpuUserNanos = deltaOf(before.cpuUserNanos, after.cpuUserNanos),
        cpuSystemNanos = deltaOf(before.cpuSystemNanos, after.cpuSystemNanos),
        cpuTotalNanos = after.cpuTotalNanos - before.cpuTotalNanos,
        maxRssBytes = null,
    )
}

private fun deltaOf(before: Long?, after: Long?): Long? =
    if (before == null || after == null) null else after - before
