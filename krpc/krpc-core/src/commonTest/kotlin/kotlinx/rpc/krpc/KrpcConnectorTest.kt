/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package kotlinx.rpc.krpc

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.krpc.internal.HandlerKey
import kotlinx.rpc.krpc.internal.KrpcCallMessage
import kotlinx.rpc.krpc.internal.KrpcConnector
import kotlinx.rpc.krpc.internal.KrpcGenericMessage
import kotlinx.rpc.krpc.internal.KrpcMessage
import kotlinx.rpc.krpc.internal.KrpcPlugin
import kotlinx.rpc.krpc.internal.KrpcPluginKey
import kotlinx.rpc.krpc.internal.KrpcProtocolMessage
import kotlinx.rpc.krpc.internal.deserialize
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class KrpcConnectorTest : KrpcConnectorBaseTest() {
    @Test
    @JsName("service_call_success_is_delivered_to_a_client")
    fun `service call success is delivered to a client`() = runTest { client, server ->
        // Enable backpressure to use configured perCallBufferSize deterministically
        val clientPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val serverPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val hs = handshakeBoth(client, server, clientPExceptionsChannel, serverPExceptionsChannel)

        val clientInbox = Channel<KrpcMessage>(10)

        client.subscribeToMessages(HandlerKey.ServiceCall("svc", "1")) {
            clientInbox.send(it)
        }

        server.subscribeToMessages(HandlerKey.Service("svc")) { message ->
            if (message is KrpcCallMessage.CallDataString) {
                server.sendMessage(
                    KrpcCallMessage.CallSuccessString(
                        callId = message.callId,
                        serviceType = message.serviceType,
                        data = "${message.data} : OK",
                        connectionId = message.connectionId,
                        serviceId = message.serviceId,
                    )
                )
            }
        }

        client.sendMessage("ping".asCallMessage("svc", "1"))

        val reply = clientInbox.receive()
        val success = assertIs<KrpcCallMessage.CallSuccessString>(reply)
        assertEquals("ping : OK", success.data)

        hs.assertAllShookHands()

        assertTrue(clientPExceptionsChannel.isEmpty)
        assertTrue(serverPExceptionsChannel.isEmpty)
    }

    @Test
    @JsName("server_exception_propagates_as_a_call_exception")
    fun `server exception propagates as a call exception`() = runTest { client, server ->
        val clientPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val serverPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val hs = handshakeBoth(client, server, clientPExceptionsChannel, serverPExceptionsChannel)

        val clientInbox = Channel<KrpcMessage>(10)
        client.subscribeToMessages(HandlerKey.ServiceCall("svc", "2")) {
            clientInbox.send(it)
        }

        server.subscribeToMessages(HandlerKey.Service("svc")) {
            throw IllegalArgumentException("boom")
        }

        server.subscribeToMessages(HandlerKey.Generic) {
            throw IllegalArgumentException("boom")
        }

        client.sendMessage("req".asCallMessage("svc", "2"))

        val msg = clientInbox.receive()
        val ex = assertIs<KrpcCallMessage.CallException>(msg)
        val t = ex.cause.deserialize()
        assertTrue(t.message!!.contains("Failed to process call"), t.toString())

        client.sendMessage("req".asGenericMessage())
        val protocolMessage = clientPExceptionsChannel.receive()
        val protocolException = assertIs<KrpcProtocolMessage.Failure>(protocolMessage)
        assertEquals("req".asGenericMessage(), protocolException.failedMessage)

        hs.assertAllShookHands()
    }

    @Test
    @JsName("server_timeout_sends_a_call_exception")
    fun `server timeout sends a call exception`() = runTest(callTimeout = 200.milliseconds) { client, server ->
        val clientPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val serverPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val hs = handshakeBoth(client, server, clientPExceptionsChannel, serverPExceptionsChannel)

        val clientInbox = Channel<KrpcMessage>(10)
        client.subscribeToMessages(HandlerKey.ServiceCall("svc", "3")) {
            clientInbox.send(it)
        }

        server.subscribeToMessages(HandlerKey.Service("svc")) {
            // Simulate long processing exceeding callTimeout
            delay(500.milliseconds)
        }

        client.sendMessage("req".asCallMessage("svc", "3"))

        val msg = clientInbox.receive()

        val ex = assertIs<KrpcCallMessage.CallException>(msg)
        val t = ex.cause.deserialize()
        assertTrue(t.message!!.contains("Failed to process call"), t.toString())
        assertEquals(t.cause?.message?.contains("Timeout while processing message"), true, t.toString())

        hs.assertAllShookHands()
        assertTrue(clientPExceptionsChannel.isEmpty)
        assertTrue(serverPExceptionsChannel.isEmpty)
    }

    @Test
    @JsName("buffer_overflow_does_not_result_in_a_call_exception")
    fun `buffer overflow does not result in a call exception`() = runTest(perCallBufferSize = 1) { client, server ->
        val clientPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val serverPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val hs = handshakeBoth(client, server, clientPExceptionsChannel, serverPExceptionsChannel)

        val clientInbox = Channel<KrpcMessage>(10)
        client.subscribeToMessages(HandlerKey.ServiceCall("svc", "4")) {
            if (it is KrpcCallMessage.CallException) {
                val ex = it.cause.deserialize()

                coroutineContext.cancel(CancellationException("Unexpected failure from the server", ex))
                return@subscribeToMessages
            }

            clientInbox.send(it)
        }

        val serverInbox = Channel<KrpcMessage>(10)
        server.subscribeToMessages(HandlerKey.Service("svc")) {
            if (it is KrpcCallMessage.CallException) {
                val ex = it.cause.deserialize()

                coroutineContext.cancel(CancellationException("Unexpected failure from the client", ex))
                return@subscribeToMessages
            }

            serverInbox.send(it)
        }

        client.sendMessage("m1".asCallMessage("svc", "4"))
        client.sendMessage("m2".asCallMessage("svc", "4"))

        val msg = serverInbox.receive()
        assertEquals("m1", (msg as KrpcCallMessage.CallDataString).data)
        val msg2 = serverInbox.receive()
        assertEquals("m2", (msg2 as KrpcCallMessage.CallDataString).data)

        hs.assertAllShookHands()
        assertTrue(clientInbox.isEmpty)
        assertTrue(clientPExceptionsChannel.isEmpty)
        assertTrue(serverPExceptionsChannel.isEmpty)
    }

    @Test
    @JsName("wait_timeout_exceeds_sends_a_call_exception")
    fun `wait timeout exceeds sends a call exception`() = runTest(
        waitTimeout = 300.milliseconds,
        perCallBufferSize = 2,
    ) { client, server ->
        val clientPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val serverPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val hs = handshakeBoth(client, server, clientPExceptionsChannel, serverPExceptionsChannel)

        val clientInbox = Channel<KrpcMessage>(10)
        client.subscribeToMessages(HandlerKey.ServiceCall("svc", "5")) {
            clientInbox.send(it)
        }

        // Send one message that will stay unprocessed until waitTimeout triggers discard
        client.sendMessage("stay".asCallMessage("svc", "5"))
        client.sendMessage("stay2".asCallMessage("svc", "5"))

        val msg = clientInbox.receive()
        val ex = assertIs<KrpcCallMessage.CallException>(msg)
        val top = ex.cause.deserialize()
        // top-level message comes from buffer close; nested cause contains a wait-timeout message
        assertEquals(top.message?.contains("2 messages were unprocessed"), true, top.toString())
        assertEquals(top.cause?.message?.contains("Waiting limit of"), true, top.toString())

        hs.assertAllShookHands()
    }

    @Test
    @JsName("one_item_buffer_size_works_with_a_stream_of_messages")
    fun `one item buffer size works with a stream of messages`() = runTest(perCallBufferSize = 1) { client, server ->
        val clientPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val serverPExceptionsChannel = Channel<KrpcProtocolMessage.Failure>(Channel.UNLIMITED)
        val hs = handshakeBoth(client, server, clientPExceptionsChannel, serverPExceptionsChannel)

        val clientInbox = Channel<KrpcMessage>(10)
        client.subscribeToMessages(HandlerKey.ServiceCall("svc", "6")) {
            clientInbox.send(it)
        }

        server.subscribeToMessages(HandlerKey.Service("svc")) { message ->
            server.sendMessage(message)
        }

        launch {
            repeat(100) {
                client.sendMessage("ping".asCallMessage("svc", "6"))
            }
        }

        repeat(100) {
            assertEquals("ping", (clientInbox.receive() as KrpcCallMessage.CallDataString).data)
        }

        hs.assertAllShookHands()
    }

    private class HsResult {
        val clientShookHands = CompletableDeferred<Unit>()
        val serverShookHands = CompletableDeferred<Unit>()

        fun assertAllShookHands() {
            assertTrue(clientShookHands.isCompleted)
            assertTrue(serverShookHands.isCompleted)
        }

        suspend fun await() {
            clientShookHands.await()
            serverShookHands.await()
        }
    }

    private suspend fun handshakeBoth(
        client: KrpcConnector,
        server: KrpcConnector,
        clientExceptionsChannel: Channel<KrpcProtocolMessage.Failure>,
        serverExceptionsChannel: Channel<KrpcProtocolMessage.Failure>,
    ): HsResult {
        val hs = KrpcProtocolMessage.Handshake(KrpcPlugin.ALL)
        val hsResult = HsResult()

        client.subscribeToMessages(HandlerKey.Protocol) {
            if (it is KrpcProtocolMessage.Failure) {
                if (!hsResult.clientShookHands.isCompleted) {
                    fail("Handshake must be first message, but got: $it")
                }

                clientExceptionsChannel.send(it)
            }
            assertEquals(hs, it)
            hsResult.clientShookHands.complete(Unit)
        }

        server.subscribeToMessages(HandlerKey.Protocol) {
            if (it is KrpcProtocolMessage.Failure) {
                if (!hsResult.serverShookHands.isCompleted) {
                    fail("Handshake must be first message, but got: $it")
                }

                serverExceptionsChannel.send(it)
            }
            assertEquals(hs, it)
            hsResult.serverShookHands.complete(Unit)
        }

        client.sendMessage(hs)
        server.sendMessage(hs)

        hsResult.await()

        return hsResult
    }
}

abstract class KrpcConnectorBaseTest {
    protected fun String.asCallMessage(
        serviceId: String,
        callId: String,
    ) = KrpcCallMessage.CallDataString(
        connectionId = null,
        callId = callId,
        serviceType = serviceId,
        data = this,
        callableName = "",
        callType = KrpcCallMessage.CallType.Method,
    )

    protected fun String.asGenericMessage() = KrpcGenericMessage(
        connectionId = null,
        pluginParams = mapOf(KrpcPluginKey.GENERIC_MESSAGE_TYPE to this),
    )

    protected fun runTest(
        testTimeout: Duration = 3.seconds,
        waitTimeout: Duration = 1.seconds,
        callTimeout: Duration = 1.seconds,
        perCallBufferSize: Int = 100,
        body: suspend TestScope.(clientConnector: KrpcConnector, serverConnector: KrpcConnector) -> Unit,
    ) = kotlinx.coroutines.test.runTest(timeout = testTimeout) {
        debugCoroutines()

        val connectorConfig = KrpcConfig.Connector(waitTimeout, callTimeout, perCallBufferSize)

        val transport = LocalTransport(coroutineContext)

        val clientConnector = KrpcConnector(
            serialFormat = Json,
            transport = transport.client,
            config = connectorConfig,
            isServer = false,
        )

        val serverConnector = KrpcConnector(
            serialFormat = Json,
            transport = transport.server,
            config = connectorConfig,
            isServer = true,
        )

        try {
            body(clientConnector, serverConnector)
        } finally {
            transport.coroutineContext.job.cancelAndJoin()
        }
    }
}

private class LocalTransport(
    parentContext: CoroutineContext? = null,
) : CoroutineScope {
    override val coroutineContext = SupervisorJob(parentContext?.get(Job))

    private val clientIncoming = Channel<KrpcTransportMessage>()
    private val serverIncoming = Channel<KrpcTransportMessage>()

    val client: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job).let {
            if (parentContext != null) parentContext + it else it
        }

        override suspend fun send(message: KrpcTransportMessage) {
            serverIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return clientIncoming.receive()
        }
    }

    val server: KrpcTransport = object : KrpcTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job).let {
            if (parentContext != null) parentContext + it else it
        }

        override suspend fun send(message: KrpcTransportMessage) {
            clientIncoming.send(message)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return serverIncoming.receive()
        }
    }
}
