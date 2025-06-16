/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.checkers.FirSerializablePropertiesProvider
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension.Factory as CFactory
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension.Factory as GFactory
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent.Factory as SCFactory

class FirRpcExtensionRegistrar(private val configuration: CompilerConfiguration) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        +GFactory { FirRpcServiceGenerator(it, logger) }
        +CFactory { FirRpcAdditionalCheckers(it, configuration) }
        +SCFactory { FirSerializablePropertiesProvider(it) }
    }
}
