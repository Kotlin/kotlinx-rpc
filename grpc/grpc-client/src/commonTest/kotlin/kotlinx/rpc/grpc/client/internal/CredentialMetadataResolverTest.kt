/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.asException
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame

class CredentialMetadataResolverTest {
    @Test
    fun returnsMetadataWithRequestContext() = runTest {
        val metadata = GrpcMetadata()
        val credentials = TestCredentials {
            assertEquals("example.test:443", authority)
            assertEquals("example.Service/Call", methodName)
            metadata
        }

        assertSame(metadata, credentials.resolveRequestMetadata("example.test:443", "example.Service/Call", true))
    }

    @Test
    fun rejectsInsecureTransportBeforeCallingProvider() = runTest {
        var called = false
        val credentials = TestCredentials(requiresTransportSecurity = true) {
            called = true
            GrpcMetadata()
        }

        val failure = assertFailsWith<GrpcStatusException> {
            credentials.resolveRequestMetadata("localhost", "example.Service/Call", false)
        }

        assertEquals(GrpcStatusCode.UNAUTHENTICATED, failure.status.statusCode)
        assertFalse(called)
    }

    @Test
    fun wrapsProviderFailureWithUnavailableStatus() = runTest {
        val cause = IllegalArgumentException("metadata lookup failed")
        val credentials = TestCredentials { throw cause }

        val failure = assertFailsWith<GrpcStatusException> {
            credentials.resolveRequestMetadata("localhost", "example.Service/Call", false)
        }

        assertEquals(GrpcStatusCode.UNAVAILABLE, failure.status.statusCode)
        assertEquals(
            "Getting metadata from call credentials failed with error: ${cause.message}",
            failure.status.description,
        )
        assertSame(cause, failure.cause)
    }

    @Test
    fun mapsProviderStatusExceptionToUnavailable() = runTest {
        val cause = GrpcStatusCode.PERMISSION_DENIED.asException("provider status")
        val credentials = TestCredentials { throw cause }

        val failure = assertFailsWith<GrpcStatusException> {
            credentials.resolveRequestMetadata("localhost", "example.Service/Call", false)
        }

        assertEquals(GrpcStatusCode.UNAVAILABLE, failure.status.statusCode)
        assertSame(cause, failure.cause)
    }

    @Test
    fun propagatesCancellationUnchanged() = runTest {
        val cause = CancellationException("metadata lookup cancelled")
        val credentials = TestCredentials { throw cause }

        val failure = assertFailsWith<CancellationException> {
            credentials.resolveRequestMetadata("localhost", "example.Service/Call", false)
        }

        assertSame(cause, failure)
    }

    @Test
    fun propagatesErrorUnchanged() = runTest {
        val cause = AssertionError("metadata provider failed")
        val credentials = TestCredentials { throw cause }

        val failure = assertFailsWith<AssertionError> {
            credentials.resolveRequestMetadata("localhost", "example.Service/Call", false)
        }

        assertSame(cause, failure)
    }

    private class TestCredentials(
        override val requiresTransportSecurity: Boolean = false,
        private val lookup: suspend GrpcCallCredentials.Context.() -> GrpcMetadata,
    ) : GrpcCallCredentials {
        override suspend fun GrpcCallCredentials.Context.getRequestMetadata(): GrpcMetadata = lookup()
    }
}
