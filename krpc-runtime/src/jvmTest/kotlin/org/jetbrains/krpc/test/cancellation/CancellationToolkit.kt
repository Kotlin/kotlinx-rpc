/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.DumpLogger
import org.jetbrains.krpc.internal.logging.DumpLoggerContainer
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.registerService
import org.jetbrains.krpc.rpcClientConfig
import org.jetbrains.krpc.rpcServerConfig
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.test.KRPCTestClient
import org.jetbrains.krpc.test.KRPCTestServer
import org.jetbrains.krpc.test.KRPCTestServiceBackend
import org.jetbrains.krpc.test.LocalTransport

@Suppress("RedundantUnitReturnType")
fun runCancellationTest(body: suspend CancellationToolkit.() -> Unit): TestResult {
    runTest {
        CancellationToolkit(this).apply { body() }
    }
}

class CancellationToolkit(scope: CoroutineScope) : CoroutineScope by scope {
    private val logger = CommonLogger.initialized().logger("[CancellationTest]")

    init {
        DumpLoggerContainer.set(object : DumpLogger {
            override val isEnabled: Boolean = true

            override fun dump(vararg tags: String, message: () -> String) {
                logger.info { "${tags.joinToString(" ") { "[$it]" }} ${message()}" }
            }
        })
    }

    private val serializationConfig: RPCConfigBuilder.() -> Unit = {
        serialization {
            json()
        }
    }

    val transport = LocalTransport(this)

    val client by lazy {
        KRPCTestClient(rpcClientConfig {
            serializationConfig()

            sharedFlowParameters {
                replay = KRPCTestServiceBackend.SHARED_FLOW_REPLAY
            }
        }, transport.client)
    }

    val service: CancellationService by lazy { client.withService() }

    val serverInstances: MutableList<CancellationServiceImpl> = mutableListOf()
    private val firstServerInstance = CompletableDeferred<CancellationServiceImpl>()
    suspend fun serverInstance(): CancellationServiceImpl = firstServerInstance.await()

    val server = KRPCTestServer(rpcServerConfig { serializationConfig() }, transport.server).apply {
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
