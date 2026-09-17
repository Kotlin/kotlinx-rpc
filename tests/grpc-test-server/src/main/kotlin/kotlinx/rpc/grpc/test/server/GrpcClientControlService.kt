/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.StreamObserver
import kxrpc.testing.AwaitEventRequest
import kxrpc.testing.CallEvent
import kxrpc.testing.CallTrace
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.ControlAck
import kxrpc.testing.DiscardScenarioRequest
import kxrpc.testing.GetTraceRequest
import kxrpc.testing.GetScenarioDiagnosticsRequest
import kxrpc.testing.GrpcClientControlServiceGrpc
import kxrpc.testing.ReleaseBarrierRequest
import kxrpc.testing.ScenarioDiagnostics

internal class GrpcClientControlService(
    private val registry: CallScenarioRegistry,
) : GrpcClientControlServiceGrpc.GrpcClientControlServiceImplBase() {
    override fun configureScenario(
        request: ConfigureScenarioRequest,
        responseObserver: StreamObserver<ControlAck>,
    ) {
        respond(responseObserver) {
            registry.configure(request)
            ControlAck.getDefaultInstance()
        }
    }

    override fun awaitEvent(request: AwaitEventRequest, responseObserver: StreamObserver<CallEvent>) {
        respond(responseObserver) {
            registry.awaitEvent(request.callId, request.event, request.occurrence)
        }
    }

    override fun releaseBarrier(
        request: ReleaseBarrierRequest,
        responseObserver: StreamObserver<ControlAck>,
    ) {
        respond(responseObserver) {
            registry.releaseBarrier(request.callId, request.barrier, request.occurrence)
            ControlAck.getDefaultInstance()
        }
    }

    override fun getTrace(request: GetTraceRequest, responseObserver: StreamObserver<CallTrace>) {
        respond(responseObserver) {
            registry.trace(request.callId)
        }
    }

    override fun getScenarioDiagnostics(
        request: GetScenarioDiagnosticsRequest,
        responseObserver: StreamObserver<ScenarioDiagnostics>,
    ) {
        respond(responseObserver) {
            registry.diagnostics(request.callId)
        }
    }

    override fun discardScenario(
        request: DiscardScenarioRequest,
        responseObserver: StreamObserver<ControlAck>,
    ) {
        respond(responseObserver) {
            registry.discard(request.callId)
            ControlAck.getDefaultInstance()
        }
    }

    private fun <T> respond(responseObserver: StreamObserver<T>, operation: () -> T) {
        try {
            responseObserver.onNext(operation())
            responseObserver.onCompleted()
        } catch (error: Throwable) {
            responseObserver.onError(error.toGrpcStatus().asRuntimeException())
        }
    }
}
