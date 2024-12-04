/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.rpc.RemoteService
import kotlinx.rpc.withService
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.codegen.test.TestRpcClient

@Rpc
interface BoxService : RemoteService {
    <!FIELD_IN_RPC_SERVICE!>val plainFlow: Flow<String><!>

    <!FIELD_IN_RPC_SERVICE!>val sharedFlow: SharedFlow<String><!>

    <!FIELD_IN_RPC_SERVICE!>val stateFlow: StateFlow<String><!>
}

fun box(): String = runBlocking {
    withTimeoutOrNull(1000) {
        val plainFlow = TestRpcClient.withService<BoxService>().plainFlow.toList()
        val sharedFlow = TestRpcClient.withService<BoxService>().sharedFlow.take(1).toList()
        val stateFlow = TestRpcClient.withService<BoxService>().stateFlow.value

        val failures = mutableListOf<String>()

        if (plainFlow.size != 1 || plainFlow[0] != "registerPlainFlowField_42") {
            failures.add(
                "plainFlow.size = ${plainFlow.size} (expected 1), " +
                        "plainFlow[0] = \"${plainFlow.getOrNull(0)}\" (expected \"registerPlainFlowField_42\")"
            )
        }

        if (sharedFlow.size != 1 || sharedFlow[0] != "registerSharedFlowField_42") {
            failures.add(
                "sharedFlow.size = ${sharedFlow.size} (expected 1), " +
                        "sharedFlow[0] = \"${sharedFlow.getOrNull(0)}\" (expected \"registerSharedFlowField_42\")"
            )
        }

        if (stateFlow != "registerStateFlowField_42") {
            failures.add("stateFlow = \"$stateFlow\" (expected \"registerStateFlowField_42\")")
        }

        failures.takeIf { it.isNotEmpty() }
            ?.joinToString(";")
            ?.let { "Fail: $it" }
            ?: "OK"
    } ?: "Fail: test timed out"
}
