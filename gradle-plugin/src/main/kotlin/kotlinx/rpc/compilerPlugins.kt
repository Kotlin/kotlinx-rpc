/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.ClassNaming", "ClassName")

package kotlinx.rpc

import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

internal class CompilerPluginK2 : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-k2"
})

internal class CompilerPluginCommon : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-common"
})

internal class CompilerPluginBackend : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-backend"
})

internal class CompilerPluginCli : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-cli"

    applyToCompilation = {
        val extension = it.target.project.rpcExtension()

        it.target.project.provider {
            listOf(
                @OptIn(RpcDangerousApi::class)
                SubpluginOption("annotation-type-safety", extension.annotationTypeSafetyEnabled.get().toString()),
            )
        }
    }
})
