/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ExtractKtorModule")

package kotlinx.rpc.krpc.ktor

import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.cancel
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.junit.Assert.assertEquals
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

@Rpc
interface NewService : RemoteService {
    suspend fun echo(value: String): String
}

class NewServiceImpl(
    override val coroutineContext: CoroutineContext,
    private val call: ApplicationCall,
) : NewService {
    @Suppress("UastIncorrectHttpHeaderInspection")
    override suspend fun echo(value: String): String {
        assertEquals("test-header", call.request.headers["TestHeader"])
        return value
    }
}

class KtorTransportTest {
    @Test
    fun testEcho() = testApplication {
        install(Krpc)
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
            installKrpc {
                serialization {
                    json()
                }
            }
        }

        val ktorRpcClient = clientWithGlobalConfig
            .rpc("/rpc") {
                headers["TestHeader"] = "test-header"
            }

        val serviceWithGlobalConfig = ktorRpcClient.withService<NewService>()

        val firstActual = serviceWithGlobalConfig.echo("Hello, world!")

        assertEquals("Hello, world!", firstActual)

        serviceWithGlobalConfig.cancel()
        clientWithGlobalConfig.cancel()

        val clientWithNoConfig = createClient {
            installKrpc()
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
