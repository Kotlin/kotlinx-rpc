/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.DefaultInteropHandlers
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.zeros
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds

class ServerBackpressureTest {
    @Test
    fun serverStreamingResumesAfterTheClientCollectsResponses() = grpcServerTest {
        val clientGate = registerNewGate("client response collection")
        val responsesSent = CompletableDeferred<Unit>()
        start {
            streamingOutputCall = {
                flow {
                    repeat(MESSAGE_COUNT) { emit(StreamingOutputCallResponse { payload = zeros(MESSAGE_SIZE) }) }
                    responsesSent.complete(Unit)
                }
            }
        }

        coroutineScope {
            val received = async {
                var count = 0
                testService.streamingOutputCall(StreamingOutputCallRequest {}).collect {
                    assertEquals(zeros(MESSAGE_SIZE), it.payload)
                    count++
                    if (count == 1) {
                        clientGate.pass()
                    }
                }
                count
            }
            clientGate.awaitReached(AWAIT_TIMEOUT)
            assertNull(
                withTimeoutOrNull(500.milliseconds) { responsesSent.await() },
                "The server sent every response while client collection was paused",
            )

            clientGate.open()

            assertEquals(MESSAGE_COUNT, withTimeout(AWAIT_TIMEOUT) { received.await() })
        }
        withTimeout(AWAIT_TIMEOUT) { responsesSent.await() }
    }

    @Test
    fun clientStreamingResumesAfterTheServiceCollectsRequests() = grpcServerTest {
        val serviceGate = registerNewGate("service request collection")
        val requestsSent = CompletableDeferred<Unit>()
        start {
            streamingInputCall = { requests ->
                var count = 0
                DefaultInteropHandlers.streamingInputCall(requests.onEach {
                    count++
                    if (count == 1) {
                        serviceGate.pass()
                    }
                })
            }
        }

        coroutineScope {
            val response = async {
                testService.streamingInputCall(flow {
                    repeat(MESSAGE_COUNT) { emit(StreamingInputCallRequest { payload = zeros(MESSAGE_SIZE) }) }
                    requestsSent.complete(Unit)
                })
            }
            serviceGate.awaitReached(AWAIT_TIMEOUT)
            assertNull(
                withTimeoutOrNull(500.milliseconds) { requestsSent.await() },
                "The client sent every request while service collection was paused",
            )

            serviceGate.open()

            withTimeout(AWAIT_TIMEOUT) { requestsSent.await() }
            val result = withTimeout(AWAIT_TIMEOUT) { response.await() }
            assertEquals(MESSAGE_COUNT * MESSAGE_SIZE, result.aggregatedPayloadSize)
        }
    }

    private companion object {
        const val MESSAGE_COUNT = 64
        const val MESSAGE_SIZE = 64 * 1024
    }
}
