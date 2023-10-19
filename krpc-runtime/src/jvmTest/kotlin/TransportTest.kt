import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.client.clientOf
import org.jetbrains.krpc.server.serverOf
import org.jetbrains.krpc.test.MyService
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

    @Test
    fun testUsingWrongService(): Unit = runBlocking {
        val transports = StringTransport(waiting = false)

        val client = RPC.clientOf<MyService>(transports.client)
        val result = async {
            assertFailsWith<IllegalStateException> {
                client.simpleWithParams("foo")
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server)
        result.await()
        server.cancel()
    }

    @Test
    fun testLateConnect() = runBlocking {
        val transports = StringTransport()

        val client = RPC.clientOf<Echo>(transports.client)
        val result: Deferred<String> = async {
            client.echo("foo")
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server)

        assertEquals("foo", result.await())
        assertEquals(1, echoServer.received.get())

        server.cancel()
    }

    @Test
    fun testLateConnectWithManyCalls() = runBlocking {
        val transports = StringTransport()

        val client = RPC.clientOf<Echo>(transports.client)
        val result = List(10) {
            async {
                client.echo("foo")
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server)

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServer.received.get())

        server.cancel()
    }

    @Test
    fun testLateConnectWithManyClients() = runBlocking {
        val transports = StringTransport()

        val result = List(10) {
            async {
                val client = RPC.clientOf<Echo>(transports.client)
                client.echo("foo")
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server)

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServer.received.get())

        server.cancel()
    }


    @Test
    fun testLateConnectWithManyCallsAndClients() = runBlocking {
        val transports = StringTransport()

        val result = List(10) {
            async {
                val client = RPC.clientOf<Echo>(transports.client)
                List(10) {
                    async {
                        client.echo("foo")
                    }
                }.awaitAll()
            }
        }

        val echoServer = EchoServer()
        val server = RPC.serverOf<Echo>(echoServer, transports.server)

        val response = result.awaitAll().flatten()
        assertTrue { response.all { it == "foo" } }
        assertEquals(100, echoServer.received.get())

        server.cancel()
    }

    @Test
    fun testConnectMultipleServicesOnSingleTransport(): Unit = runBlocking {
        val transports = StringTransport()

        val firstClient = RPC.clientOf<Echo>(transports.client)
        val firstResult = async {
            firstClient.echo("foo")
        }

        val secondClient = RPC.clientOf<Second>(transports.client)
        val secondResult = async {
            secondClient.second("bar")
        }

        delay(1000)
        val echoServer = EchoServer()
        val echo = RPC.serverOf<Echo>(echoServer, transports.server)
        assertEquals("foo", firstResult.await())
        assertEquals(1, echoServer.received.get())
        echo.cancel()

        delay(1000)
        val secondServer = SecondServer()
        val second = RPC.serverOf<Second>(secondServer, transports.server)
        assertEquals("bar", secondResult.await())
        assertEquals(1, secondServer.received.get())
        second.cancel()
    }

    @Test
    @Ignore
    fun testCancelFromClientToServer() = runBlocking {
        val transports = StringTransport()

        val client = RPC.clientOf<MyService>(transports.client)

        val echoServer = EchoServer()
        RPC.serverOf<Echo>(echoServer, transports.server)

        client.cancel()
        echoServer.coroutineContext.job.join()
        assertTrue(echoServer.coroutineContext.job.isCancelled)
    }

    @Test
    @Ignore
    fun testCancelFromServerToClient() = runBlocking {
        val transports = StringTransport()

        val client = RPC.clientOf<MyService>(transports.client)

        val echoServer = EchoServer()
        RPC.serverOf<Echo>(echoServer, transports.server)

        echoServer.cancel()
        client.coroutineContext.job.join()
        assertTrue(client.coroutineContext.job.isCancelled)
    }
}
