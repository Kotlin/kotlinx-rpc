/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Guards a native resource against double-free between an explicit release path and the GC
 * cleaner fallback. Used with `kotlin.native.ref.createCleaner` — the cleaner lambda must capture
 * the guard via its argument (e.g., `createCleaner(Pair(raw, guard)) { ... }`), never by closing
 * over an enclosing instance.
 */
@InternalRpcApi
public class ResourceGuard {
    public val released: AtomicBoolean = atomic(false)
}
