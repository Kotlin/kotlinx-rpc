/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlinx.rpc.internal.utils.InternalRPCApi
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

@InternalRPCApi
public class ServiceScope(public val serviceCoroutineScope: CoroutineScope) : CoroutineContext.Element {
    internal companion object Key : CoroutineContext.Key<ServiceScope>

    override val key: CoroutineContext.Key<*> = Key
}

@InternalRPCApi
public suspend fun createServiceScope(serviceCoroutineScope: CoroutineScope): ServiceScope {
    val context = currentCoroutineContext()

    if (context[ServiceScope.Key] != null) {
        error("serviceScoped nesting is not allowed")
    }

    return ServiceScope(serviceCoroutineScope)
}

@InternalRPCApi
public suspend fun serviceScopeOrNull(): ServiceScope? {
    return currentCoroutineContext()[ServiceScope.Key]
}

@InternalRPCApi
@OptIn(ExperimentalContracts::class)
public suspend inline fun <T> serviceScoped(
    serviceCoroutineScope: CoroutineScope,
    noinline block: suspend CoroutineScope.() -> T,
): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return withContext(createServiceScope(serviceCoroutineScope), block)
}
