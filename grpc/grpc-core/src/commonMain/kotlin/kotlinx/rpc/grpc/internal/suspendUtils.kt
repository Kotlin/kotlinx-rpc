/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.internal.utils.InternalRpcApi

// todo really needed?
@InternalRpcApi
public fun <T> Flow<T>.singleOrStatusFlow(
    expected: String,
    descriptor: Any,
): Flow<T> = flow {
    var found = false
    collect {
        if (!found) {
            found = true
            emit(it)
        } else {
            throw StatusException(
                Status(StatusCode.INTERNAL, "Expected one $expected for $descriptor but received two")
            )
        }
    }

    if (!found) {
        throw StatusException(
            Status(StatusCode.INTERNAL, "Expected one $expected for $descriptor but received none")
        )
    }
}

@InternalRpcApi
public suspend fun <T> Flow<T>.singleOrStatus(
    expected: String,
    descriptor: Any,
): T = singleOrStatusFlow(expected, descriptor).single()

@InternalRpcApi
public class Ready(private val isReallyReady: () -> Boolean) {
    // A CONFLATED channel never suspends to send, and two notifications of readiness are equivalent
    // to one
    private val channel = Channel<Unit>(Channel.CONFLATED)

    public fun onReady() {
        channel.trySend(Unit).onFailure { e ->
            throw e ?: AssertionError(
                "Should be impossible; a CONFLATED channel should never return false on offer"
            )
        }
    }

    public suspend fun suspendUntilReady() {
        while (!isReallyReady()) {
            channel.receive()
        }
    }
}