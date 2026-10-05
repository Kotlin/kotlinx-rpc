/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * A Kotlin callback type that receives a valid native buffer pointer as argument
 * and may access it within the callback.
 */
internal typealias NativePointerCallback<T> = (CPointer<ByteVar>?) -> T

/**
 * A foreign function signature that takes a Kotlin callback that is
 * called with a native buffer pointer as argument from the foreign runtime.
 */
internal typealias NativeBufferAccessFunc = (callback: NativePointerCallback<Unit>) -> Unit

/**
 * Buffer access on native platforms cause cross language runtime calls, as
 * the call stack traverses Kotlin->Foreign->Kotlin. If the inner Kotlin callback
 * causes an exception, we must capture it on the Kotlin side, a rethrow it,
 * after the buffer access returns from the foreign runtime.
 */
internal object GuardedNativeBufferAccess {
    /**
     * Runs [action] inside [accessBuffer] and rethrows its failure only after [accessBuffer] returns.
     *
     * @param accessBuffer The foreign function that takes a Kotlin callback and calls it with a valid
     * buffer pointer as argument from the foreign runtime.
     * @param action The action that is executed with the retrieved buffer pointer.
     * It may throw an exception and return a result.
     */
    inline fun <T> access(
        accessBuffer: NativeBufferAccessFunc,
        crossinline action: NativePointerCallback<T>,
    ): T {
        var result: Result<T>? = null
        accessBuffer { pointer -> result = runCatching { action(pointer) } }
        return checkNotNull(result) { "accessBuffer returned without invoking its block." }.getOrThrow()
    }
}
