/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.extension.RpcIrExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
fun CompilerPluginRegistrar.ExtensionStorage.registerRpcExtensions(configuration: CompilerConfiguration) {
    VersionSpecificApi.INSTANCE = VersionSpecificApiImpl

    IrGenerationExtension.registerExtension(RpcIrExtension(configuration))
    FirExtensionRegistrarAdapter.registerExtension(FirRpcExtensionRegistrar(configuration))
}
