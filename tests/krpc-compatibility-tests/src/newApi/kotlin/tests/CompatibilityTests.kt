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
import kotlinx.rpc.krpc.KrpcProtocol
import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend
import kotlin.test.assertEquals

@Suppress("unused")
class CompatibilityTests : CompatibilityTest {
    override fun getAllTests(): Map<String, suspend (RpcClient<KrpcProtocol>) -> Unit> {
        return mapOf(
            this::`should work with older data class without nullable field`.toEntry(),
            this::`should work with older interface without method`.toEntry(),
            this::`should work with older data class without a field with default value`.toEntry(),
        )
    }

    suspend fun `should work with older data class without nullable field`(rpcClient: RpcClient<KrpcProtocol>) {
        val service = rpcClient.service<FooInterface>()
        val res = service.get()
        assertEquals("", res.field)
        assertEquals(null, res.field2)
    }

    suspend fun `should work with older interface without method`(rpcClient: RpcClient<KrpcProtocol>) {
        val service = rpcClient.service<BarInterface>()
        service.get()
        // Of course, we can't call the second method
    }

    suspend fun `should work with older data class without a field with default value`(rpcClient: RpcClient<KrpcProtocol>) {
        val service = rpcClient.service<BazInterface>()
        val res = service.get()
        assertEquals("asd", res.field)
        assertEquals("", res.field2)
        // Of course, we can't call the second method
    }

    private fun KCallable<Unit>.toEntry(): Pair<String, suspend (RpcClient<KrpcProtocol>) -> Unit> {
        return name to { callSuspend(it) }
    }
}
