/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlin.concurrent.atomics.ExperimentalAtomicApi::class,
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
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import platform.Foundation.NSError
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestSourceProtocol
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.Test
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
        val source = KotlinGrpcRequestSource(this, flowOf(42)) { value ->
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
        val source = KotlinGrpcRequestSource(this, flow {
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
        val source = KotlinGrpcRequestSource(this, flow {
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
        val source = KotlinGrpcRequestSource(this, flowOf(1, 2, 3)) { value ->
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
        val source = KotlinGrpcRequestSource(this, flow {
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
        val source = KotlinGrpcRequestSource(this, flowOf(42)) { value ->
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
        val source = KotlinGrpcRequestSource(this, flow {
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
        val result = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
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
        val source = KotlinGrpcRequestSource(this, flowOf(42)) { value ->
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
    fun requestFlowErrorPropagatesAndCompletesPendingPullOnce() = runTest {
        val expected = AssertionError("request flow failed")
        val siblingStopped = CompletableDeferred<Unit>()
        lateinit var source: KotlinGrpcRequestSource<Int>
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
                source = KotlinGrpcRequestSource(this, flow { failNow.await(); throw expected }, ::testRequest)
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
        lateinit var source: KotlinGrpcRequestSource<Int>

        val failure = assertFailsWith<AssertionError> {
            coroutineScope {
                source = KotlinGrpcRequestSource(this, flowOf(42)) { throw expected }
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
        // Encoding is eager, so the source fails before its first pull.
        val source = KotlinGrpcRequestSource<Int>(this, flowOf(1)) { throw expected }

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
        withTimeout(1_000.milliseconds) { parent.join() }
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
                    val source = KotlinGrpcRequestSource<Int>(this, flow { finish.await() }) {
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
    fun singleRequestSourceDeliversRequestOnlyAfterFlowCompletes() = runTest {
        val finish = CompletableDeferred<Unit>()
        val source = KotlinGrpcSingleRequestSource(this, flow { emit(42); finish.await() }) { value ->
            testRequest(value)
        }

        val pull = source.pullRecording()
        assertFalse(pull.result.isCompleted, "the request must not be delivered before the flow ends")

        finish.complete(Unit)
        val (message, error) = withTimeout(1_000.milliseconds) { pull.result.await() }
        assertEquals(1L, assertNotNull(message).length())
        assertNull(error)
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun singleRequestSourceIsReadyOnlyAfterNonSuspendingCollection() = runTest {
        val encode = { value: Int -> testRequest(value) }
        val finish = CompletableDeferred<Unit>()

        val ready = KotlinGrpcSingleRequestSource(this, flowOf(42), encode)
        val suspended = KotlinGrpcSingleRequestSource(this, flow { emit(42); finish.await() }, encode)
        val failed = KotlinGrpcSingleRequestSource(this, flowOf(1, 2)) { error("encode failed") }

        assertNotNull(ready.readyMessage)
        assertNull(suspended.readyMessage)
        assertNull(failed.readyMessage)
        finish.complete(Unit)
        suspended.cancelAndJoin()
    }

    @Test
    fun singleRequestSourceRejectsSecondPull() = runTest {
        val source = KotlinGrpcSingleRequestSource(this, flowOf(42)) { value ->
            testRequest(value)
        }
        assertNotNull(source.pull().first)

        val second = source.pull()
        assertNull(second.first)
        assertNotNull(second.second)
        source.cancel()
    }

    @Test
    fun singleRequestSourceRetainsOriginalKotlinFailure() = runTest {
        val expected = IllegalStateException("request failed")
        val source = KotlinGrpcSingleRequestSource<Int>(this, flow { throw expected }) {
            error("No request should be encoded")
        }

        val (message, error) = source.pull()
        assertNull(message)
        assertNotNull(error)
        assertSame(expected, source.originalFailure)
        source.cancel()
    }

    @Test
    fun singleRequestFlowErrorPropagatesAndCompletesPendingPullOnce() = runTest {
        val expected = AssertionError("request flow failed")
        val siblingStopped = CompletableDeferred<Unit>()
        lateinit var source: KotlinGrpcSingleRequestSource<Int>
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
                source = KotlinGrpcSingleRequestSource(this, flow { failNow.await(); throw expected }, ::testRequest)
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
    }

    @Test
    fun singleRequestEncodeErrorPropagatesAndCompletesLatePull() = runTest {
        val expected = AssertionError("encode failed")
        lateinit var source: KotlinGrpcSingleRequestSource<Int>

        val failure = assertFailsWith<AssertionError> {
            coroutineScope {
                source = KotlinGrpcSingleRequestSource(this, flowOf(42)) { throw expected }
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
    fun singleRequestSourceTreatsCancellationThrownByRequestFlowAsEof() = runTest {
        val source = KotlinGrpcSingleRequestSource<Int>(
            scope = this,
            requests = flow { throw CancellationException("request flow cancelled itself") },
        ) {
            error("No request should be encoded")
        }

        assertEquals(null to null, withTimeout(1_000.milliseconds) { source.pull() })
        assertNull(source.originalFailure)
    }

    @Test
    fun cancellingSingleRequestSourceCompletesPendingPullAsEof() = runTest {
        val source = KotlinGrpcSingleRequestSource<Int>(this, flow { awaitCancellation() }) {
            error("No request should be encoded")
        }
        val pull = source.pullRecording()

        source.cancel()

        assertEquals(null to null, withTimeout(1_000.milliseconds) { pull.result.await() })
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun cancellingSingleRequestSourceWhileEncodingNeverDeliversRequest() = runTest {
        val emitNow = CompletableDeferred<Unit>()
        lateinit var source: KotlinGrpcSingleRequestSource<Int>
        source = KotlinGrpcSingleRequestSource(this, flow { emitNow.await(); emit(42) }) { value ->
            source.cancel()
            testRequest(value)
        }
        val pull = source.pullRecording()

        emitNow.complete(Unit)

        assertEquals(null to null, withTimeout(1_000.milliseconds) { pull.result.await() })
        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertNull(source.readyMessage)
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun parentCompletionCompletesPendingSingleRequestPullAsEof() = runTest {
        val parent = Job()
        val source = KotlinGrpcSingleRequestSource<Int>(
            scope = CoroutineScope(coroutineContext + parent),
            requests = flow { awaitCancellation() },
        ) {
            error("No request should be encoded")
        }
        val pull = source.pullRecording()

        parent.cancel()

        assertEquals(null to null, withTimeout(1_000.milliseconds) { pull.result.await() })
        withTimeout(1_000.milliseconds) { parent.join() }
        assertEquals(1, pull.calls.load())
    }

    @Test
    fun parentCancellationDuringSingleRequestEncodingWithdrawsRequest() = runTest {
        val parent = Job()
        val source = KotlinGrpcSingleRequestSource(
            scope = CoroutineScope(coroutineContext + parent),
            requests = flowOf(42),
        ) { value ->
            parent.cancel()
            testRequest(value)
        }

        withTimeout(1_000.milliseconds) { parent.join() }

        assertNull(source.readyMessage)
        assertNull(source.originalFailure)
        assertEquals(null to null, withTimeout(1_000.milliseconds) { source.pull() })
    }

    @Test
    fun parentCancellationWithdrawsReadySingleRequest() = runTest {
        val parent = Job()
        val source = KotlinGrpcSingleRequestSource(
            scope = CoroutineScope(coroutineContext + parent),
            requests = flowOf(42),
            encode = ::testRequest,
        )
        assertNotNull(source.readyMessage)

        parent.cancel()
        withTimeout(1_000.milliseconds) { parent.join() }

        assertNull(source.readyMessage)
        assertEquals(null to null, withTimeout(1_000.milliseconds) { source.pull() })
    }

    @Test
    fun cancelRacingSingleRequestPullCompletesExactlyOnce() = runTest {
        withContext(Dispatchers.Default) {
            repeat(STRESS_ITERATIONS) {
                coroutineScope {
                    val source = KotlinGrpcSingleRequestSource<Int>(this, flow { awaitCancellation() }) {
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
    fun cancelledReadySingleRequestIsNeverPulled() = runTest {
        val source = KotlinGrpcSingleRequestSource(this, flowOf(42), ::testRequest)
        assertNotNull(source.readyMessage)

        source.cancelAndJoin()

        assertNull(source.readyMessage)
        assertEquals(null to null, source.pull())
    }

    @Test
    fun cancellingFailedSingleRequestPreservesOriginalFailure() = runTest {
        val expected = IllegalStateException("request failed")
        val source = KotlinGrpcSingleRequestSource<Int>(this, flow { throw expected }, ::testRequest)

        source.cancelAndJoin()

        assertSame(expected, source.originalFailure)
        assertNotNull(source.pull().second)
    }

    private suspend fun SwiftGrpcRequestSourceProtocol.pull(): Pair<SwiftGrpcRequestBytes?, NSError?> {
        val result = CompletableDeferred<Pair<SwiftGrpcRequestBytes?, NSError?>>()
        nextRequestWithCompletion { message, error -> result.complete(message to error) }
        return result.await()
    }

    private fun SwiftGrpcRequestSourceProtocol.pullRecording(): RecordingCompletion =
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
