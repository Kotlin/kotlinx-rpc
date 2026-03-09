/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import io.grpc.Grpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.server.GrpcServerCredentials
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/**
 * Platform-specific gRPC server builder.
 */
public actual typealias ServerBuilder<T> = io.grpc.ServerBuilder<T>

@InternalRpcApi
public actual fun ServerBuilder(port: Int, credentials: GrpcServerCredentials?): ServerBuilder<*> {
    if (credentials != null) return Grpc.newServerBuilderForPort(port, credentials)
    return io.grpc.ServerBuilder.forPort(port)
}

@InternalRpcApi
public actual fun PlatformServer(builder: ServerBuilder<*>): PlatformServer {
    return builder.build().toKotlin()
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
