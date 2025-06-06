/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlin.test.Test
import kotlin.test.assertEquals
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class ThreadLocalJvmTest {
    @Test
    fun testThreadLocalInMultipleThreads() {
        val threadLocal = RpcInternalThreadLocal<String>()
        val mainThreadId = Thread.currentThread().id

        // Set a value in the main thread
        threadLocal.set("main-thread-value")

        // Create a new thread and verify it has its own value
        val latch = CountDownLatch(1)
        var threadIdRef: Long = -1
        var threadValueRef: String? = null

        thread {
            try {
                // This thread should have a different thread ID
                threadIdRef = Thread.currentThread().id

                // The thread local should return the default value since we haven't set it in this thread
                threadValueRef = threadLocal.get()

                // Now set a different value in this thread
                threadLocal.set("worker-thread-value")
            } finally {
                latch.countDown()
            }
        }

        // Wait for the thread to complete
        latch.await()

        // Verify the main thread's value is still intact
        assertEquals("main-thread-value", threadLocal.get())

        // Verify the worker thread had a different ID
        assert(threadIdRef != mainThreadId) { "Worker thread should have a different ID" }

        // Verify the worker thread initially got the default value
        assertEquals(null, threadValueRef)

        // Create another thread to verify the worker thread's value
        val latch2 = CountDownLatch(1)
        var threadValue2Ref: String? = null

        thread {
            try {
                // This should be a new thread with its own value
                threadValue2Ref = threadLocal.get()
            } finally {
                latch2.countDown()
            }
        }

        latch2.await()

        // Verify the second thread got its own default value
        assertEquals(null, threadValue2Ref)
    }
}
