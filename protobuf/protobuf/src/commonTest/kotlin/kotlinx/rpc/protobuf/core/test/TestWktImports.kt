/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.core.test

import Content
import WithWkt
import invoke
import com.google.protobuf.kotlin.Any
import com.google.protobuf.kotlin.pack
import com.google.protobuf.kotlin.unpack
import kotlin.test.Test
import kotlin.test.assertEquals

class TestWktImports {
    @Test
    fun testWktImportsDontClash() {
        val message = WithWkt {
            any = Any.pack(Content {
                content = "some"
            })
        }

        assertEquals("some", message.any.unpack<Content>().content)
    }
}
