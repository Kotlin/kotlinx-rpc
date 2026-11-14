/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.Attributes
import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.SecurityLevel
import io.grpc.Status
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcMetadata
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.concurrent.Executor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GrpcCallCredentialsTest {
    @Test
    fun alreadyCancelledCallCompletesMetadataApplier() = runTest {
        val parent = Job().apply { cancel() }
        val credentials = GrpcEmptyCallCredentials.toJvm(StandardTestDispatcher(testScheduler) + parent)
        val applier = RecordingApplier()

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        runCurrent()

        assertEquals(1, applier.applied.size)
        assertTrue(applier.failures.isEmpty())
    }

    @Test
    fun cancellationBeforeQueuedMetadataStartsCompletesApplier() = runTest {
        val parent = Job()
        val credentials = GrpcEmptyCallCredentials.toJvm(StandardTestDispatcher(testScheduler) + parent)
        val applier = RecordingApplier()

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        parent.cancel()
        runCurrent()

        assertEquals(1, applier.applied.size)
        assertTrue(applier.failures.isEmpty())
    }

    @Test
    fun alreadyCancelledCallWithSuspendingMetadataFailsApplierOnce() = runTest {
        val parent = Job().apply { cancel() }
        val source = TestCredentials { awaitCancellation() }
        val credentials = source.toJvm(StandardTestDispatcher(testScheduler) + parent)
        val applier = RecordingApplier()

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        runCurrent()

        assertTrue(applier.applied.isEmpty())
        assertEquals(Status.Code.UNAVAILABLE, applier.failures.single().code)
    }

    @Test
    fun cancellationDuringMetadataLookupFailsApplierOnce() = runTest {
        val parent = Job()
        val source = TestCredentials { awaitCancellation() }
        val credentials = source.toJvm(StandardTestDispatcher(testScheduler) + parent)
        val applier = RecordingApplier()

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        runCurrent()
        parent.cancel()
        runCurrent()

        assertTrue(applier.applied.isEmpty())
        assertEquals(Status.Code.UNAVAILABLE, applier.failures.single().code)
    }

    @Test
    fun providerFailureCompletesApplierWithUnavailable() = runTest {
        val cause = IllegalArgumentException("metadata lookup failed")
        val source = TestCredentials { throw cause }
        val credentials = source.toJvm(StandardTestDispatcher(testScheduler))
        val applier = RecordingApplier()

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        runCurrent()

        assertTrue(applier.applied.isEmpty())
        assertEquals(Status.Code.UNAVAILABLE, applier.failures.single().code)
        assertSame(cause, applier.failures.single().cause)
    }

    @Test
    fun insecureTransportCompletesApplierWithUnauthenticated() = runTest {
        val source = TestCredentials(requiresTransportSecurity = true) {
            error("The provider must not run on an insecure transport.")
        }
        val credentials = source.toJvm(StandardTestDispatcher(testScheduler))
        val applier = RecordingApplier()

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        runCurrent()

        assertTrue(applier.applied.isEmpty())
        assertEquals(Status.Code.UNAUTHENTICATED, applier.failures.single().code)
    }

    @Test
    fun applierExceptionDoesNotInvokeFailureCallback() = runTest {
        val cause = IllegalStateException("applier failed")
        var observed: Throwable? = null
        val handler = CoroutineExceptionHandler { _, error -> observed = error }
        val parent = SupervisorJob()
        val credentials = GrpcEmptyCallCredentials.toJvm(StandardTestDispatcher(testScheduler) + parent + handler)
        val applier = RecordingApplier { throw cause }

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        runCurrent()
        parent.cancel()

        assertEquals(1, applier.applied.size)
        assertTrue(applier.failures.isEmpty())
        assertSame(cause, observed)
    }

    @Test
    fun providerErrorFailsApplierAndPropagatesError() = runTest {
        val cause = AssertionError("metadata provider failed")
        var observed: Throwable? = null
        val handler = CoroutineExceptionHandler { _, error -> observed = error }
        val parent = SupervisorJob()
        val source = TestCredentials { throw cause }
        val credentials = source.toJvm(StandardTestDispatcher(testScheduler) + parent + handler)
        val applier = RecordingApplier()

        credentials.applyRequestMetadata(requestInfo, directExecutor, applier)
        runCurrent()
        parent.cancel()

        assertTrue(applier.applied.isEmpty())
        assertEquals(Status.Code.UNAVAILABLE, applier.failures.single().code)
        assertSame(cause, applier.failures.single().cause)
        assertSame(cause, observed)
    }

    private class TestCredentials(
        override val requiresTransportSecurity: Boolean = false,
        private val lookup: suspend GrpcCallCredentials.Context.() -> GrpcMetadata,
    ) : GrpcCallCredentials {
        override suspend fun GrpcCallCredentials.Context.getRequestMetadata(): GrpcMetadata = lookup()
    }

    private class RecordingApplier(private val onApply: () -> Unit = {}) : CallCredentials.MetadataApplier() {
        val applied: MutableList<Metadata> = mutableListOf()
        val failures: MutableList<Status> = mutableListOf()

        override fun apply(headers: Metadata) {
            applied += headers
            onApply()
        }

        override fun fail(status: Status) {
            failures += status
        }
    }

    private companion object {
        val directExecutor: Executor = Executor { it.run() }
        val marshaller: MethodDescriptor.Marshaller<Unit> = object : MethodDescriptor.Marshaller<Unit> {
            override fun stream(value: Unit): InputStream = ByteArrayInputStream(byteArrayOf())

            override fun parse(stream: InputStream): Unit = Unit
        }
        val descriptor: MethodDescriptor<Unit, Unit> = MethodDescriptor.newBuilder<Unit, Unit>()
            .setType(MethodDescriptor.MethodType.UNARY)
            .setFullMethodName("example.Service/Call")
            .setRequestMarshaller(marshaller)
            .setResponseMarshaller(marshaller)
            .build()
        val requestInfo: CallCredentials.RequestInfo = object : CallCredentials.RequestInfo() {
            override fun getMethodDescriptor(): MethodDescriptor<*, *> = descriptor

            override fun getSecurityLevel(): SecurityLevel = SecurityLevel.NONE

            override fun getAuthority(): String = "localhost"

            override fun getTransportAttrs(): Attributes = Attributes.EMPTY
        }
    }
}
