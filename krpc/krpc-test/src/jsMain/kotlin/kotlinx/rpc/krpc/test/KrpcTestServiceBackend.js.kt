/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.test.TestScope

actual inline fun runThreadIfPossible(runner: () -> Unit) {
    runner()
}

@Suppress("detekt.EmptyFunctionBlock")
internal actual fun debugCoroutines() {
}
