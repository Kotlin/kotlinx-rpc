package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

internal class RPCIrExtension(private val logger: MessageCollector, private val options: RPCOptions) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val context = RPCIrTransformerContext(logger, pluginContext, options)

        moduleFragment.transform(RPCIrServiceInfoFunctionCallsTransformer(logger), context)
    }
}
