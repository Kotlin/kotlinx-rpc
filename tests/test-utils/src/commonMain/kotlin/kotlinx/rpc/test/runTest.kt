/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlin.time.Duration

fun runTestWithCoroutinesProbes(
    timeout: Duration,
    body: suspend TestScope.() -> Unit,
): TestResult {
    return withDebugProbes {
        kotlinx.coroutines.test.runTest(timeout = timeout, testBody = body)
    }
}

expect fun <T> withDebugProbes(body: () -> T): T
