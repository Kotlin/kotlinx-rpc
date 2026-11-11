/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitPropertiesDsl
import util.other.libs
import util.other.optionalPropertyValue

/**
 * The DevKit settings that the compiler plugin and the Gradle plugin must share.
 *
 * A Kotlin master build (`kotlinx.rpc.kotlinMasterBuild=true`) doesn't register the supported versions.
 * It registers only the `kotlin-compiler` version of the version catalog (`-Pkotlin.compiler=<version>`).
 * The DevKit always adds the version of the Kotlin Gradle plugin.
 */
fun DevKitPropertiesDsl<*>.configureRpcCompilerPlugin() {
    // The DevKit uses the value as the compiler plugin ID and as the package of the classes that it generates.
    pluginPackage.set("kotlinx.rpc.codegen")

    if (project.optionalPropertyValue("kotlinMasterBuild")) {
        testAgainst(project.libs.versions.kotlin.compiler.get(), forIde = false)
        return
    }

    cliVersions(min = "2.2.20")
    ideaVersions(min = "252", includeRc = true, includeEap = true)
}
