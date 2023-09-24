package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.test.Test

interface JsTestService : RPC, EmptyService {
    override suspend fun empty()
}

class JsTest : CommonTestSuite() {
    override fun runAsync(body: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch { body() }
    }

    @Test
    fun test() = testServices<JsService, JsTestService, CommonService, CommonTestService>()
}
