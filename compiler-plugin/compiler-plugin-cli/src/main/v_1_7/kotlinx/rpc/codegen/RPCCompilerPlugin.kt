/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.RPCIrExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class RPCCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "kotlinx.rpc.compiler-plugin"

    override val pluginOptions = emptyList<CliOption>()
}

class RPCCompilerPlugin : ComponentRegistrar {
    init {
        VersionSpecificApi.INSTANCE = VersionSpecificApiImpl
    }

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(project, RPCIrExtension(configuration))
    }
}
