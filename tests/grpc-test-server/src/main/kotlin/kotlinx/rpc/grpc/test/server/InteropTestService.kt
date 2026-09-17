/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import grpc.testing.EmptyOuterClass.Empty
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.grpc.testing.integration.Messages.Payload
import io.grpc.testing.integration.Messages.SimpleRequest
import io.grpc.testing.integration.Messages.SimpleResponse
import io.grpc.testing.integration.TestServiceGrpc

// EmptyCall and UnaryCall follow grpc-java's v1.81.0 interop TestServiceImpl behavior:
// https://github.com/grpc/grpc-java/blob/v1.81.0/interop-testing/src/main/java/io/grpc/testing/integration/TestServiceImpl.java
internal class InteropTestService : TestServiceGrpc.TestServiceImplBase() {
    override fun emptyCall(request: Empty, responseObserver: StreamObserver<Empty>) {
        responseObserver.onNext(Empty.getDefaultInstance())
        responseObserver.onCompleted()
    }

    override fun unaryCall(request: SimpleRequest, responseObserver: StreamObserver<SimpleResponse>) {
        val response = SimpleResponse.newBuilder()
        if (request.responseSize != 0) {
            val body = if (request.responseSize > 0) {
                ByteString.copyFrom(ByteArray(request.responseSize))
            } else {
                ByteString.EMPTY
            }
            response.setPayload(Payload.newBuilder().setBody(body))
        }

        if (request.hasResponseStatus()) {
            responseObserver.onError(
                Status.fromCodeValue(request.responseStatus.code)
                    .withDescription(request.responseStatus.message)
                    .asRuntimeException()
            )
            return
        }

        responseObserver.onNext(response.build())
        responseObserver.onCompleted()
    }
}
