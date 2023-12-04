package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.native.NativeService
import kotlin.test.Test

interface NativeTestService : RPC, EmptyService {
    override suspend fun empty()

    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>
}

class NativeTest : CommonTestSuite<Unit>() {
    override fun runAsync(body: suspend () -> Unit) = runBlocking { body() }

    @Test
    fun test() = testServices<NativeService, NativeTestService, CommonService, CommonTestService>()
}
