/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// NOTE: preserve package for compatibility tests
@file:Suppress("PackageDirectoryMismatch")
package org.jetbrains.krpc.test.api.util

import kotlinx.coroutines.flow.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.test.plainFlow
import kotlinx.serialization.Serializable

@Serializable
data class SamplingData(
    val data: String,
)

@Rpc
interface SamplingService {
    suspend fun echo(arg1: String, data: SamplingData): SamplingData

    suspend fun clientStream(flow: Flow<Int>): List<Int>

    fun serverFlow(): Flow<SamplingData>

    suspend fun callException()
}

class SamplingServiceImpl : SamplingService {
    override suspend fun echo(arg1: String, data: SamplingData): SamplingData {
        return data
    }

    override suspend fun clientStream(flow: Flow<Int>): List<Int> {
        return flow.toList()
    }

    override fun serverFlow(): Flow<SamplingData> {
        return plainFlow { SamplingData("data") }
    }

    override suspend fun callException() {
        error("Server exception")
    }
}
