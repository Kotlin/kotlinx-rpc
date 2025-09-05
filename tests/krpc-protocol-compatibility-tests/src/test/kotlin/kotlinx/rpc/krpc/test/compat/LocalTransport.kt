/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.compat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.job
import kotlin.coroutines.CoroutineContext

class LocalTransport(parentScope: CoroutineScope? = null) : CoroutineScope {
    override val coroutineContext = parentScope?.run { SupervisorJob(coroutineContext.job) }
        ?: SupervisorJob()

    private val clientIncoming = Channel<String>()
    private val serverIncoming = Channel<String>()

    val client: CompatTransport = object : CompatTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext.job)

        override suspend fun send(message: String) {
            serverIncoming.send(message)
        }

        override suspend fun receive(): String {
            return clientIncoming.receive()
        }
    }

    val server: CompatTransport = object : CompatTransport {
        override val coroutineContext: CoroutineContext = Job(this@LocalTransport.coroutineContext)

        override suspend fun send(message: String) {
            clientIncoming.send(message)
        }

        override suspend fun receive(): String {
            return serverIncoming.receive()
        }
    }
}
