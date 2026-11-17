/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import io.grpc.ForwardingChannelBuilder2
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcConfigurationException
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientInterceptor
import kotlinx.rpc.grpc.client.plus
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.server.GrpcInsecureServerCredentials
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerConfiguration
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.assertGrpcFailure
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

class GrpcJavaBuilderTest {
    @Test
    fun `in-process builders - should complete a call without a port`() {
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
        ) { server, client ->
            assertEquals(-1, server.port)
            assertEquals("Echo", client.echo("Echo"))
        }
    }

    @Test
    fun `client builder limit without a configured limit - should stay`() {
        runBuilderTest(
            clientBuilder = { nettyChannelBuilder(it).maxInboundMessageSize(SMALL_LIMIT) },
        ) { _, client ->
            assertGrpcFailure(GrpcStatusCode.RESOURCE_EXHAUSTED) {
                runBlocking { client.echo(LARGE_MESSAGE) }
            }
        }
    }

    @Test
    fun `client builder limit with a configured limit - should be replaced`() {
        runBuilderTest(
            clientBuilder = { nettyChannelBuilder(it).maxInboundMessageSize(SMALL_LIMIT) },
            clientConfiguration = { maxInboundMessageSize = 4 * 1024 * 1024 },
        ) { _, client ->
            assertEquals(LARGE_MESSAGE, client.echo(LARGE_MESSAGE))
        }
    }

    @Test
    fun `server builder limit without a configured limit - should stay`() {
        runBuilderTest(
            serverBuilder = NettyServerBuilder.forPort(0).maxInboundMessageSize(SMALL_LIMIT),
        ) { _, client ->
            assertGrpcFailure(GrpcStatusCode.RESOURCE_EXHAUSTED) {
                runBlocking { client.echo(LARGE_MESSAGE) }
            }
        }
    }

    @Test
    fun `server builder limit with a configured limit - should be replaced`() {
        runBuilderTest(
            serverBuilder = NettyServerBuilder.forPort(0).maxInboundMessageSize(SMALL_LIMIT),
            serverConfiguration = { maxInboundMessageSize = 4 * 1024 * 1024 },
        ) { _, client ->
            assertEquals(LARGE_MESSAGE, client.echo(LARGE_MESSAGE))
        }
    }

    @Test
    fun `client credentials in the configuration - should fail`() {
        val error = assertFailsWith<GrpcConfigurationException> {
            GrpcClient(InProcessChannelBuilder.forName("unused")) {
                credentials = plaintext()
            }
        }
        assertContains(error.message.orEmpty(), "`credentials`")
    }

    @Test
    fun `server credentials in the configuration - should fail`() {
        val error = assertFailsWith<GrpcConfigurationException> {
            GrpcServer(InProcessServerBuilder.forName("unused")) {
                credentials = GrpcInsecureServerCredentials.create()
            }
        }
        assertContains(error.message.orEmpty(), "`credentials`")
    }

    @Test
    fun `client setting that the builder does not support - should fail`() {
        val error = assertFailsWith<GrpcUnsupportedSettingException> {
            GrpcClient(NoKeepAliveChannelBuilder(InProcessChannelBuilder.forName("unused"))) {
                keepAlive { time = 10.seconds }
            }
        }
        assertContains(error.message.orEmpty(), "`keepAlive`")
        assertIs<UnsupportedOperationException>(error.cause)
    }

    @Test
    fun `server setting that the builder does not support - should fail`() {
        val error = assertFailsWith<GrpcUnsupportedSettingException> {
            GrpcServer(InProcessServerBuilder.forName("unused")) {
                maxConnectionIdle = 10.seconds
            }
        }
        assertContains(error.message.orEmpty(), "`maxConnectionIdle`")
        assertIs<UnsupportedOperationException>(error.cause)
    }

    @Test
    fun `call credentials from a client interceptor - should reach the server`() {
        var serverHeaders: GrpcMetadata? = null
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
            serverConfiguration = {
                intercept(object : GrpcServerInterceptor {
                    override fun <Req, Resp> GrpcServerCallScope<Req, Resp>.intercept(
                        request: Flow<Req>,
                    ): Flow<Resp> {
                        serverHeaders = requestHeaders
                        return proceed(request)
                    }
                })
            },
            clientConfiguration = {
                intercept(object : GrpcClientInterceptor {
                    override fun <Req, Resp> GrpcClientCallScope<Req, Resp>.intercept(
                        request: Flow<Req>,
                    ): Flow<Resp> {
                        callOptions.callCredentials += NoTLSBearerTokenCredentials()
                        return proceed(request)
                    }
                })
            },
        ) { _, client ->
            client.echo("Echo")
        }

        assertEquals(listOf("Bearer token"), serverHeaders?.getAll("authorization"))
    }

    private fun runBuilderTest(
        serverBuilder: ServerBuilder<*> = NettyServerBuilder.forPort(0),
        clientBuilder: (GrpcServer) -> ManagedChannelBuilder<*> = ::nettyChannelBuilder,
        serverConfiguration: GrpcServerConfiguration.() -> Unit = {},
        clientConfiguration: GrpcClientConfiguration.() -> Unit = {},
        test: suspend (GrpcServer, GrpcClient) -> Unit,
    ) = runBlocking {
        val server = GrpcServer(serverBuilder) {
            services { registerService<EchoService> { EchoServiceImpl() } }
            serverConfiguration()
        }.start()
        val client = GrpcClient(clientBuilder(server), clientConfiguration)

        try {
            test(server, client)
        } finally {
            client.shutdownNow()
            server.shutdownNow()
            server.awaitTermination(30.seconds)
            client.awaitTermination(30.seconds)
        }
    }

    private suspend fun GrpcClient.echo(message: String): String =
        withService<EchoService>().unaryEcho(EchoRequest { this.message = message }).message

    private class NoKeepAliveChannelBuilder(
        private val delegate: ManagedChannelBuilder<*>,
    ) : ForwardingChannelBuilder2<NoKeepAliveChannelBuilder>() {
        override fun delegate(): ManagedChannelBuilder<*> = delegate

        override fun keepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit): NoKeepAliveChannelBuilder =
            throw UnsupportedOperationException()
    }

    companion object {
        private const val SMALL_LIMIT = 128
        private val LARGE_MESSAGE = "x".repeat(1024)

        private fun nettyChannelBuilder(server: GrpcServer): NettyChannelBuilder =
            NettyChannelBuilder.forAddress("localhost", server.port).usePlaintext()
    }
}
