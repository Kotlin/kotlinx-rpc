/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

@file:OptIn(ExperimentalRpcApi::class)

import kotlin.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@Rpc
interface MyService {
    <!NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE!>fun hello()<!>

    suspend fun <!TYPE_PARAMETERS_IN_RPC_FUNCTION!><T><!> generic(a: T)

    suspend fun <!TYPE_PARAMETERS_IN_RPC_FUNCTION!><T, T2, T3, T4 : List<T>><!> generic2(a: T, b: Int, c: T4, t2: T2): T3

    <!AD_HOC_POLYMORPHISM_IN_RPC_SERVICE!>suspend fun sameName()<!>

    <!AD_HOC_POLYMORPHISM_IN_RPC_SERVICE!>suspend fun sameName(a: Int)<!>

    <!AD_HOC_POLYMORPHISM_IN_RPC_SERVICE!>suspend fun sameName(a: Int, b: Int)<!>

    <!AD_HOC_POLYMORPHISM_IN_RPC_SERVICE!>suspend fun sameName(a: Int, b: Int, c: Int)<!>

    <!AD_HOC_POLYMORPHISM_IN_RPC_SERVICE!>suspend fun sameName2()<!>

    <!AD_HOC_POLYMORPHISM_IN_RPC_SERVICE!>suspend fun sameName2(a: Int)<!>
}

@Rpc
interface MyServiceT<!TYPE_PARAMETERS_IN_RPC_INTERFACE!><T><!>

@Rpc
interface MyServiceT2<!TYPE_PARAMETERS_IN_RPC_INTERFACE!><T, R, A, B><!>

/* GENERATED_FIR_TAGS: annotationUseSiteTargetFile, classReference, functionDeclaration, interfaceDeclaration,
nullableType, suspend, typeConstraint, typeParameter */
