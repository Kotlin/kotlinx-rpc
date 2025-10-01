/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlinx.rpc.krpc.internal.KrpcSendHandler
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class KrpcSendHandlerTest : KrpcSendHandlerBaseTest() {
    @Test
    fun zeroWindowSize() = runTest { _, handler ->
        handler.updateWindowSize(0)

        val job = launch {
            var cancelled = false

            try {
                handler.sendMessage("Hello".asMessage())
            } catch (e: CancellationException) {
                cancelled = e.message?.contains("Test finished") ?: false
                if (!cancelled) {
                    fail("Unexpected cancellation exception", e)
                }

                throw e
            }

            if (!cancelled) {
                fail("Expected cancellation exception")
            }
        }

        handler.awaitSenders(1)

        handler.close(CancellationException("Test finished"))
        job.join()
    }

    @Test
    fun oneWindowSize() = runTest { channel, handler ->
        handler.updateWindowSize(0)

        val job = launch {
            handler.sendMessage("Hello".asMessage())
        }

        handler.awaitSenders(1)

        handler.updateWindowSize(1)

        job.join()

        assertEquals("Hello", channel.receive().value)
    }

    @Test
    fun multipleWindowSize() = runTest { channel, handler ->
        handler.updateWindowSize(0)

        val jobs = List(3) {
            launch {
                handler.sendMessage("Hello".asMessage())
            }
        }

        handler.awaitSenders(3)

        handler.updateWindowSize(1)

        assertEquals("Hello", channel.receive().value)

        handler.awaitSenders(2)

        handler.updateWindowSize(2)

        assertEquals("Hello", channel.receive().value)
        assertEquals("Hello", channel.receive().value)

        jobs.joinAll()

        handler.updateWindowSize(1)
        handler.sendMessage("Hello".asMessage())
        assertEquals("Hello", channel.receive().value)
    }
}

internal abstract class KrpcSendHandlerBaseTest {
    protected suspend fun KrpcSendHandler.awaitSenders(num: Int) {
        withTimeoutOrNull(10.seconds) {
            while (true) {
                if (__continuations.size == num) {
                    break
                }

                yield()
            }
        } ?: fail("Timeout while waiting for continuation, current size: ${__continuations.size}")
    }

    protected fun runTest(
        timeout: Duration = 30.seconds,
        body: suspend TestScope.(Channel<KrpcTransportMessage>, KrpcSendHandler) -> Unit,
    ) = runTestWithCoroutinesProbes(timeout = timeout) {
        val channel = Channel<KrpcTransportMessage>(
            capacity = Channel.UNLIMITED,
        )

        val handler = KrpcSendHandler(channel)
        try {
            body(channel, handler)
        } finally {
            handler.close()
            channel.cancel()
            channel.close()
        }
    }
}

val KrpcTransportMessage.value get() = (this as KrpcTransportMessage.StringMessage).value
fun String.asMessage() = KrpcTransportMessage.StringMessage(this)
