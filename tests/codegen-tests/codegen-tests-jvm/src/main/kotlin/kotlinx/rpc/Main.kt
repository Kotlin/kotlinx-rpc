/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.client.withService
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate

interface MainService : RPC, EmptyService {
    @RPCEagerField
    override val flow: Flow<Int>

    override val stateFlow: StateFlow<Int>

    override val sharedFlow: SharedFlow<Int>

    override suspend fun empty()
}

@Serializable(with = LocalDateSerializer::class)
@JvmInline
value class LocalDateValue(val value: LocalDate)

class LocalDateSerializer : KSerializer<LocalDateValue> {
    override fun deserialize(decoder: Decoder): LocalDateValue {
        TODO("Not yet implemented")
    }

    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: LocalDateValue) {
        TODO("Not yet implemented")
    }
}

interface DatabaseAbsenceRPC : RPC {
    // ...
    suspend fun getAbsences(date: LocalDateValue)
}

interface FieldOnly : RPC {
    val flow: Flow<Int>
}

fun main(): Unit = runBlocking {
    testService<MainService>()
    testService<CommonService>()
    testService<RootService>()

    stubEngine.withService<DatabaseAbsenceRPC>().apply {
        getAbsences(LocalDateValue(LocalDate.now()))
    }
}
