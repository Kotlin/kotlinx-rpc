/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.ClassNaming", "ClassName")

package kotlinx.rpc

import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class CompilerPluginK2 : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-k2"
})

class CompilerPluginCommon : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-common"
})

class CompilerPluginBackend : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-backend"
})

class CompilerPluginCli : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-cli"

    applyToCompilation = {
        val extension = it.target.project.extensions.findByType<RpcExtension>()
            ?: RpcExtension(it.target.project.objects)

        val strict = extension.strict

        it.target.project.provider {
            listOf(
                SubpluginOption("strict-stateFlow", strict.stateFlow.get().toCompilerArg()),
                SubpluginOption("strict-sharedFlow", strict.sharedFlow.get().toCompilerArg()),
                SubpluginOption("strict-nested-flow", strict.nestedFlow.get().toCompilerArg()),
                // WIP: https://youtrack.jetbrains.com/issue/KRPC-133
//                SubpluginOption("strict-stream-scope", strict.streamScopedFunctions.get().toCompilerArg()),
//                SubpluginOption(
//                    "strict-suspending-server-streaming",
//                    strict.suspendingServerStreaming.get().toCompilerArg()
//                ),
                SubpluginOption("strict-not-top-level-server-flow", strict.notTopLevelServerFlow.get().toCompilerArg()),
                SubpluginOption("strict-fields", strict.fields.get().toCompilerArg()),
                @OptIn(RpcDangerousApi::class)
                SubpluginOption("annotation-type-safety", extension.annotationTypeSafetyEnabled.get().toString()),
            )
        }
    }
})
