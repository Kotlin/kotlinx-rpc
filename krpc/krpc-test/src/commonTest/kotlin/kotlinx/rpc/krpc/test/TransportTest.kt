/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.KrpcConfigBuilder
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlin.coroutines.CoroutineContext
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

@Rpc
interface Echo : RemoteService {
    suspend fun echo(message: String): String
}

@Rpc
interface Second : RemoteService {
    suspend fun second(message: String): String
}

class EchoImpl(override val coroutineContext: CoroutineContext) : Echo {
    val received = atomic(0)

    override suspend fun echo(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class SecondServer(override val coroutineContext: CoroutineContext) : Second {
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
    }

    private val serverConfig = rpcServerConfig {
        serialization {
            json()
        }
    }

    private fun clientOf(localTransport: LocalTransport): RpcClient {
        return KrpcTestClient(clientConfig, localTransport.client)
    }

    private fun serverOf(
        localTransport: LocalTransport,
        config: (KrpcConfigBuilder.Server.() -> Unit)? = null
    ): RpcServer {
        val serverConfig = config?.let { rpcServerConfig(it) } ?: serverConfig
        return KrpcTestServer(serverConfig, localTransport.server)
    }

    private fun runTest(block: suspend TestScope.() -> Unit): TestResult = kotlinx.coroutines.test.runTest {
        val logger = RpcInternalCommonLogger.logger("TransportTest")

        RpcInternalDumpLoggerContainer.set(object : RpcInternalDumpLogger {
            override val isEnabled: Boolean = true

            override fun dump(vararg tags: String, message: () -> String) {
                logger.info { "${tags.joinToString(" ") { "[$it]" }} ${message()}" }
            }
        })

        block()

        RpcInternalDumpLoggerContainer.set(null)
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

            waitForServices = false
        }
        server.registerService<Echo> { EchoImpl(it) }

        result.await()
        server.cancel()
    }

    @Test
    fun testLateConnect() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<Echo>()
        val result: Deferred<String> = async {
            client.echo("foo")
        }

        val server = serverOf(transports)

        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }

        assertEquals("foo", result.await())
        assertEquals(1, echoServices.single().received.value)

        server.cancel()
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
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServices.single().received.value)

        server.cancel()
    }

    @Test
    fun testLateConnectWithManyServices() = runTest {
        repeat(100) {
            val transports = LocalTransport()

            val client = clientOf(transports)

            val result = List(10) {
                async {
                    val service = client.withService<Echo>()
                    service.echo("foo")
                }
            }

            val server = serverOf(transports)
            val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }

            val response = result.awaitAll()
            assertTrue { response.all { it == "foo" } }
            assertEquals(10, echoServices.sumOf { it.received.value })

            server.cancel()
        }
    }


    @Test
    fun testLateConnectWithManyCallsAndClients() = runTest {
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
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }

        val response = result.awaitAll().flatten()
        assertTrue { response.all { it == "foo" } }
        assertEquals(100, echoServices.sumOf { it.received.value })

        server.cancel()
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

        delay(1000)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }
        assertEquals("foo", firstResult.await())
        assertEquals(1, echoServices.single().received.value)
        echoServices.single().cancel()

        delay(1000)
        val secondServices = server.registerServiceAndReturn<Second, _> { SecondServer(it) }
        assertEquals("bar", secondResult.await())
        assertEquals(1, secondServices.single().received.value)
        secondServices.single().cancel()

        server.cancel()
    }

    @Test
    @Ignore
    fun testCancelFromClientToServer() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<KrpcTestService>()

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }

        client.cancel()
        echoServices.single().coroutineContext.job.join()
        assertTrue(echoServices.single().coroutineContext.job.isCancelled)
    }

    private inline fun <@Rpc reified Service : Any, reified Impl : Service> RpcServer.registerServiceAndReturn(
        crossinline body: (CoroutineContext) -> Impl,
    ): List<Impl> {
        val instances = mutableListOf<Impl>()

        registerService<Service> { ctx ->
            body(ctx).also { instances.add(it) }
        }

        return instances
    }
}
