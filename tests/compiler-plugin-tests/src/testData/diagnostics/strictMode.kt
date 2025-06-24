/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main
// FILE: b.kt

@file:OptIn(ExperimentalRpcApi::class)

import kotlin.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

data class InnerFlow(
    val flow: Flow<Int>,
)

data class Wrapper<T>(
    val inner: T,
)

data class MultiFlow(
    val flow1: Flow<Int>,
    val flow2: Flow<Int>,
    val flow3: Flow<Int>,
)

@Rpc
interface MyService {
    <!FIELD_IN_RPC_SERVICE!>val flow: Flow<Int><!>
    <!FIELD_IN_RPC_SERVICE!>val stateFlow: StateFlow<Int><!>
    <!FIELD_IN_RPC_SERVICE!>val sharedFlow: SharedFlow<Int><!>
    suspend fun state(<!STATE_FLOW_IN_RPC_SERVICE!>flow: StateFlow<Int><!>): <!STATE_FLOW_IN_RPC_SERVICE!>StateFlow<Int><!>
    suspend fun shared(<!SHARED_FLOW_IN_RPC_SERVICE!>flow: SharedFlow<Int><!>): <!SHARED_FLOW_IN_RPC_SERVICE!>SharedFlow<Int><!>
    suspend fun deepState(<!STATE_FLOW_IN_RPC_SERVICE!>flow: Wrapper<StateFlow<Int>><!>)
    suspend fun deepShared(<!SHARED_FLOW_IN_RPC_SERVICE!>flow: Wrapper<SharedFlow<Int>><!>)
    suspend fun flowOk(flow: Flow<Int>): Int
    fun serverFlowOk(): Flow<Int>
    <!SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE!>suspend fun serverFlowFail(): Flow<Int><!>
    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun notTopLevelServerFlow(): <!NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE!>InnerFlow<!><!>
    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun wrappedNotTopLevelServerFlow(): <!NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE!>Wrapper<Flow<Int>><!><!>
    suspend fun wrappedALot(): Wrapper<Wrapper<Wrapper<Wrapper<Int>>>>
    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun wrappedALotFlow(): <!NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE!>Wrapper<Wrapper<Wrapper<Flow<Int>>>><!><!>
    suspend fun notTopLevelClientFlow(flow: InnerFlow)
    fun nestedServerFlow(): <!NESTED_STREAMING_IN_RPC_SERVICE!>Flow<Flow<Int>><!>
    fun nestedServerTrickyFlow(): <!NESTED_STREAMING_IN_RPC_SERVICE!>Flow<Wrapper<Flow<Int>>><!>
    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun serverMultiFlow(): <!NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE, NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE, NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE!>MultiFlow<!><!>
    suspend fun clientMultiFlow(flow: MultiFlow)
    suspend fun clientMultiFlowMany(flow: MultiFlow, flow2: MultiFlow, flow3: MultiFlow)
    suspend fun clientMultiFlowPlain(flow: Flow<Int>, flow2: Flow<Int>, flow3: Flow<Int>)
    suspend fun clientInnerFlow(inner: InnerFlow)
    suspend fun clientNestedFlow(<!NESTED_STREAMING_IN_RPC_SERVICE!>inner: Flow<Flow<Int>><!>)
    suspend fun clientNestedTrickyFlow(<!NESTED_STREAMING_IN_RPC_SERVICE!>inner: Wrapper<Flow<Wrapper<Flow<Int>>>><!>)
    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun nonSuspendNoFlow()<!>
    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun nonSuspendNoFlowString(): String<!>
}
