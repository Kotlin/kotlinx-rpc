/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// WITH_STDLIB

import kotlinx.rpc.RPC

interface MyService : RPC {
    suspend fun simple()
}

fun box(): String {
    return "OK"
}
