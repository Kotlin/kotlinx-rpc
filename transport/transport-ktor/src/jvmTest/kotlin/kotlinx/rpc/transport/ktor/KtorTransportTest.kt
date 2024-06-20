/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ExtractKtorModule")

package kotlinx.rpc.transport.ktor

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RPC
import kotlinx.rpc.client.withService
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc
import org.junit.Assert.assertEquals
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

interface NewService : RPC {
    suspend fun echo(value: String): String
}

class NewServiceImpl(
    override val coroutineContext: CoroutineContext,
    private val call: ApplicationCall,
) : NewService {
    override suspend fun echo(value: String): String {
        assertEquals("test-header", call.request.headers["TestHeader"])
        return value
    }
}

class KtorTransportTest {
    @Test
    fun testEcho() = runBlocking {
        val server = embeddedServer(Netty, port = 4242) {
            install(RPC)
            routing {
                rpc("/rpc") {
                    rpcConfig {
                        serialization {
                            json {
                                ignoreUnknownKeys = true
                            }
                        }

                        waitForServices = true
                    }

                    registerService<NewService> { NewServiceImpl(it, call) }
                }
            }
        }.start()

        val clientWithGlobalConfig = HttpClient {
            install(WebSockets) {
                maxFrameSize = Int.MAX_VALUE.toLong() - 42
            }
            install(kotlinx.rpc.transport.ktor.client.RPC) {
                serialization {
                    json()
                }
            }
        }

        val ktorRPCClient = clientWithGlobalConfig
            .rpc("ws://localhost:4242/rpc") {
                headers["TestHeader"] = "test-header"
            }

        assertEquals(Int.MAX_VALUE.toLong() - 42, ktorRPCClient.webSocketSession.maxFrameSize)

        val serviceWithGlobalConfig = ktorRPCClient.withService<NewService>()

        val firstActual = serviceWithGlobalConfig.echo("Hello, world!")

        assertEquals("Hello, world!", firstActual)

        serviceWithGlobalConfig.cancel()
        clientWithGlobalConfig.cancel()

        val clientWithNoConfig = HttpClient {
            installRPC()
        }

        val serviceWithLocalConfig = clientWithNoConfig.rpc("ws://localhost:4242/rpc") {
            headers["TestHeader"] = "test-header"

            rpcConfig {
                serialization {
                    json()
                }
            }
        }.withService<NewService>()

        val secondActual = serviceWithLocalConfig.echo("Hello, world!")

        assertEquals("Hello, world!", secondActual)

        serviceWithLocalConfig.cancel()
        clientWithNoConfig.cancel()

        server.stop()
    }
}
