/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.serialization.BinaryFormat
import org.jetbrains.krpc.*
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.internal.hex.hexToReadableBinary
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.DumpLogger
import org.jetbrains.krpc.internal.logging.DumpLoggerContainer
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.transport.RPCPlugin
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.server.KRPCServer
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
        timeout: Duration = 10.seconds,
        block: suspend TestBody.() -> Unit,
    ): TestResult {
        return kotlinx.coroutines.test.runTest(timeout = timeout) {
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
            private val _log = CommonLogger.initialized().logger("ProtocolTestDump")
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
                registerService<ProtocolTestService>(
                    ProtocolTestServiceImpl(transport.coroutineContext)
                )
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
) : KRPCServer(config), RPCTransport by transport.server {
    val clientPlugins: Map<Long, Set<RPCPlugin>>

    init {
        val prop = KRPCServer::class
            .memberProperties
            .single { it.name == "clientSupportedPlugins" }
            .apply {
                isAccessible = true
            }

        @Suppress("UNCHECKED_CAST")
        clientPlugins = prop.call(this) as Map<Long, Set<RPCPlugin>>
    }
}

class ProtocolTestClient(
    config: RPCConfig.Client,
    transport: LocalTransport,
) : KRPCClient(config), RPCTransport by transport.client {
    @OptIn(ExperimentalCoroutinesApi::class)
    val serverPlugins: Set<RPCPlugin>
        get() = if (serverSupportedPluginsDeferred.isCompleted) {
            serverSupportedPluginsDeferred.getCompleted()
        } else {
            emptySet()
        }

    private val serverSupportedPluginsDeferred: CompletableDeferred<Set<RPCPlugin>>

    private lateinit var idProperty: KProperty1<KRPCClient, *>

    val id: Long by lazy {
        idProperty.call(this) as Long
    }

    init {
        val (idProp, deferredProp) = KRPCClient::class
            .memberProperties
            .filter { it.name == "serverSupportedPlugins" || it.name == "connectionId" }
            .sortedBy { it.name }
            .onEach {
                it.isAccessible = true
            }
        idProperty = idProp

        @Suppress("UNCHECKED_CAST")
        serverSupportedPluginsDeferred = deferredProp.call(this) as CompletableDeferred<Set<RPCPlugin>>
    }
}
