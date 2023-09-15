package org.jetbrains.krpc.codegen

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

private val MODULE_ID = CompilerConfigurationKey<String>("moduleId")
private val KRPC_DIRECT_DEPENDENCIES = CompilerConfigurationKey<List<String>>("krpcDirectDependencies")

private val ws = Regex("\\s")

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class RPCCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "org.jetbrains.krpc.krpc-compiler-plugin"

    override val pluginOptions = listOf<CliOption>(
        CliOption(
            optionName = "moduleId",
            valueDescription = "string",
            description = "Unique id of this module",
        ),
        CliOption(
            optionName = "krpcDirectDependencies",
            valueDescription = "list",
            description = "List of all module id's that are direct krpc dependencies of this compilaton",
        ),
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option.optionName) {
            pluginOptions[0].optionName -> configuration.put(MODULE_ID, value)
            pluginOptions[1].optionName -> {
                val list = value.split(";").onEach {
                    if (it.contains(ws)) {
                        error("krpcDirectDependencies option should be a list of module id's separated by ';' symbol, no whitespaces.")
                    }
                }

                configuration.appendList(KRPC_DIRECT_DEPENDENCIES, list)
            }

            else -> error("Unsupported plugin option: ${option.optionName}=$value")
        }
    }
}

internal class RPCOptions(
    val moduleId: String,
    val krpcDirectDependencies: List<String>,
)

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class RPCCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2 = false // todo

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val options = RPCOptions(
            moduleId = configuration.get(MODULE_ID) ?: error("Expected $MODULE_ID option"),
            krpcDirectDependencies = configuration.get(KRPC_DIRECT_DEPENDENCIES) ?: error("Expected $KRPC_DIRECT_DEPENDENCIES option"),
        )

        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        IrGenerationExtension.registerExtension(RPCIrExtension(logger, options))
    }
}
