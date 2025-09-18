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

@Grpc(<!WRONG_PROTO_PACKAGE_VALUE!>"-invalid name"<!>)
interface WrongAnnotations1

@Grpc(protoPackage = <!WRONG_PROTO_PACKAGE_VALUE!>"-invalid name"<!>)
interface WrongAnnotations2

@Grpc(protoPackage = <!WRONG_PROTO_PACKAGE_VALUE!>"invalid.name" + " "<!>)
interface WrongAnnotations3

@Grpc
interface WrongAnnotations4 {
    @Grpc.Method(<!WRONG_PROTO_METHOD_NAME_VALUE!>"wrongName+1"<!>)
    suspend fun wrongName1()

    @Grpc.Method(name = <!WRONG_PROTO_METHOD_NAME_VALUE!>"wrongName+2"<!>)
    suspend fun wrongName2()

    @Grpc.Method(name = <!WRONG_PROTO_METHOD_NAME_VALUE!>"wrongName" + "+3"<!>)
    suspend fun wrongName3()

    @Grpc.Method<!WRONG_SAFE_IDEMPOTENT_COMBINATION!>(safe = true, idempotent = false)<!>
    suspend fun wrongSafeIdempotent1()

    @Grpc.Method<!WRONG_SAFE_IDEMPOTENT_COMBINATION!>(safe = true)<!>
    suspend fun wrongSafeIdempotent2()
}
