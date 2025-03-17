/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("FunctionName")

package tests

import kotlinx.rpc.krpc.compatibility.CompatibilityTest
import interfaces.BarInterface
import interfaces.BazInterface
import interfaces.FooInterface
import kotlinx.rpc.RpcClient
import kotlinx.rpc.withService
import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend
import kotlin.test.assertEquals

@Suppress("unused")
class CompatibilityTests : CompatibilityTest {
    override fun getAllTests(): Map<String, suspend (RpcClient) -> Unit> {
        return mapOf(
            this::`should work with interface with additional method`.toEntry(),
            this::`should work with newer data class with additional nullable field`.toEntry(),
            this::`should work with newer data class with a field with default value`.toEntry(),
        )
    }

    suspend fun `should work with newer data class with additional nullable field`(rpcClient: RpcClient) {
        val service = rpcClient.withService<FooInterface>()
        val res = service.get()
        assertEquals("", res.field)
    }

    suspend fun `should work with interface with additional method`(rpcClient: RpcClient) {
        val service = rpcClient.withService<BarInterface>()
        service.get()
    }

    suspend fun `should work with newer data class with a field with default value`(rpcClient: RpcClient) {
        val service = rpcClient.withService<BazInterface>()
        val res = service.get()
        assertEquals("asd", res.field)
        // Of course, we can't call the second method
    }

    private fun KCallable<Unit>.toEntry(): Pair<String, suspend (RpcClient) -> Unit> {
        return name to { callSuspend(it) }
    }
}
