/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.RPCIrExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class RPCCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "kotlinx.rpc.compiler-plugin"

    override val pluginOptions = emptyList<CliOption>()
}

@OptIn(ExperimentalCompilerApi::class)
class RPCCompilerPlugin : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        registerRpcExtensions(configuration)
    }
}

@OptIn(ExperimentalCompilerApi::class)
fun CompilerPluginRegistrar.ExtensionStorage.registerRpcExtensions(configuration: CompilerConfiguration) {
    VersionSpecificApi.INSTANCE = VersionSpecificApiImpl

    IrGenerationExtension.registerExtension(RPCIrExtension(configuration))
    FirExtensionRegistrarAdapter.registerExtension(FirRPCExtensionRegistrar(configuration))
}
