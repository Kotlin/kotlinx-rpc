/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class RPCIrExtension(configuration: CompilerConfiguration) : IrGenerationExtension {
    private val logger = configuration.get(VersionSpecificApi.INSTANCE.messageCollectorKey, MessageCollector.NONE)

    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val context = RPCIrContext(pluginContext, VersionSpecificApi.INSTANCE)

        val processor = RPCIrServiceProcessor(logger)
        moduleFragment.transform(processor, context)
    }
}
