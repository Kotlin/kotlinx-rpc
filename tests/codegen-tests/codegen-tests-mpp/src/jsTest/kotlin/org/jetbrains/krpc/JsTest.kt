package org.jetbrains.krpc

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.fail

interface JsTestService : RPC, EmptyService {
    override suspend fun empty()

    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>
}

class JsTest : CommonTestSuite<Promise<Unit>>() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun runAsync(body: suspend () -> Unit): Promise<Unit> {
        return GlobalScope.async(
            Dispatchers.Unconfined,
            block = { body() }
        ).asPromise()
    }

    @Test
    fun test() = testServices<JsService, JsTestService, CommonService, CommonTestService>()
}
