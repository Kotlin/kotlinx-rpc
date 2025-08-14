/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal


import HelloReply
import HelloReplyInternal
import HelloRequest
import HelloRequestInternal
import invoke
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test

class GrpcCoreTest {
    val GRPC_PROPAGATE_DEFAULTS = 0x0000FFFFu

    private fun NativeManagedChannel.helloWorldCall(): ClientCall<HelloRequest, HelloReply> {
        val fullName = "/helloworld.Greeter/SayHello"
        val descriptor = methodDescriptor(
            fullMethodName = fullName,
            requestCodec = HelloRequestInternal.CODEC,
            responseCodec = HelloReplyInternal.CODEC,
            type = MethodType.UNARY,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

        return newCall(descriptor, GrpcCallOptions())
    }

    private fun createChannel(): NativeManagedChannel {
        return NativeManagedChannel(
            "localhost:50051",
            GrpcInsecureCredentials(),
        )
    }

    private fun helloReq(timeout: UInt = 0u): HelloRequest {
        return HelloRequest {
            name = "world"
            this.timeout = timeout
        }
    }

    private fun shutdownAndWait(channel: NativeManagedChannel) {
        channel.shutdown()
        runBlocking {
            channel.awaitTermination()
        }
    }


    @Test
    fun normalUnaryCallTest() = repeat(1000) {
        val channel = createChannel()
        val call = channel.helloWorldCall()
        val req = helloReq()

        val sem = CompletableDeferred<Status>()
        val helloReply = CompletableDeferred<HelloReply>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onMessage(message: HelloReply) {
                helloReply.complete(message)
            }

            override fun onClose(status: Status, trailers: GrpcTrailers) {
                sem.complete(status)
            }
        }

        call.start(listener, GrpcTrailers())
        call.sendMessage(req)
        call.halfClose()
        call.request(1)

        runBlocking {
            withTimeout(10000) {
                val status = sem.await()
                val helloReply = helloReply.await()
                assert(status.statusCode == StatusCode.OK)
                assert(helloReply.message == "Hello world")
            }
        }

        shutdownAndWait(channel)
    }

}