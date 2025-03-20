/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.descriptor.RpcInvokator
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule

@InternalRpcApi
public abstract class KrpcServiceHandler {
    protected abstract val sender: KrpcMessageSender
    protected abstract val config: KrpcConfig
    protected abstract val logger: RpcInternalCommonLogger

    protected suspend fun handleIncomingHotFlows(streamContext: KrpcStreamContext) {
        for (hotFlow in streamContext.incomingHotFlows) {
            streamContext.launch {
                /** Start consuming incoming requests, see [KrpcIncomingHotFlow.emit] */
                hotFlow.emit(null)
            }
        }
    }

    protected suspend fun handleOutgoingStreams(
        streamContext: KrpcStreamContext,
        serialFormat: SerialFormat,
        serviceTypeString: String,
    ) {
        val mutex = Mutex()
        for (outgoingStream in streamContext.outgoingStreams) {
            streamContext.launch {
                try {
                    when (outgoingStream.kind) {
                        StreamKind.Flow, StreamKind.SharedFlow, StreamKind.StateFlow -> {
                            val stream = outgoingStream.stream as Flow<*>

                            collectAndSendOutgoingStream(
                                mutex = mutex,
                                serialFormat = serialFormat,
                                flow = stream,
                                outgoingStream = outgoingStream,
                                serviceTypeString = serviceTypeString,
                            )
                        }
                    }
                } catch (e : CancellationException) {
                    // canceled by a streamScope
                    throw e
                } catch (@Suppress("detekt.TooGenericExceptionCaught") cause: Throwable) {
                    mutex.withLock {
                        val serializedReason = serializeException(cause)
                        val message = KrpcCallMessage.StreamCancel(
                            callId = outgoingStream.callId,
                            serviceType = serviceTypeString,
                            streamId = outgoingStream.streamId,
                            cause = serializedReason,
                            connectionId = outgoingStream.connectionId,
                            serviceId = outgoingStream.serviceId,
                        )
                        sender.sendMessage(message)
                    }
                    throw cause
                }

                mutex.withLock {
                    val message = KrpcCallMessage.StreamFinished(
                        callId = outgoingStream.callId,
                        serviceType = serviceTypeString,
                        streamId = outgoingStream.streamId,
                        connectionId = outgoingStream.connectionId,
                        serviceId = outgoingStream.serviceId,
                    )

                    sender.sendMessage(message)
                }
            }
        }
    }

    @Suppress("detekt.LongParameterList")
    private suspend fun collectAndSendOutgoingStream(
        mutex: Mutex,
        serialFormat: SerialFormat,
        flow: Flow<*>,
        serviceTypeString: String,
        outgoingStream: KrpcStreamCall,
    ) {
        flow.collect {
            // because we can send new message for the new flow,
            // which is not published with `transport.send(message)`
            mutex.withLock {
                val message = when (serialFormat) {
                    is StringFormat -> {
                        val stringData = serialFormat.encodeToString(outgoingStream.elementSerializer, it)
                        KrpcCallMessage.StreamMessageString(
                            callId = outgoingStream.callId,
                            serviceType = serviceTypeString,
                            streamId = outgoingStream.streamId,
                            data = stringData,
                            connectionId = outgoingStream.connectionId,
                            serviceId = outgoingStream.serviceId,
                        )
                    }

                    is BinaryFormat -> {
                        val binaryData = serialFormat.encodeToByteArray(outgoingStream.elementSerializer, it)
                        KrpcCallMessage.StreamMessageBinary(
                            callId = outgoingStream.callId,
                            serviceType = serviceTypeString,
                            streamId = outgoingStream.streamId,
                            data = binaryData,
                            connectionId = outgoingStream.connectionId,
                            serviceId = outgoingStream.serviceId,
                        )
                    }

                    else -> {
                        unsupportedSerialFormatError(serialFormat)
                    }
                }

                sender.sendMessage(message)
            }
        }
    }

    protected fun prepareSerialFormat(rpcFlowContext: LazyKrpcStreamContext): SerialFormat {
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

    protected fun RpcCallable<*>.toMessageCallType(): KrpcCallMessage.CallType {
        return when (invokator) {
            is RpcInvokator.Method -> KrpcCallMessage.CallType.Method
            is RpcInvokator.Field -> KrpcCallMessage.CallType.Field
        }
    }
}
