/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.RUSAGE_SELF
import platform.posix.getrusage
import platform.posix.rusage

@OptIn(ExperimentalForeignApi::class)
internal actual fun processResourceUsage(): ProcessResourceUsage? = memScoped {
    val usage = alloc<rusage>()
    if (getrusage(RUSAGE_SELF, usage.ptr) != 0) {
        return null
    }
    val userNanos = usage.ru_utime.tv_sec * 1_000_000_000L + usage.ru_utime.tv_usec * 1_000L
    val systemNanos = usage.ru_stime.tv_sec * 1_000_000_000L + usage.ru_stime.tv_usec * 1_000L
    ProcessResourceUsage(
        cpuUserNanos = userNanos,
        cpuSystemNanos = systemNanos,
        cpuTotalNanos = userNanos + systemNanos,
        maxRssBytes = usage.ru_maxrss,
    )
}
