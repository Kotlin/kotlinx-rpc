/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.rpc.krpc.serialization.KrpcSerialFormat
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatBuilder
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Builder for [KrpcConfig]. Provides DSL to configure parameters for KrpcClient and/or KrpcServer.
 */
public sealed class KrpcConfigBuilder protected constructor() {
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

    /**
     * DSL for connector configuration.
     *
     * Connector is responsible for handling all messages transferring.
     * Example usage:
     * ```kotlin
     * connector {
     *     waitTimeout = 10.seconds
     *     callTimeout = 10.seconds
     *     perCallBufferSize = 1000
     * }
     * ```
     */
    public fun connector(builder: Connector.() -> Unit) {
        connector.builder()
    }

    /**
     * Configuration for RPC connector - a handler for all messages transferring.
     */
    public class Connector {
        /**
         * A flag indicating how long a client or a server should wait for subscribers
         * if no service is available to process a message immediately.
         * If negative ([dontWait]) or when timeout is exceeded,
         * the endpoint that sent the unprocessed message will receive a call exception
         * saying there were no services to process the message.
         */
        public var waitTimeout: Duration = Duration.INFINITE

        /**
         * A flag indicating that a client or a server should not wait for subscribers
         *
         * @see Connector.waitTimeout
         */
        public fun dontWait(): Duration = (-1).seconds

        /**
         * A timeout for a call.
         * If a call is not completed in this time, it will be cancelled with a call exception.
         */
        public var callTimeout: Duration = Duration.INFINITE

        /**
         * A buffer size for a single call.
         *
         * The default value is 1,
         * meaning that only after one message is handled - the next one will be sent.
         *
         * This buffer also applies to how many messages are cached with [waitTimeout]
         */
        public var perCallBufferSize: Int = 1
    }

    @Deprecated("Use connector { } instead", level = DeprecationLevel.ERROR)
    public var waitForServices: Boolean = true

    /**
     * @see [KrpcConfigBuilder]
     */
    public class Client : KrpcConfigBuilder() {
        public fun build(): KrpcConfig.Client {
            return KrpcConfig.Client(
                serialFormatInitializer = rpcSerialFormat(),
                connector = buildConnector(),
            )
        }
    }

    /**
     * @see [KrpcConfigBuilder]
     */
    public class Server : KrpcConfigBuilder() {
        public fun build(): KrpcConfig.Server {
            return KrpcConfig.Server(
                serialFormatInitializer = rpcSerialFormat(),
                connector = buildConnector(),
            )
        }
    }

    /*
     * #####################################################################
     * #                                                                   #
     * #                     CLASS INTERNALS AHEAD                         #
     * #                                                                   #
     * #####################################################################
     */

    private var serialFormatInitializer: KrpcSerialFormatBuilder<*, *>? = null

    private val connector = Connector()

    public fun buildConnector(): KrpcConfig.Connector {
        return KrpcConfig.Connector(connector.waitTimeout, connector.callTimeout, connector.perCallBufferSize)
    }

    private val configuration = object : KrpcSerialFormatConfiguration {
        override fun register(rpcSerialFormatInitializer: KrpcSerialFormatBuilder.Binary<*, *>) {
            serialFormatInitializer = rpcSerialFormatInitializer
        }

        override fun register(rpcSerialFormatInitializer: KrpcSerialFormatBuilder.String<*, *>) {
            serialFormatInitializer = rpcSerialFormatInitializer
        }
    }

    protected fun rpcSerialFormat(): KrpcSerialFormatBuilder<*, *> {
        return when (val format = serialFormatInitializer) {
            null -> error("Please, choose serialization format")
            else -> format
        }
    }
}

/**
 * Configuration class that is used by kRPC protocol's client and server (KrpcClient and KrpcServer).
 */
public sealed interface KrpcConfig {
    /**
     * @see KrpcConfigBuilder.serialization
     */
    public val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>

    public val connector: Connector

    @Deprecated("Use connector instead", level = DeprecationLevel.ERROR)
    public val waitForServices: Boolean get() = true

    /**
     * @see KrpcConfigBuilder.connector
     */
    public class Connector internal constructor(
        /**
         * @see KrpcConfigBuilder.Connector.waitTimeout
         */
        public val waitTimeout: Duration,

        /**
         * @see KrpcConfigBuilder.Connector.callTimeout
         */
        public val callTimeout: Duration,

        /**
         * @see KrpcConfigBuilder.Connector.perCallBufferSize
         */
        public val perCallBufferSize: Int,
    )

    /**
     * @see [KrpcConfig]
     */
    public class Client internal constructor(
        /**
         * @see KrpcConfigBuilder.serialization
         */
        override val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>,

        override val connector: Connector,
    ) : KrpcConfig

    /**
     * @see [KrpcConfig]
     */
    public class Server internal constructor(
        /**
         * @see KrpcConfigBuilder.serialization
         */
        override val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>,

        /**
         * @see KrpcConfigBuilder.connector
         */
        override val connector: Connector,
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
