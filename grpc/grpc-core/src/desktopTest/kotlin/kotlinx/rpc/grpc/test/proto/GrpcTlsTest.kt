/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import hello.HelloRequest
import hello.HelloService
import hello.invoke
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.server.TlsClientAuth
import kotlinx.rpc.grpc.client.GrpcTlsClientCredentials
import kotlinx.rpc.grpc.server.TlsServerCredentials
import kotlinx.rpc.grpc.test.CA_PEM
import kotlinx.rpc.grpc.test.CLIENT_CERT_PEM
import kotlinx.rpc.grpc.test.CLIENT_KEY_PEM
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.SERVER_CERT_PEM
import kotlinx.rpc.grpc.test.SERVER_KEY_PEM
import kotlinx.rpc.grpc.test.assertGrpcFailure
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class GrpcTlsTest : GrpcProtoTest() {

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `test client side TLS with default credentials - should succeed`() = runTest {
        // uses default client TLS credentials
        // TODO: Use a test server controlled by us (KRPC-215)
        val grpcClient = GrpcClient("grpcb.in", 9001)
        val service = grpcClient.withService<HelloService>()
        val request = HelloRequest {
            greeting = "world"
        }
        val result = service.SayHello(request)

        assertEquals("hello world", result.reply)

        grpcClient.shutdown()
        grpcClient.awaitTermination()
    }

    @Test
    fun `test TLS with valid certificates - should succeed`() {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM)
        val clientTls = GrpcTlsClientCredentials { trustManager(SERVER_CERT_PEM) }

        runGrpcTest(serverTls, clientTls, overrideAuthority = "foo.test.google.fr", test = ::defaultUnaryTest)
    }

    @Test
    fun `test mTLS with valid certificates - should succeed`() = runTest {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM) {
            trustManager(CA_PEM)
            clientAuth(TlsClientAuth.REQUIRE)
        }
        val clientTls = GrpcTlsClientCredentials {
            keyManager(CLIENT_CERT_PEM, CLIENT_KEY_PEM)
            trustManager(CA_PEM)
        }

        runGrpcTest(serverTls, clientTls, overrideAuthority = "foo.test.google.fr", test = ::defaultUnaryTest)
    }

    @Test
    fun `test mTLS with clientAuth optional - should succeed`() = runTest {
        // the server uses a trustManager that does not know about the client certificate,
        // so the client can authentication cannot be verified.
        // but as the clientAuth is optional, the connection will succeed.
        val caCertWithoutClient = SERVER_CERT_PEM
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM) {
            trustManager(caCertWithoutClient)
            // clientAuth is optional, so a client without a certificate can connect
            clientAuth(TlsClientAuth.OPTIONAL)
        }
        val clientTls = GrpcTlsClientCredentials {
            keyManager(CLIENT_CERT_PEM, CLIENT_KEY_PEM)
            trustManager(CA_PEM)
        }

        runGrpcTest(serverTls, clientTls, overrideAuthority = "foo.test.google.fr", test = ::defaultUnaryTest)
    }

    @Test
    fun `test mTLS with clientAuth required - should fail`() = runTest {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM) {
            trustManager(CA_PEM)
            // client must authenticate
            clientAuth(TlsClientAuth.REQUIRE)
        }
        // client does NOT provide keyManager, only trusts CA
        val clientTls = GrpcTlsClientCredentials {
            trustManager(CA_PEM)
        }

        assertGrpcFailure(StatusCode.UNAVAILABLE) {
            runGrpcTest(serverTls, clientTls, overrideAuthority = "foo.test.google.fr", test = ::defaultUnaryTest)
        }
    }

    @Test
    fun `test TLS with no client trustManager - should fail`() = runTest {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM)
        // client credential doesn't contain a trustManager, so server authentication will fail
        val clientTls = GrpcTlsClientCredentials {}
        assertGrpcFailure(StatusCode.UNAVAILABLE) {
            runGrpcTest(serverTls, clientTls, overrideAuthority = "foo.test.google.fr", test = ::defaultUnaryTest)
        }
    }

    @Test
    fun `test TLS with invalid authority - should fail`() = runTest {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM)
        val clientTls = GrpcTlsClientCredentials { trustManager(CA_PEM) }
        // the authority does not match the certificate
        assertGrpcFailure(StatusCode.UNAVAILABLE) {
            runGrpcTest(serverTls, clientTls, overrideAuthority = "invalid.host.name", test = ::defaultUnaryTest)
        }
    }

    @Test
    fun `test TLS server with plaintext client - should fail`() = runTest {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM)
        assertGrpcFailure(StatusCode.UNAVAILABLE) {
            runGrpcTest(serverCreds = serverTls, overrideAuthority = "foo.test.google.fr", test = ::defaultUnaryTest)
        }
    }

    @Test
    fun `test TLS client with plaintext server - should fail`() = runTest {
        val clientTls = GrpcTlsClientCredentials { trustManager(CA_PEM) }
        assertGrpcFailure(StatusCode.UNAVAILABLE) {
            runGrpcTest(clientCreds = clientTls, overrideAuthority = "foo.test.google.fr", test = ::defaultUnaryTest)
        }
    }

}

private suspend fun defaultUnaryTest(client: GrpcClient) {
    val service = client.withService<EchoService>()
    val request = EchoRequest { message = "Echo" }
    val response = service.UnaryEcho(request)
    assertEquals("Echo", response.message)
}