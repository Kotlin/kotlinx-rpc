/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import com.google.protobuf.kotlin.Empty
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingInputCallResponse
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import io.grpc.testing.integration.TestService
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.GrpcStatusCode

/** Per-method handlers that tests can replace before the server starts. */
internal class ScriptedTestServiceBuilder {
    var emptyCall: suspend (Empty) -> Empty = DefaultInteropHandlers::emptyCall
    var unaryCall: suspend (SimpleRequest) -> SimpleResponse = DefaultInteropHandlers::unaryCall
    var cacheableUnaryCall: suspend (SimpleRequest) -> SimpleResponse = DefaultInteropHandlers::unaryCall
    var streamingOutputCall: (StreamingOutputCallRequest) -> Flow<StreamingOutputCallResponse> =
        DefaultInteropHandlers::streamingOutputCall
    var streamingInputCall: suspend (Flow<StreamingInputCallRequest>) -> StreamingInputCallResponse =
        DefaultInteropHandlers::streamingInputCall
    var fullDuplexCall: (Flow<StreamingOutputCallRequest>) -> Flow<StreamingOutputCallResponse> =
        DefaultInteropHandlers::fullDuplexCall
    var halfDuplexCall: (Flow<StreamingOutputCallRequest>) -> Flow<StreamingOutputCallResponse> =
        DefaultInteropHandlers::halfDuplexCall
}

/** Hosts the interop service with the handlers selected by each test. */
internal class ScriptedTestService(private val handlers: ScriptedTestServiceBuilder) : TestService {
    override suspend fun emptyCall(message: Empty): Empty = handlers.emptyCall(message)

    override suspend fun unaryCall(message: SimpleRequest): SimpleResponse = handlers.unaryCall(message)

    override suspend fun cacheableUnaryCall(message: SimpleRequest): SimpleResponse =
        handlers.cacheableUnaryCall(message)

    override fun streamingOutputCall(message: StreamingOutputCallRequest): Flow<StreamingOutputCallResponse> =
        handlers.streamingOutputCall(message)

    override suspend fun streamingInputCall(message: Flow<StreamingInputCallRequest>): StreamingInputCallResponse =
        handlers.streamingInputCall(message)

    override fun fullDuplexCall(message: Flow<StreamingOutputCallRequest>): Flow<StreamingOutputCallResponse> =
        handlers.fullDuplexCall(message)

    override fun halfDuplexCall(message: Flow<StreamingOutputCallRequest>): Flow<StreamingOutputCallResponse> =
        handlers.halfDuplexCall(message)

    override suspend fun unimplementedCall(message: Empty): Empty = throw GrpcStatusCode.UNIMPLEMENTED.asException()
}
