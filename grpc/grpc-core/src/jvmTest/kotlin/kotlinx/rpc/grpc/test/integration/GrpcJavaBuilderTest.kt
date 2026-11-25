/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import io.grpc.CallCredentials
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.CompositeChannelCredentials
import io.grpc.ForwardingChannelBuilder2
import io.grpc.ForwardingServerBuilder
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import io.grpc.ManagedChannelBuilder
import io.grpc.ManagedChannelProvider
import io.grpc.ManagedChannelRegistry
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.ServerProvider
import io.grpc.ServerRegistry
import io.grpc.ServerServiceDefinition
import io.grpc.Status
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.inprocess.InProcessSocketAddress
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.ClientCalls
import io.grpc.stub.ServerCalls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcClientChannelBuilderConfiguration
import kotlinx.rpc.grpc.client.GrpcClientInterceptor
import kotlinx.rpc.grpc.client.GrpcTlsClientTransportCredentials
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
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
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
    fun `port factory with credentials - should skip a server provider without credentials support`() = runBlocking {
        withInProcessServerProvider("unused") {
            val server = GrpcServer(0) { credentials = plaintext() }.start()

            try {
                assertIs<InetSocketAddress>(server.listenAddresses.single())
            } finally {
                server.shutdownNow()
                server.awaitTermination(30.seconds)
            }
        }
    }

    @Test
    fun `address factory without transport credentials - should use a channel provider without credentials support`() {
        val name = InProcessServerBuilder.generateName()
        runAddressFactoryTest(InProcessServerBuilder.forName(name), providerChannelName = name) {
            GrpcClient("unused", 1)
        }
    }

    @Test
    fun `address factory with call credentials only - should use a channel provider without credentials support`() {
        val name = InProcessServerBuilder.generateName()
        runAddressFactoryTest(InProcessServerBuilder.forName(name), providerChannelName = name) {
            GrpcClient("unused", 1) { credentials = NoTLSBearerTokenCredentials() }
        }
    }

    @Test
    fun `address factory with transport credentials - should skip a channel provider without credentials support`() {
        runAddressFactoryTest(NettyServerBuilder.forPort(0), providerChannelName = "unused") { server ->
            GrpcClient("localhost", server.port) { credentials = plaintext() }
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
        val shared = GrpcTlsClientTransportCredentials() + NoTLSBearerTokenCredentials()
        val server = RequestHeadersInterceptor()
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
            serverConfiguration = { intercept(server) },
            clientConfiguration = {
                callCredentials = shared.callCredentials
            },
        ) { _, client ->
            client.echo("Echo")
        }

        assertEquals(listOf("Bearer token"), server.requestHeaders?.getAll("authorization"))
    }

    @Test
    fun `client builder call credentials with configured call credentials - should both reach the server`() {
        val server = RequestHeadersInterceptor()
        runBuilderTest(
            clientBuilder = {
                val credentials = CompositeChannelCredentials.create(
                    InsecureChannelCredentials.create(),
                    HeaderCallCredentials("x-builder", "builder"),
                )
                Grpc.newChannelBuilderForAddress("localhost", it.port, credentials)
            },
            serverConfiguration = { intercept(server) },
            clientConfiguration = { callCredentials = NoTLSBearerTokenCredentials() },
        ) { _, client ->
            client.echo("Echo")
        }

        assertEquals(listOf("builder"), server.requestHeaders?.getAll("x-builder"))
        assertEquals(listOf("Bearer token"), server.requestHeaders?.getAll("authorization"))
    }

    @Test
    fun `client interceptors of the configuration and of the builder - should run in this order`() {
        val interceptors = CopyOnWriteArrayList<String>()
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name),
            clientBuilder = {
                InProcessChannelBuilder.forName(name).intercept(RecordingJavaClientInterceptor(interceptors))
            },
            clientConfiguration = { intercept(RecordingClientInterceptor(interceptors)) },
        ) { _, client ->
            client.echo("Echo")
        }

        assertEquals(listOf(CONFIGURATION, BUILDER), interceptors)
    }

    @Test
    fun `client keepAlive with one value - should replace all keep-alive values of the builder`() {
        val builder = KeepAliveRecordingChannelBuilder(InProcessChannelBuilder.forName("unused"))
            .keepAliveTimeout(5, TimeUnit.SECONDS)
            .keepAliveWithoutCalls(true)

        GrpcClient(builder) {
            keepAlive { time = 30.seconds }
        }.shutdownNow()

        assertEquals(30.seconds, builder.time)
        assertEquals(20.seconds, builder.timeout)
        assertEquals(false, builder.withoutCalls)
    }

    @Test
    fun `server keepAlive with one value - should replace both keepalive values of the builder`() {
        val builder = KeepAliveRecordingServerBuilder(InProcessServerBuilder.forName("unused"))
            .keepAliveTimeout(5, TimeUnit.SECONDS)

        GrpcServer(builder) {
            keepAlive { time = 30.seconds }
        }.shutdownNow()

        assertEquals(30.seconds, builder.time)
        assertEquals(20.seconds, builder.timeout)
    }

    @Test
    fun `server builder service with configured services - should stay`() {
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name).addService(grpcJavaService(BUILDER_METHOD)),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
        ) { _, client ->
            callGrpcJavaService(name, BUILDER_METHOD)
            assertEquals("Echo", client.echo("Echo"))
        }
    }

    @Test
    fun `server builder service with the name of a configured service - should be replaced`() {
        var calledMethod: String? = null
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name)
                .addService(grpcJavaService(ECHO_METHOD, Status.DATA_LOSS)),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
            clientConfiguration = {
                intercept(object : GrpcClientInterceptor {
                    override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                        request: Flow<Request>,
                    ): Flow<Response> {
                        calledMethod = method.getFullMethodName()
                        return proceed(request)
                    }
                })
            },
        ) { _, client ->
            assertEquals("Echo", client.echo("Echo"))
        }

        assertEquals(ECHO_METHOD.fullMethodName, calledMethod)
    }

    @Test
    fun `server interceptors of the builder and of the configuration - should run in this order`() {
        val interceptors = CopyOnWriteArrayList<String>()
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name)
                .intercept(RecordingJavaServerInterceptor(interceptors)),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
            serverConfiguration = { intercept(RecordingServerInterceptor(interceptors)) },
        ) { _, client ->
            client.echo("Echo")
        }

        assertEquals(listOf(BUILDER, CONFIGURATION), interceptors)
    }

    @Test
    fun `server interceptor of the configuration on a service of the builder - should not run`() {
        val interceptors = CopyOnWriteArrayList<String>()
        val name = InProcessServerBuilder.generateName()
        runBuilderTest(
            serverBuilder = InProcessServerBuilder.forName(name)
                .addService(grpcJavaService(BUILDER_METHOD))
                .intercept(RecordingJavaServerInterceptor(interceptors)),
            clientBuilder = { InProcessChannelBuilder.forName(name) },
            serverConfiguration = { intercept(RecordingServerInterceptor(interceptors)) },
        ) { _, _ ->
            callGrpcJavaService(name, BUILDER_METHOD)
        }

        assertEquals(listOf(BUILDER), interceptors)
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

    private fun runAddressFactoryTest(
        serverBuilder: ServerBuilder<*>,
        providerChannelName: String,
        createClient: (GrpcServer) -> GrpcClient,
    ) = runBlocking {
        val server = GrpcServer(serverBuilder) {
            services { registerService<EchoService> { EchoServiceImpl() } }
        }.start()
        var client: GrpcClient? = null

        try {
            withInProcessChannelProvider(providerChannelName) {
                client = createClient(server)
            }
            assertEquals("Echo", client?.echo("Echo"))
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

    private inline fun withInProcessChannelProvider(name: String, block: () -> Unit) {
        val provider = object : ManagedChannelProvider() {
            override fun isAvailable(): Boolean = true

            // Above the priority of the Netty provider.
            override fun priority(): Int = 10

            override fun builderForAddress(host: String, port: Int): ManagedChannelBuilder<*> =
                InProcessChannelBuilder.forName(name)

            override fun builderForTarget(target: String): ManagedChannelBuilder<*> =
                InProcessChannelBuilder.forName(name)

            override fun getSupportedSocketAddressTypes(): Collection<Class<out SocketAddress>> =
                listOf(InProcessSocketAddress::class.java)
        }
        val registry = ManagedChannelRegistry.getDefaultRegistry()
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

    private class KeepAliveRecordingChannelBuilder(
        private val delegate: ManagedChannelBuilder<*>,
    ) : ForwardingChannelBuilder2<KeepAliveRecordingChannelBuilder>() {
        var time: Duration? = null
        var timeout: Duration? = null
        var withoutCalls: Boolean? = null

        override fun delegate(): ManagedChannelBuilder<*> = delegate

        override fun keepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit): KeepAliveRecordingChannelBuilder =
            apply { time = timeUnit.toNanos(keepAliveTime).nanoseconds }

        override fun keepAliveTimeout(keepAliveTimeout: Long, timeUnit: TimeUnit): KeepAliveRecordingChannelBuilder =
            apply { timeout = timeUnit.toNanos(keepAliveTimeout).nanoseconds }

        override fun keepAliveWithoutCalls(enable: Boolean): KeepAliveRecordingChannelBuilder =
            apply { withoutCalls = enable }
    }

    private class KeepAliveRecordingServerBuilder(
        private val delegate: ServerBuilder<*>,
    ) : ForwardingServerBuilder<KeepAliveRecordingServerBuilder>() {
        var time: Duration? = null
        var timeout: Duration? = null

        override fun delegate(): ServerBuilder<*> = delegate

        override fun keepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit): KeepAliveRecordingServerBuilder =
            apply { time = timeUnit.toNanos(keepAliveTime).nanoseconds }

        override fun keepAliveTimeout(keepAliveTimeout: Long, timeUnit: TimeUnit): KeepAliveRecordingServerBuilder =
            apply { timeout = timeUnit.toNanos(keepAliveTimeout).nanoseconds }
    }

    private class RequestHeadersInterceptor : GrpcServerInterceptor {
        @Volatile
        var requestHeaders: GrpcMetadata? = null

        override fun <Req, Resp> GrpcServerCallScope<Req, Resp>.intercept(request: Flow<Req>): Flow<Resp> {
            this@RequestHeadersInterceptor.requestHeaders = requestHeaders
            return proceed(request)
        }
    }

    private class RecordingServerInterceptor(private val interceptors: MutableList<String>) : GrpcServerInterceptor {
        override fun <Req, Resp> GrpcServerCallScope<Req, Resp>.intercept(request: Flow<Req>): Flow<Resp> {
            interceptors += CONFIGURATION
            return proceed(request)
        }
    }

    private class RecordingJavaServerInterceptor(private val interceptors: MutableList<String>) : ServerInterceptor {
        override fun <ReqT, RespT> interceptCall(
            call: ServerCall<ReqT, RespT>,
            headers: Metadata,
            next: ServerCallHandler<ReqT, RespT>,
        ): ServerCall.Listener<ReqT> {
            interceptors += BUILDER
            return next.startCall(call, headers)
        }
    }

    private class RecordingClientInterceptor(private val interceptors: MutableList<String>) : GrpcClientInterceptor {
        override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
            request: Flow<Request>,
        ): Flow<Response> {
            interceptors += CONFIGURATION
            return proceed(request)
        }
    }

    private class RecordingJavaClientInterceptor(private val interceptors: MutableList<String>) : ClientInterceptor {
        override fun <ReqT, RespT> interceptCall(
            method: MethodDescriptor<ReqT, RespT>,
            callOptions: CallOptions,
            next: Channel,
        ): ClientCall<ReqT, RespT> {
            interceptors += BUILDER
            return next.newCall(method, callOptions)
        }
    }

    private class HeaderCallCredentials(private val name: String, private val value: String) : CallCredentials() {
        override fun applyRequestMetadata(requestInfo: RequestInfo, appExecutor: Executor, applier: MetadataApplier) {
            val headers = Metadata()
            headers.put(Metadata.Key.of(name, Metadata.ASCII_STRING_MARSHALLER), value)
            applier.apply(headers)
        }
    }

    private object BytesMarshaller : MethodDescriptor.Marshaller<ByteArray> {
        override fun stream(value: ByteArray): InputStream = value.inputStream()
        override fun parse(stream: InputStream): ByteArray = stream.readBytes()
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
        private const val BUILDER = "builder"
        private const val CONFIGURATION = "configuration"

        private val BUILDER_METHOD = bytesMethod("kotlinx.rpc.grpc.test.BuilderService", "Call")
        private val ECHO_METHOD = bytesMethod("kotlinx.rpc.grpc.test.EchoService", "UnaryEcho")

        private fun bytesMethod(service: String, method: String): MethodDescriptor<ByteArray, ByteArray> =
            MethodDescriptor.newBuilder(BytesMarshaller, BytesMarshaller)
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(service, method))
                .build()

        private fun grpcJavaService(
            method: MethodDescriptor<ByteArray, ByteArray>,
            status: Status = Status.OK,
        ): ServerServiceDefinition {
            val handler = ServerCalls.asyncUnaryCall<ByteArray, ByteArray> { _, response ->
                if (status.isOk) {
                    response.onNext(ByteArray(0))
                    response.onCompleted()
                } else {
                    response.onError(status.asRuntimeException())
                }
            }

            return ServerServiceDefinition.builder(method.serviceName.orEmpty()).addMethod(method, handler).build()
        }

        private fun callGrpcJavaService(serverName: String, method: MethodDescriptor<ByteArray, ByteArray>) {
            val channel = InProcessChannelBuilder.forName(serverName).build()

            try {
                ClientCalls.blockingUnaryCall(channel, method, CallOptions.DEFAULT, ByteArray(0))
            } finally {
                channel.shutdownNow()
            }
        }

        private fun nettyChannelBuilder(server: GrpcServer): NettyChannelBuilder =
            NettyChannelBuilder.forAddress("localhost", server.port).usePlaintext()
    }
}
