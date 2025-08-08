/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main

import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.coroutines.flow.Flow

@Grpc
interface MyService {
    suspend fun unary(value: String): String

    suspend fun clientStreaming(flow: Flow<String>): String

    fun serverStreaming(value: String): Flow<String>

    fun bidiStreaming(flow: Flow<String>): Flow<String>

    suspend fun zeroValues()

    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun checkRegularDiagnosticsWork()<!>

    <!MULTIPLE_PARAMETERS_IN_GRPC_SERVICE!>suspend fun multipleParams(param1: String, param2: String)<!>

    suspend fun nullable(<!NULLABLE_PARAMETER_IN_GRPC_SERVICE!>param1: String?<!>): <!NULLABLE_RETURN_TYPE_IN_GRPC_SERVICE!>String?<!>

    suspend fun innerFlow(<!NON_TOP_LEVEL_CLIENT_STREAMING_IN_RPC_SERVICE!>withFlow: WithFlow<!>)
}

class WithFlow(val flow: Flow<String>)
