/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

actual fun runThreadIfPossible(runner: () -> Unit) {
    Thread(runner).start()
}

@OptIn(ExperimentalCoroutinesApi::class)
internal actual fun CoroutineScope.debugCoroutines() {
    DebugProbes.install()
    launch {
        withContext(Dispatchers.IO) {
            delay(10.seconds)
        }
        DebugProbes.dumpCoroutines()
    }
}
