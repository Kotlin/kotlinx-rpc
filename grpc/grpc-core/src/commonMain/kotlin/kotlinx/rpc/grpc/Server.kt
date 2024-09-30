/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlin.time.Duration

public interface Server {
    public val port: Int
    public val isShutdown: Boolean
    public val isTerminated: Boolean

    public fun start() : Server
    public fun shutdown() : Server
    public fun shutdownNow() : Server
    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE) : Server
}

internal expect fun Server(builder: ServerBuilder<*>): Server
