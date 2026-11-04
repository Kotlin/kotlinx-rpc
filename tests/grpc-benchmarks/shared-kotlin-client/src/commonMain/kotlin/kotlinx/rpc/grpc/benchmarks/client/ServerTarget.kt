/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

internal data class ServerTarget(val host: String, val port: Int) {
    // The same format as the Swift benchmark client, so every client accepts and prints one target value.
    override fun toString(): String = if (':' in host) "[$host]:$port" else "$host:$port"

    companion object {
        fun parse(value: String): ServerTarget {
            val host: String
            val portText: String
            if (value.startsWith("[")) {
                val closingBracket = value.indexOf(']')
                require(closingBracket > 0 && value.getOrNull(closingBracket + 1) == ':') {
                    "Invalid target '$value'. Expected [IPv6]:PORT."
                }
                host = value.substring(1, closingBracket)
                portText = value.substring(closingBracket + 2)
            } else {
                val colon = value.lastIndexOf(':')
                require(colon >= 0) {
                    "Invalid target '$value'. Expected HOST:PORT."
                }
                host = value.substring(0, colon)
                portText = value.substring(colon + 1)
            }
            val port = portText.toIntOrNull()
            require(host.isNotEmpty() && port != null && port in 1..65_535) {
                "Invalid target '$value'. Expected HOST:PORT with a valid port."
            }
            return ServerTarget(host, port)
        }
    }
}
