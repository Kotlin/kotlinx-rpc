package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

interface MainService : RPC, EmptyService {
    @RPCEagerField
    override val flow: Flow<Int>

    override val stateFlow: StateFlow<Int>

    override val sharedFlow: SharedFlow<Int>

    override suspend fun empty()
}

fun main() = runBlocking {
    testService<MainService>()
    testService<CommonService>()
}
