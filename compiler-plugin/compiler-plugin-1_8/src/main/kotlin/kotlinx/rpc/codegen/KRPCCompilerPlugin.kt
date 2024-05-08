/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class RPCCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "kotlinx.rpc.compiler-plugin"

    override val pluginOptions = emptyList<CliOption>()
}

@OptIn(ExperimentalCompilerApi::class)
class RPCCompilerPlugin : CompilerPluginRegistrar() {
    init {
        VersionSpecificApi.upload(VersionSpecificApiImpl)
    }

    override val supportsK2: Boolean = false

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val extension = RPCCompilerPluginCore.provideExtension(configuration)

        IrGenerationExtension.registerExtension(extension)
    }
}
