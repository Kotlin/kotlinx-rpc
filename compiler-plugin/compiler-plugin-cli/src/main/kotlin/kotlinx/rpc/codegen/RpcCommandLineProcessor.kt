/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class RpcCommandLineProcessor : CommandLineProcessor {
    override val pluginId = PLUGIN_ID

    override val pluginOptions = listOf(
        RpcFirCliOptions.ANNOTATION_TYPE_SAFETY,
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        when (option) {
            RpcFirCliOptions.ANNOTATION_TYPE_SAFETY -> {
                @Suppress("NullableBooleanElvis")
                configuration.put(
                    RpcFirConfigurationKeys.ANNOTATION_TYPE_SAFETY,
                    value.toBooleanStrictOrNull() ?: true,
                )
            }
        }
    }
}
