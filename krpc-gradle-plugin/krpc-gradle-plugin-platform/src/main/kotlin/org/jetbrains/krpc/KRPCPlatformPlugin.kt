/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.jetbrains.krpc.KRPCPluginConst.KRPC_GROUP_ID
import org.jetbrains.krpc.KRPCPluginConst.KRPC_BOM_ARTIFACT_ID
import org.jetbrains.krpc.KRPCPluginConst.krpcFullVersion

class KRPCPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val bomDependency = "${KRPC_GROUP_ID}:${KRPC_BOM_ARTIFACT_ID}:${krpcFullVersion}"

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
