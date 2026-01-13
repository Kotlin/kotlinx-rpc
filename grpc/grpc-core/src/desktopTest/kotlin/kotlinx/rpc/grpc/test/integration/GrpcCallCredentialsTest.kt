/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.StatusCode
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
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.milliseconds

class GrpcCallCredentialsTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        return registerService<EchoService> { EchoServiceImpl() }
    }

    private fun assertAuthorizationHeaders(metadata: GrpcMetadata?, vararg expectedTokens: String) {
        assertNotNull(metadata, "Metadata should not be null")
        val authHeaders = metadata.getAll("authorization")
        assertNotNull(authHeaders, "Authorization headers should not be null")
        assertEquals(expectedTokens.size, authHeaders.size)
        expectedTokens.forEachIndexed { index, token ->
            assertEquals(token, authHeaders[index])
        }
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

        assertAuthorizationHeaders(grpcMetadata, "Bearer token")
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

        assertAuthorizationHeaders(grpcMetadata, "Bearer token-1", "Bearer token-2")
    }

    @Test
    fun `test combine three or more call credentials at config and interceptor time - should succeed`() {
        var grpcMetadata: GrpcMetadata? = null
        val configCallCreds = (NoTLSBearerTokenCredentials("token-1") + NoTLSBearerTokenCredentials("token-2") + NoTLSBearerTokenCredentials("token-3"))
        runGrpcTest(
            configure = {
                credentials = plaintext() + configCallCreds
            },
            clientInterceptors = clientInterceptor {
                callOptions.callCredentials += NoTLSBearerTokenCredentials("token-4")
                proceed(it)
            },
            serverInterceptors = serverInterceptor {
                grpcMetadata = requestHeaders
                proceed(it)
            },
            test = ::unaryCall
        )

        // 4 before 1 as callOption callCredentials are applied before client level ones
        assertAuthorizationHeaders(grpcMetadata, "Bearer token-4", "Bearer token-1", "Bearer token-2", "Bearer token-3")
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

        assertAuthorizationHeaders(grpcMetadata, "Bearer token")
    }

    @Test
    fun `test throw status exception - should fail with status`() {
        assertGrpcFailure(StatusCode.UNAVAILABLE, "This is my custom exception") {
            runGrpcTest(
                configure = {
                    credentials = plaintext() + ThrowingCallCredentials()
                },
                test = ::unaryCall
            )
        }
    }

    @Test
    fun `test throw exception - should fail`() {
        assertGrpcFailure(StatusCode.UNAVAILABLE, "This is my custom exception") {
            runGrpcTest(
                configure = {
                    credentials = plaintext() + ThrowingCallCredentials(IllegalStateException("This is my custom exception"))
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

        assertAuthorizationHeaders(grpcMetadata, "Bearer token")
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

    @Test
    fun `test call credentials cancellation because of timeout - should fail`() {
        var callCredsCancelled = false
        val slowCredentials = object : GrpcCallCredentials {
            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
                try {
                    // block indefinitely to simulate a slow call credential.
                    // this works even in a runTest coroutine dispatcher
                    CompletableDeferred<Unit>().await()
                    return buildGrpcMetadata {
                        append("Authentication", "Bearer token")
                    }
                } catch (err: CancellationException) {
                    callCredsCancelled = true
                    throw err
                }
            }

            override val requiresTransportSecurity: Boolean
                get() = false
        }
        assertGrpcFailure(StatusCode.DEADLINE_EXCEEDED) {
            runGrpcTest(
                configure = {
                    credentials = plaintext() + slowCredentials
                },
                clientInterceptors = clientInterceptor {
                    callOptions.timeout = 100.milliseconds
                    proceed(it)
                },
                test = ::unaryCall
            )
        }

        // assert that the getRequestMetadata suspend method was cancelled
        assertEquals(true, callCredsCancelled)
    }

    @Test
    fun `test call credentials should be called even if second fails - should fail`() {
        var calledCredentialHandler = false
        val someCredentials = object : PlaintextCallCredentials() {
            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
                calledCredentialHandler = true
                return buildGrpcMetadata { }
            }
        }
        assertGrpcFailure(StatusCode.UNAVAILABLE) {
            runGrpcTest(
                configure = {
                    credentials = plaintext() + someCredentials + ThrowingCallCredentials()
                },
                test = ::unaryCall
            )
        }

        // assert that the getRequestMetadata suspend method was cancelled
        assertEquals(true, calledCredentialHandler)
    }

    @Test
    fun `test call credentials should not be called if previous one fails - should fail`() {
        var calledCredentialHandler = false
        val someCredentials = object : PlaintextCallCredentials() {
            override suspend fun Context.getRequestMetadata(): GrpcMetadata {
                calledCredentialHandler = true
                return buildGrpcMetadata { }
            }
        }
        assertGrpcFailure(StatusCode.UNAVAILABLE) {
            runGrpcTest(
                configure = {
                    credentials = plaintext() + ThrowingCallCredentials() + someCredentials
                },
                test = ::unaryCall
            )
        }

        // assert that the getRequestMetadata suspend method was cancelled
        assertEquals(false, calledCredentialHandler)
    }
}

private suspend fun unaryCall(grpcClient: GrpcClient) {
    val service = grpcClient.withService<EchoService>()
    val response = service.UnaryEcho(EchoRequest { message = "Echo" })
    assertEquals("Echo", response.message)
}

abstract class PlaintextCallCredentials : GrpcCallCredentials {
    override val requiresTransportSecurity: Boolean
        get() = false
}

class ThrowingCallCredentials(
    private val exception: Throwable = StatusCode.UNIMPLEMENTED.asException("This is my custom exception")
) : PlaintextCallCredentials() {
    override suspend fun Context.getRequestMetadata(): GrpcMetadata {
        throw exception
    }
}

class NoTLSBearerTokenCredentials(
    val token: String = "token"
) : PlaintextCallCredentials() {
    override suspend fun Context.getRequestMetadata(): GrpcMetadata {
        return buildGrpcMetadata {
            // potentially fetching the token from a secure storage
            append("Authorization", "Bearer $token")
        }
    }
}

class TlsBearerTokenCredentials : GrpcCallCredentials {
    override suspend fun Context.getRequestMetadata(): GrpcMetadata {
        return buildGrpcMetadata {
            append("Authorization", "Bearer token")
        }
    }
}