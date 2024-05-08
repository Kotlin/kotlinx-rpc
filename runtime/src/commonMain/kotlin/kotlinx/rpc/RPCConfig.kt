/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.rpc.internal.InternalKRPCApi
import kotlinx.rpc.serialization.RPCSerialFormat
import kotlinx.rpc.serialization.RPCSerialFormatBuilder
import kotlinx.rpc.serialization.RPCSerialFormatConfiguration

/**
 * Builder for [RPCConfig]. Provides DSL to configure parameters for KRPCClient and/or KRPCServer.
 */
public sealed class RPCConfigBuilder private constructor() {
    /**
     * DSL for parameters of [MutableSharedFlow] and [SharedFlow].
     *
     * This is a temporary solution that hides the problem of transferring these parameters.
     * [SharedFlow] and [MutableSharedFlow] do not define theirs 'replay', 'extraBufferCapacity' and 'onBufferOverflow'
     * parameters, and thus they cannot be encoded and transferred.
     * So then creating their instance on an endpoint, kRPC should know which parameters to use.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class SharedFlowParametersBuilder internal constructor() {
        /**
         * The number of values replayed to new subscribers (cannot be negative, defaults to zero).
         */
        public var replay: Int = DEFAULT_REPLAY

        /**
         * The number of values buffered in addition to replay.
         * emit does not suspend while there is a buffer space remaining
         * (optional, cannot be negative, defaults to zero).
         */
        public var extraBufferCapacity: Int = DEFAULT_EXTRA_BUFFER_CAPACITY

        /**
         * Configures an emit action on buffer overflow.
         * Optional, defaults to suspending attempts to emit a value.
         * Values other than [BufferOverflow.SUSPEND] are supported only when replay > 0 or extraBufferCapacity > 0.
         * Buffer overflow can happen only when there is at least one subscriber
         * that is not ready to accept the new value.
         * In the absence of subscribers only the most recent replay values are stored
         * and the buffer overflow behavior is never triggered and has no effect.
         */
        public var onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND

        @InternalKRPCApi
        public fun builder(): () -> MutableSharedFlow<Any?> = {
            MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
        }

        private companion object {
            /**
             * Default value of [replay]
             */
            const val DEFAULT_REPLAY = 1
            /**
             * Default value of [extraBufferCapacity]
             */
            const val DEFAULT_EXTRA_BUFFER_CAPACITY = 10
        }
    }

    protected var sharedFlowBuilder: () -> MutableSharedFlow<Any?> = SharedFlowParametersBuilder().builder()

    /**
     * @see SharedFlowParametersBuilder
     */
    public fun sharedFlowParameters(builder: SharedFlowParametersBuilder.() -> Unit) {
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

    /**
     * DSL for serialization configuration.
     *
     * Example usage with kotlinx.serialization Json:
     * ```kotlin
     * serialization {
     *     json {
     *         ignoreUnknownKeys = true
     *     }
     * }
     * ```
     *
     * @see RPCSerialFormatConfiguration
     * @see RPCSerialFormat
     */
    public fun serialization(builder: RPCSerialFormatConfiguration.() -> Unit) {
        configuration.builder()
    }

    protected fun rpcSerialFormat(): RPCSerialFormatBuilder<*, *> {
        return when (val format = serialFormatInitializer) {
            null -> error("Please, choose serialization format")
            else -> format
        }
    }

    /**
     * A flag indicating whether the client should wait for subscribers
     * if no service is available to process a message immediately.
     * If false, the endpoint that sent the message will receive call exception
     * that says that there were no services to process its message.
     */
    public var waitForServices: Boolean = true

    /**
     * @see [RPCConfigBuilder]
     */
    public class Client : RPCConfigBuilder() {
        public fun build(): RPCConfig.Client {
            return RPCConfig.Client(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
                waitForServices = waitForServices,
            )
        }
    }

    /**
     * @see [RPCConfigBuilder]
     */
    public class Server : RPCConfigBuilder() {
        public fun build(): RPCConfig.Server {
            return RPCConfig.Server(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
                waitForServices = waitForServices,
            )
        }
    }
}

/**
 * Configuration class that is used by kRPC default RPC client and server (KRPCClient and KRPCServer).
 */
public sealed interface RPCConfig {
    @InternalKRPCApi
    public val sharedFlowBuilder: () -> MutableSharedFlow<Any?>
    @InternalKRPCApi
    public val serialFormatInitializer: RPCSerialFormatBuilder<*, *>
    @InternalKRPCApi
    public val waitForServices: Boolean

    /**
     * @see [RPCConfig]
     */
    public class Client internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatBuilder<*, *>,
        override val waitForServices: Boolean,
    ) : RPCConfig

    /**
     * @see [RPCConfig]
     */
    public class Server internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: RPCSerialFormatBuilder<*, *>,
        override val waitForServices: Boolean,
    ) : RPCConfig
}

/**
 * Creates [RPCConfig.Client] using [RPCConfigBuilder.Client] DSL
 */
public fun rpcClientConfig(builder: RPCConfigBuilder.Client.() -> Unit = {}): RPCConfig.Client {
    return RPCConfigBuilder.Client().apply(builder).build()
}

/**
 * Creates [RPCConfig.Server] using [RPCConfigBuilder.Server]  DSL.
 */
public fun rpcServerConfig(builder: RPCConfigBuilder.Server.() -> Unit = {}): RPCConfig.Server {
    return RPCConfigBuilder.Server().apply(builder).build()
}
