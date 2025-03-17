/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package interfaces

import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

@Serializable
data class Baz(val field: String)

@Rpc
interface BazInterface : RemoteService {
    suspend fun get(): Baz
}

class BazInterfaceImpl(override val coroutineContext: CoroutineContext) : BazInterface {
    override suspend fun get(): Baz = Baz("asd")
}
