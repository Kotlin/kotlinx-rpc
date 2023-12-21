package org.jetbrains.krpc.internal.transport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.RPCCall
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.internal.*
import org.jetbrains.krpc.internal.logging.CommonLogger

@InternalKRPCApi
abstract class RPCEndpointBase: CoroutineScope {
    protected abstract val sender: RPCMessageSender
    protected abstract val config: RPCConfig
    protected abstract val logger: CommonLogger

    protected suspend fun handleIncomingHotFlows(scope: CoroutineScope, streamContext: LazyRPCStreamContext) {
        for (hotFlow in streamContext.awaitInitialized().incomingHotFlows) {
            scope.launch {
                /** Start consuming incoming requests, see [RPCIncomingHotFlow.emit] */
                hotFlow.emit(null)
            }
        }
    }

    protected suspend fun handleOutgoingStreams(
        scope: CoroutineScope,
        streamContext: LazyRPCStreamContext,
        serialFormat: SerialFormat,
        serviceTypeString: String
    ) {
        val mutex = Mutex()
        for (outgoingStream in streamContext.awaitInitialized().outgoingStreams) {
            scope.launch {
                val callId = outgoingStream.callId
                val streamId = outgoingStream.streamId
                val elementSerializer = outgoingStream.elementSerializer
                try {
                    when (outgoingStream.kind) {
                        StreamKind.Flow, StreamKind.SharedFlow, StreamKind.StateFlow -> {
                            val stream = outgoingStream.stream as Flow<*>

                            collectAndSendOutgoingFlow(
                                mutex = mutex,
                                serialFormat = serialFormat,
                                flow = stream,
                                callId = callId,
                                streamId = streamId,
                                elementSerializer = elementSerializer,
                                serviceTypeString = serviceTypeString
                            )
                        }
                    }
                } catch (@Suppress("detekt.TooGenericExceptionCaught") cause: Throwable) {
                    mutex.withLock {
                        val serializedReason = serializeException(cause)
                        val message = RPCMessage.StreamCancel(callId, serviceTypeString, streamId, serializedReason)
                        sender.sendMessage(message)
                    }
                    throw cause
                }

                mutex.withLock {
                    val message = RPCMessage.StreamFinished(callId, serviceTypeString, streamId)
                    sender.sendMessage(message)
                }
            }
        }
    }

    private suspend fun collectAndSendOutgoingFlow(
        mutex: Mutex,
        serialFormat: SerialFormat,
        flow: Flow<*>,
        callId: String,
        streamId: String,
        elementSerializer: KSerializer<Any?>,
        serviceTypeString: String
    ) {
        flow.collect {
            // because we can send new message for the new flow,
            // which is not published with `transport.send(message)`
            mutex.withLock {
                val message = when (serialFormat) {
                    is StringFormat -> {
                        val stringData = serialFormat.encodeToString(elementSerializer, it)
                        RPCMessage.StreamMessageString(callId, serviceTypeString, streamId, stringData)
                    }

                    is BinaryFormat -> {
                        val binaryData = serialFormat.encodeToByteArray(elementSerializer, it)
                        RPCMessage.StreamMessageBinary(callId, serviceTypeString, streamId, binaryData)
                    }

                    else -> {
                        unsupportedSerialFormatError(serialFormat)
                    }
                }

                sender.sendMessage(message)
            }
        }
    }

    protected fun prepareSerialFormat(rpcFlowContext: LazyRPCStreamContext): SerialFormat {
        val module = SerializersModule {
            contextual(Flow::class) {
                @Suppress("UNCHECKED_CAST")
                StreamSerializer.Flow(rpcFlowContext.initialize(), it.first() as KSerializer<Any?>)
            }

            contextual(SharedFlow::class) {
                @Suppress("UNCHECKED_CAST")
                StreamSerializer.SharedFlow(rpcFlowContext.initialize(), it.first() as KSerializer<Any?>)
            }

            contextual(StateFlow::class) {
                @Suppress("UNCHECKED_CAST")
                StreamSerializer.StateFlow(rpcFlowContext.initialize(), it.first() as KSerializer<Any?>)
            }
        }

        return config.serialFormatInitializer.applySerializersModuleAndBuild(module)
    }

    protected fun RPCCall.Type.toMessageCallType(): RPCMessage.CallType {
        return when (this) {
            RPCCall.Type.Method -> RPCMessage.CallType.Method
            RPCCall.Type.Field -> RPCMessage.CallType.Field
        }
    }
}
