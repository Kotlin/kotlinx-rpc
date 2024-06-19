/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.RPCPluginConst.COMPILER_PLUGIN_MODULE
import kotlinx.rpc.RPCPluginConst.GROUP_ID
import kotlinx.rpc.RPCPluginConst.INTERNAL_DEVELOPMENT_PROPERTY
import kotlinx.rpc.RPCPluginConst.KSP_PLUGIN_ARTIFACT_ID
import kotlinx.rpc.RPCPluginConst.KSP_PLUGIN_ID
import kotlinx.rpc.RPCPluginConst.KSP_PLUGIN_MODULE
import kotlinx.rpc.RPCPluginConst.kotlinVersion
import kotlinx.rpc.RPCPluginConst.libraryFullVersion
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

internal data class RPCConfig(
    val isInternalDevelopment: Boolean,
)

class RPCGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val isInternalDevelopment =
            (target.properties.getOrDefault(INTERNAL_DEVELOPMENT_PROPERTY, null) as String?)
                ?.toBoolean() ?: false

        val config = RPCConfig(
            isInternalDevelopment = isInternalDevelopment,
        )

        applyPlatformConfiguration(target, config)
        applyKspPlugin(target, config)
        applyCompilerPlugin(target, config)
    }

    private fun applyCompilerPlugin(target: Project, config: RPCConfig) {
        if (config.isInternalDevelopment) {
            target.dependencies.apply {
                if (target.configurations.findByName(PLUGIN_CLASSPATH_CONFIGURATION_NAME) != null) {
                    add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, COMPILER_PLUGIN_MODULE)
                }

                if (target.configurations.findByName(NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME) != null) {
                    add(NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME, COMPILER_PLUGIN_MODULE)
                }
            }
        } else {
            target.plugins.apply(RPCKotlinCompilerPlugin::class.java)
        }

        // https://youtrack.jetbrains.com/issue/KT-53477/Native-Gradle-plugin-doesnt-add-compiler-plugin-transitive-dependencies-to-compiler-plugin-classpath
        target.configurations.matching {
            it.name.startsWith("kotlin") && it.name.contains("CompilerPluginClasspath")
        }.all {
            isTransitive = true
        }
    }

    private fun applyKspPlugin(target: Project, config: RPCConfig) {
        var kspPluginConfigurationsApplied = false
        withKspPlugin(target) {
            val libraryKspPlugin = when {
                config.isInternalDevelopment -> KSP_PLUGIN_MODULE

                else -> "$GROUP_ID:$KSP_PLUGIN_ARTIFACT_ID:$libraryFullVersion"
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

            kspPluginConfigurationsApplied = true
        }

        target.afterEvaluate {
            if (!kspPluginConfigurationsApplied) {
                error("Expected KSP Gradle Plugin to be present in the project's configuration. " +
                        "Please, add `id(\"$KSP_PLUGIN_ID\")` plugin to the project.")
            }
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

    private fun applyPlatformConfiguration(target: Project, config: RPCConfig) {
        if (config.isInternalDevelopment) {
            return
        }

        target.plugins.apply(RPCPlatformPlugin::class.java)
    }
}
