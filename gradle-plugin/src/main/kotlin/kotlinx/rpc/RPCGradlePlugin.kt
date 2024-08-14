/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.RPCPluginConst.GROUP_ID
import kotlinx.rpc.RPCPluginConst.KSP_PLUGIN_ARTIFACT_ID
import kotlinx.rpc.RPCPluginConst.KSP_PLUGIN_ID
import kotlinx.rpc.RPCPluginConst.KSP_PLUGIN_MODULE
import kotlinx.rpc.RPCPluginConst.kotlinVersion
import kotlinx.rpc.RPCPluginConst.libraryKotlinPrefixedVersion
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin

internal data class RPCConfig(
    val kotlinLanguageVersion: KotlinVersion,
)

class RPCGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.isInternalDevelopment) {
            target.plugins.apply(RPCPlatformPlugin::class.java)
        }

        // Will apply only if the KSP plugin is present.
        // While the K1 plugin is not present, it is alright to leave it this way.
        // It will generate redundant code, but it is not critical,
        // since that code will not be used, and K1 will shortly remove KSP altogether
        applyKspPlugin(target, target.isInternalDevelopment)

        applyCompilerPlugin(target)
    }

    private fun applyCompilerPlugin(target: Project) {
        target.plugins.apply(CompilerPluginK2::class.java)
        target.plugins.apply(CompilerPluginCommon::class.java)
        target.plugins.apply(CompilerPluginBackend::class.java)
        target.plugins.apply(CompilerPluginCli::class.java)
    }

    private fun applyKspPlugin(target: Project, isInternalDevelopment: Boolean) {
        withKspPlugin(target) {
            val libraryKspPlugin = when {
                isInternalDevelopment -> KSP_PLUGIN_MODULE

                else -> "$GROUP_ID:$KSP_PLUGIN_ARTIFACT_ID:$libraryKotlinPrefixedVersion"
            }

            val isKmpProject = target.extensions.findByType(KotlinMultiplatformExtension::class.java) != null

            // separate function is needed for different gradle versions
            // in 7.6 `Configuration` argument is `this`, in 8.* it is a first argument (hence `it`)
            val onConfiguration: (Configuration) -> Unit = { kspConfiguration ->
                target.dependencies.add(kspConfiguration.name, libraryKspPlugin)
            }

            target.configurations
                // `matching` and `all` functions are live in gradle, without them code would not work
                .matching { config ->
                    val name = config.name
                    // all ksp configurations start with ksp prefix
                    name.startsWith("ksp") &&
                            // filter out non-compilation configs
                            !name.contains("metadata") &&
                            !name.contains("Metadata") &&
                            !name.contains("ProcessorClasspath") &&
                            !name.contains("GeneratedByKsp") &&
                            !name.contains("PluginClasspath") &&
                            // ksp and kspTest configurations are deprecated for kmp project,
                            // target specific configurations are present instead
                            !(isKmpProject && (name == "ksp" || name == "kspTest"))
                }
                .all(onConfiguration)
        }
    }

    private fun withKspPlugin(target: Project, action: Action<Unit>) {
        // separate function is needed for different gradle versions
        // in 7.6 `Plugin` argument is `this`, in 8.* it is a first argument (hence `it`)
        val onPlugin: (Plugin<Any>) -> Unit = {
            val plugin = it as KotlinCompilerPluginSupportPlugin

            val version = plugin.getPluginArtifact().version
            if (version?.startsWith(kotlinVersion) != true) {
                error("Invalid KSP Gradle Plugin version: $version. Expected version to start with $kotlinVersion")
            }

            action.execute(Unit)
        }

        // `withId` is live, so order of plugins can be arbitrary
        target.plugins.withId(KSP_PLUGIN_ID, onPlugin)
    }
}
