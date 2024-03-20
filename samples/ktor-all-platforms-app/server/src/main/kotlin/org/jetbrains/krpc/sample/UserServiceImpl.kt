/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.sample

import UserData
import UserService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

class UserServiceImpl : UserService {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun hello(user: String, userData: UserData): String {
        return "Nice to meet you $user, how is it in ${userData.address}?"
    }

    override suspend fun subscribeToNews(): Flow<String> {
        return flow {
            repeat(10) {
                delay(300)
                emit("Article number $it")
            }
        }
    }
}
