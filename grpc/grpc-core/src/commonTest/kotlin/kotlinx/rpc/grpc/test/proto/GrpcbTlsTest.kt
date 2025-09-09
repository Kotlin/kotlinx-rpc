/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import hello.HelloRequest
import hello.HelloService
import hello.invoke
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.TlsChannelCredentials
import kotlinx.rpc.grpc.TlsClientAuth
import kotlinx.rpc.grpc.TlsServerCredentials
import kotlinx.rpc.grpc.test.CA_PEM
import kotlinx.rpc.grpc.test.CLIENT_CERT_PEM
import kotlinx.rpc.grpc.test.CLIENT_KEY_PEM
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.SERVER_CERT_PEM
import kotlinx.rpc.grpc.test.SERVER_KEY_PEM
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class GrpcbTlsTest : GrpcProtoTest() {

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun testDefaultTlsCall() = runTest {
        // uses default client TLS credentials
        val grpcClient = GrpcClient("grpcb.in", 9001)
        val service = grpcClient.withService<HelloService>()
        val request = HelloRequest {
            greeting = "world"
        }
        val result = service.SayHello(request)

        assertEquals("hello world", result.reply)

        // Ensure we don't leak the client channel between tests
        grpcClient.shutdown()
        grpcClient.awaitTermination()
    }


    @Test
    fun testCustomTls() {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM)
        val clientTls = TlsChannelCredentials { trustManager(SERVER_CERT_PEM) }

        runGrpcTest(serverTls, clientTls, overrideAuthority = "foo.test.google.fr") { client ->
            val service = client.withService<EchoService>()
            val request = EchoRequest { message = "Echo" }
            val response = service.UnaryEcho(request)
            assertEquals("Echo", response.message)
        }
    }

    @Test
    fun testCustomMTls() = runTest {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM) {
            trustManager(CA_PEM)
            clientAuth(TlsClientAuth.REQUIRE)
        }
        val clientTls = TlsChannelCredentials {
            keyManager(CLIENT_CERT_PEM, CLIENT_KEY_PEM)
            trustManager(CA_PEM)
        }

        runGrpcTest(serverTls, clientTls, overrideAuthority = "foo.test.google.fr") { client ->
            val service = client.withService<EchoService>()
            val request = EchoRequest { message = "Echo" }
            val response = service.UnaryEcho(request)
            assertEquals("Echo", response.message)
        }
    }

}