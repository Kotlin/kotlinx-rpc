/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

internal sealed interface BufferResult<T> {
    class Success<T>(val message: T) : BufferResult<T>
    class Failure<T> : BufferResult<T>
    class Closed<T>(val cause: Throwable?) : BufferResult<T>
}

internal fun <T> BufferResult<T>.getOrNull(): T? {
    return if (this is BufferResult.Success) message else null
}

internal inline fun <T> BufferResult<T>.onFailure(body: () -> Unit): BufferResult<T> {
    if (this is BufferResult.Failure) {
        body()
    }

    return this
}

internal inline fun <T> BufferResult<T>.onClosed(body: (Throwable?) -> Unit): BufferResult<T> {
    if (this is BufferResult.Closed) {
        body(cause)
    }

    return this
}

internal inline val BufferResult<*>.isFailure: Boolean
    get() = this is BufferResult.Failure

internal inline val BufferResult<*>.isSuccess: Boolean
    get() = this is BufferResult.Success

internal inline val BufferResult<*>.isClosed: Boolean
    get() = this is BufferResult.Closed
