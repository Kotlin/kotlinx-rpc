/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame

class SwiftGrpcContinuationTest {
    @Test
    fun synchronousCompletionReturnsValueOrEof() = runTest {
        assertEquals("response", awaitSwiftGrpcCompletion({}) { it("response", null) })
        assertNull(awaitSwiftGrpcCompletion<String>({}) { it(null, null) })
    }

    @Test
    fun completionErrorRetainsNSError() = runTest {
        val expected = swiftGrpcError("bridge failed")
        val failure = assertFailsWith<SwiftGrpcInteropException> {
            awaitSwiftGrpcCompletion<String>({}) { it(null, expected) }
        }
        assertSame(expected, failure.error)
    }

    @Test
    fun registrationFailureRetainsKotlinException() = runTest {
        val expected = IllegalStateException("registration failed")
        assertSame(expected, assertFailsWith<IllegalStateException> {
            awaitSwiftGrpcCompletion<String>({}) { throw expected }
        })
    }

    @Test
    fun cancellationStopsOperationAndIgnoresLateCompletion() = runTest {
        val registered = CompletableDeferred<SwiftGrpcCompletion<String>>()
        var cancelled = false
        val operation = async {
            awaitSwiftGrpcCompletion({ cancelled = true }) { registered.complete(it) }
        }
        val completion = registered.await()
        assertFalse(cancelled)

        operation.cancelAndJoin()
        completion("late response", null)

        assertEquals(true, cancelled)
    }
}
