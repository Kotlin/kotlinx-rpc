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

        fun build(): () -> MutableSharedFlow<Any?> = {
            MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
        }

        companion object {
            const val DEFAULT_REPLAY = 1
            const val DEFAULT_EXTRA_BUFFER_CAPACITY = 10
        }
    }

    protected var sharedFlowBuilder: () -> MutableSharedFlow<Any?> = SharedFlowParametersBuilder().build()

    fun sharedFlowParameters(builder: SharedFlowParametersBuilder.() -> Unit) {
        sharedFlowBuilder = SharedFlowParametersBuilder().apply(builder).build()
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

    class Client : RPCConfigBuilder() {
        fun build(): RPCConfig.Client {
            return RPCConfig.Client(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
            )
        }
    }

    class Server : RPCConfigBuilder() {
        fun build(): RPCConfig.Server {
            return RPCConfig.Server(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
            )
        }
    }
}

sealed interface RPCConfig {
    @InternalKRPCApi
    val sharedFlowBuilder: () -> MutableSharedFlow<Any?>
    @InternalKRPCApi
    val serialFormatInitializer: RPCSerialFormatBuilder<*, *>

    class Client internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatBuilder<*, *>,
    ) : RPCConfig {
        companion object {
            val Default: Client by lazy { RPCConfigBuilder.Client().build() }
        }
    }

    class Server internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatBuilder<*, *>,
    ) : RPCConfig {
        companion object {
            val Default: Server by lazy { RPCConfigBuilder.Server().build() }
        }
    }
}

fun rpcClientConfig(builder: RPCConfigBuilder.Client.() -> Unit = {}): RPCConfig.Client {
    return RPCConfigBuilder.Client().apply(builder).build()
}

fun rpcServerConfig(builder: RPCConfigBuilder.Server.() -> Unit = {}): RPCConfig.Server {
    return RPCConfigBuilder.Server().apply(builder).build()
}
