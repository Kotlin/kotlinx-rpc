/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcCompression
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class GrpcCompressionTest : GrpcProtoTest() {
    override fun RpcServer.registerServices() {
        return registerService<EchoService> { EchoServiceImpl() }
    }


    @Test
    fun `test gzip client compression - should succeed`() = runTest {
        var requestCompression: String? = null
        var requestAccept: String? = null
        val responseCompression: CompletableDeferred<String?> = CompletableDeferred()
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                callOptions.compression = GrpcCompression.Gzip
                onHeaders { headers ->
                    println(headers)
                    responseCompression.complete(headers["grpc-encoding"])
                }
                proceed(it)
            },
            serverInterceptors = serverInterceptor {
                println(requestHeaders["grpc-encoding"])
                requestCompression = requestHeaders["grpc-encoding"]
                requestAccept = requestHeaders["grpc-accept-encoding"]
                println(requestHeaders)
                proceed(it)
            }
        ) {
            val response = it.withService<EchoService>().UnaryEcho(EchoRequest.invoke { message = "Echo" })
            assertEquals("Echo", response.message)
        }

        val responseCompressionValue = responseCompression.await()
        assertEquals("gzip", requestCompression)
        assertEquals("gzip", requestAccept)
        // the server does not automatically use compression for responses
        assertEquals("identity", responseCompressionValue)
    }


}