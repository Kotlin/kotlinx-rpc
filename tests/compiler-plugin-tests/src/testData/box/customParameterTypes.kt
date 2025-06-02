/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.rpc.withService
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.codegen.test.TestRpcClient

@Serializable
data class TestData(val value: String)

@Rpc
interface BoxService {
    suspend fun test1(testData: TestData): String

    suspend fun test2(testData: TestData): String
}

fun box(): String = runBlocking {
    val test1 = TestRpcClient.withService<BoxService>().test1(TestData("value"))
    val test2 = TestRpcClient.withService<BoxService>().test2(TestData("value"))

    if (test1 == "call_42" && test2 == "call_42") "OK" else "Fail: test1=$test1, test2=$test2"
}
