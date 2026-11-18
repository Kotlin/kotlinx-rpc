/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import io.grpc.ForwardingChannelBuilder2
import io.grpc.ForwardingServerBuilder
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerProvider
import io.grpc.ServerRegistry
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.inprocess.InProcessSocketAddress
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcClientChannelBuilderConfiguration
import kotlinx.rpc.grpc.client.GrpcTlsClientCredentials
import kotlinx.rpc.grpc.client.callCredentials
import kotlinx.rpc.grpc.client.plus
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.GrpcServerBuilderConfiguration
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.listenAddresses
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.assertGrpcFailure
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import java.net.InetSocketAddress
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
    fun `listenAddresses of an in-process server - should have the in-process address`() {
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
        ) { server, _ ->
            val address = assertIs<InProcessSocketAddress>(server.listenAddresses.single())
            assertEquals(name, address.name)
        }
    }

    @Test
    fun `listenAddresses of a server on a port - should have the port`() {
        runBuilderTest { server, _ ->
            val address = assertIs<InetSocketAddress>(server.listenAddresses.single())
            assertEquals(server.port, address.port)
        }
    }

    @Test
    fun `listenAddresses before start and after termination - should be empty`() = runBlocking {
        val server = GrpcServer(InProcessServerBuilder.forName(InProcessServerBuilder.generateName()))
        assertEquals(emptyList(), server.listenAddresses)

        server.start()
        assertEquals(1, server.listenAddresses.size)

        server.shutdownNow()
        server.awaitTermination(30.seconds)
        assertEquals(emptyList(), server.listenAddresses)
    }

    @Test
    fun `listenAddresses of a server that does not report them - should be empty`() {
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = NoListenSocketsServerBuilder(InProcessServerBuilder.forName(name)),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
        ) { server, client ->
            assertEquals(emptyList(), server.listenAddresses)
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
    fun `server builder limit with a cleared limit - should stay`() {
        runBuilderTest(
            serverBuilder = NettyServerBuilder.forPort(0).maxInboundMessageSize(SMALL_LIMIT),
            serverConfiguration = {
                maxInboundMessageSize = 4 * 1024 * 1024
                clearMaxInboundMessageSize()
            },
        ) { _, client ->
            assertGrpcFailure(GrpcStatusCode.RESOURCE_EXHAUSTED) {
                runBlocking { client.echo(LARGE_MESSAGE) }
            }
        }
    }

    @Test
    fun `client setting that the builder does not support - should fail`() {
        // The exception is also an UnsupportedOperationException, the type that the builder throws.
        val error = assertFailsWith<UnsupportedOperationException> {
            GrpcClient(NoKeepAliveChannelBuilder(InProcessChannelBuilder.forName("unused"))) {
                keepAlive { time = 10.seconds }
            }
        }
        assertIs<GrpcUnsupportedSettingException>(error)
        assertContains(error.message.orEmpty(), "`keepAlive`")
        assertContains(error.message.orEmpty(), "Remove `keepAlive`")
        assertIs<UnsupportedOperationException>(error.cause)
    }

    @Test
    fun `server setting that the builder does not support - should fail`() {
        val error = assertFailsWith<GrpcUnsupportedSettingException> {
            GrpcServer(InProcessServerBuilder.forName("unused")) {
                maxConnectionIdle = 10.seconds
            }
        }
        assertContains(error.message.orEmpty(), "Remove `maxConnectionIdle`")
        assertIs<UnsupportedOperationException>(error.cause)
    }

    @Test
    fun `port factory default that the server provider does not support - should be skipped`() = runBlocking {
        val name = InProcessServerBuilder.generateName()
        // `InProcessServerBuilder` rejects `maxConnectionIdle`, and the port factory applies its default.
        withInProcessServerProvider(name) {
            val server = GrpcServer(0) {
                services { registerService<EchoService> { EchoServiceImpl() } }
            }.start()
            var client: GrpcClient? = null

            try {
                client = GrpcClient(InProcessChannelBuilder.forName(name))
                assertEquals("Echo", client.echo("Echo"))
            } finally {
                client?.shutdownNow()
                server.shutdownNow()
                server.awaitTermination(30.seconds)
                client?.awaitTermination(30.seconds)
            }
        }
    }

    @Test
    fun `port factory setting that the server provider does not support - should fail`() {
        withInProcessServerProvider("unused") {
            val error = assertFailsWith<UnsupportedOperationException> {
                GrpcServer(0) { maxConnectionIdle = 10.seconds }
            }
            assertIs<GrpcUnsupportedSettingException>(error)
            assertContains(error.message.orEmpty(), "Remove `maxConnectionIdle`")
        }
    }

    @Test
    fun `call credentials that require transport security on a plaintext channel - should fail`() {
        runBuilderTest(
            clientConfiguration = { callCredentials = TlsBearerTokenCredentials() },
        ) { _, client ->
            assertGrpcFailure(GrpcStatusCode.UNAUTHENTICATED) {
                runBlocking { client.echo("Echo") }
            }
        }
    }

    @Test
    fun `call credentials of a combined value - should reach the server`() {
        val shared = GrpcTlsClientCredentials() + NoTLSBearerTokenCredentials()
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
                callCredentials = shared.callCredentials
            },
        ) { _, client ->
            client.echo("Echo")
        }

        assertEquals(listOf("Bearer token"), serverHeaders?.getAll("authorization"))
    }

    private fun runBuilderTest(
        serverBuilder: ServerBuilder<*> = NettyServerBuilder.forPort(0),
        clientBuilder: (GrpcServer) -> ManagedChannelBuilder<*> = ::nettyChannelBuilder,
        serverConfiguration: GrpcServerBuilderConfiguration.() -> Unit = {},
        clientConfiguration: GrpcClientChannelBuilderConfiguration.() -> Unit = {},
        test: suspend (GrpcServer, GrpcClient) -> Unit,
    ) = runBlocking {
        val server = GrpcServer(serverBuilder) {
            services { registerService<EchoService> { EchoServiceImpl() } }
            serverConfiguration()
        }.start()
        var client: GrpcClient? = null

        try {
            client = GrpcClient(clientBuilder(server), clientConfiguration)
            test(server, client)
        } finally {
            client?.shutdownNow()
            server.shutdownNow()
            server.awaitTermination(30.seconds)
            client?.awaitTermination(30.seconds)
        }
    }

    private suspend fun GrpcClient.echo(message: String): String =
        withService<EchoService>().unaryEcho(EchoRequest { this.message = message }).message

    /**
     * Runs [block] while grpc-java makes an in-process builder with the name [name] for every port.
     */
    private inline fun withInProcessServerProvider(name: String, block: () -> Unit) {
        val provider = object : ServerProvider() {
            override fun isAvailable(): Boolean = true

            // Above the priority of the Netty provider.
            override fun priority(): Int = 10

            override fun builderForPort(port: Int): ServerBuilder<*> = InProcessServerBuilder.forName(name)
        }
        val registry = ServerRegistry.getDefaultRegistry()
        registry.register(provider)

        try {
            block()
        } finally {
            registry.deregister(provider)
        }
    }

    private class NoKeepAliveChannelBuilder(
        private val delegate: ManagedChannelBuilder<*>,
    ) : ForwardingChannelBuilder2<NoKeepAliveChannelBuilder>() {
        override fun delegate(): ManagedChannelBuilder<*> = delegate

        override fun keepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit): NoKeepAliveChannelBuilder =
            throw UnsupportedOperationException()
    }

    private class NoListenSocketsServerBuilder(
        private val delegate: ServerBuilder<*>,
    ) : ForwardingServerBuilder<NoListenSocketsServerBuilder>() {
        override fun delegate(): ServerBuilder<*> = delegate

        override fun build(): Server = NoListenSocketsServer(delegate.build())
    }

    /**
     * Doesn't override `getListenSockets` of [Server]. The default throws [UnsupportedOperationException].
     */
    private class NoListenSocketsServer(private val delegate: Server) : Server() {
        override fun start(): Server = apply { delegate.start() }
        override fun shutdown(): Server = apply { delegate.shutdown() }
        override fun shutdownNow(): Server = apply { delegate.shutdownNow() }
        override fun isShutdown(): Boolean = delegate.isShutdown
        override fun isTerminated(): Boolean = delegate.isTerminated
        override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = delegate.awaitTermination(timeout, unit)
        override fun awaitTermination() = delegate.awaitTermination()
    }

    companion object {
        private const val SMALL_LIMIT = 128
        private val LARGE_MESSAGE = "x".repeat(1024)

        private fun nettyChannelBuilder(server: GrpcServer): NettyChannelBuilder =
            NettyChannelBuilder.forAddress("localhost", server.port).usePlaintext()
    }
}
