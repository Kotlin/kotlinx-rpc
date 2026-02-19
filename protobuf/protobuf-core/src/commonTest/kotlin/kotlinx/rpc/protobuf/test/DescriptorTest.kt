/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import Equals
import com.google.protobuf.kotlin.Timestamp
import kotlinx.rpc.protobuf.internal.protoDescriptorOf
import kotlin.test.Test
import kotlin.test.assertEquals

class DescriptorTest {

    @Test
    fun `get descriptor and check fullName`() {
        val descriptor = protoDescriptorOf<Timestamp>()
        assertEquals("google.protobuf.Timestamp", descriptor.fullName)
    }

    @Test
    fun `get descriptor nested class`() {
        val descriptor = protoDescriptorOf<Equals.Nested>()
        assertEquals("Equals.Nested", descriptor.fullName)
    }

}