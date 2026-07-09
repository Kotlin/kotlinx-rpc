/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: BACKEND

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.descriptor.RpcInvokator
import kotlinx.rpc.descriptor.flowInvokator
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.descriptor.unaryInvokator
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@Rpc
interface BoxService {
    suspend fun withDefaults(a: Int, b: String = "b", c: String? = "c"): String

    fun streamWithDefaults(a: Int, b: String = "s"): Flow<String>
}

class BoxServiceImpl : BoxService {
    override suspend fun withDefaults(a: Int, b: String, c: String?): String = "$a-$b-$c"

    override fun streamWithDefaults(a: Int, b: String): Flow<String> = flowOf("$a-$b")
}

@OptIn(ExperimentalRpcApi::class)
fun box(): String = runBlocking {
    val callable = serviceDescriptorOf<BoxService>().getCallable("withDefaults")
        ?: return@runBlocking "Fail: no callable"

    val service = BoxServiceImpl()

    val allAbsent = callable.unaryInvokator.call(service, arrayOf<Any?>(1, RpcInvokator.Absent, RpcInvokator.Absent))
    if (allAbsent != "1-b-c") return@runBlocking "Fail: $allAbsent"

    // null is treated as absent for parameters of non-nullable types (#543)
    val nullForNonNullable = callable.unaryInvokator.call(service, arrayOf<Any?>(2, null, "override"))
    if (nullForNonNullable != "2-b-override") return@runBlocking "Fail: $nullForNonNullable"

    // explicit null for a parameter of a nullable type is not absent
    val explicitNull = callable.unaryInvokator.call(service, arrayOf<Any?>(3, "override", null))
    if (explicitNull != "3-override-null") return@runBlocking "Fail: $explicitNull"

    val allPresent = callable.unaryInvokator.call(service, arrayOf<Any?>(4, "b2", "c2"))
    if (allPresent != "4-b2-c2") return@runBlocking "Fail: $allPresent"

    val streamCallable = serviceDescriptorOf<BoxService>().getCallable("streamWithDefaults")
        ?: return@runBlocking "Fail: no stream callable"

    val streamAbsent = streamCallable.flowInvokator.call(service, arrayOf<Any?>(5, RpcInvokator.Absent)).single()
    if (streamAbsent != "5-s") return@runBlocking "Fail: $streamAbsent"

    val streamNullForNonNullable = streamCallable.flowInvokator.call(service, arrayOf<Any?>(6, null)).single()
    if (streamNullForNonNullable != "6-s") return@runBlocking "Fail: $streamNullForNonNullable"

    val streamPresent = streamCallable.flowInvokator.call(service, arrayOf<Any?>(7, "s2")).single()
    if (streamPresent != "7-s2") return@runBlocking "Fail: $streamPresent"

    "OK"
}
