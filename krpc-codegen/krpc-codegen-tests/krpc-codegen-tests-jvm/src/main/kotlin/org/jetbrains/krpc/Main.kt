package org.jetbrains.krpc

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

interface MainService : RPC, EmptyService {
//    val stateFlow: StateFlow<Int>
//
//    val sharedFlow: SharedFlow<Int>

    override suspend fun empty()
}

fun main() = runBlocking {
    testService<MainService> { empty() }
    testService<CommonService> { empty() }
}
