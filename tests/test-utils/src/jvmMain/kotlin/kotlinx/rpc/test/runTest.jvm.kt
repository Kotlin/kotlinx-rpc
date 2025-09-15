/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes

@OptIn(ExperimentalCoroutinesApi::class)
actual fun <T> withDebugProbes(body: () -> T): T {
    var result: T? = null
    DebugProbes.withDebugProbes {
        result = body()
    }
    return result!!
}
