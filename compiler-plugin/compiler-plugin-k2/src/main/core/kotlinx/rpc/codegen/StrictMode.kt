/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import kotlin.text.lowercase

enum class StrictMode {
    NONE, WARNING, ERROR;

    companion object {
        fun fromCli(value: String): StrictMode? {
            return when (value.lowercase()) {
                "none" -> NONE
                "warning" -> WARNING
                "error" -> ERROR
                else -> null
            }
        }
    }
}

data class StrictModeAggregator(
    val stateFlow: StrictMode,
    val sharedFlow: StrictMode,
    val nestedFlow: StrictMode,
    val streamScopedFunctions: StrictMode,
    val suspendingServerStreaming: StrictMode,
    val notTopLevelServerFlow: StrictMode,
    val fields: StrictMode,
)

object StrictModeConfigurationKeys {
    val STATE_FLOW = CompilerConfigurationKey.create<StrictMode>("state flow rpc mode")
    val SHARED_FLOW = CompilerConfigurationKey.create<StrictMode>("shared flow rpc mode")
    val NESTED_FLOW = CompilerConfigurationKey.create<StrictMode>("nested flow rpc mode")
    val STREAM_SCOPED_FUNCTIONS = CompilerConfigurationKey.create<StrictMode>("stream scoped rpc mode")
    val SUSPENDING_SERVER_STREAMING = CompilerConfigurationKey.create<StrictMode>(
        "suspending server streaming rpc mode"
    )
    val NOT_TOP_LEVEL_SERVER_FLOW = CompilerConfigurationKey.create<StrictMode>("not top level server flow rpc mode")
    val FIELDS = CompilerConfigurationKey.create<StrictMode>("fields rpc mode")
}

fun CompilerConfiguration.strictModeAggregator(): StrictModeAggregator {
    return StrictModeAggregator(
        stateFlow = get(StrictModeConfigurationKeys.STATE_FLOW, StrictMode.WARNING),
        sharedFlow = get(StrictModeConfigurationKeys.SHARED_FLOW, StrictMode.WARNING),
        nestedFlow = get(StrictModeConfigurationKeys.NESTED_FLOW, StrictMode.WARNING),
        streamScopedFunctions = get(StrictModeConfigurationKeys.STREAM_SCOPED_FUNCTIONS, StrictMode.NONE),
        suspendingServerStreaming = get(StrictModeConfigurationKeys.SUSPENDING_SERVER_STREAMING, StrictMode.NONE),
        notTopLevelServerFlow = get(StrictModeConfigurationKeys.NOT_TOP_LEVEL_SERVER_FLOW, StrictMode.WARNING),
        fields = get(StrictModeConfigurationKeys.FIELDS, StrictMode.WARNING),
    )
}

object StrictModeCliOptions {
    val STATE_FLOW = CliOption(
        optionName = "strict-stateFlow",
        valueDescription = VALUE_DESCRIPTION,
        description = description("StateFlow"),
        required = false,
        allowMultipleOccurrences = false,
    )

    val SHARED_FLOW = CliOption(
        optionName = "strict-sharedFlow",
        valueDescription = VALUE_DESCRIPTION,
        description = description("SharedFlow"),
        required = false,
        allowMultipleOccurrences = false,
    )

    val NESTED_FLOW = CliOption(
        optionName = "strict-nested-flow",
        valueDescription = VALUE_DESCRIPTION,
        description = description("Nested flows"),
        required = false,
        allowMultipleOccurrences = false,
    )

    val STREAM_SCOPED_FUNCTIONS = CliOption(
        optionName = "strict-stream-scope",
        valueDescription = VALUE_DESCRIPTION,
        description = description("Stream Scopes"),
        required = false,
        allowMultipleOccurrences = false,
    )

    val SUSPENDING_SERVER_STREAMING = CliOption(
        optionName = "strict-suspending-server-streaming",
        valueDescription = VALUE_DESCRIPTION,
        description = description("suspending server streaming methods"),
        required = false,
        allowMultipleOccurrences = false,
    )

    val NOT_TOP_LEVEL_SERVER_FLOW = CliOption(
        optionName = "strict-not-top-level-server-flow",
        valueDescription = VALUE_DESCRIPTION,
        description = description("not top-level server streaming declarations"),
        required = false,
        allowMultipleOccurrences = false,
    )

    val FIELDS = CliOption(
        optionName = "strict-fields",
        valueDescription = VALUE_DESCRIPTION,
        description = description("fields"),
        required = false,
        allowMultipleOccurrences = false,
    )

    const val VALUE_DESCRIPTION = "none, warning or error"

    fun description(entity: String): String {
        return "Diagnostic level for $entity in @Rpc services."
    }

    val configurationMapper = mapOf(
        STATE_FLOW to StrictModeConfigurationKeys.STATE_FLOW,
        SHARED_FLOW to StrictModeConfigurationKeys.SHARED_FLOW,
        NESTED_FLOW to StrictModeConfigurationKeys.NESTED_FLOW,
        STREAM_SCOPED_FUNCTIONS to StrictModeConfigurationKeys.STREAM_SCOPED_FUNCTIONS,
        SUSPENDING_SERVER_STREAMING to StrictModeConfigurationKeys.SUSPENDING_SERVER_STREAMING,
        NOT_TOP_LEVEL_SERVER_FLOW to StrictModeConfigurationKeys.NOT_TOP_LEVEL_SERVER_FLOW,
        FIELDS to StrictModeConfigurationKeys.FIELDS,
    )
}

fun AbstractCliOption.processAsStrictModeOption(value: String, configuration: CompilerConfiguration): Boolean {
    StrictModeCliOptions.configurationMapper[this]?.let { key ->
        StrictMode.fromCli(value)?.let { mode ->
            configuration.put(key, mode)
            return true
        }
    }

    return false
}
