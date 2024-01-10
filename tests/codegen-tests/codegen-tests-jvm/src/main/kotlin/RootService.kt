/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused", "detekt.MissingPackageDeclaration")

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.EmptyService
import org.jetbrains.krpc.RPC

interface RootService : EmptyService, RPC {
    suspend fun rootCall(data: RootData): RootResponse

    override val flow: Flow<Int>

    override val stateFlow: StateFlow<Int>

    override val sharedFlow: SharedFlow<Int>

    override suspend fun empty()
}

@Serializable
class RootData(val hello: String)

@Serializable
class RootResponse(val world: String)
