/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.krpc.KrpcConfigBuilder
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.test.KrpcTestClient
import kotlinx.rpc.krpc.test.KrpcTestServer
import kotlinx.rpc.krpc.test.LocalTransport
import kotlinx.rpc.registerService
import kotlinx.rpc.withService

fun runCancellationTest(body: suspend CancellationToolkit.() -> Unit): TestResult {
    return runTest {
        CancellationToolkit(this).apply { body() }
    }
}

class CancellationToolkit(scope: CoroutineScope) : CoroutineScope by scope {
    private val logger = RpcInternalCommonLogger.logger("CancellationTest")

    init {
        RpcInternalDumpLoggerContainer.set(object : RpcInternalDumpLogger {
            override val isEnabled: Boolean = true

            override fun dump(vararg tags: String, message: () -> String) {
                logger.info { "${tags.joinToString(" ") { "[$it]" }} ${message()}" }
            }
        })
    }

    private val serializationConfig: KrpcConfigBuilder.() -> Unit = {
        serialization {
            json()
        }
    }

    val transport = LocalTransport(this)

    val client by lazy {
        KrpcTestClient(rpcClientConfig {
            serializationConfig()
        }, transport.client)
    }

    val service: CancellationService by lazy { client.withService() }

    val serverInstances: MutableList<CancellationServiceImpl> = mutableListOf()
    private val firstServerInstance = CompletableDeferred<CancellationServiceImpl>()
    suspend fun serverInstance(): CancellationServiceImpl = firstServerInstance.await()

    val server = KrpcTestServer(rpcServerConfig { serializationConfig() }, transport.server).apply {
        registerService<CancellationService> {
            CancellationServiceImpl(it).also { impl ->
                if (!firstServerInstance.isCompleted) {
                    firstServerInstance.complete(impl)
                }

                serverInstances.add(impl)
            }
        }
    }
}

/**
 * [runTest] can skip delays, and sometimes it prevents from writing a test
 * running delay on [Dispatchers.Default] fixes delay, for questions refer to [runTest] documentation.
 */
suspend fun unskippableDelay(millis: Long) {
    withContext(Dispatchers.Default) { delay(millis) }
}
