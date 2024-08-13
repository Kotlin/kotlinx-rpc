/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ExtractKtorModule")

package kotlinx.rpc.transport.ktor

import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.cancel
import kotlinx.rpc.RPC
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc
import kotlinx.rpc.withService
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
    fun testEcho() = testApplication {
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

        val clientWithGlobalConfig = createClient {
            installRPC {
                serialization {
                    json()
                }
            }
        }

        val ktorRPCClient = clientWithGlobalConfig
            .rpc("/rpc") {
                headers["TestHeader"] = "test-header"
            }

        val serviceWithGlobalConfig = ktorRPCClient.withService<NewService>()

        val firstActual = serviceWithGlobalConfig.echo("Hello, world!")

        assertEquals("Hello, world!", firstActual)

        serviceWithGlobalConfig.cancel()
        clientWithGlobalConfig.cancel()

        val clientWithNoConfig = createClient {
            installRPC()
        }

        val serviceWithLocalConfig = clientWithNoConfig.rpc("/rpc") {
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
    }
}
