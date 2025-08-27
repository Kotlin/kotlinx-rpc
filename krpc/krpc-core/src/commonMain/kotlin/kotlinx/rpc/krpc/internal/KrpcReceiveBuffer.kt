/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel

internal class KrpcReceiveBuffer(
    private val bufferSize: () -> Int,
) {
    data class MessageRequest(
        val message: KrpcMessage,
        val onMessageFailure: suspend (Throwable?) -> Unit,
    )

    private val _inBuffer = atomic(0)
    val inBuffer get() = _inBuffer.value

    private val _channelSize by lazy { bufferSize() }
    val channel by lazy {
        Channel<MessageRequest>(capacity = _channelSize)
    }

    val window get() = _channelSize - _inBuffer.value

    suspend fun receiveCatching(): BufferResult<MessageRequest> {
        val result = channel.receiveCatching()
        if (result.isSuccess) {
            _inBuffer.decrementAndGet()
        }

        return when {
            result.isSuccess -> BufferResult.Success(result.getOrThrow())
            result.isClosed -> BufferResult.Closed(result.exceptionOrNull())
            result.isFailure -> BufferResult.Failure()
            else -> error("Unreachable")
        }
    }

    fun trySend(message: MessageRequest): BufferResult<Unit> {
        val result = channel.trySend(message)
        if (result.isSuccess) {
            _inBuffer.incrementAndGet()
        }

        return when {
            result.isSuccess -> BufferResult.Success(Unit)
            result.isClosed -> BufferResult.Closed(result.exceptionOrNull())
            result.isFailure -> BufferResult.Failure()
            else -> error("Unreachable")
        }
    }

    fun close(cause: Throwable?) {
        channel.close(cause)
        channel.cancel(CancellationException(null, cause))

        _inBuffer.getAndSet(_channelSize)
    }
}
