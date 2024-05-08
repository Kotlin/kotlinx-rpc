/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources

object RPCPluginConst {
    const val COMPILER_PLUGIN_MODULE = "kotlinx-rpc-compiler-plugin"
    const val KSP_PLUGIN_MODULE = "kotlinx-rpc-ksp-plugin"

    const val KSP_PLUGIN_ID = "com.google.devtools.ksp"

    const val PLUGIN_ID = "org.jetbrains.kotlinx.rpc.plugin"
    const val GROUP_ID = "org.jetbrains.kotlinx"
    const val COMPILER_PLUGIN_ARTIFACT_ID = "kotlinx-rpc-compiler-plugin"
    const val KSP_PLUGIN_ARTIFACT_ID = "kotlinx-rpc-ksp-plugin"
    const val BOM_ARTIFACT_ID = "kotlinx-rpc-bom"

    const val INTERNAL_DEVELOPMENT_PROPERTY = "kotlinx.rpc.plugin.internalDevelopment"

    val kotlinVersion by lazy { loadKotlinVersion() }

    val libraryFullVersion by lazy {
        "$kotlinVersion-$PLUGIN_VERSION"
    }

    /**
     * Same as [getKotlinPluginVersion] but without logger
     *
     * Cannot use with [Project] because [libraryFullVersion] is called in [RPCKotlinCompilerPlugin] in -all module
     */
    private fun loadKotlinVersion(): String {
        return object {}
            .loadPropertyFromResources("project.properties", "project.version")
    }
}
