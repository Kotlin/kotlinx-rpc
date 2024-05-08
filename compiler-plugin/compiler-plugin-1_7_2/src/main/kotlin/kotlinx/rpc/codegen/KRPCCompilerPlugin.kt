/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class KRPCCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "kotlinx.rpc.krpc-compiler-plugin"

    override val pluginOptions = emptyList<CliOption>()
}

class KRPCCompilerPlugin : ComponentRegistrar {
    init {
        VersionSpecificApi.upload(VersionSpecificApiImpl)
    }

    override val supportsK2: Boolean = false

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val extension = KRPCCompilerPluginCore.provideExtension(configuration)

        IrGenerationExtension.registerExtension(project, extension)
    }
}
