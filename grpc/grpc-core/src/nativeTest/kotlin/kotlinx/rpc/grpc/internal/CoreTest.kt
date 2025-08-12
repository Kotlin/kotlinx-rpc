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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.*
import libgrpcpp_c.grpc_init
import libgrpcpp_c.grpc_shutdown
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test

class GrpcCoreTest {
    val GRPC_PROPAGATE_DEFAULTS = 0x0000FFFFu

    internal fun runHelloWorld(block: suspend (ManagedChannel, ClientCall<HelloRequest, HelloReply>) -> Unit) =
        runBlocking {
            grpc_init()

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
            val channel = NativeManagedChannel(
                "localhost:50051",
                GrpcInsecureCredentials(),
            )

            try {
                val call = channel.newCall(descriptor, GrpcCallOptions())
                block(channel, call)

            } finally {
                channel.shutdown()
                grpc_shutdown()
            }
        }

    @Test
    fun grpcClientTest() = runHelloWorld { channel, call ->
        val req = HelloRequest {
            name = "world"
            timeout = 0u
        }

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
        delay(1)
        call.request(1)

        channel.shutdown()

        withTimeout(10000) {
            val status = sem.await()
            val helloReply = helloReply.await()
            println("status: ${status.statusCode} (${status.getDescription()})")
            println("helloReply: $helloReply")
            assert(status.statusCode == StatusCode.OK)
            assert(helloReply.message == "Hello world")
        }
    }


    @Test
    fun testNormalOften() {
        for (i in 0..1000) {
            grpcClientTest()
        }
    }

}