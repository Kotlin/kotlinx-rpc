/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: BACKEND

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.withService
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.codegen.test.TestRpcClient

@Rpc
interface BoxService {
    suspend fun stream(flow: Flow<String>): String
}

fun box(): String = runBlocking {
    val result = TestRpcClient.withService<BoxService>().stream(flow { })

    if (result == "call_42") "OK" else "Fail: $result"
}
