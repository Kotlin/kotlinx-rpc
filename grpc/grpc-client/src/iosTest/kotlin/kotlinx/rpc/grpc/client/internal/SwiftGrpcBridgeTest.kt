/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import platform.Foundation.NSError
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestMessageProtocol
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class SwiftGrpcBridgeTest {
    @Test
    fun requestMessageCopiesIntoBorrowedSwiftStorage() = memScoped {
        val expected = ByteArray(20_000) { it.toByte() }
        val message = KotlinGrpcRequestMessage(Buffer().apply { write(expected) })
        val destination = allocArray<ByteVar>(expected.size)

        assertTrue(message.fillBuffer(destination, expected.size.toLong()))
        assertContentEquals(expected, ByteArray(expected.size) { destination[it] })
    }

    @Test
    fun scopedSwiftBytesCopyAcrossMultipleBufferSegments() = memScoped {
        val expected = ByteArray(20_000) { (it * 31).toByte() }
        val source = allocArray<ByteVar>(expected.size)
        for (index in expected.indices) source[index] = expected[index]

        assertContentEquals(expected, copySwiftBytes(source, expected.size.toLong()).readByteArray())
    }

    @Test
    fun requestSourcePullsOneMessageAndThenSignalsEof() = runTest {
        val source = KotlinGrpcRequestSource(this, flowOf(42)) { value ->
            KotlinGrpcRequestMessage(Buffer().apply { writeByte(value.toByte()) })
        }

        val first = source.pull()
        assertNotNull(first.first)
        assertNull(first.second)

        val eof = source.pull()
        assertNull(eof.first)
        assertNull(eof.second)
        source.cancel()
    }

    @Test
    fun requestSourceRetainsOriginalKotlinFailure() = runTest {
        val expected = IllegalStateException("request failed")
        val source = KotlinGrpcRequestSource<Int>(this, flow { throw expected }) {
            error("No request should be encoded")
        }

        val result = source.pull()
        assertNull(result.first)
        assertNotNull(result.second)
        assertSame(expected, source.originalFailure)
        source.cancel()
    }

    @Test
    fun cancellingRequestSourceCompletesOutstandingPull() = runTest {
        val source = KotlinGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }
        val result = CompletableDeferred<Pair<SwiftGrpcRequestMessageProtocol?, NSError?>>()
        source.nextRequestWithCompletion { message, error -> result.complete(message to error) }

        // Immediate cancellation without intermediate suspend call.
        source.cancel()

        assertNull(result.await().first)
        assertNotNull(result.await().second)
    }

    @Test
    fun cancelledRequestSourceCompletesSubsequentPull() = runTest {
        val source = KotlinGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }

        source.cancel()

        val result = withTimeout(1_000.milliseconds) { source.pull() }
        assertNull(result.first)
        assertNotNull(result.second)
    }

    @Test
    fun cancellingRequestSourceCompletesReentrantPull() = runTest {
        val source = KotlinGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }
        val reentrantResult = CompletableDeferred<Pair<SwiftGrpcRequestMessageProtocol?, NSError?>>()
        source.nextRequestWithCompletion { _, _ ->
            source.nextRequestWithCompletion { message, error ->
                reentrantResult.complete(message to error)
            }
        }

        source.cancel()

        val result = withTimeout(1_000.milliseconds) { reentrantResult.await() }
        assertNull(result.first)
        assertNotNull(result.second)
    }

    private suspend fun KotlinGrpcRequestSource<*>.pull(): Pair<SwiftGrpcRequestMessageProtocol?, NSError?> {
        val result = CompletableDeferred<Pair<SwiftGrpcRequestMessageProtocol?, NSError?>>()
        nextRequestWithCompletion { message, error -> result.complete(message to error) }
        return result.await()
    }
}
