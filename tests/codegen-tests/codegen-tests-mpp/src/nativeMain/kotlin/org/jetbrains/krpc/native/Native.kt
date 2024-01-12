/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package org.jetbrains.krpc.native

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.CommonService
import org.jetbrains.krpc.EmptyService
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.testService

interface NativeService : RPC, EmptyService {
    override suspend fun empty()

    override val flow: Flow<Int>

    override val sharedFlow: SharedFlow<Int>

    override val stateFlow: StateFlow<Int>
}

fun main() = runBlocking {
    testService<NativeService>()
    testService<CommonService>()
}
