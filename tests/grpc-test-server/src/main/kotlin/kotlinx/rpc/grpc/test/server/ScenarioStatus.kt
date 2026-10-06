/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.Status

/** Maps a failure of a control or scenario operation to the status reported to the test client. */
internal fun Throwable.toGrpcStatus(): Status {
    val status = when (this) {
        is IllegalArgumentException -> Status.INVALID_ARGUMENT
        is ScenarioNotFoundException -> Status.NOT_FOUND
        is ScenarioDiscardedException -> Status.FAILED_PRECONDITION
        is ScenarioWaitTimeoutException -> Status.DEADLINE_EXCEEDED
        is InterruptedException -> {
            Thread.currentThread().interrupt()
            Status.CANCELLED
        }
        // Checked after the scenario exceptions above, which extend IllegalStateException.
        is IllegalStateException -> Status.FAILED_PRECONDITION
        else -> Status.INTERNAL
    }
    return status.withDescription(message).withCause(this)
}
