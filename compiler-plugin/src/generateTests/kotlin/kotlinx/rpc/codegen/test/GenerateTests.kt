/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import kotlinx.rpc.codegen.test.runners.AbstractBoxTest
import kotlinx.rpc.codegen.test.runners.AbstractDiagnosticTest
import org.jetbrains.kotlin.compiler.plugin.devkit.generateDevKitTestsWithJUnit5

fun main() = generateDevKitTestsWithJUnit5 {
    testClass<AbstractDiagnosticTest> {
        model("diagnostics")
    }

    testClass<AbstractBoxTest> {
        model("box")
    }
}
