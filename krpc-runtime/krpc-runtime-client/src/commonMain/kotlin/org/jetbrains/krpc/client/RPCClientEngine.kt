package org.jetbrains.krpc.client

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.*
import org.jetbrains.krpc.internal.InternalKRPCApi
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private val CLIENT_ENGINE_ID = atomic(initial = 0L)

internal class RPCClientEngine(
    private val transport: RPCTransport,
    serviceType: KType,
    private val config: RPCConfig.Client = RPCConfig.Client.Default,
) : RPCEngine {
    private val callCounter = atomic(0L)
    private val engineId: Long = CLIENT_ENGINE_ID.incrementAndGet()
    private val logger = KotlinLogging.logger("RPCClientEngine[0x${hashCode().toString(16)}]")
    private val serviceTypeString = serviceType.toString()

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

    @Serializable
    object FieldDataObject : RPCMethodClassArguments {
        override fun asArray(): Array<Any?> = emptyArray()
    }

    override suspend fun call(callInfo: RPCCallInfo, deferred: CompletableDeferred<*>): Any? {
        val (callContext, json) = prepareAndExecuteCall(callInfo, deferred)

        launch {
            handleOutgoingFlows(callContext, json)
        }

        launch {
            handleIncomingHotFlows(callContext)
        }

        return deferred.await()
    }

    private suspend fun <T> prepareAndExecuteCall(
        callInfo: RPCCallInfo,
        deferred: CompletableDeferred<T>,
    ): Pair<LazyRPCStreamContext, Json> {
        val id = callCounter.incrementAndGet()
        val callId = "$engineId:${callInfo.dataType}:$id"

        logger.trace { "start a call[$callId] ${callInfo.callableName}" }

        val flowContext = LazyRPCStreamContext { RPCStreamContext(callId, config) }
        val json = prepareJson(flowContext)
        val firstMessage = serializeRequest(callId, callInfo, json)

        @Suppress("UNCHECKED_CAST")
        executeCall(callId, flowContext, callInfo, firstMessage, json, deferred as CompletableDeferred<Any?>)

        return flowContext to json
    }

    @OptIn(InternalKRPCApi::class)
    private suspend fun executeCall(
        callId: String,
        flowContext: LazyRPCStreamContext,
        callInfo: RPCCallInfo,
        firstMessage: RPCMessage,
        json: Json,
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
                            val serializerResult = json.serializersModule.rpcSerializerForType(callInfo.returnType)
                            json.decodeFromString(serializerResult, message.data)
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
                        flowContext.initialize().send(message, json)
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

    @OptIn(InternalKRPCApi::class)
    private suspend fun handleOutgoingFlows(flowContext: LazyRPCStreamContext, json: Json) {
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
                                    val data = json.encodeToString(elementSerializer, it)
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

    private fun serializeRequest(callId: String, callInfo: RPCCallInfo, json: StringFormat): RPCMessage {
        val serializerData = json.serializersModule.rpcSerializerForType(callInfo.dataType)
        val jsonData = json.encodeToString(serializerData, callInfo.data)
        return RPCMessage.CallData(callId, serviceTypeString, callInfo.callableName, jsonData)
    }

    private fun prepareJson(rpcFlowContext: LazyRPCStreamContext): Json = Json {
        serializersModule = SerializersModule {
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

            config.serializersModuleExtension?.invoke(this)
        }
    }
}
