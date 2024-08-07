/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.services

import kotlinx.rpc.codegen.FirRPCExtensionRegistrar
import kotlinx.rpc.codegen.RPCIrPlugin
import kotlinx.rpc.codegen.VersionSpecificApi
import kotlinx.rpc.codegen.VersionSpecificApiImpl
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlinx.serialization.compiler.extensions.SerializationComponentRegistrar

class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        FirExtensionRegistrarAdapter.registerExtension(FirRPCExtensionRegistrar(configuration))

        VersionSpecificApi.INSTANCE = VersionSpecificApiImpl
        IrGenerationExtension.registerExtension(RPCIrPlugin.provideExtension(configuration))

        // libs
        SerializationComponentRegistrar.registerExtensions(this)
    }
}
