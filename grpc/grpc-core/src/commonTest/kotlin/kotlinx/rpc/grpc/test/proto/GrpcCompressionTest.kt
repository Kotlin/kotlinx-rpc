/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcCompression
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.Runtime
import kotlinx.rpc.grpc.test.assertContainsAll
import kotlinx.rpc.grpc.test.assertGrpcFailure
import kotlinx.rpc.grpc.test.captureStdErr
import kotlinx.rpc.grpc.test.clearNativeEnv
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.grpc.test.runtime
import kotlinx.rpc.grpc.test.setNativeEnv
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.collections.emptyList
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests that the client can configure the compression of requests.
 *
 * This test is hard to realize on native, as the gRPC-Core doesn't expose internal headers like
 * `grpc-encoding` to the user application. This means we cannot verify that the client or sever
 * actually sent those headers on native. Instead, we capture the grpc trace output (written to stderr)
 * and verify that the client and server actually used the compression algorithm.
 */
class GrpcCompressionTest : GrpcProtoTest() {
    override fun RpcServer.registerServices() {
        return registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `test gzip client compression - should succeed`() = runTest {
        testCompression(
            clientCompression = GrpcCompression.Gzip,
            expectedEncoding = "gzip",
            expectedRequestCompressionAlg = 2,
            expectedRequestDecompressionAlg = 2
        )
    }

    @Test
    fun `test identity compression - should not compress`() = runTest {
        testCompression(
            clientCompression = GrpcCompression.None,
            expectedEncoding = null,
            expectedRequestCompressionAlg = 0,
            expectedRequestDecompressionAlg = 0
        )
    }

    @Test
    fun `test no compression set - should not compress`() = runTest {
        testCompression(
            clientCompression = null,
            expectedEncoding = null,
            expectedRequestCompressionAlg = 0,
            expectedRequestDecompressionAlg = 0
        )
    }

    @Test
    fun `test unknown compression - should fail`() = assertGrpcFailure(
        StatusCode.INTERNAL,
        "Unable to find compressor by name unknownCompressionName"
    ) {
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                callOptions.compression = object : GrpcCompression {
                    override val name: String
                        get() = "unknownCompressionName"

                }
                proceed(it)
            }
        ) { client ->
            client.withService<EchoService>().UnaryEcho(EchoRequest.invoke { message = "Unknown compression" })
        }
    }

    private suspend fun testCompression(
        clientCompression: GrpcCompression?,
        expectedEncoding: String?,
        expectedRequestCompressionAlg: Int,
        expectedRequestDecompressionAlg: Int,
        expectedResponseCompressionAlg: Int = 0,
        expectedResponseDecompressionAlg: Int = 0
    ) {
        var reqHeaders = emptyMap<String, String>()
        var respHeaders = emptyMap<String, String>()
        val logs = captureNativeGrpcLogs {
            runGrpcTest(
                clientInterceptors = clientInterceptor {
                    clientCompression?.let { compression ->
                        callOptions.compression = compression
                    }
                    onHeaders { headers -> respHeaders = headers.toMap() }
                    proceed(it)
                },
                serverInterceptors = serverInterceptor {
                    reqHeaders = requestHeaders.toMap()
                    proceed(it)
                }
            ) {
                val message = "Echo with ${clientCompression?.name}"
                val response = it.withService<EchoService>().UnaryEcho(EchoRequest.invoke { this.message = message })

                // Verify the call succeeded and data is correct
                assertEquals(message, response.message)
            }
        }

        if (runtime == Runtime.NATIVE) {
            // if we are on native, we need to parse the logs manually to get the `grpc-` prefixed headers
            val traceHeaders = HeadersTrace.fromTrace(logs)
            reqHeaders = traceHeaders.requestHeaders
            respHeaders = traceHeaders.responseHeaders

            // verify that the client and server actually used the expected compression algorithm
            val compression = CompressionTrace.fromTrace(logs)
            assertEquals(expectedRequestCompressionAlg, compression.requestCompressionAlg)
            assertEquals(expectedRequestDecompressionAlg, compression.requestDecompressionAlg)
            assertEquals(expectedResponseCompressionAlg, compression.responseCompressionAlg)
            assertEquals(expectedResponseDecompressionAlg, compression.responseDecompressionAlg)
        }

        fun Map<String, String>.grpcAcceptEncoding() =
            this["grpc-accept-encoding"]?.split(",")?.map { it.trim() } ?: emptyList()

        // check request headers
        if (expectedEncoding != null) {
            assertEquals(expectedEncoding, reqHeaders["grpc-encoding"])
        }
        assertContainsAll(listOf("gzip"), reqHeaders.grpcAcceptEncoding())

        assertContainsAll(listOf("gzip"), respHeaders.grpcAcceptEncoding())
    }

    private suspend fun captureNativeGrpcLogs(block: suspend () -> Unit): String {
        try {
            return captureStdErr {
                setNativeEnv("GRPC_TRACE", "compression,http")
                block()
            }
        } finally {
            clearNativeEnv("GRPC_GRACE")
        }
    }

    private fun GrpcMetadata.toMap(): Map<String, String> {
        return keys().mapNotNull { key ->
            if (!key.endsWith("-bin")) {
                key to this@toMap[key]!!
            } else null
        }.toMap()
    }

    data class CompressionTrace(
        val requestCompressionAlg: Int,
        val requestDecompressionAlg: Int,
        val responseCompressionAlg: Int,
        val responseDecompressionAlg: Int
    ) {
        companion object {
            fun fromTrace(logs: String): CompressionTrace {
                val compressMessageRegex = Regex("""CompressMessage: len=\d+ alg=(\d+)""")
                val decompressMessageRegex = Regex("""DecompressMessage: len=\d+ max=\d+ alg=(\d+)""")

                val compressions = compressMessageRegex.findAll(logs).map { it.groupValues[1].toInt() }.toList()
                val decompressions = decompressMessageRegex.findAll(logs).map { it.groupValues[1].toInt() }.toList()

                require(compressions.size == 2) {
                    "Expected exactly 2 CompressMessage entries, but found ${compressions.size}"
                }
                require(decompressions.size == 2) {
                    "Expected exactly 2 DecompressMessage entries, but found ${decompressions.size}"
                }

                return CompressionTrace(
                    requestCompressionAlg = compressions[0],
                    requestDecompressionAlg = decompressions[0],
                    responseCompressionAlg = compressions[1],
                    responseDecompressionAlg = decompressions[1]
                )
            }
        }
    }

    data class HeadersTrace(
        val requestHeaders: Map<String, String>,
        val responseHeaders: Map<String, String>
    ) {
        companion object {
            fun fromTrace(logs: String): HeadersTrace {
                val metadataRegex = Regex(
                    """perform_stream_op\[.*SEND_INITIAL_METADATA\{([^}]+)\}""",
                    RegexOption.MULTILINE
                )

                val metadataBlocks = metadataRegex.findAll(logs).map { it.groupValues[1] }.toList()

                require(metadataBlocks.size == 2) {
                    "Expected exactly 2 SEND_INITIAL_METADATA entries, but found ${metadataBlocks.size}"
                }

                return HeadersTrace(
                    requestHeaders = parseHeaders(metadataBlocks[0]),
                    responseHeaders = parseHeaders(metadataBlocks[1])
                )
            }

            private fun parseHeaders(metadataBlock: String): Map<String, String> {
                val headers = mutableMapOf<String, String>()
                val headerRegex = Regex("""([^:,]+):\s*([^,]+(?:,\s*[^:,]+)*)(?=,\s+[^:,]+:|${'$'})""")

                for (match in headerRegex.findAll(metadataBlock)) {
                    val key = match.groupValues[1].trim()
                    val value = match.groupValues[2].trim()
                    headers[key] = value
                }

                return headers
            }
        }
    }
}