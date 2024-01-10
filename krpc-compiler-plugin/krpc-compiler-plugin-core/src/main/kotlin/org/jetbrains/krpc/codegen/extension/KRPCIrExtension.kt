/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen.extension

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.platform.konan.isNative
import org.jetbrains.krpc.codegen.VersionSpecificApi

internal class KRPCIrExtension(
    private val logger: MessageCollector,
    private val versionSpecificApi: VersionSpecificApi,
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        if (versionSpecificApi.isJs(pluginContext.platform) || pluginContext.platform.isNative()) {
            val context = KRPCIrContext(pluginContext, versionSpecificApi)

            moduleFragment.transform(KRPCIrServiceProcessor(logger), context)
        }
    }
}
