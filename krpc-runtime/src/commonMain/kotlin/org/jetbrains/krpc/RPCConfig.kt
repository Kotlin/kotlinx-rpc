package org.jetbrains.krpc

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.serialization.RPCSerialFormatBuilder
import org.jetbrains.krpc.serialization.RPCSerialFormatConfiguration

abstract class RPCConfigBuilder internal constructor() {
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

interface RPCConfig {
    val sharedFlowBuilder: () -> MutableSharedFlow<Any?>
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

fun rpcClientConfigBuilder(builder: RPCConfigBuilder.Client.() -> Unit = {}): RPCConfigBuilder.Client.() -> Unit {
    return builder
}

fun rpcServerConfigBuilder(builder: RPCConfigBuilder.Server.() -> Unit = {}): RPCConfigBuilder.Server.() -> Unit {
    return builder
}

@InternalKRPCApi
fun rpcClientConfig(builder: RPCConfigBuilder.Client.() -> Unit = {}): RPCConfig.Client {
    return RPCConfigBuilder.Client().apply(builder).build()
}

@InternalKRPCApi
fun rpcServerConfig(builder: RPCConfigBuilder.Server.() -> Unit = {}): RPCConfig.Server {
    return RPCConfigBuilder.Server().apply(builder).build()
}
