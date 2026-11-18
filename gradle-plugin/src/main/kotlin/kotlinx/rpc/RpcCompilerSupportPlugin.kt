/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/**
 * Applies the kotlinx.rpc compiler plugin to every Kotlin compilation of the project.
 *
 * The DevKit generates [PluginInfo]. This class doesn't extend the `DevKitSupportPlugin` of the DevKit,
 * because the published Gradle plugin must not depend on the DevKit runtime library.
 */
internal class RpcCompilerSupportPlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getCompilerPluginId(): String = PluginInfo.PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = PluginInfo.PLUGIN_ARTIFACT

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project: Project = kotlinCompilation.target.project
        val extension = project.rpcExtension()

        return project.provider {
            listOf(
                @OptIn(RpcDangerousApi::class)
                SubpluginOption("annotation-type-safety", extension.annotationTypeSafetyEnabled.get().toString()),
            )
        }
    }
}
