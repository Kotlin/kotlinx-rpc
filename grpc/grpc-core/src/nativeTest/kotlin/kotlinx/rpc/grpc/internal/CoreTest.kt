/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import HelloReply
import HelloReplyInternal
import HelloRequest
import HelloRequestInternal
import grpc.examples.echo.EchoRequest
import grpc.examples.echo.EchoRequestInternal
import grpc.examples.echo.EchoResponseInternal
import grpc.examples.echo.invoke
import invoke
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GrpcCoreTest {

    private fun descriptorFor(fullName: String = "helloworld.Greeter/SayHello"): MethodDescriptor<HelloRequest, HelloReply> =
        methodDescriptor(
            fullMethodName = fullName,
            requestCodec = HelloRequestInternal.CODEC,
            responseCodec = HelloReplyInternal.CODEC,
            type = MethodType.UNARY,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

    private fun ManagedChannel.newHelloCall(fullName: String = "helloworld.Greeter/SayHello"): ClientCall<HelloRequest, HelloReply> =
        platformApi.newCall(descriptorFor(fullName), GrpcCallOptions())

    private fun createChannel(): ManagedChannel = ManagedChannelBuilder("localhost:50051")
        .usePlaintext()
        .buildChannel()


    private fun helloReq(timeout: UInt = 0u): HelloRequest = HelloRequest {
        name = "world"
        this.timeout = timeout
    }

    private fun shutdownAndWait(channel: ManagedChannel) {
        channel.shutdown()
        runBlocking { channel.awaitTermination() }
    }

    @Test
    fun normalUnaryCall_ok() = repeat(1000) {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val req = helloReq()

        val statusDeferred = CompletableDeferred<Status>()
        val replyDeferred = CompletableDeferred<HelloReply>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onMessage(message: HelloReply) {
                replyDeferred.complete(message)
            }

            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }

        call.start(listener, GrpcTrailers())
        call.sendMessage(req)
        call.halfClose()
        call.request(1)

        runBlocking {
            withTimeout(10000) {
                val status = statusDeferred.await()
                val reply = replyDeferred.await()
                assertEquals(StatusCode.OK, status.statusCode)
                assertEquals("Hello world", reply.message)
            }
        }
        shutdownAndWait(channel)
    }

    @Test
    fun sendMessage_beforeStart_throws() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val req = helloReq()
        assertFailsWith<IllegalStateException> { call.sendMessage(req) }
        shutdownAndWait(channel)
    }

    @Test
    fun request_beforeStart_throws() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        assertFailsWith<IllegalStateException> { call.request(1) }
        shutdownAndWait(channel)
    }

    @Test
    fun start_twice_throws() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }
        call.start(listener, GrpcTrailers())
        assertFailsWith<IllegalStateException> { call.start(listener, GrpcTrailers()) }
        // cancel to finish the call quickly
        call.cancel("Double start test", null)
        runBlocking { withTimeout(5000) { statusDeferred.await() } }
        shutdownAndWait(channel)
    }

    @Test
    fun send_afterHalfClose_throws() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val req = helloReq()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }
        call.start(listener, GrpcTrailers())
        call.halfClose()
        assertFailsWith<IllegalStateException> { call.sendMessage(req) }
        // Ensure call completes
        call.cancel("cleanup", null)
        runBlocking { withTimeout(5000) { statusDeferred.await() } }
        shutdownAndWait(channel)
    }

    @Test
    fun request_zero_throws() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }
        call.start(listener, GrpcTrailers())
        assertFailsWith<IllegalStateException> { call.request(0) }
        call.cancel("cleanup", null)
        runBlocking { withTimeout(5000) { statusDeferred.await() } }
        shutdownAndWait(channel)
    }

    @Test
    fun cancel_afterStart_resultsInCancelledStatus() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }
        call.start(listener, GrpcTrailers())
        call.cancel("user cancel", null)
        runBlocking {
            withTimeout(10000) {
                val status = statusDeferred.await()
                assertEquals(StatusCode.CANCELLED, status.statusCode)
            }
        }
        shutdownAndWait(channel)
    }

    @Test
    fun invalid_method_returnsNonOkStatus() {
        val channel = createChannel()
        val call = channel.newHelloCall("/helloworld.Greeter/NoSuchMethod")
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }

        call.start(listener, GrpcTrailers())
        call.sendMessage(helloReq())
        call.halfClose()
        call.request(1)
        runBlocking {
            withTimeout(10000) {
                val status = statusDeferred.await()
                assertTrue(status.statusCode != StatusCode.OK)
            }
        }
        shutdownAndWait(channel)
    }


    @Test
    fun halfCloseBeforeSendingMessage_errorWithoutCrashing() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }
        assertFailsWith<IllegalStateException> {
            try {
                call.start(listener, GrpcTrailers())
                call.halfClose()
                call.sendMessage(helloReq())
            } finally {
                shutdownAndWait(channel)
            }
        }
    }

    @Test
    fun invokeStartAfterShutdown() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }

        channel.shutdown()
        call.start(listener, GrpcTrailers())
        call.sendMessage(helloReq())
        call.halfClose()
        call.request(1)

        runBlocking {
            withTimeout(10000) {
                val status = statusDeferred.await()
                assertEquals(StatusCode.UNAVAILABLE, status.statusCode)
            }
        }
    }

    @Test
    fun shutdownNowInMiddleOfCall() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = object : ClientCall.Listener<HelloReply>() {
            override fun onClose(status: Status, trailers: GrpcTrailers) {
                statusDeferred.complete(status)
            }
        }

        call.start(listener, GrpcTrailers())
        // set timeout on the server to 1000 ms, to simulate a long-running call
        call.sendMessage(helloReq(1000u))
        call.halfClose()
        call.request(1)

        runBlocking {
            delay(100)
            channel.shutdownNow()
            withTimeout(10000) {
                val status = statusDeferred.await()
                assertEquals(StatusCode.CANCELLED, status.statusCode)
            }
        }
    }

    @Test
    fun unaryCallTest() = runBlocking {
        val ch = createChannel()
        val desc = descriptorFor()
        val req = helloReq()
        repeat(1000) {
            val res = unaryRpc(ch.platformApi, desc, req)
            assertEquals("Hello world", res.message)
        }
    }


    private fun echoDescriptor(methodName: String, type: MethodType) =
        methodDescriptor(
            fullMethodName = "grpc.examples.echo.Echo/$methodName",
            requestCodec = EchoRequestInternal.CODEC,
            responseCodec = EchoResponseInternal.CODEC,
            type = type,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

    @Test
    fun unaryEchoTest() = runBlocking {
        val ch = createChannel()
        val desc = echoDescriptor("UnaryEcho", MethodType.UNARY)
        val req = EchoRequest { message = "Echoooo" }
        unaryRpc(ch.platformApi, desc, req)
        return@runBlocking
    }
}