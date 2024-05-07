/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused", "detekt.MissingPackageDeclaration")

import org.gradle.api.initialization.Settings
import java.io.File
import java.io.File.separator

const val KOTLINX_RPC_PREFIX = "kotlinx-rpc-"

/**
 * Includes a project by the given Gradle path and sets proper public name.
 *
 * Adds mandatory "kotlinx-rpc-" prefix to the project name, keeping the path as is.
 * Example:
 * ```kotlin
 * // this declaration
 * includePublic(":bom")
 *
 * // is the short form for this
 * include(":kotlinx-rpc-bom")
 * project(":kotlinx-rpc-bom").projectDir = file("./bom")
 * ```
 */
fun Settings.includePublic(projectPath: String) {
    require(projectPath.startsWith(":")) { "Project name should start with a colon: $projectPath" }

    val fullProjectName = projectPath.replace(":", ":$KOTLINX_RPC_PREFIX")
    val fullProjectPath = "." + projectPath.replace(":", separator)

    include(fullProjectName)
    project(fullProjectName).projectDir = File(fullProjectPath)
}
