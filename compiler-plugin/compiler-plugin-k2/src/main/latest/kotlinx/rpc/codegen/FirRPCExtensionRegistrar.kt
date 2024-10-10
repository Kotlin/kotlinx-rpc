/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension.Factory as CFactory
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension.Factory as GFactory
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension.Factory as SFactory

class FirRpcExtensionRegistrar(private val configuration: CompilerConfiguration) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        +CFactory { FirRpcCheckers(it)  }
        +GFactory { FirRpcServiceGenerator(it, logger) }
        +SFactory { FirRpcSupertypeGenerator(it, logger) }
    }
}
