/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit
import kxrpc.testing.AwaitEventRequest
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.DiscardScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.FlowControlBehavior
import kxrpc.testing.GetTraceRequest
import kxrpc.testing.GetScenarioDiagnosticsRequest
import kxrpc.testing.GrpcClientControlServiceGrpc
import kxrpc.testing.GrantInboundDemandRequest
import kxrpc.testing.ReleaseBarrierRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GrpcClientControlServiceTest {
    @Test
    fun servesControlLifecycleOverGrpc() {
        val registry = CallScenarioRegistry()
        val server = NettyServerBuilder.forPort(0)
            .addService(GrpcClientControlService(registry))
            .build()
            .start()
        val channel = NettyChannelBuilder.forAddress("127.0.0.1", server.port)
            .usePlaintext()
            .build()
        try {
            val client = GrpcClientControlServiceGrpc.newBlockingStub(channel)
            val callId = "network-call"
            client.configureScenario(ConfigureScenarioRequest.newBuilder().setCallId(callId).build())

            val recorded = registry.recordEvent(callId, EventType.CALL_ACCEPTED)
            val awaited = client.awaitEvent(
                AwaitEventRequest.newBuilder()
                    .setCallId(callId)
                    .setEvent(EventType.CALL_ACCEPTED)
                    .setOccurrence(1)
                    .build()
            )
            assertEquals(recorded, awaited)
            assertEquals(listOf(recorded), client.getTrace(getTraceRequest(callId)).eventsList)
            assertEquals(callId, client.getScenarioDiagnostics(diagnosticsRequest(callId)).callId)

            client.discardScenario(DiscardScenarioRequest.newBuilder().setCallId(callId).build())
            val error = kotlin.runCatching { client.getTrace(getTraceRequest(callId)) }.exceptionOrNull()
            assertEquals(Status.Code.NOT_FOUND, (error as StatusRuntimeException).status.code)
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
            server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS)
        }
    }

    @Test
    fun routesScenarioLifecycleThroughControlRpcMethods() {
        val registry = CallScenarioRegistry()
        val service = GrpcClientControlService(registry)
        val callId = "control-call"

        assertSuccess(
            observer = RecordingObserver(),
            invoke = { observer ->
                service.configureScenario(
                    ConfigureScenarioRequest.newBuilder()
                        .setCallId(callId)
                        .addBarriers(
                            Barrier.newBuilder()
                                .setType(BarrierType.SEND_RESPONSE)
                                .setOccurrence(1)
                                .build()
                        )
                        .setFlowControl(
                            FlowControlBehavior.newBuilder()
                                .setManualInboundDemand(true)
                        )
                        .build(),
                    observer,
                )
            },
        )

        val recorded = registry.callAccepted(callId)
        val awaited = assertSuccess(
            observer = RecordingObserver(),
            invoke = { observer ->
                service.awaitEvent(
                    AwaitEventRequest.newBuilder()
                        .setCallId(callId)
                        .setEvent(EventType.CALL_ACCEPTED)
                        .setOccurrence(1)
                        .build(),
                    observer,
                )
            },
        )
        assertEquals(recorded, awaited)

        val trace = assertSuccess(
            observer = RecordingObserver(),
            invoke = { observer ->
                service.getTrace(getTraceRequest(callId), observer)
            },
        )
        assertEquals(listOf(recorded), trace.eventsList)

        val diagnostics = assertSuccess(
            observer = RecordingObserver(),
            invoke = { observer ->
                service.getScenarioDiagnostics(diagnosticsRequest(callId), observer)
            },
        )
        assertEquals(1, diagnostics.activeCallCount)
        assertEquals(1, diagnostics.outstandingBarriersCount)
        assertEquals(0, diagnostics.controlWaiterCount)

        assertSuccess(
            observer = RecordingObserver(),
            invoke = { observer ->
                service.releaseBarrier(
                    ReleaseBarrierRequest.newBuilder()
                        .setCallId(callId)
                        .setBarrier(BarrierType.SEND_RESPONSE)
                        .setOccurrence(1)
                        .build(),
                    observer,
                )
            },
        )
        registry.awaitBarrier(callId, BarrierType.SEND_RESPONSE, 1)

        val demandGrants = mutableListOf<Int>()
        registry.registerInboundDemand(callId, demandGrants::add)
        assertSuccess(
            observer = RecordingObserver(),
            invoke = { observer ->
                service.grantInboundDemand(
                    GrantInboundDemandRequest.newBuilder()
                        .setCallId(callId)
                        .setMessageCount(2)
                        .build(),
                    observer,
                )
            },
        )
        assertEquals(listOf(2), demandGrants)

        assertSuccess(
            observer = RecordingObserver(),
            invoke = { observer ->
                service.discardScenario(DiscardScenarioRequest.newBuilder().setCallId(callId).build(), observer)
            },
        )

        val missing = RecordingObserver<kxrpc.testing.CallTrace>()
        service.getTrace(getTraceRequest(callId), missing)
        assertEquals(Status.Code.NOT_FOUND, (missing.error as StatusRuntimeException).status.code)
    }

    private fun getTraceRequest(callId: String): GetTraceRequest {
        return GetTraceRequest.newBuilder().setCallId(callId).build()
    }

    private fun diagnosticsRequest(callId: String): GetScenarioDiagnosticsRequest {
        return GetScenarioDiagnosticsRequest.newBuilder().setCallId(callId).build()
    }

    private fun <T> assertSuccess(
        observer: RecordingObserver<T>,
        invoke: (RecordingObserver<T>) -> Unit,
    ): T {
        invoke(observer)
        assertNull(observer.error)
        assertTrue(observer.completed)
        return observer.values.single()
    }

    private class RecordingObserver<T> : StreamObserver<T> {
        val values = mutableListOf<T>()
        var error: Throwable? = null
        var completed: Boolean = false

        override fun onNext(value: T) {
            values += value
        }

        override fun onError(error: Throwable) {
            this.error = error
        }

        override fun onCompleted() {
            completed = true
        }
    }
}
