/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api

import org.jetbrains.krpc.internal.transport.RPCMessage
import org.jetbrains.krpc.test.api.ApiTestContext.Companion.NewLine
import org.junit.Test
import kotlin.test.fail

class ApiVersioningTest {
    @Test
    fun testProtocolApiVersion() {
        val context = checkProtocolApi<RPCMessage>()

        if (context.fails.isNotEmpty()) {
            fail(context.fails.joinToString(NewLine.repeat(2), NewLine, NewLine))
        }
    }
}
