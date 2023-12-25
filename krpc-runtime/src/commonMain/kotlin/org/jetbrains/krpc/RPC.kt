/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope

/**
 * Marker interface for all RPC services.
 * For each service that inherits this interface kRPC will generate an implementation to use on client side.
 *
 * [CoroutineScope] defines service lifetime.
 */
interface RPC : CoroutineScope {
    companion object
}
