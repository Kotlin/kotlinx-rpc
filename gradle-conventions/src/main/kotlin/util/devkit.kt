/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitPropertiesDsl
import util.other.libs
import util.other.optionalPropertyValue

/**
 * `true` when `kotlinx.rpc.compilerPlugin.onlyProjectKotlin=true`.
 *
 * Use the property in a build that only compiles or tests the libraries.
 * The compiler plugin then has one Kotlin version instead of all supported versions, so the build is faster.
 * Such a compiler plugin must not be published. See `conventions-devkit-compiler-plugin`.
 */
val Project.onlyProjectKotlin: Boolean get() = optionalPropertyValue("onlyProjectKotlin", "compilerPlugin")

/**
 * The DevKit settings that the compiler plugin and the Gradle plugin must share.
 *
 * The DevKit always adds the version of the Kotlin Gradle plugin.
 * A Kotlin master build (`kotlinx.rpc.kotlinMasterBuild=true`) and an [onlyProjectKotlin] build don't register
 * the supported versions. They register only the `kotlin-compiler` version of the version catalog, because
 * the libraries compile with it. `-Pkotlin.compiler=<version>` sets this version.
 */
fun DevKitPropertiesDsl<*>.configureRpcCompilerPlugin() {
    // The DevKit uses the value as the compiler plugin ID and as the package of the classes that it generates.
    pluginPackage.set("kotlinx.rpc.codegen")

    if (project.optionalPropertyValue("kotlinMasterBuild") || project.onlyProjectKotlin) {
        testAgainst(project.libs.versions.kotlin.compiler.get(), forIde = false)
        return
    }

    cliVersions(min = "2.3.0")
    ideaVersions(min = "252", includeRc = true, includeEap = true)
}
