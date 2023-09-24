package org.jetbrains.krpc

import kotlinx.coroutines.runBlocking

interface MainService : RPC, EmptyService {
    override suspend fun empty()
}

fun main() = runBlocking {
    testService<MainService> { empty() }
    testService<CommonService> { empty() }
}
