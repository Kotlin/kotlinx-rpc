/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.KRPCPluginConst.BOM_ARTIFACT_ID
import kotlinx.rpc.KRPCPluginConst.GROUP_ID
import kotlinx.rpc.KRPCPluginConst.libraryFullVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class KRPCPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val bomDependency = "${GROUP_ID}:${BOM_ARTIFACT_ID}:${libraryFullVersion}"

        val matcher: (Configuration) -> Boolean = { conf ->
            conf.name in knownConfigurations
        }

        target.configurations.matching(matcher).all {
            target.dependencies.add(name, target.dependencies.platform(bomDependency))
        }
    }

    companion object {
        private const val IMPLEMENTATION = "implementation"
        private const val COMMON_IMPLEMENTATION = "commonMainImplementation"

        private val knownConfigurations = setOf(IMPLEMENTATION, COMMON_IMPLEMENTATION)
    }
}
