package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class KRPCCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "org.jetbrains.krpc.krpc-compiler-plugin"

    override val pluginOptions = emptyList<CliOption>()
}

class KRPCCompilerPlugin : ComponentRegistrar {
    init {
        VersionSpecificApi.upload(VersionSpecificApiImpl)
    }

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val extension = KRPCCompilerPluginCore.provideExtension(configuration)

        IrGenerationExtension.registerExtension(project, extension)
    }
}
