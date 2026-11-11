/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: BACKEND

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.codegen.test.TestRpcClient
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@Rpc
interface BoxService {
    suspend fun simple(): String
}

@OptIn(ExperimentalRpcApi::class)
fun box(): String = runBlocking {
    val descriptor = serviceDescriptorOf<BoxService>()
    val result = descriptor.callables["simple"]?.name

    if (result == "simple") "OK" else "Fail: $result"
}

/* GENERATED_FIR_TAGS: functionDeclaration, interfaceDeclaration, suspend */
