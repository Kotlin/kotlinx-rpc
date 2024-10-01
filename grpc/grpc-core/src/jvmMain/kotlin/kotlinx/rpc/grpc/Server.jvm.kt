/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

public actual typealias ServerBuilder<T> = io.grpc.ServerBuilder<T>

internal actual fun ServerBuilder(port: Int): ServerBuilder<*> {
    return io.grpc.ServerBuilder.forPort(port)
}


internal actual fun Server(builder: ServerBuilder<*>): Server {
    return builder.build().toKotlin()
}

private fun io.grpc.Server.toKotlin(): Server {
    return object : Server {
        override val port: Int = this@toKotlin.port

        override val isShutdown: Boolean
            get() = this@toKotlin.isShutdown

        override val isTerminated: Boolean
            get() = this@toKotlin.isTerminated

        override fun start() : Server {
            this@toKotlin.start()
            return this
        }

        override fun shutdown(): Server {
            this@toKotlin.shutdown()
            return this
        }

        override fun shutdownNow(): Server {
            this@toKotlin.shutdownNow()
            return this
        }

        override suspend fun awaitTermination(duration: Duration): Server {
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
