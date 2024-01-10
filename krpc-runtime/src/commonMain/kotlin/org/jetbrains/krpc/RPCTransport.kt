/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class RPCTransport(
    override val coroutineContext: CoroutineContext = Job()
) : CoroutineScope {
    abstract suspend fun send(message: RPCMessage)

    /**
     * Subscribes to RPC messages and executes the specified block when a message is received.
     *
     * @param block The block of code to be executed when a message is received. The block should accept a single
     * parameter of type `RPCMessage` and return a boolean indicating whether the RPCMessage is consumed or not.
     *
     * If the [block] throws an exception, it will be removed from the list of subscribers.
     *
     * @return Unit
     */
    abstract suspend fun subscribe(block: suspend (RPCMessage) -> Boolean)
}
