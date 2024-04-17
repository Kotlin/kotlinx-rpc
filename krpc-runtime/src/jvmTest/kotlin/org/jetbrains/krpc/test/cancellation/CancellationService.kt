/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.cancellation

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import org.jetbrains.krpc.RPC
import kotlin.coroutines.CoroutineContext

interface CancellationService : RPC {
    suspend fun serverDelay(millis: Long)

    suspend fun callException()
}

class CancellationServiceImpl(override val coroutineContext: CoroutineContext) : CancellationService {
    val delayCounter = atomic(0)

    override suspend fun serverDelay(millis: Long) {
        delay(millis)
        delayCounter.incrementAndGet()
    }

    override suspend fun callException() {
        error("callException")
    }
}
