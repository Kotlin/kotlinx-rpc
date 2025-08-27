/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.time.Duration

@InternalRpcApi
public sealed interface HandlerKey {
    @InternalRpcApi
    public data object Protocol : HandlerKey

    @InternalRpcApi
    public data object Generic : HandlerKey

    @InternalRpcApi
    public data class Service(val serviceType: String) : HandlerKey

    @InternalRpcApi
    public data class ServiceCall(val serviceType: String, val callId: String) : HandlerKey
}

internal val KrpcMessage.handlerKey get(): HandlerKey {
    return when (this) {
        is KrpcCallMessage -> HandlerKey.ServiceCall(serviceType, callId)
        is KrpcProtocolMessage -> HandlerKey.Protocol
        is KrpcGenericMessage -> HandlerKey.Generic
    }
}

internal sealed interface KrpcReceiveHandler {
    fun handle(
        message: KrpcMessage,
        onMessageFailure: suspend (Throwable?) -> Unit,
    ): BufferResult<Unit>

    fun close(key: HandlerKey, e: Throwable?)
}

internal class KrpcStoringReceiveHandler(
    private val buffer: KrpcReceiveBuffer,
    private val sender: KrpcMessageSender,
) : KrpcReceiveHandler {
    val inBuffer get() = buffer.inBuffer
    private val closed = atomic(false)
    private val scope get() = sender.transportScope

    override fun handle(
        message: KrpcMessage,
        onMessageFailure: suspend (Throwable?) -> Unit,
    ): BufferResult<Unit> {
        if (closed.value) {
            return BufferResult.Closed(illegalStateException("KrpcStoringReceiveHandler closed"))
        }

        return buffer.trySend(KrpcReceiveBuffer.MessageRequest(message, onMessageFailure))
    }

    override fun close(key: HandlerKey, e: Throwable?) {
        if (!closed.compareAndSet(expect = false, update = true)) {
            return
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        if (buffer.channel.isEmpty) {
            buffer.close(e)
            return
        }

        // close for sending
        buffer.channel.close(e)

        scope.launch(CoroutineName("krpc-connector-message-buffer-close-$key")) {
            val undelivered = buffer.channel.toList()
                .map { it.message }
                .filterIsInstance<KrpcCallMessage>() // ignore protocol messages

            undelivered.groupBy { it.callId }.forEach { (callId, messages) ->
                val message = messages.first()

                val cause = illegalStateException(
                    message = "Buffer closed for $callId for service ${message.serviceType}, " +
                            "${messages.size} messages were unprocessed",
                    cause = e,
                )

                val callException = KrpcCallMessage.CallException(
                    callId = message.callId,
                    serviceType = message.serviceType,
                    cause = serializeException(cause),
                    connectionId = message.connectionId,
                    serviceId = message.serviceId,
                )

                this@KrpcStoringReceiveHandler.sender.sendMessage(callException)
            }

            buffer.close(e)
        }
    }

    private val _processingStarted = atomic(false)
    val processingStarted get() = _processingStarted.value

    suspend fun receiveCatching(): BufferResult<KrpcReceiveBuffer.MessageRequest> {
        if (closed.value) {
            return BufferResult.Closed(illegalStateException("KrpcStoringReceiveHandler closed"))
        }

        _processingStarted.getAndSet(true)
        return buffer.receiveCatching()
    }
}

internal class KrpcActingReceiveHandler(
    private val callHandler: KrpcMessageSubscription,
    private val storingHandler: KrpcStoringReceiveHandler,
    private val sender: KrpcMessageSender,
    key: HandlerKey,
    timeout: Duration,
) : KrpcReceiveHandler {
    private val job = sender.transportScope.launch(
        context = CoroutineName("krpc-connector-message-handler-$key"),
        start = CoroutineStart.LAZY,
    ) {
        while (true) {
            val (message, onMessageFailure) = storingHandler.receiveCatching().getOrNull() ?: break

            val result = withTimeoutOrNull(timeout) { tryHandle(message) }
                ?: HandlerResult.Failure(
                    illegalStateException("Timeout while processing message")
                )

            if (result is HandlerResult.Failure) {
                onMessageFailure(result.cause)
            }

            if (message !is KrpcCallMessage) continue

            broadcastWindowUpdate(1, message.connectionId, message.serviceType, message.callId)
        }
    }

    init {
        if (storingHandler.inBuffer > 0) {
            job.start()
        }
    }

    override fun handle(
        message: KrpcMessage,
        onMessageFailure: suspend (Throwable?) -> Unit,
    ): BufferResult<Unit> {
        job.start()
        return storingHandler.handle(message, onMessageFailure)
    }

    override fun close(key: HandlerKey, e: Throwable?) {
        if (e != null) {
            job.cancel(CancellationException(null, e))
        } else {
            job.cancel()
        }

        storingHandler.close(key, e)
    }

    private suspend fun tryHandle(message: KrpcMessage): HandlerResult {
        val result = runCatching {
            callHandler(message)
        }

        return when {
            result.isFailure -> {
                val exception = result.exceptionOrNull()

                if (exception is CancellationException) {
                    throw exception
                }

                HandlerResult.Failure(exception)
            }

            else -> {
                HandlerResult.Success
            }
        }
    }

    internal suspend fun broadcastWindowUpdate(update: Int, connectionId: Long?, serviceType: String, callId: String) {
        sender.sendMessage(
            KrpcGenericMessage(
                connectionId = connectionId,
                pluginParams = mutableMapOf(
                    KrpcPluginKey.WINDOW_UPDATE to "$update",
                    KrpcPluginKey.WINDOW_KEY to "$serviceType/$callId",
                ),
            )
        )
    }

    private sealed interface HandlerResult {
        data object Success : HandlerResult

        data class Failure(val cause: Throwable?) : HandlerResult
    }
}

internal sealed interface WindowResult {
    data class Success(val update: Int, val key: HandlerKey) : WindowResult

    data class Failure(val message: String) : WindowResult
}

internal fun decodeWindow(message: KrpcGenericMessage): WindowResult {
    val windowParam = message.pluginParams!![KrpcPluginKey.WINDOW_UPDATE]!!.split("/")

    val updateParam = windowParam.getOrNull(0)?.toIntOrNull()

    if (windowParam.size != 1 || updateParam == null) {
        return WindowResult.Failure("Window param must be of the form <available>/<windowId> and in form of two longs")
    }

    val windowKey = message.pluginParams[KrpcPluginKey.WINDOW_KEY]?.split("/").orEmpty()
    val serviceType = windowKey.getOrNull(0)
    val callId = windowKey.getOrNull(1)

    if (windowKey.size != 2 || serviceType == null || callId == null) {
        return WindowResult.Failure("Window key must be of the form <serviceType>/<callId>")
    }

    val subscriptionKey = HandlerKey.ServiceCall(serviceType, callId)

    return WindowResult.Success(updateParam, subscriptionKey)
}
