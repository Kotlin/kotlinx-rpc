/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.checkers.FirSerializablePropertiesProvider
import kotlinx.rpc.codegen.checkers.diagnostics.registerDiagnosticRendererFactories
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension.Factory as CFactory
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension.Factory as DGFactory
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent.Factory as SCFactory
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension.Factory as SGFactory

class FirRpcExtensionRegistrar(private val configuration: CompilerConfiguration) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        val logger = configuration[vsApi { messageCollectorKey }, MessageCollector.NONE]

        +DGFactory { FirRpcServiceGenerator(it, logger) }
        +DGFactory { FirProtobufMessageGenerator(it, logger) }
        +CFactory { FirRpcAdditionalCheckers(it, configuration) }
        +SCFactory { FirSerializablePropertiesProvider(it) }
        +SGFactory { FirRpcServiceSuperTypeExtension(it) }

        registerDiagnosticRendererFactories()
    }
}
