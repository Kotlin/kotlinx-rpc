/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class RpcGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        applyCompilerPlugin(target)
    }

    private fun applyCompilerPlugin(target: Project) {
        target.plugins.apply(CompilerPluginK2::class.java)
        target.plugins.apply(CompilerPluginCommon::class.java)
        target.plugins.apply(CompilerPluginBackend::class.java)
        target.plugins.apply(CompilerPluginCli::class.java)
    }
}
