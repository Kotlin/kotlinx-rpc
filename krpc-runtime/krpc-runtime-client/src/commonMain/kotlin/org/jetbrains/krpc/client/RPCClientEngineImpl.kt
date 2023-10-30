package org.jetbrains.krpc.client

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.*
import org.jetbrains.krpc.client.internal.FieldDataObject
import org.jetbrains.krpc.client.internal.RPCFlow
import org.jetbrains.krpc.internal.*
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private val CLIENT_ENGINE_ID = atomic(initial = 0L)

internal class RPCClientEngineImpl(
    private val transport: RPCTransport,
    serviceKClass: KClass<out RPC>,
    private val config: RPCConfig.Client = RPCConfig.Client.Default,
) : RPCClientEngine {
    private val callCounter = atomic(0L)
    private val engineId: Long = CLIENT_ENGINE_ID.incrementAndGet()
    private val serviceTypeString = serviceKClass.toString()
    private val logger = KotlinLogging.logger("RPCClientEngine[$serviceTypeString][0x${hashCode().toString(16)}]")

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default

    init {
        coroutineContext.job.invokeOnCompletion {
            logger.trace { "Job completed with $it" }
        }
    }

    override fun <T> registerPlainFlowField(fieldName: String, type: KType): Flow<T> {
        return RPCFlow.Plain<T>(serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, fieldName, type)
        }
    }

    override fun <T> registerSharedFlowField(fieldName: String, type: KType): SharedFlow<T> {
        return RPCFlow.Shared<T>(serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, fieldName, type)
        }
    }

    override fun <T> registerStateFlowField(fieldName: String, type: KType): StateFlow<T> {
        return RPCFlow.State<T>(serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, fieldName, type)
        }
    }

    private fun initializeFlowField(rpcFlow: RPCFlow<*, *>, fieldName: String, type: KType) {
        val callInfo = RPCCallInfo(
            callableName = fieldName,
            data = FieldDataObject,
            dataType = typeOf<FieldDataObject>(),
            returnType = type,
        )

        launch {
            call(
                callInfo = callInfo,
                deferred = rpcFlow.deferred,
            )
        }
    }

    override suspend fun call(callInfo: RPCCallInfo, deferred: CompletableDeferred<*>): Any? {
        val (callContext, serialFormat) = prepareAndExecuteCall(callInfo, deferred)

        launch {
            handleOutgoingFlows(callContext, serialFormat)
        }

        launch {
            handleIncomingHotFlows(callContext)
        }

        return deferred.await()
    }

    private suspend fun <T> prepareAndExecuteCall(
        callInfo: RPCCallInfo,
        deferred: CompletableDeferred<T>,
    ): Pair<LazyRPCStreamContext, SerialFormat> {
        val id = callCounter.incrementAndGet()
        val callId = "$engineId:${callInfo.dataType}:$id"

        logger.trace { "start a call[$callId] ${callInfo.callableName}" }

        val flowContext = LazyRPCStreamContext { RPCStreamContext(callId, config) }
        val serialFormat = prepareSerialFormat(flowContext)
        val firstMessage = serializeRequest(callId, callInfo, serialFormat)

        @Suppress("UNCHECKED_CAST")
        executeCall(callId, flowContext, callInfo, firstMessage, serialFormat, deferred as CompletableDeferred<Any?>)

        return flowContext to serialFormat
    }

    private suspend fun executeCall(
        callId: String,
        flowContext: LazyRPCStreamContext,
        callInfo: RPCCallInfo,
        firstMessage: RPCMessage,
        serialFormat: SerialFormat,
        deferred: CompletableDeferred<Any?>,
    ) {
        launch {
            transport.subscribe { message ->
                if (message.callId != callId) return@subscribe false

                when (message) {
                    is RPCMessage.CallData -> error("Unexpected message")
                    is RPCMessage.CallException -> {
                        val cause = runCatching {
                            message.cause.deserialize()
                        }

                        val result = if (cause.isFailure) {
                            cause.exceptionOrNull()!!
                        } else {
                            cause.getOrNull()!!
                        }

                        deferred.completeExceptionally(result)
                    }

                    is RPCMessage.CallSuccess -> {
                        val value = runCatching {
                            val serializerResult = serialFormat.serializersModule.rpcSerializerForType(callInfo.returnType)

                            when (serialFormat) {
                                is StringFormat -> serialFormat.decodeFromString(serializerResult, message.data)
                                else -> error("binary not supported for RPCMessage.CallSuccess")
                            }
                        }

                        deferred.completeWith(value)
                    }

                    is RPCMessage.StreamCancel -> {
                        flowContext.initialize().cancelStream(message)
                    }

                    is RPCMessage.StreamFinished -> {
                        flowContext.initialize().closeStream(message)
                    }

                    is RPCMessage.StreamMessage -> {
                        flowContext.initialize().send(message, serialFormat)
                    }
                }

                return@subscribe true
            }
        }

        coroutineContext.job.invokeOnCompletion {
            flowContext.valueOrNull?.close()
        }

        transport.send(firstMessage)
    }

    private suspend fun handleOutgoingFlows(flowContext: LazyRPCStreamContext, serialFormat: SerialFormat) {
        val mutex = Mutex()
        for (clientStream in flowContext.awaitInitialized().outgoingStreams) {
            launch {
                val callId = clientStream.callId
                val flowId = clientStream.streamId
                val elementSerializer = clientStream.elementSerializer
                try {
                    when (clientStream.kind) {
                        StreamKind.Flow, StreamKind.SharedFlow, StreamKind.StateFlow -> {
                            val stream = clientStream.stream as Flow<*>

                            stream.collect {
                                mutex.withLock {
                                    val data = when (serialFormat) {
                                        is StringFormat -> serialFormat.encodeToString(elementSerializer, it)
                                        else -> error("binary not supported for RPCMessage.CallSuccess")
                                    }
                                    val message = RPCMessage.StreamMessage(callId, serviceTypeString, flowId, data)
                                    transport.send(message)
                                }
                            }
                        }
                    }
                } catch (cause: Throwable) {
                    val serializedReason = serializeException(cause)
                    val message = RPCMessage.StreamCancel(callId, serviceTypeString, flowId, serializedReason)
                    transport.send(message)
                    throw cause
                }

                val message = RPCMessage.StreamFinished(callId, serviceTypeString, flowId)
                transport.send(message)
            }
        }
    }

    private suspend fun handleIncomingHotFlows(flowContext: LazyRPCStreamContext) {
        for (hotFlow in flowContext.awaitInitialized().incomingHotFlows) {
            launch {
                /** Start consuming incoming requests, see [RPCIncomingHotFlow.emit] */
                hotFlow.emit(null)
            }
        }
    }

    private fun serializeRequest(callId: String, callInfo: RPCCallInfo, serialFormat: SerialFormat): RPCMessage {
        val serializerData = serialFormat.serializersModule.rpcSerializerForType(callInfo.dataType)
        val encodedData = when (serialFormat) {
            is StringFormat -> serialFormat.encodeToString(serializerData, callInfo.data)
            else -> error("binary not supported for RPCMessage.CallSuccess")
        }
        return RPCMessage.CallData(callId, serviceTypeString, callInfo.callableName, encodedData)
    }

    private fun prepareSerialFormat(rpcFlowContext: LazyRPCStreamContext): SerialFormat {
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
