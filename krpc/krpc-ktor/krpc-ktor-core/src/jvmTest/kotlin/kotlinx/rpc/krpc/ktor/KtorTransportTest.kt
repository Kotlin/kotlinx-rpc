/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ExtractKtorModule")

package kotlinx.rpc.krpc.ktor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.client.KrpcClient
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.junit.Assert.assertEquals
import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import java.net.ServerSocket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

@Rpc
interface NewService {
    suspend fun echo(value: String): String
}

class NewServiceImpl(
    private val call: ApplicationCall,
) : NewService {
    @Suppress("UastIncorrectHttpHeaderInspection")
    override suspend fun echo(value: String): String {
        assertEquals("test-header", call.request.headers["TestHeader"])
        return value
    }
}

@Rpc
interface SlowService {
    suspend fun verySlow(): String
}

class SlowServiceImpl : SlowService {
    val received = CompletableDeferred<Unit>()

    override suspend fun verySlow(): String {
        received.complete(Unit)

        delay(Int.MAX_VALUE.toLong())

        error("Must not be called")
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

                registerService<NewService> { NewServiceImpl(call) }
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

        clientWithNoConfig.cancel()
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Test
    @Ignore("Wait for Ktor fix (https://github.com/ktorio/ktor/pull/4927) or apply workaround if rejected")
    fun testEndpointsTerminateWhenWsDoes() = runTest(timeout = 15.seconds) {
        DebugProbes.install()

        val logger = setupLogger()

        val port: Int = findFreePort()

        val newPool = Executors.newCachedThreadPool().asCoroutineDispatcher()

        val serverReady = CompletableDeferred<Unit>()
        val dropServer = CompletableDeferred<Unit>()

        val service = SlowServiceImpl()

        @Suppress("detekt.GlobalCoroutineUsage")
        val serverJob = GlobalScope.launch(CoroutineName("server")) {
            withContext(newPool) {
                val server = embeddedServer(
                    factory = Netty,
                    port = port,
                    parentCoroutineContext = newPool,
                ) {
                    install(Krpc)

                    routing {
                        get {
                            call.respondText("hello")
                        }

                        rpc("/rpc") {
                            rpcConfig {
                                serialization {
                                    json()
                                }
                            }

                            registerService<SlowService> { service }
                        }
                    }
                }.start(wait = false)

                serverReady.complete(Unit)

                dropServer.await()

                server.stop(shutdownGracePeriod = 100L, shutdownTimeout = 100L, timeUnit = TimeUnit.MILLISECONDS)
            }

            logger.info { "Server stopped" }
        }

        val ktorClient = HttpClient(CIO) {
            installKrpc {
                serialization {
                    json()
                }
            }
        }

        serverReady.await()

        assertEquals("hello", ktorClient.get("http://0.0.0.0:$port").bodyAsText())

        val rpcClient = ktorClient.rpc("ws://0.0.0.0:$port/rpc")

        launch {
            try {
                rpcClient.withService<SlowService>().verySlow()
                error("Must not be called")
            } catch (_: CancellationException) {
                logger.info { "Cancellation exception caught for RPC request" }
                ensureActive()
            }
        }

        service.received.await()

        logger.info { "Received RPC request" }

        dropServer.complete(Unit)

        logger.info { "Waiting for RPC client to complete" }

        (rpcClient as KrpcClient).awaitCompletion()

        logger.info { "RPC client completed" }

        ktorClient.close()
        newPool.close()

        serverJob.cancel()
    }

    private fun findFreePort(): Int {
        val port: Int
        while (true) {
            val socket = try {
                ServerSocket(0)
            } catch (_: Throwable) {
                continue
            }

            port = socket.localPort
            socket.close()
            break
        }
        return port
    }

    private fun setupLogger(): Logger {
        val logger = LoggerFactory.getLogger(KtorTransportTest::class.java)

        RpcInternalDumpLoggerContainer.set(object : RpcInternalDumpLogger {

            override val isEnabled: Boolean = true

            override fun dump(vararg tags: String, message: () -> String) {
                logger.info { "[${tags.joinToString()}] ${message()}" }
            }
        })

        return logger
    }
}
