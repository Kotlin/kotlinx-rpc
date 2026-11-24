/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.SimpleRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.copy
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.getBinary
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.clientInterceptor
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.serverInterceptor
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ServerMetadataTest {
    @Test
    fun requestMetadataReachesTheInterceptor() = grpcServerTest {
        val interceptorHeaders = CompletableDeferred<GrpcMetadata>()
        start(
            serverConfig = {
                intercept(serverInterceptor { interceptorHeaders.complete(requestHeaders.copy()) })
            },
            clientConfig = { intercept(clientInterceptor { requestHeaders.appendTestMetadata() }) },
        )

        testService.unaryCall(SimpleRequest {})

        withTimeout(AWAIT_TIMEOUT) {
            assertTestMetadata(interceptorHeaders.await())
        }
    }

    @Test
    fun responseHeadersAndTrailersReachTheClient() = grpcServerTest {
        val headers = CompletableDeferred<GrpcMetadata>()
        val trailers = CompletableDeferred<GrpcMetadata>()
        start(
            serverConfig = {
                intercept(serverInterceptor {
                    responseHeaders.appendTestMetadata()
                    responseTrailers.appendTestMetadata()
                })
            },
            clientConfig = {
                intercept(clientInterceptor {
                    onHeaders { headers.complete(it.copy()) }
                    onClose { _, metadata -> trailers.complete(metadata.copy()) }
                })
            },
        )

        testService.unaryCall(SimpleRequest {})

        withTimeout(AWAIT_TIMEOUT) {
            assertTestMetadata(headers.await())
            assertTestMetadata(trailers.await())
        }
    }

    @Test
    fun exceptionTrailersMergeWithInterceptorTrailers() = grpcServerTest {
        start(
            serverConfig = {
                intercept(serverInterceptor { responseTrailers.append("x-origin", "interceptor") })
            },
        ) {
            unaryCall = {
                throw GrpcStatusCode.INVALID_ARGUMENT.asException(
                    trailers = GrpcMetadata { append("x-origin", "exception") },
                )
            }
        }

        val error = assertGrpcStatus(GrpcStatusCode.INVALID_ARGUMENT) { testService.unaryCall(SimpleRequest {}) }

        val expected = listOf("interceptor", "exception")
        assertEquals(expected, error.trailers?.getAll("x-origin"))
    }

    private fun GrpcMetadata.appendTestMetadata() {
        append("x-ascii", "value")
        appendBinary("x-bytes-bin", byteArrayOf(0, 1, -1))
        append("x-ordered", "first")
        append("x-ordered", "second")
    }

    private fun assertTestMetadata(metadata: GrpcMetadata) {
        assertEquals("value", metadata["x-ascii"])
        assertContentEquals(byteArrayOf(0, 1, -1), metadata.getBinary("x-bytes-bin"))
        assertEquals(listOf("first", "second"), metadata.getAll("x-ordered"))
    }
}
