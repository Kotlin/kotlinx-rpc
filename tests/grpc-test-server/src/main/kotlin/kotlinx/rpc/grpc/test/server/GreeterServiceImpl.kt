/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.StreamObserver
import kotlinx.rpc.grpc.test.GreeterServiceGrpc
import kotlinx.rpc.grpc.test.HelloworldGrpc.HelloReply
import kotlinx.rpc.grpc.test.HelloworldGrpc.HelloRequest

internal class GreeterServiceImpl : GreeterServiceGrpc.GreeterServiceImplBase() {
    override fun sayHello(request: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
        if (request.hasTimeout() && request.timeout != 0) {
            Thread.sleep(Integer.toUnsignedLong(request.timeout))
        }
        responseObserver.onNext(
            HelloReply.newBuilder()
                .setMessage("Hello ${request.name}")
                .build()
        )
        responseObserver.onCompleted()
    }
}
