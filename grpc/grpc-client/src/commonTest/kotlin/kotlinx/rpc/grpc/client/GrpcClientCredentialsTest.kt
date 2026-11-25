/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.getAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GrpcClientCredentialsTest {
    private val tls = GrpcTlsClientTransportCredentials()

    @Test
    fun transportCredentialsAlone() {
        val credentials: GrpcClientCredentials = tls

        assertSame(tls, credentials.transportCredentials)
        assertSame(GrpcEmptyCallCredentials, credentials.callCredentials)
    }

    @Test
    fun callCredentialsAlone() {
        val token = Token("a")
        val credentials: GrpcClientCredentials = token

        assertSame(GrpcDefaultClientTransportCredentials, credentials.transportCredentials)
        assertSame(token, credentials.callCredentials)
    }

    @Test
    fun transportWithCallCredentials() = runTest {
        val credentials = tls + Token("a")

        assertSame(tls, credentials.transportCredentials)
        assertEquals(listOf("a"), credentials.callCredentials.tokens())
    }

    @Test
    fun transportWithSeveralCallCredentials() = runTest {
        val credentials = tls + Token("a") + Token("b")

        assertSame(tls, credentials.transportCredentials)
        assertEquals(listOf("a", "b"), credentials.callCredentials.tokens())
    }

    @Test
    fun callCredentialsWithMoreCallCredentials() = runTest {
        val first: GrpcClientCredentials = Token("a")
        val credentials = first + Token("b")

        assertSame(GrpcDefaultClientTransportCredentials, credentials.transportCredentials)
        assertEquals(listOf("a", "b"), credentials.callCredentials.tokens())
    }

    @Test
    fun defaultCredentialsWithCallCredentials() = runTest {
        val credentials = GrpcDefaultClientTransportCredentials + Token("a")

        assertSame(GrpcDefaultClientTransportCredentials, credentials.transportCredentials)
        assertEquals(listOf("a"), credentials.callCredentials.tokens())
    }

    @Test
    fun defaultCredentialsOfConfiguration() {
        val credentials = GrpcClientConfiguration().credentials

        assertSame(GrpcDefaultClientTransportCredentials, credentials.transportCredentials)
        assertSame(GrpcEmptyCallCredentials, credentials.callCredentials)
    }

    private class Token(private val value: String) : GrpcCallCredentials {
        override suspend fun GrpcCallCredentials.Context.getRequestMetadata(): GrpcMetadata =
            GrpcMetadata { append("authorization", value) }
    }

    private suspend fun GrpcCallCredentials.tokens(): List<String> {
        val metadata = with(GrpcCallCredentials.Context("authority", "Service/method")) { getRequestMetadata() }
        return metadata.getAll("authorization")
    }
}
