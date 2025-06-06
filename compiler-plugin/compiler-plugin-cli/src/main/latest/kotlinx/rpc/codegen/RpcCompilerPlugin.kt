/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.RpcIrExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class RpcCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "kotlinx-rpc"

    override val pluginOptions = listOf(
        RpcFirCliOptions.ANNOTATION_TYPE_SAFETY,
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        when (option) {
            RpcFirCliOptions.ANNOTATION_TYPE_SAFETY -> {
                @Suppress("NullableBooleanElvis")
                configuration.put(
                    RpcFirConfigurationKeys.ANNOTATION_TYPE_SAFETY,
                    value.toBooleanStrictOrNull() ?: true,
                )
            }
        }
    }
}

@OptIn(ExperimentalCompilerApi::class)
class RpcCompilerPlugin : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        registerRpcExtensions(configuration)
    }
}

@OptIn(ExperimentalCompilerApi::class)
fun CompilerPluginRegistrar.ExtensionStorage.registerRpcExtensions(configuration: CompilerConfiguration) {
    VersionSpecificApi.INSTANCE = VersionSpecificApiImpl

    IrGenerationExtension.registerExtension(RpcIrExtension(configuration))
    FirExtensionRegistrarAdapter.registerExtension(FirRpcExtensionRegistrar(configuration))
}
