/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.RPCPluginConst.COMPILER_PLUGIN_ARTIFACT_ID
import kotlinx.rpc.RPCPluginConst.GROUP_ID
import kotlinx.rpc.RPCPluginConst.PLUGIN_ID
import kotlinx.rpc.RPCPluginConst.libraryFullVersion
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class RPCKotlinCompilerPlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider { emptyList() }
    }

    override fun getCompilerPluginId(): String {
        return PLUGIN_ID
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(GROUP_ID, COMPILER_PLUGIN_ARTIFACT_ID, libraryFullVersion)
    }
}
