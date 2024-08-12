/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RPC
import kotlinx.rpc.withService
import kotlinx.rpc.codegen.test.TestRpcClient

interface BoxService : RPC {
    // plugin should add @Contextual annotation to the flow parameter in the generated class
    suspend fun stream(flow: Flow<String>): String
}

fun box(): String = runBlocking {
    val result = TestRpcClient.withService<BoxService>().stream(flow { })

    if (result == "call_42") "OK" else "Fail: $result"
}
