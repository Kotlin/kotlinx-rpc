package org.jetbrains.krpc

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.krpc.internal.InternalKRPCApi
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
