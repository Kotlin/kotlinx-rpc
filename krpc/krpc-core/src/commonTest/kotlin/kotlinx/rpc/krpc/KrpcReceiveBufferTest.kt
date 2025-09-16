/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.krpc.internal.BufferResult
import kotlinx.rpc.krpc.internal.KrpcGenericMessage
import kotlinx.rpc.krpc.internal.KrpcPluginKey
import kotlinx.rpc.krpc.internal.KrpcReceiveBuffer
import kotlinx.rpc.krpc.internal.isClosed
import kotlinx.rpc.krpc.internal.isFailure
import kotlinx.rpc.krpc.internal.isSuccess
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class KrpcReceiveBufferTest : KrpcReceiveBufferBaseTest() {
    @Test
    fun zeroBufferSize() = runTest(bufferSize = 0) { buffer ->
        val result = buffer.trySend("hello")

        assertTrue { result.isFailure }
        assertEquals(0, buffer.window)
        assertEquals(0, buffer.inBuffer)
    }

    @Test
    fun oneBufferSize() = runTest(bufferSize = 1) { buffer ->
        assertEquals(1, buffer.window)
        val result = buffer.trySend("hello")

        assertTrue { result.isSuccess }
        assertEquals(0, buffer.window)
        assertEquals(1, buffer.inBuffer)

        val secondResult = buffer.trySend("world")
        assertTrue { secondResult.isFailure }
    }

    @Test
    fun closeBuffer() = runTest(bufferSize = 3) { buffer ->
        assertEquals(3, buffer.window)
        buffer.trySend("hello")
        buffer.trySend("world")
        buffer.close(null)
        assertEquals(3, buffer.inBuffer)
        assertEquals(0, buffer.window)
        val result = buffer.trySend("test")
        assertTrue { result.isClosed }
    }
}

internal abstract class KrpcReceiveBufferBaseTest {
    protected fun KrpcReceiveBuffer.trySend(
        message: String,
    ): BufferResult<Unit> {
        return trySend(
            KrpcReceiveBuffer.MessageRequest(
                message = KrpcGenericMessage(null, mapOf(KrpcPluginKey.WINDOW_KEY to message)),
                onMessageFailure = { },
            )
        )
    }

    protected fun runTest(
        bufferSize: Int,
        timeout: Duration = 10.seconds,
        body: suspend TestScope.(KrpcReceiveBuffer) -> Unit,
    ) = runTestWithCoroutinesProbes(timeout = timeout) {
        val buffer = KrpcReceiveBuffer(
            bufferSize = { bufferSize },
        )

        try {
            body(buffer)
        } finally {
            buffer.close(null)
        }
    }
}
