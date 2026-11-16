/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlin.concurrent.atomics.ExperimentalAtomicApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.getAllBinary
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.internal.singleOrStatusFlow
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.remove
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.internal.KOTLINX_RPC_VERSION
import platform.Foundation.NSError
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcMetadata
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestSourceProtocol
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SwiftGrpcBridgeTest {
    @Test
    fun requestSourcePullsOneMessageAndThenSignalsEof() = runTest {
        withRequestSource(flowOf(42).map(::testRequest)) { source ->
            val first = source.pull()
            assertNotNull(first.first)
            assertNull(first.second)

            val eof = source.pull()
            assertNull(eof.first)
            assertNull(eof.second)
        }
    }

    @Test
    fun requestSourcePullArrivesBeforeEmission() = runTest {
        val emitNow = CompletableDeferred<Unit>()
        withRequestSource(flow { emitNow.await(); emit(42) }.map(::testRequest)) { source ->
            val pull = source.pullRecording()

            assertFalse(pull.result.isCompleted)
            emitNow.complete(Unit)

            val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
            assertEquals(1L, assertNotNull(message).length().toLong())
            assertNull(error)
            assertEquals(1, pull.calls.load())
        }
    }

    @Test
    fun requestSourcePreEncodesOneOfferAndWaitsUntilPulled() = runTest {
        val advanced = CompletableDeferred<Unit>()
        val encodeCalls = AtomicInt(0)
        val requests = flow {
            emit(42)
            advanced.complete(Unit)
        }.map {
            encodeCalls.incrementAndFetch()
            testRequest(it)
        }

        withRequestSource(requests) { source ->
            assertFalse(advanced.isCompleted, "the producer must remain suspended at the unclaimed offer")
            assertEquals(1, encodeCalls.load())

            val pull = source.pullRecording()
            val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
            assertEquals(1L, assertNotNull(message).length().toLong())
            assertNull(error)
            assertEquals(1, pull.calls.load())
            withTimeout(1_000.milliseconds) { advanced.await() }
            assertEquals(1, encodeCalls.load())
        }
    }

    @Test
    fun requestSourceDeliversSequentialReentrantPullsInOrder() = runTest {
        withRequestSource(flowOf(1, 2, 3).map { testRequest(ByteArray(it) { it.toByte() }) }) { source ->
            val results = mutableListOf<Pair<Long?, NSError?>>()
            val callbackCalls = mutableListOf<AtomicInt>()
            val completed = CompletableDeferred<Unit>()

            fun pullNext() {
                val calls = AtomicInt(0)
                callbackCalls += calls
                source.nextRequestWithCompletion { message, error ->
                    calls.incrementAndFetch()
                    results += message?.length()?.toLong() to error
                    if (message == null) {
                        completed.complete(Unit)
                    } else {
                        pullNext()
                    }
                }
            }

            pullNext()
            withTimeout(1_000.milliseconds) { completed.await() }

            assertEquals(listOf(1L, 2L, 3L, null), results.map { it.first })
            assertTrue(results.all { it.second == null })
            assertTrue(callbackCalls.all { it.load() == 1 })
        }
    }

    @Test
    fun requestSourceRejectsConcurrentPullWithoutDisturbingWaitingPull() = runTest {
        val emitNow = CompletableDeferred<Unit>()
        withRequestSource(flow { emitNow.await(); emit(42); awaitCancellation() }.map(::testRequest)) { source ->
            val accepted = source.pullRecording()
            val rejected = source.pullRecording()

            val (rejectedMessage, rejectedError) = rejected.result.await()
            assertNull(rejectedMessage)
            assertNotNull(rejectedError)
            assertEquals(1, rejected.calls.load())
            assertFalse(accepted.result.isCompleted)

            emitNow.complete(Unit)
            val (acceptedMessage, acceptedError) = withTimeout(1_000.milliseconds) { accepted.result.await() }
            assertEquals(1L, assertNotNull(acceptedMessage).length().toLong())
            assertNull(acceptedError)
            assertEquals(1, accepted.calls.load())

            // The flow never ends, so the block must cancel the source before it returns.
            source.cancel()
        }
    }

    @Test
    fun cancellationCompletesDeliveringAndQueuedPullsAsEof() = runTest {
        withRequestSource(flowOf(42).map(::testRequest)) { source ->
            // The collector received the first pull, but has not resumed yet. The second one waits.
            val delivering = source.pullRecording()
            val queued = source.pullRecording()
            assertFalse(delivering.result.isCompleted)
            assertFalse(queued.result.isCompleted)

            source.cancel()
            assertEquals(null to null, withTimeout(1_000.milliseconds) { delivering.result.await() })
            assertEquals(null to null, withTimeout(1_000.milliseconds) { queued.result.await() })

            assertEquals(1, delivering.calls.load())
            assertEquals(1, queued.calls.load())
        }
    }

    @Test
    fun cancellingPreEncodedOfferedRequestWithdrawsAndStopsCollector() = runTest {
        val collectorStopped = CompletableDeferred<Unit>()
        val encodeCalls = AtomicInt(0)
        val requests = flow {
            try {
                emit(42)
            } finally {
                collectorStopped.complete(Unit)
            }
        }.map {
            encodeCalls.incrementAndFetch()
            testRequest(it)
        }

        withRequestSource(requests) { source ->
            source.cancel()
        }

        assertTrue(collectorStopped.isCompleted)
        assertEquals(1, encodeCalls.load())
    }

    @Test
    fun requestFlowFailureFailsTheCallerWithTheOriginalException() = runTest {
        val expected = IllegalStateException("request failed")
        lateinit var pull: RecordingCompletion

        val failure = assertFailsWith<IllegalStateException> {
            withRequestSource(flow<SwiftGrpcRequestBytes> { throw expected }) { source ->
                pull = source.pullRecording()
            }
        }

        assertSame(expected, failure)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertNull(message)
        assertNotNull(error)
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancellingRequestSourceCompletesOutstandingPull() = runTest {
        withRequestSource(flow<SwiftGrpcRequestBytes> { awaitCancellation() }) { source ->
            val result = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
            source.nextRequestWithCompletion { message, error -> result.complete(message to error) }

            // Immediate cancellation without an intermediate suspend call.
            source.cancel()

            assertNull(result.await().first)
            assertNull(result.await().second)
        }
    }

    @Test
    fun cancelledRequestSourceCompletesSubsequentPull() = runTest {
        withRequestSource(flow<SwiftGrpcRequestBytes> { awaitCancellation() }) { source ->
            source.cancel()

            val result = withTimeout(1_000.milliseconds) { source.pull() }
            assertNull(result.first)
            assertNull(result.second)
        }
    }

    @Test
    fun cancellingRequestSourceCompletesReentrantPull() = runTest {
        withRequestSource(flow<SwiftGrpcRequestBytes> { awaitCancellation() }) { source ->
            val reentrantResult = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
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
    }

    @Test
    fun terminationAfterEofDoesNotCompleteAgain() = runTest {
        withRequestSource(flowOf(42).map(::testRequest)) { source ->
            assertNotNull(source.pull().first)

            val eof = source.pullRecording()
            assertEquals(null to null, eof.result.await())

            // The collection ended on its own, so later cancels must be no-ops.
            repeat(3) { source.cancel() }

            assertEquals(1, eof.calls.load())
        }
    }

    @Test
    fun requestFlowFailureCompletesPendingPullOnce() = runTest {
        val expected = IllegalStateException("request failed while pulling")
        val failNow = CompletableDeferred<Unit>()
        lateinit var pull: RecordingCompletion

        val failure = assertFailsWith<IllegalStateException> {
            withRequestSource(flow<SwiftGrpcRequestBytes> { failNow.await(); throw expected }) { source ->
                pull = source.pullRecording()
                failNow.complete(Unit)
            }
        }

        assertSame(expected, failure)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertNull(message)
        assertNotNull(error)
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun requestFlowFailurePropagatesToTheCallerScope() = runTest {
        val expected = AssertionError("request flow failed")
        val siblingStopped = CompletableDeferred<Unit>()
        val failNow = CompletableDeferred<Unit>()
        lateinit var pull: RecordingCompletion

        val failure = assertFailsWith<AssertionError> {
            coroutineScope {
                launch {
                    try {
                        awaitCancellation()
                    } finally {
                        siblingStopped.complete(Unit)
                    }
                }
                withRequestSource(flow<SwiftGrpcRequestBytes> { failNow.await(); throw expected }) { source ->
                    pull = source.pullRecording()
                    failNow.complete(Unit)
                }
            }
        }

        assertSame(expected, failure)
        assertTrue(siblingStopped.isCompleted)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertNull(message)
        assertNotNull(error)
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun encodeErrorPropagatesAndCompletesLatePull() = runTest {
        val expected = AssertionError("encode failed")
        lateinit var pull: RecordingCompletion

        val failure = assertFailsWith<AssertionError> {
            withRequestSource(flowOf(42).map { throw expected }) { source ->
                pull = source.pullRecording()
            }
        }

        assertSame(expected, failure)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertNull(message)
        assertNotNull(error)
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancelAfterRequestFlowFailurePreservesFailure() = runTest {
        val expected = IllegalStateException("request failed")

        val failure = assertFailsWith<IllegalStateException> {
            withRequestSource(flow<SwiftGrpcRequestBytes> { throw expected }) { source ->
                val first = source.pull()
                assertNull(first.first)
                assertNotNull(first.second)

                source.cancel()

                val late = withTimeout(1_000.milliseconds) { source.pull() }
                assertNull(late.first)
                assertNotNull(late.second)
            }
        }

        assertSame(expected, failure)
    }

    @Test
    fun encodeFailureCompletesPendingPullOnce() = runTest {
        val expected = IllegalStateException("encode failed")
        val emitNow = CompletableDeferred<Unit>()
        lateinit var pull: RecordingCompletion

        val failure = assertFailsWith<IllegalStateException> {
            withRequestSource(flow { emitNow.await(); emit(42) }.map { throw expected }) { source ->
                pull = source.pullRecording()
                emitNow.complete(Unit)
            }
        }

        assertSame(expected, failure)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertNull(message)
        assertNotNull(error)
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancellationThrownByRequestFlowCompletesAsEof() = runTest {
        withRequestSource(
            flow<SwiftGrpcRequestBytes> { throw CancellationException("request flow cancelled itself") },
        ) { source ->
            val result = withTimeout(1_000.milliseconds) { source.pull() }

            assertEquals(null to null, result)
        }
    }

    @Test
    fun callerCancellationCompletesPendingPullAsEof() = runTest {
        val parent = Job()
        lateinit var pull: RecordingCompletion

        assertFailsWith<CancellationException> {
            withContext(parent) {
                withRequestSource(flow<SwiftGrpcRequestBytes> { awaitCancellation() }) { source ->
                    pull = source.pullRecording()
                    parent.cancel()
                    awaitCancellation()
                }
            }
        }

        assertEquals(null to null, withTimeout(1_000.milliseconds) { pull.result.await() })
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancelRacingPullCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                val pull = RecordingCompletion()

                withRequestSource(flow<SwiftGrpcRequestBytes> { awaitCancellation() }) { source ->
                    val start = CompletableDeferred<Unit>()

                    coroutineScope {
                        joinAll(
                            launch { start.await(); source.nextRequestWithCompletion(pull.callback) },
                            launch { start.await(); source.cancel() },
                            launch { start.complete(Unit) },
                        )
                    }
                }

                assertEquals(null to null, withTimeout(5.seconds) { pull.result.await() })
                assertEquals(1, pull.calls.load())
            }
        }
    }

    @Test
    fun cancelRacingRequestFlowFailureCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                val expected = IllegalStateException("request failed during cancellation")
                val failNow = CompletableDeferred<Unit>()
                val pull = RecordingCompletion()

                val outcome = runCatching {
                    withRequestSource(flow<SwiftGrpcRequestBytes> { failNow.await(); throw expected }) { source ->
                        source.nextRequestWithCompletion(pull.callback)
                        val start = CompletableDeferred<Unit>()

                        coroutineScope {
                            joinAll(
                                launch { start.await(); failNow.complete(Unit) },
                                launch { start.await(); source.cancel() },
                                launch { start.complete(Unit) },
                            )
                        }
                    }
                }

                val (message, error) = withTimeout(5.seconds) { pull.result.await() }
                assertNull(message)
                // Either transition can win, but a reported failure must be the original one.
                if (error != null) {
                    assertSame(expected, outcome.exceptionOrNull())
                }
                assertEquals(1, pull.calls.load())
            }
        }
    }

    @Test
    fun pullRacingNormalCompletionCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                val finish = CompletableDeferred<Unit>()
                val pull = RecordingCompletion()

                withRequestSource(flow<SwiftGrpcRequestBytes> { finish.await() }) { source ->
                    val start = CompletableDeferred<Unit>()

                    coroutineScope {
                        joinAll(
                            launch { start.await(); source.nextRequestWithCompletion(pull.callback) },
                            launch { start.await(); finish.complete(Unit) },
                            launch { start.complete(Unit) },
                        )
                    }
                }

                assertEquals(null to null, withTimeout(5.seconds) { pull.result.await() })
                assertEquals(1, pull.calls.load())
            }
        }
    }

    @Test
    fun requestSourceDeliversValidatedUnaryOnlyAfterFlowCompletes() = runTest {
        val finish = CompletableDeferred<Unit>()
        val requests = flow { emit(42); finish.await() }
        val validated = requests.singleOrStatusFlow("request", "unary")
        lateinit var pull: RecordingCompletion

        withRequestSource(flow { emit(validated.single()) }.map(::testRequest)) { source ->
            pull = source.pullRecording()

            assertFalse(pull.result.isCompleted, "the request must not be delivered before the flow ends")

            finish.complete(Unit)
            val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
            assertEquals(1L, assertNotNull(message).length().toLong())
            assertNull(error)
            assertEquals(null to null, source.pull())
            assertEquals(1, pull.calls.load())
        }
    }

    @Test
    fun invalidUnaryRequestFailsBeforeEncoding() = runTest {
        for (requests in listOf(flowOf<Int>(), flowOf(1, 2))) {
            val validated = requests.singleOrStatusFlow("request", "unary")
            lateinit var pull: RecordingCompletion

            val failure = assertFailsWith<GrpcStatusException> {
                withRequestSource(flow { emit(validated.single()) }.map {
                    error("An invalid unary request must not be encoded")
                }) { source ->
                    pull = source.pullRecording()
                }
            }

            assertEquals(GrpcStatusCode.INTERNAL, failure.status.statusCode)
            val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
            assertNull(message)
            assertNotNull(error)
            assertEquals(1, pull.calls.load())
        }
    }

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
            append("label", "café")
            appendBinary("data-bin", byteArrayOf())
            append("removed", "value")
            remove("removed", "value")
        }

        val swift = metadata.toSwift()
        val result = swift.toKotlin()
        assertEquals(5L, swift.count.toLong())
        assertEquals(listOf("label", "data-bin"), result.keys().toList())
        assertEquals(metadata.getAll("label"), result.getAll("label"))
        assertEquals(listOf("first", "", "caf?"), result.getAll("label"))
        val binary = result.getAllBinary("data-bin")
        assertEquals(2, binary.size)
        assertContentEquals(expected, binary[0])
        assertContentEquals(byteArrayOf(), binary[1])
        assertEquals(0L, GrpcMetadata().toSwift().count.toLong())
    }

    @Test
    fun metadataVisitorCopiesDuplicateAndEmptyValues() {
        val metadata = SwiftGrpcMetadata()
        metadata.addStringValue("first", forKey = "label")
        metadata.addStringValue("", forKey = "label")
        val expected = byteArrayOf(0, 127, -128, -1)
        expected.usePinned { pinned ->
            metadata.addBinaryValue(pinned.addressOf(0), expected.size.convert(), forKey = "data-bin")
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

    private suspend fun SwiftGrpcRequestSourceProtocol.pull(): Pair<SwiftGrpcRequestBytes?, NSError?> {
        val result = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
        nextRequestWithCompletion { message, error -> result.complete(message to error) }
        return result.await()
    }

    private fun SwiftGrpcRequestSourceProtocol.pullRecording(): RecordingCompletion =
        RecordingCompletion().also { nextRequestWithCompletion(it.callback) }

    /** Records the first result of a request pull and the number of times its completion ran. */
    private class RecordingCompletion {
        val calls = AtomicInt(0)
        val result = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
        val callback: (SwiftGrpcRequestBytes?, NSError?) -> Unit = { message, error ->
            calls.incrementAndFetch()
            result.complete(message to error)
        }
    }

    private fun testRequest(value: Int): SwiftGrpcRequestBytes = testRequest(byteArrayOf(value.toByte()))

    private fun testRequest(bytes: ByteArray): SwiftGrpcRequestBytes =
        SwiftRequestMessageWriter(bytes.size).apply { write(bytes) }.requestBytes()

    private companion object {
        const val STRESS_ITERATIONS = 1_000
    }
}
