/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.runners.AbstractFirPhasedDiagnosticTest
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider

open class AbstractDiagnosticTest : AbstractFirPhasedDiagnosticTest(FirParser.LightTree) {
    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }

    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)

        with(builder) {
            defaultDirectives {
                /*
                 * Containers of different directives, which can be used in tests:
                 * - ModuleStructureDirectives
                 * - LanguageSettingsDirectives
                 * - DiagnosticsDirectives
                 * - FirDiagnosticsDirectives
                 *
                 * All of them are located in `org.jetbrains.kotlin.test.directives` package
                 */
            }

            commonFirWithPluginFrontendConfiguration()
        }
    }
}
