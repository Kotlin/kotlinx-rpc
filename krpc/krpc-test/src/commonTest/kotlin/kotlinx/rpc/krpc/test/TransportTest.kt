/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcConfigBuilder
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.client.KrpcClient
import kotlinx.rpc.krpc.internal.KrpcProtocolMessage
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Rpc
interface Echo {
    suspend fun echo(message: String): String
}

@Rpc
interface Second {
    suspend fun second(message: String): String
}

class EchoImpl : Echo {
    val received = atomic(0)

    override suspend fun echo(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class SecondServer : Second {
    val received = atomic(0)

    override suspend fun second(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class TransportTest {
    private val clientConfig = rpcClientConfig {
        serialization {
            json()
        }

        connector {
            waitTimeout = Duration.INFINITE
        }
    }

    private val serverConfig = rpcServerConfig {
        serialization {
            json()
        }

        connector {
            waitTimeout = Duration.INFINITE
        }
    }

    private fun clientOf(localTransport: LocalTransport): RpcClient {
        return KrpcTestClient(clientConfig, localTransport.client)
    }

    private fun serverOf(
        localTransport: LocalTransport,
        config: (KrpcConfigBuilder.Server.() -> Unit)? = null,
    ): RpcServer {
        val serverConfig = config?.let { rpcServerConfig(it) } ?: serverConfig
        return KrpcTestServer(serverConfig, localTransport.server)
    }

    private fun runTest(
        timeout: Duration = 120.seconds,
        times: Int = testIterations,
        block: suspend TestScope.(logs: List<String>) -> Unit,
    ): TestResult = runTestWithCoroutinesProbes(timeout = timeout) {
        repeat(times) {
            val logs = mutableListOf<String>()
            val logsChannel = Channel<String>(Channel.UNLIMITED)

            val logsJob = launch(CoroutineName("logs collector")) {
                for (log in logsChannel) {
                    logs.add(log)
                }
            }

            RpcInternalDumpLoggerContainer.set(object : RpcInternalDumpLogger {
                override val isEnabled: Boolean = true

                override fun dump(vararg tags: String, message: () -> String) {
                    val message = "${tags.joinToString(" ") { "[$it]" }} ${message()}"
                    logsChannel.trySend(message)
                }
            })

            try {
                block(logs)
            } finally {
                RpcInternalDumpLoggerContainer.set(null)
                logsJob.cancelAndJoin()
                logsChannel.close()
            }
        }
    }

    @Test
    fun testUsingWrongService() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<KrpcTestService>()
        val result = async {
            assertFails {
                client.simpleWithParams("foo")
            }
        }

        val server = serverOf(transports) {
            serialization {
                json()
            }

            connector {
                waitTimeout = dontWait()
            }
        }
        server.registerService<Echo> { EchoImpl() }

        result.await()
        transports.cancel()
    }

    @Test
    fun testLateConnect() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<Echo>()
        val result: Deferred<String> = async {
            client.echo("foo")
        }

        val server = serverOf(transports)

        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        assertEquals("foo", result.await())
        assertEquals(1, echoServices.single().received.value)

        transports.cancel()
    }

    @Test
    fun testLateConnectWithManyCalls() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<Echo>()
        val result = List(10) {
            async {
                client.echo("foo")
            }
        }

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServices.single().received.value)

        transports.cancel()
    }

    @Test
    fun testLateConnectWithManyServices() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports)

        val result = List(3) {
            async {
                val service = client.withService<Echo>()
                service.echo("foo")
            }
        }

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(3, echoServices.sumOf { it.received.value })

        transports.coroutineContext.job.cancelAndJoin()
    }

    @Test
    fun testLateConnectWithManyCallsAndClients() = runTest(timeout = 240.seconds) {
        val transports = LocalTransport()

        val client = clientOf(transports)

        val result = List(10) {
            async {
                val service = client.withService<Echo>()
                List(10) {
                    async {
                        service.echo("foo")
                    }
                }.awaitAll()
            }
        }

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        val response = result.awaitAll().flatten()
        assertTrue { response.all { it == "foo" } }
        assertEquals(100, echoServices.sumOf { it.received.value })

        transports.cancel()
    }

    @Test
    fun testConnectMultipleServicesOnSingleClient() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports)

        val firstClient = client.withService<Echo>()
        val firstResult = async {
            firstClient.echo("foo")
        }

        val secondClient = client.withService<Second>()
        val secondResult = async {
            secondClient.second("bar")
        }

        val server = serverOf(transports)

        repeat(10) {
            // give way to requests
            yield()
        }
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }
        assertEquals("foo", firstResult.await())
        assertEquals(1, echoServices.single().received.value)

        repeat(10) {
            // give way to requests
            yield()
        }
        val secondServices = server.registerServiceAndReturn<Second, _> { SecondServer() }
        assertEquals("bar", secondResult.await())
        assertEquals(1, secondServices.single().received.value)

        transports.cancel()
    }

    private val handshakeClassSerialName = KrpcProtocolMessage.Handshake.serializer().descriptor.serialName

    @Suppress("RegExpRedundantEscape") // fails on js otherwise
    private val clientHandshake = ".*\\[Client\\] \\[Send\\] \\{\"type\":\"$handshakeClassSerialName\".*".toRegex()

    private val transportInitialized = atomic(0)
    private val configInitialized = atomic(0)

    @Test
    fun transportInitializedOnlyOnce() = runTest(times = 1) { logs ->
        val localTransport = LocalTransport()
        val client = object : KrpcClient() {
            override suspend fun initializeTransport(): KrpcTransport {
                transportInitialized.getAndIncrement()
                return localTransport.client
            }

            override fun initializeConfig(): KrpcConfig.Client {
                configInitialized.getAndIncrement()
                return clientConfig
            }
        }

        val server = serverOf(localTransport)

        server.registerServiceAndReturn<Echo, _> { EchoImpl() }
        server.registerServiceAndReturn<Second, _> { SecondServer() }

        client.withService<Echo>().apply { echo("foo"); echo("bar") }
        client.withService<Second>().apply { second("bar"); second("baz") }

        assertEquals(1, transportInitialized.value)
        assertEquals(1, configInitialized.value)
        assertEquals(1, logs.count { it.matches(clientHandshake) })
    }

    private inline fun <@Rpc reified Service : Any, reified Impl : Service> RpcServer.registerServiceAndReturn(
        crossinline body: () -> Impl,
    ): List<Impl> {
        val instances = mutableListOf<Impl>()

        registerService<Service> { body().also { instances.add(it) } }

        return instances
    }
}

internal expect val testIterations: Int
