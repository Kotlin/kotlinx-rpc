package org.jetbrains.krpc

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import org.jetbrains.krpc.KRPCPluginConst.KRPC_CONFIG_PROPERTY
import org.jetbrains.krpc.KRPCPluginConst.KRPC_GROUP_ID
import org.jetbrains.krpc.KRPCPluginConst.KRPC_INTERNAL_DEVELOPMENT_PROPERTY
import org.jetbrains.krpc.KRPCPluginConst.KRPC_KSP_PLUGIN_ARTIFACT_ID
import org.jetbrains.krpc.KRPCPluginConst.KSP_PLUGIN_ID
import org.jetbrains.krpc.KRPCPluginConst.kotlinVersion
import org.jetbrains.krpc.KRPCPluginConst.krpcFullVersion

internal data class KRPCConfig(
    val isInternalDevelopment: Boolean,
)

class KRPCGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val isInternalDevelopment =
            (target.properties.getOrDefault(KRPC_INTERNAL_DEVELOPMENT_PROPERTY, null) as String?)
                ?.toBooleanStrictOrNull() ?: false

        val config = KRPCConfig(
            isInternalDevelopment = isInternalDevelopment,
        )

        applyPlatformConfiguration(target, config)
        applyKspPlugin(target, config)
        applyCompilerPlugin(target, config)
    }

    private fun applyCompilerPlugin(target: Project, config: KRPCConfig) {
        if (config.isInternalDevelopment) {
            target.dependencies.apply {
                if (target.configurations.findByName(PLUGIN_CLASSPATH_CONFIGURATION_NAME) != null) {
                    add(
                        PLUGIN_CLASSPATH_CONFIGURATION_NAME,
                        target.project(":krpc-compiler-plugin")
                    )
                }

                if (target.configurations.findByName(NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME) != null) {
                    add(
                        NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME,
                        target.project(":krpc-compiler-plugin")
                    )
                }
            }
        } else {
            target.extraProperties[KRPC_CONFIG_PROPERTY] = config

            target.plugins.apply(KRPCKotlinCompilerPlugin::class.java)
        }
    }

    private fun applyKspPlugin(target: Project, config: KRPCConfig) {
        var kspPluginConfigurationsApplied = false
        withKspPlugin(target) {
            val krpcKspPlugin = when {
                config.isInternalDevelopment -> {
                    target.project(":krpc-ksp-plugin")
                }

                else -> {
                    "$KRPC_GROUP_ID:$KRPC_KSP_PLUGIN_ARTIFACT_ID:$krpcFullVersion"
                }
            }

            val isKmpProject = target.extensions.findByType(KotlinMultiplatformExtension::class.java) != null

            target.configurations
                // `matching` and `all` functions are live in gradle, without them code would not work
                .matching { config ->
                    val name = config.name
                    // all ksp configurations start with ksp prefix
                    name.startsWith("ksp") &&
                            // filter out non-compilation configs
                            !name.lowercase().contains("metadata") &&
                            !name.contains("ProcessorClasspath") &&
                            !name.contains("GeneratedByKsp") &&
                            !name.contains("PluginClasspath") &&
                            // ksp and kspTest configurations are deprecated for kmp project,
                            // target specific configurations are present instead
                            !(isKmpProject && (name == "ksp" || name == "kspTest"))
                }
                .all { kspConfiguration ->
                    target.dependencies.add(kspConfiguration.name, krpcKspPlugin)
                }

            kspPluginConfigurationsApplied = true
        }

        target.afterEvaluate {
            if (!kspPluginConfigurationsApplied) {
                error("Expected KSP Gradle Plugin to be present in the project's configuration. Please, add `id(\"$KSP_PLUGIN_ID\")` plugin to the project.")
            }
        }
    }

    private fun withKspPlugin(target: Project, action: Action<Unit>) {
        // `withId` is live, so order of plugins can be arbitrary
        target.plugins.withId(KSP_PLUGIN_ID) {
            val plugin = it as KotlinCompilerPluginSupportPlugin

            val version = plugin.getPluginArtifact().version
            if (version?.startsWith(kotlinVersion) != true) {
                error("Invalid KSP Gradle Plugin version: $version. Expected version to start with $kotlinVersion")
            }

            action.execute(Unit)
        }
    }

    private fun applyPlatformConfiguration(target: Project, config: KRPCConfig) {
        if (config.isInternalDevelopment) {
            return
        }

        target.plugins.apply(KRPCPlatformPlugin::class.java)
    }
}
