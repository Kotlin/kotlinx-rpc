/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.serialization.KrpcSerialFormat
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatBuilder
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration

@Deprecated("Use KrpcConfigBuilder instead", ReplaceWith("KrpcConfigBuilder"), level = DeprecationLevel.ERROR)
public typealias RPCConfigBuilder = KrpcConfigBuilder

/**
 * Builder for [KrpcConfig]. Provides DSL to configure parameters for KrpcClient and/or KrpcServer.
 */
public sealed class KrpcConfigBuilder private constructor() {
    /**
     * DSL for parameters of [MutableSharedFlow] and [SharedFlow].
     *
     * This is a temporary solution that hides the problem of transferring these parameters.
     * [SharedFlow] and [MutableSharedFlow] do not define theirs 'replay', 'extraBufferCapacity' and 'onBufferOverflow'
     * parameters, and thus they cannot be encoded and transferred.
     * So then creating their instance on an endpoint, the library should know which parameters to use.
     */
    @Deprecated(
        "SharedFlow support is deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
        level = DeprecationLevel.WARNING,
    )
    @Suppress("MemberVisibilityCanBePrivate")
    public class SharedFlowParametersBuilder internal constructor() {
        /**
         * The number of values replayed to new subscribers (cannot be negative, defaults to zero).
         */
        @Deprecated(
            "SharedFlow support is deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
            level = DeprecationLevel.WARNING,
        )
        public var replay: Int = DEFAULT_REPLAY

        /**
         * The number of values buffered in addition to replay.
         * emit does not suspend while there is a buffer space remaining
         * (optional, cannot be negative, defaults to zero).
         */
        @Deprecated(
            "SharedFlow support is deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
            level = DeprecationLevel.WARNING,
        )
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
        @Deprecated(
            "SharedFlow support is deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
            level = DeprecationLevel.WARNING,
        )
        public var onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND

        @InternalRpcApi
        public fun builder(): () -> MutableSharedFlow<Any?> = {
            @Suppress("DEPRECATION")
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

    @Suppress("DEPRECATION")
    protected var sharedFlowBuilder: () -> MutableSharedFlow<Any?> = SharedFlowParametersBuilder().builder()

    /**
     * @see SharedFlowParametersBuilder
     */
    @Deprecated(
        "SharedFlow support is deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
        level = DeprecationLevel.WARNING,
    )
    public fun sharedFlowParameters(builder: @Suppress("DEPRECATION") SharedFlowParametersBuilder.() -> Unit) {
        @Suppress("DEPRECATION")
        sharedFlowBuilder = SharedFlowParametersBuilder().apply(builder).builder()
    }

    private var serialFormatInitializer: KrpcSerialFormatBuilder<*, *>? = null

    private val configuration = object : KrpcSerialFormatConfiguration {
        override fun register(rpcSerialFormatInitializer: KrpcSerialFormatBuilder.Binary<*, *>) {
            serialFormatInitializer = rpcSerialFormatInitializer
        }

        override fun register(rpcSerialFormatInitializer: KrpcSerialFormatBuilder.String<*, *>) {
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
     * @see KrpcSerialFormatConfiguration
     * @see KrpcSerialFormat
     */
    public fun serialization(builder: KrpcSerialFormatConfiguration.() -> Unit) {
        configuration.builder()
    }

    protected fun rpcSerialFormat(): KrpcSerialFormatBuilder<*, *> {
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
     * @see [KrpcConfigBuilder]
     */
    public class Client : KrpcConfigBuilder() {
        public fun build(): KrpcConfig.Client {
            return KrpcConfig.Client(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
                waitForServices = waitForServices,
            )
        }
    }

    /**
     * @see [KrpcConfigBuilder]
     */
    public class Server : KrpcConfigBuilder() {
        public fun build(): KrpcConfig.Server {
            return KrpcConfig.Server(
                sharedFlowBuilder = sharedFlowBuilder,
                serialFormatInitializer = rpcSerialFormat(),
                waitForServices = waitForServices,
            )
        }
    }
}

@Deprecated("Use KrpcConfig instead", ReplaceWith("KrpcConfig"), level = DeprecationLevel.ERROR)
public typealias RPCConfig = KrpcConfig

/**
 * Configuration class that is used by kRPC protocol's client and server (KrpcClient and KrpcServer).
 */
public sealed interface KrpcConfig {
    @InternalRpcApi
    public val sharedFlowBuilder: () -> MutableSharedFlow<Any?>
    @InternalRpcApi
    public val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>
    @InternalRpcApi
    public val waitForServices: Boolean

    /**
     * @see [KrpcConfig]
     */
    public class Client internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>,
        override val waitForServices: Boolean,
    ) : KrpcConfig

    /**
     * @see [KrpcConfig]
     */
    public class Server internal constructor(
        override val sharedFlowBuilder: () -> MutableSharedFlow<Any?>,
        override val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>,
        override val waitForServices: Boolean,
    ) : KrpcConfig
}

/**
 * Creates [KrpcConfig.Client] using [KrpcConfigBuilder.Client] DSL
 */
public fun rpcClientConfig(builder: KrpcConfigBuilder.Client.() -> Unit = {}): KrpcConfig.Client {
    return KrpcConfigBuilder.Client().apply(builder).build()
}

/**
 * Creates [KrpcConfig.Server] using [KrpcConfigBuilder.Server]  DSL.
 */
public fun rpcServerConfig(builder: KrpcConfigBuilder.Server.() -> Unit = {}): KrpcConfig.Server {
    return KrpcConfigBuilder.Server().apply(builder).build()
}
