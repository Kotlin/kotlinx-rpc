/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners

import kotlinx.rpc.codegen.test.services.ExtensionRegistrarConfigurator
import kotlinx.rpc.codegen.test.services.RpcCompileClasspathProvider
import kotlinx.rpc.codegen.test.services.RpcRuntimeClasspathProvider
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives

fun TestConfigurationBuilder.commonFirWithPluginFrontendConfiguration() {
    defaultDirectives {
        +FirDiagnosticsDirectives.ENABLE_PLUGIN_PHASES
        +FirDiagnosticsDirectives.FIR_DUMP
        +JvmEnvironmentConfigurationDirectives.FULL_JDK
        +CodegenTestDirectives.IGNORE_DEXING
    }

    useConfigurators(
        ::RpcCompileClasspathProvider,
        ::ExtensionRegistrarConfigurator,
    )

    useCustomRuntimeClasspathProviders(
        ::RpcRuntimeClasspathProvider,
    )
}
