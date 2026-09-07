/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GrpcClientTargetTest {
    @Test
    fun parsesHostnameWithPort() {
        assertTarget("example.com:50051", "example.com", 50051)
    }

    @Test
    fun defaultsHostnamePortTo443() {
        assertTarget("example.com", "example.com", 443)
        assertTarget("localhost", "localhost", 443)
    }

    @Test
    fun preservesHostnameSpelling() {
        assertTarget("Example.COM.:8443", "Example.COM.", 8443)
        assertTarget("my-service.internal", "my-service.internal", 443)
    }

    @Test
    fun parsesIpv4() {
        assertTarget("127.0.0.1:50051", "127.0.0.1", 50051)
        assertTarget("127.0.0.1", "127.0.0.1", 443)
    }

    @Test
    fun parsesBracketedIpv6AndRemovesBrackets() {
        val hosts = listOf("::1", "::", "2001:db8::1", "2001:DB8:0:1:2:3:4:5", "2001:db8:1::")
        for (host in hosts) {
            assertTarget("[$host]:50051", host, 50051)
            assertTarget("[$host]", host, 443)
        }
    }

    @Test
    fun parsesIpv6WithIpv4Tail() {
        for (host in listOf("::ffff:192.0.2.1", "1:2:3:4:5:6:192.0.2.1", "::192.0.2.1")) {
            assertTarget("[$host]:50051", host, 50051)
            assertTarget("[$host]", host, 443)
        }
    }

    @Test
    fun leavesBracketedAddressValidationToTransport() {
        assertTarget("[fe80::1%en0]:50051", "fe80::1%en0", 50051)
        assertTarget("[custom-address]", "custom-address", 443)
    }

    @Test
    fun acceptsPortBoundariesAndLeadingZeros() {
        for (host in listOf("example.com", "127.0.0.1", "[::1]")) {
            val expectedHost = host.removeSurrounding("[", "]")
            assertTarget("$host:1", expectedHost, 1)
            assertTarget("$host:65535", expectedHost, 65535)
            assertTarget("$host:00443", expectedHost, 443)
        }
    }

    @Test
    fun rejectsBlankTargets() {
        for (target in listOf("", " ", "\t", "\n\r")) {
            assertInvalid(target, "blank")
        }
    }

    @Test
    fun rejectsMissingHosts() {
        for (endpoint in listOf(":50051", ":", "[]", "[]:50051")) {
            assertInvalidEndpoint(endpoint, "missing a host")
        }
        for (target in listOf("dns:///", "DNS:///", "dns://")) {
            assertInvalid(target, "missing a host")
        }
    }

    @Test
    fun rejectsEmptyAndNonNumericPorts() {
        val ports = listOf("", "abc", "443x", "+443", "-1", "1.5", "1e3", "４４３", "٤٤٣")
        for (host in listOf("example.com", "127.0.0.1", "[::1]")) {
            for (port in ports) {
                assertInvalidEndpoint("$host:$port", "decimal digits")
            }
        }
    }

    @Test
    fun rejectsPortsOutsideRangeAndIntegerOverflow() {
        for (host in listOf("example.com", "127.0.0.1", "[::1]")) {
            for (port in listOf("0", "000", "65536", "2147483648", "999999999999999999999999")) {
                assertInvalidEndpoint("$host:$port", "1..65535")
            }
        }
    }

    @Test
    fun rejectsUnbracketedIpv6() {
        for (endpoint in listOf("::1", "::1:50051", "2001:db8::1", "1:2:3:4:5:6:7:8", "::")) {
            assertInvalidEndpoint(endpoint, "enclosed in brackets")
        }
    }

    @Test
    fun rejectsMalformedBrackets() {
        val endpoints = listOf("[::1", "[::1:50051", "::1]", "[[::1]]", "[::1]]:50051", "a[::1]", "[::1]:[443]")
        for (endpoint in endpoints) {
            assertInvalidEndpoint(endpoint, "Malformed IPv6 brackets")
        }
    }

    @Test
    fun rejectsTextAfterIpv6Brackets() {
        for (endpoint in listOf("[::1]50051", "[::1]text", "[::1]/path", "[::1](50051)")) {
            assertInvalidEndpoint(endpoint, "after IPv6 brackets")
        }
        assertInvalidEndpoint("[::1]:443:50051", "decimal digits")
    }

    @Test
    fun rejectsUnsupportedSchemes() {
        for (target in listOf("http://example.com", "https://example.com:443", "unix:///tmp/socket", "HTTP://host")) {
            assertInvalid(target, "Unsupported gRPC target scheme")
        }
    }

    @Test
    fun rejectsDnsAuthorities() {
        for (authority in listOf("resolver", "resolver:53", "127.0.0.1", "[::1]:53", "user@resolver")) {
            for (scheme in listOf("dns", "DNS", "DnS")) {
                assertInvalid("$scheme://$authority/example.com:50051", "DNS authority must be empty")
            }
        }
        assertInvalid("dns://example.com", "DNS authority must be empty")
    }

    @Test
    fun rejectsMalformedDnsPrefixes() {
        for (target in listOf("dns:/example.com", "dns:example.com", "dns:////example.com")) {
            assertInvalid(target)
        }
    }

    @Test
    fun rejectsWhitespaceAndControlCharacters() {
        val endpoints = listOf(
            " example.com", "example.com ", "exam ple.com", "example.com:\t443", "[::1]\n", "host\u0000",
        )
        for (endpoint in endpoints) {
            assertInvalidEndpoint(endpoint, "whitespace or control characters")
        }
    }

    @Test
    fun rejectsUriComponentsAndEscapedHosts() {
        val endpoints = listOf(
            "/", "/example.com", "example.com/", "example.com/path", "user@example.com", "example.com?query",
            "example.com#fragment", "example.com\\path", "example%2ecom", "example.com:443/path",
            "example.com:443?query", "example.com:443#fragment", "[::1]:443/path", "[::1]?query",
        )
        for (endpoint in endpoints) {
            assertInvalidEndpoint(endpoint)
        }
    }

    private fun assertTarget(endpoint: String, host: String, port: Int) {
        for (prefix in listOf("", "dns:///", "DNS:///", "DnS:///")) {
            val target = prefix + endpoint
            assertEquals(GrpcClientTarget(host, port), GrpcClientTarget.parse(target), target)
        }
    }

    private fun assertInvalidEndpoint(endpoint: String, message: String = "") {
        for (prefix in listOf("", "dns:///", "DNS:///")) {
            assertInvalid(prefix + endpoint, message)
        }
    }

    private fun assertInvalid(target: String, message: String = "") {
        val exception = assertFailsWith<IllegalArgumentException>(target) { GrpcClientTarget.parse(target) }
        val actualMessage = exception.message.orEmpty()
        assertTrue(actualMessage.isNotBlank(), "Expected a useful error message for '$target'")
        assertTrue(message in actualMessage, "Expected '$message' for '$target', got '$actualMessage'")
    }
}
