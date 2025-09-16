/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ExtractKtorModule")

package kotlinx.rpc.krpc.ktor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.internal.logging.dumpLogger
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlinx.rpc.withService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.net.ServerSocket
import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.fail
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
    val fence = CompletableDeferred<Unit>()

    override suspend fun verySlow(): String {
        received.complete(Unit)

        fence.await()

        return "hello"
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
    fun testClientTerminatesWhenServerWsDoes() = runTestWithCoroutinesProbes(timeout = 60.seconds) {
        val logger = setupLogger()

        val port: Int = findFreePort()

        val newPool = Executors.newCachedThreadPool().asCoroutineDispatcher()

        val serverReady = CompletableDeferred<Unit>()
        val dropServer = CompletableDeferred<Unit>()

        val impl = SlowServiceImpl()

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

                            registerService<SlowService> { impl }
                        }
                    }
                }.startSuspend(wait = false)

                serverReady.complete(Unit)

                dropServer.await()

                server.stopSuspend(gracePeriodMillis = 100L, timeoutMillis = 300L)
            }

            logger.info { "Server stopped" }
        }

        val ktorClient = HttpClient(CIO) {
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }

            installKrpc {
                serialization {
                    json()
                }
            }
        }

        serverReady.await()

        assertEquals("hello", ktorClient.get("http://0.0.0.0:$port").bodyAsText())

        val rpcClient = ktorClient.rpc("ws://0.0.0.0:$port/rpc")

        var cancellationExceptionCaught = false
        val job = launch {
            try {
                rpcClient.withService<SlowService>().verySlow()
                fail("Must not be called")
            } catch (_: CancellationException) {
                cancellationExceptionCaught = true
                ensureActive()
            }
        }

        impl.received.await()

        logger.info { "Received RPC request" }

        dropServer.complete(Unit)

        logger.info { "Waiting for RPC client to complete" }

        rpcClient.awaitCompletion()

        job.join()

        assertTrue(cancellationExceptionCaught)

        logger.info { "RPC client completed" }

        ktorClient.close()

        serverJob.join()
        newPool.close()
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun testServerTerminatesWhenClientWsDoes() = runTestWithCoroutinesProbes(timeout = 60.seconds) {
        val logger = setupLogger()

        val port: Int = findFreePort()

        val newPool = Executors.newCachedThreadPool().asCoroutineDispatcher()

        val serverReady = CompletableDeferred<Unit>()
        val dropServer = CompletableDeferred<Unit>()

        val impl = SlowServiceImpl()
        val sessionFinished = CompletableDeferred<Unit>()

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
                            coroutineContext.job.invokeOnCompletion {
                                sessionFinished.complete(Unit)
                            }

                            rpcConfig {
                                serialization {
                                    json()
                                }
                            }

                            registerService<SlowService> { impl }
                        }
                    }
                }.startSuspend(wait = false)

                serverReady.complete(Unit)

                dropServer.await()

                server.stopSuspend(gracePeriodMillis = 100L, timeoutMillis = 300L)
            }

            logger.info { "Server stopped" }
        }

        val ktorClient = HttpClient(CIO) {
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }

            installKrpc {
                serialization {
                    json()
                }
            }
        }

        serverReady.await()

        assertEquals("hello", ktorClient.get("http://0.0.0.0:$port").bodyAsText())

        val rpcClient = ktorClient.rpc("ws://0.0.0.0:$port/rpc")

        var cancellationExceptionCaught = false
        val job = launch {
            try {
                rpcClient.withService<SlowService>().verySlow()
                fail("Must not be called")
            } catch (_: CancellationException) {
                cancellationExceptionCaught = true
                ensureActive()
            }
        }

        impl.received.await()

        logger.info { "Received RPC request, Dropping client" }

        ktorClient.cancel()

        logger.info { "Waiting for RPC client to complete" }

        rpcClient.awaitCompletion()

        logger.info { "Waiting for request to complete" }

        job.join()

        assertTrue(cancellationExceptionCaught)

        logger.info { "RPC client and request completed" }

        sessionFinished.await()

        logger.info { "Session finished" }

        dropServer.complete(Unit)
        serverJob.join()

        newPool.close()
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

    private fun setupLogger(): RpcInternalCommonLogger {
        val logger = RpcInternalCommonLogger.logger(KtorTransportTest::class)

        RpcInternalDumpLoggerContainer.set(logger.dumpLogger())

        return logger
    }
}
