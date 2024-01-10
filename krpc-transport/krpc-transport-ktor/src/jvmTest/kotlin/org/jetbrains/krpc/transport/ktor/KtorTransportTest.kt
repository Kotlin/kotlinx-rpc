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
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.RPC
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

class NewServiceImpl : NewService {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun echo(value: String): String {
        return value
    }
}

class KtorTransportTest {

    @Test
    fun testEcho() = runBlocking {
        val server = embeddedServer(Netty, port = 4242) {
            install(WebSockets)
            install(org.jetbrains.krpc.transport.ktor.server.KRPC) {
                serialization {
                    json()
                }
            }
            routing {
                rpc<NewService>("/rpc", NewServiceImpl())
            }
        }.start()

        val clientWithGlobalConfig = HttpClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
            install(org.jetbrains.krpc.transport.ktor.client.KRPC) {
                serialization {
                    json()
                }
            }
        }

        val clientWithServiceConfig = HttpClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
            install(org.jetbrains.krpc.transport.ktor.client.KRPC)
        }

        val serviceWithGlobalConfig = clientWithGlobalConfig.rpc<NewService>("ws://localhost:4242/rpc")
        val actual1 = serviceWithGlobalConfig.echo("Hello, world!")

        assertEquals("Hello, world!", actual1)

        serviceWithGlobalConfig.cancel()
        clientWithGlobalConfig.close()

        val serviceWithLocalConfig = clientWithServiceConfig.rpc<NewService>("ws://localhost:4242/rpc") {
            rpcConfig {
                serialization {
                    json()
                }
            }
        }

        val actual2 = serviceWithLocalConfig.echo("Hello, world!")

        assertEquals("Hello, world!", actual2)

        serviceWithLocalConfig.cancel()
        clientWithServiceConfig.close()

        server.stop()
    }
}
