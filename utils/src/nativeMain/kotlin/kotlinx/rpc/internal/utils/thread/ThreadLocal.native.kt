/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.Worker

/**
 * Native implementation of currentThreadId that returns the current worker's ID.
 * In Kotlin/Native, we use Worker.current.id as a thread identifier.
 */
@OptIn(ObsoleteWorkersApi::class)
@InternalRpcApi
public actual fun currentThreadId(): Long = Worker.current.id.toLong()
