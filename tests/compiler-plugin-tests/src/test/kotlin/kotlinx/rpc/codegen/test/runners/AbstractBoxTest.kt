/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.WITH_REFLECT
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirBlackBoxCodegenTestBase
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider

open class AbstractBoxTest : AbstractFirBlackBoxCodegenTestBase(FirParser.LightTree) {
    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }

    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)

        with(builder) {
            /*
             * Containers of different directives, which can be used in tests:
             * - ModuleStructureDirectives
             * - LanguageSettingsDirectives
             * - DiagnosticsDirectives
             * - FirDiagnosticsDirectives
             * - CodegenTestDirectives
             * - JvmEnvironmentConfigurationDirectives
             *
             * All of them are located in `org.jetbrains.kotlin.test.directives` package
             */
            defaultDirectives {
                +DUMP_IR
                +WITH_STDLIB
                +WITH_REFLECT
            }

            commonFirWithPluginFrontendConfiguration()
        }
    }
}
