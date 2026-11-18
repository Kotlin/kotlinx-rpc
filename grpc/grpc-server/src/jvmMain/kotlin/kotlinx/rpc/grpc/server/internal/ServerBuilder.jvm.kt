/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import io.grpc.Grpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.internal.applySetting
import kotlinx.rpc.grpc.server.GrpcServerBaseConfiguration
import kotlinx.rpc.grpc.server.GrpcServerConfiguration
import kotlinx.rpc.grpc.server.GrpcServerCredentials
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/**
 * Platform-specific gRPC server builder.
 */
@InternalRpcApi
public actual typealias ServerBuilder<T> = io.grpc.ServerBuilder<T>

@InternalRpcApi
public actual fun ServerBuilder(port: Int, credentials: GrpcServerCredentials?): ServerBuilder<*> {
    if (credentials != null) {
        return Grpc.newServerBuilderForPort(port, credentials)
    }
    return io.grpc.ServerBuilder.forPort(port)
}

@InternalRpcApi
public actual fun PlatformServer(builder: ServerBuilder<*>): PlatformServer {
    return builder.build().toKotlin()
}

internal actual fun ServerBuilder<*>.applyConfig(config: GrpcServerConfiguration): ServerBuilder<*> {
    return applyConfig(config, explicitOnly = false)
}

/**
 * Applies each setting of [config] to the builder with [applySetting].
 *
 * @param explicitOnly When `true`, a size limit or a connection limit that [config] doesn't set
 * explicitly stays as the builder has it.
 */
internal fun ServerBuilder<*>.applyConfig(
    config: GrpcServerBaseConfiguration,
    explicitOnly: Boolean,
): ServerBuilder<*> {
    if (!explicitOnly || config.isMaxInboundMessageSizeExplicitlySet) {
        applySetting(this, "maxInboundMessageSize", config.isMaxInboundMessageSizeExplicitlySet) {
            maxInboundMessageSize(config.maxInboundMessageSize)
        }
    }
    if (!explicitOnly || config.isMaxInboundMetadataSizeExplicitlySet) {
        applySetting(this, "maxInboundMetadataSize", config.isMaxInboundMetadataSizeExplicitlySet) {
            maxInboundMetadataSize(config.maxInboundMetadataSize)
        }
    }
    config.keepAlive?.let {
        applySetting(this, "keepAlive", isSet = true) {
            keepAliveTime(it.time.inWholeNanoseconds, TimeUnit.NANOSECONDS)
            keepAliveTimeout(it.timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    if (!explicitOnly || config.isMaxConnectionIdleExplicitlySet) {
        applySetting(this, "maxConnectionIdle", config.isMaxConnectionIdleExplicitlySet) {
            maxConnectionIdle(config.maxConnectionIdle.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    if (!explicitOnly || config.isMaxConnectionAgeExplicitlySet) {
        applySetting(this, "maxConnectionAge", config.isMaxConnectionAgeExplicitlySet) {
            maxConnectionAge(config.maxConnectionAge.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    if (!explicitOnly || config.isMaxConnectionAgeGraceExplicitlySet) {
        applySetting(this, "maxConnectionAgeGrace", config.isMaxConnectionAgeGraceExplicitlySet) {
            maxConnectionAgeGrace(config.maxConnectionAgeGrace.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    return this
}

internal interface JvmPlatformServer : PlatformServer {
    val server: io.grpc.Server
}

private fun io.grpc.Server.toKotlin(): PlatformServer {
    return object : JvmPlatformServer {
        override val server: io.grpc.Server
            get() = this@toKotlin

        override val port: Int
            get() = this@toKotlin.port

        override val isShutdown: Boolean
            get() = this@toKotlin.isShutdown

        override val isTerminated: Boolean
            get() = this@toKotlin.isTerminated

        override fun start(): PlatformServer {
            this@toKotlin.start()
            return this
        }

        override fun shutdown(): PlatformServer {
            this@toKotlin.shutdown()
            return this
        }

        override fun shutdownNow(): PlatformServer {
            this@toKotlin.shutdownNow()
            return this
        }

        override suspend fun awaitTermination(duration: Duration): PlatformServer {
            withContext(Dispatchers.IO) {
                if (duration == Duration.INFINITE) {
                    this@toKotlin.awaitTermination()
                } else {
                    this@toKotlin.awaitTermination(duration.inWholeNanoseconds, TimeUnit.NANOSECONDS)
                }
            }
            return this
        }
    }
}
