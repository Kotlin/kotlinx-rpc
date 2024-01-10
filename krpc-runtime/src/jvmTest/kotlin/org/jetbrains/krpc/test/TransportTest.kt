/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import org.jetbrains.krpc.*
import org.jetbrains.krpc.client.clientOf
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.server.serverOf
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

    private val clientConfig = rpcClientConfigBuilder {
        serialization {
            json()
        }
    }

    private val serverConfig = rpcServerConfigBuilder {
        serialization {
            json()
        }
    }

    @Test
    fun testUsingWrongService(): Unit = runBlocking {
        val transports = LocalTransport(waiting = false)

        val client = RPC.clientOf<KRPCTestService>(transports.client, clientConfig)
        val result = async {
            assertFailsWith<IllegalStateException> {
                client.simpleWithParams("foo")
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)
        result.await()
        server.cancel()
    }

    @Test
    fun testLateConnect() = runBlocking {
        val transports = LocalTransport()

        val client = RPC.clientOf<Echo>(transports.client, clientConfig)
        val result: Deferred<String> = async {
            client.echo("foo")
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)

        assertEquals("foo", result.await())
        assertEquals(1, echoServer.received.get())

        server.cancel()
    }

    @Test
    fun testLateConnectWithManyCalls() = runBlocking {
        val transports = LocalTransport()

        val client = RPC.clientOf<Echo>(transports.client, clientConfig)
        val result = List(10) {
            async {
                client.echo("foo")
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServer.received.get())

        server.cancel()
    }

    @Test
    fun testLateConnectWithManyClients() = runBlocking {
        val transports = LocalTransport()

        val result = List(10) {
            async {
                val client = RPC.clientOf<Echo>(transports.client, clientConfig)
                client.echo("foo")
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServer.received.get())

        server.cancel()
    }


    @Test
    fun testLateConnectWithManyCallsAndClients() = runBlocking {
        val transports = LocalTransport()

        val result = List(10) {
            async {
                val client = RPC.clientOf<Echo>(transports.client, clientConfig)
                List(10) {
                    async {
                        client.echo("foo")
                    }
                }.awaitAll()
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)

        val response = result.awaitAll().flatten()
        assertTrue { response.all { it == "foo" } }
        assertEquals(100, echoServer.received.get())

        server.cancel()
    }

    @Test
    fun testConnectMultipleServicesOnSingleTransport(): Unit = runBlocking {
        val transports = LocalTransport()

        val firstClient = RPC.clientOf<Echo>(transports.client, clientConfig)
        val firstResult = async {
            firstClient.echo("foo")
        }

        val secondClient = RPC.clientOf<Second>(transports.client, clientConfig)
        val secondResult = async {
            secondClient.second("bar")
        }

        delay(1000)
        val echoServer = EchoServer()
        val echo = RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)
        assertEquals("foo", firstResult.await())
        assertEquals(1, echoServer.received.get())
        echo.cancel()

        delay(1000)
        val secondServer = SecondServer()
        val second = RPC.serverOf<Second>(secondServer, transports.server, serverConfig)
        assertEquals("bar", secondResult.await())
        assertEquals(1, secondServer.received.get())
        second.cancel()
    }

    @Test
    @Ignore
    fun testCancelFromClientToServer() = runBlocking {
        val transports = LocalTransport()

        val client = RPC.clientOf<KRPCTestService>(transports.client, clientConfig)

        val echoServer = EchoServer()
        RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)

        client.cancel()
        echoServer.coroutineContext.job.join()
        assertTrue(echoServer.coroutineContext.job.isCancelled)
    }

    @Test
    @Ignore
    fun testCancelFromServerToClient() = runBlocking {
        val transports = LocalTransport()

        val client = RPC.clientOf<KRPCTestService>(transports.client, clientConfig)

        val echoServer = EchoServer()
        RPC.serverOf<Echo>(echoServer, transports.server, serverConfig)

        echoServer.cancel()
        client.coroutineContext.job.join()
        assertTrue(client.coroutineContext.job.isCancelled)
    }
}
