/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlin.concurrent.atomics.ExperimentalAtomicApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.internal.singleOrStatusFlow
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import platform.Foundation.NSError
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SwiftGrpcBridgeTest {
    @Test
    fun requestSourcePullsOneMessageAndThenSignalsEof() = runTest {
        val source = SwiftGrpcRequestSource(this, flowOf(42)) { value ->
            testRequest(value)
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
    fun requestSourcePullArrivesBeforeEmission() = runTest {
        val emitNow = CompletableDeferred<Unit>()
        val source = SwiftGrpcRequestSource(this, flow {
            emitNow.await()
            emit(42)
        }) { value ->
            testRequest(value)
        }
        val pull = source.pullRecording()

        assertFalse(pull.result.isCompleted)
        emitNow.complete(Unit)

        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertEquals(1L, assertNotNull(message).length())
        assertNull(error)
        assertEquals(1, pull.calls.load())
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
    }

    @Test
    fun requestSourcePreEncodesOneOfferAndWaitsUntilPulled() = runTest {
        val advanced = CompletableDeferred<Unit>()
        val encodeCalls = AtomicInt(0)
        val source = SwiftGrpcRequestSource(this, flow {
            emit(42)
            advanced.complete(Unit)
        }) { value ->
            encodeCalls.incrementAndFetch()
            testRequest(value)
        }

        assertFalse(advanced.isCompleted, "the producer must remain suspended at the unclaimed offer")
        assertEquals(1, encodeCalls.load())

        val pull = source.pullRecording()
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertEquals(1L, assertNotNull(message).length())
        assertNull(error)
        assertEquals(1, pull.calls.load())
        withTimeout(1_000.milliseconds) { advanced.await() }
        assertEquals(1, encodeCalls.load())
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
    }

    @Test
    fun requestSourceDeliversSequentialReentrantPullsInOrder() = runTest {
        val source = SwiftGrpcRequestSource(this, flowOf(1, 2, 3)) { value ->
            testRequest(ByteArray(value) { value.toByte() })
        }
        val results = mutableListOf<Pair<Long?, NSError?>>()
        val callbackCalls = mutableListOf<AtomicInt>()
        val completed = CompletableDeferred<Unit>()

        fun pullNext() {
            val calls = AtomicInt(0)
            callbackCalls += calls
            source.nextRequestWithCompletion { message, error ->
                calls.incrementAndFetch()
                results += message?.length() to error
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
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
    }

    @Test
    fun requestSourceRejectsConcurrentPullWithoutDisturbingWaitingPull() = runTest {
        val emitNow = CompletableDeferred<Unit>()
        val source = SwiftGrpcRequestSource(this, flow {
            emitNow.await()
            emit(42)
            awaitCancellation()
        }) { value ->
            testRequest(value)
        }
        val accepted = source.pullRecording()
        val rejected = source.pullRecording()

        val (rejectedMessage, rejectedError) = rejected.result.await()
        assertNull(rejectedMessage)
        assertNotNull(rejectedError)
        assertEquals(1, rejected.calls.load())
        assertFalse(accepted.result.isCompleted)

        emitNow.complete(Unit)
        val (acceptedMessage, acceptedError) = withTimeout(1_000.milliseconds) { accepted.result.await() }
        assertEquals(1L, assertNotNull(acceptedMessage).length())
        assertNull(acceptedError)
        assertEquals(1, accepted.calls.load())
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
    }

    @Test
    fun cancellationCompletesDeliveringAndQueuedPullsAsEof() = runTest {
        val source = SwiftGrpcRequestSource(this, flowOf(42)) { value ->
            testRequest(value)
        }
        // The collector receives the first pull, but has not resumed yet. The second one waits.
        val delivering = source.pullRecording()
        val queued = source.pullRecording()
        assertFalse(delivering.result.isCompleted)
        assertFalse(queued.result.isCompleted)

        source.cancel()
        assertEquals(null to null, withTimeout(1_000.milliseconds) { delivering.result.await() })
        assertEquals(null to null, withTimeout(1_000.milliseconds) { queued.result.await() })

        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, delivering.calls.load())
        assertEquals(1, queued.calls.load())
    }

    @Test
    fun cancellingPreEncodedOfferedRequestWithdrawsAndJoins() = runTest {
        val collectorStopped = CompletableDeferred<Unit>()
        val encodeCalls = AtomicInt(0)
        val source = SwiftGrpcRequestSource(this, flow {
            try {
                emit(42)
            } finally {
                collectorStopped.complete(Unit)
            }
        }) { value ->
            encodeCalls.incrementAndFetch()
            testRequest(value)
        }

        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }

        assertTrue(collectorStopped.isCompleted)
        assertEquals(1, encodeCalls.load())
    }

    @Test
    fun requestSourceRetainsOriginalKotlinFailure() = runTest {
        val expected = IllegalStateException("request failed")
        val source = SwiftGrpcRequestSource<Int>(this, flow { throw expected }) {
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
        val source = SwiftGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }
        val result = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
        source.nextRequestWithCompletion { message, error -> result.complete(message to error) }

        // Immediate cancellation without intermediate suspend call.
        source.cancel()

        assertNull(result.await().first)
        assertNull(result.await().second)
    }

    @Test
    fun cancelledRequestSourceCompletesSubsequentPull() = runTest {
        val source = SwiftGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }

        source.cancel()

        val result = withTimeout(1_000.milliseconds) { source.pull() }
        assertNull(result.first)
        assertNull(result.second)
    }

    @Test
    fun cancellingRequestSourceCompletesReentrantPull() = runTest {
        val source = SwiftGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }
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

    @Test
    fun terminationAfterEofDoesNotCompleteAgain() = runTest {
        val source = SwiftGrpcRequestSource(this, flowOf(42)) { value ->
            testRequest(value)
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
        val source = SwiftGrpcRequestSource<Int>(this, flow { failNow.await(); throw expected }) {
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
    fun requestFlowErrorPropagatesAndCompletesPendingPullOnce() = runTest {
        val expected = AssertionError("request flow failed")
        val siblingStopped = CompletableDeferred<Unit>()
        lateinit var source: SwiftGrpcRequestSource<Int>
        lateinit var pull: RecordingCompletion

        val failure = assertFailsWith<AssertionError> {
            coroutineScope {
                launch(start = CoroutineStart.UNDISPATCHED) {
                    try {
                        awaitCancellation()
                    } finally {
                        siblingStopped.complete(Unit)
                    }
                }
                val failNow = CompletableDeferred<Unit>()
                source = SwiftGrpcRequestSource(this, flow { failNow.await(); throw expected }, ::testRequest)
                pull = source.pullRecording()
                failNow.complete(Unit)
            }
        }

        assertSame(expected, failure)
        assertTrue(siblingStopped.isCompleted)
        assertSame(expected, source.originalFailure)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertNull(message)
        assertNotNull(error)
        assertEquals(1, pull.calls.load())
        assertNotNull(source.pull().second)
    }

    @Test
    fun encodeErrorPropagatesAndCompletesLatePull() = runTest {
        val expected = AssertionError("encode failed")
        lateinit var source: SwiftGrpcRequestSource<Int>

        val failure = assertFailsWith<AssertionError> {
            coroutineScope {
                source = SwiftGrpcRequestSource(this, flowOf(42)) { throw expected }
            }
        }

        assertSame(expected, failure)
        assertSame(expected, source.originalFailure)
        val pull = source.pullRecording()
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertNull(message)
        assertNotNull(error)
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancelAfterRequestFlowFailurePreservesFailure() = runTest {
        val expected = IllegalStateException("request failed")
        val source = SwiftGrpcRequestSource<Int>(this, flow { throw expected }) {
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
        // Encoding is eager, so the source fails before its first pull.
        val source = SwiftGrpcRequestSource<Int>(this, flowOf(1)) { throw expected }

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
        val source = SwiftGrpcRequestSource<Int>(
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
        val source = SwiftGrpcRequestSource<Int>(
            scope = CoroutineScope(coroutineContext + parent),
            requests = flow { awaitCancellation() },
        ) {
            error("No request should be encoded")
        }
        val pull = source.pullRecording()

        parent.cancel()

        assertEquals(null to null, withTimeout(1_000.milliseconds) { pull.result.await() })
        withTimeout(1_000.milliseconds) { parent.join() }
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancelRacingPullCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                coroutineScope {
                    val source = SwiftGrpcRequestSource<Int>(this, flow { awaitCancellation() }) {
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
                    val source = SwiftGrpcRequestSource<Int>(
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
                    if (error != null) {
                        assertSame(expected, source.originalFailure)
                    }
                    assertEquals(1, pull.calls.load())
                }
            }
        }
    }

    @Test
    fun pullRacingNormalCompletionCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                coroutineScope {
                    val finish = CompletableDeferred<Unit>()
                    val source = SwiftGrpcRequestSource<Int>(this, flow { finish.await() }) {
                        error("No request should be encoded")
                    }
                    val start = CompletableDeferred<Unit>()
                    val pull = RecordingCompletion()

                    joinAll(
                        launch { start.await(); source.nextRequestWithCompletion(pull.callback) },
                        launch { start.await(); finish.complete(Unit) },
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
    fun requestSourceDeliversValidatedUnaryOnlyAfterFlowCompletes() = runTest {
        val finish = CompletableDeferred<Unit>()
        val requests = flow { emit(42); finish.await() }
        val validated = requests.singleOrStatusFlow("request", "unary")
        val source = SwiftGrpcRequestSource<Int>(this, flow { emit(validated.single()) }, ::testRequest)
        val pull = source.pullRecording()

        assertFalse(pull.result.isCompleted, "the request must not be delivered before the flow ends")

        finish.complete(Unit)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertEquals(1L, assertNotNull(message).length())
        assertNull(error)
        assertEquals(null to null, source.pull())
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun invalidUnaryRequestFailsBeforeEncoding() = runTest {
        for (requests in listOf(flowOf<Int>(), flowOf(1, 2))) {
            val validated = requests.singleOrStatusFlow("request", "unary")
            val source = SwiftGrpcRequestSource<Int>(this, flow { emit(validated.single()) }) {
                error("An invalid unary request must not be encoded")
            }

            val (message, error) = source.pull()

            assertNull(message)
            assertNotNull(error)
            val failure = assertIs<GrpcStatusException>(source.originalFailure)
            assertEquals(GrpcStatusCode.INTERNAL, failure.status.statusCode)
            withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        }
    }

    private suspend fun SwiftGrpcRequestSource<*>.pull(): Pair<SwiftGrpcRequestBytes?, NSError?> {
        val result = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
        nextRequestWithCompletion { message, error -> result.complete(message to error) }
        return result.await()
    }

    private fun SwiftGrpcRequestSource<*>.pullRecording(): RecordingCompletion =
        RecordingCompletion().also { nextRequestWithCompletion(it.callback) }

    /** Records the first result of a request pull and how often its completion was invoked. */
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
