/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import java.lang.management.ManagementFactory

internal actual fun processResourceUsage(): ProcessResourceUsage? {
    // The HotSpot bean reports only the combined CPU time, without a user or system split
    // and without a resident size.
    val bean = ManagementFactory.getOperatingSystemMXBean()
    if (bean !is com.sun.management.OperatingSystemMXBean) {
        return null
    }
    return ProcessResourceUsage(
        cpuUserNanos = null,
        cpuSystemNanos = null,
        cpuTotalNanos = bean.processCpuTime,
        maxRssBytes = null,
    )
}
