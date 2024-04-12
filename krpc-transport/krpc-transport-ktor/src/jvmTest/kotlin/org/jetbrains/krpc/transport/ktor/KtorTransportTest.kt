/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ExtractKtorModule")

package org.jetbrains.krpc.transport.ktor

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.transport.ktor.client.rpc
import org.jetbrains.krpc.transport.ktor.client.rpcConfig
import org.jetbrains.krpc.transport.ktor.server.rpc
import org.junit.Assert.assertEquals
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

interface NewService : RPC {
    suspend fun echo(value: String): String
}

class NewServiceImpl(override val coroutineContext: CoroutineContext) : NewService {
    override suspend fun echo(value: String): String {
        return value
    }
}

class KtorTransportTest {
    @Test
    fun testEcho() = runBlocking {
        val server = embeddedServer(Netty, port = 4242) {
            install(WebSockets)
            install(org.jetbrains.krpc.transport.ktor.server.RPC) {
                serialization {
                    json()
                }
            }
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

                    registerService<NewService> { NewServiceImpl(it) }
                }
            }
        }.start()

        val clientWithGlobalConfig = HttpClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
            install(org.jetbrains.krpc.transport.ktor.client.RPC) {
                serialization {
                    json()
                }
            }
        }

        val serviceWithGlobalConfig = clientWithGlobalConfig
            .rpc("ws://localhost:4242/rpc")
            .withService<NewService>()

        val firstActual = serviceWithGlobalConfig.echo("Hello, world!")

        assertEquals("Hello, world!", firstActual)

        serviceWithGlobalConfig.cancel()
        clientWithGlobalConfig.cancel()

        val clientWithNoConfig = HttpClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
            install(org.jetbrains.krpc.transport.ktor.client.RPC)
        }

        val serviceWithLocalConfig = clientWithNoConfig.rpc("ws://localhost:4242/rpc") {
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
