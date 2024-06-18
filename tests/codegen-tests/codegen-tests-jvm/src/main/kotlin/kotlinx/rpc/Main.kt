/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.client.withService

interface MainService : RPC, EmptyService {
    @RPCEagerField
    override val flow: Flow<Int>

    override val stateFlow: StateFlow<Int>

    override val sharedFlow: SharedFlow<Int>

    override suspend fun empty()
}

interface FieldOnly : RPC {
    val flow: Flow<Int>
}

fun main(): Unit = runBlocking {
    testService<MainService>()
    testService<CommonService>()
    testService<RootService>()

    stubEngine.withService<FieldOnly>().flow
}
