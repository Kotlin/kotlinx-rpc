/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

/** The platform name written to results when `--platform-label` is not set. */
internal expect val currentPlatform: String

/** The wall-clock time in milliseconds since the Unix epoch. */
internal expect fun epochMillis(): Long
