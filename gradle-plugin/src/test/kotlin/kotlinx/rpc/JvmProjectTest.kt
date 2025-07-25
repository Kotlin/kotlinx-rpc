/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.base.BaseTest
import kotlin.test.Test

class JvmProjectTest : BaseTest() {
    @Test
    fun `Minimal Configuration`() {
        val result = runGradle("help")

        assert(result.output.contains("BUILD SUCCESSFUL")) {
            "Build should be successful, but got: ${result.output}"
        }
    }
}
