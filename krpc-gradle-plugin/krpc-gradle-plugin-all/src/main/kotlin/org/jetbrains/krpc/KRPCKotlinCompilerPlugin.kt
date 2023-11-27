package org.jetbrains.krpc

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.krpc.KRPCPluginConst.KRPC_COMPILER_PLUGIN_ARTIFACT_ID
import org.jetbrains.krpc.KRPCPluginConst.KRPC_GROUP_ID
import org.jetbrains.krpc.KRPCPluginConst.KRPC_PLUGIN_ID
import org.jetbrains.krpc.KRPCPluginConst.krpcFullVersion

class KRPCKotlinCompilerPlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider { emptyList() }
    }

    override fun getCompilerPluginId(): String {
        return KRPC_PLUGIN_ID
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(KRPC_GROUP_ID, KRPC_COMPILER_PLUGIN_ARTIFACT_ID, krpcFullVersion)
    }
}
