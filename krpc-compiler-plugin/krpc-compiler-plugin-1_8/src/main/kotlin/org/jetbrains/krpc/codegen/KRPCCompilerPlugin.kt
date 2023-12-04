package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KRPCCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "org.jetbrains.krpc.krpc-compiler-plugin"

    override val pluginOptions = emptyList<CliOption>()
}

@OptIn(ExperimentalCompilerApi::class)
class KRPCCompilerPlugin : CompilerPluginRegistrar() {
    init {
        VersionSpecificApi.upload(VersionSpecificApiImpl)
    }

    override val supportsK2: Boolean = false

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val extension = KRPCCompilerPluginCore.provideExtension(configuration)

        IrGenerationExtension.registerExtension(extension)
    }
}
