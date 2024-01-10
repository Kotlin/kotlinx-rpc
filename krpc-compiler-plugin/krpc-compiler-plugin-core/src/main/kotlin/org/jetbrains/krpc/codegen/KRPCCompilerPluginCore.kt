/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.krpc.codegen.extension.KRPCIrExtension

object KRPCCompilerPluginCore {
    fun provideExtension(configuration: CompilerConfiguration): IrGenerationExtension {
        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val versionSpecificApi = VersionSpecificApi.loadService()

        return KRPCIrExtension(logger, versionSpecificApi)
    }
}
