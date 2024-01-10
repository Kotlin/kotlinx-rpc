/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.broadcast

class LocalTransport(waiting: Boolean = true) : CoroutineScope {
    override val coroutineContext = Job()
    private val clientIncoming = Channel<RPCMessage>()
    private val serverIncoming = Channel<RPCMessage>()


    val client: RPCTransport = object : RPCTransport(coroutineContext) {

        override suspend fun send(message: RPCMessage) {
            serverIncoming.send(message)
        }

        override suspend fun subscribe(block: suspend (RPCMessage) -> Boolean) {
            for (message in clientIncoming) {
                block(message)
            }
        }
    }.broadcast(waiting)

    val server: RPCTransport = object : RPCTransport(coroutineContext) {
        override suspend fun send(message: RPCMessage) {
            clientIncoming.send(message)
        }

        override suspend fun subscribe(block: suspend (RPCMessage) -> Boolean) {
            for (message in serverIncoming) {
                block(message)
            }
        }
    }.broadcast(waiting)
}
