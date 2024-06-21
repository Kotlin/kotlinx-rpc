/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.coroutines.CoroutineScope

@Deprecated(
    message = "streamScoped function was moved out of internal package. Please, use the public one",
    replaceWith = ReplaceWith(
        "streamScoped(block)",
        "kotlinx.rpc.streamScoped",
    ),
    level = DeprecationLevel.WARNING
)
public suspend fun <T> streamScoped(block: suspend CoroutineScope.() -> T): T {
    return kotlinx.rpc.streamScoped(block)
}

@Deprecated(
    message = "invokeOnStreamScopeCompletion function was moved out of internal package. Please, use the public one",
    replaceWith = ReplaceWith(
        "kotlinx.rpc.invokeOnStreamScopeCompletion(throwIfNoScope, block)",
        "kotlinx.rpc.invokeOnStreamScopeCompletion",
    ),
    level = DeprecationLevel.WARNING
)
@ExperimentalRPCApi
public suspend fun invokeOnStreamScopeCompletion(throwIfNoScope: Boolean = true, block: (Throwable?) -> Unit) {
    return kotlinx.rpc.invokeOnStreamScopeCompletion(throwIfNoScope, block)
}
