/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.compiler.plugin.devkit.runners.BackportedJvmBlackBoxCodegenTestBase
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.WITH_REFLECT

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
open class AbstractBoxTest : BackportedJvmBlackBoxCodegenTestBase(
    {
        defaultDirectives {
            +DUMP_IR
            +WITH_STDLIB
            +WITH_REFLECT
        }

        commonFirWithPluginFrontendConfiguration()
    },
    parser = FirParser.LightTree,
)
