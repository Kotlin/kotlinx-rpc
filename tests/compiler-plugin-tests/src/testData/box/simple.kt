/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// WITH_STDLIB
// WITH_REFLECT

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RPC
import kotlinx.rpc.withService
import kotlinx.rpc.codegen.test.TestRpcClient

interface MyService : RPC {
    suspend fun simple(): String
}

fun box(): String = runBlocking {
    val result = TestRpcClient.withService<MyService>().simple()

    if (result == "call_42") "OK" else "Fail: $result"
}
