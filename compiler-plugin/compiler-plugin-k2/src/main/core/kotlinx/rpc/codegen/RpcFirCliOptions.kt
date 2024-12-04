/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object RpcFirCliOptions {
    val ANNOTATION_TYPE_SAFETY = CliOption(
        optionName = "annotation-type-safety",
        valueDescription = "true or false",
        description = "Enables/disables annotation type safety analysis.",
        required = false,
        allowMultipleOccurrences = false,
    )
}

object RpcFirConfigurationKeys {
    val ANNOTATION_TYPE_SAFETY = CompilerConfigurationKey.create<Boolean>("annotation type safety")
}
