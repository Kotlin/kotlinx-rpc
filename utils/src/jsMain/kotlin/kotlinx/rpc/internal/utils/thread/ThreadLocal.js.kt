/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * JS implementation of currentThreadId.
 * Since JavaScript is single-threaded (except for Web Workers), we use a global counter
 * to simulate thread IDs. In a real multi-threaded environment with Web Workers,
 * a more sophisticated approach would be needed.
 */
@InternalRpcApi
public actual fun currentThreadId(): Long = 1L // Always return 1 for main thread in JS
