package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.test.Test

interface JsTestService : RPC, EmptyService {
    override suspend fun empty()

    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>
}

class JsTest : CommonTestSuite() {
    override fun runAsync(body: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch { body() }
    }

    @Test
    fun test() = testServices<JsService, JsTestService, CommonService, CommonTestService>()
}
