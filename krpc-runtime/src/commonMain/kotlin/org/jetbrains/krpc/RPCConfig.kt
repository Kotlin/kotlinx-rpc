package org.jetbrains.krpc

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import org.jetbrains.krpc.serialization.RPCSerialFormatConfiguration
import org.jetbrains.krpc.serialization.RPCSerialFormatInitializer

abstract class RPCConfigBuilder internal constructor() {
    @Suppress("MemberVisibilityCanBePrivate")
    class SharedFlowParametersBuilder internal constructor() {
        var replay: Int = 1
        var extraBufferCapacity: Int = 10
        var onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND

        fun builder(): () -> MutableSharedFlow<Any?> = {
            MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
        }
    }

    protected var sharedFlowBuilder: () -> MutableSharedFlow<Any?> = SharedFlowParametersBuilder().builder()

    fun sharedFlowParameters(builder: SharedFlowParametersBuilder.() -> Unit) {
        sharedFlowBuilder = SharedFlowParametersBuilder().apply(builder).builder()
    }

    private var serialFormatInitializer: RPCSerialFormatInitializer<*, *>? = null

    private val configuration = object : RPCSerialFormatConfiguration {
        override fun register(rpcSerialFormatInitializer: RPCSerialFormatInitializer<out StringFormat, *>) {
            serialFormatInitializer = rpcSerialFormatInitializer
        }

        override fun register(rpcSerialFormatInitializer: RPCSerialFormatInitializer<out BinaryFormat, *>) {
            serialFormatInitializer = rpcSerialFormatInitializer
        }
    }

    fun serialization(builder: RPCSerialFormatConfiguration.() -> Unit) {
        configuration.builder()
    }

    protected fun rpcSerialFormat(): RPCSerialFormatInitializer<*, *> {
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
    val serialFormatInitializer: RPCSerialFormatInitializer<*, *>

    class Client internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatInitializer<*, *>,
    ) : RPCConfig {
        companion object {
            val Default: Client by lazy { RPCConfigBuilder.Client().build() }
        }
    }

    class Server internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatInitializer<*, *>,
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
