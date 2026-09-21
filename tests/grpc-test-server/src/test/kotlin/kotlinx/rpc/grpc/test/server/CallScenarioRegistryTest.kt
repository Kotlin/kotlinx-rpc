/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kxrpc.testing.AdversarialResponseCardinality
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.CallEvent
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GrpcStatus
import kxrpc.testing.MetadataEntry
import kxrpc.testing.TerminalBehavior
import kxrpc.testing.TerminalStage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CallScenarioRegistryTest {
    @Test
    fun recordsMonotonicTraceAndPerTypeOccurrences() {
        val registry = registry()
        registry.configure(scenario("call-1"))

        val accepted = registry.recordEvent("call-1", EventType.CALL_ACCEPTED)
        val firstRequest = registry.recordEvent("call-1", EventType.REQUEST_MESSAGE_RECEIVED)
        val secondRequest = registry.recordEvent("call-1", EventType.REQUEST_MESSAGE_RECEIVED)

        assertEquals(1L, accepted.sequence)
        assertEquals(1, accepted.occurrence)
        assertEquals(2L, firstRequest.sequence)
        assertEquals(1, firstRequest.occurrence)
        assertEquals(3L, secondRequest.sequence)
        assertEquals(2, secondRequest.occurrence)
        assertEquals(listOf(accepted, firstRequest, secondRequest), registry.trace("call-1").eventsList)
    }

    @Test
    fun eventWaiterCompletesAfterRequestedOccurrenceIsRecorded() {
        val registry = registry()
        registry.configure(scenario("call-2"))

        val executor = Executors.newSingleThreadExecutor()
        try {
            val waiter = executor.submit<CallEvent> {
                registry.awaitEvent("call-2", EventType.REQUEST_MESSAGE_RECEIVED, 2)
            }

            registry.recordEvent("call-2", EventType.REQUEST_MESSAGE_RECEIVED)
            registry.recordEvent("call-2", EventType.REQUEST_MESSAGE_RECEIVED)

            val event = waiter.get(1, TimeUnit.SECONDS)
            assertEquals(2L, event.sequence)
            assertEquals(2, event.occurrence)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun configuredBarrierBlocksUntilReleased() {
        val registry = registry()
        registry.configure(scenario("call-3", BarrierType.SEND_RESPONSE))

        val executor = Executors.newSingleThreadExecutor()
        try {
            val waiter = executor.submit {
                registry.awaitBarrier("call-3", BarrierType.SEND_RESPONSE, 1)
            }

            registry.releaseBarrier("call-3", BarrierType.SEND_RESPONSE, 1)

            waiter.get(1, TimeUnit.SECONDS)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun discardRemovesStateAndWakesWaiters() {
        val registry = registry()
        registry.configure(scenario("call-4"))

        val executor = Executors.newSingleThreadExecutor()
        try {
            val waiter = executor.submit<ScenarioDiscardedException> {
                assertFailsWith {
                    registry.awaitEvent("call-4", EventType.CALL_CLOSED, 1)
                }
            }

            registry.awaitWaiterCount("call-4", 1)
            registry.discard("call-4")

            assertEquals("scenario 'call-4' was discarded", waiter.get(1, TimeUnit.SECONDS).message)
            assertEquals(0, registry.activeScenarioCount())
            assertFailsWith<ScenarioNotFoundException> { registry.trace("call-4") }
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun rejectsInvalidAndDuplicateConfiguration() {
        val registry = registry()

        assertFailsWith<IllegalArgumentException> { registry.configure(scenario("")) }
        assertFailsWith<IllegalArgumentException> {
            registry.configure(scenario("unspecified", BarrierType.BARRIER_TYPE_UNSPECIFIED))
        }

        registry.configure(scenario("call-5"))
        assertFailsWith<IllegalStateException> { registry.configure(scenario("call-5")) }
        assertFailsWith<IllegalStateException> {
            registry.releaseBarrier("call-5", BarrierType.SEND_RESPONSE, 1)
        }
    }

    @Test
    fun preservesPhaseThreeScenarioConfiguration() {
        val registry = registry()
        val request = ConfigureScenarioRequest.newBuilder()
            .setCallId("phase-3")
            .addInitialMetadata(metadata("x-test-value", "first"))
            .addInitialMetadata(metadata("x-test-value", ""))
            .addInitialMetadata(metadata("x-test-bin", byteArrayOf(0, -1, 42)))
            .addTrailingMetadata(metadata("x-trailer", "done"))
            .setTerminalBehavior(
                TerminalBehavior.newBuilder()
                    .setStatus(
                        GrpcStatus.newBuilder()
                            .setCode(13)
                            .setDescription("configured failure")
                    )
                    .setStage(TerminalStage.AFTER_RESPONSE_MESSAGES)
                    .setResponseMessageCount(2)
            )
            .setAdversarialResponseCardinality(AdversarialResponseCardinality.DUPLICATE_RESPONSE)
            .build()

        registry.configure(request)

        assertEquals(request, registry.configuration("phase-3"))
    }

    @Test
    fun rejectsInvalidPhaseThreeConfiguration() {
        val registry = registry()

        assertFailsWith<IllegalArgumentException> {
            registry.configure(
                scenario("uppercase-metadata").toBuilder()
                    .addInitialMetadata(metadata("X-Test", "value"))
                    .build()
            )
        }
        assertFailsWith<IllegalArgumentException> {
            registry.configure(
                scenario("missing-status").toBuilder()
                    .setTerminalBehavior(
                        TerminalBehavior.newBuilder()
                            .setStage(TerminalStage.AFTER_SERVICE_COMPLETION)
                    )
                    .build()
            )
        }
        assertFailsWith<IllegalArgumentException> {
            registry.configure(
                scenario("missing-response-count").toBuilder()
                    .setTerminalBehavior(
                        terminalBehavior(TerminalStage.AFTER_RESPONSE_MESSAGES)
                    )
                    .build()
            )
        }
        assertFailsWith<IllegalArgumentException> {
            registry.configure(
                scenario("unexpected-response-count").toBuilder()
                    .setTerminalBehavior(
                        terminalBehavior(TerminalStage.AFTER_SERVICE_COMPLETION).toBuilder()
                            .setResponseMessageCount(1)
                    )
                    .build()
            )
        }
    }

    @Test
    fun diagnosticsReportActiveCallsWaitersAndOutstandingBarriers() {
        val registry = registry()
        val responseBarrier = barrier(BarrierType.SEND_RESPONSE, 2)
        val halfCloseBarrier = barrier(BarrierType.DELIVER_CLIENT_HALF_CLOSE, 1)
        registry.configure(
            ConfigureScenarioRequest.newBuilder()
                .setCallId("diagnostics")
                .addBarriers(responseBarrier)
                .addBarriers(halfCloseBarrier)
                .build()
        )
        registry.callAccepted("diagnostics")

        val executor = Executors.newSingleThreadExecutor()
        try {
            val waiter = executor.submit<CallEvent> {
                registry.awaitEvent("diagnostics", EventType.CALL_CLOSED, 1)
            }
            registry.awaitWaiterCount("diagnostics", 1)

            val active = registry.diagnostics("diagnostics")
            assertEquals(1, active.activeCallCount)
            assertEquals(listOf(responseBarrier, halfCloseBarrier), active.outstandingBarriersList)
            assertEquals(1, active.controlWaiterCount)

            registry.releaseBarrier("diagnostics", BarrierType.SEND_RESPONSE, 2)
            val closed = registry.callClosed("diagnostics")
            assertEquals(closed, waiter.get(1, TimeUnit.SECONDS))

            val completed = registry.diagnostics("diagnostics")
            assertEquals(0, completed.activeCallCount)
            assertEquals(listOf(halfCloseBarrier), completed.outstandingBarriersList)
            assertEquals(0, completed.controlWaiterCount)
        } finally {
            executor.shutdownNow()
        }
    }

    private fun registry(): CallScenarioRegistry = CallScenarioRegistry(Duration.ofSeconds(2))

    private fun scenario(callId: String, vararg barrierTypes: BarrierType): ConfigureScenarioRequest {
        return ConfigureScenarioRequest.newBuilder()
            .setCallId(callId)
            .addAllBarriers(
                barrierTypes.map { type ->
                    Barrier.newBuilder()
                        .setType(type)
                        .setOccurrence(1)
                        .build()
                }
            )
            .build()
    }

    private fun barrier(type: BarrierType, occurrence: Int): Barrier {
        return Barrier.newBuilder()
            .setType(type)
            .setOccurrence(occurrence)
            .build()
    }

    private fun metadata(key: String, value: String): MetadataEntry {
        return metadata(key, value.encodeToByteArray())
    }

    private fun metadata(key: String, value: ByteArray): MetadataEntry {
        return MetadataEntry.newBuilder()
            .setKey(key)
            .setValue(ByteString.copyFrom(value))
            .build()
    }

    private fun terminalBehavior(stage: TerminalStage): TerminalBehavior {
        return TerminalBehavior.newBuilder()
            .setStatus(GrpcStatus.newBuilder().setCode(13).setDescription("failure"))
            .setStage(stage)
            .build()
    }
}
