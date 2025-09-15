/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestResult
import kotlinx.rpc.krpc.KrpcConfigBuilder
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.internal.logging.dumpLogger
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.test.KrpcTestClient
import kotlinx.rpc.krpc.test.KrpcTestServer
import kotlinx.rpc.krpc.test.LocalTransport
import kotlinx.rpc.registerService
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlinx.rpc.withService
import kotlin.time.Duration.Companion.seconds

fun runCancellationTest(body: suspend CancellationToolkit.() -> Unit): TestResult {
    return runTestWithCoroutinesProbes(timeout = 15.seconds) {
        val toolkit = CancellationToolkit(this)
        try {
            body(toolkit)
        } finally {
            toolkit.close()
        }
    }
}

class CancellationToolkit(scope: CoroutineScope) : CoroutineScope by scope {
    private val logger = RpcInternalCommonLogger.logger("CancellationTest")

    init {
        RpcInternalDumpLoggerContainer.set(logger.dumpLogger())
    }

    private val configBuilder: KrpcConfigBuilder.() -> Unit = {
        serialization {
            json()
        }
    }

    val transport = LocalTransport(this.coroutineContext.job)

    val client by lazy {
        KrpcTestClient(rpcClientConfig {
            configBuilder()
        }, transport.client)
    }

    val service: CancellationService by lazy { client.withService() }

    val serverInstances: MutableList<CancellationServiceImpl> = mutableListOf()
    private val firstServerInstance = CompletableDeferred<CancellationServiceImpl>()
    suspend fun serverInstance(): CancellationServiceImpl = firstServerInstance.await()

    val server = KrpcTestServer(rpcServerConfig { configBuilder() }, transport.server).apply {
        registerService<CancellationService> {
            CancellationServiceImpl().also { impl ->
                if (!firstServerInstance.isCompleted) {
                    firstServerInstance.complete(impl)
                }

                serverInstances.add(impl)
            }
        }
    }

    suspend fun close() {
        RpcInternalDumpLoggerContainer.set(null)
        client.close()
        server.close()
        client.awaitCompletion()
        server.awaitCompletion()
        transport.coroutineContext.job.cancelAndJoin()
    }
}
