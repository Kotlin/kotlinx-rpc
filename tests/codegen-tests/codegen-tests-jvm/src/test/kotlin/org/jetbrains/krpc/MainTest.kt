/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

interface MainServiceTest : RPC, EmptyService {
    override suspend fun empty()

    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>
}

class MainTest {
    @Test
    fun test() = testServices<MainService, MainServiceTest, CommonService>()

    private inline fun <reified S1, reified S2, reified S3> testServices()
            where S1 : RPC, S2 : RPC, S3 : RPC,
                  S1 : EmptyService, S2 : EmptyService, S3 : EmptyService =
        runBlocking {
            testService<S1>()
            testService<S2>()
            testService<S3>()
        }
}
