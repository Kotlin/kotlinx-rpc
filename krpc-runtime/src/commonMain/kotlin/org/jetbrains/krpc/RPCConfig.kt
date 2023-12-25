/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.serialization.RPCSerialFormatBuilder
import org.jetbrains.krpc.serialization.RPCSerialFormatConfiguration

sealed class RPCConfigBuilder private constructor() {
    @Suppress("MemberVisibilityCanBePrivate")
    class SharedFlowParametersBuilder internal constructor() {
        var replay: Int = DEFAULT_REPLAY
        var extraBufferCapacity: Int = DEFAULT_EXTRA_BUFFER_CAPACITY
        var onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND

        fun builder(): () -> MutableSharedFlow<Any?> = {
            MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
        }

        companion object {
            const val DEFAULT_REPLAY = 1
            const val DEFAULT_EXTRA_BUFFER_CAPACITY = 10
        }
    }

    protected var sharedFlowBuilder: () -> MutableSharedFlow<Any?> = SharedFlowParametersBuilder().builder()

    fun sharedFlowParameters(builder: SharedFlowParametersBuilder.() -> Unit) {
        sharedFlowBuilder = SharedFlowParametersBuilder().apply(builder).builder()
    }

    private var serialFormatInitializer: RPCSerialFormatBuilder<*, *>? = null

    private val configuration = object : RPCSerialFormatConfiguration {
        override fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.Binary<*, *>) {
            serialFormatInitializer = rpcSerialFormatInitializer
        }

        override fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.String<*, *>) {
            serialFormatInitializer = rpcSerialFormatInitializer
        }
    }

    fun serialization(builder: RPCSerialFormatConfiguration.() -> Unit) {
        configuration.builder()
    }

    protected fun rpcSerialFormat(): RPCSerialFormatBuilder<*, *> {
        return when (val format = serialFormatInitializer) {
            null -> error("Please, choose serialization format")
            else -> format
        }
    }

    var waitForServices: Boolean = true

    class Client : RPCConfigBuilder() {
        fun build(): RPCConfig.Client {
            return RPCConfig.Client(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
                waitForServices = waitForServices,
            )
        }
    }

    class Server : RPCConfigBuilder() {
        fun build(): RPCConfig.Server {
            return RPCConfig.Server(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
                waitForServices = waitForServices,
            )
        }
    }
}

/**
 * Configuration class that is used by kRPC default RPC client and server.
 */
sealed interface RPCConfig {
    @InternalKRPCApi
    val sharedFlowBuilder: () -> MutableSharedFlow<Any?>
    @InternalKRPCApi
    val serialFormatInitializer: RPCSerialFormatBuilder<*, *>
    @InternalKRPCApi
    val waitForServices: Boolean

    class Client internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatBuilder<*, *>,
        override val waitForServices: Boolean,
    ) : RPCConfig

    class Server internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatBuilder<*, *>,
        override val waitForServices: Boolean,
    ) : RPCConfig
}

fun rpcClientConfig(builder: RPCConfigBuilder.Client.() -> Unit = {}): RPCConfig.Client {
    return RPCConfigBuilder.Client().apply(builder).build()
}

fun rpcServerConfig(builder: RPCConfigBuilder.Server.() -> Unit = {}): RPCConfig.Server {
    return RPCConfigBuilder.Server().apply(builder).build()
}
