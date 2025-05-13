/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.services

import kotlinx.rpc.codegen.RpcFirConfigurationKeys
import kotlinx.rpc.codegen.StrictMode
import kotlinx.rpc.codegen.StrictModeConfigurationKeys
import kotlinx.rpc.codegen.registerRpcExtensions
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.directives.model.DirectiveApplicability
import org.jetbrains.kotlin.test.directives.model.DirectivesContainer
import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlinx.serialization.compiler.extensions.SerializationComponentRegistrar

class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override val directiveContainers: List<DirectivesContainer> = listOf(RpcDirectives)

    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration,
    ) {
        val strictMode = module.directives[RpcDirectives.RPC_STRICT_MODE]
        if (strictMode.isNotEmpty()) {
            val mode = StrictMode.fromCli(strictMode.single()) ?: StrictMode.ERROR
            configuration.put(StrictModeConfigurationKeys.STATE_FLOW, mode)
            configuration.put(StrictModeConfigurationKeys.SHARED_FLOW, mode)
            configuration.put(StrictModeConfigurationKeys.NESTED_FLOW, mode)
            configuration.put(StrictModeConfigurationKeys.STREAM_SCOPED_FUNCTIONS, mode)
            configuration.put(StrictModeConfigurationKeys.SUSPENDING_SERVER_STREAMING, mode)
            configuration.put(StrictModeConfigurationKeys.NOT_TOP_LEVEL_SERVER_FLOW, mode)
            configuration.put(StrictModeConfigurationKeys.FIELDS, mode)
        }

        val annotationTypeSafety = module.directives[RpcDirectives.ANNOTATION_TYPE_SAFETY]
        if (annotationTypeSafety.isNotEmpty()) {
            configuration.put(
                RpcFirConfigurationKeys.ANNOTATION_TYPE_SAFETY,
                annotationTypeSafety.single().toBooleanStrict(),
            )
        }

        registerRpcExtensions(configuration)

        // libs
        SerializationComponentRegistrar.registerExtensions(this)
    }
}

object RpcDirectives : SimpleDirectivesContainer() {
    val RPC_STRICT_MODE by stringDirective("none, warning or error", DirectiveApplicability.Module)
    val ANNOTATION_TYPE_SAFETY by stringDirective("true or false", DirectiveApplicability.Module)
}
