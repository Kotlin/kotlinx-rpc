/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * JVM implementation of currentThreadId that returns the current thread's ID.
 */
@InternalRpcApi
public actual fun currentThreadId(): Long = Thread.currentThread().id
