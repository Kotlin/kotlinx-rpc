/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.dokka

import org.jetbrains.dokka.base.testApi.testRunner.BaseAbstractTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HideInternalRpcApiPluginTest : BaseAbstractTest() {
    @Test
    fun `should hide annotated functions`() {
        val configuration = dokkaConfiguration {
            sourceSets {
                sourceSet {
                    sourceRoots = listOf("src/main/kotlin/basic/Test.kt")
                }
            }
        }

        testInline(
            """
            |/src/main/kotlin/basic/Test.kt
            |package kotlinx.rpc.internal.utils
            |
            |annotation class InternalRpcApi
            |
            |fun shouldBeVisible() {}
            |
            |@InternalRpcApi
            |fun shouldBeExcludedFromDocumentation() {}
        """.trimMargin(),
            configuration = configuration,
            pluginOverrides = listOf(RpcDokkaPlugin())
        ) {
            preMergeDocumentablesTransformationStage = { modules ->
                val testModule = modules.single { it.name == "root" }
                val testPackage = testModule.packages.single { it.name == "kotlinx.rpc.internal.utils" }

                val packageFunctions = testPackage.functions
                assertEquals(1, packageFunctions.size)
                assertEquals("shouldBeVisible", packageFunctions[0].name)
            }
        }
    }
}
