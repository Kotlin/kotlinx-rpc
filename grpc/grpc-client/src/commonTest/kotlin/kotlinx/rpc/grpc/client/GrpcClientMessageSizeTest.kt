/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.invoke
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.testing.GrpcClientTestServer
import kotlinx.rpc.grpc.client.testing.GrpcClientTestFixture
import kotlinx.rpc.grpc.client.testing.assertGrpcStatusCode
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.test.TestPlatform
import kotlinx.rpc.test.currentPlatform
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val swiftBackendPlatforms =
    setOf(TestPlatform.IOS, TestPlatform.MACOS, TestPlatform.TVOS, TestPlatform.WATCHOS)

class GrpcClientMessageSizeTest {
    @Test
    fun responseAboveDefaultLimitFailsTheCall() = grpcClientTest {
        // The serialized response adds overhead to the payload, so it exceeds the default limit.
        // The nio transport of grpc-swift maps the decoder error of an oversized message to INTERNAL,
        // so this test fails on Apple platforms until the transport reports RESOURCE_EXHAUSTED.
        assertGrpcStatusCode(GrpcStatusCode.RESOURCE_EXHAUSTED) {
            testService.unaryCall(SimpleRequest { responseSize = DEFAULT_LIMIT_BYTES })
        }
    }

    @Test
    fun responseAboveConfiguredLimitFailsTheCall() {
        grpcClientTestWithConfiguredLimit(CONFIGURED_LIMIT_BYTES) {
            assertGrpcStatusCode(GrpcStatusCode.RESOURCE_EXHAUSTED) {
                testService.unaryCall(SimpleRequest { responseSize = REJECTED_RESPONSE_BYTES })
            }
        }
    }

    @Test
    fun responseWithinConfiguredLimitPasses() {
        grpcClientTestWithConfiguredLimit(CONFIGURED_LIMIT_BYTES) {
            val response = testService.unaryCall(SimpleRequest { responseSize = ACCEPTED_RESPONSE_BYTES })

            assertContentEquals(ByteArray(ACCEPTED_RESPONSE_BYTES), response.payload.body.toByteArray())
        }
    }

    @Test
    fun clientRejectsNegativeMaxInboundMessageSize() {
        assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { maxInboundMessageSize = -1 }
        }
    }

    @Test
    fun clearedMaxInboundMessageSizeAllowsClientCreation() {
        GrpcClient(GrpcClientTestServer.HOST, GrpcClientTestServer.port) {
            maxInboundMessageSize = CONFIGURED_LIMIT_BYTES
            clearMaxInboundMessageSize()
        }.shutdown()
    }

    /**
     * Runs [block] with a configured [GrpcClientConfiguration.maxInboundMessageSize].
     *
     * Apple platforms can't enforce a configured limit, so client creation with it configured fails there.
     * [block] runs on the platforms that enforce the limit.
     */
    private fun grpcClientTestWithConfiguredLimit(
        limitBytes: Int,
        block: suspend GrpcClientTestFixture.() -> Unit,
    ) {
        if (currentPlatform in swiftBackendPlatforms) {
            val failure = assertFailsWith<IllegalStateException> {
                GrpcClient(GrpcClientTestServer.HOST, GrpcClientTestServer.port) {
                    maxInboundMessageSize = limitBytes
                }
            }

            assertTrue(failure.message.orEmpty().contains("maxInboundMessageSize"), "actual: ${failure.message}")
            return
        }
        grpcClientTest(
            clientConfig = { maxInboundMessageSize = limitBytes },
            block = block,
        )
    }

    private companion object {
        /** The 4 MiB default limit of [GrpcClientConfiguration.maxInboundMessageSize]. */
        const val DEFAULT_LIMIT_BYTES: Int = 4 * 1024 * 1024

        /** Smaller than the 4 MiB default limit, so only a test that sets it can reject the response below. */
        const val CONFIGURED_LIMIT_BYTES: Int = 4 * 1024

        /** Larger than [CONFIGURED_LIMIT_BYTES], smaller than the 4 MiB default limit. */
        const val REJECTED_RESPONSE_BYTES: Int = 32 * 1024

        /** Smaller than [CONFIGURED_LIMIT_BYTES]. */
        const val ACCEPTED_RESPONSE_BYTES: Int = 1024
    }
}
