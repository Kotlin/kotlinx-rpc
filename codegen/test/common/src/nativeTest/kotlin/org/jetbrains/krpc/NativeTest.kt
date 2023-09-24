package org.jetbrains.krpc

import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.native.NativeService
import kotlin.test.Test

interface NativeTestService : RPC, EmptyService {
    override suspend fun empty()
}

class NativeTest : CommonTestSuite() {
    override fun runAsync(body: suspend () -> Unit) = runBlocking { body() }

    @Test
    fun test() = testServices<NativeService, NativeTestService, CommonService, CommonTestService>()
}
