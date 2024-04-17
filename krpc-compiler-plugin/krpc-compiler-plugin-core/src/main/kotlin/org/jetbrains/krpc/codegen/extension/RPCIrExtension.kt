/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen.extension

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.krpc.codegen.VersionSpecificApi

internal class RPCIrExtension(
    private val logger: MessageCollector,
    private val versionSpecificApi: VersionSpecificApi,
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val context = RPCIrContext(pluginContext, versionSpecificApi)

        val processor = RPCIrServiceProcessor(logger)
        moduleFragment.transform(processor, context)
    }
}
