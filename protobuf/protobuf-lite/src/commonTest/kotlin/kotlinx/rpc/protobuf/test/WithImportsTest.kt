/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import UseImported
import kotlin.test.Test
import to.be.imported.IWantToBeImported
import kotlin.test.assertEquals

class WithImportsTest {
    @Test
    fun importedFromAnotherModule() {
        val mesasge = UseImported {
            import = IWantToBeImported {
                hello = "hello"
            }
        }

        assertEquals("hello", mesasge.import.hello)
    }
}
