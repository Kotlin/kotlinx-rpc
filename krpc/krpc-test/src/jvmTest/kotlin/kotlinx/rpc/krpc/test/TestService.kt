/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.annotations.Rpc

@Rpc
interface TestService {
    suspend fun unary(n: Int): Int
    fun serverStreaming(num: Int): Flow<Int>
    suspend fun clientStreaming(n: Flow<Int>): Int
    fun bidiStreaming(flow: Flow<Int>): Flow<Int>
}

class TestServiceImpl : TestService {
    val unaryInvocations = atomic(0)
    val serverStreamingInvocations = atomic(0)
    val clientStreamingInvocations = atomic(0)
    val bidiStreamingInvocations = atomic(0)

    override suspend fun unary(n: Int): Int {
        unaryInvocations.incrementAndGet()
        return n
    }

    override fun serverStreaming(num: Int): Flow<Int> {
        serverStreamingInvocations.incrementAndGet()
        return (1..num).asFlow()
    }

    override suspend fun clientStreaming(n: Flow<Int>): Int {
        clientStreamingInvocations.incrementAndGet()
        return n.toList().sum()
    }

    override fun bidiStreaming(flow: Flow<Int>): Flow<Int> {
        bidiStreamingInvocations.incrementAndGet()
        return flow
    }
}
