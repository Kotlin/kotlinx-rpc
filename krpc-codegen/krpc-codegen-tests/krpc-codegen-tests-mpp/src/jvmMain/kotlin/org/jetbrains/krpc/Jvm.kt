package org.jetbrains.krpc

import kotlinx.coroutines.runBlocking

interface JvmService : RPC, EmptyService {
    override suspend fun empty()
}

fun main() = runBlocking {
    testService<JvmService> { empty() }
    testService<CommonService> { empty() }
}
