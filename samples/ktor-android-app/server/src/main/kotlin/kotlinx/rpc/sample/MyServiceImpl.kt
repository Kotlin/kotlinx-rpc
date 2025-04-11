/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.sample

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

class MyServiceImpl(override val coroutineContext: CoroutineContext) : MyService {
    override suspend fun hello(user: String, userData: UserData): String {
        return "Nice to meet you $user, how is it in ${userData.address}?"
    }

    override fun subscribeToNews(): Flow<String> {
        return flow {
            repeat(10) {
                delay(200)
                emit("Article number $it")
            }
        }
    }
}