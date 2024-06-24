/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.RPCPluginConst.COMPILER_PLUGIN_ARTIFACT_ID
import kotlinx.rpc.RPCPluginConst.GROUP_ID
import kotlinx.rpc.RPCPluginConst.PLUGIN_ID
import kotlinx.rpc.RPCPluginConst.libraryKotlinPrefixedVersion
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KotlinCompilerPluginBuilder {
    var applicable : (kotlinCompilation: KotlinCompilation<*>) -> Boolean = { true }
    var applyToCompilation: (kotlinCompilation: KotlinCompilation<*>) -> Provider<List<SubpluginOption>> = {
        it.target.project.provider { emptyList() }
    }
    var pluginId: String = PLUGIN_ID
    var groupId = GROUP_ID
    var artifactId: String? = null
    var pluginSuffix = ""
    var version = libraryKotlinPrefixedVersion

    fun build(): KotlinCompilerPluginSupportPlugin {
        return object : KotlinCompilerPluginSupportPlugin {
            var isInternal = false

            override fun apply(target: Project) {
                isInternal = target.isInternalDevelopment
            }

            override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
                return this@KotlinCompilerPluginBuilder.applicable(kotlinCompilation)
            }

            override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
                return this@KotlinCompilerPluginBuilder.applyToCompilation(kotlinCompilation)
            }

            override fun getCompilerPluginId(): String {
                return pluginId + pluginSuffix
            }

            override fun getPluginArtifact(): SubpluginArtifact {
                val artifactId = artifactId ?: compilerPluginArtifactId(isInternal)

                return SubpluginArtifact(groupId, artifactId + pluginSuffix, version)
            }
        }
    }

    private fun compilerPluginArtifactId(isInternal: Boolean): String {
        return if (isInternal) {
            COMPILER_PLUGIN_ARTIFACT_ID.removePrefix("kotlinx-rpc-")
        } else {
            COMPILER_PLUGIN_ARTIFACT_ID
        }
    }
}

fun compilerPlugin(builder: KotlinCompilerPluginBuilder.() -> Unit): KotlinCompilerPluginSupportPlugin {
    return KotlinCompilerPluginBuilder().apply(builder).build()
}
