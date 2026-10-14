/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNUSED_PARAMETER")

package kotlinx.rpc.test

actual suspend fun captureStdErr(block: suspend () -> Unit): String =
    throw UnsupportedOperationException("Standard error capture is supported only on JVM and POSIX Native targets")

actual suspend fun captureStdOut(block: suspend () -> Unit): String =
    throw UnsupportedOperationException("Standard output capture is supported only on JVM and POSIX Native targets")
