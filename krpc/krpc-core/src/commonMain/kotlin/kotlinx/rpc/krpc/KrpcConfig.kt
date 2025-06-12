/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.serialization.KrpcSerialFormat
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatBuilder
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration

/**
 * Builder for [KrpcConfig]. Provides DSL to configure parameters for KrpcClient and/or KrpcServer.
 */
public sealed class KrpcConfigBuilder protected constructor() {
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
                serialFormatInitializer = rpcSerialFormat(),
                waitForServices = waitForServices,
            )
        }
    }
}

/**
 * Configuration class that is used by kRPC protocol's client and server (KrpcClient and KrpcServer).
 */
public sealed interface KrpcConfig {
    @InternalRpcApi
    public val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>
    @InternalRpcApi
    public val waitForServices: Boolean

    /**
     * @see [KrpcConfig]
     */
    public class Client internal constructor(
        override val serialFormatInitializer: KrpcSerialFormatBuilder<*, *>,
        override val waitForServices: Boolean,
    ) : KrpcConfig

    /**
     * @see [KrpcConfig]
     */
    public class Server internal constructor(
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
