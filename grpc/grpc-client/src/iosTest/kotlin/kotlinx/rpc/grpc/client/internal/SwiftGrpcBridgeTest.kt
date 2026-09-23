/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
    kotlin.concurrent.atomics.ExperimentalAtomicApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.getAllBinary
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.remove
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.internal.KOTLINX_RPC_VERSION
import platform.Foundation.NSError
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMetadata
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestMessageProtocol
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SwiftGrpcBridgeTest {
    @Test
    fun swiftUserAgentIncludesKotlinxRpcRuntimeToken() {
        val runtimeToken = "kotlinx-rpc-swift/$KOTLINX_RPC_VERSION"

        assertEquals(runtimeToken, composeSwiftGrpcUserAgent(null))
        assertEquals(runtimeToken, composeSwiftGrpcUserAgent(""))
        assertEquals("MyApp/1.2.3 $runtimeToken", composeSwiftGrpcUserAgent("MyApp/1.2.3"))
    }

    @Test
    fun kotlinMetadataVisitorPreservesValuesAcrossSwiftRoundTrip() {
        val expected = ByteArray(20_000) { it.toByte() }
        val metadata = GrpcMetadata().apply {
            append("label", "first")
            appendBinary("data-bin", expected)
            append("label", "")
            append("label", "caf\u00e9")
            appendBinary("data-bin", byteArrayOf())
            append("removed", "value")
            remove("removed", "value")
        }

        val swift = metadata.toSwift()
        val result = swift.toKotlin()
        assertEquals(5L, swift.count)
        assertEquals(listOf("label", "data-bin"), result.keys().toList())
        assertEquals(metadata.getAll("label"), result.getAll("label"))
        assertEquals(listOf("first", "", "caf?"), result.getAll("label"))
        val binary = result.getAllBinary("data-bin")
        assertEquals(2, binary.size)
        assertContentEquals(expected, binary[0])
        assertContentEquals(byteArrayOf(), binary[1])
        assertEquals(0L, GrpcMetadata().toSwift().count)
    }

    @Test
    fun metadataVisitorCopiesDuplicateAndEmptyValues() {
        val metadata = SwiftGrpcMetadata()
        metadata.addStringValue("first", forKey = "label")
        metadata.addStringValue("", forKey = "label")
        val expected = byteArrayOf(0, 127, -128, -1)
        expected.usePinned { pinned ->
            metadata.addBinaryValue(pinned.addressOf(0), expected.size.toLong(), forKey = "data-bin")
        }
        metadata.addBinaryValue(null, 0, forKey = "data-bin")

        val result = metadata.toKotlin()
        assertEquals(listOf("first", ""), result.getAll("label").toList())
        val binary = result.getAllBinary("data-bin").toList()
        assertEquals(2, binary.size)
        assertContentEquals(expected, binary[0])
        assertContentEquals(byteArrayOf(), binary[1])

        // The Kotlin result owns its bytes independently of the Swift metadata.
        binary[0][0] = 42
        assertContentEquals(expected, metadata.toKotlin().getAllBinary("data-bin").first())
        assertTrue(SwiftGrpcMetadata().toKotlin().keys().isEmpty())
    }

    @Test
    fun metadataVisitorFiltersHttp2PseudoHeaders() {
        val metadata = SwiftGrpcMetadata().apply {
            addStringValue("200", forKey = ":status")
            addStringValue("application/grpc", forKey = "content-type")
            addStringValue("gzip", forKey = "grpc-encoding")
            addStringValue("value", forKey = "custom-header")
        }

        val result = metadata.toKotlin()
        assertFalse(":status" in result.keys())
        assertEquals("application/grpc", result["content-type"])
        assertEquals("gzip", result["grpc-encoding"])
        assertEquals("value", result["custom-header"])
    }

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
        assertNull(result.await().second)
    }

    @Test
    fun cancelledRequestSourceCompletesSubsequentPull() = runTest {
        val source = KotlinGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }

        source.cancel()

        val result = withTimeout(1_000.milliseconds) { source.pull() }
        assertNull(result.first)
        assertNull(result.second)
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
        assertNull(result.second)
    }

    @Test
    fun terminationAfterEofDoesNotCompleteAgain() = runTest {
        val source = KotlinGrpcRequestSource(this, flowOf(42)) { value ->
            KotlinGrpcRequestMessage(Buffer().apply { writeByte(value.toByte()) })
        }
        assertNotNull(source.pull().first)

        val eof = source.pullRecording()
        assertEquals(null to null, eof.result.await())

        // Joining must not depend on an earlier cancel, and later cancels must be no-ops.
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        repeat(3) { source.cancel() }

        assertEquals(1, eof.calls.load())
        assertNull(source.originalFailure)
    }

    @Test
    fun requestFlowFailureCompletesPendingPullOnce() = runTest {
        val expected = IllegalStateException("request failed while pulling")
        val failNow = CompletableDeferred<Unit>()
        val source = KotlinGrpcRequestSource<Int>(this, flow { failNow.await(); throw expected }) {
            error("No request should be encoded")
        }

        val pull = source.pullRecording()
        failNow.complete(Unit)

        val (message, error) = pull.result.await()
        assertNull(message)
        assertNotNull(error)
        assertSame(expected, source.originalFailure)

        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancelAfterRequestFlowFailurePreservesFailure() = runTest {
        val expected = IllegalStateException("request failed")
        val source = KotlinGrpcRequestSource<Int>(this, flow { throw expected }) {
            error("No request should be encoded")
        }
        assertNotNull(source.pull().second)

        source.cancel()

        assertSame(expected, source.originalFailure)
        val late = withTimeout(1_000.milliseconds) { source.pull() }
        assertNull(late.first)
        assertNotNull(late.second)
    }

    @Test
    fun encodeFailureFailsSourceAndStopsCollector() = runTest {
        val expected = IllegalStateException("encode failed")
        // The collector suspends while offering the second element until the source stops it.
        val source = KotlinGrpcRequestSource<Int>(this, flowOf(1, 2)) { throw expected }

        val pull = source.pullRecording()

        val (message, error) = pull.result.await()
        assertNull(message)
        assertNotNull(error)
        assertSame(expected, source.originalFailure)

        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancellationThrownByRequestFlowCompletesAsEof() = runTest {
        val source = KotlinGrpcRequestSource<Int>(
            scope = this,
            requests = flow { throw CancellationException("request flow cancelled itself") },
        ) {
            error("No request should be encoded")
        }

        val result = withTimeout(1_000.milliseconds) { source.pull() }

        assertEquals(null to null, result)
        assertNull(source.originalFailure)
        source.cancel()
    }

    @Test
    fun parentCompletionCompletesPendingPullAsEof() = runTest {
        val parent = Job()
        val source = KotlinGrpcRequestSource<Int>(
            scope = CoroutineScope(coroutineContext + parent),
            requests = flow { awaitCancellation() },
        ) {
            error("No request should be encoded")
        }
        val pull = source.pullRecording()

        parent.cancel()

        assertEquals(null to null, withTimeout(1_000.milliseconds) { pull.result.await() })
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancelRacingPullCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                coroutineScope {
                    val source = KotlinGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
                        error("No request should be encoded")
                    }
                    val start = CompletableDeferred<Unit>()
                    val pull = RecordingCompletion()

                    joinAll(
                        launch { start.await(); source.nextRequestWithCompletion(pull.callback) },
                        launch { start.await(); source.cancel() },
                        launch { start.complete(Unit) },
                    )

                    assertEquals(null to null, withTimeout(5.seconds) { pull.result.await() })
                    withTimeout(5.seconds) { source.cancelAndJoin() }
                    assertEquals(1, pull.calls.load())
                }
            }
        }
    }

    @Test
    fun cancelRacingRequestFlowFailureCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                coroutineScope {
                    val expected = IllegalStateException("request failed during cancellation")
                    val failNow = CompletableDeferred<Unit>()
                    val source = KotlinGrpcRequestSource<Int>(
                        scope = this,
                        requests = flow { failNow.await(); throw expected },
                    ) {
                        error("No request should be encoded")
                    }
                    val start = CompletableDeferred<Unit>()
                    val pull = source.pullRecording()

                    joinAll(
                        launch { start.await(); failNow.complete(Unit) },
                        launch { start.await(); source.cancel() },
                        launch { start.complete(Unit) },
                    )

                    val (message, error) = withTimeout(5.seconds) { pull.result.await() }
                    withTimeout(5.seconds) { source.cancelAndJoin() }
                    assertNull(message)
                    // Either transition may win, but a reported failure must be the original one.
                    if (error != null) assertSame(expected, source.originalFailure)
                    assertEquals(1, pull.calls.load())
                }
            }
        }
    }

    @Test
    fun elapsedDeadlineNormalizesGrpcSwiftResetRace() {
        val unavailable = GrpcClientCallEvents.Closed(
            status = GrpcStatus(
                GrpcStatusCode.UNAVAILABLE,
                "Stream unexpectedly closed: received $DEADLINE_RST_STREAM_DESCRIPTION.",
            ),
            trailers = GrpcMetadata(),
        )

        val normalized = unavailable.normalizeDeadlineRace(1500.milliseconds, 2.seconds)
        val closed = normalized as GrpcClientCallEvents.Closed
        assertEquals(GrpcStatusCode.DEADLINE_EXCEEDED, closed.status.statusCode)
        assertSame(unavailable, unavailable.normalizeDeadlineRace(1500.milliseconds, 1.seconds))
    }

    private suspend fun KotlinGrpcRequestSource<*>.pull(): Pair<SwiftGrpcRequestMessageProtocol?, NSError?> {
        val result = CompletableDeferred<Pair<SwiftGrpcRequestMessageProtocol?, NSError?>>()
        nextRequestWithCompletion { message, error -> result.complete(message to error) }
        return result.await()
    }

    private fun KotlinGrpcRequestSource<*>.pullRecording(): RecordingCompletion =
        RecordingCompletion().also { nextRequestWithCompletion(it.callback) }

    /** Records the first result of a request pull and how often its completion was invoked. */
    private class RecordingCompletion {
        val calls = AtomicInt(0)
        val result = CompletableDeferred<Pair<SwiftGrpcRequestMessageProtocol?, NSError?>>()
        val callback: (SwiftGrpcRequestMessageProtocol?, NSError?) -> Unit = { message, error ->
            calls.incrementAndFetch()
            result.complete(message to error)
        }
    }

    private companion object {
        const val STRESS_ITERATIONS = 1_000
    }
}
