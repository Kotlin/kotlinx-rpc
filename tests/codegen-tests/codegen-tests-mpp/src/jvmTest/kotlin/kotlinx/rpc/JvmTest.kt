/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test

interface JvmTestService : RPC, EmptyService {
    override suspend fun empty()

    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>
}

class JvmTest : CommonTestSuite<Unit>() {
    override fun runAsync(body: suspend () -> Unit) = runBlocking { body() }

    @Test
    fun test() = testServices<JvmService, JvmTestService, CommonService, CommonTestService>()
}
