/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.test.WaitCounter
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class ConcurrentClientShutdownNativeTest {
    @Test
    fun closingClientResumesActiveCollectorsDuringRequestCleanup() =
        runTestWithCoroutinesProbes(timeout = 45.seconds) {
            withContext(Dispatchers.Default) {
                val toolkit = CancellationToolkit(this)
                val observed = WaitCounter()
                try {
                    // Closing a response channel resumes its collector inline, removing that request.
                    val collectors = List(4) {
                        launch(Dispatchers.Unconfined) {
                            toolkit.service.incomingStream().collect { observed.increment() }
                        }
                    }
                    withTimeout(15.seconds) { observed.await(4) }

                    toolkit.client.close()
                    withTimeout(10.seconds) {
                        toolkit.client.awaitCompletion()
                        collectors.joinAll()
                    }
                } finally {
                    withContext(NonCancellable) {
                        withTimeout(10.seconds) { toolkit.close() }
                    }
                }
            }
        }
}
