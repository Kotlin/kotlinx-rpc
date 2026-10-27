/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.statusCode
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun assertGrpcFailure(statusCode: GrpcStatusCode, message: String? = null, block: () -> Unit) {
    val exc = assertFailsWith<GrpcStatusException>(message) { block() }
    assertEquals(statusCode, exc.getStatus().statusCode)
    if (message != null) {
        assertContains(exc.getStatus().getDescription() ?: "", message)
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

/** The port of the `:tests:grpc-test-server`. The test task passes it in `GRPC_TEST_SERVER_PORT`. */
expect val grpcTestServerPort: Int

expect suspend fun captureGrpcLogs(
    jvmLogLevel: String = "DEBUG",
    jvmLoggers: List<String> = listOf("io.grpc"),
    nativeVerbosity: String = "DEBUG",
    nativeTracers: List<String> = listOf("all"),
    block: suspend () -> Unit
): String
