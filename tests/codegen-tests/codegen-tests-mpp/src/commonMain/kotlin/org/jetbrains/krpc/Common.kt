/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.job
import org.jetbrains.krpc.client.withService
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.server.internal.rpcServiceMethodSerializationTypeOf
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.typeOf

val logger by lazy {
    CommonLogger.initialized().logger("KSPGeneratorTest")
}

interface EmptyService {
    val flow: Flow<Int>

    val sharedFlow: SharedFlow<Int>

    val stateFlow: StateFlow<Int>

    suspend fun empty()
}

val stubEngine = object : RPCClient {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun <T> call(call: RPCCall): T {
        logger.info { "Called ${call.callableName}" }
        error("ok")
    }

    override fun <T> registerPlainFlowField(field: RPCField): Flow<T> {
        logger.info { "registered flow: ${field.name}" }
        return flow {  }
    }

    override fun <T> registerSharedFlowField(field: RPCField): SharedFlow<T> {
        logger.info { "registered flow: ${field.name}" }
        return MutableSharedFlow(1)
    }

    override fun <T> registerStateFlowField(field: RPCField): StateFlow<T> {
        logger.info { "registered flow: ${field.name}" }

        @Suppress("UNCHECKED_CAST")
        return MutableStateFlow<Any?>(null) as StateFlow<T>
    }

    override fun provideStubContext(serviceId: Long): CoroutineContext {
        return SupervisorJob(coroutineContext.job)
    }
}

interface CommonService : RPC, EmptyService {
    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>

    override suspend fun empty()
}

suspend inline fun <reified T> testService() where T : RPC, T : EmptyService {
    val test: suspend T.() -> Unit = {
        runCatching {
            empty()
        }

        flow
        sharedFlow
        stateFlow
    }

    stubEngine.withService<T>().test()
    stubEngine.withService<T>(typeOf<T>()).test()
    stubEngine.withService(T::class).test()

    logger.info { rpcServiceMethodSerializationTypeOf<T>("empty") }
    logger.info { rpcServiceMethodSerializationTypeOf(typeOf<T>(), "empty") }
}
