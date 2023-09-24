@file:Suppress("ExtractKtorModule")

package sample

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.ktor.client.rpc
import org.jetbrains.krpc.ktor.server.rpc
import org.junit.Assert.assertEquals
import kotlin.test.Test


interface NewService : RPC {
    suspend fun echo(value: String): String
}

class NewServiceImpl : NewService {
    override suspend fun echo(value: String): String {
        return value
    }
}

class KtorTransportTest {

    @Test
    fun testEcho() = runBlocking {
        val server = embeddedServer(Netty, port = 4242) {
            install(WebSockets)
            routing {
                route("rpc") {
                    rpc<NewService>(NewServiceImpl())
                }
            }
        }.start()

        val client = HttpClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }
        val service = client.rpc<NewService>("ws://localhost:4242/rpc")
        val actual = service.echo("Hello, world!")
        assertEquals("Hello, world!", actual)

        server.stop()
    }
}