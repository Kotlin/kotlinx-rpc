/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import org.jetbrains.krpc.*
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.server.RPCServer
import org.jetbrains.krpc.server.registerService
import org.slf4j.simple.SimpleLogger
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

interface Echo : RPC {
    suspend fun echo(message: String): String
}

interface Second: RPC {
    suspend fun second(message: String): String
}

class EchoServer : Echo {
    override val coroutineContext: CoroutineContext = Job()

    val received = AtomicInteger()

    override suspend fun echo(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class SecondServer : Second {
    override val coroutineContext: CoroutineContext = Job()

    val received = AtomicInteger()

    override suspend fun second(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class TransportTest {
    init {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")
    }

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
        return KRPCTestClient(clientConfig, Job(), localTransport.client)
    }

    private fun serverOf(
        localTransport: LocalTransport,
        config: (RPCConfigBuilder.Server.() -> Unit)? = null
    ): RPCServer {
        val serverConfig = config?.let { rpcServerConfig(it) } ?: serverConfig
        return KRPCTestServer(serverConfig, Job(), localTransport.server)
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

        val echoServer = EchoServer()
        val server = serverOf(transports) {
            serialization {
                json()
            }

            waitForServices = false
        }
        server.registerService<Echo>(echoServer)

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

        val echoServer = EchoServer()
        val server = serverOf(transports)
        server.registerService<Echo>(echoServer)

        assertEquals("foo", result.await())
        assertEquals(1, echoServer.received.get())

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

        val echoServer = EchoServer()
        val server = serverOf(transports)
        server.registerService<Echo>(echoServer)

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServer.received.get())

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

        val echoServer = EchoServer()
        val server = serverOf(transports)
        server.registerService<Echo>(echoServer)

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServer.received.get())

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

        val echoServer = EchoServer()
        val server = serverOf(transports)
        server.registerService<Echo>(echoServer)

        val response = result.awaitAll().flatten()
        assertTrue { response.all { it == "foo" } }
        assertEquals(100, echoServer.received.get())

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
        val echoServer = EchoServer()
        server.registerService<Echo>(echoServer)
        assertEquals("foo", firstResult.await())
        assertEquals(1, echoServer.received.get())
        echoServer.cancel()

        delay(1000)
        val secondServer = SecondServer()
        server.registerService<Second>(secondServer)
        assertEquals("bar", secondResult.await())
        assertEquals(1, secondServer.received.get())
        secondServer.cancel()

        server.cancel()
    }

    @Test
    @Ignore
    fun testCancelFromClientToServer() = runBlocking {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<KRPCTestService>()

        val echoServer = EchoServer()
        val server = serverOf(transports)
        server.registerService<Echo>(echoServer)

        client.cancel()
        echoServer.coroutineContext.job.join()
        assertTrue(echoServer.coroutineContext.job.isCancelled)
    }

    @Test
    @Ignore
    fun testCancelFromServerToClient() = runBlocking {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<KRPCTestService>()

        val echoServer = EchoServer()
        val server = serverOf(transports)
        server.registerService<Echo>(echoServer)

        echoServer.cancel()
        client.coroutineContext.job.join()
        assertTrue(client.coroutineContext.job.isCancelled)
    }
}
