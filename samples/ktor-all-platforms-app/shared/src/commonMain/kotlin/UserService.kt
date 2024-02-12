/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.RPC

@Serializable
data class UserData(
    val address: String,
    val lastName: String,
)

interface UserService : RPC {
    suspend fun hello(user: String, userData: UserData): String

    suspend fun subscribeToNews(): Flow<String>
}
