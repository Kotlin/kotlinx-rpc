/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface CommonTestService : RPC, EmptyService {
    override suspend fun empty()

    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>
}

abstract class CommonTestSuite<TestResult> {
    abstract fun runAsync(body: suspend () -> Unit): TestResult

    inline fun <reified S1, reified S2, reified S3, reified S4> testServices(): TestResult
            where S1 : RPC, S2 : RPC, S3 : RPC, S4 : RPC,
                  S1 : EmptyService, S2 : EmptyService, S3 : EmptyService, S4 : EmptyService =
        runAsync {
            testService<S1>()
            testService<S2>()
            testService<S3>()
            testService<S4>()
        }
}
