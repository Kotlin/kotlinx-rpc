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
import kotlinx.coroutines.test.TestScope
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
import kotlin.reflect.typeOf

/** Runs real adapter code on the test scheduler with a fake server call. */
internal class AdapterHarness(testScope: TestScope) {
    val uncaught: MutableList<Throwable> = mutableListOf()

    val scope: CoroutineScope = CoroutineScope(
        StandardTestDispatcher(testScope.testScheduler) +
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
        return FakeCallSession(call, definition(descriptor))
    }
}

/** A fake call and the listener callbacks that drive its adapter job. */
internal class FakeCallSession<Response>(
    val call: FakeServerCall<String, Response>,
    definition: ServerMethodDefinition<String, Response>,
) {
    val listener = definition.getServerCallHandler().startCall(call, GrpcMetadata())
}
