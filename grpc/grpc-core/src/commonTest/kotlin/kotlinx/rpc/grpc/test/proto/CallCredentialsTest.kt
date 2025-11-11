/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.TlsClientCredentials
import kotlinx.rpc.grpc.client.plus
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.server.TlsServerCredentials
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

class GrpcCallCredentialsTest : GrpcProtoTest() {
    override fun RpcServer.registerServices() {
        return registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `test simple combined call credentials - should succeed`() {
        var grpcMetadata: GrpcMetadata? = null
        runGrpcTest(
            configure = {
                credentials = plaintext() + NoTLSBearerTokenCredentials()
            },
            serverInterceptors = serverInterceptor {
                grpcMetadata = requestHeaders
                proceed(it)
            },
            test = ::unaryCall
        )

        val authHeaders = grpcMetadata?.getAll("authorization")
        assertEquals(1, authHeaders?.size)
        assertEquals("Bearer token", authHeaders?.single())
    }

    @Test
    fun `test combine multiple call credentials - should succeed`() {
        var grpcMetadata: GrpcMetadata? = null
        val callCreds = (NoTLSBearerTokenCredentials("token-1") + NoTLSBearerTokenCredentials("token-2"))
        runGrpcTest(
            configure = {
                credentials = plaintext() + callCreds
            },
            serverInterceptors = serverInterceptor {
                grpcMetadata = requestHeaders
                proceed(it)
            },
            test = ::unaryCall
        )
        val authHeaders = grpcMetadata?.getAll("authorization")
        assertEquals(2, authHeaders?.size)
        assertEquals("Bearer token-1", authHeaders?.first())
        assertEquals("Bearer token-2", authHeaders?.get(1))
    }

    @Test
    fun `test plaintext call credentials - should fail`() {
        assertGrpcFailure(StatusCode.UNAUTHENTICATED, "Transport security required but not present") {
            runGrpcTest(
                configure = {
                    credentials = plaintext() + TlsBearerTokenCredentials()
                },
                test = ::unaryCall
            )
        }
    }

    @Test
    fun `test tls call credentials - should succeed`() {
        val serverTls = TlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM)
        val clientTls = TlsClientCredentials { trustManager(SERVER_CERT_PEM) }
        val clientCombined = clientTls + TlsBearerTokenCredentials()

        var grpcMetadata: GrpcMetadata? = null
        runGrpcTest(
            configure = {
                credentials = clientCombined
                overrideAuthority = "foo.test.google.fr"
            },
            serverCreds = serverTls,
            serverInterceptors = serverInterceptor {
                grpcMetadata = requestHeaders
                proceed(it)
            },
            test = ::unaryCall
        )

        val authHeaders = grpcMetadata?.getAll("authorization")
        assertEquals(1, authHeaders?.size)
        assertEquals("Bearer token", authHeaders?.single())
    }

    @Test
    fun `test throw status exception - should fail with status`() {
        val throwingCallCredentials = object : GrpcCallCredentials {
            override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
                throw StatusException(Status(StatusCode.UNIMPLEMENTED, "This is my custom exception"))
            }

            override val requiresTransportSecurity: Boolean
                get() = false
        }

        assertGrpcFailure(StatusCode.UNIMPLEMENTED, "This is my custom exception") {
            runGrpcTest(
                configure = {
                    credentials = plaintext() + throwingCallCredentials
                },
                serverInterceptors = serverInterceptor {
                    proceed(it)
                },
                test = ::unaryCall
            )
        }
    }

    @Test
    fun `test interceptor call credentials - should succeed`() {
        var grpcMetadata: GrpcMetadata? = null
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                callOptions.callCredentials += NoTLSBearerTokenCredentials()
                proceed(it)
            },
            serverInterceptors = serverInterceptor {
                grpcMetadata = requestHeaders
                proceed(it)
            },
            test = ::unaryCall
        )
        val authHeaders = grpcMetadata?.getAll("authorization")
        assertEquals(1, authHeaders?.size)
        assertEquals("Bearer token", authHeaders?.single())
    }

    @Test
    fun `test interceptor call credentials without TLS - should fail`() {
        assertGrpcFailure(StatusCode.UNAUTHENTICATED, "Transport security required but not present") {
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                callOptions.callCredentials += TlsBearerTokenCredentials()
                proceed(it)
            },
            test = ::unaryCall
        )}
    }
}

private suspend fun unaryCall(grpcClient: GrpcClient) {
    val service = grpcClient.withService<EchoService>()
    val response = service.UnaryEcho(EchoRequest { message = "Echo" })
    assertEquals("Echo", response.message)
}

class NoTLSBearerTokenCredentials(
    val token: String = "token"
): GrpcCallCredentials {
    override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
        // potentially fetching the token from a secure storage
        append("Authorization", "Bearer $token")
    }

    override val requiresTransportSecurity: Boolean
        get() = false
}

class TlsBearerTokenCredentials: GrpcCallCredentials {
    override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
        append("Authorization", "Bearer token")
    }
}