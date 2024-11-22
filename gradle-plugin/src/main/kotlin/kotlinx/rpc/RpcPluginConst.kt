/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.RpcPluginConst.INTERNAL_DEVELOPMENT_PROPERTY
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources

object RpcPluginConst {
    const val GROUP_ID = "org.jetbrains.kotlinx"
    const val PLUGIN_ID = "org.jetbrains.kotlinx.rpc.plugin"
    const val COMPILER_PLUGIN_ARTIFACT_ID = "kotlinx-rpc-compiler-plugin"

    const val INTERNAL_DEVELOPMENT_PROPERTY = "kotlinx.rpc.plugin.internalDevelopment"

    private val kotlinVersion by lazy { loadKotlinVersion() }

    val libraryKotlinPrefixedVersion by lazy {
        "$kotlinVersion-$PLUGIN_VERSION"
    }

    /**
     * Same as [getKotlinPluginVersion] but without logger
     */
    private fun loadKotlinVersion(): String {
        return object {}
            .loadPropertyFromResources("project.properties", "project.version")
    }
}

val Project.isInternalDevelopment: Boolean
    get() {
        return (properties.getOrDefault(INTERNAL_DEVELOPMENT_PROPERTY, null) as String?)
            ?.toBoolean() ?: false
    }
