/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package interfaces

import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable

@Serializable
data class Baz(val field: String, val field2: String = "")

@Rpc
interface BazInterface {
    suspend fun get(): Baz
}

class BazInterfaceImpl : BazInterface {
    override suspend fun get(): Baz = Baz("asd", "def")
}
