/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.test.WaitCounter
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

class ConcurrentClientShutdownNativeTest {
    @Test
    fun closingClientResumesActiveCollectorsDuringRequestCleanup() =
        runTestWithCoroutinesProbes(timeout = 30.seconds) {
            withContext(Dispatchers.Default) {
                val failures = Channel<Throwable>(Channel.UNLIMITED)
                val handler = CoroutineExceptionHandler { _, failure -> failures.trySend(failure) }
                val parent = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
                val toolkit = CancellationToolkit(parent)
                val observed = WaitCounter()

                try {
                    // Closing a response channel resumes its collector inline, removing that request.
                    val collectors = List(4) {
                        parent.launch(Dispatchers.Unconfined) {
                            toolkit.service.incomingStream().collect { observed.increment() }
                        }
                    }
                    withTimeout(15.seconds) { observed.await(4) }

                    toolkit.client.close()
                    withTimeout(10.seconds) {
                        toolkit.client.awaitCompletion()
                        collectors.joinAll()
                    }
                    assertNull(failures.tryReceive().getOrNull())
                } finally {
                    try {
                        parent.coroutineContext.job.cancel()
                        withTimeout(10.seconds) { toolkit.close() }
                    } finally {
                        failures.cancel()
                    }
                }
            }
        }
}
