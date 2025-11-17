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
import kotlinx.rpc.grpc.buildGrpcMetadata
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallCredentials.Context
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcTlsClientCredentials
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
        assertGrpcFailure(StatusCode.UNAUTHENTICATED, "Established channel does not have a sufficient security level to transfer call credential.") {
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
        val clientTls = GrpcTlsClientCredentials { trustManager(SERVER_CERT_PEM) }
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
            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
                throw StatusException(Status(StatusCode.UNIMPLEMENTED, "This is my custom exception"))
            }

            override val requiresTransportSecurity: Boolean
                get() = false
        }

        assertGrpcFailure(StatusCode.UNAVAILABLE, "This is my custom exception") {
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
    fun `test throw exception - should fail`() {
        val throwingCallCredentials = object : GrpcCallCredentials {
            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
                throw IllegalStateException("This is my custom exception")
            }
            override val requiresTransportSecurity: Boolean
                get() = false
        }
        assertGrpcFailure(StatusCode.UNAVAILABLE, "This is my custom exception") {
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
        assertGrpcFailure(StatusCode.UNAUTHENTICATED, "Established channel does not have a sufficient security level to transfer call credential.") {
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                callOptions.callCredentials += TlsBearerTokenCredentials()
                proceed(it)
            },
            test = ::unaryCall
        )}
    }

    @Test
    fun `test context contains correct method descriptor - should succeed`() {
        var capturedMethod: String? = null

        val contextCapturingCredentials = object : GrpcCallCredentials {
            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
                capturedMethod = methodName
                return GrpcMetadata()
            }

            override val requiresTransportSecurity: Boolean = false
        }

        runGrpcTest(
            configure = {
                credentials = plaintext() + contextCapturingCredentials
            },
            test = ::unaryCall
        )

        assertEquals("kotlinx.rpc.grpc.test.EchoService/UnaryEcho", capturedMethod)
    }

    @Test
    fun `test context contains correct authority - should succeed`() {
        var capturedAuthority: String? = null

        val contextCapturingCredentials = object : GrpcCallCredentials {
            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
                capturedAuthority = authority
                return GrpcMetadata()
            }

            override val requiresTransportSecurity: Boolean = false
        }

        runGrpcTest(
            configure = {
                credentials = plaintext() + contextCapturingCredentials
                overrideAuthority = "test.example.com"
            },
            test = ::unaryCall
        )

        assertEquals("test.example.com", capturedAuthority)
    }

//    @Test
//    fun `test long running call credentials - should succeed`() {
//        var grpcMetadata: GrpcMetadata? = null
//        class SlowCredentials(
//            val token: String
//        ) : GrpcCallCredentials {
//            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
//                delay(1000)
//                return buildGrpcMetadata {
//                    append(token, token)
//                }
//            }
//
//            override val requiresTransportSecurity: Boolean
//                get() = false
//        }
//
//        runGrpcTest(
//            configure = {
//                credentials = plaintext() + SlowCredentials("token-1")
//            },
//            clientInterceptors = clientInterceptor {
//                callOptions.callCredentials += SlowCredentials("token-2")
//                proceed(it)
//            },
//            serverInterceptors = serverInterceptor {
//                grpcMetadata = requestHeaders
//                proceed(it)
//            },
//            test = {
//                coroutineScope {
//                    launch { unaryCall(it) }
//                    delay(200)
//                    cancel("Midcanceling")
//                }
//            }
//        )
//
//        val authHeaders = grpcMetadata?.getAll("token-1")
//        assertEquals(1, authHeaders?.size)
//        assertEquals("token-1", authHeaders?.single())
//        val authHeaders2 = grpcMetadata?.getAll("token-2")
//        assertEquals(1, authHeaders2?.size)
//        assertEquals("token-2", authHeaders2?.single())
//    }
}

private suspend fun unaryCall(grpcClient: GrpcClient) {
    val service = grpcClient.withService<EchoService>()
    val response = service.UnaryEcho(EchoRequest { message = "Echo" })
    assertEquals("Echo", response.message)
}

class NoTLSBearerTokenCredentials(
    val token: String = "token"
): GrpcCallCredentials {
    override suspend fun Context.getRequestMetadata(): GrpcMetadata {
        return buildGrpcMetadata {
            // potentially fetching the token from a secure storage
            append("Authorization", "Bearer $token")
        }
    }

    override val requiresTransportSecurity: Boolean
        get() = false
}

class TlsBearerTokenCredentials: GrpcCallCredentials {
    override suspend fun Context.getRequestMetadata(): GrpcMetadata {
        return buildGrpcMetadata {
            append("Authorization", "Bearer token")
        }
    }
}