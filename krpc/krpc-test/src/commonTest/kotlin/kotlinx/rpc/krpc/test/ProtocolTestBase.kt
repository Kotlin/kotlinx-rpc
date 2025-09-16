/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.utils.hex.rpcInternalHexToReadableBinary
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.client.InitializedKrpcClient
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.server.KrpcServer
import kotlinx.rpc.registerService
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlinx.rpc.withService
import kotlinx.serialization.BinaryFormat
import kotlin.time.Duration.Companion.seconds

abstract class ProtocolTestBase {
    protected fun runTest(
        clientConfig: KrpcConfig.Client = rpcClientConfig {
            serialization {
                json()
            }
        },
        serverConfig: KrpcConfig.Server = rpcServerConfig {
            serialization {
                json()
            }
        },
        block: suspend TestBody.() -> Unit,
    ): TestResult {
        return runTestWithCoroutinesProbes(timeout = 60.seconds) {
            val finished = TestBody(clientConfig, serverConfig, this)

            try {
                finished.block()
            } finally {
                finished.transport.coroutineContext.job.cancelAndJoin()
            }
        }
    }

    class TestBody(
        clientConfig: KrpcConfig.Client,
        serverConfig: KrpcConfig.Server,
        private val scope: TestScope
    ) : CoroutineScope by scope {
        private val logger = object : RpcInternalDumpLogger {
            private val _log = RpcInternalCommonLogger.logger("ProtocolTestDump")
            override val isEnabled: Boolean = true
            private val isBinary = clientConfig.serialFormatInitializer.build() is BinaryFormat

            override fun dump(vararg tags: String, message: () -> String) {
                val text = if (isBinary) {
                    val hex = message()
                    "$hex (readable: ${hex.rpcInternalHexToReadableBinary()})"
                } else {
                    message()
                }

                _log.info { "${tags.joinToString(" ") { "[$it]" }} $text" }
            }
        }

        init {
            RpcInternalDumpLoggerContainer.set(logger)
        }

        val transport = LocalTransport()

        // lazy - initialize default endpoints only if needed
        val defaultClient by lazy { ProtocolTestClient(clientConfig, transport) }

        val defaultServer by lazy {
            ProtocolTestServer(serverConfig, transport).apply {
                registerService<ProtocolTestService> { ProtocolTestServiceImpl() }
            }
        }

        val service: ProtocolTestService by lazy { defaultClient.withService() }
    }
}

@Rpc
interface ProtocolTestService {
    suspend fun sendRequest()
}

private class ProtocolTestServiceImpl : ProtocolTestService {
    override suspend fun sendRequest() {
        // nothing
    }
}

class ProtocolTestServer(
    config: KrpcConfig.Server,
    transport: LocalTransport,
) : KrpcServer(config, transport.server)

class ProtocolTestClient(
    config: KrpcConfig.Client,
    transport: LocalTransport,
) : InitializedKrpcClient(config, transport.client)
