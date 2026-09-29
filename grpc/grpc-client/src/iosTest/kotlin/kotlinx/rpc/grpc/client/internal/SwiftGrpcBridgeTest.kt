/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
    kotlin.concurrent.atomics.ExperimentalAtomicApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.addressOf
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
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.getAllBinary
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.remove
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.internal.KOTLINX_RPC_VERSION
import platform.Foundation.NSError
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMetadata
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestBytes
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestSourceProtocol
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
    fun ownedRequestStorageCoversEmptyAndLargeMessages() {
        for (expected in listOf(byteArrayOf(), ByteArray(4 * 1024 * 1024 - 1024) { it.toByte() })) {
            val owned = testRequest(expected)
            assertEquals(expected.size.toLong(), owned.length())
            assertFalse(owned.isDiscarded())
            owned.discard()
            assertTrue(owned.isDiscarded())
        }
    }

    @Test
    fun failedOwnedEncodingDiscardsStorageAndKeepsOriginalFailure() = runTest {
        val expected = IllegalStateException("write failed")
        lateinit var storage: SwiftRequestMessageWriter
        val marshaller = object : GrpcMarshaller<Int> {
            override fun prepare(value: Int, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
                object : GrpcEncodedMessage {
                    override val size: Int = 1
                    override fun writeTo(writer: GrpcMessageWriter) {
                        storage = writer as SwiftRequestMessageWriter
                        throw expected
                    }
                }

            override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): Int =
                error("not used")
        }
        val source = KotlinGrpcRequestSource(this, flowOf(1)) { encodeSwiftRequest(marshaller, it) }

        assertNotNull(source.pull().second)
        assertSame(expected, source.originalFailure)
        assertFailsWith<IllegalStateException> { storage.seal() }
        assertTrue(storage.requestBytes().isDiscarded())
        source.cancelAndJoin()
    }

    @Test
    fun offeredOwnedRequestIsDiscardedOnCancellation() = runTest {
        lateinit var owned: SwiftGrpcRequestBytes
        val source = KotlinGrpcRequestSource(this, flowOf(byteArrayOf(7))) {
            encodeSwiftRequest(ByteArrayRequestMarshaller, it).also { bytes ->
                owned = bytes
            }
        }

        source.cancelAndJoin()
        assertTrue(owned.isDiscarded())
    }

    @Test
    fun unclaimedSingleOwnedRequestIsDiscardedOnCancellation() = runTest {
        val source = KotlinGrpcSingleRequestSource(this, flowOf(byteArrayOf(9))) {
            encodeSwiftRequest(ByteArrayRequestMarshaller, it)
        }
        val owned = assertNotNull(source.readyMessage)

        source.cancelAndJoin()
        assertTrue(owned.isDiscarded())
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
                if (message == null) completed.complete(Unit) else pullNext()
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
    fun requestSourceDeliveringRejectsConcurrentPullAndCancellationCompletesAcceptedPull() = runTest {
        val source = KotlinGrpcRequestSource(this, flowOf(42)) { value ->
            testRequest(value)
        }
        val accepted = source.pullRecording()
        val rejected = source.pullRecording()

        val (rejectedMessage, rejectedError) = rejected.result.await()
        assertNull(rejectedMessage)
        assertNotNull(rejectedError)
        assertEquals(1, rejected.calls.load())
        assertFalse(accepted.result.isCompleted)

        source.cancel()
        assertEquals(null to null, withTimeout(1_000.milliseconds) { accepted.result.await() })
        assertEquals(1, accepted.calls.load())

        withTimeout(1_000.milliseconds) { source.cancelAndJoin() }
        assertEquals(1, accepted.calls.load())
        assertEquals(1, rejected.calls.load())
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
        assertEquals(1, pull.calls.load())
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
        encodeSwiftRequest(ByteArrayRequestMarshaller, bytes)

    private companion object {
        const val STRESS_ITERATIONS = 1_000
    }
}

private object ByteArrayRequestMarshaller : GrpcMarshaller<ByteArray> {
    override fun prepare(value: ByteArray, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        GrpcEncodedMessage.of(value)

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): ByteArray =
        error("not used")
}
