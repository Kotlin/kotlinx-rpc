/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

internal data class ServerTarget(val port: Int) {
    val host: String = "localhost"

    override fun toString(): String = "$host:$port"
}
