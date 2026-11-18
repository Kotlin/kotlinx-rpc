/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import org.jetbrains.kotlin.compiler.plugin.devkit.runners.BackportedFirPhasedDiagnosticTest
import org.jetbrains.kotlin.test.FirParser

/*
 * Containers of different directives, which can be used in tests:
 * - ModuleStructureDirectives
 * - LanguageSettingsDirectives
 * - DiagnosticsDirectives
 * - FirDiagnosticsDirectives
 *
 * All of them are located in `org.jetbrains.kotlin.test.directives` package
 */
open class AbstractDiagnosticTest : BackportedFirPhasedDiagnosticTest(
    {
        commonFirWithPluginFrontendConfiguration()
    },
    parser = FirParser.LightTree,
)
