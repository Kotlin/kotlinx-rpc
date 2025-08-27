/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.test.TestScope
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.Worker

@OptIn(ObsoleteWorkersApi::class)
actual fun runThreadIfPossible(runner: () -> Unit) {
    Worker.start(errorReporting = true).executeAfter(0L, runner)
}

@Suppress("detekt.EmptyFunctionBlock")
internal actual fun debugCoroutines() {
}
