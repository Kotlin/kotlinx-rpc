/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.krpc.internal.HandlerKey
import kotlinx.rpc.krpc.internal.KrpcActingReceiveHandler
import kotlinx.rpc.krpc.internal.KrpcCallMessage
import kotlinx.rpc.krpc.internal.KrpcCallMessage.CallType
import kotlinx.rpc.krpc.internal.KrpcGenericMessage
import kotlinx.rpc.krpc.internal.KrpcMessage
import kotlinx.rpc.krpc.internal.KrpcMessageSender
import kotlinx.rpc.krpc.internal.KrpcMessageSubscription
import kotlinx.rpc.krpc.internal.KrpcPluginKey
import kotlinx.rpc.krpc.internal.KrpcReceiveBuffer
import kotlinx.rpc.krpc.internal.KrpcStoringReceiveHandler
import kotlinx.rpc.krpc.internal.WindowResult
import kotlinx.rpc.krpc.internal.decodeWindow
import kotlinx.rpc.krpc.internal.deserialize
import kotlinx.rpc.krpc.internal.isFailure
import kotlinx.rpc.krpc.internal.isSuccess
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class KrpcReceiveHandlerTest : KrpcReceiveHandlerBaseTest() {
    @Test
    fun zeroBufferSizeStoring() = runStoringTest(bufferSize = 0) {
        val result = storing.handle("test".asMessage()) {}

        assertTrue { result.isFailure }
    }

    @Test
    fun windowUpdate() = runActingTest(
        callTimeOut = 10.seconds,
        bufferSize = 0,
        callHandler = { },
    ) { acting ->
        acting.broadcastWindowUpdate(1, null, "service", "callId")

        val windowResult = decodeWindow(channel.receive() as KrpcGenericMessage)

        assertTrue(windowResult is WindowResult.Success, windowResult.toString())
        assertEquals(1, windowResult.update)
        assertEquals("service", windowResult.key.serviceType)
        assertEquals("callId", windowResult.key.callId)

        acting.broadcastWindowUpdate(-1, null, "service", "callId")
        val windowResult2 = decodeWindow(channel.receive() as KrpcGenericMessage)
        assertEquals(-1, (windowResult2 as WindowResult.Success).update)

        acting.broadcastWindowUpdate(Int.MAX_VALUE, null, "service", "callId")
        val windowResult3 = decodeWindow(channel.receive() as KrpcGenericMessage)
        assertEquals(Int.MAX_VALUE, (windowResult3 as WindowResult.Success).update)

        acting.broadcastWindowUpdate(Int.MIN_VALUE, null, "service", "callId")
        val windowResult4 = decodeWindow(channel.receive() as KrpcGenericMessage)
        assertEquals(Int.MIN_VALUE, (windowResult4 as WindowResult.Success).update)
    }

    @Test
    fun oneBufferSizeStoring() = runStoringTest(bufferSize = 1) {
        val result = storing.handle("test".asMessage()) {}

        assertTrue { result.isSuccess }

        val result2 = storing.handle("test".asMessage()) {}

        assertTrue { result2.isFailure }
    }

    @Test
    fun multipleWindowSizeStoring() = runStoringTest(bufferSize = 3) {
        storing.handle("test1".asCallMessage("1")) {}
        storing.handle("test2".asCallMessage("1")) {}
        storing.handle("test3".asCallMessage("2")) {}

        storing.close(HandlerKey.Generic, null)

        val message1 = (channel.receive() as KrpcCallMessage.CallException).cause.deserialize().message.orEmpty()
        assertTrue(message1.contains("2 messages were unprocessed"), message1)

        val message2 = (channel.receive() as KrpcCallMessage.CallException).cause.deserialize().message.orEmpty()
        assertTrue(message2.contains("1 messages were unprocessed"), message2)
    }
}

internal abstract class KrpcReceiveHandlerBaseTest {
    class Counter {
        val launches = atomic(0)
        val total = atomic(0)
    }

    protected fun String.asMessage() = KrpcGenericMessage(
        connectionId = null,
        pluginParams = mapOf(KrpcPluginKey.WINDOW_KEY to this)
    )

    protected fun String.asCallMessage(callId: String) = KrpcCallMessage.CallDataString(
        connectionId = null,
        callId = callId,
        serviceType = "service",
        data = this,
        callableName = "",
        callType = CallType.Method,
    )

    class TestConfig(
        val storing: KrpcStoringReceiveHandler,
        val buffer: KrpcReceiveBuffer,
        val channel: Channel<KrpcMessage>,
        val sender: KrpcMessageSender,
        val testScope: TestScope,
    ) : CoroutineScope by testScope

    protected fun runActingTest(
        callTimeOut: Duration,
        bufferSize: Int,
        callHandler: KrpcMessageSubscription<KrpcMessage>,
        timeout: Duration = 30.seconds,
        body: suspend TestConfig.(acting: KrpcActingReceiveHandler) -> Unit,
    ): TestResult {
        return runStoringTest(bufferSize, timeout) {
            val acting = KrpcActingReceiveHandler(
                callHandler = callHandler,
                storingHandler = storing,
                sender = sender,
                key = HandlerKey.Generic,
                timeout = callTimeOut,
                broadcastUpdates = true,
            )

            body(this, acting)

            acting.close(HandlerKey.Generic, null)
        }
    }

    protected fun runStoringTest(
        bufferSize: Int,
        timeout: Duration = 30.seconds,
        body: suspend TestConfig.() -> Unit,
    ) = runTestWithCoroutinesProbes(timeout = timeout) {
        val buffer = KrpcReceiveBuffer(
            bufferSize = { bufferSize },
        )

        val channel = Channel<KrpcMessage>(Channel.UNLIMITED)

        val senderJob = Job(this@runTestWithCoroutinesProbes.coroutineContext.job)
        val sender = object : KrpcMessageSender {
            override suspend fun sendMessage(message: KrpcMessage) {
                channel.send(message)
            }

            override val transportScope: CoroutineScope = CoroutineScope(senderJob)

            override fun drainSendQueueAndClose(message: String) {
                TODO("Not yet implemented")
            }
        }

        val handler = KrpcStoringReceiveHandler(buffer, sender)
        val config = TestConfig(handler, buffer, channel, sender, this)

        try {
            body(config)
        } finally {
            handler.close(HandlerKey.Generic, null)
            buffer.close(null)
            channel.cancel()
            channel.close()
            senderJob.cancelAndJoin()
        }
    }
}

internal expect val stressIterations: Int
internal expect val stressBufferSize: Int
