package org.jetbrains.krpc

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.modules.SerializersModuleBuilder

abstract class RPCConfigBuilder {
    @Suppress("MemberVisibilityCanBePrivate")
    class SharedFlowFactoryBuilder {
        var replay: Int = 1
        var extraBufferCapacity: Int = 10
        var onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND

        fun factory(): () -> MutableSharedFlow<Any?> = { MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow) }
    }

    protected var incomingSharedFlowFactory: () -> MutableSharedFlow<Any?> = SharedFlowFactoryBuilder().factory()

    fun incomingSharedFlowFactory(builder: SharedFlowFactoryBuilder.() -> Unit) {
        incomingSharedFlowFactory = SharedFlowFactoryBuilder().apply(builder).factory()
    }

    protected var serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)? = null

    @Suppress("unused")
    fun serializersModuleExtension(extension: SerializersModuleBuilder.() -> Unit) {
        serializersModuleExtension = extension
    }

    class Client: RPCConfigBuilder() {
        fun build(): RPCConfig.Client {
            return RPCConfig.Client(
                incomingSharedFlowFactory = incomingSharedFlowFactory,
                serializersModuleExtension = serializersModuleExtension,
            )
        }
    }

    class Server: RPCConfigBuilder() {
        fun build(): RPCConfig.Server {
            return RPCConfig.Server(
                incomingSharedFlowFactory = incomingSharedFlowFactory,
                serializersModuleExtension = serializersModuleExtension,
            )
        }
    }
}

interface RPCConfig {
    val incomingSharedFlowFactory: () -> MutableSharedFlow<Any?>
    val serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)?

    class Client internal constructor(
        override val incomingSharedFlowFactory: () -> MutableSharedFlow<Any?>,
        override val serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)? = null,
    ): RPCConfig {
        companion object {
            val Default: Client = RPCConfigBuilder.Client().build()
        }
    }

    class Server internal constructor(
        override val incomingSharedFlowFactory: () -> MutableSharedFlow<Any?>,
        override val serializersModuleExtension: (SerializersModuleBuilder.() -> Unit)? = null,
    ): RPCConfig  {
        companion object {
            val Default: Server = RPCConfigBuilder.Server().build()
        }
    }
}
