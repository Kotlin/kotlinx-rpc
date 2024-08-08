/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.*
import kotlinx.rpc.client.KRPCClient
import kotlinx.rpc.internal.hex.hexToReadableBinary
import kotlinx.rpc.internal.logging.CommonLogger
import kotlinx.rpc.internal.logging.DumpLogger
import kotlinx.rpc.internal.logging.DumpLoggerContainer
import kotlinx.rpc.serialization.json
import kotlinx.rpc.server.KRPCServer
import kotlinx.serialization.BinaryFormat
import kotlin.coroutines.CoroutineContext

abstract class ProtocolTestBase {
    @Suppress("RedundantUnitReturnType")
    protected fun runTest(
        clientConfig: RPCConfig.Client = rpcClientConfig {
            serialization {
                json()
            }
        },
        serverConfig: RPCConfig.Server = rpcServerConfig {
            serialization {
                json()
            }
        },
        block: suspend TestBody.() -> Unit,
    ): TestResult {
        return kotlinx.coroutines.test.runTest {
            val finished = TestBody(clientConfig, serverConfig, this).apply { block() }

            finished.transport.coroutineContext.job.cancelAndJoin()
        }
    }

    class TestBody(
        clientConfig: RPCConfig.Client,
        serverConfig: RPCConfig.Server,
        private val scope: TestScope
    ) : CoroutineScope by scope {
        private val logger = object : DumpLogger {
            private val _log = CommonLogger.logger("ProtocolTestDump")
            override val isEnabled: Boolean = true
            private val isBinary = clientConfig.serialFormatInitializer.build() is BinaryFormat

            override fun dump(vararg tags: String, message: () -> String) {
                val text = if (isBinary) {
                    val hex = message()
                    "$hex (readable: ${hex.hexToReadableBinary()})"
                } else {
                    message()
                }

                _log.info { "${tags.joinToString(" ") { "[$it]" }} $text" }
            }
        }

        init {
            DumpLoggerContainer.set(logger)
        }

        val transport = LocalTransport()

        // lazy - initialize default endpoints only if needed
        val defaultClient by lazy { ProtocolTestClient(clientConfig, transport) }

        val defaultServer by lazy {
            ProtocolTestServer(serverConfig, transport).apply {
                registerService<ProtocolTestService> {
                    ProtocolTestServiceImpl(transport.coroutineContext)
                }
            }
        }

        val service: ProtocolTestService by lazy { defaultClient.withService() }
    }
}

interface ProtocolTestService : RPC {
    suspend fun sendRequest()
}

private class ProtocolTestServiceImpl(
    override val coroutineContext: CoroutineContext,
) : ProtocolTestService {
    override suspend fun sendRequest() {
        // nothing
    }
}

class ProtocolTestServer(
    config: RPCConfig.Server,
    transport: LocalTransport,
) : KRPCServer(config, transport.server)

class ProtocolTestClient(
    config: RPCConfig.Client,
    transport: LocalTransport,
) : KRPCClient(config, transport.client)
