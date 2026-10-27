/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver

/**
 * Disables the grpc-java auto request of the call if its scenario enables `flow_control.manual_inbound_demand`.
 * Then the server reads a request message only after the test grants demand through `AllowInboundReads`.
 *
 * Call it from the service method, because grpc-java rejects [ServerCallStreamObserver.disableAutoRequest] after
 * the service method returns.
 */
internal fun enableManualInboundDemandIfConfigured(
    registry: CallScenarioRegistry,
    responseObserver: StreamObserver<*>,
) {
    val callId = TEST_CALL_ID_CONTEXT_KEY.get() ?: return
    if (!registry.configuration(callId).flowControl.manualInboundDemand) {
        return
    }
    val serverObserver = responseObserver as? ServerCallStreamObserver<*>
        ?: error("the response observer of scenario '$callId' isn't a ServerCallStreamObserver")

    serverObserver.disableAutoRequest()
    // AllowInboundReads calls serverObserver.request(n) through the registry. The registry also forwards the demand
    // that the test granted before this point.
    registry.registerInboundDemand(callId, serverObserver::request)
}
