package org.jetbrains.krpc.native

import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.CommonService
import org.jetbrains.krpc.EmptyService
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.testService

interface NativeService : RPC, EmptyService {
    override suspend fun empty()
}

fun main() = runBlocking {
    testService<NativeService> { empty() }
    testService<CommonService> { empty() }
}
