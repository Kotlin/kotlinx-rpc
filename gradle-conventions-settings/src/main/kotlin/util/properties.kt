/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

package util

import org.gradle.api.Project

fun Project.logAbsentProperty(name: String): Nothing? {
    logger.info("Property '$name' is not present.")

    return null
}

private const val SPACE_USERNAME = "kotlinx.rpc.team.space.username"

fun Project.getSpaceUsername(): String? {
    return providers.gradleProperty(SPACE_USERNAME).orNull
        ?: System.getenv(SPACE_USERNAME)?.ifEmpty { null }
        ?: logAbsentProperty(SPACE_USERNAME)
}

private const val SPACE_PASSWORD = "kotlinx.rpc.team.space.password"

fun Project.getSpacePassword(): String? {
    return providers.gradleProperty(SPACE_PASSWORD).orNull
        ?: System.getenv(SPACE_PASSWORD)?.ifEmpty { null }
        ?: logAbsentProperty(SPACE_USERNAME)
}
