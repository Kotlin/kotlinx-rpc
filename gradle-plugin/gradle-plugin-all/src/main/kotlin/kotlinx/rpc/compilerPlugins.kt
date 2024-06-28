/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.ClassNaming", "ClassName")

package kotlinx.rpc

import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin

class CompilerPlugin : KotlinCompilerPluginSupportPlugin by compilerPlugin()

class CompilerPluginK2 : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-k2"
})

class CompilerPluginCommon : KotlinCompilerPluginSupportPlugin by compilerPlugin({
    pluginSuffix = "-common"
})
