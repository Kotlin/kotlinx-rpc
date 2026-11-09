/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import com.google.protobuf.kotlin.Empty
import com.google.protobuf.kotlin.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.successfulUnaryEvents
import kotlinx.rpc.grpc.getAll
import kxrpc.testing.EventType
import kxrpc.testing.MetadataEntry
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class GrpcClientMetadataLimitTest {
    @Test
    fun initialMetadataAboveTheLimitFailsTheCall() {
        grpcClientTest(
            scenario = {
                initialMetadata = listOf(metadataEntry(LARGE_METADATA_KEY, OVERSIZED_METADATA_VALUE))
            },
        ) {
            assertLimitFailure { testService.emptyCall(Empty {}) }

            // The failure came from the received headers, so the server must have sent them first.
            awaitServerEvent(EventType.INITIAL_HEADERS_SENT)
        }
    }

    @Test
    fun trailingMetadataAboveTheLimitFailsTheCall() {
        grpcClientTest(
            scenario = {
                // Separate initial headers keep the oversized trailing metadata in its own HEADERS frame.
                initialMetadata = listOf(metadataEntry(SMALL_METADATA_KEY, "small"))
                trailingMetadata = listOf(metadataEntry(LARGE_METADATA_KEY, OVERSIZED_METADATA_VALUE))
            },
        ) {
            assertLimitFailure { testService.emptyCall(Empty {}) }

            // The server closed with the oversized trailers before the client read them.
            awaitServerEvent(EventType.CALL_CLOSED)
        }
    }

    @Test
    fun metadataAboveConfiguredLimitFailsTheCall() {
        grpcClientTest(
            clientConfig = {
                maxInboundMetadataSize = CONFIGURED_METADATA_SIZE_LIMIT
            },
            scenario = {
                initialMetadata = listOf(metadataEntry(LARGE_METADATA_KEY, MEDIUM_METADATA_VALUE))
            },
        ) {
            assertLimitFailure { testService.emptyCall(Empty {}) }

            // The failure came from the received headers, so the server must have sent them first.
            awaitServerEvent(EventType.INITIAL_HEADERS_SENT)
        }
    }

    @Test
    fun largeMetadataPassesWithConfiguredLimit() {
        val recorder = MetadataRecorder()

        grpcClientTest(
            clientConfig = {
                // The metadata below is larger than the 16 KiB default of the transports, so only a
                // configured limit above that value can receive it.
                maxInboundMetadataSize = 32 * 1024
                intercept(recorder.interceptor())
            },
            scenario = {
                initialMetadata = listOf(metadataEntry(LARGE_METADATA_KEY, LARGE_METADATA_VALUE))
                trailingMetadata = listOf(metadataEntry(LARGE_METADATA_KEY, LARGE_METADATA_VALUE))
            },
        ) {
            testService.emptyCall(Empty {})

            assertEquals(listOf(LARGE_METADATA_VALUE), assertNotNull(recorder.headers).getAll(LARGE_METADATA_KEY))
            assertEquals(listOf(LARGE_METADATA_VALUE), assertNotNull(recorder.trailers).getAll(LARGE_METADATA_KEY))
            assertServerTrace(successfulUnaryEvents())
        }
    }

    @Test
    fun metadataUnderTheDefaultLimitPasses() {
        val recorder = MetadataRecorder()

        grpcClientTest(
            clientConfig = { intercept(recorder.interceptor()) },
            scenario = {
                initialMetadata = listOf(metadataEntry(LARGE_METADATA_KEY, MEDIUM_METADATA_VALUE))
            },
        ) {
            testService.emptyCall(Empty {})

            assertEquals(listOf(MEDIUM_METADATA_VALUE), assertNotNull(recorder.headers).getAll(LARGE_METADATA_KEY))
            assertServerTrace(successfulUnaryEvents())
        }
    }

    @Test
    fun metadataWithinConfiguredLimitPasses() {
        val recorder = MetadataRecorder()

        grpcClientTest(
            clientConfig = {
                maxInboundMetadataSize = CONFIGURED_METADATA_SIZE_LIMIT
                intercept(recorder.interceptor())
            },
            scenario = {
                initialMetadata = listOf(metadataEntry(SMALL_METADATA_KEY, "small"))
            },
        ) {
            testService.emptyCall(Empty {})

            assertEquals(listOf("small"), assertNotNull(recorder.headers).getAll(SMALL_METADATA_KEY))
            assertServerTrace(successfulUnaryEvents())
        }
    }

    @Test
    fun clientRejectsNonPositiveMaxInboundMetadataSize() {
        assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { maxInboundMetadataSize = 0 }
        }
    }

    /**
     * Runs [block] and asserts that it fails with a status exception of any code.
     *
     * Each backend reports metadata over its receive limit with a status of its own, so the tests
     * can't expect one code for all platforms.
     */
    private suspend fun assertLimitFailure(block: suspend () -> Unit): GrpcStatusException {
        val failure = try {
            block()
            null
        } catch (error: Exception) {
            error
        }
        return assertIs<GrpcStatusException>(
            failure,
            "expected the call to fail, actual: ${failure?.let { "${it::class.simpleName}: ${it.message}" }}",
        )
    }

    /** An ASCII metadata entry with which a scenario makes the server send large response metadata. */
    private fun metadataEntry(key: String, value: String): MetadataEntry {
        return MetadataEntry {
            this.key = key
            this.value = ByteString(*value.encodeToByteArray())
        }
    }

    /** Records the response headers and trailers of the calls of one test. */
    private class MetadataRecorder {
        var headers: GrpcMetadata? = null
            private set

        var trailers: GrpcMetadata? = null
            private set

        fun interceptor(): GrpcClientInterceptor = object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                onHeaders { headers -> this@MetadataRecorder.headers = headers }
                onClose { _, trailers -> this@MetadataRecorder.trailers = trailers }
                return proceed(request)
            }
        }
    }

    private companion object {
        const val SMALL_METADATA_KEY: String = "x-test-small-metadata"
        const val LARGE_METADATA_KEY: String = "x-test-large-metadata"

        /** Smaller than the 8 KiB default limit, so only a test that sets it can reject the values below. */
        const val CONFIGURED_METADATA_SIZE_LIMIT: Int = 2048

        /** Larger than [CONFIGURED_METADATA_SIZE_LIMIT], smaller than the 8 KiB default limit. */
        val MEDIUM_METADATA_VALUE: String = "x".repeat(4096)

        /** Larger than the 16 KiB transport default, smaller than the 32 KiB configured test limit. */
        val LARGE_METADATA_VALUE: String = "x".repeat(20 * 1024)

        /** Larger than the 8 KiB default limit of every backend. */
        val OVERSIZED_METADATA_VALUE: String = "x".repeat(64 * 1024)
    }
}
