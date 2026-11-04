/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.rpc.annotations.Rpc
import org.springframework.stereotype.Service

@Rpc
interface EchoService {
    suspend fun echo(value: String): String

    fun range(count: Int): Flow<Int>

    suspend fun fail(): String
}

@Rpc
interface ReverseService {
    suspend fun reverse(value: String): String
}

@Service
open class TestService : EchoService, ReverseService {
    override suspend fun echo(value: String): String = value

    override fun range(count: Int): Flow<Int> = (0 until count).asFlow()

    override suspend fun fail(): String = error("expected service failure")

    override suspend fun reverse(value: String): String = value.reversed()
}
