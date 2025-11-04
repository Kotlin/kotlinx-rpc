/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.statusCode
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun assertGrpcFailure(statusCode: StatusCode, message: String? = null, block: () -> Unit) {
    val exc = assertFailsWith<StatusException>(message) { block() }
    assertEquals(statusCode, exc.getStatus().statusCode)
    if (message != null) {
        assertContains(message, exc.getStatus().getDescription() ?: "")
    }
}

fun <T> assertContainsAll(actual: Iterable<T>, expected: Iterable<T>) {
    val expectedSet = expected.toSet()
    for (element in actual) {
        require(element in expectedSet) {
            "Actual element '$element' not found in expected collection"
        }
    }
}

enum class Runtime {
    JVM,
    NATIVE
}
expect val runtime: Runtime

expect fun setNativeEnv(key: String, value: String)
expect fun clearNativeEnv(key: String)

/**
 * Captures the standard error output written during the execution of the provided suspending block.
 *
 * @param block A suspending lambda function whose standard error output will be captured.
 * @return A string containing the captured standard error output.
 */
expect suspend fun captureStdErr(block: suspend () -> Unit): String

