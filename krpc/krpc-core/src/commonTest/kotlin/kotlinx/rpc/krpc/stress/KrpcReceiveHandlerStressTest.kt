/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.stress

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.rpc.krpc.KrpcReceiveHandlerBaseTest
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.internal.KrpcCallMessage
import kotlinx.rpc.krpc.internal.KrpcGenericMessage
import kotlinx.rpc.krpc.internal.KrpcMessage
import kotlinx.rpc.krpc.internal.KrpcSendHandler
import kotlinx.rpc.krpc.internal.WindowResult
import kotlinx.rpc.krpc.internal.decodeWindow
import kotlinx.rpc.krpc.internal.deserialize
import kotlinx.rpc.krpc.internal.onClosed
import kotlinx.rpc.krpc.internal.onFailure
import kotlinx.rpc.krpc.stressBufferSize
import kotlinx.rpc.krpc.stressIterations
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class KrpcReceiveHandlerStressTest : KrpcReceiveHandlerBaseTest() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun stressActing() {
        val actorJob = Job()
        val collected = mutableListOf<KrpcMessage>()
        val bufferSize = stressBufferSize

        runActingTest(
            callTimeOut = 10.seconds,
            bufferSize = bufferSize,
            callHandler = { collected.add(it) },
            timeout = 10.minutes,
        ) { acting ->
            val sendChannel = Channel<KrpcTransportMessage>(Channel.UNLIMITED)
            val sender = KrpcSendHandler(sendChannel)
            sender.updateWindowSize(bufferSize)

            val windowJob = launch {
                while (true) {
                    val window = when (val message = channel.receive()) {
                        is KrpcCallMessage.CallException -> fail(
                            "Unexpected call exception",
                            message.cause.deserialize()
                        )

                        is KrpcGenericMessage -> decodeWindow(message)
                        else -> fail("Unexpected message: $message")
                    }

                    sender.updateWindowSize((window as WindowResult.Success).update)
                }
            }

            val senderJob = launch {
                while (true) {
                    val message = sendChannel.receive() as KrpcTransportMessage.StringMessage

                    acting.handle(message.value.asCallMessage("1")) {
                        fail(
                            "Unexpected onMessageFailure call, " +
                                    "window: ${sender.window}, collected: ${collected.size}\"",
                            it
                        )
                    }.onFailure {
                        fail(
                            "Unexpected onFailure call, " +
                                    "window: ${sender.window}, collected: ${collected.size}"
                        )
                    }.onClosed {
                        fail(
                            "Unexpected onClosed call, " +
                                    "window: ${sender.window}, collected: ${collected.size}\"",
                            it
                        )
                    }
                }
            }

            val counter = Counter()
            val printJob = launch {
                while (true) {
                    withContext(Dispatchers.Default) {
                        delay(5.seconds)
                    }
                    println(
                        "Collected: ${collected.size}, " +
                                "launches: ${counter.launches.value}, " +
                                "total: ${counter.total.value}"
                    )
                }
            }

            val iterations = stressIterations
            List(iterations) {
                launch {
                    repeat(100) {
                        sender.sendMessage(KrpcTransportMessage.StringMessage("Hello"))
                        counter.total.incrementAndGet()
                    }
                    counter.launches.incrementAndGet()
                }
            }.joinAll()

            while (!buffer.channel.isEmpty && sender.window != bufferSize) {
                yield()
            }

            assertEquals(iterations * 100, collected.size)
            actorJob.cancelAndJoin()
            senderJob.cancelAndJoin()
            windowJob.cancelAndJoin()
            printJob.cancelAndJoin()
        }
    }
}
