/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import ch.qos.logback.classic.LoggerContext
import kotlinx.coroutines.cancelAndJoin
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.registerService
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlinx.rpc.withService
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

/**
 * Regression test for GitHub #541.
 *
 * `KrpcServerService`, `KrpcServer` and `KrpcClient` used to derive their logger name from
 * `rpcInternalObjectId()`, which embeds the instance `hashCode()`. Logging backends (Logback here)
 * cache one logger per distinct name and never evict them, so every new connection leaked a logger
 * into the backend's registry. After the fix the logger name is stable per type, so the number of
 * cached loggers stays constant regardless of how many connections are served.
 */
class LoggerLeakTest {
    @Test
    fun `loggers are reused across connections instead of created per instance`() =
        runTestWithCoroutinesProbes(timeout = 60.seconds) {
            val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
            val connections = 64

            repeat(connections) {
                val transport = LocalTransport(coroutineContext, recordTimestamps = false)

                val client = KrpcTestClient(rpcClientConfig { serialization { json() } }, transport.client)
                val server = KrpcTestServer(rpcServerConfig { serialization { json() } }, transport.server)
                server.registerService<TestService> { TestServiceImpl() }

                // triggers lazy creation of the per-service-type KrpcServerService on the server
                client.withService<TestService>().unary(1)

                client.close()
                server.close()
                client.awaitCompletion()
                server.awaitCompletion()
                transport.coroutineContext.cancelAndJoin()
            }

            val testServiceFqName = TestService::class.qualifiedName!!
            val serverServiceLoggers = loggerContext.loggerList.filter {
                it.name.startsWith("KrpcServerService") && it.name.contains(testServiceFqName)
            }
            assertEquals(
                1,
                serverServiceLoggers.size,
                "Expected a single KrpcServerService logger for $testServiceFqName after $connections " +
                        "connections, found ${serverServiceLoggers.size}: ${serverServiceLoggers.map { it.name }}",
            )

            val clientLoggers = loggerContext.loggerList.filter {
                it.name.startsWith(KrpcTestClient::class.simpleName!!)
            }
            assertEquals(
                1,
                clientLoggers.size,
                "Expected a single ${KrpcTestClient::class.simpleName} logger after $connections " +
                        "connections, found ${clientLoggers.size}: ${clientLoggers.map { it.name }}",
            )

            val serverLoggers = loggerContext.loggerList.filter {
                it.name.startsWith(KrpcTestServer::class.simpleName!!)
            }
            assertEquals(
                1,
                serverLoggers.size,
                "Expected a single ${KrpcTestServer::class.simpleName} logger after $connections " +
                        "connections, found ${serverLoggers.size}: ${serverLoggers.map { it.name }}",
            )
        }
}
