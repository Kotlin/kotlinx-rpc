/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodDescriptor
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.bidiStreamingServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.clientStreamingServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.serverStreamingServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.unaryServerMethodDefinition
import kotlinx.rpc.test.combineFailures
import kotlin.reflect.typeOf
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Runs an adapter test and checks all calls for contract violations, active jobs, and unexpected failures.
 * The test controls scheduler execution through [AdapterTestFixture.runAdapterTasks].
 */
internal fun adapterTest(block: suspend (AdapterTestFixture) -> Unit): TestResult = runTest(timeout = TEST_TIMEOUT) {
    runAdapterTest(this, block)
}

internal suspend fun runAdapterTest(testScope: TestScope, block: suspend (AdapterTestFixture) -> Unit) {
    val fixture = AdapterTestFixture(testScope)
    val testFailure = runCatching { block(fixture) }.exceptionOrNull()
    val verificationFailure = runCatching { fixture.assertFinished() }.exceptionOrNull()
    combineFailures(testFailure, verificationFailure)?.let { throw it }
}

/**
 * Runs the adapter code in `suspendServerCalls.kt` against fake server calls.
 *
 * Each session queues an adapter coroutine on a [StandardTestDispatcher] that shares [TestScope.testScheduler].
 * Listener callbacks deliver transport events, such as requests or cancellation, and can queue work for that coroutine.
 * Use [runAdapterTasks] after callbacks to process the queued work before assertions.
 *
 * ```kotlin
 * adapterTest { fixture ->
 *     val session = fixture.bidiStreaming { it }
 *     fixture.runAdapterTasks() // Starts the adapter, which requests a message and suspends.
 *
 *     session.deliverRequest("request")
 *     fixture.runAdapterTasks() // Consumes the request, sends its response, and waits for another request.
 *
 *     session.listener.onHalfClose()
 *     fixture.runAdapterTasks() // Completes the request flow and closes the call.
 * }
 * ```
 */
internal class AdapterTestFixture(testScope: TestScope) {
    private val scheduler = testScope.testScheduler

    private val calls = mutableListOf<FakeServerCall<*, *>>()
    private val uncaught = mutableListOf<Throwable>()
    private var checkedUncaughtCount = 0

    val scope: CoroutineScope = CoroutineScope(
        StandardTestDispatcher(scheduler) +
            SupervisorJob(testScope.backgroundScope.coroutineContext[Job]) +
            CoroutineExceptionHandler { _, error -> uncaught += error },
    )

    fun unary(
        interceptors: List<GrpcServerInterceptor> = emptyList(),
        implementation: suspend (String) -> String,
    ): FakeCallSession<String> = start(GrpcMethodType.UNARY, stringMarshaller) { descriptor ->
        scope.unaryServerMethodDefinition(descriptor, typeOf<String>(), interceptors, implementation)
    }

    fun clientStreaming(
        interceptors: List<GrpcServerInterceptor> = emptyList(),
        implementation: suspend (Flow<String>) -> String,
    ): FakeCallSession<String> = start(GrpcMethodType.CLIENT_STREAMING, stringMarshaller) { descriptor ->
        scope.clientStreamingServerMethodDefinition(descriptor, typeOf<String>(), interceptors, implementation)
    }

    fun serverStreaming(
        interceptors: List<GrpcServerInterceptor> = emptyList(),
        implementation: (String) -> Flow<String>,
    ): FakeCallSession<String> = start(GrpcMethodType.SERVER_STREAMING, stringMarshaller) { descriptor ->
        scope.serverStreamingServerMethodDefinition(descriptor, typeOf<String>(), interceptors, implementation)
    }

    fun bidiStreaming(
        interceptors: List<GrpcServerInterceptor> = emptyList(),
        implementation: (Flow<String>) -> Flow<String>,
    ): FakeCallSession<String> = start(GrpcMethodType.BIDI_STREAMING, stringMarshaller) { descriptor ->
        scope.bidiStreamingServerMethodDefinition(descriptor, typeOf<String>(), interceptors, implementation)
    }

    fun unaryUnit(
        interceptors: List<GrpcServerInterceptor> = emptyList(),
        implementation: suspend (String) -> Unit,
    ): FakeCallSession<Unit> = start(GrpcMethodType.UNARY, unitMarshaller) { descriptor ->
        scope.unaryServerMethodDefinition(descriptor, typeOf<Unit>(), interceptors, implementation)
    }

    fun isIdle(): Boolean = scope.coroutineContext.job.children.none()

    /**
     * Marks current unhandled failures as expected after [assertions] succeeds.
     * Failures that arrive later still fail [assertFinished].
     */
    fun assertUncaught(assertions: (List<Throwable>) -> Unit) {
        val reportedCount = uncaught.size
        assertions(uncaught.drop(checkedUncaughtCount))
        checkedUncaughtCount = reportedCount
    }

    internal fun assertFinished() {
        calls.forEach { it.assertNoViolations() }
        assertTrue(isIdle(), "Adapter call jobs are still active")
        assertEquals(emptyList(), uncaught.drop(checkedUncaughtCount), "Unexpected uncaught adapter failures")
    }

    /**
     * Runs queued tasks at the current virtual time, including tasks that they queue, until no more tasks can run.
     * Adapter jobs can still wait for requests, readiness, or a future timer when this method returns.
     *
     * This runs the shared scheduler, so it can also run tasks from other sessions or test coroutines.
     * It doesn't advance virtual time.
     */
    fun runAdapterTasks() {
        scheduler.runCurrent()
    }

    private fun <Response> start(
        type: GrpcMethodType,
        responseMarshaller: GrpcMarshaller<Response>,
        definition: (GrpcMethodDescriptor<String, Response>) -> ServerMethodDefinition<String, Response>,
    ): FakeCallSession<Response> {
        val descriptor = methodDescriptor(
            fullMethodName = "adapter.Test/${type.name}",
            requestMarshaller = stringMarshaller,
            responseMarshaller = responseMarshaller,
            type = type,
            schemaDescriptor = null,
            idempotent = false,
            safe = false,
            sampledToLocalTracing = false,
        )
        val call = FakeServerCall(descriptor)
        calls += call
        return FakeCallSession(call, definition(descriptor))
    }
}

/** A fake call and the listener callbacks that drive its adapter job. */
internal class FakeCallSession<Response>(
    val call: FakeServerCall<String, Response>,
    definition: ServerMethodDefinition<String, Response>,
) {
    val listener = definition.getServerCallHandler().startCall(call, GrpcMetadata())

    /**
     * Delivers a request only when the adapter has granted credit.
     * Use [listener] directly for malformed call sequences.
     */
    fun deliverRequest(message: String) {
        call.consumeRequestCredit()
        listener.onMessage(message)
    }
}
