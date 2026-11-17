/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import io.grpc.Grpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
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
 * @param explicitOnly When `true`:
 * - A size limit or a connection limit that [config] doesn't set explicitly stays as the builder has it.
 * - A setting that the builder doesn't support fails with [GrpcUnsupportedSettingException].
 */
internal fun ServerBuilder<*>.applyConfig(
    config: GrpcServerConfiguration,
    explicitOnly: Boolean,
): ServerBuilder<*> {
    if (!explicitOnly || config.isMaxInboundMessageSizeExplicitlySet) {
        setting("maxInboundMessageSize", explicitOnly) { maxInboundMessageSize(config.maxInboundMessageSize) }
    }
    if (!explicitOnly || config.isMaxInboundMetadataSizeExplicitlySet) {
        setting("maxInboundMetadataSize", explicitOnly) { maxInboundMetadataSize(config.maxInboundMetadataSize) }
    }
    config.keepAlive?.let {
        setting("keepAlive", explicitOnly) {
            keepAliveTime(it.time.inWholeNanoseconds, TimeUnit.NANOSECONDS)
            keepAliveTimeout(it.timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    if (!explicitOnly || config.isMaxConnectionIdleExplicitlySet) {
        setting("maxConnectionIdle", explicitOnly) {
            maxConnectionIdle(config.maxConnectionIdle.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    if (!explicitOnly || config.isMaxConnectionAgeExplicitlySet) {
        setting("maxConnectionAge", explicitOnly) {
            maxConnectionAge(config.maxConnectionAge.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    if (!explicitOnly || config.isMaxConnectionAgeGraceExplicitlySet) {
        setting("maxConnectionAgeGrace", explicitOnly) {
            maxConnectionAgeGrace(config.maxConnectionAgeGrace.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }
    return this
}

private inline fun ServerBuilder<*>.setting(name: String, wrapUnsupported: Boolean, set: () -> Unit) {
    try {
        set()
    } catch (e: UnsupportedOperationException) {
        if (!wrapUnsupported) {
            throw e
        }
        throw GrpcUnsupportedSettingException(
            "The grpc-java builder `${this::class.java.name}` doesn't support `$name`. " +
                "Remove `$name` from the configuration block.",
            e,
        )
    }
}

private fun io.grpc.Server.toKotlin(): PlatformServer {
    return object : PlatformServer {
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
