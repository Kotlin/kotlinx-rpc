/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Runs cleanup steps independently and records their failures.
 * Each step uses [Dispatchers.Default], so its timeout uses real time under `runTest`.
 * Use [kotlinx.coroutines.NonCancellable] around cleanup that must run after caller cancellation.
 */
public class TestCleanup(public val stepTimeout: Duration = 3.seconds) {
    private var recordedFailure: Throwable? = null

    /**
     * Records step failures so later steps can run.
     * If [name] is set, a timeout becomes an [AssertionError] that identifies the step.
     */
    public suspend fun run(name: String? = null, timeout: Duration = stepTimeout, block: suspend () -> Unit) {
        try {
            withContext(Dispatchers.Default) {
                withTimeout(timeout) { block() }
            }
        } catch (error: Throwable) {
            val failure = if (name != null && error is TimeoutCancellationException) {
                AssertionError("Teardown step '$name' didn't finish in $timeout", error)
            } else {
                error
            }
            recordedFailure = combineFailures(recordedFailure, failure)
        }
    }

    /** Returns the first failure with later failures suppressed, or `null` if every step succeeded. */
    public fun failure(): Throwable? = recordedFailure
}

public fun combineFailures(primaryFailure: Throwable?, secondaryFailure: Throwable?): Throwable? {
    if (primaryFailure == null) {
        return secondaryFailure
    }
    if (secondaryFailure != null && secondaryFailure !== primaryFailure) {
        primaryFailure.addSuppressed(secondaryFailure)
    }
    return primaryFailure
}
