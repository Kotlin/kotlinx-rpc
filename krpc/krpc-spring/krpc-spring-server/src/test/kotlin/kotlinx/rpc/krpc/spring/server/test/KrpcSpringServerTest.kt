/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.test

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.withService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class KrpcSpringServerTest {
    @Test
    fun `serves unary calls with JSON serialization`() = runBlocking {
        KrpcSpringServer().use { server ->
            val client = server.rpcClient()

            assertEquals("hello", server.withRpcTimeout { client.withService<EchoService>().echo("hello") })

            client.close()
        }
    }

    @Test
    fun `serves unary calls with CBOR serialization`() = runBlocking {
        KrpcSpringServer("cbor").use { server ->
            val client = server.rpcClient()

            assertEquals("hello", server.withRpcTimeout { client.withService<EchoService>().echo("hello") })

            client.close()
        }
    }

    @Test
    fun `delivers server stream elements in order`() = runBlocking {
        KrpcSpringServer().use { server ->
            val client = server.rpcClient()
            val actual = server.withRpcTimeout { client.withService<EchoService>().range(512).toList() }

            assertEquals((0 until 512).toList(), actual)

            client.close()
        }
    }

    @Test
    fun `serves concurrent calls on one connection`() = runBlocking {
        KrpcSpringServer().use { server ->
            val client = server.rpcClient()
            val service = client.withService<EchoService>()
            val values = (0 until 32).map { "value-$it" }

            val actual = server.withRpcTimeout {
                coroutineScope { values.map { value -> async { service.echo(value) } }.awaitAll() }
            }

            assertEquals(values, actual)

            client.close()
        }
    }

    @Test
    fun `serves each RPC interface implemented by one bean`() = runBlocking {
        KrpcSpringServer().use { server ->
            val client = server.rpcClient()

            assertEquals("cba", server.withRpcTimeout { client.withService<ReverseService>().reverse("abc") })

            client.close()
        }
    }

    @Test
    fun `a service failure reaches the client without closing the connection`() = runBlocking {
        KrpcSpringServer().use { server ->
            val client = server.rpcClient()
            val service = client.withService<EchoService>()

            val failure = server.withRpcTimeout { runCatching { service.fail() }.exceptionOrNull() }
            assertNotNull(failure)
            assertTrue(failure.message.orEmpty().contains("expected service failure"))
            assertEquals("still connected", server.withRpcTimeout { service.echo("still connected") })

            client.close()
        }
    }

    @Test
    fun `closing a client closes its WebSocket session`() = runBlocking {
        KrpcSpringServer().use { server ->
            val client = server.rpcClient()
            server.withRpcTimeout { client.withService<EchoService>().echo("open") }
            val session = client.webSocketSession.await()

            client.close()

            awaitSessionClose(session)
        }
    }

    @Test
    fun `closing one client leaves another client connected`() = runBlocking {
        KrpcSpringServer().use { server ->
            val firstClient = server.rpcClient()
            val secondClient = server.rpcClient()
            server.withRpcTimeout { firstClient.withService<EchoService>().echo("first") }
            val firstSession = firstClient.webSocketSession.await()
            val secondService = secondClient.withService<EchoService>()

            firstClient.close()

            awaitSessionClose(firstSession)
            assertEquals("second", server.withRpcTimeout { secondService.echo("second") })

            secondClient.close()
        }
    }

    @Test
    fun `closing the Spring context closes open client WebSockets`() = runBlocking {
        KrpcSpringServer().use { server ->
            val client = server.rpcClient()
            server.withRpcTimeout { client.withService<EchoService>().echo("open") }
            val session = client.webSocketSession.await()

            server.closeApplicationContext()

            awaitSessionClose(session)
            client.close()
        }
    }

    private suspend fun awaitSessionClose(session: WebSocketSession) {
        withTimeout(5.seconds) { session.coroutineContext.job.join() }
    }
}
