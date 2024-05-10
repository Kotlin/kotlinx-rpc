/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// copied from stdlib, as there it is available only from Kotlin 1.8

@InternalRPCApi
interface AutoCloseable {
    fun close()
}

@InternalRPCApi
@OptIn(ExperimentalContracts::class)
@Suppress("detekt.TooGenericExceptionCaught")
inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.closeFinally(exception)
    }
}

@InternalRPCApi
@PublishedApi
@Suppress("detekt.TooGenericExceptionCaught")
internal fun AutoCloseable?.closeFinally(cause: Throwable?) = when {
    this == null -> {}
    cause == null -> close()
    else ->
        try {
            close()
        } catch (closeException: Throwable) {
            cause.addSuppressed(closeException)
        }
}
