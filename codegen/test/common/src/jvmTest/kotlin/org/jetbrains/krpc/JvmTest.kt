package org.jetbrains.krpc

import kotlinx.coroutines.runBlocking
import org.junit.Test

interface JvmTestService : RPC, EmptyService {
    override suspend fun empty()
}

class JvmTest : CommonTestSuite() {
    override fun runAsync(body: suspend () -> Unit) = runBlocking { body() }

    @Test
    fun test() = testServices<JvmService, JvmTestService, CommonService, CommonTestService>()
}
