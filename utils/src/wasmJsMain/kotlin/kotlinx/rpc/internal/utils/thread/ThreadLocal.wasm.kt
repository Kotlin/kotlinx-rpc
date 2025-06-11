/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * WASM JS implementation of currentThreadId.
 * Since WASM JS is single-threaded (similar to regular JS), we return a constant value.
 */
@InternalRpcApi
public actual fun currentThreadId(): Long = 1L // Always return 1 for main thread in WASM JS
