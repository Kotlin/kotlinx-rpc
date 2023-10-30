package org.jetbrains.krpc.internal

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
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport

@InternalKRPCApi
abstract class RPCEngine {
    protected abstract val serviceTypeString: String
    protected abstract val transport: RPCTransport
    protected abstract val config: RPCConfig

    protected suspend fun handleIncomingHotFlows(scope: CoroutineScope, flowContext: LazyRPCStreamContext) {
        for (hotFlow in flowContext.awaitInitialized().incomingHotFlows) {
            scope.launch {
                /** Start consuming incoming requests, see [RPCIncomingHotFlow.emit] */
                hotFlow.emit(null)
            }
        }
    }

    protected suspend fun handleOutgoingStreams(
        scope: CoroutineScope,
        flowContext: LazyRPCStreamContext,
        serialFormat: SerialFormat
    ) {
        val mutex = Mutex()
        for (clientStream in flowContext.awaitInitialized().outgoingStreams) {
            scope.launch {
                val callId = clientStream.callId
                val streamId = clientStream.streamId
                val elementSerializer = clientStream.elementSerializer
                try {
                    when (clientStream.kind) {
                        StreamKind.Flow, StreamKind.SharedFlow, StreamKind.StateFlow -> {
                            val stream = clientStream.stream as Flow<*>

                            collectAndSendOutgoingFlow(mutex, serialFormat, stream, callId, streamId, elementSerializer)
                        }
                    }
                } catch (cause: Throwable) {
                    val serializedReason = serializeException(cause)
                    val message = RPCMessage.StreamCancel(callId, serviceTypeString, streamId, serializedReason)
                    transport.send(message)
                    throw cause
                }

                val message = RPCMessage.StreamFinished(callId, serviceTypeString, streamId)
                transport.send(message)
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
    ) {
        flow.collect {
            // because we can send new message for the new flow,
            // which is not published with `transport.send(message)`
            mutex.withLock {
                val data = when (serialFormat) {
                    is StringFormat -> serialFormat.encodeToString(elementSerializer, it)
                    else -> error("binary not supported for RPCMessage.StreamMessage")
                }
                val message = RPCMessage.StreamMessage(callId, serviceTypeString, streamId, data)
                transport.send(message)
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

        return config.serialFormatInitializer.applySerializersModuleAndGet(module)
    }
}
