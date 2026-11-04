/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.coroutines.delay
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/** Thrown when no readiness call succeeds within the configured timeout. */
internal class ReadinessTimeoutException(
    val target: String,
    val timeout: Duration,
    val lastError: String?,
) : Exception(buildString {
    append("The benchmark server at '$target' did not accept a readiness call within $timeout.")
    lastError?.let { append(" Last error: $it") }
})

/**
 * Calls the server until one call succeeds, and returns the time from the first call to that
 * success. The first call pays for the connection setup, so the returned duration is an upper
 * bound for the connect time of the channel.
 *
 * A call that fails because the server isn't listening yet is retried after a short delay.
 */
internal suspend fun awaitReadiness(
    client: GrpcClient,
    backend: BenchmarkBackend,
    target: String,
    timeout: Duration,
): Duration {
    val unaryCall = backend.prepareUnaryCall(client)
    val request = benchmarkRequest(requestBytes = 0, responseBytes = 0)
    val started = TimeSource.Monotonic.markNow()
    var lastError: String? = null

    while (started.elapsedNow() < timeout) {
        try {
            unaryCall(request)
            return started.elapsedNow()
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            lastError = error.message ?: error::class.simpleName
            delay(READINESS_RETRY_DELAY)
        }
    }
    throw ReadinessTimeoutException(target, timeout, lastError)
}

private val READINESS_RETRY_DELAY = 250.milliseconds
