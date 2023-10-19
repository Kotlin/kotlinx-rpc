package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.konan.isNative

internal class KRPCIrExtension(private val logger: MessageCollector) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        if (pluginContext.platform.isJs() || pluginContext.platform.isNative()) {
            val context = KRPCIrContext(pluginContext)

            moduleFragment.transform(KRPCIrServiceProcessor(logger), context)
        }
    }
}
