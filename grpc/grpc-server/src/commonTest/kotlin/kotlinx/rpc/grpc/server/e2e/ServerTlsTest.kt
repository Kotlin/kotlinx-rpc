/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.SimpleRequest
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.GrpcTlsClientCredentials
import kotlinx.rpc.grpc.server.GrpcTlsClientAuth
import kotlinx.rpc.grpc.server.GrpcTlsServerCredentials
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.zeros
import kotlinx.rpc.test.tls.CA_PEM
import kotlinx.rpc.test.tls.CLIENT_CERT_PEM
import kotlinx.rpc.test.tls.CLIENT_KEY_PEM
import kotlinx.rpc.test.tls.SERVER_CERT_PEM
import kotlinx.rpc.test.tls.SERVER_KEY_PEM
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerTlsTest {
    @Test
    fun tlsClientReceivesAResponse() = grpcServerTest {
        start(
            serverConfig = { credentials = GrpcTlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM) },
            clientConfig = {
                credentials = GrpcTlsClientCredentials { trustManager(CA_PEM) }
                overrideAuthority = "foo.test.google.fr"
            },
        )

        val response = testService.unaryCall(SimpleRequest { responseSize = 7 })

        assertEquals(zeros(7), response.payload)
    }

    @Test
    fun mutualTlsAcceptsAClientCertificate() = grpcServerTest {
        start(
            serverConfig = {
                credentials = GrpcTlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM) {
                    trustManager(CA_PEM)
                    clientAuth(GrpcTlsClientAuth.REQUIRE)
                }
            },
            clientConfig = {
                credentials = GrpcTlsClientCredentials {
                    trustManager(CA_PEM)
                    keyManager(CLIENT_CERT_PEM, CLIENT_KEY_PEM)
                }
                overrideAuthority = "foo.test.google.fr"
            },
        )

        val response = testService.unaryCall(SimpleRequest { responseSize = 7 })

        assertEquals(zeros(7), response.payload)
    }

    @Test
    fun mutualTlsRejectsAClientWithoutACertificate() = grpcServerTest {
        start(
            serverConfig = {
                credentials = GrpcTlsServerCredentials(SERVER_CERT_PEM, SERVER_KEY_PEM) {
                    trustManager(CA_PEM)
                    clientAuth(GrpcTlsClientAuth.REQUIRE)
                }
            },
            clientConfig = {
                credentials = GrpcTlsClientCredentials { trustManager(CA_PEM) }
                overrideAuthority = "foo.test.google.fr"
            },
        )

        assertGrpcStatus(GrpcStatusCode.UNAVAILABLE) { testService.unaryCall(SimpleRequest {}) }

        assertEquals(emptyList(), observer.calls)
    }
}
