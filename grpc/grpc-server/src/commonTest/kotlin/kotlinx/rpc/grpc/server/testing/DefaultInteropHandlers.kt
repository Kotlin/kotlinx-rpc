/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import com.google.protobuf.kotlin.Empty
import io.grpc.testing.integration.EchoStatus
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingInputCallResponse
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlin.time.Duration.Companion.microseconds

/** Default service behavior for the gRPC interoperability cases. */
internal object DefaultInteropHandlers {
    suspend fun emptyCall(@Suppress("UNUSED_PARAMETER") request: Empty): Empty = Empty {}

    suspend fun unaryCall(request: SimpleRequest): SimpleResponse {
        request.responseStatus.throwIfSet()
        return SimpleResponse { payload = zeros(request.responseSize) }
    }

    fun streamingOutputCall(request: StreamingOutputCallRequest): Flow<StreamingOutputCallResponse> = flow {
        for (parameter in request.responseParameters) {
            if (parameter.intervalUs > 0) {
                delay(parameter.intervalUs.microseconds)
            }
            emit(StreamingOutputCallResponse { payload = zeros(parameter.size) })
        }
        request.responseStatus.throwIfSet()
    }

    suspend fun streamingInputCall(requests: Flow<StreamingInputCallRequest>): StreamingInputCallResponse {
        var total = 0
        requests.collect { total += it.payload.body.size }
        return StreamingInputCallResponse { aggregatedPayloadSize = total }
    }

    fun fullDuplexCall(requests: Flow<StreamingOutputCallRequest>): Flow<StreamingOutputCallResponse> = flow {
        requests.collect { request ->
            request.responseStatus.throwIfSet()
            emitAll(streamingOutputCall(request))
        }
    }

    fun halfDuplexCall(requests: Flow<StreamingOutputCallRequest>): Flow<StreamingOutputCallResponse> = flow {
        emitAll(fullDuplexCall(requests.toList().asFlow()))
    }
}

internal fun zeros(size: Int): Payload = Payload { body = ByteString(*ByteArray(size)) }

internal fun EchoStatus.throwIfSet() {
    if (code == 0) {
        return
    }
    val statusCode = GrpcStatusCode.entries.first { it.value == code }
    throw GrpcStatus(statusCode, message).asException()
}
