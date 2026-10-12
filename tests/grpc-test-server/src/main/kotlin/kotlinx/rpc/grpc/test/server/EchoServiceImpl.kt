/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.StreamObserver
import kotlinx.rpc.grpc.test.EchoGrpc.EchoRequest
import kotlinx.rpc.grpc.test.EchoGrpc.EchoResponse
import kotlinx.rpc.grpc.test.EchoServiceGrpc

internal class EchoServiceImpl : EchoServiceGrpc.EchoServiceImplBase() {
    override fun unaryEcho(request: EchoRequest, responseObserver: StreamObserver<EchoResponse>) {
        sleep(request.timeout)
        responseObserver.onNext(response(request.message))
        responseObserver.onCompleted()
    }

    override fun serverStreamingEcho(request: EchoRequest, responseObserver: StreamObserver<EchoResponse>) {
        val count = if (request.hasServerStreamReps()) request.serverStreamReps else 5
        repeat(count) {
            responseObserver.onNext(response(request.message))
        }
        responseObserver.onCompleted()
    }

    override fun clientStreamingEcho(responseObserver: StreamObserver<EchoResponse>): StreamObserver<EchoRequest> {
        return object : StreamObserver<EchoRequest> {
            private val messages = mutableListOf<String>()

            override fun onNext(request: EchoRequest) {
                messages += request.message
            }

            override fun onError(error: Throwable) {
                responseObserver.onError(error)
            }

            override fun onCompleted() {
                responseObserver.onNext(response(messages.joinToString(", ")))
                responseObserver.onCompleted()
            }
        }
    }

    override fun bidirectionalStreamingEcho(
        responseObserver: StreamObserver<EchoResponse>,
    ): StreamObserver<EchoRequest> {
        return object : StreamObserver<EchoRequest> {
            override fun onNext(request: EchoRequest) {
                responseObserver.onNext(response(request.message))
            }

            override fun onError(error: Throwable) {
                responseObserver.onError(error)
            }

            override fun onCompleted() {
                responseObserver.onCompleted()
            }
        }
    }

    private fun response(message: String): EchoResponse {
        return EchoResponse.newBuilder().setMessage(message).build()
    }

    private fun sleep(timeoutMillis: Int) {
        if (timeoutMillis == 0) return
        Thread.sleep(Integer.toUnsignedLong(timeoutMillis))
    }
}
