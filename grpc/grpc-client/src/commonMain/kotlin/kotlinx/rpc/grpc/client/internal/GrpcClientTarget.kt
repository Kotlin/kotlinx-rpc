/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

internal data class GrpcClientTarget(val host: String, val port: Int) {
    companion object
}

/**
 * Parses `host[:port]` or `[IPv6][:port]`, optionally prefixed with `dns:///` (case-insensitive).
 * DNS authorities and other URI components are unsupported. The default port is 443, and IPv6
 * brackets are removed from the returned host for use with grpc-swift-2. Host and address validity
 * is left to the platform transport.
 */
internal fun GrpcClientTarget.Companion.parse(target: String): GrpcClientTarget {
    require(target.isNotBlank()) { "gRPC target must not be blank" }
    require(target.none { it.isWhitespace() || it.isISOControl() }) {
        "gRPC target must not contain whitespace or control characters"
    }

    val schemeEnd = target.indexOf("://")
    val endpoint = if (schemeEnd < 0) {
        target
    } else {
        val scheme = target.substring(0, schemeEnd)
        require(scheme.equals("dns", ignoreCase = true)) {
            "Unsupported gRPC target scheme '$scheme'; only dns:/// is supported"
        }
        val path = target.substring(schemeEnd + 3)
        require(path.isNotEmpty()) { "gRPC target is missing a host; use dns:///host[:port]" }
        require(path.startsWith('/')) {
            "DNS authority must be empty; use dns:///host[:port]"
        }
        path.substring(1)
    }

    require(endpoint.isNotEmpty()) { "gRPC target is missing a host" }
    return if (endpoint.startsWith('[')) {
        parseBracketedTarget(endpoint)
    } else {
        parseUnbracketedTarget(endpoint)
    }
}

private fun parseBracketedTarget(endpoint: String): GrpcClientTarget {
    val closingBracket = endpoint.indexOf(']')
    require(
        closingBracket >= 0 && endpoint.lastIndexOf('[') == 0 && endpoint.lastIndexOf(']') == closingBracket
    ) { "Malformed IPv6 brackets in gRPC target; use [IPv6][:port]" }

    val host = endpoint.substring(1, closingBracket)
    require(host.isNotEmpty()) { "gRPC target is missing a host inside IPv6 brackets" }

    val suffix = endpoint.substring(closingBracket + 1)
    require(suffix.isEmpty() || suffix.startsWith(':')) {
        "Unexpected text after IPv6 brackets; expected [IPv6][:port]"
    }
    return GrpcClientTarget(host, if (suffix.isEmpty()) 443 else parseTargetPort(suffix.substring(1)))
}

private fun parseUnbracketedTarget(endpoint: String): GrpcClientTarget {
    require('[' !in endpoint && ']' !in endpoint) {
        "Malformed IPv6 brackets in gRPC target; use [IPv6][:port]"
    }
    val colon = endpoint.indexOf(':')
    require(colon == endpoint.lastIndexOf(':')) {
        "IPv6 addresses must be enclosed in brackets; use [IPv6][:port]"
    }

    val host = if (colon < 0) endpoint else endpoint.substring(0, colon)
    require(host.isNotEmpty()) { "gRPC target is missing a host" }
    require(host.all { it.isLetterOrDigit() || it in ".-_" }) {
        "Invalid gRPC target host '$host'; expected a hostname or IPv4 address without URI components"
    }
    return GrpcClientTarget(host, if (colon < 0) 443 else parseTargetPort(endpoint.substring(colon + 1)))
}

private fun parseTargetPort(port: String): Int {
    require(port.isNotEmpty() && port.all { it in '0'..'9' }) {
        "gRPC target port must contain only decimal digits, but was '$port'"
    }
    val value = port.toIntOrNull()
    require(value != null && value in 1..65535) {
        "gRPC target port must be in 1..65535, but was '$port'"
    }
    return value
}
