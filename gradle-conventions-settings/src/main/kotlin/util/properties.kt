/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

package util

import org.gradle.api.Project
import java.io.File

fun Project.logAbsentProperty(name: String): Nothing? {
    logger.info("Property '$name' is not present.")

    return null
}

fun Project.getLocalProperties(): java.util.Properties {
    return java.util.Properties().apply {
        val propertiesDir = File(
            rootDir.path
                .removeSuffix("/gradle-conventions")
                .removeSuffix("/gradle-conventions-settings")
                .removeSuffix("/ksp-plugin")
                .removeSuffix("/compiler-plugin")
                .removeSuffix("/gradle-plugin")
        )
        val localFile = File(propertiesDir, "local.properties")
        if (localFile.exists()) {
            localFile.inputStream().use { load(it) }
        }
    }
}

fun Project.getSpaceUsername(): String? {
    val username = "kotlinx.rpc.team.space.username"
    return getLocalProperties()[username] as String?
        ?: providers.gradleProperty(username).orNull
        ?: System.getenv(username)?.ifEmpty { null }
        ?: logAbsentProperty(username)
}

fun Project.getSpacePassword(): String? {
    val password = "kotlinx.rpc.team.space.password"
    return getLocalProperties()[password] as String?
        ?: providers.gradleProperty(password).orNull
        ?: System.getenv(password)?.ifEmpty { null }
        ?: logAbsentProperty(password)
}
