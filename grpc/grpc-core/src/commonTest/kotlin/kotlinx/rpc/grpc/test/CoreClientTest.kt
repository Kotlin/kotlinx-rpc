/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package kotlinx.rpc.grpc.test

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.client.internal.ClientCall
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcInsecureClientCredentials
import kotlinx.rpc.grpc.client.internal.ManagedChannel
import kotlinx.rpc.grpc.client.internal.ManagedChannelBuilder
import kotlinx.rpc.grpc.client.internal.buildChannel
import kotlinx.rpc.grpc.client.internal.clientCallListener
import kotlinx.rpc.grpc.client.internal.createCall
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.grpc.descriptor.MethodType
import kotlinx.rpc.grpc.descriptor.methodDescriptor
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.registerService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

private const val PORT = 50051

/**
 * Client tests that use lower level API directly to test that it behaves correctly.
 * Before executing the tests run [GreeterServiceImpl.runServer] on JVM.
 */
// TODO: Start external service server automatically (KRPC-208)
class GrpcCoreClientTest {

    private fun descriptorFor(fullName: String = "kotlinx.rpc.grpc.test.GreeterService/SayHello"): MethodDescriptor<HelloRequest, HelloReply> =
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

    private fun ManagedChannel.newHelloCall(fullName: String = "kotlinx.rpc.grpc.test.GreeterService/SayHello"): ClientCall<HelloRequest, HelloReply> =
        platformApi.createCall(descriptorFor(fullName), GrpcCallOptions())

    private fun createChannel(): ManagedChannel = ManagedChannelBuilder(
        target = "localhost:$PORT",
        credentials = GrpcInsecureClientCredentials()
    ).buildChannel()


    private fun helloReq(timeout: UInt = 0u): HelloRequest = HelloRequest {
        name = "world"
        this.timeout = timeout
    }

    private fun shutdownAndWait(channel: ManagedChannel, now: Boolean = false) {
        if (now) {
            channel.shutdownNow()
        } else {
            channel.shutdown()
        }
        runBlocking { channel.awaitTermination() }
    }

    @Test
    fun normalUnaryCall_ok() = repeat(1000) {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val req = helloReq()

        val statusDeferred = CompletableDeferred<Status>()
        val replyDeferred = CompletableDeferred<HelloReply>()
        val listener = createClientCallListener<HelloReply>(
            onMessage = { replyDeferred.complete(it) },
            onClose = { status, _ -> statusDeferred.complete(status) }
        )

        call.start(listener, GrpcMetadata())
        call.sendMessage(req)
        call.halfClose()
        call.request(1)

        runBlocking {
            withTimeout(10000) {
                val status = statusDeferred.await()
                assertEquals(StatusCode.OK, status.statusCode)
                val reply = replyDeferred.await()
                assertEquals("Hello world", reply.message)
            }
        }
        shutdownAndWait(channel)
    }

    @Test
    fun start_twice_throws() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = createClientCallListener<HelloReply>(
            onClose = { status, _ -> statusDeferred.complete(status) }
        )
        call.start(listener, GrpcMetadata())
        assertFailsWith<IllegalStateException> { call.start(listener, GrpcMetadata()) }
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
        val listener = createClientCallListener<HelloReply>(
            onClose = { status, _ -> statusDeferred.complete(status) }
        )
        call.start(listener, GrpcMetadata())
        call.halfClose()
        assertFailsWith<IllegalStateException> { call.sendMessage(req) }
        // Ensure call completes
        call.cancel("cleanup", null)
        runBlocking { withTimeout(5000) { statusDeferred.await() } }
        shutdownAndWait(channel)
    }

    @Test
    fun request_negative_throws() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = createClientCallListener<HelloReply>(
            onClose = { status, _ -> statusDeferred.complete(status) }
        )
        call.start(listener, GrpcMetadata())
        assertFails { call.request(-1) }
        call.cancel("cleanup", null)
        runBlocking { withTimeout(5000) { statusDeferred.await() } }
        shutdownAndWait(channel)
    }

    @Test
    fun cancel_afterStart_resultsInCancelledStatus() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val statusDeferred = CompletableDeferred<Status>()
        val listener = createClientCallListener<HelloReply>(
            onClose = { status, _ -> statusDeferred.complete(status) }
        )
        call.start(listener, GrpcMetadata())
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
        val call = channel.newHelloCall("kotlinx.rpc.grpc.test.Greeter/NoSuchMethod")
        val statusDeferred = CompletableDeferred<Status>()
        val listener = createClientCallListener<HelloReply>(
            onClose = { status, _ -> statusDeferred.complete(status) }
        )

        call.start(listener, GrpcMetadata())
        call.sendMessage(helloReq())
        call.halfClose()
        call.request(1)
        runBlocking {
            withTimeout(10000) {
                val status = statusDeferred.await()
                assertEquals(StatusCode.UNIMPLEMENTED, status.statusCode)
            }
        }
        shutdownAndWait(channel)
    }


    @Test
    fun halfCloseBeforeSendingMessage_errorWithoutCrashing() {
        val channel = createChannel()
        val call = channel.newHelloCall()
        val listener = createClientCallListener<HelloReply>()
        assertFailsWith<IllegalStateException> {
            try {
                call.start(listener, GrpcMetadata())
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
        val listener = createClientCallListener<HelloReply>(
            onClose = { status, _ -> statusDeferred.complete(status) }
        )

        channel.shutdown()
        runBlocking { channel.awaitTermination() }
        call.start(listener, GrpcMetadata())
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
        val listener = createClientCallListener<HelloReply>(
            onClose = { status, _ -> statusDeferred.complete(status) }
        )

        call.start(listener, GrpcMetadata())
        // set timeout on the server to 1000 ms, to simulate a long-running call
        call.sendMessage(helloReq(1000u))
        call.halfClose()
        call.request(1)

        runBlocking {
            delay(100)
            channel.shutdownNow()
            withTimeout(10000) {
                val status = statusDeferred.await()
                assertEquals(StatusCode.UNAVAILABLE, status.statusCode)
            }
        }
    }
}

class GreeterServiceImpl : GreeterService {

    override suspend fun SayHello(message: HelloRequest): HelloReply {
        delay(message.timeout?.toLong() ?: 0)
        return HelloReply {
            this.message = "Hello ${message.name}"
        }
    }


    /**
     * Run this on JVM before executing tests.
     */
    @Test
    fun runServer() = runTest {
        val server = GrpcServer(
            port = PORT,
        ) {
            services {
                registerService<GreeterService> { GreeterServiceImpl() }
            }
        }

        try {
            server.start()
            println("Server started")
            server.awaitTermination()
        } finally {
            server.shutdown()
            server.awaitTermination()

        }
    }

}


private fun <T> createClientCallListener(
    onHeaders: (headers: GrpcMetadata) -> Unit = {},
    onMessage: (message: T) -> Unit = {},
    onClose: (status: Status, trailers: GrpcMetadata) -> Unit = { _, _ -> },
    onReady: () -> Unit = {},
) = clientCallListener(
    onHeaders = onHeaders,
    onMessage = onMessage,
    onClose = onClose,
    onReady = onReady,
)