/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver

/**
 * Lets the test decide when the server reads the next request message, for scenarios that enable
 * `flow_control.manual_inbound_demand`.
 *
 * This lets client tests exercise send-side backpressure deterministically. When the server stops reading, the client
 * under test should suspend its sends instead of buffering without limit or failing. A regular grpc-java server
 * cannot show whether it does, as it reads as fast as the client sends. With manual demand, a test can stop the
 * server from reading, check that the client suspends, and then check that it continues in order once demand is
 * granted, or that cancelling a call stuck on sending cleans up. The
 * [kxrpc.testing.EventType.REQUEST_MESSAGE_RECEIVED] events show exactly how many messages the server has read, so
 * these tests need no timing assumptions.
 *
 * grpc-java normally requests the next request message on its own. This turns that off and connects the call to the
 * scenario's demand endpoint instead: the server reads a message only after the test grants demand through
 * `GrantInboundDemand`. Unread messages stay in the transport, so the HTTP/2 flow-control window toward the server is
 * no longer replenished, and the client's sends eventually have to wait.
 *
 * Does nothing for calls without a scenario or without manual inbound demand. Only methods that stream requests call
 * it, and only from the service method itself, because grpc-java rejects the setup once that method returns.
 *
 * The response side of flow control is handled separately by [ResponseReadinessGate].
 *
 * @param registry Registry containing the selected scenario.
 * @param responseObserver Observer supplied to the generated service method.
 */
internal fun enableManualInboundDemandIfConfigured(
    registry: CallScenarioRegistry,
    responseObserver: StreamObserver<*>,
) {
    val callId = TEST_CALL_ID_CONTEXT_KEY.get() ?: return
    if (!registry.configuration(callId).flowControl.manualInboundDemand) return
    val serverObserver = responseObserver as? ServerCallStreamObserver<*>
        ?: error("grpc-java did not supply a ServerCallStreamObserver for scenario '$callId'")

    serverObserver.disableAutoRequest()
    // GrantInboundDemand reaches serverObserver.request(n) through the registry, which also forwards demand granted
    // before this point.
    registry.registerInboundDemand(callId, serverObserver::request)
}
