/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package interfaces

import kotlinx.rpc.annotations.Rpc

@Rpc
interface BarInterface {
    suspend fun get()
}

class BarInterfaceImpl() : BarInterface {
    override suspend fun get() {}
}
