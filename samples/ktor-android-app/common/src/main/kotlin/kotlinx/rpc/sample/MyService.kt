/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.sample

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC

interface MyService : RPC {
    suspend fun hello(user: String, userData: UserData): String

    suspend fun subscribeToNews(): Flow<String>
}