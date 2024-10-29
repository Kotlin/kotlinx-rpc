/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.rpc.annotations.Rpc

/**
 * Marker interface for an RPC service.
 * Provides type safety and [CoroutineScope] for [Rpc] annotated services.
 *
 * Every [RemoteService] service MUST be annotated with [Rpc] annotation.
 *
 * @see Rpc
 */
public interface RemoteService : CoroutineScope

@Deprecated(
    message = "Deprecated in favor of RemoteService. Will be removed in 0.5.0",
    replaceWith = ReplaceWith("RemoteService", "kotlinx.rpc.RemoteService"),
    level = DeprecationLevel.ERROR,
)
public interface RPC : CoroutineScope