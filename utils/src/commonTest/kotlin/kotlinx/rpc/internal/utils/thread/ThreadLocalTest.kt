/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadLocalTest {
    @Test
    fun testGetWithInitialValue() {
        val threadLocal = RpcInternalThreadLocal<String>()
        val value = threadLocal.get()
        assertEquals(null, value)
    }

    @Test
    fun testSetAndGet() {
        val threadLocal = RpcInternalThreadLocal<Int>()
        threadLocal.set(42)
        val value = threadLocal.get()
        assertEquals(42, value)
    }

    @Test
    fun testRemove() {
        val threadLocal = RpcInternalThreadLocal<String>()
        threadLocal.set("value")
        assertEquals("value", threadLocal.get())
        
        threadLocal.remove()
        assertEquals(null, threadLocal.get())
    }

    @Test
    fun testMultipleThreadLocals() {
        val threadLocal1 = RpcInternalThreadLocal<String>()
        val threadLocal2 = RpcInternalThreadLocal<Int>()
        
        threadLocal1.set("value1")
        threadLocal2.set(42)
        
        assertEquals("value1", threadLocal1.get())
        assertEquals(42, threadLocal2.get())
    }
}
