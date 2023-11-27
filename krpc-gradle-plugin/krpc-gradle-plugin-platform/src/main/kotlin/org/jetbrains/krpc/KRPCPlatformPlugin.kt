package org.jetbrains.krpc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.krpc.KRPCPluginConst.KRPC_GROUP_ID
import org.jetbrains.krpc.KRPCPluginConst.KRPC_BOM_ARTIFACT_ID
import org.jetbrains.krpc.KRPCPluginConst.krpcFullVersion

class KRPCPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.dependencies.apply {
            val bomDependency = "${KRPC_GROUP_ID}:${KRPC_BOM_ARTIFACT_ID}:${krpcFullVersion}"

            add("implementation", enforcedPlatform(bomDependency))
        }
    }
}
