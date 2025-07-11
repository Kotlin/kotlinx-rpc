/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// NOTE: preserve package for compatibility tests
@file:Suppress("PackageDirectoryMismatch")
@file:UseSerializers(SamplingCustomStringSerializer::class)

package org.jetbrains.krpc.test.api.util

import kotlinx.coroutines.flow.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.test.plainFlow
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class SamplingData(
    val data: String,
)

data class SamplingCustomString(
    val data: String,
)

object SamplingCustomStringSerializer : KSerializer<SamplingCustomString> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("SamplingCustomStringSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SamplingCustomString): Unit = encoder.encodeString(value.data)

    override fun deserialize(decoder: Decoder): SamplingCustomString = SamplingCustomString(decoder.decodeString())
}

@Rpc
interface SamplingService {
    suspend fun echo(arg1: String, data: SamplingData): SamplingData

    suspend fun parseNullableArg(arg1: String, arg2: @Contextual() SamplingCustomString?): String

    suspend fun clientStream(flow: Flow<Int>): List<Int>

    fun serverFlow(): Flow<SamplingData>

    suspend fun callException()
}

class SamplingServiceImpl : SamplingService {
    override suspend fun echo(arg1: String, data: SamplingData): SamplingData {
        return SamplingData("$arg1 ${data.data}")
    }

    override suspend fun parseNullableArg(arg1: String, arg2: SamplingCustomString?): String {
        return "$arg1 ${arg2?.data}"
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
