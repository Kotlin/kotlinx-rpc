package org.jetbrains.krpc

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.modules.SerializersModuleBuilder

abstract class RPCConfigBuilder {
    @Suppress("MemberVisibilityCanBePrivate")
    class SharedFlowParametersBuilder {
        var replay: Int = 1
        var extraBufferCapacity: Int = 10
        var onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND

        fun builder(): () -> MutableSharedFlow<Any?> = { MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow) }
    }

    protected var sharedFlowBuilder: () -> MutableSharedFlow<Any?> = SharedFlowParametersBuilder().builder()

    fun sharedFlowParameters(builder: SharedFlowParametersBuilder.() -> Unit) {
        sharedFlowBuilder = SharedFlowParametersBuilder().apply(builder).builder()
    }

    protected var serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)? = null

    @Suppress("unused")
    fun serializersModuleExtension(extension: SerializersModuleBuilder.() -> Unit) {
        serializersModuleExtension = extension
    }

    class Client: RPCConfigBuilder() {
        fun build(): RPCConfig.Client {
            return RPCConfig.Client(
                sharedFlowBuilder = sharedFlowBuilder,
                serializersModuleExtension = serializersModuleExtension,
            )
        }
    }

    class Server: RPCConfigBuilder() {
        fun build(): RPCConfig.Server {
            return RPCConfig.Server(
                sharedFlowBuilder = sharedFlowBuilder,
                serializersModuleExtension = serializersModuleExtension,
            )
        }
    }
}

interface RPCConfig {
    val sharedFlowBuilder: () -> MutableSharedFlow<Any?>
    val serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)?

    class Client internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)? = null,
    ): RPCConfig {
        companion object {
            val Default: Client = RPCConfigBuilder.Client().build()
        }
    }

    class Server internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)? = null,
    ): RPCConfig  {
        companion object {
            val Default: Server = RPCConfigBuilder.Server().build()
        }
    }
}
