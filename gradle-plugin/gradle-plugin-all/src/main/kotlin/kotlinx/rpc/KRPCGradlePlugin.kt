/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.KRPCPluginConst.COMPILER_PLUGIN_MODULE
import kotlinx.rpc.KRPCPluginConst.GROUP_ID
import kotlinx.rpc.KRPCPluginConst.INTERNAL_DEVELOPMENT_PROPERTY
import kotlinx.rpc.KRPCPluginConst.KSP_PLUGIN_ARTIFACT_ID
import kotlinx.rpc.KRPCPluginConst.KSP_PLUGIN_ID
import kotlinx.rpc.KRPCPluginConst.KSP_PLUGIN_MODULE
import kotlinx.rpc.KRPCPluginConst.kotlinVersion
import kotlinx.rpc.KRPCPluginConst.libraryFullVersion
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

internal data class KRPCConfig(
    val isInternalDevelopment: Boolean,
)

class KRPCGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val isInternalDevelopment =
            (target.properties.getOrDefault(INTERNAL_DEVELOPMENT_PROPERTY, null) as String?)
                ?.toBoolean() ?: false

        val config = KRPCConfig(
            isInternalDevelopment = isInternalDevelopment,
        )

        applyPlatformConfiguration(target, config)
        applyKspPlugin(target, config)
        applyCompilerPlugin(target, config)
    }

    private fun applyCompilerPlugin(target: Project, config: KRPCConfig) {
        if (config.isInternalDevelopment) {
            val compilerPlugin = target.rootProject.subprojects
                .singleOrNull { subproject -> subproject.name == COMPILER_PLUGIN_MODULE }
                ?: error("Expected compiler plugin module '$COMPILER_PLUGIN_MODULE' to be present")

            target.dependencies.apply {
                if (target.configurations.findByName(PLUGIN_CLASSPATH_CONFIGURATION_NAME) != null) {
                    add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, compilerPlugin)
                }

                if (target.configurations.findByName(NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME) != null) {
                    add(NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME, compilerPlugin)
                }
            }
        } else {
            target.plugins.apply(KRPCKotlinCompilerPlugin::class.java)
        }

        // https://youtrack.jetbrains.com/issue/KT-53477/Native-Gradle-plugin-doesnt-add-compiler-plugin-transitive-dependencies-to-compiler-plugin-classpath
        target.configurations.matching {
            it.name.startsWith("kotlin") && it.name.contains("CompilerPluginClasspath")
        }.all {
            isTransitive = true
        }
    }

    private fun applyKspPlugin(target: Project, config: KRPCConfig) {
        var kspPluginConfigurationsApplied = false
        withKspPlugin(target) {
            val krpcKspPlugin = when {
                config.isInternalDevelopment -> {
                    target.project(":$KSP_PLUGIN_MODULE")
                }

                else -> {
                    "$GROUP_ID:$KSP_PLUGIN_ARTIFACT_ID:$libraryFullVersion"
                }
            }

            val isKmpProject = target.extensions.findByType(KotlinMultiplatformExtension::class.java) != null

            // separate function is needed for different gradle versions
            // in 7.6 `Configuration` argument is `this`, in 8.* it is a first argument (hence `it`)
            val onConfiguration: (Configuration) -> Unit = { kspConfiguration ->
                target.dependencies.add(kspConfiguration.name, krpcKspPlugin)
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

    private fun applyPlatformConfiguration(target: Project, config: KRPCConfig) {
        if (config.isInternalDevelopment) {
            return
        }

        target.plugins.apply(KRPCPlatformPlugin::class.java)
    }
}
