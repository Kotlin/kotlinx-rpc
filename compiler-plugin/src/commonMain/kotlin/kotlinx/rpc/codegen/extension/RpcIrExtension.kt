/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.RpcIrProtoProcessorDelegate
import kotlinx.rpc.codegen.RpcIrServiceProcessorDelegate
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import kotlinx.rpc.codegen.messageCollectorKeyCompat

class RpcIrExtension(configuration: CompilerConfiguration) : IrGenerationExtension {
    private val logger = configuration.get(messageCollectorKeyCompat, MessageCollector.NONE)

    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val context = RpcIrContext(pluginContext)

        val serviceProcessor = RpcIrServiceProcessorDelegate(RpcIrServiceProcessor(logger))
        moduleFragment.transform(serviceProcessor, context)

        val protoProcessor = RpcIrProtoProcessorDelegate(RpcIrProtoProcessor(logger))
        moduleFragment.transform(protoProcessor, context)
    }
}
