/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package interfaces

import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlin.coroutines.CoroutineContext

@Rpc
interface BarInterface : RemoteService {
    suspend fun get(): Unit
    suspend fun get2(): Unit
}

class BarInterfaceImpl(override val coroutineContext: CoroutineContext) : BarInterface {
    override suspend fun get() {}

    override suspend fun get2() {}
}
