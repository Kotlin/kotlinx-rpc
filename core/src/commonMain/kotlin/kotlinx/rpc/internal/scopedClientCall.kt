/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.coroutines.CoroutineScope

/**
 * Scopes client RPC call from a service with [serviceScope].
 *
 * Used by code generators.
 */
@InternalRPCApi
@Suppress("unused")
public suspend inline fun <T> scopedClientCall(serviceScope: CoroutineScope, crossinline body: suspend () -> T): T {
    return serviceScoped(serviceScope) {
        body()
    }
}
