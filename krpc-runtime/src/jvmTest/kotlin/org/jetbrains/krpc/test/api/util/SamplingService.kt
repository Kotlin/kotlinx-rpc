/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api.util

import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.test.plainFlow
import org.jetbrains.krpc.test.sharedFlowOfT
import org.jetbrains.krpc.test.stateFlowOfT
import kotlin.coroutines.CoroutineContext

@Serializable
data class SamplingData(
    val data: String,
)

interface SamplingService : RPC {
    suspend fun echo(arg1: String, data: SamplingData): SamplingData

    suspend fun clientStream(flow: Flow<Int>): List<Int>

    suspend fun clientNestedStream(flow: Flow<Flow<Int>>): List<List<Int>>

    suspend fun serverFlow(): Flow<SamplingData>

    suspend fun serverNestedFlow(): Flow<Flow<Int>>

    suspend fun callException()

    val plainFlow: Flow<Int>

    val sharedFlow: SharedFlow<Int>

    val stateFlow: StateFlow<Int>

    suspend fun emitNextInStateFlow(next: Int)
}

class SamplingServiceImpl(override val coroutineContext: CoroutineContext) : SamplingService {
    override suspend fun echo(arg1: String, data: SamplingData): SamplingData {
        return data
    }

    override suspend fun clientStream(flow: Flow<Int>): List<Int> {
        return flow.toList()
    }

    override suspend fun clientNestedStream(flow: Flow<Flow<Int>>): List<List<Int>> {
        return flow.map { it.toList() }.toList()
    }

    override suspend fun serverFlow(): Flow<SamplingData> {
        return plainFlow { SamplingData("data") }
    }

    override suspend fun serverNestedFlow(): Flow<Flow<Int>> {
        return plainFlow { plainFlow { it } }
    }

    override suspend fun callException() {
        error("Server exception")
    }

    override val plainFlow: Flow<Int> = plainFlow { it }

    override val sharedFlow: SharedFlow<Int> = sharedFlowOfT { it }

    override val stateFlow: MutableStateFlow<Int> = stateFlowOfT { it }

    override suspend fun emitNextInStateFlow(next: Int) {
        stateFlow.value = next
    }
}
