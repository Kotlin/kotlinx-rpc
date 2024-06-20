/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.rpc.*
import kotlinx.rpc.client.withService
import kotlinx.rpc.serialization.json
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

interface Echo : RPC {
    suspend fun echo(message: String): String
}

interface Second : RPC {
    suspend fun second(message: String): String
}

class EchoImpl(override val coroutineContext: CoroutineContext) : Echo {
    val received = AtomicInteger()

    override suspend fun echo(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class SecondServer(override val coroutineContext: CoroutineContext) : Second {
    val received = AtomicInteger()

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

    private fun clientOf(localTransport: LocalTransport): RPCClient {
        return KRPCTestClient(clientConfig, localTransport.client)
    }

    private fun serverOf(
        localTransport: LocalTransport,
        config: (RPCConfigBuilder.Server.() -> Unit)? = null
    ): RPCServer {
        val serverConfig = config?.let { rpcServerConfig(it) } ?: serverConfig
        return KRPCTestServer(serverConfig, localTransport.server)
    }

    @Test
    fun testUsingWrongService(): Unit = runBlocking {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<KRPCTestService>()
        val result = async {
            assertFailsWith<IllegalStateException> {
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
    fun testLateConnect() = runBlocking {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<Echo>()
        val result: Deferred<String> = async {
            client.echo("foo")
        }

        val server = serverOf(transports)

        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }

        assertEquals("foo", result.await())
        assertEquals(1, echoServices.single().received.get())

        server.cancel()
    }

    @Test
    fun testLateConnectWithManyCalls() = runBlocking {
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
        assertEquals(10, echoServices.single().received.get())

        server.cancel()
    }

    @Test
    fun testLateConnectWithManyServices() = runBlocking {
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
        assertEquals(10, echoServices.sumOf { it.received.get() })

        server.cancel()
    }


    @Test
    fun testLateConnectWithManyCallsAndClients() = runBlocking {
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
        assertEquals(100, echoServices.sumOf { it.received.get() })

        server.cancel()
    }

    @Test
    fun testConnectMultipleServicesOnSingleClient(): Unit = runBlocking {
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
        assertEquals(1, echoServices.single().received.get())
        echoServices.single().cancel()

        delay(1000)
        val secondServices = server.registerServiceAndReturn<Second, _> { SecondServer(it) }
        assertEquals("bar", secondResult.await())
        assertEquals(1, secondServices.single().received.get())
        secondServices.single().cancel()

        server.cancel()
    }

    @Test
    @Ignore
    fun testCancelFromClientToServer() = runBlocking {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<KRPCTestService>()

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl(it) }

        client.cancel()
        echoServices.single().coroutineContext.job.join()
        assertTrue(echoServices.single().coroutineContext.job.isCancelled)
    }

    private inline fun <reified Service : RPC, reified Impl : Service> RPCServer.registerServiceAndReturn(
        crossinline body: (CoroutineContext) -> Impl,
    ): List<Impl> {
        val instances = mutableListOf<Impl>()

        registerService<Service> { ctx ->
            body(ctx).also { instances.add(it) }
        }

        return instances
    }
}
