/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.services

import kotlinx.rpc.codegen.RpcFirConfigurationKeys
import kotlinx.rpc.codegen.registerRpcExtensions
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.directives.model.DirectiveApplicability
import org.jetbrains.kotlin.test.directives.model.DirectivesContainer
import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices

class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override val directiveContainers: List<DirectivesContainer> = listOf(RpcDirectives)

    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration,
    ) {
        val annotationTypeSafety = module.directives[RpcDirectives.ANNOTATION_TYPE_SAFETY]
        if (annotationTypeSafety.isNotEmpty()) {
            configuration.put(
                RpcFirConfigurationKeys.ANNOTATION_TYPE_SAFETY,
                annotationTypeSafety.single().toBooleanStrict(),
            )
        }

        registerRpcExtensions(configuration)
    }
}

object RpcDirectives : SimpleDirectivesContainer() {
    val ANNOTATION_TYPE_SAFETY by stringDirective("true or false", DirectiveApplicability.Module)
}
