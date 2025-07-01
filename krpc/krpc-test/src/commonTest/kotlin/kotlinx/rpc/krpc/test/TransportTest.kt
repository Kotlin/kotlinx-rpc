/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.KrpcConfigBuilder
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@Rpc
interface Echo {
    suspend fun echo(message: String): String
}

@Rpc
interface Second {
    suspend fun second(message: String): String
}

class EchoImpl : Echo {
    val received = atomic(0)

    override suspend fun echo(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class SecondServer : Second {
    val received = atomic(0)

    override suspend fun second(message: String): String {
        received.incrementAndGet()
        return message
    }
}

class TransportTest {
    private val clientConfig = rpcClientConfig {
        serialization {
            json()
        }
    }

    private val serverConfig = rpcServerConfig {
        serialization {
            json()
        }
    }

    private fun clientOf(localTransport: LocalTransport): RpcClient {
        return KrpcTestClient(clientConfig, localTransport.client)
    }

    private fun serverOf(
        localTransport: LocalTransport,
        config: (KrpcConfigBuilder.Server.() -> Unit)? = null,
    ): RpcServer {
        val serverConfig = config?.let { rpcServerConfig(it) } ?: serverConfig
        return KrpcTestServer(serverConfig, localTransport.server)
    }

    private fun runTest(block: suspend TestScope.() -> Unit): TestResult =
        kotlinx.coroutines.test.runTest(timeout = 20.seconds) {
            debugCoroutines()

            val logger = RpcInternalCommonLogger.logger("TransportTest")

            RpcInternalDumpLoggerContainer.set(object : RpcInternalDumpLogger {
                override val isEnabled: Boolean = true

                override fun dump(vararg tags: String, message: () -> String) {
                    logger.info { "${tags.joinToString(" ") { "[$it]" }} ${message()}" }
                }
            })

            block()

            RpcInternalDumpLoggerContainer.set(null)
        }

    @Test
    fun testUsingWrongService() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<KrpcTestService>()
        val result = async {
            assertFails {
                client.simpleWithParams("foo")
            }
        }

        val server = serverOf(transports) {
            serialization {
                json()
            }

            waitForServices = false
        }
        server.registerService<Echo> { EchoImpl() }

        result.await()
        transports.cancel()
    }

    @Test
    fun testLateConnect() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<Echo>()
        val result: Deferred<String> = async {
            client.echo("foo")
        }

        val server = serverOf(transports)

        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        assertEquals("foo", result.await())
        assertEquals(1, echoServices.single().received.value)

        transports.cancel()
    }

    @Test
    fun testLateConnectWithManyCalls() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports).withService<Echo>()
        val result = List(10) {
            async {
                client.echo("foo")
            }
        }

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(10, echoServices.single().received.value)

        transports.cancel()
    }

    @Test
    fun testLateConnectWithManyServices() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports)

        val result = List(3) {
            async {
                val service = client.withService<Echo>()
                service.echo("foo")
            }
        }

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        val response = result.awaitAll()
        assertTrue { response.all { it == "foo" } }
        assertEquals(3, echoServices.sumOf { it.received.value })

        transports.coroutineContext.job.cancelAndJoin()
    }

    @Test
    fun testLateConnectWithManyCallsAndClients() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports)

        val result = List(10) {
            async {
                val service = client.withService<Echo>()
                List(10) {
                    async {
                        service.echo("foo")
                    }
                }.awaitAll()
            }
        }

        val server = serverOf(transports)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }

        val response = result.awaitAll().flatten()
        assertTrue { response.all { it == "foo" } }
        assertEquals(100, echoServices.sumOf { it.received.value })

        transports.cancel()
    }

    @Test
    fun testConnectMultipleServicesOnSingleClient() = runTest {
        val transports = LocalTransport()

        val client = clientOf(transports)

        val firstClient = client.withService<Echo>()
        val firstResult = async {
            firstClient.echo("foo")
        }

        val secondClient = client.withService<Second>()
        val secondResult = async {
            secondClient.second("bar")
        }

        val server = serverOf(transports)

        delay(1000)
        val echoServices = server.registerServiceAndReturn<Echo, _> { EchoImpl() }
        assertEquals("foo", firstResult.await())
        assertEquals(1, echoServices.single().received.value)

        delay(1000)
        val secondServices = server.registerServiceAndReturn<Second, _> { SecondServer() }
        assertEquals("bar", secondResult.await())
        assertEquals(1, secondServices.single().received.value)

        transports.cancel()
    }

    private inline fun <@Rpc reified Service : Any, reified Impl : Service> RpcServer.registerServiceAndReturn(
        crossinline body: () -> Impl,
    ): List<Impl> {
        val instances = mutableListOf<Impl>()

        registerService<Service> { body().also { instances.add(it) } }

        return instances
    }
}
