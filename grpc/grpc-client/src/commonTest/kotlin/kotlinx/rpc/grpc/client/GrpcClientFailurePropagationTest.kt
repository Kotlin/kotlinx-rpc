/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.trailers
import kotlinx.rpc.grpc.client.testing.ExpectedServerEvent
import kotlinx.rpc.grpc.client.testing.assertGrpcStatus
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kxrpc.testing.EventType
import kxrpc.testing.GrpcStatus as ScenarioGrpcStatus
import kxrpc.testing.MetadataEntry
import kxrpc.testing.TerminalBehavior
import kxrpc.testing.TerminalStage
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class GrpcClientFailurePropagationTest {
    @Test
    fun statusBeforeHeadersPropagatesOnce() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(failureObserver(callbackEvents)) },
            scenario = {
                // Trailers-only response: the server delivers no request, and the client gets
                // no initial-headers callback.
                trailingMetadata = listOf(metadataEntry(FAILURE_STAGE_KEY, "before-headers"))
                terminalBehavior = terminalBehavior(
                    GrpcStatusCode.UNAVAILABLE,
                    "failed before headers",
                    TerminalStage.BEFORE_INITIAL_METADATA,
                )
            },
        ) {
            val failure = assertGrpcStatus(GrpcStatusCode.UNAVAILABLE, "failed before headers") {
                testService.emptyCall(Empty {})
            }

            // The client still gets the terminal callback and the trailers exactly once.
            assertEquals("before-headers", failure.trailers?.getAll(FAILURE_STAGE_KEY)?.single())
            assertEquals(listOf("close:UNAVAILABLE"), callbackEvents)
            assertServerTrace(
                listOf(
                    ExpectedServerEvent(EventType.CALL_ACCEPTED),
                    ExpectedServerEvent(EventType.CALL_CLOSED),
                )
            )
        }
    }

    @Test
    fun statusAfterHeadersPreservesHeadersAndPropagatesTrailers() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(failureObserver(callbackEvents)) },
            scenario = {
                // The server sends real initial headers, and it fails before the service returns
                // a response.
                initialMetadata = listOf(metadataEntry(FAILURE_STAGE_KEY, "headers"))
                trailingMetadata = listOf(metadataEntry(FAILURE_STAGE_KEY, "trailers"))
                terminalBehavior = terminalBehavior(
                    GrpcStatusCode.INTERNAL,
                    "failed after headers",
                    TerminalStage.AFTER_INITIAL_METADATA,
                )
            },
        ) {
            val failure = assertGrpcStatus(GrpcStatusCode.INTERNAL, "failed after headers") {
                testService.emptyCall(Empty {})
            }

            // The client observes the headers before close, and only the trailers accompany
            // the failure.
            assertEquals("trailers", failure.trailers?.getAll(FAILURE_STAGE_KEY)?.single())
            assertEquals(listOf("headers:headers", "close:INTERNAL"), callbackEvents)
            assertServerTrace(
                listOf(
                    ExpectedServerEvent(EventType.CALL_ACCEPTED),
                    ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
                    ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
                    ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT),
                    ExpectedServerEvent(EventType.CALL_CLOSED),
                )
            )
        }
    }

    private fun failureObserver(callbackEvents: MutableList<String>): GrpcClientInterceptor =
        object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                onHeaders { headers ->
                    callbackEvents += "headers:${headers.getAll(FAILURE_STAGE_KEY).single()}"
                }
                onClose { status, _ -> callbackEvents += "close:${status.statusCode.name}" }
                return proceed(request)
            }
        }

    private companion object {
        const val FAILURE_STAGE_KEY: String = "x-failure-stage"

        fun metadataEntry(key: String, value: String): MetadataEntry = MetadataEntry {
            this.key = key
            this.value = ByteString(*value.encodeToByteArray())
        }

        fun terminalBehavior(
            code: GrpcStatusCode,
            description: String,
            stage: TerminalStage,
        ): TerminalBehavior = TerminalBehavior {
            status = ScenarioGrpcStatus {
                this.code = code.value
                this.description = description
            }
            this.stage = stage
        }
    }
}
