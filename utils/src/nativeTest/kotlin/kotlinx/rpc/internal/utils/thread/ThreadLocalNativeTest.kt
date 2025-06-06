/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.TransferMode
import kotlin.experimental.ExperimentalNativeApi

class ThreadLocalNativeTest {
    @OptIn(ObsoleteWorkersApi::class, ExperimentalNativeApi::class)
    @Test
    fun testThreadLocalInWorkers() {
        val threadLocal = RpcInternalThreadLocal<String>()
        val mainWorkerId = Worker.current.id.toLong()

        // Set a value in the main worker
        threadLocal.set("main-worker-value")

        // Create a new worker and verify it has its own value
        val worker = Worker.start()

        // Execute a task in the worker
        val future = worker.execute(TransferMode.SAFE, { threadLocal }) { tl ->
            // Get the worker's ID
            val workerId = Worker.current.id.toLong()

            // Get the initial value (should be null since we haven't set it in this worker)
            val initialValue = tl.get()

            // Set a new value in this worker
            tl.set("worker-value")

            // Return the worker ID and initial value
            Pair(workerId, initialValue)
        }

        // Wait for the worker to complete and get the results
        val (workerId, workerInitialValue) = future.result

        // Verify the worker had a different ID
        assert(workerId != mainWorkerId) { "Worker should have a different ID" }

        // Verify the worker initially got null (default value)
        assertEquals(null, workerInitialValue)

        // Verify the main worker's value is still intact
        assertEquals("main-worker-value", threadLocal.get())

        // Create another worker to verify isolation
        val worker2 = Worker.start()

        val future2 = worker2.execute(TransferMode.SAFE, { threadLocal }) { tl ->
            // This should be a new worker with its own value
            tl.get()
        }

        // Verify the second worker got null (default value)
        assertEquals(null, future2.result)

        // Clean up
        worker.requestTermination().result
        worker2.requestTermination().result
    }

    @OptIn(ObsoleteWorkersApi::class, ExperimentalNativeApi::class)
    @Test
    fun testMultipleThreadLocalsInWorkers() {
        val threadLocal1 = RpcInternalThreadLocal<String>()
        val threadLocal2 = RpcInternalThreadLocal<Int>()

        // Set values in the main worker
        threadLocal1.set("main-value")
        threadLocal2.set(42)

        // Create a worker and set different values
        val worker = Worker.start()

        val future = worker.execute(TransferMode.SAFE, { Pair(threadLocal1, threadLocal2) }) { (tl1, tl2) ->
            // Set different values in the worker
            tl1.set("worker-value")
            tl2.set(99)

            // Return the values
            Pair(tl1.get(), tl2.get())
        }

        // Get the worker's values
        val (workerValue1, workerValue2) = future.result

        // Verify the worker's values
        assertEquals("worker-value", workerValue1)
        assertEquals(99, workerValue2)

        // Verify the main worker's values are unchanged
        assertEquals("main-value", threadLocal1.get())
        assertEquals(42, threadLocal2.get())

        // Clean up
        worker.requestTermination().result
    }
}
