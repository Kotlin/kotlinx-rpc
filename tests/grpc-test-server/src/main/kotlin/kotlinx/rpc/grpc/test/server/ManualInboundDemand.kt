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
 * Client tests use it to check send-side backpressure. When the server stops reading, the client under test must
 * suspend its sends, not buffer without limit or fail. A regular grpc-java server reads as fast as the client sends,
 * so it can't show whether the client does this. With manual inbound demand, a test can do these checks:
 *
 * - Stop the server from reading, and check that the client suspends.
 * - Grant demand, and check that the client continues in order.
 * - Cancel a call that waits to send, and check that the cancellation cleans up.
 *
 * The [kxrpc.testing.EventType.REQUEST_MESSAGE_RECEIVED] events show how many messages the server has read, so these
 * tests need no timing assumptions.
 *
 * Usually grpc-java requests the next request message itself. This function turns that off and connects the call to
 * the inbound demand of the scenario. Then the server reads a message only after the test grants demand through
 * `GrantInboundDemand`. Unread messages stay in the transport, so the HTTP/2 flow-control window toward the server
 * isn't refilled. Then the sends of the client must wait at some point.
 *
 * Does nothing if the call has no scenario or the scenario doesn't enable manual inbound demand. Only methods that
 * stream requests call it. Call it from the service method itself, because grpc-java rejects the setup after that
 * method returns.
 *
 * [ResponseReadinessGate] handles the response side of flow control.
 *
 * @param responseObserver The observer that grpc-java gives to the service method.
 */
internal fun enableManualInboundDemandIfConfigured(
    registry: CallScenarioRegistry,
    responseObserver: StreamObserver<*>,
) {
    val callId = TEST_CALL_ID_CONTEXT_KEY.get() ?: return
    if (!registry.configuration(callId).flowControl.manualInboundDemand) return
    val serverObserver = responseObserver as? ServerCallStreamObserver<*>
        ?: error("the response observer of scenario '$callId' isn't a ServerCallStreamObserver")

    serverObserver.disableAutoRequest()
    // GrantInboundDemand calls serverObserver.request(n) through the registry. The registry also forwards the demand
    // that the test granted before this point.
    registry.registerInboundDemand(callId, serverObserver::request)
}
