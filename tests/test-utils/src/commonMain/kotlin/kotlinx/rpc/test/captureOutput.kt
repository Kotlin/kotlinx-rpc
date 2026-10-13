/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

/**
 * Captures standard error from [block] on JVM and POSIX Native targets.
 * Run captures sequentially because standard error is shared by the entire process.
 *
 * @throws UnsupportedOperationException on Windows, JS, and Wasm.
 */
expect suspend fun captureStdErr(block: suspend () -> Unit): String

/**
 * Captures standard output from [block] on JVM and POSIX Native targets.
 * Run captures sequentially because standard output is shared by the entire process.
 *
 * @throws UnsupportedOperationException on Windows, JS, and Wasm.
 */
expect suspend fun captureStdOut(block: suspend () -> Unit): String
