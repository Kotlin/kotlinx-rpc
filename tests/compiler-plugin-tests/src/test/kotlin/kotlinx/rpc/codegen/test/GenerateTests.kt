/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "src/testData", testsRoot = "src/test-gen") {
            // KRPC-137 Remove temporary explicit dependencies in 2.1.10 and unmute compiler tests
//            testClass<AbstractDiagnosticTest> {
//                model("diagnostics")
//            }

//            testClass<AbstractBoxTest> {
//                model("box")
//            }
        }
    }
}
